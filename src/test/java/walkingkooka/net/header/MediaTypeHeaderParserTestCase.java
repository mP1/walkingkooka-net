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
import java.util.Optional;

public abstract class MediaTypeHeaderParserTestCase<P extends MediaTypeHeaderParser, V> extends HeaderParserWithParametersTestCase<P,
    V> {

    MediaTypeHeaderParserTestCase() {
        super();
    }

    // constants

    final static String TYPE = "type";
    final static String SUBTYPE = "subtype";

    @Test
    public final void testParseWildcardFails() {
        this.parseStringFails("*", "Missing sub type at 1 in \"*\"");
    }

    @Test
    public final void testParseKeyValueSeparatorFails() {
        this.parseStringInvalidCharacterFails("=");
    }

    @Test
    public void testParseParseSlashFails() {
        this.parseStringInvalidCharacterFails("/");
    }

    @Test
    public void testParseParseValueSeparatorFails() {
        this.parseStringInvalidCharacterFails(",");
    }

    @Test
    public final void testParseSlashSubtypeFails() {
        this.parseStringInvalidCharacterFails(
            "/subtype",
            '/'
        );
    }

    @Test
    public final void testParseTypeFails() {
        this.parseStringFails(
            "type",
            "Missing type at 4 in \"type\""
        );
    }

    @Test
    public final void testParseTypeSlashFails() {
        this.parseStringFails(
            "type/",
            "Missing sub type at 5 in \"type/\""
        );
    }

    @Test
    public final void testParseTypeInvalidCharacterFails() {
        this.parseStringInvalidCharacterFails(
            "prima?ry/subtype",
            '?'
        );
    }

    @Test
    public final void testParseTypeSlashSubTypeMissingFails() {
        this.parseStringInvalidCharacterFails("type/;");
    }

    @Test
    public final void testParseTypeSlashSubTypeSpaceInvalidFails() {
        this.parseStringInvalidCharacterFails(
            "type/subtype Q",
            'Q'
        );
    }

    @Test
    public final void testParseTypeSlashSubTypeTabInvalidFails() {
        this.parseStringInvalidCharacterFails(
            "type/subtype\tQ",
            'Q'
        );
    }

    @Test
    public final void testParseParameterNameInvalidFails() {
        this.parseStringInvalidCharacterFails("type/subtype;parameter;");
    }

    @Test
    public final void testParseParameterValueInvalidFails() {
        this.parseStringInvalidCharacterFails("type/subtype;parameter=value/");
    }

    @Test
    public final void testParseParameterValueMissingFails() {
        this.parseStringMissingParameterValueFails("type/subtype;parameter");
    }

    @Test
    public final void testParseParameterValueMissingFails2() {
        this.parseStringMissingParameterValueFails("type/subtype;parameter=");
    }

    @Test
    public final void testParseParameterValueMissingFails3() {
        this.parseStringMissingParameterValueFails("type/subtype;p1=v1;p2");
    }

    @Test
    public final void testParseParameterValueMissingFails4() {
        this.parseStringMissingParameterValueFails("type/subtype;p1=v1;p2=");
    }

    @Test
    public final void testParseParameterValueUnclosedQuoteFails() {
        this.parseMissingClosingQuoteFails("type/subtype;parameter=\"");
    }

    @Test
    public final void testParseParameterValueQuotedInvalidFails() {
        this.parseStringInvalidCharacterFails(
            "type/subtype;p=\"v\";[",
            '['
        );
    }

    @Test
    public final void testParseParameterValueQuotedInvalidFails2() {
        this.parseStringInvalidCharacterFails(
            "type/subtype;p=\"v\"[",
            '['
        );
    }

    @Test
    public void testParseParseSeparatorFails() {
        this.parseStringInvalidCharacterFails(",");
    }

    @Test
    public void testParseParseSeparatorTypeSlashSubtypeFails() {
        this.parseStringInvalidCharacterFails(
            ",type/subtype",
            ','
        );
    }

    @Test
    public final void testParseTypeSpaceSlashSubFails() {
        this.parseStringInvalidCharacterFails(
            "a /b",
            ' '
        );
    }

    @Test
    public final void testParseTypeTabSlashSubFails() {
        this.parseStringInvalidCharacterFails(
            "a\t/b",
            '\t'
        );
    }

    @Test
    public final void testParseTypeSlashSpaceSubFails() {
        this.parseStringInvalidCharacterFails(
            "a/ b",
            ' '
        );
    }

    @Test
    public final void testParseTypeSlashTabSubFails() {
        this.parseStringInvalidCharacterFails(
            "a/\tb",
            '\t'
        );
    }

    @Test
    public final void testParseTypeSlashSubType() {
        this.parseStringAndCheck(
            TYPE + "/" + SUBTYPE,
            TYPE,
            SUBTYPE
        );
    }

    @Test
    public final void testParseTypeWildcardSlashSubType() {
        this.parseStringAndCheck(
            "*/*",
            MediaType.WILDCARD.string(),
            MediaType.WILDCARD.string()
        );
    }

    @Test
    public final void testParseTypeSlashSubType2() {
        this.parseStringAndCheck(
            "a/b",
            "a",
            "b"
        );
    }

    @Test
    public final void testParseSpaceTypeSlashSub() {
        this.parseStringAndCheck(
            " a/b",
            "a",
            "b"
        );
    }

    @Test
    public final void testParseTabTypeSlashSub() {
        this.parseStringAndCheck(
            "\ta/b",
            "a",
            "b"
        );
    }

    @Test
    public final void testParseTypeSlashSubTypeWildcard() {
        this.parseStringAndCheck(
            "a/*",
            "a",
            MediaType.WILDCARD.string()
        );
    }

    @Test
    public final void testParseTypeSlashSubTypeSpace() {
        this.parseStringAndCheck(
            "a/b ",
            "a",
            "b"
        );
    }

    @Test
    public final void testParseTypeSlashSubTypeTab() {
        this.parseStringAndCheck(
            "a/b\t",
            "a",
            "b"
        );
    }

    @Test
    public final void testParseTypeSlashSubTypeCrFails() {
        this.parseStringInvalidCharacterFails("a/b\r");
    }

    @Test
    public final void testParseTypeSlashSubTypeCrNlFails() {
        this.parseStringInvalidCharacterFails("a/b\r\n");
    }

    @Test
    public final void testParseTypeSlashSubTypeCrNlNonWhitespaceFails() {
        this.parseStringInvalidCharacterFails("a/b\r\n!");
    }

    @Test
    public final void testParseTypeSlashSubTypeCrNlSpace() {
        this.parseStringAndCheck(
            "a/b\r\n ",
            "a",
            "b"
        );
    }

    @Test
    public final void testParseTypeSlashSubTypeCrNlTab() {
        this.parseStringAndCheck(
            "a/b\r\n\t",
            "a",
            "b"
        );
    }

    @Test
    public final void testParseTypeSlashSubTypeSpaceSpace() {
        this.parseStringAndCheck(
            "a/b  ",
            "a",
            "b"
        );
    }

    @Test
    public final void testParseTypeSlashSubTypeSpaceTab() {
        this.parseStringAndCheck(
            "a/b \t",
            "a",
            "b"
        );
    }

    @Test
    public final void testParseTypeSlashSubTypeTabTab() {
        this.parseStringAndCheck("a/b\t\t",
            "a",
            "b");
    }

    @Test
    public final void testParseTypeSlashSubTypeTabSpace() {
        this.parseStringAndCheck(
            "abc/def\t ",
            "abc",
            "def"
        );
    }

    @Test
    public final void testParseTypeSlashSubTypePlusSuffix() {
        this.parseStringAndCheck(
            "abc/def+suffix123",
            "abc",
            "def",
            Optional.of("suffix123"),
            MediaType.NO_PARAMETERS
        );
    }

    @Test
    public final void testParseTypeSlashSubTypePrefixSuffix() {
        this.parseStringAndCheck(
            "a/b+suffix123",
            "a",
            "b",
            Optional.of("suffix123"),
            MediaType.NO_PARAMETERS
        );
    }

    @Test
    public final void testParseTypeSlashVendorDotSubType() {
        this.parseStringAndCheck(
            "a/vnd.b",
            "a",
            "vnd.b"
        );
    }

    @Test
    public final void testParseTypeSlashSubTypeParameter() {
        this.parseStringAndCheck(
            TYPE + "/" + SUBTYPE + ";p=v",
            TYPE,
            SUBTYPE,
            parameters("p", "v")
        );
    }

    @Test
    public final void testParseTypeSlashSubTypeParameter2() {
        this.parseStringAndCheck(
            TYPE + "/" + SUBTYPE + ";parameter=value",
            TYPE,
            SUBTYPE,
            parameters("parameter", "value")
        );
    }

    @Test
    public final void testParseTypeSlashSubTypeParameterQuotedValue() {
        this.parseStringAndCheck(
            TYPE + "/" + SUBTYPE + ";parameter=\"value\"",
            TYPE,
            SUBTYPE,
            parameters("parameter", "value")
        );
    }

    @Test
    public final void testParseTypeSlashSubTypePlusSuffixParameter() {
        this.parseStringAndCheck(
            TYPE + "/" + SUBTYPE + "+suffix123;parameter=\"quoted-value-123\"",
            TYPE,
            SUBTYPE,
            Optional.of("suffix123"),
            parameters("parameter", "quoted-value-123")
        );
    }

    @Test
    public final void testParseTypeSlashSubTypePlusSuffixParameterParameter() {
        this.parseStringAndCheck(
            TYPE + "/" + SUBTYPE + "+suffix123;parameter1=value1;parameter2=value2",
            TYPE,
            SUBTYPE,
            Optional.of("suffix123"),
            parameters("parameter1", "value1", "parameter2", "value2")
        );
    }

    @Test
    public final void testParseTypeSlashSubTypeParameterParameterValueQuotedParameterValue() {
        final String text = TYPE + "/" + SUBTYPE + ";parameter=\"value\"\"q\"";

        this.parseStringInvalidCharacterFails(
            text,
            text.indexOf('q') - 1
        );
    }

    @Test
    public final void testParseTypeSlashSubTypeParameterSpace() {
        this.parseStringAndCheck(
            TYPE + "/" + SUBTYPE + ";parameter=value ",
            TYPE,
            SUBTYPE,
            parameters("parameter", "value")
        );
    }

    @Test
    public final void testParseTypeSlashSubTypeParameterTab() {
        this.parseStringAndCheck(
            TYPE + "/" + SUBTYPE + ";parameter=value\t",
            TYPE,
            SUBTYPE,
            parameters("parameter", "value")
        );
    }

    @Test
    public final void testParseTypeSlashSubTypeParameterCrFails() {
        this.parseStringInvalidCharacterFails(TYPE + "/" + SUBTYPE + ";parameter=value\r");
    }

    @Test
    public final void testParseTypeSlashSubTypeParameterCrNlFails() {
        this.parseStringInvalidCharacterFails(TYPE + "/" + SUBTYPE + ";parameter=value\r\n");
    }

    @Test
    public final void testParseTypeSlashSubTypeParameterCrNlNonWhitespaceFails() {
        this.parseStringInvalidCharacterFails(TYPE + "/" + SUBTYPE + ";parameter=value\r\n!");
    }

    @Test
    public final void testParseTypeSlashSubTypeParameterSpaceTabCrNlSpaceTab() {
        this.parseStringAndCheck(
            TYPE + "/" + SUBTYPE + ";parameter=value \t\r\n \t",
            TYPE,
            SUBTYPE,
            parameters("parameter", "value")
        );
    }

    @Test
    public final void testParseTypeSlashSubTypeQuotedParameterValue() {
        this.parseStringAndCheck(
            TYPE + "/" + SUBTYPE + ";parameter=\"value\"",
            TYPE,
            SUBTYPE,
            parameters("parameter", "value")
        );
    }

    @Test
    public final void testParseTypeSlashSubTypeQuotedParameterValueSpace() {
        this.parseStringAndCheck(
            TYPE + "/" + SUBTYPE + ";parameter=\"value\" ",
            TYPE,
            SUBTYPE,
            parameters("parameter", "value")
        );
    }

    @Test
    public final void testParseTypeSlashSubTypeQuotedParameterValueTab() {
        this.parseStringAndCheck(
            TYPE + "/" + SUBTYPE + ";parameter=\"value\" ",
            TYPE,
            SUBTYPE,
            parameters("parameter", "value")
        );
    }

    @Test
    public final void testParseTypeSlashSubTypeQuotedParameterValueSpaceTabSpaceTab() {
        this.parseStringAndCheck(
            TYPE + "/" + SUBTYPE + ";parameter=\"value\" \t \t",
            TYPE,
            SUBTYPE,
            parameters("parameter", "value")
        );
    }

    @Test
    public final void testParseTypeSlashSubTypeQuotedParameterValueOtherwiseInvalidCharacters() {
        this.parseStringAndCheck(
            TYPE + "/" + SUBTYPE + ";parameter=\"val?ue\"",
            TYPE,
            SUBTYPE,
            parameters("parameter", "val?ue")
        );
    }

    @Test
    public final void testParseTypeSlashSubTypeQuotedParameterValueBackslashEscaped() {
        this.parseStringAndCheck(
            TYPE + "/" + SUBTYPE + ";parameter=\"val\\\\ue\"",
            TYPE,
            SUBTYPE,
            parameters("parameter", "val\\ue")
        );
    }

    @Test
    public final void testParseTypeSlashSubTypeQuotedParameterValueEscapedDoubleQuote() {
        this.parseStringAndCheck(
            TYPE + "/" + SUBTYPE + ";parameter=\"val\\\"ue\"",
            TYPE,
            SUBTYPE,
            parameters("parameter", "val\"ue")
        );
    }

    @Test
    public final void testParseTypeSlashSubTypeQuotedParameterValueParameter2() {
        this.parseStringAndCheck(
            TYPE + "/" + SUBTYPE + ";p1=\"v1\";p2=v2",
            TYPE,
            SUBTYPE,
            parameters("p1", "v1", "p2", "v2")
        );
    }

    @Test
    public final void testParseTypeSlashSubTypeSpaceParameter() {
        this.parseStringAndCheck(
            TYPE + "/" + SUBTYPE + "; p=v",
            TYPE,
            SUBTYPE,
            parameters("p", "v")
        );
    }

    @Test
    public final void testParseTypeSlashSubTypeTabParameter() {
        this.parseStringAndCheck(
            TYPE + "/" + SUBTYPE + ";\tp=v",
            TYPE,
            SUBTYPE,
            parameters("p", "v")
        );
    }

    @Test
    public final void testParseTypeSlashSubTypeCrParameterFails() {
        this.parseStringInvalidCharacterFails(
            TYPE + "/" + SUBTYPE + ";\rq=v",
            'q'
        );
    }

    @Test
    public final void testParseTypeSlashSubTypeCrNlParameterFails() {
        this.parseStringInvalidCharacterFails(
            TYPE + "/" + SUBTYPE + ";\r\nq=v", 'q'
        );
    }

    @Test
    public final void testParseTypeSlashSubTypeSpaceTabSpaceTabParameter2() {
        this.parseStringAndCheck(
            TYPE + "/" + SUBTYPE + "; \t \tp=v",
            TYPE,
            SUBTYPE,
            parameters("p", "v")
        );
    }

    @Test
    public final void testParseTypeSlashSubTypeParameterParameter() {
        this.parseStringAndCheck(TYPE + "/" + SUBTYPE + ";p=v;p2=v2",
            TYPE,
            SUBTYPE,
            parameters("p", "v", "p2", "v2")
        );
    }

    @Test
    public final void testParseTypeSlashSubTypeSpaceTabCrNlSpaceTabParameter() {
        this.parseStringAndCheck(
            TYPE + "/" + SUBTYPE + ";p=v; \t\r\n \tp2=v2",
            TYPE,
            SUBTYPE,
            parameters("p", "v", "p2", "v2")
        );
    }

    @Test
    public void testParseParseParameterBoundary() {
        this.parseStringAndCheck(
            "type/subtype;boundary=\"abc-123\"",
            TYPE,
            SUBTYPE,
            parameters(
                "boundary",
                MediaTypeBoundary.with("abc-123")
            )
        );
    }

    @Test
    public void testParseParseParameterCharset() {
        this.parseStringAndCheck(
            "type/subtype;charset=utf-8",
            TYPE,
            SUBTYPE,
            parameters("charset", CharsetName.UTF_8)
        );
    }

    static Map<MediaTypeParameterName<?>, Object> parameters(final String name,
                                                             final Object value) {
        return Maps.of(
            MediaTypeParameterName.with(name),
            value
        );
    }

    static Map<MediaTypeParameterName<?>, Object> parameters(final String name,
                                                            final Object value,
                                                            final String name2,
                                                            final Object value2) {
        return Maps.of(
            MediaTypeParameterName.with(name),
            value,
            MediaTypeParameterName.with(name2),
            value2
        );
    }

    private void parseStringAndCheck(final String text,
                                     final String type,
                                     final String subtype) {
        this.check(
            MediaType.parse(text),
            type,
            subtype
        );
    }

    private void parseStringAndCheck(final String text,
                                     final String type,
                                     final String subtype,
                                     final Map<MediaTypeParameterName<?>, Object> parameters) {
        this.parseStringAndCheck(
            text,
            type,
            subtype,
            MediaType.NO_SUFFIX,
            parameters
        );
    }

    abstract void parseStringAndCheck(final String text,
                                      final String type,
                                      final String subtype,
                                      final Optional<String> suffix,
                                      final Map<MediaTypeParameterName<?>, Object> parameters);

    final void check(final MediaType mediaType,
                     final String type,
                     final String subtype) {
        check(
            mediaType,
            type,
            subtype,
            MediaType.NO_SUFFIX,
            MediaType.NO_PARAMETERS
        );
    }

    final void check(final MediaType mediaType,
                     final String type,
                     final String subtype,
                     final Optional<String> suffix,
                     final Map<MediaTypeParameterName<?>, Object> parameters) {
        this.checkEquals(type, mediaType.type(), "type=" + mediaType);
        this.checkEquals(subtype, mediaType.subType(), "subType=" + mediaType);
        this.checkEquals(suffix, mediaType.suffix(), "suffix=" + mediaType);
        this.checkEquals(parameters, mediaType.parameters(), "parameters=" + mediaType);
    }

    @Override final String valueLabel() {
        return MediaTypeHeaderParser.MEDIATYPE;
    }
}
