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
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.map.Maps;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertSame;

public final class AcceptCharsetHeaderParserTest extends HeaderParserWithParametersTestCase<AcceptCharsetHeaderParser,
    AcceptCharset> {

    // parse ...........................................................................................................

    @Test
    public void testParseParameterSeparatorFails() {
        this.parseStringMissingValueFails(";", 0);
    }

    @Test
    public void testParseKeyValueSeparatorFails() {
        this.parseStringInvalidCharacterFails("=");
    }

    @Test
    public void testParseSlashFails() {
        this.parseStringInvalidCharacterFails("/");
    }

    @Test
    public void testParseValueSeparatorFails() {
        this.parseStringInvalidCharacterFails(",");
    }

    @Test
    public void testParseCharsetSeparatorFails() {
        this.parseStringMissingValueFails("utf-8,");
    }

    @Test
    public void testParseCharsetSeparatorSpaceFails() {
        this.parseStringFails("utf-8, ", "Missing charset at 7 in \"utf-8, \"");
    }

    @Test
    public void testParseCharsetSeparatorTabFails() {
        this.parseStringFails("utf-8,\t", "Missing charset at 7 in \"utf-8,\\t\"");
    }

    @Test
    public void testParseCharsetInvalidInitialCharacterFails() {
        this.parseStringInvalidCharacterFails("!utf-8", '!');
    }

    @Test
    public void testParseCharsetInvalidPartCharacterFails() {
        this.parseStringInvalidCharacterFails("utf\08", '\0');
    }

    @Test
    public void testParseCharsetUtf8() {
        this.parseStringAndCheck(
            "utf-8",
            "utf-8"
        );
    }

    @Test
    public void testParseCharsetUtf8Constant() {
        assertSame(
            AcceptCharset.UTF_8,
            AcceptCharsetHeaderParser.parseAcceptCharset("UTF-8")
        );
    }

    @Test
    public void testParseCharsetUtf8ConstantHeaderValueTextWrongCase() {
        assertSame(
            AcceptCharset.UTF_8,
            AcceptCharsetHeaderParser.parseAcceptCharset("utF-8")
        );
    }

    @Test
    public void testParseCharsetInvalidCharacterFails2() {
        this.parseStringInvalidCharacterFails("UTF-8BC<");
    }

    @Test
    public void testParseWildcard() {
        this.parseStringAndCheck(
            "*",
            AcceptCharsetValue.WILDCARD_VALUE
        );
    }

    @Test
    public void testParseWildcardInvalidFails() {
        this.parseStringInvalidCharacterFails("*1");
    }

    @Test
    public void testParseWildcardInvalidFails2() {
        this.parseStringInvalidCharacterFails("*u");
    }

    @Test
    public void testParseWildcardWildcardFails() {
        this.parseStringInvalidCharacterFails("**");
    }

    @Test
    public void testParseCharsetEqualsFails() {
        this.parseStringMissingParameterValueFails("UTF-8;b=");
    }

    @Test
    public void testParseCharsetSpaceEqualsFails() {
        this.parseStringMissingParameterValueFails("UTF-8;b =");
    }

    @Test
    public void testParseCharsetTabEqualsFails() {
        this.parseStringMissingParameterValueFails("UTF-8;b =");
    }

    @Test
    public void testParseCharsetSpaceTabSpaceTabEqualsFails() {
        this.parseStringMissingParameterValueFails("UTF-8;b \t \t=");
    }

    @Test
    public void testParseCharsetEqualsSpaceFails() {
        this.parseStringMissingParameterValueFails("UTF-8;b= ");
    }

    @Test
    public void testParseCharsetEqualsTabFails() {
        this.parseStringMissingParameterValueFails("UTF-8;b=\t");
    }

    @Test
    public void testParseCharsetEqualsCrNlSpaceFails() {
        this.parseStringMissingParameterValueFails("UTF-8;b=\r\n ");
    }

    @Test
    public void testParseCharsetEqualsCrNlTabFails() {
        this.parseStringMissingParameterValueFails("UTF-8;b=\r\n\t");
    }

    @Test
    public void testParseCharsetParameterTokenSeparatorTokenSeparatorFails() {
        this.parseStringInvalidCharacterFails(
            "UTF-8;;",
            6
        );
    }

    @Test
    public void testParseCharset2() {
        this.parseStringAndCheck(
            "utf-16",
            "utf-16"
        );
    }

    @Test
    public void testParseCharsetSpace() {
        this.parseStringAndCheck(
            "utf-8 ",
            "utf-8"
        );
    }

    @Test
    public void testParseCharsetTab() {
        this.parseStringAndCheck(
            "utf-8\t",
            "utf-8"
        );
    }

    @Test
    public void testParseCharsetCrNlSpace() {
        this.parseStringAndCheck(
            "utf-8\r\n ",
            "utf-8")
        ;
    }

    @Test
    public void testParseCharsetCrNlTab() {
        this.parseStringAndCheck(
            "utf-8\r\n\t",
            "utf-8"
        );
    }

    @Test
    public void testParseCharsetSpaceTabCrNlSpaceTab() {
        this.parseStringAndCheck(
            "UTF-8 \t\r\n \t",
            "UTF-8"
        );
    }

    @Test
    public void testParseCharsetSeparator() {
        this.parseStringAndCheck(
            "UTF-8;",
            "UTF-8"
        );
    }

    @Test
    public void testParseCharsetSeparatorSpace() {
        this.parseStringAndCheck(
            "UTF-8; ",
            "UTF-8"
        );
    }

    @Test
    public void testParseCharsetSeparatorTab() {
        this.parseStringAndCheck(
            "UTF-8;\t",
            "UTF-8"
        );
    }

    @Test
    public void testParseCharsetParameterSeparatorFails() {
        this.parseStringInvalidCharacterFails(
            "UTF-8;=",
            6
        );
    }

    @Test
    public void testParseCharsetParameterNameInvalidCharFails() {
        this.parseStringInvalidCharacterFails(
            "UTF-8;b>=c",
            '>'
        );
    }

    @Test
    public void testParseCharsetParameterNameSpaceInvalidCharFails() {
        this.parseStringInvalidCharacterFails(
            "UTF-8;b >=c",
            '>'
        );
    }

    @Test
    public void testParseCharsetParameterNameTabInvalidCharFails() {
        this.parseStringInvalidCharacterFails(
            "UTF-8;b\t>=c",
            '>'
        );
    }

    @Test
    public void testParseCharsetParameterNameEqualsInvalidCharFails() {
        this.parseStringInvalidCharacterFails(
            "zUTF-8;b=\0c",
            '\0'
        );
    }

    @Test
    public void testParseCharsetParameterNameEqualsParameterValue() {
        this.parseStringAndCheck(
            "UTF-8;b=c",
            "UTF-8",
            "b",
            "c"
        );
    }

    @Test
    public void testParseCharsetParameterNameSpaceEqualsParameterValue() {
        this.parseStringAndCheck(
            "UTF-8;b =c",
            "UTF-8",
            "b",
            "c"
        );
    }

    @Test
    public void testParseCharsetParameterNameTabEqualsParameterValue() {
        this.parseStringAndCheck(
            "UTF-8;b\t=c",
            "UTF-8",
            "b",
            "c"
        );
    }

    @Test
    public void testParseCharsetParameterNameSpaceTabSpaceTabEqualsParameterValue() {
        this.parseStringAndCheck(
            "UTF-8;b \t \t=c",
            "UTF-8",
            "b",
            "c"
        );
    }

    @Test
    public void testParseCharsetParameterNameEqualsSpaceParameterValue() {
        this.parseStringAndCheck(
            "UTF-8;b= c",
            "UTF-8",
            "b",
            "c"
        );
    }

    @Test
    public void testParseCharsetParameterNameEqualsTabParameterValue() {
        this.parseStringAndCheck(
            "UTF-8;b=\tc",
            "UTF-8",
            "b",
            "c"
        );
    }

    @Test
    public void testParseCharsetParameterNameEqualsSpaceTabSpaceTabParameterValue() {
        this.parseStringAndCheck(
            "UTF-8;b= \t \tc",
            "UTF-8",
            "b",
            "c"
        );
    }

    @Test
    public void testParseCharsetParameterValueInvalidCharFails() {
        this.parseStringInvalidCharacterFails(
            "UTF-8;b=c>",
            '>'
        );
    }

    @Test
    public void testParseCharsetParameterValueSpaceInvalidCharFails() {
        this.parseStringInvalidCharacterFails(
            "UTF-8;b=c Q",
            'Q'
        );
    }

    @Test
    public void testParseCharsetParameterValueSpaceInvalidCharFails2() {
        this.parseStringInvalidCharacterFails("UTF-8;b=c >", '>');
    }

    @Test
    public void testParseCharsetParameterSpace() {
        this.parseStringAndCheck(
            "UTF-8;bcd=123 ",
            "UTF-8",
            "bcd",
            "123"
        );
    }

    @Test
    public void testParseCharsetParameterSeparator() {
        this.parseStringAndCheck(
            "UTF-8;bcd=123;",
            "UTF-8",
            "bcd",
            "123"
        );
    }

    @Test
    public void testParseCharsetParameterTab() {
        this.parseStringAndCheck(
            "UTF-8;bcd=123\t",
            "UTF-8",
            "bcd",
            "123"
        );
    }

    @Test
    public void testParseCharsetParameterSpaceTabSpaceTab() {
        this.parseStringAndCheck(
            "UTF-8;bcd=123 \t \t",
            "UTF-8",
            "bcd",
            "123"
        );
    }

    @Test
    public void testParseCharsetParameterSeparatorSpaceParameter() {
        this.parseStringAndCheck(
            "UTF-8; b=c",
            "UTF-8",
            "b",
            "c"
        );
    }

    @Test
    public void testParseCharsetParameterSeparatorTabParameter() {
        this.parseStringAndCheck(
            "UTF-8;\tb=c",
            "UTF-8",
            "b",
            "c"
        );
    }

    @Test
    public void testParseCharsetSpaceParameterSeparatorParameter() {
        this.parseStringAndCheck(
            "UTF-8 ;b=c",
            "UTF-8",
            "b",
            "c"
        );
    }

    @Test
    public void testParseCharsetSpaceParameterSeparatorSpaceParameter() {
        this.parseStringAndCheck(
            "UTF-8 ; b=c",
            "UTF-8",
            "b",
            "c"
        );
    }

    @Test
    public void testParseCharsetTabParameterSeparatorTabParameter() {
        this.parseStringAndCheck("UTF-8\t;\tb=c",
            "UTF-8",
            "b", "c");
    }

    @Test
    public void testParseCharsetSpaceTabSpaceTabParameterSeparatorSpaceTabParameter() {
        this.parseStringAndCheck(
            "UTF-8 \t \t; \t \tb=c",
            "UTF-8",
            "b",
            "c"
        );
    }

    @Test
    public void testParseCharsetParameterNameQuotedParameterValueQuotedParameterValueFails() {
        final String text = "UTF-8;b=\"c\"\"d\"";
        this.parseStringInvalidCharacterFails(
            text,
            text.indexOf('d') - 1
        );
    }

    @Test
    public void testParseCharsetParameterSeparatorParameterNameFails() {
        this.parseStringMissingParameterValueFails("UTF-8;b=c;D");
    }

    @Test
    public void testParseCharsetParameter() {
        this.parseStringAndCheck(
            "utf-8;p1=v1;",
            "utf-8",
            "p1",
            "v1"
        );
    }

    @Test
    public void testParseCharsetParameterQuotedValue() {
        this.parseStringAndCheck(
            "utf-8;p1=\"v1\";",
            "utf-8",
            "p1",
            "v1"
        );
    }

    @Test
    public void testParseCharsetParameterSeparatorParameter() {
        this.parseStringAndCheck(
            "utf-8;p1=v1;p2=v2",
            "utf-8",
            "p1", "v1",
            "p2", "v2"
        );
    }

    @Test
    public void testParseCharsetParameterWhitespaceSeparatorParameter() {
        this.parseStringAndCheck(
            "utf-8;p1=v1 ;p2=v2",
            "utf-8",
            "p1", "v1",
            "p2", "v2"
        );
    }

    @Test
    public void testParseCharsetParameterParameterSeparatorWhitespaceParameter() {
        this.parseStringAndCheck(
            "utf-8;p1=v1; p2=v2",
            "utf-8",
            "p1", "v1",
            "p2", "v2"
        );
    }

    @Test
    public void testParseCharsetSeparatorCharset() {
        this.parseStringAndCheck(
            "utf-8,utf-16;",
            this.charsetHeader("utf-8"),
            this.charsetHeader("utf-16")
        );
    }

    @Test
    public void testParseQInvalidValueFails() {
        this.parseStringFails(
            "utf-8; q=X",
            "Invalid number in \"X\""
        );
    }

    @Test
    public void testParseQ() {
        this.parseStringAndCheck(
            "utf-8; q=0.75",
            "utf-8",
            "q",
            0.75f
        );
    }

    @Test
    public void testParseCharsetSeparatorCharsetQSorted() {
        this.parseStringAndCheck(
            "utf-8; q=0.5, utf-16; q=0.75",
            this.charsetHeader("utf-16", "q", 0.75f),
            this.charsetHeader("utf-8", "q", 0.5f)
        );
    }

    @Override
    public AcceptCharset parseString(final String text) {
        return AcceptCharsetHeaderParser.parseAcceptCharset(text);
    }

    private void parseStringAndCheck(final String header, final String charset) {
        this.parseStringAndCheck(header, charset, AcceptCharsetValue.NO_PARAMETERS);
    }

    private void parseStringAndCheck(final String header,
                                     final String charset,
                                     final String parameterName, final Object parameterValue) {
        this.parseStringAndCheck(header, charset, this.parameters(parameterName, parameterValue));
    }

    private void parseStringAndCheck(final String header,
                                     final String charset,
                                     final String parameterName1, final Object parameterValue1,
                                     final String parameterName2, final Object parameterValue2) {
        this.parseStringAndCheck(header,
            charset,
            parameters(parameterName1, parameterValue1, parameterName2, parameterValue2));
    }

    private void parseStringAndCheck(final String header,
                                     final String charset,
                                     final Map<AcceptCharsetValueParameterName<?>, Object> parameters) {
        this.parseStringAndCheck(
            header,
            AcceptCharsetValue.with(
                CharsetName.with(charset)
            ).setParameters(parameters)
        );
    }

    private void parseStringAndCheck(final String header,
                                     final AcceptCharsetValue... values) {
        this.parseStringAndCheck(
            header,
            AcceptCharset.with(
                Lists.of(values)
            )
        );
    }

    private AcceptCharsetValue charsetHeader(final String charset) {
        return AcceptCharsetValue.with(CharsetName.with(charset));
    }

    private AcceptCharsetValue charsetHeader(final String charset,
                                             final String parameterName,
                                             final Object parameterValue) {
        return AcceptCharsetValue.with(
            CharsetName.with(charset)
        ).setParameters(this.parameters(parameterName, parameterValue));
    }

    private Map<AcceptCharsetValueParameterName<?>, Object> parameters(final String name,
                                                                       final Object value) {
        return this.parameters(
            AcceptCharsetValueParameterName.with(name),
            value
        );
    }

    private Map<AcceptCharsetValueParameterName<?>, Object> parameters(final AcceptCharsetValueParameterName<?> name,
                                                                       final Object value) {
        return Maps.of(name, value);
    }

    private Map<AcceptCharsetValueParameterName<?>, Object> parameters(
        final String parameterName1, final Object parameterValue1,
        final String parameterName2, final Object parameterValue2) {
        return Maps.of(
            AcceptCharsetValueParameterName.with(parameterName1),
            parameterValue1,
            AcceptCharsetValueParameterName.with(parameterName2),
            parameterValue2
        );
    }

    @Override
    String valueLabel() {
        return AcceptCharsetHeaderParser.CHARSET;
    }

    @Override
    public Class<AcceptCharsetHeaderParser> type() {
        return AcceptCharsetHeaderParser.class;
    }
}
