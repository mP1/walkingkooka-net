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

package walkingkooka.net.http;

import org.junit.jupiter.api.Test;
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.ToStringTesting;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.map.Maps;
import walkingkooka.collect.set.Sets;
import walkingkooka.net.header.HttpHeaderName;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.ConstantsTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.reflect.TypeNameTesting;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class HttpStatusCodeTest implements ClassTesting2<HttpStatusCode>,
        ConstantsTesting<HttpStatusCode>,
        HashCodeEqualsDefinedTesting2<HttpStatusCode>,
        ToStringTesting<HttpStatusCode>,
        TypeNameTesting<HttpStatusCode> {

    private final static int CODE = 299;
    @Test
    public void testStandard() {
        assertSame(HttpStatusCode.OK, HttpStatusCode.withCode(200));
    }

    @Test
    public void testNonStandard() {
        final int value = 999;
        final HttpStatusCode code = HttpStatusCode.withCode(value);
        this.checkEquals(value, code.code());
        this.checkEquals("Status=999", code.message);
    }

    @Test
    public void testNonStandardNotCached() {
        final int value = 999;
        assertNotSame(HttpStatusCode.withCode(value), HttpStatusCode.withCode(value));
    }

    @Test
    public void testStatusDefaultMessageUnique() {
        final Map<String, HttpStatusCode> intToCode = Maps.ordered();
        for (final HttpStatusCode code : HttpStatusCode.CONSTANTS.values()) {
            assertNull(intToCode.put(code.message, code), "Default message is not unique " + code.message + "=" + code);
        }
    }

    @Test
    public void testStatus() {
        for (final HttpStatusCode code : HttpStatusCode.CONSTANTS.values()) {
            final HttpStatus status = code.status();
            assertSame(status, code.status(), "status not cached");
            assertSame(code, status.value(), "code");
            this.checkNotEquals("", status.message(), "message");
        }
    }

    @Test
    public void testPartialContentRangesHeader() {
        this.checkEquals(Sets.of(HttpHeaderName.RANGE), HttpStatusCode.PARTIAL_CONTENT.requiredHttpHeaders());
    }

    @Test
    public void testRedirectRequireLocationHeader() {
        this.checkEquals(Lists.empty(),
                HttpStatusCode.CONSTANTS.values()
                        .stream()
                        .filter(c -> HttpStatusCodeCategory.REDIRECTION == c.category())
                        .filter(c -> HttpStatusCode.MULTIPLE_CHOICES != c)
                        .filter(c -> false == c.requiredHttpHeaders().equals(Sets.of(HttpHeaderName.LOCATION)))
                        .map(c -> c + " " + c.requiredHttpHeaders())
                        .collect(Collectors.toList()));
    }

    @Test
    public void testCode() throws Exception {
        for (final HttpStatusCode constant : HttpStatusCode.CONSTANTS.values()) {
            final String message = constant.message.toUpperCase()
                    .replace(' ', '_');
            this.checkEquals(HttpServletResponse.class.getField("SC_" + message).getInt(null),
                    constant.code(),
                    () -> constant.toString());
        }
    }

    // category ....................................................................

    @Test
    public void testCategory1xx() {
        this.categoryAndCheck(HttpStatusCode.CONTINUE, HttpStatusCodeCategory.INFORMATION);
    }

    @Test
    public void testCategory2xx() {
        this.categoryAndCheck(HttpStatusCode.OK, HttpStatusCodeCategory.SUCCESSFUL);
    }

    @Test
    public void testCategory3xx() {
        this.categoryAndCheck(HttpStatusCode.MOVED_TEMPORARILY, HttpStatusCodeCategory.REDIRECTION);
    }

    @Test
    public void testCategory4xx() {
        this.categoryAndCheck(HttpStatusCode.BAD_REQUEST, HttpStatusCodeCategory.CLIENT_ERROR);
    }

    @Test
    public void testCategory5xx() {
        this.categoryAndCheck(HttpStatusCode.INTERNAL_SERVER_ERROR, HttpStatusCodeCategory.SERVER_ERROR);
    }

    private void categoryAndCheck(final HttpStatusCode code, final HttpStatusCodeCategory category) {
        assertSame(category, code.category(), "category for status code: " + code);
    }

    // setMessage..............................................................

    @Test
    public void testSetMessageNullFails() {
        assertThrows(NullPointerException.class, () -> HttpStatusCode.OK.setMessage(null));
    }

    @Test
    public void testSetMessageEmptyFails() {
        assertThrows(IllegalArgumentException.class, () -> HttpStatusCode.OK.setMessage(""));
    }

    @Test
    public void testSetMessageWhitespaceFails() {
        assertThrows(IllegalArgumentException.class, () -> HttpStatusCode.OK.setMessage("  "));
    }

    @Test
    public void testSetMessageDefault() {
        assertSame(HttpStatusCode.OK.status(),
                HttpStatusCode.OK.setMessage(HttpStatusCode.OK.message),
                "set message with default should return constant");
    }

    @Test
    public void testSetMessage() {
        final String message = "Message something something2";
        final HttpStatusCode code = HttpStatusCode.MOVED_TEMPORARILY;
        final HttpStatus status = code.setMessage(message);
        this.checkEquals(code, status.value(), "code");
        this.checkEquals(message, status.message(), "message");
    }

    // setMessageOrDefault..............................................................

    @Test
    public void testSetMessageOrDefaultNullDefaults() {
        this.setMessageOrDefaultAndCheck(HttpStatusCode.OK,
                null,
                HttpStatusCode.OK.status());
    }

    @Test
    public void testSetMessageOrDefaultEmptyDefaults() {
        this.setMessageOrDefaultAndCheck(HttpStatusCode.OK,
                "",
                HttpStatusCode.OK.status());
    }

    @Test
    public void testSetMessageOrDefaultWhitespaceDefaults() {
        this.setMessageOrDefaultAndCheck(HttpStatusCode.OK,
                "   ",
                HttpStatusCode.OK.status());
    }

    @Test
    public void testSetMessageOrDefault() {
        final String message = "message 123";
        this.setMessageOrDefaultAndCheck(HttpStatusCode.OK,
                message,
                HttpStatus.with(HttpStatusCode.OK, message));
    }

    @Test
    public void testSetMessageOrDefaultDefault() {
        assertSame(HttpStatusCode.OK.status(),
                HttpStatusCode.OK.setMessage(HttpStatusCode.OK.message),
                "set message with default should return constant");
    }

    private void setMessageOrDefaultAndCheck(final HttpStatusCode code,
                                             final String message,
                                             final HttpStatus status) {
        this.checkEquals(status, code.setMessageOrDefault(message));
    }

    // equals...........................................................................................................

    @Test
    public void testDifferentCode() {
        this.checkNotEquals(HttpStatusCode.withCode(1000 + CODE));
    }

    // ClassTesting....................................................................................................

    @Override
    public Class<HttpStatusCode> type() {
        return HttpStatusCode.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }

    // Constants........................................................................................................

    @Override
    public Set<HttpStatusCode> intentionalDuplicateConstants() {
        return Sets.empty();
    }

    // HashcodeEquals...................................................................................................

    @Override
    public HttpStatusCode createObject() {
        return HttpStatusCode.withCode(CODE);
    }

    // TypeNameTesting .........................................................................................

    @Override
    public String typeNamePrefix() {
        return HttpStatus.class.getSimpleName();
    }

    @Override
    public String typeNameSuffix() {
        return "";
    }
}
