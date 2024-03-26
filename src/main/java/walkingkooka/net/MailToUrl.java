/*
 * Copyright 2019 Miroslav Pokorny (github.com/mP1)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package walkingkooka.net;

import walkingkooka.Cast;
import walkingkooka.collect.list.Lists;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.text.CharSequences;
import walkingkooka.text.CharacterConstant;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * https://en.wikipedia.org/wiki/Mailto
 * https://www.ietf.org/rfc/rfc2368.txt
 * <pre>
 * RFC 2368                 The mailto URL scheme                 July 1998
 *
 *
 *      mailtoURL  =  "mailto:" [ to ] [ headers ]
 *      to         =  #mailbox
 *      headers    =  "?" header *( "&" header )
 *      header     =  hname "=" hvalue
 *      hname      =  *urlc
 *      hvalue     =  *urlc
 *
 *    "#mailbox" is as specified in RFC 822 [RFC822]. This means that it
 *    consists of zero or more comma-separated mail addresses, possibly
 *    including "phrase" and "comment" components. Note that all URL
 *    reserved characters in "to" must be encoded: in particular,
 *    parentheses, commas, and the percent sign ("%"), which commonly occur
 *    in the "mailbox" syntax.
 *
 *    "hname" and "hvalue" are encodings of an RFC 822 header name and
 *    value, respectively. As with "to", all URL reserved characters must
 *    be encoded.
 *
 *    The special hname "body" indicates that the associated hvalue is the
 *    body of the message. The "body" hname should contain the content for
 *    the first text/plain body part of the message. The mailto URL is
 *    primarily intended for generation of short text messages that are
 *    actually the content of automatic processing (such as "subscribe"
 *    messages for mailing lists), not general MIME bodies.
 *
 *    Within mailto URLs, the characters "?", "=", "&" are reserved.
 *
 *    Because the "&" (ampersand) character is reserved in HTML, any mailto
 *    URL which contains an ampersand must be spelled differently in HTML
 *    than in other contexts.  A mailto URL which appears in an HTML
 *    document must use "&amp;" instead of "&".
 *
 *    Also note that it is legal to specify both "to" and an "hname" whose
 *    value is "to". That is,
 *
 *      mailto:addr1%2C%20addr2
 *
 *      is equivalent to
 *
 *      mailto:?to=addr1%2C%20addr2
 *
 *      is equivalent to
 *
 *      mailto:addr1?to=addr2
 *
 *    8-bit characters in mailto URLs are forbidden. MIME encoded words (as
 *    defined in [RFC2047]) are permitted in header values, but not for any
 *    part of a "body" hname.
 * </pre>
 */
public final class MailToUrl extends Url {

    final static String SCHEME = "mailto:";

    public final static CharacterConstant EMAIL_SEPARATOR = CharacterConstant.with(',');

    static MailToUrl parseMailTo0(final String url) {
        CharSequences.failIfNullOrEmpty(url, "url");

        if (false == CharSequences.startsWith(url, SCHEME)) {
            throw new IllegalArgumentException("Missing " + CharSequences.quoteAndEscape(SCHEME) + " from url " + CharSequences.quoteAndEscape(url));
        }

        int emailAddressesStart = SCHEME.length();

        final int questionMark = url.indexOf(
                Url.QUERY_START.character(),
                emailAddressesStart
        );

        final List<EmailAddress> emailAddresses = parseEmailAddresses(
                url,
                emailAddressesStart,
                -1 == questionMark ?
                        url.length() :
                        questionMark
        );

        final UrlQueryString headers = -1 == questionMark ?
                UrlQueryString.EMPTY :
                UrlQueryString.parse(
                        url.substring(questionMark + 1)
                );

        return with(
                emailAddresses,
                headers
        );
    }

    private static List<EmailAddress> parseEmailAddresses(final String url,
                                                          final int start,
                                                          final int end) {
        final List<EmailAddress> emailAddresses = Lists.array();

        int i = start;
        while (i < end) {
            final int separator = url.indexOf(
                    EMAIL_SEPARATOR.character(),
                    i
            );
            if (-1 == separator) {
                emailAddresses.add(
                        decodeEmailAddress(
                                url,
                                i,
                                end
                        )
                );
                break;
            }
            emailAddresses.add(
                    decodeEmailAddress(
                            url,
                            i,
                            separator
                    )
            );

            i = separator + 1;
        }

        return emailAddresses;
    }

