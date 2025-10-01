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

import org.junit.jupiter.api.Test;
import walkingkooka.collect.list.Lists;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;
import walkingkooka.visit.Visiting;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class MailToUrlTest extends UrlTestCase<MailToUrl>
    implements HasHostAddressTesting<MailToUrl> {

    @Test
    public void testWithNullEmailAddressesFails() {
        assertThrows(
            NullPointerException.class,
            () -> MailToUrl.with(
                null, // url can be null
                null,
                this.headers()
            )
        );
    }

    @Test
    public void testWithNullHeadersFails() {
        assertThrows(
            NullPointerException.class,
            () -> MailToUrl.with(
                null, // url can be null
                Lists.empty(),
                null
            )
        );
    }

    @Test
    public void testWith() {
        final List<EmailAddress> addresses = this.emailAddresses();
        final UrlQueryString headers = this.headers();
        final MailToUrl mailToUrl = MailToUrl.with(
            null, // url can be null
            addresses,
            headers
        );

        this.checkEmailAddresses(mailToUrl);
        this.checkHeaders(mailToUrl);
    }

    // setEmailAddresses................................................................................................

    @Test
    public void testSetEmailAddressesNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createUrl().setEmailAddresses(null)
        );
    }

    @Test
    public void testSetEmailAddressesSame() {
        final MailToUrl mailToUrl = this.createUrl();

        assertSame(
            mailToUrl,
            mailToUrl.setEmailAddresses(mailToUrl.emailAddresses())
        );
    }

    @Test
    public void testSetEmailAddressesDifferent() {
        final MailToUrl mailToUrl = this.createUrl();

        final List<EmailAddress> differentEmailAddresses = Lists.of(
            EmailAddress.parse("different@example.com")
        );

        final MailToUrl different = mailToUrl.setEmailAddresses(differentEmailAddresses);

        assertNotSame(
            mailToUrl,
            different
        );

        this.checkEmailAddresses(mailToUrl);
        this.checkHeaders(mailToUrl);

        this.checkEmailAddresses(different, differentEmailAddresses);
        this.checkHeaders(different);
    }

    // setHeaders................................................................................................

    @Test
    public void testSetHeadersNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createUrl().setHeaders(null)
        );
    }

    @Test
    public void testSetHeadersSame() {
        final MailToUrl mailToUrl = this.createUrl();

        assertSame(
            mailToUrl,
            mailToUrl.setHeaders(mailToUrl.headers())
        );
    }

    @Test
    public void testSetHeadersDifferent() {
        final MailToUrl mailToUrl = this.createUrl();

        final UrlQueryString differentHeaders = UrlQueryString.parse("different");

        final MailToUrl different = mailToUrl.setHeaders(differentHeaders);

        assertNotSame(
            mailToUrl,
            different
        );

        this.checkEmailAddresses(mailToUrl);
        this.checkHeaders(mailToUrl);

        this.checkEmailAddresses(different);
        this.checkHeaders(different, differentHeaders);
    }

    // ParseTesting.....................................................................................................

    @Test
    public void testParseMissingSchemeFails() {
        this.parseStringFails(
            "https://example.com",
            IllegalArgumentException.class
        );
    }

    @Test
    public void testParseOnlyEmail() {
        this.parseStringAndCheck(
            "mailto:hello@example.com",
            MailToUrl.with(
                null, // url
                Lists.of(
                    EmailAddress.parse("hello@example.com")
                ),
                UrlQueryString.EMPTY
            )
        );
    }

    @Test
    public void testParseOnlyEmailUrlEncoded() {
        this.parseStringAndCheck(
            "mailto:gorby%25kremvax@example.com",
            MailToUrl.with(
                null, // url
                Lists.of(
                    EmailAddress.parse("gorby%kremvax@example.com")
                ),
                UrlQueryString.EMPTY
            )
        );
    }

    @Test
    public void testParseEmptyHeaders() {
        this.parseStringAndCheck(
            "mailto:hello@example.com?",
            MailToUrl.with(
                null, // url
                Lists.of(
                    EmailAddress.parse("hello@example.com")
                ),
                UrlQueryString.EMPTY
            )
        );
    }

    @Test
    public void testParseNoEmailAndSubject() {
        this.parseStringAndCheck(
            "mailto:?subject=Hello",
            MailToUrl.with(
                null, // url,
                Lists.empty(),
                UrlQueryString.parse(
                    "subject=Hello"
                )
            )
        );
    }

    @Test
    public void testParseEmailAndSubject() {
        this.parseStringAndCheck(
            "mailto:hello@example.com?subject=Hello",
            MailToUrl.with(
                null, // url
                Lists.of(
                    EmailAddress.parse("hello@example.com")
                ),
                UrlQueryString.parse(
                    "subject=Hello"
                )
            )
        );
    }

    @Test
    public void testParseEmailSubjectAndBody() {
        final String url = "mailto:hello@example.com?subject=Hello&body=123";

        final MailToUrl mailToUrl = this.parseStringAndCheck(
            url,
            MailToUrl.with(
                url, // url
                Lists.of(
                    EmailAddress.parse("hello@example.com")
                ),
                UrlQueryString.parse(
                    "subject=Hello&body=123"
                )
            )
        );
        assertSame(
            url,
            mailToUrl.value(),
            "url"
        );
        this.checkEquals(
            Optional.of("Hello"),
            mailToUrl.subject()
        );
        this.checkEquals(
            Optional.of("123"),
            mailToUrl.body()
        );
    }

    private void checkEmailAddresses(final MailToUrl mailToUrl) {
        this.checkEmailAddresses(
            mailToUrl,
            this.emailAddresses()
        );
    }

    private void checkEmailAddresses(final MailToUrl mailToUrl,
                                     final List<EmailAddress> expected) {
        this.checkEquals(
            expected,
            mailToUrl.emailAddresses()
        );
    }

    private void checkHeaders(final MailToUrl mailToUrl) {
        this.checkHeaders(
            mailToUrl,
            this.headers()
        );
    }

    private void checkHeaders(final MailToUrl mailToUrl,
                              final UrlQueryString expected) {
        this.checkEquals(
            expected,
            mailToUrl.headers()
        );
    }

    // UrlVisitor......................................................................................................

    @Test
    public void testAccept() {
        final StringBuilder b = new StringBuilder();
        final MailToUrl url = this.createUrl();

        new FakeUrlVisitor() {
            @Override
            protected Visiting startVisit(final Url u) {
                assertSame(url, u);
                b.append("1");
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final Url u) {
                assertSame(url, u);
                b.append("2");
            }

            @Override
            protected void visit(final MailToUrl u) {
                assertSame(url, u);
                b.append("5");
            }
        }.accept(url);
        this.checkEquals("152", b.toString());
    }

    // HasHostAddress...................................................................................................

    @Test
    public void testHostAddressWhenMultipleEmailsFails() {
        final IllegalStateException thrown = assertThrows(
            IllegalStateException.class,
            () -> this.parseString("mailto:user1@example.com,user2@example.com")
                .hostAddress()
        );

        this.checkEquals(
            "More than 1 EmailAddress",
            thrown.getMessage()
        );
    }

    @Test
    public void testSetHostAddressWhenMultipleEmailsFails() {
        final IllegalStateException thrown = assertThrows(
            IllegalStateException.class,
            () -> Url.parseMailTo("mailto:user1@example.com,user2@example.com")
                .setHostAddress(HostAddress.with("different.example.com"))
        );

        this.checkEquals(
            "More than 1 EmailAddress",
            thrown.getMessage()
        );
    }

    @Test
    public void testSetHostAddressWithSame2() {
        final MailToUrl mailToUrl = this.createHasHostAddress();

        assertSame(
            mailToUrl.setHostAddress(
                HostAddress.with("example.com")
            ),
            mailToUrl
        );
    }

    @Test
    public void testSetHostAddressWithSameHostDifferentCase() {
        final MailToUrl mailToUrl = this.createHasHostAddress();

        assertSame(
            mailToUrl.setHostAddress(
                HostAddress.with("EXAMPLE.COM")
            ),
            mailToUrl
        );
    }

    @Test
    public void testSetHostAddressWithDifferent() {
        this.setHostAddressAndCheck(
            Url.parseMailTo("mailto:user@example.com"),
            HostAddress.with("different.example.com"),
            MailToUrl.parseMailTo("mailto:user@different.example.com")
        );
    }

    @Override
    public MailToUrl createHasHostAddress() {
        return Url.parseMailTo("mailto:user@example.com");
    }

    // hashCode/equals..................................................................................................

    @Test
    public void testEqualsDifferentUser() {
        this.checkNotEquals(
            this.parseString("mailto:hello@example.com"),
            this.parseString("mailto:different@example.com")
        );
    }

    @Test
    public void testEqualsUserDifferentCase() {
        this.checkNotEquals(
            this.parseString("mailto:hello@example.com"),
            this.parseString("mailto:HELLO@example.com")
        );
    }

    @Test
    public void testEqualsDifferentDomain() {
        this.checkNotEquals(
            this.parseString("mailto:hello@example.com"),
            this.parseString("mailto:hello@different.com")
        );
    }

    @Test
    public void testEqualsDomainDifferentCase() {
        this.checkEquals(
            this.parseString("mailto:hello@example.com"),
            this.parseString("mailto:hello@EXAMPLE.COM")
        );
    }

    // ToString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
            this.createUrl(),
            "mailto:hello1@example.com,hello2@example.com?subject=SubjectHello123&body=Body456");
    }

    @Test
    public void testToStringNoEmails() {
        this.toStringAndCheck(
            MailToUrl.with(
                null, // null url is ok
                Lists.of(),
                UrlQueryString.parse("a=b")
            ),
            "mailto:?a=b"
        );
    }

    //    To indicate the address "unlikely?address@example.com", and include
    //   another header, one would do:
    //
    //     <mailto:unlikely%3Faddress@example.com?blat=foop>
    @Test
    public void testToStringUrlEncodedEmailAddresses() {
        this.toStringAndCheck(
            MailToUrl.with(
                null, // null url is ok
                Lists.of(
                    EmailAddress.parse("unlikely?address@example.com")
                ),
                UrlQueryString.parse("blat=foop")
            ),
            "mailto:unlikely%3Faddress@example.com?blat=foop");
    }

    //   To indicate the address "gorby%kremvax@example.com" one would do:
    //
    //     <mailto:gorby%25kremvax@example.com>
    @Test
    public void testToStringRfcEmail() {
        this.toStringAndCheck(
            MailToUrl.with(
                null, // null url is ok
                Lists.of(
                    EmailAddress.parse("gorby%kremvax@example.com")
                ),
                UrlQueryString.EMPTY
            ),
            "mailto:gorby%25kremvax@example.com"
        );
    }

    @Override
    MailToUrl createUrl() {
        return MailToUrl.with(
            null, // null url is ok
            this.emailAddresses(),
            this.headers()
        );
    }

    private List<EmailAddress> emailAddresses() {
        return Lists.of(
            EmailAddress.parse("hello1@example.com"),
            EmailAddress.parse("hello2@example.com")
        );
    }

    private UrlQueryString headers() {
        return UrlQueryString.parse("subject=SubjectHello123&body=Body456");
    }

    // JsonNodeMarshallTesting .........................................................................................

    @Override
    public MailToUrl unmarshall(final JsonNode node,
                                final JsonNodeUnmarshallContext context) {
        return Url.unmarshallMailTo(node, context);
    }

    // ParseStringTesting...............................................................................................

    @Override
    public MailToUrl parseString(final String text) {
        return MailToUrl.parseMailTo0(text);
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<MailToUrl> type() {
        return MailToUrl.class;
    }
}
