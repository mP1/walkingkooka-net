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
import walkingkooka.InvalidTextLengthException;
import walkingkooka.naming.NameTesting;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.test.ParseStringTesting;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.text.CharSequences;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class UrlPathNameTest implements ClassTesting2<UrlPathName>,
    NameTesting<UrlPathName, UrlPathName>,
    ParseStringTesting<UrlPathName> {

    @Override
    public void testEmptyFails() {
        throw new UnsupportedOperationException();
    }

    @Test
    public void testWithEmpty() {
        assertSame(
            UrlPathName.ROOT,
            UrlPathName.with("")
        );
    }

    @Test
    public void testWithTooLongFails() {
        final char[] chars = new char[UrlPathName.MAXIMUM_LENGTH + 1];
        Arrays.fill(chars, 'x');

        assertThrows(
            InvalidTextLengthException.class,
            () -> UrlPathName.with(
                new String(chars)
            )
        );
    }

    @Test
    public void testWithSlashesFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> UrlPathName.with("dd/mm/yyyy")
        );
    }

    // Comparable.......................................................................................................

    @Test
    public void testCompareToArraySort() {
        final UrlPathName a1 = UrlPathName.with("A1");
        final UrlPathName b2 = UrlPathName.with("B2");
        final UrlPathName c3 = UrlPathName.with("c3");
        final UrlPathName d4 = UrlPathName.with("d4");

        this.compareToArraySortAndCheck(d4, a1, c3, b2,
            a1, b2, c3, d4);
    }

    @Test
    public void testWith2() {
        this.createNameAndCheck("abc");
    }

    @Test
    public void testWithPercentage() {
        this.createNameAndCheck("abc%20def");
    }

    @Test
    public void testEqualsDifferentName() {
        this.checkNotEquals(
            UrlPathName.with("different")
        );
    }

    @Test
    public void testCompareLess2() {
        this.compareToAndCheckLess(
            UrlPathName.with("zzzzz")
        );
    }

    @Test
    public void testCaseSignificant() {
        this.checkNotEquals(UrlPathName.with("ABC"));
    }

    @Test
    public void testToStringAddQuotesBecauseOfWhitespace() {
        final String string = "name with whitespace";
        final UrlPathName name = UrlPathName.with(string);
        this.checkEquals(CharSequences.quote(string).toString(), name.toString());
    }

    @Override
    public UrlPathName createName(final String name) {
        return UrlPathName.with(name);
    }

    @Override
    public CaseSensitivity caseSensitivity() {
        return CaseSensitivity.SENSITIVE;
    }

    @Override
    public String nameText() {
        return "path";
    }

    @Override
    public String differentNameText() {
        return "different";
    }

    @Override
    public String nameTextLess() {
        return "file";
    }

    // parse............................................................................................................

    @Override
    public void testParseStringEmptyFails() {
        throw new UnsupportedOperationException();
    }

    @Test
    public void testParseIncludesEncodedCharacter() {
        this.parseStringAndCheck(
            "%7Efoo",
            UrlPathName.with("~foo")
        );
    }

    @Override
    public UrlPathName parseString(final String text) {
        return UrlPathName.parse(text);
    }

    @Override
    public Class<? extends RuntimeException> parseStringFailedExpected(final Class<? extends RuntimeException> thrown) {
        return thrown;
    }

    @Override
    public RuntimeException parseStringFailedExpected(final RuntimeException thrown) {
        return thrown;
    }

    // class............................................................................................................

    @Override
    public Class<UrlPathName> type() {
        return UrlPathName.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
