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
import walkingkooka.InvalidCharacterException;
import walkingkooka.collect.list.Lists;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.text.CharSequences;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class ETagTest extends HeaderTestCase<ETag> {

    // with ............................................................................................................

    @Test
    public void testStrongNullValueFails() {
        assertThrows(
            NullPointerException.class,
            () -> ETag.strong(null)
        );
    }

    @Test
    public void testStrongInvalidValueFails() {
        assertThrows(
            InvalidCharacterException.class,
            () -> ETag.strong("abc def")
        );
    }

    @Test
    public void testWeakNullValueFails() {
        assertThrows(
            NullPointerException.class,
            () -> ETag.weak(null)
        );
    }

    @Test
    public void testWeakInvalidValueFails() {
        assertThrows(
            InvalidCharacterException.class,
            () -> ETag.weak("abc def")
        );
    }

    // text.............................................................................................................

    @Test
    public void testTextString() {
        this.textAndCheck(
            ETag.strong("abc123"),
            "\"abc123\""
        );
    }

    @Test
    public void testTextWeak() {
        this.textAndCheck(
            ETag.weak("abc123"),
            "W/\"abc123\""
        );
    }

    @Test
    public void testIsWildcard() {
        this.isWildcardAndCheck(false);
    }

    // toString ........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
            ETag.strong("abc123"),
            "\"abc123\""
        );
    }

    @Test
    public void testToStringWeak() {
        this.toStringAndCheck(
            ETag.weak("abc123"),
            "W/\"abc123\""
        );
    }

    @Override
    public void testCheckToStringOverridden() {
        throw new UnsupportedOperationException();
    }

    // toHeaderTextList.................................................................................................

    @Test
    public void testTextListOneStrong() {
        this.textListAndCheck(
            "\"abc123\"",
            ETag.strong("abc123")
        );
    }

    @Test
    public void testTextListOneWeak() {
        this.textListAndCheck(
            "W/\"abc123\"",
            ETag.weak("abc123")
        );
    }

    @Test
    public void testTextListOneWildcard() {
        this.textListAndCheck("*",
            ETag.wildcard());
    }

    @Test
    public void testTextListSeveral() {
        this.textListAndCheck(
            "\"1\", \"2\"",
            ETag.strong("1"),
            ETag.strong("2")
        );
    }

    @Test
    public void testTextListSeveral2() {
        this.textListAndCheck(
            "\"11\", \"22\"",
            ETag.strong("11"),
            ETag.strong("22")
        );
    }

    @Test
    public void testTextListSeveral3() {
        this.textListAndCheck(
            "W/\"11\", \"22\"",
            ETag.weak("11"),
            ETag.strong("22")
        );
    }

    private void textListAndCheck(final String toString, final ETag... tags) {
        this.checkEquals(toString,
            Header.toHeaderTextList(Lists.of(tags), Header.SEPARATOR.string().concat(" ")),
            "ETag.toString(List) failed =" + CharSequences.quote(toString));
    }

    @Override
    public ETag createHeader() {
        return ETag.weak("A");
    }

    @Override
    public ETag createDifferentHeader() {
        return ETag.weak("Different");
    }

    @Override
    public boolean isMultipart() {
        return false;
    }

    @Override
    public boolean isRequest() {
        return true;
    }

    @Override
    public boolean isResponse() {
        return true;
    }

    @Override
    public Class<ETag> type() {
        return ETag.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
