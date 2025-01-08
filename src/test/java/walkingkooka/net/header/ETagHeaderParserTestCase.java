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

public abstract class ETagHeaderParserTestCase<P extends ETagHeaderParser>
    extends HeaderParserTestCase<P, ETag> {

    ETagHeaderParserTestCase() {
        super();
    }

    // parse ...........................................................................................

    @Test
    public final void testParameterSeparatorFails() {
        this.parseStringInvalidCharacterFails(";");
    }

    @Test
    public final void testKeyValueSeparatorFails() {
        this.parseStringInvalidCharacterFails("=");
    }

    @Test
    public void testSlashFails() {
        this.parseStringInvalidCharacterFails("/");
    }

    @Test
    public final void testCommentFails() {
        this.parseStringCommentFails("(comment-abc123)", 0);
    }

    @Test
    public final void testInvalidInitialFails2() {
        this.parseStringInvalidCharacterFails("w");
    }

    @Test
    public final void testInvalidInitialFails3() {
        this.parseStringInvalidCharacterFails("0");
    }

    @Test
    public final void testInvalidQuotedCharacterFails() {
        this.parseStringInvalidCharacterFails("\"abc\0\"", '\0');
    }

    @Test
    public final void testInvalidQuotedCharacterFails2() {
        this.parseStringInvalidCharacterFails("W/\"abc\0\"", '\0');
    }

    @Test
    public final void testWFails() {
        this.parseStringFails("W", ETagHeaderParser.incompleteWeakIndicator("W"));
    }

    @Test
    public final void testWeaknessWithoutQuotedValueFails() {
        this.parseStringMissingValueFails("W/");
    }

    @Test
    public final void testWeakInvalidFails() {
        this.parseStringInvalidCharacterFails("WA");
    }

    @Test
    public final void testWeakInvalidFails2() {
        this.parseStringInvalidCharacterFails("W0");
    }

    @Test
    public final void testBeginQuoteFails() {
        this.parseMissingClosingQuoteFails("\"");
    }

    @Test
    public final void testBeginQuoteFails2() {
        this.parseMissingClosingQuoteFails("\"A");
    }

    @Test
    public final void testBeginQuoteFails3() {
        this.parseMissingClosingQuoteFails("\"'");
    }

    @Test
    public final void testWildcard() {
        this.parseStringAndCheck("*", ETag.wildcard());
    }

    @Test
    public final void testValue() {
        this.parseStringAndCheck("\"a\"", "a");
    }

    @Test
    public final void testValue2() {
        this.parseStringAndCheck("\"0\"", "0");
    }

    @Test
    public final void testValue3() {
        this.parseStringAndCheck("\"0123456789ABCDEF\"", "0123456789ABCDEF");
    }

    @Test
    public final void testWeakValue() {
        this.parseStringAndCheck("W/\"a\"", "a", ETagValidator.WEAK);
    }

    @Test
    public final void testWeakValue2() {
        this.parseStringAndCheck("W/\"0\"", "0", ETagValidator.WEAK);
    }

    @Test
    public final void testWeakValue3() {
        this.parseStringAndCheck("W/\"0123456789ABCDEF\"", "0123456789ABCDEF", ETagValidator.WEAK);
    }

    @Test
    public final void testValueCommentFails() {
        this.parseStringCommentFails("*(comment-abc123)", 1);
    }

    final void parseStringAndCheck(final String text, final String value) {
        this.parseStringAndCheck(text, value, ETagValidator.STRONG);
    }

    final void parseStringAndCheck(final String text, final String value, final ETagValidator validator) {
        this.parseStringAndCheck(text, ETag.with(value, validator));
    }

    @Override final String valueLabel() {
        return ETagHeaderParser.VALUE;
    }
}
