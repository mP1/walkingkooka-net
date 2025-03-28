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
import walkingkooka.Cast;
import walkingkooka.predicate.character.CharPredicates;
import walkingkooka.text.CharSequences;

public final class HeaderParserWithParametersTest extends HeaderParserWithParametersTestCase<HeaderParserWithParameters<?, ?>,
    Void> {

    @Test
    public void testParseQuotedFails() {
        this.parseStringInvalidCharacterFails("\"quoted\"", '"');
    }

    @Test
    public void testParseParameterSeparatorFails() {
        this.parseStringMissingValueFails(";", 0);
    }

    @Test
    public void testParseValueSeparatorFails() {
        this.parseStringInvalidCharacterFails(",");
    }

    @Test
    public void testParseKeyValueSeparatorFails() {
        this.parseStringInvalidCharacterFails("=");
    }

    @Test
    public void testParseValue() {
        this.parseStringAndCheck("v", "[value-v]v");
    }

    @Test
    public void testParseWildcard() {
        this.parseStringAndCheck("*", "[wildcard]*");
    }

    @Test
    public void testParseSlash() {
        this.parseStringInvalidCharacterFails("/");
    }

    @Test
    public void testParseValueSpace() {
        this.parseStringAndCheck("v ", "[value-v]v");
    }

    @Test
    public void testParseValueTab() {
        this.parseStringAndCheck("v\t", "[value-v]v");
    }

    @Test
    public void testParseValueCrNlSpace() {
        this.parseStringAndCheck("v\r\n ", "[value-v]v");
    }

    @Test
    public void testParseValueCrNlTab() {
        this.parseStringAndCheck("v\r\n\t", "[value-v]v");
    }

    @Test
    public void testParseValueCrFails() {
        this.parseStringInvalidCharacterFails("v\r");
    }

    @Test
    public void testParseValueCrNlFails() {
        this.parseStringInvalidCharacterFails("v\r\n");
    }

    @Test
    public void testParseValueCrNlNonWhitespaceFails() {
        this.parseStringInvalidCharacterFails("v\r\n!");
    }

    @Test
    public void testParseValueParameterSeparator() {
        this.parseStringAndCheck("v;", "[value-v]v");
    }

    @Test
    public void testParseValueParameterSeparatorParameterSeparatorFails() {
        this.parseStringInvalidCharacterFails("v;;", 2);
    }

    @Test
    public void testParseValueParameterSeparatorParameterNameFails() {
        this.parseStringMissingParameterValueFails("v;p");
    }

    @Test
    public void testParseValueParameterSeparatorParameterInvalidCharacterFails() {
        this.parseStringInvalidCharacterFails("v;p\0");
    }

    @Test
    public void testParseValueParameterSeparatorParameterNameKeyValueSeparatorFails() {
        this.parseStringMissingParameterValueFails("v;p=");
    }

    @Test
    public void testParseValueParameterSeparatorParameterNameKeyValueSeparatorSpaceFails() {
        this.parseStringMissingParameterValueFails("v;p= ");
    }

    @Test
    public void testParseValueParameterSeparatorParameterNameKeyValueSeparatorTabFails() {
        this.parseStringMissingParameterValueFails("v;p=\t");
    }

    @Test
    public void testParseValueParameterSeparatorParameterNameKeyValueSeparatorCrFails() {
        this.parseStringInvalidCharacterFails("v;p=\r");
    }

    @Test
    public void testParseValueParameterSeparatorParameterNameKeyValueSeparatorCrNlFails() {
        this.parseStringInvalidCharacterFails("v;p=\r\n");
    }

    @Test
    public void testParseValueParameterSeparatorParameterNameKeyValueSeparatorCrNlNonSpaceFails() {
        this.parseStringInvalidCharacterFails("v;p=\r\nq");
    }

    @Test
    public void testParseValueParameterSeparatorParameterNameKeyValueSeparatorUnquotedParameterValue() {
        this.parseStringAndCheck("v;p=u", "[value-v][parameter-name-p][parameter-value-unquoted-u]v; p=u");
    }

    @Test
    public void testParseValueParameterSeparatorParameterNameKeyValueSeparatorKeyValueSeparatorUnquotedParameterValueFails() {
        final String text = "v;p==u";
        this.parseStringInvalidCharacterFails(text, text.indexOf('=') + 1);
    }

    @Test
    public void testParseValueParameterSeparatorParameterNameKeyValueSeparatorUnquotedParameterValueParameterSeparator() {
        this.parseStringAndCheck("v;p=u;", "[value-v][parameter-name-p][parameter-value-unquoted-u]v; p=u");
    }

    @Test
    public void testParseValueParameterSeparatorParameterNameKeyValueSeparatorUnquotedParameterValueValueSeparatorFails() {
        this.parseStringInvalidCharacterFails("v;p=u,");
    }

    @Test
    public void testParseValueParameterSeparatorParameterNameKeyValueSeparatorQuotedParameterValueNonAscii() {
        this.parseStringInvalidCharacterFails("v;p=\"\0\"", '\0');
    }

    @Test
    public void testParseValueParameterSeparatorParameterNameKeyValueSeparatorQuotedParameterValueNonAscii2() {
        this.parseStringInvalidCharacterFails("v;p=\"\u0080\"", '\u0080');
    }

    @Test
    public void testParseValueParameterSeparatorParameterNameKeyValueSeparatorQuotedParameterValueMissing() {
        this.parseMissingClosingQuoteFails("v;p=\"q");
    }

    @Test
    public void testParseValueParameterSeparatorParameterNameKeyValueSeparatorQuotedParameterValue() {
        this.parseStringAndCheck("v;p=\"q\"", "[value-v][parameter-name-p][parameter-value-quoted-q]v; p=q");
    }

    @Test
    public void testParseValueParameterSeparatorParameterNameKeyValueSeparatorQuotedParameterValueQuotedParameterValueFails() {
        final String text = "v;p=\"q\"\"r\"";
        this.parseStringInvalidCharacterFails(text, text.indexOf('r') - 1);
    }

    @Test
    public void testParseValueParameterSeparatorParameterNameKeyValueSeparatorParameterValueParameterSeparatorParameterNameKeyValueSeparatorParameterValue() {
        this.parseStringAndCheck("v;p=\"q\";r=\"s\"",
            "[value-v][parameter-name-p][parameter-value-quoted-q][parameter-name-r][parameter-value-quoted-s]v; p=q; r=s");
    }

    @Test
    public void testParseValueValueSeparatorValue() {
        this.parseStringAndCheck("v,w",
            "[value-v]v[value-w]w");
    }

    @Test
    public void testParseValueValueSeparatorValueValueSeparatorValue() {
        this.parseStringAndCheck("v,w,x",
            "[value-v]v[value-w]w[value-x]x");
    }

    @Test
    public void testParseValueParametersValueSeparatorValueValueSeparatorValue() {
        this.parseStringAndCheck("v;p=q,w,x",
            "[value-v][parameter-name-p][parameter-value-unquoted-q]v; p=q[value-w]w[value-x]x");
    }

    @Test
    public void testParseWildcardValueSeparatorValue() {
        this.parseStringAndCheck("*,v",
            "[wildcard]*[value-v]v");
    }

    @Test
    public void testParseWildcardValueSeparatorParameterNameKeyValueSeparatorParameterValue() {
        this.parseStringAndCheck("*;p=q",
            "[wildcard][parameter-name-p][parameter-value-unquoted-q]*; p=q");
    }

    private void parseStringAndCheck(final String text,
                                     final String events) {
        final StringBuilder recorded = new StringBuilder();

        new HeaderParserWithParameters<AcceptCharsetValue, AcceptCharsetValueParameterName<?>>(text) {

            @Override
            boolean allowMultipleValues() {
                return true;
            }

            @Override
            AcceptCharsetValue wildcardValue() {
                recorded.append("[wildcard]");
                this.position++;
                return AcceptCharsetValue.WILDCARD_VALUE;
            }

            @Override
            AcceptCharsetValue value() {
                final String text = this.token(CharPredicates.letterOrDigit());
                recorded.append("[value-").append(text).append("]");
                return AcceptCharsetValue.with(CharsetName.with(text));
            }

            @Override
            AcceptCharsetValueParameterName<?> parameterName() {
                final String text = this.token(CharPredicates.letterOrDigit());
                recorded.append("[parameter-name-").append(text).append("]");
                return AcceptCharsetValueParameterName.with(text);
            }

            @Override
            String quotedParameterValue(AcceptCharsetValueParameterName<?> parameterName) {
                final String text = this.quotedText(CharPredicates.letterOrDigit(), false);
                recorded.append("[parameter-value-quoted-").append(text, 1, text.length() - 1).append(']');
                return text;
            }

            @Override
            String unquotedParameterValue(AcceptCharsetValueParameterName<?> parameterName) {
                final String text = this.token(CharPredicates.letterOrDigit());
                recorded.append("[parameter-value-unquoted-").append(text).append(']');
                return text;
            }

            @Override
            void valueComplete(final AcceptCharsetValue value) {
                recorded.append(value.toString());
            }

            @Override
            void missingValue() {
                this.failMissingValue(VALUE_LABEL);
            }
        }.parse();

        this.checkEquals(events,
            recorded.toString(),
            "recorded events for " + CharSequences.quoteAndEscape(text));
    }

    @Override
    public Void parseString(final String text) {
        new HeaderParserWithParameters<AcceptCharsetValue, AcceptCharsetValueParameterName<?>>(text) {

            @Override
            boolean allowMultipleValues() {
                return false;
            }

            @Override
            AcceptCharsetValue wildcardValue() {
                return this.token(CharPredicates.letterOrDigit(), s -> AcceptCharsetValue.with(CharsetName.with(s)));
            }

            @Override
            AcceptCharsetValue value() {
                return this.token(CharPredicates.letterOrDigit(), s -> AcceptCharsetValue.with(CharsetName.with(s)));
            }

            @Override
            AcceptCharsetValueParameterName<?> parameterName() {
                return AcceptCharsetValueParameterName.with(this.token(CharPredicates.letterOrDigit()));
            }

            @Override
            String quotedParameterValue(AcceptCharsetValueParameterName<?> parameterName) {
                return this.quotedText(CharPredicates.letterOrDigit(), false);
            }

            @Override
            String unquotedParameterValue(AcceptCharsetValueParameterName<?> parameterName) {
                return this.token(CharPredicates.letterOrDigit());
            }

            @Override
            void valueComplete(final AcceptCharsetValue value) {

            }

            @Override
            void missingValue() {
                this.failMissingValue(VALUE_LABEL);
            }
        }.parse();
        return null;
    }

    @Override
    String valueLabel() {
        return VALUE_LABEL;
    }

    private final static String VALUE_LABEL = "Value";

    @Override
    public Class<HeaderParserWithParameters<?, ?>> type() {
        return Cast.to(HeaderParserWithParameters.class);
    }

    // TypeNameTesting .........................................................................................

    @Override
    public String typeNamePrefix() {
        return HeaderParser.class.getSimpleName();
    }

    @Override
    public String typeNameSuffix() {
        return "";
    }
}
