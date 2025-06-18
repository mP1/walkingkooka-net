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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import walkingkooka.InvalidCharacterException;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.map.Maps;
import walkingkooka.predicate.PredicateTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.test.ParseStringTesting;

import java.nio.charset.Charset;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

final public class MediaTypeTest extends HeaderWithParametersTestCase<MediaType, MediaTypeParameterName<?>>
    implements ParseStringTesting<MediaType>,
    PredicateTesting2<MediaType, MediaType> {

    // constants

    private final static String TYPE = "type";
    private final static String SUBTYPE = "subtype";
    private final static Optional<String> SUFFIX = Optional.empty();
    private final static String PARAMETER_NAME = "parameter123";
    private final static String PARAMETER_VALUE = "value456";

    // tests

    @Override
    public void testTypeNaming() {
    }

    @Test
    public void testWithNullTypeFails() {
        assertThrows(
            NullPointerException.class,
            () -> MediaType.with(null, SUBTYPE)
        );
    }

    @Test
    public void testWithEmptyTypeAndSubTypeFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> MediaType.with("", SUBTYPE)
        );
    }

    @Test
    public void testWithTypeContainsSlashFails() {
        assertThrows(
            InvalidCharacterException.class,
            () -> MediaType.with("ty/pe", SUBTYPE)
        );
    }

    @Test
    public void testWithTypeContainsInvalidCharacterFails() {
        assertThrows(
            InvalidCharacterException.class,
            () -> MediaType.with("ty?pe", SUBTYPE)
        );
    }

    @Test
    public void testWithNullSubTypeFails() {
        assertThrows(
            NullPointerException.class,
            () -> MediaType.with(TYPE, null)
        );
    }

    @Test
    public void testWithTypeAndEmptySubTypeFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> MediaType.with(TYPE, "")
        );
    }

    @Test
    public void testWithEmptyIncludesSlashFails() {
        assertThrows(
            InvalidCharacterException.class,
            () -> MediaType.with(TYPE, "s/ub")
        );
    }

    @Test
    public void testWithEmptyIncludesInvalidCharacterFails() {
        assertThrows(
            InvalidCharacterException.class,
            () -> MediaType.with(TYPE, "s?ub")
        );
    }

    @Test
    public void testWithTypeAndSubType() {
        this.check(
            MediaType.with(TYPE, SUBTYPE),
            TYPE,
            SUBTYPE
        );
    }

    @Test
    public void testWithTypeAndWildcardSubType() {
        this.check(
            MediaType.with(
                TYPE,
                MediaType.WILDCARD.string()
            ),
            TYPE,
            MediaType.WILDCARD.string()
        );
    }

    @Test
    public void testWithWildcardTypeAndWildcardSubType() {
        final String wildcard = MediaType.WILDCARD.string();
        this.check(MediaType.with(wildcard, wildcard),
            wildcard,
            wildcard);
    }

    // constants .......................................................................................................

    @Test
    public void testWithExistingConstant() {
        final MediaType constant = MediaType.APPLICATION_JAVASCRIPT;
        assertSame(
            constant,
            MediaType.with(
                constant.type()
                    .toUpperCase(),
                constant.subType().
                    toUpperCase()
            )
        );
    }

    @Test
    public void testWithExistingConstant2() {
        final MediaType constant = MediaType.APPLICATION_JAVASCRIPT;
        assertSame(
            constant,
            MediaType.with(
                constant.type()
                    .toLowerCase(),
                constant.subType()
                    .toLowerCase()
            )
        );
    }

    // parse ...........................................................................................................

    @Test
    @Override
    public void testParseStringEmptyFails() {
        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> MediaType.parse("")
        );
    }

    // setType .........................................................................................................

    @Test
    public void testSetTypeNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.mediaType()
                .setType(null)
        );
    }

    @Test
    public void testSetTypeEmptyFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> this.mediaType()
                .setType("")
        );
    }

    @Test
    public void testSetTypeInvalidCharacterFails() {
        assertThrows(
            InvalidCharacterException.class,
            () -> this.mediaType()
                .setType("type/")
        );
    }

    @Test
    public void testSetTypeInvalidCharacterFails2() {
        assertThrows(
            InvalidCharacterException.class,
            () -> this.mediaType()
                .setType("type?")
        );
    }

    @Test
    public void testSetTypeSame() {
        final MediaType mediaType = this.mediaType();
        assertSame(mediaType, mediaType.setType(TYPE));
    }

    @Test
    public void testSetTypeSameDifferentCase() {
        final MediaType mediaType = this.mediaType();
        this.checkNotEquals(TYPE, TYPE.toUpperCase());
        assertSame(mediaType, mediaType.setType(TYPE.toUpperCase()));
    }

    @Test
    public void testSetTypeDifferent() {
        final MediaType mediaType = this.mediaType();
        final String type = "different";
        final MediaType different = mediaType.setType(type);
        this.check(different, type, SUBTYPE, parameters());
    }

    // setSubType ......................................................................................................

    @Test
    public void testSetSubTypeNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.mediaType()
                .setSubType(null)
        );
    }

    @Test
    public void testSetSubTypeEmptyFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> this.mediaType()
                .setSubType("")
        );
    }

    @Test
    public void testSetSubTypeInvalidCharacterFails() {
        assertThrows(
            InvalidCharacterException.class,
            () -> this.mediaType()
                .setSubType("type/")
        );
    }

    @Test
    public void testSetSubTypeInvalidCharacterFails2() {
        assertThrows(
            InvalidCharacterException.class,
            () -> this.mediaType()
                .setSubType("type?")
        );
    }

    @Test
    public void testSetSubTypeSame() {
        final MediaType mediaType = this.mediaType();
        assertSame(
            mediaType,
            mediaType.setSubType(SUBTYPE)
        );
    }

    @Test
    public void testSetSubTypeSameDifferentCase() {
        final MediaType mediaType = this.mediaType();
        this.checkNotEquals(
            SUBTYPE,
            SUBTYPE.toUpperCase()
        );

        assertSame(
            mediaType,
            mediaType.setSubType(
                SUBTYPE.toUpperCase()
            )
        );
    }

    @Test
    public void testSetSubTypeDifferent() {
        final MediaType mediaType = this.mediaType();
        final String subtype = "different";
        final MediaType different = mediaType.setSubType(subtype);
        this.check(different, TYPE, subtype, parameters());
    }

    // setSuffix........................................................................................................

    @Test
    public void testSetSuffixNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.mediaType().setSuffix(null)
        );
    }

    @Test
    public void testSetSuffixSame() {
        final MediaType mediaType = MediaType.TEXT_PLAIN;

        assertSame(
            mediaType,
            mediaType.setSuffix(MediaType.NO_SUFFIX)
        );
    }

    @Test
    public void testSetSuffixDifferentAdds() {
        this.setSuffixAndCheck(
            MediaType.parse("image/svg"),
            Optional.of("xml")
        );
    }

    @Test
    public void testSetSuffixDifferentReplaces() {
        this.setSuffixAndCheck(
            MediaType.parse("image/svg+xml"),
            Optional.of("different")
        );
    }

    @Test
    public void testSetSuffixDifferentRemoves() {
        this.setSuffixAndCheck(
            MediaType.parse("image/svg+xml"),
            Optional.empty()
        );
    }

    private void setSuffixAndCheck(final MediaType mediaType,
                                   final Optional<String> suffix) {
        this.check(
            mediaType.setSuffix(suffix),
            mediaType.type(),
            mediaType.subType(),
            suffix,
            mediaType.parameters()
        );
    }

    // isText...........................................................................................................

    @Test
    public void testIsTextWithTextPlaintext() {
        this.isTextAndCheck(
            MediaType.TEXT_PLAIN,
            true
        );
    }

    @Test
    public void testIsTextWithTextXml() {
        this.isTextAndCheck(
            MediaType.TEXT_XML,
            true
        );
    }

    @Test
    public void testIsTextWithApplicationJson() {
        this.isTextAndCheck(
            MediaType.APPLICATION_JSON,
            true
        );
    }

    @Test
    public void testIsTextWithApplicationBinary() {
        this.isTextAndCheck(
            MediaType.BINARY,
            false
        );
    }

    private void isTextAndCheck(final MediaType mediaType,
                                final boolean expected) {
        this.checkEquals(
            expected,
            mediaType.isText(),
            mediaType::toString
        );
    }

    // setParameters ....................................................................................................

    @Test
    public void testSetParametersInvalidQFactor() {
        assertThrows(
            HeaderException.class,
            () -> this.mediaType()
                .setParameters(
                    Maps.of(
                        MediaTypeParameterName.Q,
                        -1.0f
                    )
                )
        );
    }

    @Test
    public void testSetParametersSameDifferentCase() {
        final MediaType mediaType = this.mediaType();

        final String parameter = PARAMETER_NAME.toUpperCase();
        this.checkNotEquals(parameter, PARAMETER_NAME);
        assertSame(mediaType, mediaType.setParameters(this.parameters(parameter, PARAMETER_VALUE)));
    }

    @Test
    public void testSetParametersDifferent() {
        final MediaType mediaType = this.mediaType();
        final Map<MediaTypeParameterName<?>, Object> parameters = MediaType.NO_PARAMETERS;
        final MediaType different = mediaType.setParameters(parameters);
        this.check(different, TYPE, SUBTYPE, parameters);
    }

    @Test
    public void testSetParametersDifferent2() {
        final MediaType mediaType = this.mediaType();
        final Map<MediaTypeParameterName<?>, Object> parameters = Maps.of(
            MediaTypeParameterName.with("different"),
            "value789"
        );
        final MediaType different = mediaType.setParameters(parameters);
        this.check(
            different,
            TYPE,
            SUBTYPE,
            parameters
        );
    }

    private void check(final MediaType mediaType, final String type, final String subtype) {
        this.check(mediaType, type, subtype, MediaType.NO_PARAMETERS);
    }

    private void check(final MediaType mediaType,
                       final String type,
                       final String subtype,
                       final Map<MediaTypeParameterName<?>, Object> parameters) {
        this.check(
            mediaType,
            type,
            subtype,
            MediaType.NO_SUFFIX,
            parameters
        );
    }

    private void check(final MediaType mediaType,
                       final String type,
                       final String subtype,
                       final Optional<String> suffix,
                       final Map<MediaTypeParameterName<?>, Object> parameters) {
        this.checkEquals(
            type,
            mediaType.type(),
            () -> "type=" + mediaType
        );

        this.checkEquals(
            subtype,
            mediaType.subType(),
            () -> "subType=" + mediaType
        );

        this.checkEquals(
            suffix,
            mediaType.suffix(),
            () -> "suffix " + mediaType
        );

        this.checkParameters(
            mediaType,
            parameters
        );
    }

    @Test
    public void testSetParametersConstant() {
        final MediaType constant = MediaType.TEXT_PLAIN;
        final MediaType withParameters = constant.setParameters(this.parameters());
        this.checkNotEquals(constant, withParameters);
        assertSame(constant, withParameters.setParameters(MediaType.NO_PARAMETERS));
    }

    // clearParameters .................................................................................................

    @Test
    public void testClearParametersWhenNoParameters() {
        final MediaType mediaType = MediaType.TEXT_PLAIN;

        assertSame(
            mediaType,
            mediaType.clearParameters()
        );
    }

    @Test
    public void testClearParametersWithParameters() {
        final MediaType mediaType = MediaType.TEXT_PLAIN;

        assertSame(
            mediaType,
            mediaType.setCharset(CharsetName.UTF_8)
                .clearParameters()
        );
    }

    @Test
    public void testClearParametersWithParameters2() {
        final MediaType mediaType = MediaType.parse("text/something");

        this.checkEquals(
            mediaType,
            mediaType.setCharset(CharsetName.UTF_8)
                .clearParameters()
        );
    }

    // setBoundary .....................................................................................................

    @Test
    public void testSetBoundaryWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> MediaType.TEXT_PLAIN.setBoundary(null)
        );
    }

    @Test
    public void testSetBoundary() {
        final MediaTypeBoundary boundary = MediaTypeBoundary.with("boundary123");

        this.check(
            MediaType.MULTIPART_FORM_DATA.setBoundary(boundary),
            "multipart",
            "form-data",
            Maps.of(MediaTypeParameterName.BOUNDARY, boundary)
        );
    }

    @Test
    public void testSetBoundarySame() {
        final MediaTypeBoundary boundary = MediaTypeBoundary.with("boundary123");
        final MediaType mediaType = MediaType.MULTIPART_FORM_DATA.setBoundary(boundary);

        assertSame(
            mediaType,
            mediaType.setBoundary(boundary)
        );
    }

    @Test
    public void testSetBoundaryDifferent() {
        final MediaType mediaType111 = MediaType.MULTIPART_FORM_DATA.setBoundary(
            MediaTypeBoundary.with("boundary111")
        );

        final MediaTypeBoundary differentBoundary = MediaTypeBoundary.with("boundary-different");
        final MediaType different = mediaType111.setBoundary(differentBoundary);

        this.checkEquals(
            MediaType.MULTIPART_FORM_DATA.setBoundary(differentBoundary),
            different
        );

        this.check(
            different,
            "multipart",
            "form-data",
            Maps.of(
                MediaTypeParameterName.BOUNDARY,
                differentBoundary
            )
        );
    }

    // setCharset ......................................................................................................

    @Test
    public void testSetCharsetNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> MediaType.TEXT_PLAIN.setCharset(null)
        );
    }

    @Test
    public void testSetCharset() {
        final CharsetName utf8 = CharsetName.UTF_8;
        this.check(
            MediaType.TEXT_PLAIN.setCharset(utf8),
            "text",
            "plain",
            Maps.of(MediaTypeParameterName.CHARSET, utf8)
        );
    }

    @Test
    public void testSetCharsetSame() {
        final CharsetName utf8 = CharsetName.UTF_8;
        final MediaType textPlainWithCharset = MediaType.TEXT_PLAIN.setCharset(utf8);
        assertSame(
            textPlainWithCharset,
            textPlainWithCharset.setCharset(utf8)
        );
    }

    @Test
    public void testSetCharsetDifferent() {
        final CharsetName utf8 = CharsetName.UTF_8;
        final MediaType with8 = MediaType.TEXT_PLAIN.setCharset(utf8);

        final CharsetName utf16 = CharsetName.UTF_16;
        final MediaType with16 = with8.setCharset(utf16);
        assertNotSame(with8, with16);

        this.check(with16,
            "text",
            "plain",
            Maps.of(MediaTypeParameterName.CHARSET, utf16));
        this.check(with8,
            "text",
            "plain",
            Maps.of(MediaTypeParameterName.CHARSET, utf8));
    }

    @Test
    public void testSetCharsetDifferent2() {
        final Map<MediaTypeParameterName<?>, Object> parameters = Maps.of(
            MediaTypeParameterName.CHARSET, CharsetName.UTF_8,
            MediaTypeParameterName.Q, 0.5f
        );

        final MediaType with8 = MediaType.TEXT_PLAIN.setParameters(parameters);

        final CharsetName utf16 = CharsetName.UTF_16;
        final MediaType with16 = with8.setCharset(utf16);

        assertNotSame(with8, with16);

        final Map<MediaTypeParameterName<?>, Object> parameters2 = Maps.of(MediaTypeParameterName.CHARSET, utf16,
            MediaTypeParameterName.Q, 0.5f);

        this.check(
            with16,
            "text",
            "plain",
            parameters2
        );

        this.check(
            with8,
            "text",
            "plain",
            parameters
        );
    }

    @Test
    public void testSetCharset2() {
        final Map<MediaTypeParameterName<?>, Object> parameters = Maps.of(
            MediaTypeParameterName.Q,
            0.5f
        );
        final MediaType without = MediaType.TEXT_PLAIN.setParameters(parameters);

        final CharsetName charset = CharsetName.UTF_16;
        final MediaType with16 = without.setCharset(charset);

        assertNotSame(without, with16);

        this.check(
            with16,
            "text",
            "plain",
            Maps.of(MediaTypeParameterName.CHARSET, charset,
                MediaTypeParameterName.Q, 0.5f)
        );

        this.check(
            without,
            "text",
            "plain",
            parameters
        );
    }

    // acceptCharset ...................................................................................................

    @Test
    public void testAcceptCharsetWithNullDefaultFails() {
        assertThrows(
            NullPointerException.class,
            () -> MediaType.ANY_TEXT.acceptCharset(null)
        );
    }

    @Test
    public void testAcceptCharsetPresent() {
        this.acceptCharsetAndCheck(
            "text/plain;charset=UTF-8",
            "UTF-16",
            "UTF-8"
        );
    }

    @Test
    public void testAcceptCharsetPresent2() {
        this.acceptCharsetAndCheck(
            "text/plain;q=0.5;charset=UTF-8",
            "UTF-16",
            "UTF-8"
        );
    }

    @Test
    public void testAcceptCharsetUnsupportedUsesDefault() {
        this.acceptCharsetAndCheck(
            "text/plain;charset=UTF99",
            "UTF-16",
            "UTF-16"
        );
    }

    @Test
    public void testAcceptCharsetUsesDefault() {
        this.acceptCharsetAndCheck(
            "text/plain;q=0.5",
            "UTF-16",
            "UTF-16"
        );
    }

    @Test
    public void testAcceptCharsetUsesDefault2() {
        this.acceptCharsetAndCheck(
            MediaType.BINARY.toHeaderText(),
            "UTF-16",
            "UTF-16"
        );
    }

    private void acceptCharsetAndCheck(final String text,
                                       final String defaultCharset,
                                       final String expected) {
        this.acceptCharsetAndCheck(
            text,
            Charset.forName(defaultCharset),
            Charset.forName(expected)
        );
    }

    private void acceptCharsetAndCheck(final String text,
                                       final Charset defaultCharset,
                                       final Charset expected) {
        this.checkEquals(
            expected,
            MediaType.parse(text).acceptCharset(defaultCharset),
            () -> "acceptCharset of " + text + " with defaultCharset " + defaultCharset
        );
    }

    // contentTypeCharset ..............................................................................................

    @Test
    public void testContentTypeCharsetWithNullDefaultFails() {
        assertThrows(
            NullPointerException.class,
            () -> MediaType.ANY_TEXT.contentTypeCharset(null)
        );
    }

    @Test
    public void testContentTypeCharsetPresent() {
        this.contentTypeCharsetAndCheck(
            "text/plain;charset=UTF-8",
            "UTF-16",
            "UTF-8"
        );
    }

    @Test
    public void testContentTypeCharsetPresent2() {
        this.contentTypeCharsetAndCheck(
            "text/plain;q=0.5;charset=UTF-8",
            "UTF-16",
            "UTF-8"
        );
    }

    @Test
    public void testContentTypeCharsetUnsupportedFails() {
        final IllegalArgumentException thrown = assertThrows(
            IllegalArgumentException.class,
            () -> MediaType.parse("text/plain;charset=UTF99")
                .contentTypeCharset(Charset.defaultCharset())
        );
        this.checkEquals(
            "Supported charset required was \"UTF99\"",
            thrown.getMessage()
        );
    }

    @Test
    public void testContentTypeCharsetUsesDefault() {
        this.contentTypeCharsetAndCheck(
            "text/plain;q=0.5",
            "UTF-16",
            "UTF-16"
        );
    }

    @Test
    public void testContentTypeCharsetUsesDefault2() {
        this.contentTypeCharsetAndCheck(
            MediaType.BINARY.toHeaderText(),
            "UTF-16",
            "UTF-16"
        );
    }

    private void contentTypeCharsetAndCheck(final String text,
                                            final String defaultCharset,
                                            final String expected) {
        this.contentTypeCharsetAndCheck(
            text,
            Charset.forName(defaultCharset),
            Charset.forName(expected)
        );
    }

    private void contentTypeCharsetAndCheck(final String text,
                                            final Charset defaultCharset,
                                            final Charset expected) {
        this.checkEquals(expected,
            MediaType.parse(text).contentTypeCharset(defaultCharset),
            () -> "contentTypeCharset of " + text + " with defaultCharset " + defaultCharset);
    }

    // qWeight .........................................................................................................

    @Test
    public void testQParameterPresent() {
        this.qualityFactorAndCheck(
            this.mediaType()
                .setParameters(parameters(
                        MediaTypeParameterName.Q.value(),
                        0.5f
                    )
                ),
            0.5f
        );
    }

    @Test
    public void testQParameterPresentInvalidFails() {
        assertThrows(
            HeaderException.class,
            () -> this.mediaType()
                .setParameters(parameters(
                        MediaTypeParameterName.Q.value(),
                        "XYZ"
                    )
                ).qualityFactor()
        );
    }

    @Test
    public void testQParameterAbsent() {
        this.qualityFactorAndCheck(
            this.mediaType(),
            MediaType.QUALITY_FACTOR_EMPTY
        );
    }

    private void qualityFactorAndCheck(final MediaType type,
                                       final float quality) {
        this.qualityFactorAndCheck(
            type,
            Optional.of(quality)
        );
    }

    private void qualityFactorAndCheck(final MediaType type,
                                       final Optional<Float> quality) {
        this.checkEquals(
            quality,
            type.qualityFactor(),
            () -> type + " quality factor"
        );
    }

    // parse ...........................................................................................................

    @Test
    public void testParse() {
        this.parseStringAndCheck(
            "type1/subtype1",
            MediaType.with("type1", "subtype1")
        );
    }

    @Test
    public void testParseSuffix() {
        this.parseStringAndCheck(
            "type1/subtype1+suffix1",
            MediaType.with(
                "type1",
                "subtype1"
            ).setSuffix(
                Optional.of("suffix1")
            )
        );
    }

    @Test
    public void testParseWithUnquotedParameter() {
        this.parseStringAndCheck("type1/subtype1;abc=def",
            MediaType.with("type1", "subtype1")
                .setParameters(
                    Maps.of(
                        MediaTypeParameterName.with("abc"),
                        "def"
                    )
                )
        );
    }

    @Test
    public void testParseWithQuotedParameter() {
        this.parseStringAndCheck(
            "type1/subtype1;abc=\"d,\\\\ef\"",
            MediaType.with("type1", "subtype1")
                .setParameters(
                    Maps.of(
                        MediaTypeParameterName.with("abc"),
                        "d,\\ef"
                    )
                )
        );
    }

    @Test
    public void testParseWithBoundary() {
        this.parseStringAndCheck(
            "type1/subtype1;boundary=def",
            MediaType.with("type1", "subtype1")
                .setBoundary(MediaTypeBoundary.with("def"))
        );
    }

    @Test
    public void testParseWithTitleStar() {
        this.parseStringAndCheck(
            "type1/subtype1;title*=UTF-8''abc%20123",
            MediaType.with("type1", "subtype1")
                .setParameters(
                    Maps.of(
                        MediaTypeParameterName.TITLE_STAR,
                        EncodedText.with(CharsetName.UTF_8,
                            EncodedText.NO_LANGUAGE,
                            "abc 123"
                        )
                    )
                )
        );
    }

    @Test
    public void testParseSuffixAndParameters() {
        this.parseStringAndCheck(
            "type1/subtype1+suffix1;abc=123",
            MediaType.with(
                "type1",
                "subtype1"
            ).setSuffix(
                Optional.of("suffix1")
            ).setParameters(
                Maps.of(
                    MediaTypeParameterName.with("abc"),
                    "123"
                )
            )
        );
    }

    // ParseStringTesting ..............................................................................................

    @Override
    public MediaType parseString(final String text) {
        return MediaType.parse(text);
    }

    // ParseList .......................................................................................................

    @Test
    public void testParseListNullFails() {
        assertThrows(NullPointerException.class, () -> MediaType.parseList(null));
    }

    @Test
    public void testParseListEmptyFails() {
        assertThrows(IllegalArgumentException.class, () -> MediaType.parseList(""));
    }

    @Test
    public void testParseList() {
        this.checkEquals(Lists.of(
                MediaType.with("type1", "subtype1"),
                MediaType.with("type2", "subtype2")),
            MediaType.parseList("type1/subtype1,type2/subtype2"));
    }

    // test ............................................................................................................

    @Test
    public void testTestNull() {
        this.testFalse(null);
    }

    @Test
    public void testTestWithSelfDifferentCase() {
        this.testTrue(
            MediaType.with("type", "subtype"),
            MediaType.with("TYPE", "SUBTYPE")
        );
    }

    @Test
    public void testTestAnyAlwaysMatches() {
        this.testTrue(
            MediaType.ALL,
            MediaType.with("custom", "custom2")
        );
    }

    @Test
    public void testTestAnyAndAnyMatches() {
        this.testTrue(
            MediaType.ALL,
            MediaType.ALL
        );
    }

    @Test
    public void testTestSameTypeWildcardSubType() {
        final String type = "custom";
        this.testTrue(
            MediaType.with(type, MediaType.WILDCARD.string()),
            MediaType.with(type, SUBTYPE)
        );
    }

    @Test
    public void testTestDifferentTypeWildcardSubType() {
        this.testFalse(MediaType.with("custom", MediaType.WILDCARD.string()),
            MediaType.with("different", SUBTYPE));
    }

    @Test
    public void testTestSameTypeSubType() {
        this.testTrue(
            this.mediaType(),
            this.mediaType()
        );
    }

    @Test
    public void testTestDifferentTypeSameSubType() {
        this.testFalse(
            this.mediaType(),
            MediaType.with("different", SUBTYPE)
        );
    }

    @Test
    public void testTestSameTypeDifferentSubType() {
        final String type = "type";
        this.testFalse(
            MediaType.with(
                type,
                "subtype"
            ),
            MediaType.with(
                type,
                "different"
            )
        );
    }

    @Override
    public MediaType createPredicate() {
        return this.mediaType();
    }

    // requireContentType...............................................................................................

    @Test
    public void testRequireContentTypeAllWithTextPlain() {
        MediaType.ALL.requireContentType(MediaType.TEXT_PLAIN);
    }

    @Test
    public void testRequireContentTypeTextAnyWithTextPlain() {
        MediaType.ANY_TEXT.requireContentType(MediaType.TEXT_PLAIN);
    }

    @Test
    public void testRequireContentTypeTextPlainWithTextPlain() {
        MediaType.TEXT_PLAIN.requireContentType(MediaType.TEXT_PLAIN);
    }

    @Test
    public void testRequireContentTypeTextPlainWithTextPlainCharset() {
        MediaType.TEXT_PLAIN.requireContentType(
            MediaType.TEXT_PLAIN.setCharset(CharsetName.UTF_8)
        );
    }

    @Test
    public void testRequireContentTypeMissingFails() {
        final IllegalArgumentException thrown = assertThrows(
            IllegalArgumentException.class,
            () -> MediaType.TEXT_PLAIN.requireContentType(null)
        );

        this.checkEquals(
            "Content-Type: Missing required text/plain",
            thrown.getMessage()
        );
    }

    @Test
    public void testRequireContentTypeTextPlainWithTextRichTextFails() {
        final IllegalArgumentException thrown = assertThrows(
            IllegalArgumentException.class,
            () -> MediaType.TEXT_PLAIN.requireContentType(MediaType.TEXT_RICHTEXT)
        );

        this.checkEquals(
            "Content-Type: Got text/richtext require text/plain",
            thrown.getMessage()
        );
    }

    @Test
    public void testRequireContentTypeRemovesParametersMissingFails() {
        final IllegalArgumentException thrown = assertThrows(
            IllegalArgumentException.class,
            () -> MediaType.TEXT_PLAIN.setCharset(CharsetName.UTF_8)
                .requireContentType(null)
        );

        this.checkEquals(
            "Content-Type: Missing required text/plain",
            thrown.getMessage()
        );
    }

    @Test
    public void testRequireContentTypeRemovesParametersMissingFails2() {
        final IllegalArgumentException thrown = assertThrows(
            IllegalArgumentException.class,
            () -> MediaType.TEXT_PLAIN.setCharset(CharsetName.UTF_8)
                .requireContentType(MediaType.parse("xxx/yyy;zzz=1"))
        );

        this.checkEquals(
            "Content-Type: Got xxx/yyy require text/plain",
            thrown.getMessage()
        );
    }

    // requireContentTypeMissingMessage.................................................................................

    @Test
    public void testRequireContentTypeMissingMessage() {
        this.requireContentTypeMissingMessageAndCheck(
            MediaType.BINARY,
            "Content-Type: Missing required application/octet-stream"
        );
    }

    @Test
    public void testRequireContentTypeMissingMessageWithParameters() {
        this.requireContentTypeMissingMessageAndCheck(
            MediaType.TEXT_PLAIN.setCharset(CharsetName.UTF_8),
            "Content-Type: Missing required text/plain"
        );
    }

    private void requireContentTypeMissingMessageAndCheck(final MediaType mediaType,
                                                          final String expected) {
        this.checkEquals(
            expected,
            mediaType.requireContentTypeMissingMessage()
        );
    }

    // toHeaderText.....................................................................................................

    @Test
    public void testToHeaderTextParse() {
        final String text = "type/subtype";

        this.toHeaderTextAndCheck(
            MediaType.parse(text),
            TYPE + "/" + SUBTYPE
        );
    }

    @Test
    public void testToHeaderTextParseWithParameters() {
        this.toHeaderTextAndCheck(
            MediaType.parse("type/subtype;a=b;c=d"),
            "type/subtype; a=b; c=d"
        );
    }

    @Test
    public void testToHeaderTextParseWithParametersWithQuotes() {
        this.toHeaderTextAndCheck(
            MediaType.parse("type/subtype;a=b;c=\"d e\""),
            "type/subtype; a=b; c=\"d e\""
        );
    }

    @Test
    public void testToHeaderText() {
        this.toHeaderTextAndCheck(
            MediaType.with(TYPE, SUBTYPE),
            TYPE + "/" + SUBTYPE
        );
    }

    @Test
    public void testToHeaderTextWithParameters() {
        this.toHeaderTextAndCheck(
            this.mediaType(),
            TYPE + "/" + SUBTYPE + "; parameter123=value456"
        );
    }

    @Test
    public void testToHeaderTextWithParametersRequireQuotesWhitespace() {
        this.toHeaderTextAndCheck(
            MediaType.with(TYPE, SUBTYPE)
                .setParameters(parameters("a", "b c")
                ),
            "type/subtype; a=\"b c\""
        );
    }

    @Test
    public void testToHeaderTextWithParametersRequireQuotesBackslash() {
        this.toHeaderTextAndCheck(
            MediaType.with(TYPE, SUBTYPE)
                .setParameters(parameters("a", "b\\c")
                ),
            "type/subtype; a=\"b\\\\c\""
        );
    }

    @Test
    public void testIsWildcardAll() {
        this.isWildcardAndCheck(MediaType.ALL, true);
    }

    @Test
    public void testIsWildcardNotAll() {
        this.isWildcardAndCheck(false);
    }

    // toHeaderTextList.................................................................................................

    @Test
    public void testToHeaderTextListOne() {
        this.toHeaderTextListAndCheck(
            "type1/subtype1",
            MediaType.with("type1", "subtype1")
        );
    }

    @Test
    public void testToHeaderTextListMany() {
        this.toHeaderTextListAndCheck(
            "type1/subtype1, type2/subtype2",
            MediaType.with("type1", "subtype1"),
            MediaType.with("type2", "subtype2")
        );
    }

    // accept...........................................................................................................

    @Test
    public void testAccept() {
        this.acceptAndCheck(MediaType.APPLICATION_JSON);
    }

    @Test
    public void testAcceptAny() {
        this.acceptAndCheck(MediaType.ALL);
    }

    private void acceptAndCheck(final MediaType mediaType) {
        this.checkEquals(Accept.with(Lists.of(mediaType)), mediaType.accept());
    }

    // isNonSpecific.....................................................................................................

    @Test
    public void testIsNonStandardApplicationOctet() {
        this.isNonSpecificAndCheck(
            "application/octet-stream",
            false
        );
    }

    @Test
    public void testIsNonStandardMicrosoftExcel() {
        this.isNonSpecificAndCheck(
            "application/vnd.ms-excel",
            false
        );
    }

    @Test
    public void testIsNonStandardapplicationDashLittleXWwwFormUrlencodedl() {
        this.isNonSpecificAndCheck(
            "application/x-www-form-urlencoded",
            true
        );
    }

    @Test
    public void testIsNonStandardapplicationDashBigXWwwFormUrlencodedl() {
        this.isNonSpecificAndCheck(
            "application/X-www-form-urlencoded",
            true
        );
    }

    private void isNonSpecificAndCheck(final String mimeType,
                                       final boolean expected) {
        this.checkEquals(
            expected,
            MediaType.parse(mimeType).isNonStandard(),
            mimeType
        );
    }

    // isVendorType.....................................................................................................

    @Test
    public void testIsVendorSpecificApplicationOctet() {
        this.isVendorTypeAndCheck(
            "application/octet-stream",
            false
        );
    }

    @Test
    public void testIsVendorSpecificMicrosoftExcel() {
        this.isVendorTypeAndCheck(
            "application/vnd.ms-excel",
            true
        );
    }

    private void isVendorTypeAndCheck(final String mimeType,
                                      final boolean expected) {
        this.checkEquals(
            expected,
            MediaType.parse(mimeType).isVendorSpecific(),
            mimeType
        );
    }

    // HashCodeEqualsDefined ...........................................................................................

    @Test
    public void testEqualsTypeDifferentCase() {
        this.checkEqualsAndHashCode(MediaType.with("major", "minor"),
            MediaType.with("MAJOR", "minor"));
    }

    @Test
    public void testEqualsSubTypeDifferentCase() {
        this.checkEqualsAndHashCode(MediaType.with("major", "MINOR"),
            MediaType.parse("major/MINOR"));
    }

    @Test
    public void testEqualsParameterNameDifferentCase() {
        this.checkEqualsAndHashCode(MediaType.parse("type/subType; parameter123=value456"),
            MediaType.parse("type/subType; PARAMETER123=value456"));
    }

    @Test
    public void testEqualsParameterValueDifferentCase() {
        this.checkNotEquals(MediaType.parse("type/subType; parameter123=value456"),
            MediaType.parse("type/subType; parameter123=VALUE456"));
    }

    @Test
    public void testEqualsDifferentMimeType() {
        this.checkNotEquals(MediaType.parse("major/different"));
    }

    @Test
    public void testEqualsDifferentSuffix() {
        this.checkNotEquals(
            MediaType.parse("major/minor+suffix"),
            MediaType.parse("major/minor")
        );
    }

    @Test
    public void testEqualsDifferentSuffix2() {
        this.checkNotEquals(
            MediaType.parse("major/minor+suffix"),
            MediaType.parse("major/minor+different-suffix")
        );
    }

    @Test
    public void testEqualsDifferentSuffix3() {
        this.checkNotEquals(
            MediaType.parse("major/minor+suffix"),
            MediaType.parse("major/minor+different-suffix-1+different-suffix-2")
        );
    }

    @Test
    public void testEqualsDifferentParameter() {
        this.checkNotEquals(MediaType.parse("major/minor;parameter=value"));
    }

    @Test
    public void testEqualsDifferentParameter2() {
        checkNotEquals(MediaType.parse("major/minor;parameter=value"),
            MediaType.parse("major/minor;different=value"));
    }

    @Test
    public void testEqualsDifferentParameterOrderStillEqual() {
        checkEqualsAndHashCode(MediaType.parse("a/b;x=1;y=2"),
            MediaType.parse("a/b;y=2;x=1"));
    }

    @Test
    public void testEqualsParsedAndBuild() {
        checkEqualsAndHashCode(MediaType.with("major", "minor"), MediaType.parse("major/minor"));
    }

    @Test
    public void testEqualsParameterOrderIrrelevant() {
        checkEqualsAndHashCode(MediaType.parse("type/subtype;a=1;b=2;c=3"), MediaType.parse("type/subtype;c=3;b=2;a=1"));
    }

    @Test
    public void testEqualsDifferentWhitespaceSameParametersStillEqual() {
        checkEqualsAndHashCode(MediaType.parse("a/b;   x=1"), MediaType.parse("a/b;x=1"));
    }

    // equalsIgnoringParameters.........................................................................................

    @Test
    public void testEqualsIgnoringParametersDifferent2() {
        this.equalsIgnoringParametersAndCheck(
            MediaType.parse("major/minor"),
            MediaType.parse("different/different2"),
            false);
    }

    @Test
    public void testEqualsIgnoringParametersDifferentParameters() {
        this.equalsIgnoringParametersAndCheck(
            MediaType.parse("major/minor;q=1"),
            MediaType.parse("major/minor;q=0.5"),
            true);
    }

    // equalsOnlyPresentParameters.........................................................................................

    @Test
    public void testEqualsOnlyPresentParametersDifferent() {
        this.equalsOnlyPresentParametersAndCheck(
            MediaType.parse("major/minor"),
            MediaType.parse("different/different2"),
            false);
    }

    @Test
    public void testEqualsOnlyPresentParametersDifferentParameters() {
        this.equalsOnlyPresentParametersAndCheck(
            MediaType.parse("major/minor;q=1.0"),
            MediaType.parse("major/minor;q=0.5"),
            false);
    }

    @Test
    public void testEqualsOnlyPresentParametersDifferentParameters2() {
        this.equalsOnlyPresentParametersAndCheck(
            MediaType.parse("major/minor;q=1.0;parameter-2=value2"),
            MediaType.parse("major/minor;q=1.0"),
            false);
    }

    @Test
    public void testEqualsOnlyPresentParametersSharedParameters() {
        this.equalsOnlyPresentParametersAndCheck(
            MediaType.parse("major/minor;q=1.0"),
            MediaType.parse("major/minor;q=1.0"),
            true);
    }

    @Test
    public void testEqualsOnlyPresentParametersSharedAndIgnoredParameters() {
        this.equalsOnlyPresentParametersAndCheck(
            MediaType.parse("major/minor;q=1.0"),
            MediaType.parse("major/minor;q=1.0;parameter-2=value2"),
            true);
    }

    // toString........................................................................................................

    @Test
    public void testToStringParse() {
        final String text = "type/subtype";
        this.toStringAndCheck(MediaType.parse(text), text);
    }

    @Test
    public void testToStringParseWithParameters() {
        this.toStringAndCheck(MediaType.parse("type/subtype;a=b;c=d"),
            "type/subtype; a=b; c=d");
    }

    @Test
    public void testToStringParseWithParametersWithQuotes() {
        this.toStringAndCheck(MediaType.parse("type/subtype;a=b;c=\"d e\""),
            "type/subtype; a=b; c=\"d e\"");
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(MediaType.with(TYPE, SUBTYPE),
            TYPE + "/" + SUBTYPE);
    }

    @Test
    public void testToStringWithSuffix() {
        this.toStringAndCheck(
            MediaType.withParameters(
                TYPE,
                SUBTYPE,
                Optional.of("suffix1"),
                MediaType.NO_PARAMETERS
            ),
            TYPE + "/" + SUBTYPE + "+suffix1"
        );
    }

    @Test
    public void testToStringWithSuffix2() {
        this.toStringAndCheck(
            MediaType.withParameters(
                TYPE,
                SUBTYPE,
                Optional.of("suffix1a.suffix1b"),
                MediaType.NO_PARAMETERS
            ),
            TYPE + "/" + SUBTYPE + "+suffix1a.suffix1b"
        );
    }

    @Test
    public void testToStringWithSuffixParse() {
        this.toStringAndCheck(
            MediaType.parse(
                TYPE + "/" + SUBTYPE + "+suffix123"
            ),
            TYPE + "/" + SUBTYPE + "+suffix123"
        );
    }

    @Test
    public void testToStringWithParameters() {
        this.toStringAndCheck(
            mediaType(),
            TYPE + "/" + SUBTYPE + "; parameter123=value456"
        );
    }

    @Test
    public void testToStringWithParametersRequireQuotesWhitespace() {
        this.toStringAndCheck(
            MediaType.with(TYPE, SUBTYPE)
                .setParameters(parameters("a", "b c")),
            "type/subtype; a=\"b c\""
        );
    }

    @Test
    public void testToStringWithParametersRequireQuotesBackslash() {
        this.toStringAndCheck(
            MediaType.with(TYPE, SUBTYPE)
                .setParameters(parameters("a", "b\\c")
                ),
            "type/subtype; a=\"b\\\\c\""
        );
    }

    @Test
    public void testToStringWithParametersRequireQuotesDoubleQuoteChar() {
        this.toStringAndCheck(
            MediaType.with(TYPE, SUBTYPE)
                .setParameters(parameters("a", "b\"c")
                ),
            "type/subtype; a=\"b\\\"c\""
        );
    }

    // helpers..........................................................................................................

    @Override
    public MediaType createHeaderWithParameters() {
        return this.mediaType();
    }

    private MediaType mediaType() {
        return MediaType.withParameters(
            TYPE,
            SUBTYPE,
            SUFFIX,
            parameters()
        );
    }

    @Override
    MediaTypeParameterName<?> parameterName() {
        return MediaTypeParameterName.with("xyz");
    }

    private Map<MediaTypeParameterName<?>, Object> parameters() {
        return this.parameters(
            PARAMETER_NAME,
            PARAMETER_VALUE
        );
    }

    private Map<MediaTypeParameterName<?>, Object> parameters(final String name,
                                                              final Object value) {
        return Maps.of(
            MediaTypeParameterName.with(name),
            value
        );
    }

    @Override
    public MediaType createDifferentHeader() {
        return MediaType.with(
            "different-type",
            "different-sub-type"
        );
    }

    @Override
    public boolean isMultipart() {
        return true;
    }

    @Override
    public boolean isRequest() {
        return true;
    }

    @Override
    public boolean isResponse() {
        return true;
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<MediaType> type() {
        return MediaType.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
