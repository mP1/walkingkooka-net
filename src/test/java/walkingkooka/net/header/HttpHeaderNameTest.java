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
import walkingkooka.Cast;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.map.Maps;
import walkingkooka.collect.set.Sets;
import walkingkooka.naming.NameTesting2;
import walkingkooka.net.http.HasHeaders;
import walkingkooka.net.http.server.FakeHttpRequest;
import walkingkooka.reflect.ConstantsTesting;
import walkingkooka.reflect.FieldAttributes;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.text.CharSequences;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

final public class HttpHeaderNameTest extends HeaderName2TestCase<HttpHeaderName<?>, HttpHeaderName<?>>
        implements ConstantsTesting<HttpHeaderName<?>>,
        NameTesting2<HttpHeaderName<?>, HttpHeaderName<?>> {

    @Test
    public void testIsConditional() {
        this.checkEquals(Sets.of(HttpHeaderName.E_TAG, HttpHeaderName.IF_MATCH, HttpHeaderName.IF_NONE_MATCHED, HttpHeaderName.IF_MODIFIED_SINCE, HttpHeaderName.IF_UNMODIFIED_SINCE, HttpHeaderName.IF_RANGE),
                HttpHeaderName.CONSTANTS.values()
                        .stream()
                        .filter(HttpHeaderName::isConditional)
                        .collect(Collectors.toCollection(Sets::sorted)));
    }

    @Test
    public void testCustomHeaderIsConditiojnal() {
        final HttpHeaderName<?> header = HttpHeaderName.with("X-custom");
        this.checkEquals(false, header.isConditional(), header + ".isConditional");
    }

    @Test
    public void testCustomHeaderIsContent() {
        final HttpHeaderName<?> header = HttpHeaderName.with("X-custom");
        this.checkEquals(false, header.isContent(), header + ".isContent");
    }

    @Test
    public void testScopeRequest() {
        this.checkScope(HttpHeaderName.ACCEPT, HttpHeaderScope.REQUEST);
    }

    @Test
    public void testScopeResponse() {
        this.checkScope(HttpHeaderName.SERVER, HttpHeaderScope.RESPONSE);
    }

    @Test
    public void testScopeRequestResponse() {
        this.checkScope(HttpHeaderName.CONTENT_LENGTH,
                HttpHeaderScope.REQUEST,
                HttpHeaderScope.RESPONSE);
    }

    @Test
    public void testScopeRequestUnknown() {
        this.checkScope(HttpHeaderName.with("xyz"),
                HttpHeaderScope.MULTIPART,
                HttpHeaderScope.REQUEST,
                HttpHeaderScope.RESPONSE);
    }

    private void checkScope(final HttpHeaderName<?> header, final HttpHeaderScope... scopes) {
        final Set<HttpHeaderScope> scopesSet = Sets.of(scopes);

        this.checkEquals(scopesSet.contains(HttpHeaderScope.MULTIPART),
                header.isMultipart(),
                header + " isMultipart");
        this.checkEquals(scopesSet.contains(HttpHeaderScope.REQUEST),
                header.isRequest(),
                header + " isRequest");
        this.checkEquals(scopesSet.contains(HttpHeaderScope.RESPONSE),
                header.isResponse(),
                header + " isResponse");
    }

    @Test
    public void testConstantNameReturnsConstant() {
        assertSame(HttpHeaderName.ACCEPT, HttpHeaderName.with(HttpHeaderName.ACCEPT.value()));
    }

    @Test
    public void testConstantNameReturnsConstantIgnoresCase() {
        assertSame(HttpHeaderName.ACCEPT, HttpHeaderName.with("ACCept"));
    }

    @Test
    public void testConstantsCached() {
        this.checkEquals(Lists.empty(),
                Arrays.stream(HttpHeaderName.class.getDeclaredFields())
                        .filter(FieldAttributes.STATIC::is)
                        .filter(f -> f.getType() == HeaderParameterName.class)
                        .filter(HttpHeaderNameTest::constantNotCached)
                        .collect(Collectors.toList()),
                "");
    }

    private static boolean constantNotCached(final Field field) {
        try {
            final HttpHeaderName<?> name = Cast.to(field.get(null));
            return name != HttpHeaderName.with(name.value());
        } catch (final Exception cause) {
            throw new AssertionError(cause.getMessage(), cause);
        }
    }

    @Test
    public void testContentConstants() {
        final List<HttpHeaderName<?>> headers = HttpHeaderName.CONSTANTS.values()
                .stream()
                .filter(h -> h.value().startsWith("content-"))
                .filter(h -> false == h.isContent())
                .collect(Collectors.toList());
        this.checkEquals(Lists.empty(),
                headers,
                "Several HttpHeaderName.isContent() returns false when it should return true");
    }

    @Test
    public void testAcceptConstantsRequest() {
        this.constantScopeCheck("accept-",
                "accept-ranges",
                HttpHeaderName::isRequest,
                true,
                "isRequest");
    }

    @Test
    public void testAcceptConstantsResponse() {
        this.constantScopeCheck("accept-",
                "accept-ranges",
                HttpHeaderName::isResponse,
                false,
                "isResponse");
    }

    @Test
    public void testContentConstantsRequest() {
        this.constantScopeCheck("content-",
                "",
                HttpHeaderName::isRequest,
                true,
                "isRequest");
    }

    @Test
    public void testContentConstantsResponse() {
        this.constantScopeCheck("content-",
                "",
                HttpHeaderName::isResponse,
                true,
                "isResponse");
    }

    private void constantScopeCheck(final String prefix,
                                    final String ignorePrefix,
                                    final Predicate<HttpHeaderName<?>> test,
                                    final boolean value,
                                    final String method) {
        final List<HttpHeaderName<?>> headers = HttpHeaderName.CONSTANTS.values()
                .stream()
                .filter(h -> CaseSensitivity.INSENSITIVE.startsWith(h.value(), prefix))
                .filter(h -> !CaseSensitivity.INSENSITIVE.startsWith(h.value(), ignorePrefix))
                .filter(h -> value != test.test(h))
                .collect(Collectors.toList());
        this.checkEquals(Lists.empty(),
                headers,
                "Several HttpHeaderName." + method +
                        " starting with " + prefix +
                        " returns " + !value +
                        " when it should return " + value);
    }

    // stringValues.........................................................................

    @Test
    public void testStringValuesStringHeader() {
        assertSame(HttpHeaderName.SERVER, HttpHeaderName.SERVER.stringValues());
    }

    @Test
    public void testStringValuesCustomHeader() {
        final HttpHeaderName<?> custom = HttpHeaderName.with("x-custom");
        assertSame(custom, custom.stringValues());
    }

    @Test
    public void testStringValuesNonStringHeaderFails() {
        assertThrows(HttpHeaderNameTypeParameterHeaderException.class, HttpHeaderName.CONTENT_LENGTH::stringValues);
    }

    // header.........................................................................

    @Test
    public void testHeaderNullFails() {
        assertThrows(NullPointerException.class, () -> HttpHeaderName.ALLOW.header(null));
    }


    @Test
    public void testHeaderCustomHeaderIncludesDoubleQuotesSingleQuotesComments() {
        this.headerAndCheck(HttpHeaderName.with("custom-x").stringValues(),
                "abc \"def\" 'ghi' (comment-123)");
    }

    @Test
    public void testHeaderScopeAccept() {
        this.headerAndCheck(HttpHeaderName.ACCEPT,
                Accept.parse("text/html, application/xhtml+xml"));
    }

    @Test
    public void testHeaderScopeContentLength() {
        this.headerAndCheck(HttpHeaderName.CONTENT_LENGTH,
                123L);
    }

    @Test
    public void testHeaderScopeResponseContentLengthAbsent() {
        this.headerAndCheck(HttpHeaderName.CONTENT_LENGTH,
                null);
    }

    @Test
    public void testHeaderScopeUnknown() {
        this.headerAndCheck(Cast.to(HttpHeaderName.with("xyz")),
                "xyz");
    }

    private <T> void headerAndCheck(final HttpHeaderName<T> headerName,
                                         final T header) {
        this.checkEquals(Optional.ofNullable(header),
                headerName.header(this.headers(headerName, header)),
                headerName + "=" + header);
    }

    // headerOrFail..............................................................................

    @Test
    public void testHeaderOrFailNullFails() {
        assertThrows(NullPointerException.class, () -> HttpHeaderName.ALLOW.headerOrFail(null));
    }

    @Test
    public void testHeaderOrFailAbsent() {
        assertThrows(HeaderException.class, () -> HttpHeaderName.ALLOW.headerOrFail(this.headers(HttpHeaderName.CONTENT_LENGTH, 123L)));
    }

    @Test
    public void testHeaderOrFail() {
        this.headerOrFailAndCheck(HttpHeaderName.CONTENT_LENGTH, 123L);
    }

    private <T> void headerOrFailAndCheck(final HttpHeaderName<T> headerName,
                                               final T header) {
        this.checkEquals(header,
                headerName.headerOrFail(this.headers(headerName, header)),
                headerName + "=" + header);
    }

    private <T> HasHeaders headers(final HttpHeaderName<T> name, final T value) {
        return new HasHeaders() {
            @Override
            public Map<HttpHeaderName<?>, List<?>> headers() {
                return Maps.of(name, list(value));
            }
        };
    }

    // checkValue...... ...............................................................................................

    @Test
    public void testCheckNullFails() {
        assertThrows(NullPointerException.class, () -> HttpHeaderName.ACCEPT.check(null));
    }

    @Test
    public void testCheck() {
        HttpHeaderName.CONTENT_LENGTH.check(123L);
    }

    @Test
    public void testCheckList() {
        final HttpHeaderName<?> header = HttpHeaderName.COOKIE;
        header.check(Cast.to(Cookie.parseClientHeader("cookie1=value2")));
    }

    @Test
    public void testCheckInvalidValueTypeFails() {
        final HttpHeaderName<?> header = HttpHeaderName.CONTENT_LENGTH;
        assertThrows(HeaderException.class, () -> header.check(Cast.to("invalid!")));
    }

    // parse ...............................................................................................

    @Test
    public void testParseAcceptString() {
        this.parseAndCheck(HttpHeaderName.ACCEPT,
                "text/html, application/xhtml+xml",
                Accept.with(list(
                        MediaType.with("text", "html"),
                        MediaType.with("application", "xhtml+xml"))));
    }

    @Test
    public void testParseAccessControlMaxAge() {
        this.parseAndCheck(HttpHeaderName.ACCESS_CONTROL_MAX_AGE,
                "591",
                591L);
    }

    @Test
    public void testParseContentLengthString() {
        this.parseAndCheck(HttpHeaderName.CONTENT_LENGTH,
                "123",
                123L);
    }

    @Test
    public void testParseIfRangeETag() {
        this.parseAndCheck(HttpHeaderName.IF_RANGE,
                "W/\"etag-1234567890\"",
                IfRange.with(ETagValidator.WEAK.setValue("etag-1234567890")));
    }

    @Test
    public void testParseIfRangeLastModified() {
        final LocalDateTime lastModified = LocalDateTime.of(2000, 12, 31, 6, 28, 29);

        this.parseAndCheck(HttpHeaderName.IF_RANGE,
                HeaderHandler.localDateTime().toText(lastModified, HttpHeaderName.LAST_MODIFIED),
                IfRange.with(lastModified));
    }

    // headerText.........................................................................

    @Test
    public void testHeaderTextNullFails() {
        assertThrows(NullPointerException.class, () -> HttpHeaderName.CONNECTION.headerText(null));
    }

    @Test
    public void testHeaderTextString() {
        final String text = "Close";
        this.headerTextAndCheck(HttpHeaderName.CONNECTION, text, text);
    }

    @Test
    public void testHeaderTextLong() {
        this.headerTextAndCheck(HttpHeaderName.CONTENT_LENGTH, 123L, "123");
    }

    private <T> void headerTextAndCheck(final HttpHeaderName<T> header,
                                        final T value,
                                        final String formatted) {
        this.checkEquals(formatted,
                header.headerText(value),
                () -> header + ".headerText " + CharSequences.quoteIfChars(value));
    }

    // HttpRequestAttribute.................................................................................

    @Test
    public void testParameterValueContentLength() {
        this.parameterValueAndCheck(HttpHeaderName.CONTENT_LENGTH, 123L);
    }

    @Test
    public void testParameterValueUserAgent() {
        this.parameterValueAndCheck(HttpHeaderName.USER_AGENT, "Browser 123");
    }

    @Test
    public void testParameterValueUserAgentMozilla() {
        this.parameterValueAndCheck(HttpHeaderName.USER_AGENT, "Mozilla/5.0 (iPad; U; CPU OS 3_2_1 like Mac OS X; en-us) AppleWebKit/531.21.10 (KHTML, like Gecko) Mobile/7B405");
    }

    @Test
    public void testParameterValueUserAgentGooglebot() {
        this.parameterValueAndCheck(HttpHeaderName.USER_AGENT, "Googlebot/2.1 (+http://www.google.com/bot.html)");
    }

    @Test
    public void testParameterValueRequestAbsent() {
        this.parameterValueAndCheck(HttpHeaderName.CONTENT_LENGTH, null);
    }

    private <T> void parameterValueAndCheck(final HttpHeaderName<T> header, final T value) {
        this.checkEquals(Optional.ofNullable(value),
                header.parameterValue(new FakeHttpRequest() {
                    @Override
                    public Map<HttpHeaderName<?>, List<?>> headers() {
                        return Maps.of(header, list(value));
                    }
                }));
    }

    @Test
    public void testParameterValueMap() {
        final HttpHeaderName<String> header = HttpHeaderName.USER_AGENT;
        final String value = "Browser123";

        this.checkEquals(Optional.ofNullable(value),
                header.parameterValue(Maps.of(header, value)));
    }

    private static <T> List<T> list(final T...values) {
        return Lists.of(values);
    }

    @Override
    public HttpHeaderName<Object> createName(final String name) {
        return Cast.to(HttpHeaderName.with(name));
    }

    @Override
    public String nameText() {
        return "X-Custom";
    }

    @Override
    public String differentNameText() {
        return "X-different";
    }

    @Override
    public String nameTextLess() {
        return HttpHeaderName.ACCEPT.value();
    }

    @Override
    public int minLength() {
        return 1;
    }

    @Override
    public int maxLength() {
        return Integer.MAX_VALUE;
    }

    @Override
    public String possibleValidChars(final int position) {
        return NameTesting2.subtract(ASCII_NON_CONTROL, ":. ");
    }

    @Override
    public String possibleInvalidChars(final int position) {
        return CONTROL + BYTE_NON_ASCII + ":." + WHITESPACE;
    }

    @Override
    public Class<HttpHeaderName<?>> type() {
        return Cast.to(HttpHeaderName.class);
    }

    @Override
    public Set<HttpHeaderName<?>> intentionalDuplicateConstants() {
        return Sets.empty();
    }
}
