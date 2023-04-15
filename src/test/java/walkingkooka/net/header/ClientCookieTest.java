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

package walkingkooka.net.header;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.list.Lists;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertThrows;


final public class ClientCookieTest extends CookieTestCase<ClientCookie> {

    @Test
    public void testWith() {
        final ClientCookie cookie = ClientCookie.with(NAME, VALUE);
        checkName(cookie);
        checkValue(cookie);
    }

    @Test
    public void testWithQuotedValue() {
        final String value = "\"123\"";
        final ClientCookie cookie = ClientCookie.with(NAME, value);
        checkName(cookie);
        checkValue(cookie, value);
    }

    @Test
    public void testCookieClient() {
        final ClientCookie cookie = Cookie.client(NAME, VALUE);
        checkName(cookie);
        checkValue(cookie);
    }

    @Test
    public void testSetNameDifferent() {
        final CookieName name = CookieName.with("different");
        final ClientCookie cookie = ClientCookie.with(NAME, VALUE);
        final ClientCookie different = cookie.setName(name);
        checkName(different, name);
        checkValue(different);
    }

    @Test
    public void testSetValueDifferent() {
        final ClientCookie cookie = ClientCookie.with(NAME, VALUE);
        final String value = "123";
        final ClientCookie different = cookie.setValue(value);
        checkName(different);
        checkValue(different, value);
    }

    @Test
    public void testFromNullCookieFails() {
        assertThrows(NullPointerException.class, () -> ClientCookie.from((javax.servlet.http.Cookie) null));
    }

    @Test
    public void testFromJavaxServletHttpCookie() {
        final javax.servlet.http.Cookie source = new javax.servlet.http.Cookie(NAME.value(),
                VALUE);

        final ClientCookie cookie = ClientCookie.from(source);
        checkName(cookie);
        checkValue(cookie);
    }

    @Test
    public void testCookieClientFrom() {
        final javax.servlet.http.Cookie source = new javax.servlet.http.Cookie(NAME.value(),
                VALUE);

        final ClientCookie cookie = Cookie.clientFrom(source);
        checkName(cookie);
        checkValue(cookie);
    }

    @Test
    public void testToJavaxServletHttpCookie() {
        final javax.servlet.http.Cookie cookie = ClientCookie.with(NAME, VALUE)
                .toJavaxServletCookie();
        this.checkEquals(NAME.value(), cookie.getName(), "name");
        this.checkEquals(VALUE, cookie.getValue(), "value");
    }

    // parseClientHeader................................................................

    @Test
    public void testParseClientHeaderNulFails() {
        assertThrows(NullPointerException.class, () -> Cookie.parseClientHeader(null));
    }

    @Test
    public void testParseClientHeaderWithOneCookie() {
        this.parseHeaderAndCheck("cookie123=value456;", createCookie(NAME, VALUE));
    }

    @Test
    public void testParseClientHeaderWithOneCookie2() {
        this.parseHeaderAndCheck("name2=value2;", createCookie("name2", "value2"));
    }

    @Test
    public void testParseClientHeaderWithManyCookiesWithoutTrailingSemiColon() {
        this.parseHeaderAndCheck("cookie123=value456", createCookie(NAME, VALUE));
    }

    @Test
    public void testParseClientHeaderWithManyCookies() {
        this.parseHeaderAndCheck("cookie123=value456;", createCookie(NAME, VALUE));
    }

    @Test
    public void testParseClientHeaderWithManyCookies2() {
        this.parseHeaderAndCheck("cookie1=value1;cookie2=value2;cookie3=value3;", //
                createCookie("cookie1", "value1"), //
                createCookie("cookie2", "value2"), //
                createCookie("cookie3", "value3"));
    }

    @Test
    public void testParseClientHeaderWithManyCookiesWithWhitespace() {
        this.parseHeaderAndCheck("cookie1=value1; cookie2=value2; cookie3=value3;", //
                createCookie("cookie1", "value1"), //
                createCookie("cookie2", "value2"), //
                createCookie("cookie3", "value3"));
    }

    @Test
    public void testParseClientHeaderWithManyCookies2WithoutTrailingSemiColon() {
        this.parseHeaderAndCheck("cookie1=value1;cookie2=value2;cookie3=value3", //
                createCookie("cookie1", "value1"), //
                createCookie("cookie2", "value2"), //
                createCookie("cookie3", "value3"));
    }

