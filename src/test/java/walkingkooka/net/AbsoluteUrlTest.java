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
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;
import walkingkooka.visit.Visiting;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class AbsoluteUrlTest extends AbsoluteOrRelativeUrlTestCase<AbsoluteUrl> {

    // constants

    private final static UrlScheme SCHEME = UrlScheme.HTTPS;
    private final static Optional<UrlCredentials> CREDENTIALS = UrlCredentials.NO_CREDENTIALS;
    private final static HostAddress HOST = HostAddress.with("host");
    private final static Optional<IpPort> PORT = Optional.of(IpPort.with(123));

    // tests

    @Test
    public void testWithNullSchemeFails() {
        assertThrows(NullPointerException.class, () -> AbsoluteUrl.with(null,
                CREDENTIALS,
                HOST,
                PORT,
                PATH,
                QUERY,
                FRAGMENT));
    }

    @Test
    public void testWithNullCredentialsFails() {
        assertThrows(NullPointerException.class, () -> AbsoluteUrl.with(SCHEME,
                null,
                HOST,
                PORT,
                PATH,
                QUERY,
                FRAGMENT));
    }

    @Test
    public void testWithNullHostFails() {
        assertThrows(NullPointerException.class, () -> AbsoluteUrl.with(SCHEME,
                CREDENTIALS,
                null,
                PORT,
                PATH,
                QUERY,
                FRAGMENT));
    }

    @Test
    public void testWithoutPortFails() {
        assertThrows(NullPointerException.class, () -> AbsoluteUrl.with(SCHEME,
                CREDENTIALS,
                HOST,
                null,
                PATH,
                QUERY,
                FRAGMENT));
    }

    @Test
    @Override
    public void testWith() {
        final AbsoluteUrl url = AbsoluteUrl.with(SCHEME, CREDENTIALS, HOST, PORT, PATH, QUERY, FRAGMENT);

        this.checkScheme(url, SCHEME);
        this.checkCredentials(url, CREDENTIALS);
        this.checkHost(url, HOST);
        this.checkPort(url, PORT);
        this.checkPath(url, PATH);
        this.checkQueryString(url, QUERY);
        this.checkFragment(url, FRAGMENT);
    }

    @Test
    public void testHttps() {
        final AbsoluteUrl url = AbsoluteUrl.with(UrlScheme.HTTPS, CREDENTIALS, HOST, PORT, PATH, QUERY, FRAGMENT);

        this.checkScheme(url, UrlScheme.HTTPS);
        this.checkCredentials(url, CREDENTIALS);
        this.checkHost(url, HOST);
        this.checkPort(url, PORT);
        this.checkPath(url, PATH);
        this.checkQueryString(url, QUERY);
        this.checkFragment(url, FRAGMENT);
    }

    @Test
    public void testSetSchemeSame() {
        final AbsoluteUrl url = this.createUrl();
        assertSame(url, url.setScheme(SCHEME));
    }

    @Test
    public void testSetSchemeDifferent() {
        final AbsoluteUrl url = this.createUrl();
        final UrlScheme value = UrlScheme.HTTP;
        this.checkNotEquals(value, SCHEME);

        final AbsoluteUrl different = url.setScheme(value);

        assertNotSame(url, different);

        this.checkScheme(different, value);
        this.checkCredentials(different, CREDENTIALS);
        this.checkHost(different, HOST);
        this.checkPort(different, PORT);
        this.checkPath(different, PATH);
        this.checkQueryString(different, QUERY);
        this.checkFragment(different, FRAGMENT);

        this.checkScheme(url, SCHEME);
        this.checkCredentials(url, CREDENTIALS);
        this.checkHost(url, HOST);
        this.checkPort(url, PORT);
        this.checkPath(url, PATH);
        this.checkQueryString(url, QUERY);
        this.checkFragment(url, FRAGMENT);
    }

    @Test
    public void testSetCredentialsSame() {
        final AbsoluteUrl url = this.createUrl();
        assertSame(url, url.setCredentials(CREDENTIALS));
    }

    @Test
    public void testSetCredentialsDifferent() {
        final AbsoluteUrl url = this.createUrl();
        final UrlCredentials credentials = UrlCredentials.with("user123", "pass456");
        final AbsoluteUrl different = url.setCredentials(Optional.of(credentials));

        assertNotSame(url, different);

        this.checkScheme(different, SCHEME);
        this.checkCredentials(different, credentials);
        this.checkHost(different, HOST);
        this.checkPort(different, PORT);
        this.checkPath(different, PATH);
        this.checkQueryString(different, QUERY);
        this.checkFragment(different, FRAGMENT);

        this.checkScheme(url, SCHEME);
        this.checkCredentials(url, CREDENTIALS);
        this.checkHost(url, HOST);
        this.checkPort(url, PORT);
        this.checkPath(url, PATH);
        this.checkQueryString(url, QUERY);
        this.checkFragment(url, FRAGMENT);
    }

    @Test
    public void testSetCredentialsSame2() {
        final Optional<UrlCredentials> credentials = Optional.of(UrlCredentials.with("user123", "pass456"));

        final AbsoluteUrl url = this.createUrl().setCredentials(credentials);
        assertSame(url, url.setCredentials(credentials));
    }

    @Test
    public void testSetPortSame() {
        final AbsoluteUrl url = this.createUrl();
        assertSame(url, url.setPort(PORT));
    }

    @Test
    public void testSetPortDifferent() {
        final AbsoluteUrl url = this.createUrl();
        final IpPort value = IpPort.DNS;
        final AbsoluteUrl different = url.setPort(Optional.of(value));

        assertNotSame(url, different);

        this.checkScheme(different, SCHEME);
        this.checkCredentials(different, CREDENTIALS);
        this.checkHost(different, HOST);
        this.checkPort(different, value);
        this.checkPath(different, PATH);
        this.checkQueryString(different, QUERY);
        this.checkFragment(different, FRAGMENT);

        this.checkScheme(url, SCHEME);
        this.checkCredentials(url, CREDENTIALS);
        this.checkHost(url, HOST);
        this.checkPort(url, PORT);
        this.checkPath(url, PATH);
        this.checkQueryString(url, QUERY);
        this.checkFragment(url, FRAGMENT);
    }

    @Test
    public void testSetHostSame() {
        final AbsoluteUrl url = this.createUrl();
        assertSame(url, url.setHost(HOST));
    }

    @Test
    public void testSetHostDifferent() {
        final AbsoluteUrl url = this.createUrl();
        final HostAddress value = HostAddress.with("different.example.com");
        final AbsoluteUrl different = url.setHost(value);

        assertNotSame(url, different);

        this.checkScheme(different, SCHEME);
        this.checkCredentials(different, CREDENTIALS);
        this.checkHost(different, value);
        this.checkPort(different, PORT);
        this.checkPath(different, PATH);
        this.checkQueryString(different, QUERY);
        this.checkFragment(different, FRAGMENT);

        this.checkScheme(url, SCHEME);
        this.checkCredentials(url, CREDENTIALS);
        this.checkHost(url, HOST);
        this.checkPort(url, PORT);
        this.checkPath(url, PATH);
        this.checkQueryString(url, QUERY);
        this.checkFragment(url, FRAGMENT);
    }

    @Test
    public void testSetSame() {
        final AbsoluteUrl url = this.createUrl();
        assertSame(url, url.set(url.scheme(), url.credentials(), url.host(), url.port()));
    }

    @Test
    public void testSetDifferent() {
        final AbsoluteUrl url = this.createUrl();

        final UrlScheme scheme = UrlScheme.HTTP;
        this.checkNotEquals(scheme, SCHEME);

        final HostAddress host = HostAddress.with("different.example.com");
        final IpPort port = IpPort.HTTPS;
        final Optional<UrlCredentials> credentials = Optional.of(UrlCredentials.with("user123", "pass456"));

        final AbsoluteUrl different = url.set(scheme, credentials, host, Optional.of(port));

        assertNotSame(url, different);

        this.checkScheme(different, scheme);
        this.checkCredentials(different, credentials);
        this.checkHost(different, host);
        this.checkPort(different, port);
        this.checkPath(different, PATH);
        this.checkQueryString(different, QUERY);
        this.checkFragment(different, FRAGMENT);

        this.checkScheme(url, SCHEME);
        this.checkCredentials(url, CREDENTIALS);
        this.checkHost(url, HOST);
        this.checkPort(url, PORT);
        this.checkPath(url, PATH);
        this.checkQueryString(url, QUERY);
        this.checkFragment(url, FRAGMENT);
    }

    // normalize...................................................................................................

    @Test
    public void testNormalizeRequired() {
        this.normalizeAndCheck(
                "https://example.com/path1/path2/../path3?query1=2",
                "https://example.com/path1/path3?query1=2"
        );
    }

    @Test
    public void testNormalizeRequired2() {
        this.normalizeAndCheck(
                "https://example.com/path1/path2/./path3?query1=2",
                "https://example.com/path1/path2/path3?query1=2"
        );
    }

    @Test
    public void testNormalizeUnnecessary() {
        this.normalizeAndCheck(
                "https://example.com/path1/path2/path3?query1=2"
        );
    }

    @Test
    public void testNormalizeWithoutPathUnnecessary() {
        this.normalizeAndCheck(
                "https://example.com"
        );
    }

    // toString.........................................................................................................

    @Test
    public void testToStringHttpWithDefaultPort() {
        toStringAndCheck(AbsoluteUrl.with(SCHEME,
                        CREDENTIALS,
                        HOST,
                        Optional.of(IpPort.HTTP),
                        PATH,
                        QUERY,
                        FRAGMENT),
                "https://host:80/path?query=value#fragment");
    }

    @Test
    public void testToStringHttpsWithDefaultPort() {
        toStringAndCheck(AbsoluteUrl.with(UrlScheme.HTTPS,
                        CREDENTIALS,
                        HOST,
                        Optional.of(IpPort.HTTPS),
                        PATH,
                        QUERY,
                        FRAGMENT),
                "https://host:443/path?query=value#fragment");
    }

    @Test
    public void testToStringHttpsWithNonDefaultPort() {
        toStringAndCheck(AbsoluteUrl.with(UrlScheme.HTTPS,
                        CREDENTIALS,
                        HOST,
                        PORT,
                        PATH,
                        QUERY,
                        FRAGMENT),
                "https://host:123/path?query=value#fragment");
    }

    @Test
    public void testToStringEmptyPath() {
        toStringAndCheck(AbsoluteUrl.with(SCHEME,
                        CREDENTIALS,
                        HOST,
                        PORT,
                        UrlPath.EMPTY,
                        UrlQueryString.EMPTY,
                        UrlFragment.EMPTY),
                "https://host:123"
        );
    }

    @Test
    public void testToStringUrl() {
        toStringAndCheck(AbsoluteUrl.with(SCHEME,
                CREDENTIALS,
                HOST,
                PORT,
                PATH,
                QUERY,
                FRAGMENT), "https://host:123/path?query=value#fragment"
        );
    }

    @Test
    @Override
    public void testToStringWithoutQuery() {
        toStringAndCheck(AbsoluteUrl.with(SCHEME,
                CREDENTIALS,
                HOST,
                PORT,
                PATH,
                UrlQueryString.EMPTY,
                FRAGMENT), "https://host:123/path#fragment");
    }

    @Test
    @Override
    public void testToStringWithoutFragment() {
        toStringAndCheck(AbsoluteUrl.with(SCHEME,
                CREDENTIALS,
                HOST,
                PORT,
                PATH,
                QUERY,
                UrlFragment.EMPTY), "https://host:123/path?query=value"
        );
    }

    @Test
    @Override
    public void testToStringWithoutQueryAndFragment() {
        toStringAndCheck(AbsoluteUrl.with(SCHEME,
                CREDENTIALS,
                HOST,
                PORT,
                PATH,
                UrlQueryString.EMPTY,
                UrlFragment.EMPTY), "https://host:123/path");
    }

    @Test
    public void testToStringUrlWithCredentials() {
        toStringAndCheck(AbsoluteUrl.with(SCHEME,
                Optional.of(UrlCredentials.with("user123", "password456")),
                HOST,
                PORT,
                PATH,
                UrlQueryString.EMPTY,
                UrlFragment.EMPTY), "https://user123:password456@host:123/path");
    }

    @Test
    public void testRelativeUrlWithoutPathQueryOrFragment() {
        final UrlPath path = UrlPath.ROOT;
        final UrlQueryString query = UrlQueryString.EMPTY;
        final UrlFragment fragment = UrlFragment.EMPTY;
        final AbsoluteUrl url = AbsoluteUrl.with(UrlScheme.HTTPS, CREDENTIALS, HOST, PORT, path, query,
                fragment);
        final RelativeUrl relative = url.relativeUrl();
        this.checkEquals(RelativeUrl.with(path, query, fragment).value(), relative.value(), "url");
        assertSame(path, relative.path(), "path");
        assertSame(query, relative.query(), "query");
        assertSame(fragment, relative.fragment(), "fragment");
    }


    @Test
    public void testFragmentRequiresEncoding() {
        final AbsoluteUrl url = AbsoluteUrl.parseAbsolute("http://example.com#space ");

        this.checkEquals(
                UrlFragment.parse("space"),
                url.fragment()
        );
    }

    // parseAbsolute..........................................................................................

    @Test
    public void testParseMissingSchemeFails() {
        this.parseStringFails("example.com", IllegalArgumentException.class);
    }

    @Test
    public void testParseSchemeHost() {
        final String string = "https://example.com";

        final AbsoluteUrl url = AbsoluteUrl.parseAbsolute0(string);
        this.checkScheme(url, UrlScheme.HTTPS);
        this.checkCredentialsAbsent(url);
        this.checkHost(url, HostAddress.with("example.com"));
        this.checkPortAbsent(url);
        this.checkPath(url, UrlPath.EMPTY);
        this.checkQueryString(url, UrlQueryString.EMPTY);
        this.checkFragment(url, UrlFragment.EMPTY);

        this.toStringAndCheck(
                url,
                string
        );
    }

    @Test
    public void testParseSchemeHost2() {
        final String string = "https://example.com";
        final AbsoluteUrl url = AbsoluteUrl.parseAbsolute0(string);

        this.checkScheme(url, UrlScheme.HTTPS);
        this.checkCredentialsAbsent(url);
        this.checkHost(url, HostAddress.with("example.com"));
        this.checkPortAbsent(url);
        this.checkPath(url, UrlPath.EMPTY);
        this.checkQueryString(url, UrlQueryString.EMPTY);
        this.checkFragment(url, UrlFragment.EMPTY);

        this.toStringAndCheck(
                url,
                string
        );
    }

    @Test
    public void testParseSchemeHostSlash() {
        final String string = "https://example.com/";
        final AbsoluteUrl url = AbsoluteUrl.parseAbsolute0(string);

        this.checkScheme(url, UrlScheme.HTTPS);
        this.checkCredentialsAbsent(url);
        this.checkHost(url, HostAddress.with("example.com"));
        this.checkPortAbsent(url);
        this.checkPath(url, UrlPath.parse("/"));
        this.checkQueryString(url, UrlQueryString.EMPTY);
        this.checkFragment(url, UrlFragment.EMPTY);

        this.toStringAndCheck(
                url,
                string
        );
    }

    @Test
    public void testParseSchemeHostPort() {
        final String string = "https://example.com:789";
        final AbsoluteUrl url = AbsoluteUrl.parseAbsolute0(string);

        this.checkScheme(url, UrlScheme.HTTPS);
        this.checkCredentialsAbsent(url);
        this.checkHost(url, HostAddress.with("example.com"));
        this.checkPort(url, IpPort.with(789));
        this.checkPath(url, UrlPath.EMPTY);
        this.checkQueryString(url, UrlQueryString.EMPTY);
        this.checkFragment(url, UrlFragment.EMPTY);

        this.toStringAndCheck(
                url,
                string
        );
    }

    @Test
    public void testParseSchemeHostPortSlash() {
        final String string = "https://example.com:789/";
        final AbsoluteUrl url = AbsoluteUrl.parseAbsolute0(string);

        this.checkScheme(url, UrlScheme.HTTPS);
        this.checkCredentialsAbsent(url);
        this.checkHost(url, HostAddress.with("example.com"));
        this.checkPort(url, IpPort.with(789));
        this.checkPath(url, UrlPath.parse("/"));
        this.checkQueryString(url, UrlQueryString.EMPTY);
        this.checkFragment(url, UrlFragment.EMPTY);

        this.toStringAndCheck(
                url,
                string
        );
    }

    @Test
    public void testParseCredentialsMissingPasswordFails() {
        this.parseStringFails("\"https://abc@example.com", IllegalArgumentException.class);
    }

    @Test
    public void testParseSchemeCredentialsHost() {
        final String string = "http://abc:def@example.com";
        final AbsoluteUrl url = AbsoluteUrl.parseAbsolute0(string);

        this.checkScheme(url, UrlScheme.HTTP);
        this.checkCredentials(url, UrlCredentials.with("abc", "def"));
        this.checkHost(url, HostAddress.with("example.com"));
        this.checkPortAbsent(url);
        this.checkPath(url, UrlPath.EMPTY);
        this.checkQueryString(url, UrlQueryString.EMPTY);
        this.checkFragment(url, UrlFragment.EMPTY);

        this.toStringAndCheck(
                url,
                string
        );
    }

    @Test
    public void testParseSchemeHostPath() {
        final String string = "http://example.com/path/to/file";

        final AbsoluteUrl url = AbsoluteUrl.parseAbsolute0(string);

        this.checkScheme(url, UrlScheme.HTTP);
        this.checkCredentialsAbsent(url);
        this.checkHost(url, HostAddress.with("example.com"));
        this.checkPortAbsent(url);
        this.checkPath(url, UrlPath.parse("/path/to/file"));
        this.checkQueryString(url, UrlQueryString.EMPTY);
        this.checkFragment(url, UrlFragment.EMPTY);

        this.toStringAndCheck(
                url,
                string
        );
    }

    @Test
    public void testParseSchemeHostPathEndsSlash() {
        final String string = "http://example.com/path/to/file/";

        final AbsoluteUrl url = AbsoluteUrl.parseAbsolute0(string);

        this.checkScheme(url, UrlScheme.HTTP);
        this.checkCredentialsAbsent(url);
        this.checkHost(url, HostAddress.with("example.com"));
        this.checkPortAbsent(url);
        this.checkPath(url, UrlPath.parse("/path/to/file/"));
        this.checkQueryString(url, UrlQueryString.EMPTY);
        this.checkFragment(url, UrlFragment.EMPTY);

        this.toStringAndCheck(
                url,
                string
        );
    }

    @Test
    public void testParseSchemeHostPathQueryString() {
        final String string = "http://example.com/path123?query456";

        final AbsoluteUrl url = AbsoluteUrl.parseAbsolute0(string);

        this.checkScheme(url, UrlScheme.HTTP);
        this.checkCredentialsAbsent(url);
        this.checkHost(url, HostAddress.with("example.com"));
        this.checkPortAbsent(url);
        this.checkPath(url, UrlPath.parse("/path123"));
        this.checkQueryString(url, UrlQueryString.parse("query456"));
        this.checkFragment(url, UrlFragment.EMPTY);

        this.toStringAndCheck(
                url,
                string
        );
    }

    @Test
    public void testParseSchemeHostPathQueryStringEncoded() {
        final String string = "http://example.com/path123?query=4%2B56";

        final AbsoluteUrl url = AbsoluteUrl.parseAbsolute0(string);

        this.checkScheme(url, UrlScheme.HTTP);
        this.checkCredentialsAbsent(url);
        this.checkHost(url, HostAddress.with("example.com"));
        this.checkPortAbsent(url);
        this.checkPath(url, UrlPath.parse("/path123"));
        this.checkQueryString(url, UrlQueryString.parse("query=4%2B56"));
        this.checkFragment(url, UrlFragment.EMPTY);

        this.toStringAndCheck(
                url,
                string
        );
    }

    @Test
    public void testParseSchemeHostPathQueryStringFragment() {
        final String string = "http://example.com/path123?query456#fragment789";

        final AbsoluteUrl url = AbsoluteUrl.parseAbsolute0(string);

        this.checkScheme(url, UrlScheme.HTTP);
        this.checkCredentialsAbsent(url);
        this.checkHost(url, HostAddress.with("example.com"));
        this.checkPortAbsent(url);
        this.checkPath(url, UrlPath.parse("/path123"));
        this.checkQueryString(url, UrlQueryString.parse("query456"));
        this.checkFragment(url, UrlFragment.with("fragment789"));

        this.toStringAndCheck(
                url,
                string
        );
    }

    @Test
    public void testParseFragmentWithPlus() {
        final String string = "http://example.com/path123?query456#fragment+";

        final AbsoluteUrl url = AbsoluteUrl.parseAbsolute0(string);

        this.checkScheme(url, UrlScheme.HTTP);
        this.checkCredentialsAbsent(url);
        this.checkHost(url, HostAddress.with("example.com"));
        this.checkPortAbsent(url);
        this.checkPath(url, UrlPath.parse("/path123"));
        this.checkQueryString(url, UrlQueryString.parse("query456"));
        this.checkFragment(url, UrlFragment.with("fragment "));

        this.toStringAndCheck(
                url,
                string
        );
    }

    @Test
    public void testTryParseFails() {
        this.checkEquals(Optional.empty(), AbsoluteUrl.tryParse("abc"));
    }

    @Test
    public void testTryParseSuccess() {
        final String url = "https://example.com/path123?query456#fragment789";
        this.checkEquals(Optional.of(AbsoluteUrl.parseAbsolute0(url)), AbsoluteUrl.tryParse(url));
    }

    // HashCodeEqualsDefined ..................................................................................................

    @Test
    public void testEqualsDifferentScheme() {
        this.checkNotEquals(
                Url.absolute(
                        UrlScheme.HTTP,
                        CREDENTIALS,
                        HOST,
                        PORT,
                        PATH,
                        QUERY,
                        FRAGMENT
                )
        );
    }

    @Test
    public void testEqualsDifferentHost() {
        this.checkNotEquals(
                Url.absolute(
                        UrlScheme.HTTPS,
                        CREDENTIALS,
                        HostAddress.with("different-" + HOST),
                        PORT,
                        PATH,
                        QUERY,
                        FRAGMENT
                )
        );
    }

    @Test
    public void testEqualsDifferentPort() {
        this.checkNotEquals(Url.absolute(UrlScheme.HTTPS,
                CREDENTIALS,
                HOST,
                Optional.of(IpPort.with(9999)),
                PATH,
                QUERY,
                FRAGMENT));
    }

    // UrlVisitor......................................................................................................

    @Test
    public void testAccept() {
        final StringBuilder b = new StringBuilder();
        final AbsoluteUrl url = this.createUrl();

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
            protected void visit(final AbsoluteUrl u) {
                assertSame(url, u);
                b.append("5");
            }
        }.accept(url);
        this.checkEquals("152", b.toString());
    }

    // toURL............................................................................................................

    @Test
    public void testToURL() throws MalformedURLException {
        final String url = "https://example.com/123";
        final AbsoluteUrl absoluteUrl = Url.parseAbsolute(url);
        this.checkEquals(new URL(url), absoluteUrl.toURL());
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
                this.createUrl(),
                "https://host:123/path?query=value#fragment"
        );
    }

    @Test
    public void testToStringWithCredentials() {
        this.toStringAndCheck(
                this.createUrl().setCredentials(
                        Optional.of(
                                UrlCredentials.with("user1", "password2")
                        )
                ),
                "https://user1:password2@host:123/path?query=value#fragment"
        );
    }

    @Test
    public void testToStringUpperCasedScheme() {
        this.toStringAndCheck(
                AbsoluteUrl.with(UrlScheme.with("TEST"),
                        AbsoluteUrl.NO_CREDENTIALS,
                        HOST,
                        PORT,
                        UrlPath.EMPTY,
                        UrlQueryString.EMPTY,
                        UrlFragment.EMPTY),
                "test://host:123"
        );
    }

    @Test
    public void testToStringFragmentPlus() {
        this.toStringAndCheck(
                AbsoluteUrl.parseAbsolute("http://example/path123?query456#fragment+"),
                "http://example/path123?query456#fragment+"
        );
    }

    @Test
    public void testToStringFragmentPercent20() {
        this.toStringAndCheck(
                AbsoluteUrl.parseAbsolute("http://example/path123?query456#fragment%20"),
                "http://example/path123?query456#fragment+"
        );
    }

    @Test
    public void testParseToStringRoundtrip() throws Exception {
        final String string = "http://example/path123?query456#fragment";

        // some chars eg 55296 get encoded back into a question mark.
        for (int i = 128; i < 55000; i++) {
            final String stringWith = string + URLEncoder.encode(
                    String.valueOf((char) i),
                    "UTF-8"
            );

            this.toStringAndCheck(
                    AbsoluteUrl.parseAbsolute(stringWith),
                    stringWith
            );
        }
    }

    // factory

    @Override
    protected AbsoluteUrl createUrl(final UrlPath path, final UrlQueryString query, final UrlFragment fragment) {
        return AbsoluteUrl.with(SCHEME,
                CREDENTIALS,
                HOST,
                PORT,
                path,
                query,
                fragment);
    }

    private void checkScheme(final AbsoluteUrl url, final UrlScheme scheme) {
        this.checkEquals(scheme, url.scheme(), "scheme");
    }

    private void checkCredentials(final AbsoluteUrl url, final UrlCredentials credentials) {
        this.checkCredentials(url, Optional.of(credentials));
    }

    private void checkCredentialsAbsent(final AbsoluteUrl url) {
        this.checkCredentials(url, AbsoluteUrl.NO_CREDENTIALS);
    }

    private void checkCredentials(final AbsoluteUrl url, final Optional<UrlCredentials> credentials) {
        this.checkEquals(credentials, url.credentials(), "credentials");
    }

    private void checkHost(final AbsoluteUrl url, final HostAddress host) {
        this.checkEquals(host, url.host(), "host");
    }

    private void checkPort(final AbsoluteUrl url, final IpPort port) {
        this.checkPort(url, Optional.of(port));
    }

    private void checkPortAbsent(final AbsoluteUrl url) {
        this.checkPort(url, AbsoluteUrl.NO_PORT);
    }

    private void checkPort(final AbsoluteUrl url, final Optional<IpPort> port) {
        this.checkEquals(port, url.port(), "port");
    }

    // ClassTesting ....................................................................................................

    @Override
    public Class<AbsoluteUrl> type() {
        return AbsoluteUrl.class;
    }

    // JsonNodeMarshallTesting .........................................................................................

    @Override
    public AbsoluteUrl unmarshall(final JsonNode node,
                                  final JsonNodeUnmarshallContext context) {
        return Url.unmarshallAbsolute(node, context);
    }

    // ParseStringTesting ..............................................................................................

    @Override
    public AbsoluteUrl parseString(final String text) {
        return AbsoluteUrl.parseAbsolute0(text);
    }
}
