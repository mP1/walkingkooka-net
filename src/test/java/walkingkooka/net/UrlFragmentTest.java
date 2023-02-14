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
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.InvalidCharacterException;
import walkingkooka.ToStringTesting;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class UrlFragmentTest implements ClassTesting<UrlFragment>,
        HashCodeEqualsDefinedTesting2<UrlFragment>,
        ToStringTesting<UrlFragment> {

    @Test
    public void testEmpty() {
        final UrlFragment empty = UrlFragment.EMPTY;
        this.checkEquals(
                "",
                empty.value()
        );
    }

    @Test
    public void testWithNullFails() {
        assertThrows(
                NullPointerException.class,
                () -> UrlFragment.with(null)
        );
    }

    @Test
    public void testWithNonAsciiCharFails() {
        final String value = "abc\u0100";

        final InvalidCharacterException thrown = assertThrows(
                InvalidCharacterException.class,
                () -> UrlFragment.with(value)
        );

        this.checkEquals(
                value,
                thrown.text(),
                "text"
        );

        this.checkEquals(
                3,
                thrown.position(),
                "position"
        );
    }

    @Test
    public void testWithNonAsciiCharFails2() {
        final String value = "abc\u1234";

        final InvalidCharacterException thrown = assertThrows(
                InvalidCharacterException.class,
                () -> UrlFragment.with(value)
        );

        this.checkEquals(
                value,
                thrown.text(),
                "text"
        );

        this.checkEquals(
                3,
                thrown.position(),
                "position"
        );
    }

    @Test
    public void testWithEmpty() {
        assertSame(
                UrlFragment.EMPTY,
                UrlFragment.with("")
        );
    }

    @Test
    public void testWith() {
        final String value = "abc123";

        final UrlFragment urlFragment = UrlFragment.with(value);
        this.checkEquals(
                value,
                urlFragment.value()
        );
    }

    @Test
    public void testDifferent() {
        this.checkNotEquals(
                UrlFragment.with("123"),
                UrlFragment.with("456")
        );
    }

    @Test
    public void testDifferentCase() {
        this.checkNotEquals(
                UrlFragment.with("abc123"),
                UrlFragment.with("ABC123")
        );
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(
                UrlFragment.with("abc123"),
                "abc123"
        );
    }

    // HashCodeEqualsDefinedTesting2...................................................................................

    @Override
    public UrlFragment createObject() {
        return UrlFragment.with("abc123");
    }

    // ClassTesting....................................................................................................

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }

    @Override
    public Class<UrlFragment> type() {
        return UrlFragment.class;
    }
}
