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

import walkingkooka.compare.Comparators;
import walkingkooka.text.CharSequences;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;
import java.util.Optional;

/**
 * Note that equality is based by comparing all components, with only the scheme being compared while ignoring case.
 */
public final class AbsoluteUrl extends AbsoluteOrRelativeUrl implements Comparable<AbsoluteUrl> {

    public final static Optional<UrlCredentials> NO_CREDENTIALS = Optional.empty();
    public final static Optional<IpPort> NO_PORT = Optional.empty();

    /**
     * Tries to create an {@link AbsoluteUrl} or returns {@link Optional#empty()}.
     */
    public static Optional<AbsoluteUrl> tryParse(final String url) {
        AbsoluteUrl absoluteUrl = null;
        try {
            absoluteUrl = parseAbsolute0(url);
        } catch (final IllegalArgumentException fail) {
            // nop
        }
        return Optional.ofNullable(absoluteUrl);
    }

    /**
     * Parses a {@link String url} into a {@link AbsoluteUrl}
     */
    static AbsoluteUrl parseAbsolute0(final String url) {
        Objects.requireNonNull(url, "url");

        try {
            return parseAbsolute1(
                new URL(url),
                url
            );
        } catch (final MalformedURLException cause) {
            throw new IllegalArgumentException(cause.getMessage(), cause);
        }
    }

    private static AbsoluteUrl parseAbsolute1(final URL url,
                                              final String urlString) {
        return AbsoluteUrl.with(
            UrlScheme.with(url.getProtocol()),
            credentials(url),
            HostAddress.with(
                checkHost(url)
            ),
            ipPort(url),
            UrlPath.parse(url.getPath()),
            UrlQueryString.parse(nullToEmpty(url.getQuery())),
            UrlFragment.parse(nullToEmpty(url.getRef()))
        );
    }

    private static String checkHost(final URL url) {
        final String host = url.getHost();
        if (CharSequences.isNullOrEmpty(host)) {
            throw new IllegalArgumentException("Missing host name");
        }
        return host;
    }

    private static Optional<UrlCredentials> credentials(final URL url) {
        final String userInfo = url.getUserInfo();
        return CharSequences.isNullOrEmpty(userInfo) ?
            NO_CREDENTIALS :
            credentials0(userInfo);
    }

    private static Optional<UrlCredentials> credentials0(final String userInfo) {
        final int separator = userInfo.indexOf(":");
        if (-1 == separator) {
            throw new IllegalArgumentException("Invalid user credentials");
        }
        return Optional.of(UrlCredentials.with(
            userInfo.substring(0, separator),
            userInfo.substring(separator + 1)));
    }

    private static Optional<IpPort> ipPort(final URL url) {
        final int value = url.getPort();
        return -1 != value ?
            Optional.of(IpPort.with(value)) :
            NO_PORT;
    }

    /**
     * Factory that creates an {@link AbsoluteUrl}
     */
    static AbsoluteUrl with(final UrlScheme scheme,
                            final Optional<UrlCredentials> credentials,
                            final HostAddress host,
                            final Optional<IpPort> port,
                            final UrlPath path,
                            final UrlQueryString query,
                            final UrlFragment fragment) {
        Objects.requireNonNull(scheme, "scheme");
        Objects.requireNonNull(credentials, "credentials");
        Objects.requireNonNull(host, "host");
        Objects.requireNonNull(port, "port");
        Objects.requireNonNull(path, "path");
        Objects.requireNonNull(query, "query");
        Objects.requireNonNull(fragment, "fragment");

        return new AbsoluteUrl(scheme, credentials, host, port, path, query, fragment);
    }

    /**
     * Private constructor.
     */
    private AbsoluteUrl(final UrlScheme scheme,
                        final Optional<UrlCredentials> credentials,
                        final HostAddress host,
                        final Optional<IpPort> port,
                        final UrlPath path,
                        final UrlQueryString query,
                        final UrlFragment fragment) {
        super(path, query, fragment);

        this.scheme = scheme;
        this.credentials = credentials;
        this.host = host;
        this.port = port;
    }

    // Url

    @Override
    public AbsoluteUrl setPath(final UrlPath path) {
        return this.setPath0(path).cast();
    }

    @Override
    public AbsoluteUrl appendPath(final UrlPath path) {
        return this.appendPath0(path).cast();
    }

    @Override
    public AbsoluteUrl appendPathName(final UrlPathName name) {
        return this.appendPathName0(name)
            .cast();
    }

    @Override
    public AbsoluteUrl setQuery(final UrlQueryString query) {
        return this.setQuery0(query).cast();
    }

    @Override
    public AbsoluteUrl setFragment(final UrlFragment fragment) {
        return this.setFragment0(fragment).cast();
    }

