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

public final class QuotedStringHeaderHandlerTest extends StringHeaderHandlerTestCase<QuotedStringHeaderHandler> {

    @Test
    public void testParseControlCharacterFails() {
        this.parseStringFails(
            "a\\0",
            new HeaderException("Invalid character 'a' at 0")
        );
    }

    @Test
    public void testParseNonAsciiFails() {
        this.parseStringFails(
            "a\u0080",
            new HeaderException("Invalid character 'a' at 0")
        );
    }

    @Test
    public void testParseMissingOpeningDoubleQuoteFails() {
        this.parseStringFails(
            "abc\"",
            new HeaderException("Invalid character 'a' at 0")
        );
    }

    @Test
    public void testParseUnsupportedBackslashFails() {
        this.parseStringFails(
            "a\\bc",
            new HeaderException("Invalid character 'a' at 0")
        );
    }

    @Test
    public void testParse() {
        this.parseStringAndCheck("\"abc\"", "abc");
    }

    @Test
    public void testParseWithBackslashSupportedEscapedDoubleQuote() {
        this.parseStringAndCheck(
            (text) -> this.handlerSupportingBackslashes()
                .parse(
                    text
                ),
            "\"a\\\"bc\"",
            "a\"bc"
        );
    }

    @Test
    public void testParseWithBackslashSupportedEscapedBackslash() {
        this.parseStringAndCheck(
            (text) -> this.handlerSupportingBackslashes()
                .parse(
                    text
                ),
            "\"a\\\\bc\"",
            "a\\bc"
        );
    }

    @Test
    public void testToText() {
        this.toTextAndCheck("abc", "\"abc\"");
    }

    @Test
    public void testToTextWithBackslashSupported() {
        this.toTextAndCheck(this.handlerSupportingBackslashes(),
            "abc",
            "\"abc\"");
    }

    @Test
    public void testToTextWithBackslashSupportedDoubleQuote() {
        this.toTextAndCheck(this.handlerSupportingBackslashes(),
            "a\"bc",
            "\"a\\\"bc\"");
    }

    @Test
    public void testToTextWithBackslashSupportedBackslash() {
        this.toTextAndCheck(this.handlerSupportingBackslashes(),
            "a\\bc",
            "\"a\\\\bc\"");
    }

    @Test
    public void testRoundtrip() {
        this.parseAndToTextAndCheck("\"abc\"", "abc");
    }

    @Test
    public void testRoundtripWithBackslash() {
        final QuotedStringHeaderHandler handler = this.handlerSupportingBackslashes();
        final String text = "\"a\\\"bc\"";
        final String value = "a\"bc";

        this.parseStringAndCheck(
            (t) -> handler.parse(
                t
            ),
            text,
            value
        );
        this.toTextAndCheck(handler, value, text);
    }

    @Override
    public String typeNamePrefix() {
        return "QuotedString";
    }

    @Override
    String invalidHeader() {
        return "123";
    }

    @Override
    protected QuotedStringHeaderHandler handler() {
        return QuotedStringHeaderHandler.with(this.charPredicate(), false);
    }

    private QuotedStringHeaderHandler handlerSupportingBackslashes() {
        return QuotedStringHeaderHandler.with(this.charPredicate(), true);
    }

    @Override String handlerToString() {
        return this.charPredicate().toString();
    }

    @Override
    public Class<QuotedStringHeaderHandler> type() {
        return QuotedStringHeaderHandler.class;
    }
}