    private static EmailAddress decodeEmailAddress(final String url,
                                                   final int start,
                                                   final int end) {
        return EmailAddress.parse(
                URLDecoder.decode(
                        url.substring(
                                start,
                                end
                        )
                )
        );
    }

    static MailToUrl with(final List<EmailAddress> emailAddresses,
                          final UrlQueryString headers) {
        return new MailToUrl(
                checkEmailAddresses(emailAddresses),
                checkHeaders(headers)
        );
    }

    private MailToUrl(final List<EmailAddress> emailAddresses,
                      final UrlQueryString headers) {
        this.emailAddresses = emailAddresses;
        this.headers = headers;
    }

    public List<EmailAddress> emailAddresses() {
        return this.emailAddresses;
    }

    public MailToUrl setEmailAddresses(final List<EmailAddress> emailAddresses) {
        checkEmailAddresses(emailAddresses);

        final List<EmailAddress> copy = Lists.immutable(emailAddresses);
        return this.emailAddresses.equals(emailAddresses) ?
                this :
                this.replace(
                        copy,
                        headers
                );
    }

    private static List<EmailAddress> checkEmailAddresses(final List<EmailAddress> emailAddresses) {
        return Objects.requireNonNull(emailAddresses, "emailAddresses");
    }

    private final List<EmailAddress> emailAddresses;

    public UrlQueryString headers() {
        return this.headers;
    }

    public MailToUrl setHeaders(final UrlQueryString headers) {
        checkHeaders(headers);

        return this.headers.equals(headers) ?
                this :
                this.replace(
                        this.emailAddresses,
                        headers
                );
    }

    private static UrlQueryString checkHeaders(final UrlQueryString headers) {
        return Objects.requireNonNull(headers, "headers");
    }

    private final UrlQueryString headers;

    private MailToUrl replace(final List<EmailAddress> emailAddresses,
                              final UrlQueryString headers) {
        return new MailToUrl(
                emailAddresses,
                headers
        );
    }

    public Optional<String> subject() {
        return this.headers.parameter(SUBJECT);
    }

    private final static UrlParameterName SUBJECT = UrlParameterName.with("subject");

    public Optional<String> body() {
        return this.headers.parameter(BODY);
    }

    private final static UrlParameterName BODY = UrlParameterName.with("body");

    // UrlVisitor.......................................................................................................

    @Override
    void accept(final UrlVisitor visitor) {
        visitor.visit(this);
    }

    // Value............................................................................................................

    @Override
    public String value() {
        final StringBuilder b = new StringBuilder();
        b.append(SCHEME);

        String separator = "";

        for (final EmailAddress emailAddress : this.emailAddresses) {
            b.append(separator);
            b.append(
                    MailToUrl.encode(emailAddress)
            );

            separator = EMAIL_SEPARATOR.string();
        }

        final UrlQueryString headers = this.headers;
        if (false == headers.isEmpty()) {
            b.append(QUERY_START);
            b.append(headers);
        }
        return b.toString();
    }

    // According to RFC 822, the characters "?", "&", and even "%" may occur
    //   in addr-specs. The fact that they are reserved characters in this URL
    //   scheme is not a problem: those characters may appear in mailto URLs,
    //   they just may not appear in unencoded form. The standard URL encoding
    //   mechanisms ("%" followed by a two-digit hex number) must be used in
    //   certain cases.
    private static String encode(final EmailAddress emailAddress) {
        return URLEncoder.encode(
                emailAddress.value()
        ).replace("%40", "@");
    }

    // Object...........................................................................................................

    public int hashCode() {
        return Objects.hash(this.emailAddresses, this.headers);
    }

    public boolean equals(final Object other) {
        return this == other ||
                other instanceof MailToUrl && this.equals0(Cast.to(other));
    }

    private boolean equals0(final MailToUrl other) {
        return this.emailAddresses.equals(other.emailAddresses) &&
                this.headers.equals(other.headers);
    }

    @Override
    public String toString() {
        return this.value();
    }
}
