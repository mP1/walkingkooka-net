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
import walkingkooka.CanBeEmptyTesting;
import walkingkooka.ToStringTesting;
import walkingkooka.compare.ComparableTesting2;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.test.ParseStringTesting;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class UrlFragmentTest implements ParseStringTesting<UrlFragment>,
        ClassTesting<UrlFragment>,
        ComparableTesting2<UrlFragment>,
        CanBeEmptyTesting,
        ToStringTesting<UrlFragment> {

    @Test
    public void testSlashConstant() {
        this.checkEquals(
                "/",
                UrlFragment.SLASH.value()
        );
    }

    // CanBeEmpty.......................................................................................................
    @Test
    public void testIsEmptyWhenEmpty() {
        this.isEmptyAndCheck(
                UrlFragment.EMPTY,
                true
        );
    }

    @Test
    public void testIsEmptyWhenNotEmpty() {
        this.isEmptyAndCheck(
                UrlFragment.parse("not-empty"),
                false
        );
    }

    // parse............................................................................................................

    @Test
    public void testParseNullFails() {
        assertThrows(
                NullPointerException.class,
                () -> UrlFragment.parse(null)
        );
    }

    @Override
    public void testParseStringEmptyFails() {
        throw new UnsupportedOperationException();
    }

    @Test
    public void testParseEmpty() {
        assertSame(
                UrlFragment.EMPTY,
                UrlFragment.parse("")
        );
    }

    @Test
    public void testParse() {
        final String value = "abc123";

        final UrlFragment urlFragment = UrlFragment.parse(value);
        this.checkEquals(
                value,
                urlFragment.value()
        );
        this.toStringAndCheck(
                urlFragment,
                value
        );
    }

    @Test
    public void testParsePlus() {
        final String value = "space+";

        final UrlFragment urlFragment = UrlFragment.parse(value);
        this.checkEquals(
                "space+",
                urlFragment.value()
        );
        this.toStringAndCheck(
                urlFragment,
                value
        );
    }

    @Test
    public void testParsePercent20() {
        final UrlFragment urlFragment = UrlFragment.parse("space%20");
        this.checkEquals(
                "space ",
                urlFragment.value()
        );
        this.toStringAndCheck(
                urlFragment,
                "space%20"
        );
    }

    @Override
    public UrlFragment parseString(final String fragment) {
        return UrlFragment.parse(fragment);
    }

    @Override
    public Class<? extends RuntimeException> parseStringFailedExpected(final Class<? extends RuntimeException> thrown) {
        return thrown;
    }

    @Override
    public RuntimeException parseStringFailedExpected(final RuntimeException thrown) {
        return thrown;
    }

    // with............................................................................................................

    @Test
    public void testWithNullFails() {
        assertThrows(
                NullPointerException.class,
                () -> UrlFragment.with(null)
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

    // append...........................................................................................................

    @Test
    public void testAppendNullFails() {
        assertThrows(
                NullPointerException.class,
                () -> UrlFragment.EMPTY.append(null)
        );
    }

    @Test
    public void testAppendEmpty() {
        final UrlFragment fragment = UrlFragment.with("abc");
        assertSame(
                fragment,
                fragment.append(UrlFragment.EMPTY)
        );
    }

    @Test
    public void testAppendEmptyAppend() {
        final UrlFragment fragment = UrlFragment.with("abc");
        assertSame(
                fragment,
                UrlFragment.EMPTY.append(fragment)
        );
    }

    @Test
    public void testAppend() {
        checkEquals(
                UrlFragment.with("abc123"),
                UrlFragment.with("abc")
                        .append(UrlFragment.with("123"))
        );
    }

    // appendSlashThen..................................................................................................

    @Test
    public void testAppendSlashThenNullFails() {
        assertThrows(
                NullPointerException.class,
                () -> UrlFragment.EMPTY.appendSlashThen(null)
        );
    }

    @Test
    public void testAppendSlashThenEmpty() {
        final UrlFragment fragment = UrlFragment.with("abc");
        assertSame(
                fragment,
                fragment.appendSlashThen(UrlFragment.EMPTY)
        );
    }

    @Test
    public void testAppendSlashThenEmptyAppendSlashThen() {
        final UrlFragment fragment = UrlFragment.with("abc");
        assertSame(
                fragment,
                UrlFragment.EMPTY.appendSlashThen(fragment)
        );
    }

    @Test
    public void testAppendSlashThen() {
        this.appendSlashThenAndCheck(
                "abc",
                "123",
                "abc/123"
        );
    }

    @Test
    public void testAppendSlashThen2() {
        this.appendSlashThenAndCheck(
                "abc",
                "123/456",
                "abc/123/456"
        );
    }

    private void appendSlashThenAndCheck(final String fragment,
                                         final String append,
                                         final String expected) {
        this.appendSlashThenAndCheck(
                UrlFragment.parse(fragment),
                UrlFragment.parse(append),
                UrlFragment.parse(expected)
        );
    }

    private void appendSlashThenAndCheck(final UrlFragment fragment,
                                         final UrlFragment append,
                                         final UrlFragment expected) {
        this.checkEquals(
                expected,
                fragment.appendSlashThen(append),
                () -> fragment + " append " + append
        );
    }

    // equals..........................................................................................................

    @Test
    public void testEqualsDifferent() {
        this.checkNotEquals(
                UrlFragment.with("123"),
                UrlFragment.with("456")
        );
    }

    @Test
    public void testEqualsDifferentCase() {
        this.checkNotEquals(
                UrlFragment.with("abc123"),
                UrlFragment.with("ABC123")
        );
    }

    @Override
    public UrlFragment createObject() {
        return UrlFragment.with("abc123");
    }

    // Comparable.......................................................................................................

    @Test
    public void testCompareToEmptyAndNotEmpty() {
        this.compareToAndCheckLess(
                UrlFragment.EMPTY,
                UrlFragment.parse("/abc")
        );
    }

    @Test
    public void testCompareToLessCaseSensitive() {
        this.compareToAndCheckLess(
                UrlFragment.parse("/XYZ"),
                UrlFragment.parse("/abc")
        );
    }

    @Override
    public UrlFragment createComparable() {
        return UrlFragment.with("abc123");
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck2("abc123");
    }

    @Test
    public void testToStringColon() {
        this.toStringAndCheck2(":abc:123");
    }

    @Test
    public void testToStringSlash() {
        this.toStringAndCheck2("/abc/123");
    }

    @Test
    public void testToStringQuestionMark() {
        this.toStringAndCheck2("abc?123");
    }

    @Test
    public void testToStringLeftRightBracket() {
        this.toStringAndCheck2("[abc]");
    }

    @Test
    public void testToStringAtSign() {
        this.toStringAndCheck2("email@example.com");
    }

    private void toStringAndCheck2(final String verbatim) {
        this.toStringAndCheck(
                UrlFragment.with(verbatim),
                verbatim
        );
    }

    @Test
    public void testToStringSpace() {
        this.toStringAndCheck(
                UrlFragment.with("space "),
                "space%20"
        );
    }

    @Test
    public void testToStringSpecials() {
        final String text = "+!$&'()*,;=:@/?[]";

        this.toStringAndCheck(
                UrlFragment.with(text),
                text
        );
    }

    @Test
    public void testToStringAndUriCreate() {
        final char[] c = new char[256];
        for (int i = 0; i < c.length; i++) {
            c[i] = (char) i;
        }

        URI.create(
                "http://example/path#" +
                        UrlFragment.with(
                                new String(c)
                        ).toString()
        );
    }

    // roundtrip........................................................................................................

    @Test
    public void testParseAndToStringRoundtrip() {
        this.parseAndToStringRoundtripAndCheck("abc123");
    }

    @Test
    public void testParseAndToStringRoundtripSlash() {
        this.parseAndToStringRoundtripAndCheck("abc/123");
    }

    @Test
    public void testParseAndToStringRoundtripSpace() {
        this.parseAndToStringRoundtripAndCheck("abc 123");
    }

    @Test
    public void testParseAndToStringRoundtripPlus() {
        this.parseAndToStringRoundtripAndCheck("abc+123");
    }

    @Test
    public void testParseAndToStringRoundtripMathExpression() {
        this.parseAndToStringRoundtripAndCheck("=1+2*3/4");
    }

    @Test
    public void testParseAndToStringRoundtripAllAsciiCharacters() {
        final char[] c = new char[256];
        for (int i = 0; i < c.length; i++) {
            c[i] = (char) i;
        }

        this.parseAndToStringRoundtripAndCheck(
                new String(c)
        );
    }

    private void parseAndToStringRoundtripAndCheck(final String text) {
        final UrlFragment urlFragment = UrlFragment.with(text);

        this.parseStringAndCheck(
                urlFragment.toString(),
                urlFragment
        );
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