    /**
     * Unconditionally creates a new {@link AbsoluteUrl}
     */
    @Override
    AbsoluteUrl replace(final UrlPath path,
                        final UrlQueryString query,
                        final UrlFragment fragment) {
        return new AbsoluteUrl(
            this.scheme,
            this.credentials,
            this.host,
            this.port,
            path,
            query,
            fragment
        );
    }

    /**
     * Returns the scheme with this URL.
     */
    public UrlScheme scheme() {
        return this.scheme;
    }

    public AbsoluteUrl setScheme(final UrlScheme scheme) {
        Objects.requireNonNull(scheme, "scheme");

        return this.scheme.equals(scheme) ?
            this :
            new AbsoluteUrl(
                scheme,
                this.credentials,
                this.host,
                this.port,
                this.path,
                this.query,
                this.fragment
            );
    }

    private final UrlScheme scheme;

    /**
     * Returns the credentials with this URL.
     */
    public Optional<UrlCredentials> credentials() {
        return this.credentials;
    }

    public AbsoluteUrl setCredentials(final Optional<UrlCredentials> credentials) {
        Objects.requireNonNull(credentials, "credentials");

        return this.credentials.equals(credentials) ?
            this :
            new AbsoluteUrl(
                this.scheme,
                credentials,
                this.host,
                this.port,
                this.path,
                this.query,
                this.fragment
            );
    }

    private final Optional<UrlCredentials> credentials;

    /**
     * Retrieves the {@link HostAddress} embedded within this URL.
     */
    public HostAddress host() {
        return this.host;
    }

    public AbsoluteUrl setHost(final HostAddress host) {
        Objects.requireNonNull(host, "host");

        return this.host.equals(host) ?
            this :
            this.replaceHost(host);
    }

    /**
     * Unconditionally replaces the host.
     */
    private AbsoluteUrl replaceHost(final HostAddress host) {
        return new AbsoluteUrl(
            this.scheme,
            this.credentials,
            host,
            this.port,
            this.path,
            this.query,
            this.fragment
        );
    }

    private final HostAddress host;

    /**
     * Retrieves the {@link IpPort} within this URL. Note this value is never null and if not present in the URL will have the default for the scheme.
     */
    public Optional<IpPort> port() {
        return this.port;
    }

    public AbsoluteUrl setPort(final Optional<IpPort> port) {
        Objects.requireNonNull(port, "port");

        return this.port.equals(port) ?
            this :
            new AbsoluteUrl(
                this.scheme,
                this.credentials,
                this.host,
                port,
                this.path,
                this.query,
                this.fragment
            );
    }

    private final Optional<IpPort> port;

    @Override
    public AbsoluteUrl set(final UrlScheme scheme,
                           final Optional<UrlCredentials> credentials,
                           final HostAddress host,
                           final Optional<IpPort> port) {
        return this.setScheme(scheme)
            .setCredentials(credentials)
            .setHost(host)
            .setPort(port);
    }

    /**
     * Returns a {@link RelativeUrl} built from the path, query and fragment components. The path and protocol will be lost.
     */
    public RelativeUrl relativeUrl() {
        return RelativeUrl.with(
            this.path,
            this.query,
            this.fragment
        );
    }

    // UrlVisitor........................................................................................................

    @Override
    void accept(final UrlVisitor visitor) {
        visitor.visit(this);
    }

    // normalize........................................................................................................

