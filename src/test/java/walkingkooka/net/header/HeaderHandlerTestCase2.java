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
import walkingkooka.ToStringTesting;
import walkingkooka.naming.Name;
import walkingkooka.test.ParseStringTesting;
import walkingkooka.text.CharSequences;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

public abstract class HeaderHandlerTestCase2<C extends HeaderHandler<T>, T> extends HeaderHandlerTestCase<C>
    implements ParseStringTesting<T>,
    ToStringTesting<C> {

    HeaderHandlerTestCase2() {
        super();
    }

    @Test
    public void testInvalidHeaderFails() {
        assertThrows(HeaderException.class, () -> this.handler().parse(this.invalidHeader()));
    }

    abstract String invalidHeader();

    @Test
    public void testCheckNullFails() {
        assertThrows(NullPointerException.class, () -> this.check(null));
    }

    // Invalid value type got walkingkooka.net.header.AbsoluteUrlHeaderHandlerTest@29a1505c required AbsoluteUrl
    @Test
    public void testCheckWrongTypeFails() {
        this.checkTypeFails(
            this,
            "Invalid value type got " + this + " required " + this.valueType()
        );
    }

    private void checkTypeFails(final Object value,
                                final String message) {
        final Exception expected = assertThrows(
            Exception.class,
            () -> this.check(value)
        );
        this.checkEquals(
            message,
            expected.getMessage(),
            "message"
        );
    }

    @Test
    public void testCheck() {
        this.check(this.value());
    }

    @Test
    public final void testHttpHeaderNameCast() {
        final C handler = this.handler();

        final HttpHeaderName<?> header = HttpHeaderName.with("X-custom");
        if (this.value() instanceof String) {
            assertSame(header, handler.httpHeaderNameCast(header));
        } else {
            try {
                handler.httpHeaderNameCast(header);
                fail("httpHeaderNameCast() should have failed");
            } catch (final HttpHeaderNameTypeParameterHeaderException expected) {
            }
        }
    }

    @Test
    public final void testToString() {
        this.toStringAndCheck(this.handler(), this.handlerToString());
    }

    abstract String handlerToString();

    abstract C handler();

    abstract Name name();

    @Override
    public final void testParseStringEmptyFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public final T parseString(final String text) {
        return this.handler()
            .parse(
                text
            );
    }

    @Override
    public final Class<? extends RuntimeException> parseStringFailedExpected(final Class<? extends RuntimeException> thrown) {
        return thrown;
    }

    @Override
    public final RuntimeException parseStringFailedExpected(final RuntimeException thrown) {
        return thrown;
    }

    final void parseAndToTextAndCheck(final String text, final T value) {
        this.parseStringAndCheck(text, value);
        this.toTextAndCheck(value, text);
    }

    final void check(final Object value) {
        this.handler().check(value);
    }

    final void toTextAndCheck(final T value, final String expected) {
        this.toTextAndCheck(value, this.name(), expected);
    }

    final void toTextAndCheck(final T value, final Name name, final String expected) {
        this.toTextAndCheck(this.handler(), value, name, expected);
    }

    final void toTextAndCheck(final C handler, final T value, final String expected) {
        this.toTextAndCheck(handler, value, this.name(), expected);
    }

    final void toTextAndCheck(final C handler, final T value, final Name name, final String expected) {
        this.checkEquals(expected,
            handler.toText(value, name),
            () -> handler + " " + name + " of " + CharSequences.quoteIfChars(value));
    }

    abstract T value();

    /**
     * The value type as a {@link String} which will appear in {@link HeaderException} messages.
     */
    abstract String valueType();

    final String valueType(final Class<?> type) {
        return type.getSimpleName();
    }

    final String listValueType(final Class<?> type) {
        return "List of " + this.valueType(type);
    }
}