    // Caused by: walkingkooka.InvalidCharacterException: Invalid character '+' at 85 in "U92vJei3ldxFc3amPwplZOQ11IKfQK3rr94G4JK65PE=.1673218217672.zjI1J89fa0b8OODoBRqlMnqfJf+V5mu4OqJCJ7tbKgg="
    //	at walkingkooka.net.header.Cookie.checkValue0(Cookie.java:207)
    //	at walkingkooka.net.header.Cookie.checkValue(Cookie.java:184)
    //	at walkingkooka.net.header.ClientCookie.with(ClientCookie.java:96)
    //	at walkingkooka.net.header.Cookie.client(Cookie.java:132)
    //	at walkingkooka.net.header.ClientCookie.parseHeader(ClientCookie.java:75)
    //	at walkingkooka.net.header.Cookie.parseClientHeader(Cookie.java:73)
    //	at walkingkooka.net.header.ClientCookieListHeaderHandler.parse0(ClientCookieListHeaderHandler.java:43)
    //	at walkingkooka.net.header.ClientCookieListHeaderHandler.parse0(ClientCookieListHeaderHandler.java:27)
    //	at walkingkooka.net.header.HeaderHandler.parse(HeaderHandler.java:331)
    @Test
    public void testParseClientHeaderIncludesPlusSign() {
        this.parseHeaderAndCheck(
                "cookie1=U92vJei3ldxFc3amPwplZOQ11IKfQK3rr94G4JK65PE=.1673218217672.zjI1J89fa0b8OODoBRqlMnqfJf+V5mu4OqJCJ7tbKgg=", //
                createCookie(
                        "cookie1",
                        "U92vJei3ldxFc3amPwplZOQ11IKfQK3rr94G4JK65PE=.1673218217672.zjI1J89fa0b8OODoBRqlMnqfJf+V5mu4OqJCJ7tbKgg="
                )
        );
    }

    private void parseHeaderAndCheck(final String header, final ClientCookie... cookies) {
        this.checkEquals(Lists.of(cookies), ClientCookie.parseHeader(header), header);
    }

    // header ....................................................................................

    @Test
    public void testHeader() {
        this.checkEquals(HttpHeaderName.COOKIE, this.createCookie().header());
    }

    // toHeader ....................................................................................

    @Test
    public void testToHeaderText() {
        this.toHeaderTextAndCheck("cookie123=value456;");
    }

    // toHeaderTextList ................................................................

    @Test
    public void testToHeaderTextListNullFails() {
        assertThrows(NullPointerException.class, () -> ClientCookie.toHeaderTextList(null));
    }

    @Test
    public void testToHeaderTextListOne() {
        this.toHeaderTextListAndCheck("cookie123=value456;", this.createCookie());
    }

    @Test
    public void testToHeaderTextListTwo() {
        this.toHeaderTextListAndCheck("cookie123=value456; cookie789=xyz;",
                this.createCookie(),
                ClientCookie.parseHeader("cookie789=xyz;").get(0));
    }

    private void toHeaderTextListAndCheck(final String toString,
                                          final ClientCookie... cookies) {
        this.checkEquals(toString,
                ClientCookie.toHeaderTextList(Lists.of(cookies)),
                "format " + Arrays.toString(cookies));
    }

    // toString.................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(ClientCookie.with(NAME, VALUE), "cookie123=value456;");
    }

    @Test
    public void testToStringWithoutValue() {
        this.toStringAndCheck(ClientCookie.with(NAME, ""), "cookie123=;");
    }

    private ClientCookie createCookie(final String name, final String value) {
        return createCookie(CookieName.with(name), value);
    }

    @Override
    ClientCookie createCookie(final CookieName name, final String value) {
        return ClientCookie.with(name, value);
    }

    @Override
    public ClientCookie createHeader() {
        return this.createCookie();
    }

    @Override
    public ClientCookie createDifferentHeader() {
        return ClientCookie.with(CookieName.with("different-name"), "different-value");
    }

    @Override
    public boolean isRequest() {
        return true;
    }

    @Override
    public boolean isResponse() {
        return false;
    }

    @Override
    public Class<ClientCookie> type() {
        return ClientCookie.class;
    }
}
