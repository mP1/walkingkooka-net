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
import walkingkooka.InvalidCharacterException;
import walkingkooka.ToStringTesting;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.text.CharSequences;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

final public class HttpStatusTest implements ClassTesting2<HttpStatus>,
    HashCodeEqualsDefinedTesting2<HttpStatus>,
    ToStringTesting<HttpStatus> {

    // firstLineOfText..................................................................................................

    @Test
    public void testFirstLineOfTextNullFails() {
        assertThrows(NullPointerException.class, () -> HttpStatus.firstLineOfText(null));
    }

    @Test
    public void testFirstLineOfTextEmptyString() {
        this.firstLineOfTextAndCheck("");
    }

    @Test
    public void testFirstLineOfTextWithoutLineEnding() {
        this.firstLineOfTextAndCheck("abc123");
    }

    @Test
    public void testFirstLineOfTextIncludesCr() {
        this.firstLineOfTextAndCheck("abc\r123", "abc");
    }

    @Test
    public void testFirstLineOfTextIncludesCrNl() {
        this.firstLineOfTextAndCheck("abc\r\n123", "abc");
    }

    @Test
    public void testFirstLineOfTextIncludesNl() {
        this.firstLineOfTextAndCheck("abc\n123", "abc");
    }

    private void firstLineOfTextAndCheck(final String text) {
        this.firstLineOfTextAndCheck(text, text);
    }

    private void firstLineOfTextAndCheck(final String text, final String first) {
        this.checkEquals(
            first,
            HttpStatus.firstLineOfText(text),
            () -> CharSequences.escape(text).toString()
        );
    }

    // constants

    private final static HttpStatusCode CODE = HttpStatusCode.OK;
    private final static String MESSAGE = "OK";

    @Test
    public void testWithMessagesIncludeCrFails() {
        assertThrows(InvalidCharacterException.class, () -> HttpStatus.with(HttpStatusCode.OK, "message\r123"));
    }

    @Test
    public void testWithMessagesIncludeNlFails() {
        assertThrows(InvalidCharacterException.class, () -> HttpStatus.with(HttpStatusCode.OK, "message\n123"));
    }

    @Test
    public void testWith() {
        this.check(this.status(), CODE, MESSAGE);
    }

    // setCode ................................................................

    @Test
    public void testSetCodeNullFails() {
        assertThrows(NullPointerException.class, () -> status().setCode(null));
    }

    @Test
    public void testSetCodeSame() {
        final HttpStatus status = this.status();
        assertSame(status, status.setCode(CODE));
    }

    @Test
    public void testSetCodeDifferent() {
        final HttpStatus status = this.status();
        final HttpStatusCode code = HttpStatusCode.BAD_REQUEST;
        final HttpStatus different = status.setCode(code);
        assertNotSame(status, different);
        this.check(different, code, MESSAGE);
    }

    // setMessage ................................................................

    @Test
    public void testSetMessageNullFails() {
        assertThrows(NullPointerException.class, () -> status().setMessage(null));
    }

    @Test
    public void testSetMessageEmptyFails() {
        assertThrows(IllegalArgumentException.class, () -> status().setMessage(""));
    }

    @Test
    public void testSetMessageSame() {
        final HttpStatus status = this.status();
        assertSame(status, status.setMessage(MESSAGE));
    }

    @Test
    public void testSetMessageDifferent() {
        final HttpStatus status = this.status();
        final String message = "different";
        final HttpStatus different = status.setMessage(message);
        assertNotSame(status, different);
        this.check(different, CODE, message);
    }

    @Test
    public void testEqualsNonConstantStatusCode() {
        final int code = 999;
        this.checkEquals(HttpStatus.with(HttpStatusCode.withCode(code), MESSAGE), HttpStatus.with(HttpStatusCode.withCode(code), MESSAGE));
    }

    @Test
    public void testEqualsDifferentCode() {
        this.checkNotEquals(HttpStatus.with(HttpStatusCode.BAD_GATEWAY, MESSAGE));
    }

    @Test
    public void testEqualsFoundAndMovedTemporarily() {
        final HttpStatusCode found = HttpStatusCode.FOUND;
        final HttpStatusCode movedTemp = HttpStatusCode.MOVED_TEMPORARILY;
        this.checkEquals(found.code(), movedTemp.code(), "code");
        this.checkNotEquals(found, movedTemp, "HttpStatusCode");

        this.checkEquals(found.setMessage(MESSAGE), movedTemp.setMessage(MESSAGE));
    }

    @Test
    public void testEqualsDifferentMessage() {
        this.checkNotEquals(HttpStatus.with(CODE, "different"));
    }

    // helpers ..................................

    private HttpStatus status() {
        return HttpStatus.with(CODE, MESSAGE);
    }

    private void check(final HttpStatus status, final HttpStatusCode code, final String message) {
        this.checkEquals(code, status.value(), "value");
        this.checkEquals(message, status.message(), "message");
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(this.status(), "200 OK");
    }

    @Override
    public Class<HttpStatus> type() {
        return HttpStatus.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }

    @Override
    public HttpStatus createObject() {
        return this.status();
    }
}
