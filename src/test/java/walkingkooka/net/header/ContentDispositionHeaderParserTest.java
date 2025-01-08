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
import walkingkooka.collect.map.Maps;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.Optional;

public final class ContentDispositionHeaderParserTest extends HeaderParserWithParametersTestCase<ContentDispositionHeaderParser,
    ContentDisposition> {

    // parse ...................................................................................................

    @Test
    public void testWildcardFails() {
        this.parseStringInvalidCharacterFails("*");
    }

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
    public void testType() {
        this.parseStringAndCheck("A", "A");
    }

    @Test
    public void testTypeSeparatorFails() {
        this.parseStringMissingValueFails("A,");
    }

    @Test
    public void testTypeSeparatorSpaceFails() {
        this.parseStringMissingValueFails("A, ");
    }

    @Test
    public void testTypeSeparatorTabFails() {
        this.parseStringMissingValueFails("A,\t");
    }

    @Test
    public void testTypeInvalidCharacterFails() {
        this.parseStringInvalidCharacterFails("A<");
    }

    @Test
    public void testTypeInvalidCharacterFails2() {
        this.parseStringInvalidCharacterFails("ABC<");
    }

    @Test
    public void testTypeEqualsFails() {
        this.parseStringMissingParameterValueFails("A;b=");
    }

    @Test
    public void testTypeSpaceEqualsFails() {
        this.parseStringMissingParameterValueFails("A;b =");
    }

    @Test
    public void testTypeTabEqualsFails() {
        this.parseStringMissingParameterValueFails("A;b =");
    }

    @Test
    public void testTypeSpaceTabSpaceTabEqualsFails() {
        this.parseStringMissingParameterValueFails("A;b \t \t=");
    }

    @Test
    public void testTypeEqualsSpaceFails() {
        parseStringMissingParameterValueFails("A;b= ");
    }

    @Test
    public void testTypeEqualsTabFails() {
        parseStringMissingParameterValueFails("A;b=\t");
    }

    @Test
    public void testTypeEqualsCrNlSpaceFails() {
        parseStringMissingParameterValueFails("A;b=\r\n ");
    }

    @Test
    public void testTypeEqualsCrNlTabFails() {
        parseStringMissingParameterValueFails("A;b=\r\n\t");
    }

    @Test
    public void testTypeParameterSeparatorSeparatorFails() {
        this.parseStringInvalidCharacterFails("A;;", 2);
    }

    @Test
    public void testType2() {
        this.parseStringAndCheck("ABC", "ABC");
    }

    @Test
    public void testTypeSpace() {
        this.parseStringAndCheck("A ", "A");
    }

    @Test
    public void testTypeTab() {
        this.parseStringAndCheck("A\t", "A");
    }

    @Test
    public void testTypeCrFails() {
        this.parseStringInvalidCharacterFails("A\r");
    }

    @Test
    public void testTypeCrNlFails() {
        this.parseStringInvalidCharacterFails("A\r\n");
    }

    @Test
    public void testTypeCrNlNonWhitespaceFails() {
        this.parseStringInvalidCharacterFails("A\r\n.");
    }

    @Test
    public void testTypeCrNlSpace() {
        this.parseStringAndCheck("A\r\n ", "A");
    }

    @Test
    public void testTypeCrNlTab() {
        this.parseStringAndCheck("A\r\n\t", "A");
    }

    @Test
    public void testTypeSpaceTabSpaceTab() {
        this.parseStringAndCheck("A \t \t", "A");
    }

    @Test
    public void testTypeSeparator() {
        this.parseStringAndCheck("A;", "A");
    }

    @Test
    public void testTypeSeparatorSpace() {
        this.parseStringAndCheck("A; ", "A");
    }

    @Test
    public void testTypeSeparatorTab() {
        this.parseStringAndCheck("A;\t", "A");
    }

    @Test
    public void testTypeParameterSeparatorEqualsFails() {
        this.parseStringInvalidCharacterFails("A;=");
    }

    @Test
    public void testTypeParameterNameInvalidCharFails() {
        this.parseStringInvalidCharacterFails("A;b>=c", '>');
    }

    @Test
    public void testTypeParameterNameSpaceInvalidCharFails() {
        this.parseStringInvalidCharacterFails("A;b >=c", '>');
    }

    @Test
    public void testTypeParameterNameTabInvalidCharFails() {
        this.parseStringInvalidCharacterFails("A;b\t>=c", '>');
    }

    @Test
    public void testTypeParameterNameEqualsInvalidCharFails() {
        this.parseStringInvalidCharacterFails("A;b=\0c", '\0');
    }

    @Test
    public void testTypeMultiValueSeparatorFails() {
        this.parseStringMissingValueFails("A,");
    }

    @Test
    public void testTypeParameterNameEqualsParameterValue() {
        this.parseStringAndCheck("A;b=c",
            "A",
            "b", "c");
    }

    @Test
    public void testTypeParameterNameSpaceEqualsParameterValue() {
        this.parseStringAndCheck("A;b =c",
            "A",
            "b", "c");
    }

    @Test
    public void testTypeParameterNameTabEqualsParameterValue() {
        this.parseStringAndCheck("A;b\t=c",
            "A",
            "b", "c");
    }

    @Test
    public void testTypeParameterNameSpaceTabSpaceTabEqualsParameterValue() {
        this.parseStringAndCheck("A;b \t \t=c",
            "A",
            "b", "c");
    }

    @Test
    public void testTypeParameterNameEqualsSpaceParameterValue() {
        this.parseStringAndCheck("A;b= c",
            "A",
            "b", "c");
    }

    @Test
    public void testTypeParameterNameEqualsTabParameterValue() {
        this.parseStringAndCheck("A;b=\tc",
            "A",
            "b", "c");
    }

    @Test
    public void testTypeParameterNameEqualsSpaceTabSpaceTabParameterValue() {
        this.parseStringAndCheck("A;b= \t \tc",
            "A",
            "b", "c");
    }

    @Test
    public void testTypeParameterQuotedParameterValue() {
        final String text = "A;b=c\"d\"";
        this.parseStringInvalidCharacterFails(text, text.indexOf('d') - 1);
    }

    @Test
    public void testTypeParameterNameEqualsQuotedParameterValue() {
        this.parseStringAndCheck("A;b=\"c\"",
            "A",
            "b", "c");
    }

    @Test
    public void testTypeParameterValueInvalidCharFails() {
        this.parseStringInvalidCharacterFails("A;b=c>", '>');
    }

    @Test
    public void testTypeParameterValueSpaceInvalidCharFails() {
        this.parseStringInvalidCharacterFails("A;b=c Q", 'Q');
    }

    @Test
    public void testTypeParameterValueSpaceInvalidCharFails2() {
        this.parseStringInvalidCharacterFails("A;b=c >", '>');
    }

    @Test
    public void testTypeParameterSpace() {
        this.parseStringAndCheck("A;bcd=123 ",
            "A",
            "bcd", "123");
    }

    @Test
    public void testTypeParameterSeparator() {
        this.parseStringAndCheck("A;bcd=123;",
            "A",
            "bcd", "123");
    }

    @Test
    public void testTypeParameterTab() {
        this.parseStringAndCheck("A;bcd=123\t",
            "A",
            "bcd", "123");
    }

    @Test
    public void testTypeParameterSpaceTabSpaceTab() {
        this.parseStringAndCheck("A;bcd=123 \t \t",
            "A",
            "bcd", "123");
    }

    @Test
    public void testTypeParameterSeparatorSpaceParameter() {
        this.parseStringAndCheck("A; b=c",
            "A",
            "b", "c");
    }

    @Test
    public void testTypeParameterSeparatorTabParameter() {
        this.parseStringAndCheck("A;\tb=c",
            "A",
            "b", "c");
    }

    @Test
    public void testTypeSpaceParameterSeparatorParameter() {
        this.parseStringAndCheck("A ;b=c",
            "A",
            "b", "c");
    }

    @Test
    public void testTypeSpaceParameterSeparatorSpaceParameter() {
        this.parseStringAndCheck("A ; b=c",
            "A",
            "b", "c");
    }

    @Test
    public void testTypeTabParameterSeparatorTabParameter() {
        this.parseStringAndCheck("A\t;\tb=c",
            "A",
            "b", "c");
    }

    @Test
    public void testTypeSpaceTabSpaceTabParameterSeparatorSpaceTabParameter() {
        this.parseStringAndCheck("A \t \t; \t \tb=c",
            "A",
            "b", "c");
    }

    @Test
    public void testTypeParameterSeparatorParameterNameFails() {
        final String text = "A;b=c;D";
        this.parseStringMissingParameterValueFails(text);
    }

    @Test
    public void testTypeParameter() {
        this.parseStringAndCheck("V1;p1=v1;",
            "V1",
            "p1", "v1");
    }

    @Test
    public void testTypeParameterSeparatorParameter() {
        this.parseStringAndCheck("V1;p1=v1;p2=v2",
            "V1",
            "p1", "v1",
            "p2", "v2");
    }

    @Test
    public void testTypeParameterWhitespaceSeparatorParameter() {
        this.parseStringAndCheck("V1;p1=v1 ;p2=v2",
            "V1",
            "p1", "v1",
            "p2", "v2");
    }

    @Test
    public void testTypeParameterParameterSeparatorWhitespaceParameter() {
        this.parseStringAndCheck("V1;p1=v1; p2=v2",
            "V1",
            "p1", "v1",
            "p2", "v2");
    }

    @Test
    public void testTypeParameterSeparatorQuoteFails() {
        this.parseMissingClosingQuoteFails("V1;p1=\"");
    }

    // parse creation-date............................................................................................

    @Test
    public void testCreationDateInvalidFails() {
        this.parseStringFails(
            "V; creation-date=123",
            "Invalid character '1' at 0 in \"123\""
        );
    }

    @Test
    public void testCreationDate() {
        this.parseStringAndCheck("attachment; creation-date=\"Wed, 12 Feb 1997 16:29:51 -0500\"",
            "attachment",
            "creation-date",
            OffsetDateTime.of(1997, 2, 12, 16, 29, 51, 0, ZoneOffset.ofHours(-5)));
    }

    // parse filename............................................................................................

    @Test
    public void testFilenameMissingFails() {
        this.parseStringFails(
            "V; filename=\"\"",
            "Empty \"filename\""
        );
    }

    @Test
    public void testFilenameMissingFails2() {
        this.parseStringMissingParameterValueFails("V; filename=");
    }

    @Test
    public void testFilename() {
        this.parseStringAndCheck("attachment; filename=readme.txt",
            "attachment",
            "filename",
            ContentDispositionFileName.notEncoded("readme.txt"));
    }

    @Test
    public void testFilenameQuoted() {
        this.parseStringAndCheck("attachment; filename=\"readme.txt\"",
            "attachment",
            "filename",
            ContentDispositionFileName.notEncoded("readme.txt"));
    }

    // parse filename star............................................................................................

    @Test
    public void testFilenameStar() {
        this.parseStringAndCheck("attachment; filename*=UTF-8'en'abc%20123.txt",
            "attachment",
            "filename*",
            ContentDispositionFileName.encoded(
                EncodedText.with(CharsetName.UTF_8,
                    Optional.of(LanguageName.with("en")),
                    "abc 123.txt")));
    }

    // parse modification-date............................................................................................

    @Test
    public void testModificationDateInvalidFails() {
        this.parseStringFails(
            "V; modification-date=123",
            "Invalid character '1' at 0 in \"123\""
        );
    }

    @Test
    public void testModificationDate() {
        this.parseStringAndCheck("attachment; modification-date=\"Wed, 12 Feb 1997 16:29:51 -0500\"",
            "attachment",
            "modification-date",
            OffsetDateTime.of(1997, 2, 12, 16, 29, 51, 0, ZoneOffset.ofHours(-5)));
    }

    // parse read-date............................................................................................

    @Test
    public void testReadDateInvalidFails() {
        this.parseStringFails(
            "V; read-date=123",
            "Invalid character '1' at 0 in \"123\""
        );
    }

    @Test
    public void testReadDate() {
        this.parseStringAndCheck("attachment; read-date=\"Wed, 12 Feb 1997 16:29:51 -0500\"",
            "attachment",
            "read-date",
            OffsetDateTime.of(1997, 2, 12, 16, 29, 51, 0, ZoneOffset.ofHours(-5)));
    }

    // parse filename............................................................................................

    @Test
    public void testSizeInvalidFails() {
        this.parseStringFails(
            "V; size=A",
            "Invalid number in \"A\""
        );
    }

    @Test
    public void testSize() {
        this.parseStringAndCheck("attachment; size=123",
            "attachment",
            "size",
            123L);
    }

    // helpers...................................................................................................

    @Override
    public ContentDisposition parseString(final String text) {
        return ContentDispositionHeaderParser.parseContentDisposition(text);
    }

    private void parseStringAndCheck(final String header, final String type) {
        this.parseStringAndCheck(header, type, ContentDisposition.NO_PARAMETERS);
    }

    private void parseStringAndCheck(final String header,
                                     final String type,
                                     final String parameterName, final Object parameterValue) {
        this.parseStringAndCheck(header, type, Maps.of(ContentDispositionParameterName.with(parameterName), parameterValue));
    }

    private void parseStringAndCheck(final String header,
                                     final String type,
                                     final String parameterName1, final Object parameterValue1,
                                     final String parameterName2, final Object parameterValue2) {
        this.parseStringAndCheck(header,
            type,
            Maps.of(ContentDispositionParameterName.with(parameterName1), parameterValue1,
                ContentDispositionParameterName.with(parameterName2), parameterValue2));
    }


    private void parseStringAndCheck(final String header,
                                     final String type,
                                     final Map<ContentDispositionParameterName<?>, Object> parameters) {
        this.parseStringAndCheck(header,
            ContentDispositionType.with(type).setParameters(parameters));
    }

    @Override
    String valueLabel() {
        return ContentDispositionHeaderParser.TYPE;
    }

    @Override
    public Class<ContentDispositionHeaderParser> type() {
        return ContentDispositionHeaderParser.class;
    }
}
