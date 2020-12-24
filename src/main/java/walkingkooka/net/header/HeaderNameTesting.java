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
import walkingkooka.text.CharSequences;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Mixing interface to assist testing of {@link HeaderName} implementations.
 */
public interface HeaderNameTesting<N extends HeaderName<?>, C extends Comparable<C>>
        extends NameTesting2<N, C> {

    // parameterValue...........................................................................................

    @Test
    default void testParseNullFails() {
        assertThrows(NullPointerException.class, () -> this.createName().parse(null));
    }

    default <V> void parseAndCheck(final HeaderName<V> name,
                                   final String header,
                                   final V value) {
        assertEquals(value,
                name.parse(header),
                name + "=" + CharSequences.quoteIfNecessary(header));
    }

    // toString.................................................................................

    @Test
    default void testToString() {
        final String nameText = this.nameText();
        this.toStringAndCheck(this.createName(nameText), nameText);
    }

    // checkValue...........................................................................................

    @Test
    default void testCheckNullFails() {
        assertThrows(NullPointerException.class, () -> this.check(null));
    }

    @Test
    default void testCheckInvalidTypeFails() {
        assertThrows(HeaderException.class, () -> this.check(this));
    }

    default void check(final Object header) {
        this.createName().check(header);
    }

    default void check(final HeaderName<?> name,
                       final Object header) {
        assertSame(header,
                name.check(header),
                name + " didnt return correct header=" + CharSequences.quoteIfChars(header));
    }

    default N createName() {
        return this.createName(this.nameText());
    }

    String nameText();
}
