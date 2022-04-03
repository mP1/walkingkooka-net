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

public final class AcceptCharsetHeaderParserTest extends HeaderParserWithParametersTestCase<AcceptCharsetHeaderParser,
        AcceptCharset> {

    // parse ...................................................................................................

    @Test
    public void testParameterSeparatorFails() {
        this.parseStringMissingValueFails(";", 0);
    }

    @Test
    public void testKeyValueSeparatorFails() {
        this.parseStringInvalidCharacterFails("=");
    }

    @Test
    public void testSlashFails() {
        this.parseStringInvalidCharacterFails("/");
    }

    @Test
    public void testValueSeparatorFails() {
        this.parseStringInvalidCharacterFails(",");
    }

    @Test
    public void testCharsetSeparatorFails() {
        this.parseStringMissingValueFails("utf-8,");
    }

    @Test
    public void testCharsetSeparatorSpaceFails() {
        this.parseStringFails("utf-8, ", "Missing charset at 7 in \"utf-8, \"");
    }

    @Test
    public void testCharsetSeparatorTabFails() {
        this.parseStringFails("utf-8,\t", "Missing charset at 7 in \"utf-8,\\t\"");
    }

    @Test
    public void testCharsetInvalidInitialCharacterFails() {
        this.parseStringInvalidCharacterFails("!utf-8", '!');
    }

    @Test
    public void testCharsetInvalidPartCharacterFails() {
        this.parseStringInvalidCharacterFails("utf\08", '\0');
    }

    @Test
    public void testCharset() {
        this.parseStringAndCheck("utf-8", "utf-8");
    }

    @Test
    public void testCharsetInvalidCharacterFails2() {
        this.parseStringInvalidCharacterFails("UTF-8BC<");
    }

    @Test
    public void testWildcard() {
        this.parseStringAndCheck("*", AcceptCharsetValue.WILDCARD_VALUE);
    }

    @Test
    public void testWildcardInvalidFails() {
        this.parseStringInvalidCharacterFails("*1");
    }

    @Test
    public void testWildcardInvalidFails2() {
        this.parseStringInvalidCharacterFails("*u");
    }

    @Test
    public void testWildcardWildcardFails() {
        this.parseStringInvalidCharacterFails("**");
    }

    @Test
    public void testCharsetEqualsFails() {
        this.parseStringMissingParameterValueFails("UTF-8;b=");
    }

    @Test
    public void testCharsetSpaceEqualsFails() {
        this.parseStringMissingParameterValueFails("UTF-8;b =");
    }

    @Test
    public void testCharsetTabEqualsFails() {
        this.parseStringMissingParameterValueFails("UTF-8;b =");
    }

    @Test
    public void testCharsetSpaceTabSpaceTabEqualsFails() {
        this.parseStringMissingParameterValueFails("UTF-8;b \t \t=");
    }

    @Test
    public void testCharsetEqualsSpaceFails() {
        this.parseStringMissingParameterValueFails("UTF-8;b= ");
    }

    @Test
    public void testCharsetEqualsTabFails() {
        this.parseStringMissingParameterValueFails("UTF-8;b=\t");
    }

    @Test
    public void testCharsetEqualsCrNlSpaceFails() {
        this.parseStringMissingParameterValueFails("UTF-8;b=\r\n ");
    }

    @Test
    public void testCharsetEqualsCrNlTabFails() {
        this.parseStringMissingParameterValueFails("UTF-8;b=\r\n\t");
    }

    @Test
    public void testCharsetParameterTokenSeparatorTokenSeparatorFails() {
        this.parseStringInvalidCharacterFails("UTF-8;;", 6);
    }

    @Test
    public void testCharset2() {
        this.parseStringAndCheck("utf-16", "utf-16");
    }

    @Test
    public void testCharsetSpace() {
        this.parseStringAndCheck("utf-8 ", "utf-8");
    }

    @Test
    public void testCharsetTab() {
        this.parseStringAndCheck("utf-8\t", "utf-8");
    }

    @Test
    public void testCharsetCrNlSpace() {
        this.parseStringAndCheck("utf-8\r\n ", "utf-8");
    }

    @Test
    public void testCharsetCrNlTab() {
        this.parseStringAndCheck("utf-8\r\n\t", "utf-8");
    }

    @Test
    public void testCharsetSpaceTabCrNlSpaceTab() {
        this.parseStringAndCheck("UTF-8 \t\r\n \t", "UTF-8");
    }

    @Test
    public void testCharsetSeparator() {
        this.parseStringAndCheck("UTF-8;", "UTF-8");
    }

    @Test
    public void testCharsetSeparatorSpace() {
        this.parseStringAndCheck("UTF-8; ", "UTF-8");
    }

    @Test
    public void testCharsetSeparatorTab() {
        this.parseStringAndCheck("UTF-8;\t", "UTF-8");
    }

    @Test
    public void testCharsetParameterSeparatorFails() {
        this.parseStringInvalidCharacterFails("UTF-8;=", 6);
    }

    @Test
    public void testCharsetParameterNameInvalidCharFails() {
        this.parseStringInvalidCharacterFails("UTF-8;b>=c", '>');
    }

    @Test
    public void testCharsetParameterNameSpaceInvalidCharFails() {
        this.parseStringInvalidCharacterFails("UTF-8;b >=c", '>');
    }

    @Test
    public void testCharsetParameterNameTabInvalidCharFails() {
        this.parseStringInvalidCharacterFails("UTF-8;b\t>=c", '>');
    }

    @Test
    public void testCharsetParameterNameEqualsInvalidCharFails() {
        this.parseStringInvalidCharacterFails("UTF-8;b=\0c", '\0');
    }

    @Test
    public void testCharsetParameterNameEqualsParameterValue() {
        this.parseStringAndCheck("UTF-8;b=c",
                "UTF-8",
                "b", "c");
    }

    @Test
    public void testCharsetParameterNameSpaceEqualsParameterValue() {
        this.parseStringAndCheck("UTF-8;b =c",
                "UTF-8",
                "b", "c");
    }

    @Test
    public void testCharsetParameterNameTabEqualsParameterValue() {
        this.parseStringAndCheck("UTF-8;b\t=c",
                "UTF-8",
                "b", "c");
    }

    @Test
    public void testCharsetParameterNameSpaceTabSpaceTabEqualsParameterValue() {
        this.parseStringAndCheck("UTF-8;b \t \t=c",
                "UTF-8",
                "b", "c");
    }

    @Test
    public void testCharsetParameterNameEqualsSpaceParameterValue() {
        this.parseStringAndCheck("UTF-8;b= c",
                "UTF-8",
                "b", "c");
    }

    @Test
    public void testCharsetParameterNameEqualsTabParameterValue() {
        this.parseStringAndCheck("UTF-8;b=\tc",
                "UTF-8",
                "b", "c");
    }

    @Test
    public void testCharsetParameterNameEqualsSpaceTabSpaceTabParameterValue() {
        this.parseStringAndCheck("UTF-8;b= \t \tc",
                "UTF-8",
                "b", "c");
    }

    @Test
    public void testCharsetParameterValueInvalidCharFails() {
        this.parseStringInvalidCharacterFails("UTF-8;b=c>", '>');
    }

    @Test
    public void testCharsetParameterValueSpaceInvalidCharFails() {
        this.parseStringInvalidCharacterFails("UTF-8;b=c Q", 'Q');
    }

    @Test
    public void testCharsetParameterValueSpaceInvalidCharFails2() {
        this.parseStringInvalidCharacterFails("UTF-8;b=c >", '>');
    }

    @Test
    public void testCharsetParameterSpace() {
        this.parseStringAndCheck("UTF-8;bcd=123 ",
                "UTF-8",
                "bcd", "123");
    }

    @Test
    public void testCharsetParameterSeparator() {
        this.parseStringAndCheck("UTF-8;bcd=123;",
                "UTF-8",
                "bcd", "123");
    }

    @Test
    public void testCharsetParameterTab() {
        this.parseStringAndCheck("UTF-8;bcd=123\t",
                "UTF-8",
                "bcd", "123");
    }

    @Test
    public void testCharsetParameterSpaceTabSpaceTab() {
        this.parseStringAndCheck("UTF-8;bcd=123 \t \t",
                "UTF-8",
                "bcd", "123");
    }

    @Test
    public void testCharsetParameterSeparatorSpaceParameter() {
        this.parseStringAndCheck("UTF-8; b=c",
                "UTF-8",
                "b", "c");
    }

    @Test
    public void testCharsetParameterSeparatorTabParameter() {
        this.parseStringAndCheck("UTF-8;\tb=c",
                "UTF-8",
                "b", "c");
    }

    @Test
    public void testCharsetSpaceParameterSeparatorParameter() {
        this.parseStringAndCheck("UTF-8 ;b=c",
                "UTF-8",
                "b", "c");
    }

    @Test
    public void testCharsetSpaceParameterSeparatorSpaceParameter() {
        this.parseStringAndCheck("UTF-8 ; b=c",
                "UTF-8",
                "b", "c");
    }

    @Test
    public void testCharsetTabParameterSeparatorTabParameter() {
        this.parseStringAndCheck("UTF-8\t;\tb=c",
                "UTF-8",
                "b", "c");
    }

    @Test
    public void testCharsetSpaceTabSpaceTabParameterSeparatorSpaceTabParameter() {
        this.parseStringAndCheck("UTF-8 \t \t; \t \tb=c",
                "UTF-8",
                "b", "c");
    }

    @Test
    public void testCharsetParameterNameQuotedParameterValueQuotedParameterValueFails() {
        final String text = "UTF-8;b=\"c\"\"d\"";
        this.parseStringInvalidCharacterFails(text, text.indexOf('d') - 1);
    }

    @Test
    public void testCharsetParameterSeparatorParameterNameFails() {
        this.parseStringMissingParameterValueFails("UTF-8;b=c;D");
    }

    @Test
    public void testCharsetParameter() {
        this.parseStringAndCheck("utf-8;p1=v1;",
                "utf-8",
                "p1", "v1");
    }

    @Test
    public void testCharsetParameterQuotedValue() {
        this.parseStringAndCheck("utf-8;p1=\"v1\";",
                "utf-8",
                "p1", "v1");
    }

    @Test
    public void testCharsetParameterSeparatorParameter() {
        this.parseStringAndCheck("utf-8;p1=v1;p2=v2",
                "utf-8",
                "p1", "v1",
                "p2", "v2");
    }

    @Test
    public void testCharsetParameterWhitespaceSeparatorParameter() {
        this.parseStringAndCheck("utf-8;p1=v1 ;p2=v2",
                "utf-8",
                "p1", "v1",
                "p2", "v2");
    }

    @Test
    public void testCharsetParameterParameterSeparatorWhitespaceParameter() {
        this.parseStringAndCheck("utf-8;p1=v1; p2=v2",
                "utf-8",
                "p1", "v1",
                "p2", "v2");
    }

    @Test
    public void testCharsetSeparatorCharset() {
        this.parseStringAndCheck("utf-8,utf-16;",
                this.charsetHeader("utf-8"),
                this.charsetHeader("utf-16"));
    }

    // q............................................................................................

    @Test
    public void testQInvalidValueFails() {
        this.parseStringFails("utf-8; q=X",
                "Failed to convert \"q\" value \"X\", message: For input string: \"X\"");
    }

    @Test
    public void testQ() {
        this.parseStringAndCheck("utf-8; q=0.75",
                "utf-8",
                "q",
                0.75f);
    }

    @Test
    public void testCharsetSeparatorCharsetQSorted() {
        this.parseStringAndCheck("utf-8; q=0.5, utf-16; q=0.75",
                this.charsetHeader("utf-16", "q", 0.75f),
                this.charsetHeader("utf-8", "q", 0.5f));
    }

    // helpers...................................................................................................

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
        this.parseStringAndCheck(header,
                AcceptCharsetValue.with(CharsetName.with(charset)).setParameters(parameters));
    }

    private void parseStringAndCheck(final String header, final AcceptCharsetValue... values) {
        this.parseStringAndCheck(header, AcceptCharset.with(Lists.of(values)));
    }

    private AcceptCharsetValue charsetHeader(final String charset) {
        return AcceptCharsetValue.with(CharsetName.with(charset));
    }

    private AcceptCharsetValue charsetHeader(final String charset,
                                             final String parameterName,
                                             final Object parameterValue) {
        return AcceptCharsetValue.with(CharsetName.with(charset))
                .setParameters(this.parameters(parameterName, parameterValue));
    }

    private Map<AcceptCharsetValueParameterName<?>, Object> parameters(final String name,
                                                                       final Object value) {
        return this.parameters(AcceptCharsetValueParameterName.with(name), value);
    }

    private Map<AcceptCharsetValueParameterName<?>, Object> parameters(final AcceptCharsetValueParameterName<?> name,
                                                                       final Object value) {
        return Maps.of(name, value);
    }

    private Map<AcceptCharsetValueParameterName<?>, Object> parameters(
            final String parameterName1, final Object parameterValue1,
            final String parameterName2, final Object parameterValue2) {
        return Maps.of(AcceptCharsetValueParameterName.with(parameterName1),
                parameterValue1,
                AcceptCharsetValueParameterName.with(parameterName2),
                parameterValue2);
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
