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

import walkingkooka.Binary;
import walkingkooka.Cast;
import walkingkooka.Value;
import walkingkooka.net.header.MediaType;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.text.CharSequences;
import walkingkooka.text.CharacterConstant;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;
import walkingkooka.visit.Visitable;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * Base class with getters that return the common components of a {@link Url}.
 */
public abstract class Url implements Value<String>,
        Visitable {

    // constants.......................................................................................................

    /**
     * The character that separates a host name or address from the following port number.
     */
    public final static CharacterConstant HOST_PORT_SEPARATOR = CharacterConstant.with(':');

    /**
     * The character within a URL that marks the beginning of a path.
     */
    public final static CharacterConstant PATH_START = CharacterConstant.with(UrlPath.SEPARATOR.character());

    /**
     * The character within a URL that marks the start of a query string.
     */
    public final static CharacterConstant QUERY_START = CharacterConstant.with('?');

    /**
     * The character within a URL that proceeds the query string.
     */
    public final static CharacterConstant QUERY_PARAMETER_SEPARATOR = CharacterConstant.with('&');

    /**
     * The alternate character within a URL that proceeds the query string.
     */
    public final static CharacterConstant QUERY_PARAMETER_SEPARATOR2 = CharacterConstant.with(';');

    /**
     * The character within a query string that separates name value pairs.
     */
    public final static CharacterConstant QUERY_NAME_VALUE_SEPARATOR = CharacterConstant.with('=');

    /**
     * The character proceeding an anchor within a URL.
     */
    public final static CharacterConstant FRAGMENT_START = CharacterConstant.with('#');

    /**
     * A {@link RelativeUrl} with all components set to empty.
     */
    public final static RelativeUrl EMPTY_RELATIVE_URL = RelativeUrl.with(
            UrlPath.EMPTY,
            UrlQueryString.EMPTY,
            UrlFragment.EMPTY
    );

    // factories..................................................................................................

    /**
     * Examines the URL and attempts to parse it as a relative or absolute url.
     */
    public static Url parse(final String url) {
        checkUrl(url);

        return CharSequences.startsWith(
                url,
                MailToUrl.SCHEME
        ) ? MailToUrl.parseMailTo0(url) :
                parseNotMailToUrl(url);
    }

    private static Url parseNotMailToUrl(final String url) {
        final int colon = url.indexOf(':');
        return -1 != colon ?
                CaseSensitivity.INSENSITIVE.startsWith(url, DataUrl.SCHEME) ?
                        parseData(url) :
                        parseAbsolute(url) :
                parseRelative(url);
    }

    /**
     * Parses a {@link String url} into a {@link AbsoluteUrl}
     */
    public static AbsoluteUrl parseAbsolute(final String url) {
        return AbsoluteUrl.parseAbsolute0(url);
    }

    /**
     * Parses a {@link String url} expecting an {@link AbsoluteUrl} or {@link RelativeUrl}.
     */
    public static AbsoluteOrRelativeUrl parseAbsoluteOrRelative(final String url) {
        checkUrl(url);

        if (url.startsWith("data:")) {
            throw new IllegalArgumentException("Unknown protocol " + CharSequences.quoteAndEscape(url));
        }

        final boolean absolute;

        if (url.isEmpty()) {
            absolute = false;
        } else {
            final int fragment = url.indexOf('#');
            final int queryString = url.indexOf('?');
            final int path = url.indexOf('/');
            final int protocol = url.indexOf("://");

            absolute = -1 != protocol &&
                    (protocol < fragment ||
                            protocol < queryString ||
                            protocol < path
                    );
        }

        return absolute ?
                parseAbsolute(url) :
                parseRelative(url);
    }

    private static String checkUrl(final String url) {
        return Objects.requireNonNull(url, "url");
    }

    /**
     * Parses a {@link String url} into a {@link DataUrl}
     */
    public static DataUrl parseData(final String url) {
        return DataUrl.parseData0(url);
    }

    /**
     * Parses a {@link String url} into a {@link MailToUrl}.
     */
    public static MailToUrl parseMailTo(final String url) {
        return MailToUrl.parseMailTo0(url);
    }

    /**
     * Parses a {@link String url} into a {@link RelativeUrl}
     */
    public static RelativeUrl parseRelative(final String url) {
        return RelativeUrl.parseRelative0(url);
    }

    /**
     * Creates a {@link AbsoluteUrl}.
     */
    public static AbsoluteUrl absolute(final UrlScheme scheme,
                                       final Optional<UrlCredentials> credentials,
                                       final HostAddress host,
                                       final Optional<IpPort> port,
                                       final UrlPath path,
                                       final UrlQueryString query,
                                       final UrlFragment fragment) {
        return AbsoluteUrl.with(scheme, credentials, host, port, path, query, fragment);
    }

    /**
     * Creates a {@link DataUrl}.
     */
    public static DataUrl data(final Optional<MediaType> mediaType,
                               final boolean base64,
                               final Binary binary) {
        return DataUrl.with(
                mediaType,
                base64,
                binary
        );
    }

    /**
     * Creates an {@link RelativeUrl}.
     */
    public static RelativeUrl relative(final UrlPath path, final UrlQueryString query, final UrlFragment fragment) {
        return RelativeUrl.with(path, query, fragment);
    }

    // ctor.............................................................................................................

    /**
     * Package private constructor to limit sub classing.
     */
    Url() {
        super();
    }

    // isXXX............................................................................................................

    /**
     * Only {@link AbsoluteUrl} returns true.
     */
    public final boolean isAbsolute() {
        return this instanceof AbsoluteUrl;
    }

    /**
     * Only {@link DataUrl} returns true.
     */
    public final boolean isData() {
        return this instanceof DataUrl;
    }

    /**
     * Only {@link MailToUrl} returns true.
     */
    public final boolean isMailTo() {
        return this instanceof MailToUrl;
    }

    /**
     * Only {@link RelativeUrl} returns true
     */
    public final boolean isRelative() {
        return this instanceof RelativeUrl;
    }

    // UrlVisitor........................................................................................................

    abstract void accept(final UrlVisitor visitor);

    // helper...........................................................................................................

    /**
     * Convenient cast method used by would be public setters.
     */
    final <U extends Url> U cast() {
        return Cast.to(this);
    }

    // JsonNodeContext..................................................................................................

    /**
     * Accepts a json string holding an {@link Url}.
     */
    private static Url unmarshall(final JsonNode node,
                                  final JsonNodeUnmarshallContext context) {
        return unmarshall0(node, Url::parse);
    }

    /**
     * Accepts a json string holding an {@link AbsoluteUrl}.
     */
    static AbsoluteUrl unmarshallAbsolute(final JsonNode node,
                                          final JsonNodeUnmarshallContext context) {
        return unmarshall0(node, Url::parseAbsolute);
    }

    /**
     * Accepts a json string holding an {@link DataUrl}.
     */
    static DataUrl unmarshallData(final JsonNode node,
                                  final JsonNodeUnmarshallContext context) {
        return unmarshall0(node, Url::parseData);
    }

    /**
     * Accepts a json string holding an {@link MailToUrl}.
     */
    static MailToUrl unmarshallMailTo(final JsonNode node,
                                      final JsonNodeUnmarshallContext context) {
        return unmarshall0(
                node,
                Url::parseMailTo
        );
    }

    /**
     * Accepts a json string holding an {@link RelativeUrl}.
     */
    static RelativeUrl unmarshallRelative(final JsonNode node,
                                          final JsonNodeUnmarshallContext context) {
        return unmarshall0(node, Url::parseRelative);
    }

    private static <U extends Url> U unmarshall0(final JsonNode node,
                                                 final Function<String, U> parse) {
        Objects.requireNonNull(node, "node");

        return parse.apply(node.stringOrFail());
    }

    private JsonNode marshall(final JsonNodeMarshallContext context) {
        return JsonNode.string(this.value());
    }

    static {
        JsonNodeContext.register(
                JsonNodeContext.computeTypeName(Url.class),
                Url::unmarshall,
                Url::marshall,
                Url.class,
                AbsoluteUrl.class,
                DataUrl.class,
                MailToUrl.class,
                RelativeUrl.class);
    }
}
