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
import walkingkooka.naming.NameTesting2;
import walkingkooka.test.ParseStringTesting;
import walkingkooka.text.CharSequences;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Mixing interface to assist testing of {@link HeaderName} implementations.
 */
public interface HeaderNameTesting<N extends HeaderName<?>, C extends Comparable<C>>
    extends NameTesting2<N, C>,
    ParseStringTesting<Object> {

    // NameTesting......................................................................................................

    default N createName() {
        return this.createName(this.nameText());
    }

    @Override
    String nameText();

    // toString.........................................................................................................

    @Test
    @Override
    default void testToString() {
        final String nameText = this.nameText();
        this.toStringAndCheck(this.createName(nameText), nameText);
    }

    // checkValue.......................................................................................................

    @Test
    default void testCheckValueWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.checkValue(null)
        );
    }

    @Test
    default void testCheckValueWithInvalidTypeFails() {
        assertThrows(
            HeaderException.class,
            () -> this.checkValue(this)
        );
    }

    default void checkValue(final Object header) {
        this.createName().checkValue(header);
    }

    default void checkValue(final HeaderName<?> name,
                            final Object header) {
        assertSame(header,
            name.checkValue(header),
            name + " didnt return correct header=" + CharSequences.quoteIfChars(header));
    }
}
