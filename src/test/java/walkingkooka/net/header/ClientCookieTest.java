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

import static org.junit.jupiter.api.Assertions.assertEquals;
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
        assertThrows(NullPointerException.class, () -> {
            ClientCookie.from((javax.servlet.http.Cookie) null);
        });
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
        assertEquals(NAME.value(), cookie.getName(), "name");
        assertEquals(VALUE, cookie.getValue(), "value");
    }

    // parseClientHeader................................................................

    @Test
    public void testParseClientHeaderNulFails() {
        assertThrows(NullPointerException.class, () -> {
            Cookie.parseClientHeader(null);
        });
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

    private void parseHeaderAndCheck(final String header, final ClientCookie... cookies) {
        assertEquals(Lists.of(cookies), ClientCookie.parseHeader(header), header);
    }

    // header ....................................................................................

    @Test
    public void testHeader() {
        assertEquals(HttpHeaderName.COOKIE, this.createCookie().header());
    }

    // toHeaderValue ....................................................................................

    @Test
    public void testToHeaderText() {
        this.toHeaderTextAndCheck("cookie123=value456;");
    }

    // toHeaderTextList ................................................................

    @Test
    public void testToHeaderTextListNullFails() {
        assertThrows(NullPointerException.class, () -> {
            ClientCookie.toHeaderTextList(null);
        });
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
        assertEquals(toString,
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
    public ClientCookie createHeaderValue() {
        return this.createCookie();
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