    /**
     * Normalizes the hostname, port and path if necessary.<br>
     * Other possible components that may require normalizing will not be changed.
     * <br>
     * https://en.wikipedia.org/wiki/URI_normalization
     * <pre>
     * Normalizations that preserve semantics
     * The following normalizations are described in RFC 3986 [1] to result in equivalent URIs:
     *
     * Converting percent-encoded triplets to uppercase. The hexadecimal digits within a percent-encoding triplet of the
     * URI (e.g., %3a versus %3A) are case-insensitive and therefore should be normalized to use uppercase letters for the digits A-F.[2] Example:
     * http://example.com/foo%2a → http://example.com/foo%2A
     *
     * Converting the scheme and host to lowercase. The scheme and host components of the URI are case-insensitive and
     * therefore should be normalized to lowercase.[3] Example:
     * HTTP://User@Example.COM/Foo → http://User@example.com/Foo
     *
     * Decoding percent-encoded triplets of unreserved characters. Percent-encoded triplets of the URI in the ranges of
     * ALPHA (%41–%5A and %61–%7A), DIGIT (%30–%39), hyphen (%2D), period (%2E), underscore (%5F), or tilde (%7E)
     * do not require percent-encoding and should be decoded to their corresponding unreserved characters.[4] Example:
     * http://example.com/%7Efoo → http://example.com/~foo
     *
     * Removing dot-segments. Dot-segments . and .. in the path component of the URI should be removed by applying
     * the remove_dot_segments algorithm[5] to the path described in RFC 3986.[6] Example:
     * http://example.com/foo/./bar/baz/../qux → http://example.com/foo/bar/qux
     *
     * Converting an empty path to a "/" path. In presence of an authority component, an empty path component should be
     * normalized to a path component of "/".[7] Example:
     * http://example.com → http://example.com/
     *
     * Removing the default port. An empty or default port component of the URI (port 80 for the http scheme)
     * with its ":" delimiter should be removed.[8] Example:
     * http://example.com:80/ → http://example.com/
     * </pre>
     */
    @Override
    public AbsoluteUrl normalize() {
        AbsoluteUrl normalized = this;

        final HostAddress address = this.host();
        if (address.isName()) {
            // * Converting the scheme and host to lowercase. The scheme and host components of the URI are case-insensitive
            //   and therefore should be normalized to lowercase.[3] Example:
            // * HTTP://User@Example.COM/Foo → http://User@example.com/Foo

            final String addressString = address.value();
            final String addressStringLowerCase = addressString.toLowerCase();
            if (false == addressString.equals(addressStringLowerCase)) {
                // cant use setHost because the equals test would fail and return the original and never the new lowercased
                normalized = normalized.replaceHost(
                    HostAddress.with(addressStringLowerCase)
                );
            }
        }

        // Removing the default port. An empty or default port component of the URI (port 80 for the http scheme)
        // with its ":" delimiter should be removed.[8] Example:
        // http://example.com:80/ → http://example.com/
        final Optional<IpPort> maybePort = this.port;
        if (maybePort.isPresent()) {
            final UrlScheme scheme = this.scheme;
            if (UrlScheme.HTTP.equals(scheme)) {

                if (IpPort.HTTP.equals(maybePort.get())) {
                    normalized = normalized.setPort(NO_PORT);
                }
            } else {
                if (UrlScheme.HTTPS.equals(scheme)) {

                    if (IpPort.HTTPS.equals(maybePort.get())) {
                        normalized = normalized.setPort(NO_PORT);
                    }
                }
            }
        }

        // * Converting an empty path to a "/" path. In presence of an authority component, an empty path component should be
        // * normalized to a path component of "/".[7] Example:
        // * http://example.com → http://example.com/
        //
        // * Removing dot-segments. Dot-segments . and .. in the path component of the URI should be removed by applying
        // * the remove_dot_segments algorithm[5] to the path described in RFC 3986.[6] Example:
        // * http://example.com/foo/./bar/baz/../qux → http://example.com/foo/bar/qux
        return normalized.setPath(
            normalized.path()
                .normalize()
        );
    }

    // java.net.URL.....................................................................................................

    public URL toURL() throws MalformedURLException {
        return new URL(this.toString());
    }

    // Object ..........................................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(this.scheme,
            this.credentials,
            this.host,
            this.port,
            this.path,
            this.query,
            this.fragment);
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
            other instanceof AbsoluteUrl &&
                this.equals0((AbsoluteUrl) other);
    }

    private boolean equals0(final AbsoluteUrl other) {
        return this.scheme.equals(other.scheme) && //
            this.credentials.equals(other.credentials) && //
            this.host.equals(other.host) && //
            this.port.equals(other.port) && //
            this.path.equals(other.path) && //
            this.query.equals(other.query) && //
            this.fragment.equals(other.fragment);
    }

    @Override
    void toString0(final StringBuilder b) {
        this.scheme.toString0(b);

        this.credentials.ifPresent((c) -> c.absoluteUrlToString(b));
        this.host.toString0(b);
        this.port.ifPresent((p) -> p.toString0(b));
    }

    // Comparable.......................................................................................................

    /**
     * Comparing involves comparing various components in the following order, until a non zero result is found.
     * <ol>
     *     <li>{@link UrlScheme}</li>
     *     <li>{@link UrlCredentials}</li>
     *     <li>{@link HostAddress}</li>
     *     <li>{@link UrlPath}</li>
     *     <li>{@link UrlQueryString}</li>
     *     <li>{@link UrlFragment}</li>
     * </ol>
     * Identical urls will of course return zero
     */
    @Override
    public int compareTo(final AbsoluteUrl other) {
        int result = this.scheme.compareTo(other.scheme);
        if (Comparators.EQUAL == result) {
            final UrlCredentials credentials = this.credentials.orElse(null);
            final UrlCredentials otherCredentials = other.credentials.orElse(null);

            if (null == credentials || null == otherCredentials) {
                if (null == credentials && null == otherCredentials) {
                    result = Comparators.EQUAL;
                } else {
                    result = null == credentials ?
                        Comparators.LESS :
                        Comparators.MORE;
                }
            } else {
                result = credentials.compareTo(otherCredentials);
            }

            if (Comparators.EQUAL == result) {
                result = this.host.compareTo(other.host);
                if (Comparators.EQUAL == result) {

                    if (Comparators.EQUAL == result) {
                        result = this.compareToAbsoluteOrRelativeUrl(other);
                    }
                }
            }
        }

        return result;
    }
}
