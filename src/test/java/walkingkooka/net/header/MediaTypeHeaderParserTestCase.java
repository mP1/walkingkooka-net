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

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class MediaTypeHeaderParserTestCase<P extends MediaTypeHeaderParser, V> extends HeaderParserWithParametersTestCase<P,
        V> {

    MediaTypeHeaderParserTestCase() {
        super();
    }

    // constants

    final static String TYPE = "type";
    final static String SUBTYPE = "subtype";

    @Test
    public final void testWildcardFails() {
        this.parseStringFails("*", "Missing sub type at 1 in \"*\"");
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
    public void testValueSeparatorFails() {
        this.parseStringInvalidCharacterFails(",");
    }

    @Test
    public final void testSlashSubtypeFails() {
        this.parseStringInvalidCharacterFails("/subtype", '/');
    }

    @Test
    public final void testTypeFails() {
        this.parseStringFails("type",
                "Missing type at 4 in \"type\"");
    }

    @Test
    public final void testTypeSlashFails() {
        this.parseStringFails("type/",
                "Missing sub type at 5 in \"type/\"");
    }

    @Test
    public final void testTypeInvalidCharacterFails() {
        this.parseStringInvalidCharacterFails("prima?ry/subtype",
                '?');
    }

    @Test
    public final void testTypeSlashSubTypeMissingFails() {
        this.parseStringInvalidCharacterFails("type/;");
    }

    @Test
    public final void testTypeSlashSubTypeSpaceInvalidFails() {
        this.parseStringInvalidCharacterFails("type/subtype Q", 'Q');
    }

    @Test
    public final void testTypeSlashSubTypeTabInvalidFails() {
        this.parseStringInvalidCharacterFails("type/subtype\tQ", 'Q');
    }

    @Test
    public final void testParameterNameInvalidFails() {
        this.parseStringInvalidCharacterFails("type/subtype;parameter;");
    }

    @Test
    public final void testParameterValueInvalidFails() {
        this.parseStringInvalidCharacterFails("type/subtype;parameter=value/");
    }

    @Test
    public final void testParameterValueMissingFails() {
        this.parseStringMissingParameterValueFails("type/subtype;parameter");
    }

    @Test
    public final void testParameterValueMissingFails2() {
        this.parseStringMissingParameterValueFails("type/subtype;parameter=");
    }

    @Test
    public final void testParameterValueMissingFails3() {
        this.parseStringMissingParameterValueFails("type/subtype;p1=v1;p2");
    }

    @Test
    public final void testParameterValueMissingFails4() {
        this.parseStringMissingParameterValueFails("type/subtype;p1=v1;p2=");
    }

    @Test
    public final void testParameterValueUnclosedQuoteFails() {
        this.parseMissingClosingQuoteFails("type/subtype;parameter=\"");
    }

    @Test
    public final void testParameterValueQuotedInvalidFails() {
        this.parseStringInvalidCharacterFails("type/subtype;p=\"v\";[",
                '[');
    }

    @Test
    public final void testParameterValueQuotedInvalidFails2() {
        this.parseStringInvalidCharacterFails("type/subtype;p=\"v\"[",
                '[');
    }

    @Test
    public void testSeparatorFails() {
        this.parseStringInvalidCharacterFails(",");
    }

    @Test
    public void testSeparatorTypeSlashSubtypeFails() {
        this.parseStringInvalidCharacterFails(",type/subtype", ',');
    }

    @Test
    public final void testTypeSpaceSlashSubFails() {
        this.parseStringInvalidCharacterFails("a /b", ' ');
    }

    @Test
    public final void testTypeTabSlashSubFails() {
        this.parseStringInvalidCharacterFails("a\t/b", '\t');
    }

    @Test
    public final void testTypeSlashSpaceSubFails() {
        this.parseStringInvalidCharacterFails("a/ b", ' ');
    }

    @Test
    public final void testTypeSlashTabSubFails() {
        this.parseStringInvalidCharacterFails("a/\tb", '\t');
    }

    @Test
    public final void testTypeSlashSubType() {
        this.parseStringAndCheck(TYPE + "/" + SUBTYPE,
                TYPE,
                SUBTYPE);
    }

    @Test
    public final void testTypeWildcardSlashSubType() {
        this.parseStringAndCheck("*/*",
                MediaType.WILDCARD.string(),
                MediaType.WILDCARD.string());
    }

    @Test
    public final void testTypeSlashSubType2() {
        this.parseStringAndCheck("a/b",
                "a",
                "b");
    }

    @Test
    public final void testSpaceTypeSlashSub() {
        this.parseStringAndCheck(" a/b",
                "a",
                "b");
    }

    @Test
    public final void testTabTypeSlashSub() {
        this.parseStringAndCheck("\ta/b",
                "a",
                "b");
    }

    @Test
    public final void testTypeSlashSubTypeWildcard() {
        this.parseStringAndCheck("a/*",
                "a",
                MediaType.WILDCARD.string());
    }

    @Test
    public final void testTypeSlashSubTypeSpace() {
        this.parseStringAndCheck("a/b ",
                "a",
                "b");
    }

    @Test
    public final void testTypeSlashSubTypeTab() {
        this.parseStringAndCheck("a/b\t",
                "a",
                "b");
    }

    @Test
    public final void testTypeSlashSubTypeCrFails() {
        this.parseStringInvalidCharacterFails("a/b\r");
    }

    @Test
    public final void testTypeSlashSubTypeCrNlFails() {
        this.parseStringInvalidCharacterFails("a/b\r\n");
    }

    @Test
    public final void testTypeSlashSubTypeCrNlNonWhitespaceFails() {
        this.parseStringInvalidCharacterFails("a/b\r\n!");
    }

    @Test
    public final void testTypeSlashSubTypeCrNlSpace() {
        this.parseStringAndCheck("a/b\r\n ",
                "a",
                "b");
    }

    @Test
    public final void testTypeSlashSubTypeCrNlTab() {
        this.parseStringAndCheck("a/b\r\n\t",
                "a",
                "b");
    }

    @Test
    public final void testTypeSlashSubTypeSpaceSpace() {
        this.parseStringAndCheck("a/b  ",
                "a",
                "b");
    }

    @Test
    public final void testTypeSlashSubTypeSpaceTab() {
        this.parseStringAndCheck("a/b \t",
                "a",
                "b");
    }

    @Test
    public final void testTypeSlashSubTypeTabTab() {
        this.parseStringAndCheck("a/b\t\t",
                "a",
                "b");
    }

    @Test
    public final void testTypeSlashSubTypeTabSpace() {
        this.parseStringAndCheck("abc/def\t ",
                "abc",
                "def");
    }

    @Test
    public final void testTypeSlashSubTypePlusSuffix() {
        this.parseStringAndCheck("abc/def+suffix",
                "abc",
                "def+suffix");
    }

    @Test
    public final void testTypeSlashSubTypePrefixSuffix() {
        this.parseStringAndCheck("a/b+suffix",
                "a",
                "b+suffix");
    }

    @Test
    public final void testTypeSlashVendorDotSubType() {
        this.parseStringAndCheck("a/vnd.b",
                "a",
                "vnd.b");
    }

    @Test
    public final void testTypeSlashSubTypeParameter() {
        this.parseStringAndCheck(TYPE + "/" + SUBTYPE + ";p=v",
                TYPE,
                SUBTYPE,
                parameters("p", "v"));
    }

    @Test
    public final void testTypeSlashSubTypeParameter2() {
        this.parseStringAndCheck(TYPE + "/" + SUBTYPE + ";parameter=value",
                TYPE,
                SUBTYPE,
                parameters("parameter", "value"));
    }

    @Test
    public final void testTypeSlashSubTypeParameterQuotedValue() {
        this.parseStringAndCheck(TYPE + "/" + SUBTYPE + ";parameter=\"value\"",
                TYPE,
                SUBTYPE,
                parameters("parameter", "value"));
    }

    @Test
    public final void testTypeSlashSubTypeParameterParameterValueQuotedParameterValue() {
        final String text = TYPE + "/" + SUBTYPE + ";parameter=\"value\"\"q\"";
        this.parseStringInvalidCharacterFails(text, text.indexOf('q') - 1);
    }

    @Test
    public final void testTypeSlashSubTypeParameterSpace() {
        this.parseStringAndCheck(TYPE + "/" + SUBTYPE + ";parameter=value ",
                TYPE,
                SUBTYPE,
                parameters("parameter", "value"));
    }

    @Test
    public final void testTypeSlashSubTypeParameterTab() {
        this.parseStringAndCheck(TYPE + "/" + SUBTYPE + ";parameter=value\t",
                TYPE,
                SUBTYPE,
                parameters("parameter", "value"));
    }

    @Test
    public final void testTypeSlashSubTypeParameterCrFails() {
        this.parseStringInvalidCharacterFails(TYPE + "/" + SUBTYPE + ";parameter=value\r");
    }

    @Test
    public final void testTypeSlashSubTypeParameterCrNlFails() {
        this.parseStringInvalidCharacterFails(TYPE + "/" + SUBTYPE + ";parameter=value\r\n");
    }

    @Test
    public final void testTypeSlashSubTypeParameterCrNlNonWhitespaceFails() {
        this.parseStringInvalidCharacterFails(TYPE + "/" + SUBTYPE + ";parameter=value\r\n!");
    }

    @Test
    public final void testTypeSlashSubTypeParameterSpaceTabCrNlSpaceTab() {
        this.parseStringAndCheck(TYPE + "/" + SUBTYPE + ";parameter=value \t\r\n \t",
                TYPE,
                SUBTYPE,
                parameters("parameter", "value"));
    }

    @Test
    public final void testTypeSlashSubTypeQuotedParameterValue() {
        this.parseStringAndCheck(TYPE + "/" + SUBTYPE + ";parameter=\"value\"",
                TYPE,
                SUBTYPE,
                parameters("parameter", "value"));
    }

    @Test
    public final void testTypeSlashSubTypeQuotedParameterValueSpace() {
        this.parseStringAndCheck(TYPE + "/" + SUBTYPE + ";parameter=\"value\" ",
                TYPE,
                SUBTYPE,
                parameters("parameter", "value"));
    }

    @Test
    public final void testTypeSlashSubTypeQuotedParameterValueTab() {
        this.parseStringAndCheck(TYPE + "/" + SUBTYPE + ";parameter=\"value\" ",
                TYPE,
                SUBTYPE,
                parameters("parameter", "value"));
    }

    @Test
    public final void testTypeSlashSubTypeQuotedParameterValueSpaceTabSpaceTab() {
        this.parseStringAndCheck(TYPE + "/" + SUBTYPE + ";parameter=\"value\" \t \t",
                TYPE,
                SUBTYPE,
                parameters("parameter", "value"));
    }

    @Test
    public final void testTypeSlashSubTypeQuotedParameterValueOtherwiseInvalidCharacters() {
        this.parseStringAndCheck(TYPE + "/" + SUBTYPE + ";parameter=\"val?ue\"",
                TYPE,
                SUBTYPE,
                parameters("parameter", "val?ue"));
    }

    @Test
    public final void testTypeSlashSubTypeQuotedParameterValueBackslashEscaped() {
        this.parseStringAndCheck(TYPE + "/" + SUBTYPE + ";parameter=\"val\\\\ue\"",
                TYPE,
                SUBTYPE,
                parameters("parameter", "val\\ue"));
    }

    @Test
    public final void testTypeSlashSubTypeQuotedParameterValueEscapedDoubleQuote() {
        this.parseStringAndCheck(TYPE + "/" + SUBTYPE + ";parameter=\"val\\\"ue\"",
                TYPE,
                SUBTYPE,
                parameters("parameter", "val\"ue"));
    }

    @Test
    public final void testTypeSlashSubTypeQuotedParameterValueParameter2() {
        this.parseStringAndCheck(TYPE + "/" + SUBTYPE + ";p1=\"v1\";p2=v2",
                TYPE,
                SUBTYPE,
                parameters("p1", "v1", "p2", "v2"));
    }

    @Test
    public final void testTypeSlashSubTypeSpaceParameter() {
        this.parseStringAndCheck(TYPE + "/" + SUBTYPE + "; p=v",
                TYPE,
                SUBTYPE,
                parameters("p", "v"));
    }

    @Test
    public final void testTypeSlashSubTypeTabParameter() {
        this.parseStringAndCheck(TYPE + "/" + SUBTYPE + ";\tp=v",
                TYPE,
                SUBTYPE,
                parameters("p", "v"));
    }

    @Test
    public final void testTypeSlashSubTypeCrParameterFails() {
        this.parseStringInvalidCharacterFails(TYPE + "/" + SUBTYPE + ";\rq=v", 'q');
    }

    @Test
    public final void testTypeSlashSubTypeCrNlParameterFails() {
        this.parseStringInvalidCharacterFails(TYPE + "/" + SUBTYPE + ";\r\nq=v", 'q');
    }

    @Test
    public final void testTypeSlashSubTypeSpaceTabSpaceTabParameter2() {
        this.parseStringAndCheck(TYPE + "/" + SUBTYPE + "; \t \tp=v",
                TYPE,
                SUBTYPE,
                parameters("p", "v"));
    }

    @Test
    public final void testTypeSlashSubTypeParameterParameter() {
        this.parseStringAndCheck(TYPE + "/" + SUBTYPE + ";p=v;p2=v2",
                TYPE,
                SUBTYPE,
                parameters("p", "v", "p2", "v2"));
    }

    @Test
    public final void testTypeSlashSubTypeSpaceTabCrNlSpaceTabParameter() {
        this.parseStringAndCheck(TYPE + "/" + SUBTYPE + ";p=v; \t\r\n \tp2=v2",
                TYPE,
                SUBTYPE,
                parameters("p", "v", "p2", "v2"));
    }

    @Test
    public void testParameterBoundary() {
        this.parseStringAndCheck("type/subtype;boundary=\"abc-123\"",
                TYPE,
                SUBTYPE,
                parameters("boundary", MediaTypeBoundary.with("abc-123")));
    }

    @Test
    public void testParameterCharset() {
        this.parseStringAndCheck("type/subtype;charset=utf-8",
                TYPE,
                SUBTYPE,
                parameters("charset", CharsetName.UTF_8));
    }

    final Map<MediaTypeParameterName<?>, Object> parameters(final String name, final Object value) {
        return Maps.of(MediaTypeParameterName.with(name), value);
    }

    final Map<MediaTypeParameterName<?>, Object> parameters(final String name,
                                                            final Object value,
                                                            final String name2,
                                                            final Object value2) {
        return Maps.of(MediaTypeParameterName.with(name), value,
                MediaTypeParameterName.with(name2), value2);
    }

    private void parseStringAndCheck(final String text, final String type, final String subtype) {
        this.check(MediaType.parse(text), type, subtype);
    }

    abstract void parseStringAndCheck(final String text,
                                final String type,
                                final String subtype,
                                final Map<MediaTypeParameterName<?>, Object> parameters);

    final void check(final MediaType mediaType, final String type, final String subtype) {
        check(mediaType, type, subtype, MediaType.NO_PARAMETERS);
    }

    final void check(final MediaType mediaType,
                     final String type,
                     final String subtype,
                     final Map<MediaTypeParameterName<?>, Object> parameters) {
        assertEquals(type, mediaType.type(), "type=" + mediaType);
        assertEquals(subtype, mediaType.subType(), "subType=" + mediaType);
        assertEquals(parameters, mediaType.parameters(), "parameters=" + mediaType);
    }

    @Override
    final String valueLabel() {
        return MediaTypeHeaderParser.MEDIATYPE;
    }
}
