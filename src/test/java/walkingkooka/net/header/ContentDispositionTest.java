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
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.test.ParseStringTesting;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class ContentDispositionTest extends HeaderWithParametersTestCase<ContentDisposition,
    ContentDispositionParameterName<?>>
    implements ParseStringTesting<ContentDisposition> {

    private final static ContentDispositionType TYPE = ContentDispositionType.ATTACHMENT;
    private final static String PARAMETER_VALUE = "v1";

    // with.............................................................................................................

    @Test
    public void testWithNullValueFails() {
        assertThrows(
            NullPointerException.class,
            () -> ContentDisposition.with(null)
        );
    }

    @Test
    public void testWith() {
        this.check(this.createHeaderWithParameters());
    }

    // setType .........................................................................................................

    @Test
    public void testSetTypeNullFails() {
        assertThrows(NullPointerException.class, () -> this.createHeaderWithParameters().setType(null));
    }

    @Test
    public void testSetTypeSame() {
        final ContentDisposition disposition = this.createHeaderWithParameters();
        assertSame(disposition, disposition.setType(TYPE));
    }

    @Test
    public void testSetTypeDifferent() {
        final ContentDisposition disposition = this.createHeaderWithParameters();
        final ContentDispositionType type = ContentDispositionType.INLINE;
        this.check(disposition.setType(type), type, this.parameters());
        this.check(disposition);
    }

    // setParameters ...................................................................................................

    @Test
    public void testSetParametersDifferent() {
        final ContentDisposition disposition = this.createHeaderWithParameters();
        final Map<ContentDispositionParameterName<?>, Object> parameters = this.parameters("different", "2");
        this.check(disposition.setParameters(parameters), TYPE, parameters);
        this.check(disposition);
    }

    // parse ...........................................................................................................

    @Test
    public void testParseAttachment() {
        this.parseStringAndCheck(
            "attachment;",
            ContentDispositionType.ATTACHMENT.setParameters(ContentDisposition.NO_PARAMETERS)
        );
    }

    @Test
    public void testParseAttachmentFilename() {
        this.parseStringAndCheck(
            "attachment; filename=\"abc.jpg\"",
            ContentDispositionType.ATTACHMENT.setParameters(
                Maps.of(
                    ContentDispositionParameterName.FILENAME,
                    ContentDispositionFileName.notEncoded("abc.jpg")
                )
            )
        );
    }

    @Test
    public void testParseAttachmentFilenameStar() {
        this.parseStringAndCheck(
            "attachment; filename*=UTF-8''Hello.txt",
            ContentDispositionType.ATTACHMENT.setParameters(
                Maps.of(
                    ContentDispositionParameterName.FILENAME_STAR,
                    ContentDispositionFileName.encoded(
                        EncodedText.with(
                            CharsetName.UTF_8,
                            EncodedText.NO_LANGUAGE,
                            "Hello.txt"
                        )
                    )
                )
            )
        );
    }

    @Test
    public void testParseFormData() {
        this.parseStringAndCheck(
            "form-data;",
            ContentDispositionType.FORM_DATA.setParameters(ContentDisposition.NO_PARAMETERS)
        );
    }

    @Test
    public void testParseFormDataName() {
        this.parseStringAndCheck(
            "form-data; name=\"field123\"",
            ContentDispositionType.FORM_DATA.setParameters(
                Maps.of(
                    ContentDispositionParameterName.with("name"),
                    "field123"
                )
            )
        );
    }

    @Test
    public void testParseInline() {
        this.parseStringAndCheck(
            "inline;",
            ContentDispositionType.INLINE.setParameters(ContentDisposition.NO_PARAMETERS)
        );
    }

    @Override
    public ContentDisposition parseString(final String text) {
        return ContentDisposition.parse(text);
    }

    // filename.........................................................................................................

    // Content-Disposition: inline

    @Test
    public void testFilenameInline() {
        this.filenameAndCheck(
            ContentDisposition.parse("inline")
        );
    }


    // Content-Disposition: attachment

    @Test
    public void testFilenameAttachmentMissingFilename() {
        this.filenameAndCheck(
            ContentDisposition.parse("attachment")
        );
    }

    // Content-Disposition: attachment; filename="filename.jpg"

    @Test
    public void testFilenameAttachmentFilename() {
        this.filenameAndCheck(
            ContentDisposition.parse("attachment; filename=\"file123.txt\""),
            ContentDispositionFileName.notEncoded("file123.txt")
        );
    }

    // Content-Disposition: attachment; filename*="filename.jpg"
    @Test
    public void testFilenameAttachmentFilenameStar() {
        this.filenameAndCheck(
            ContentDisposition.parse("attachment; filename*=UTF-8''file123.txt"),
            ContentDispositionFileName.encoded(
                EncodedText.with(
                    CharsetName.UTF_8,
                    EncodedText.NO_LANGUAGE,
                    "file123.txt"
                )
            )
        );
    }

    @Test
    public void testFilenameAttachmentFilenameStarAndFilename() {
        this.filenameAndCheck(
            ContentDisposition.parse("attachment; filename*=UTF-8''filename-star.txt; filename=\"filename-not-star.txt\""),
            ContentDispositionFileName.encoded(
                EncodedText.with(
                    CharsetName.UTF_8,
                    EncodedText.NO_LANGUAGE,
                    "filename-star.txt"
                )
            )
        );
    }

    // Content-Disposition: form-data; filename="filename.jpg"

    @Test
    public void testFilenameFormDataFilename() {
        this.filenameAndCheck(
            ContentDisposition.parse("form-data; filename=\"file123.txt\""),
            ContentDispositionFileName.notEncoded("file123.txt")
        );
    }

    // Content-Disposition: form-data; filename*="filename.jpg"
    @Test
    public void testFilenameFormDataFilenameStar() {
        this.filenameAndCheck(
            ContentDisposition.parse("form-data; filename*=UTF-8''file123.txt"),
            ContentDispositionFileName.encoded(
                EncodedText.with(
                    CharsetName.UTF_8,
                    EncodedText.NO_LANGUAGE,
                    "file123.txt"
                )
            )
        );
    }

    @Test
    public void testFilenameFormDataFilenameStarAndFilename() {
        this.filenameAndCheck(
            ContentDisposition.parse("form-data; filename*=UTF-8''filename-star.txt; filename=\"filename-not-star.txt\""),
            ContentDispositionFileName.encoded(
                EncodedText.with(
                    CharsetName.UTF_8,
                    EncodedText.NO_LANGUAGE,
                    "filename-star.txt"
                )
            )
        );
    }

    private void filenameAndCheck(final ContentDisposition disposition) {
        this.filenameAndCheck(
            disposition,
            Optional.empty()
        );
    }

    private void filenameAndCheck(final ContentDisposition disposition,
                                  final ContentDispositionFileName expected) {
        this.filenameAndCheck(
            disposition,
            Optional.of(expected)
        );
    }

    private void filenameAndCheck(final ContentDisposition disposition,
                                  final Optional<ContentDispositionFileName> expected) {
        this.checkEquals(
            expected,
            disposition.filename(),
            disposition::toString
        );
    }

    // toHeaderText ....................................................................................................

    @Test
    public void testToHeaderTextNoParameters() {
        this.toHeaderTextAndCheck(ContentDisposition.with(TYPE),
            "attachment");
    }

    @Test
    public void testToHeaderTextWithParameters() {
        this.toHeaderTextAndCheck(this.createHeaderWithParameters(),
            "attachment; p1=v1");
    }

    @Test
    public void testToHeaderTextWithSeveralParameters() {
        this.toHeaderTextAndCheck(ContentDisposition.with(TYPE)
                .setParameters(this.parameters("p1", "v1", "p2", "v2")),
            "attachment; p1=v1; p2=v2");
    }

    // isWildcard ......................................................................................................

    @Test
    public void testIsWildcard() {
        this.isWildcardAndCheck(false);
    }

    // HashCodeEqualsDefined ...........................................................................................

    @Test
    public void testEqualsDifferentContentDispositionType() {
        this.checkNotEquals2("inline; p1=\"v1\";");
    }

    @Test
    public void testEqualsDifferentParameters() {
        this.checkNotEquals2("attachment; p2=\"v2\";");
    }

    @Test
    public void testEqualsDifferentParameters2() {
        this.checkNotEquals2("attachment; p1=\"v1\"; p2=\"v2\";");
    }

    private void checkNotEquals2(final String disposition2) {
        this.checkNotEquals(ContentDisposition.parse("attachment; p1=\"v1\";"),
            ContentDisposition.parse(disposition2));
    }

    // equalsIgnoringParameters.........................................................................................

    @Test
    public void testEqualsIgnoringParametersDifferent2() {
        this.equalsIgnoringParametersAndCheck(
            ContentDisposition.parse("attachment"),
            ContentDisposition.parse("inline"),
            false);
    }

    @Test
    public void testEqualsIgnoringParametersDifferentParameters() {
        this.equalsIgnoringParametersAndCheck(
            ContentDisposition.parse("attachment;a=1"),
            ContentDisposition.parse("attachment;b=2"),
            true);
    }

    // equalsOnlyPresentParameters......................................................................................

    @Test
    public void testEqualsOnlyPresentParametersDifferent() {
        this.equalsOnlyPresentParametersAndCheck(
            ContentDisposition.parse("attachment"),
            ContentDisposition.parse("inline"),
            false);
    }

    @Test
    public void testEqualsOnlyPresentParametersDifferentParameters() {
        this.equalsOnlyPresentParametersAndCheck(
            ContentDisposition.parse("attachment;q=1.0"),
            ContentDisposition.parse("attachment;q=0.5"),
            false);
    }

    @Test
    public void testEqualsOnlyPresentParametersDifferentParameters2() {
        this.equalsOnlyPresentParametersAndCheck(
            ContentDisposition.parse("attachment;q=1.0;parameter-2=value2"),
            ContentDisposition.parse("attachment;q=1.0"),
            false);
    }

    @Test
    public void testEqualsOnlyPresentParametersSharedParameters() {
        this.equalsOnlyPresentParametersAndCheck(
            ContentDisposition.parse("attachment;q=1.0"),
            ContentDisposition.parse("attachment;q=1.0"),
            true);
    }

    @Test
    public void testEqualsOnlyPresentParametersSharedAndIgnoredParameters() {
        this.equalsOnlyPresentParametersAndCheck(
            ContentDisposition.parse("attachment;q=1.0"),
            ContentDisposition.parse("attachment;q=1.0;parameter-2=value2"),
            true);
    }

    // toString ...........................................................................................

    @Test
    public void testToStringNoParameters() {
        this.toStringAndCheck(
            ContentDisposition.with(TYPE),
            "attachment"
        );
    }

    @Test
    public void testToStringWithParameters() {
        this.toStringAndCheck(
            this.createHeaderWithParameters(),
            "attachment; p1=v1"
        );
    }

    @Test
    public void testToStringWithSeveralParameters() {
        this.toStringAndCheck(
            ContentDisposition.with(TYPE)
                .setParameters(this.parameters("p1", "v1", "p2", "v2")),
            "attachment; p1=v1; p2=v2"
        );
    }

    // helpers .........................................................................................................

    @Override
    public ContentDisposition createHeaderWithParameters() {
        return ContentDisposition.with(TYPE)
            .setParameters(this.parameters());
    }

    @Override
    ContentDispositionParameterName<?> parameterName() {
        return ContentDispositionParameterName.with("xyz");
    }

    private Map<ContentDispositionParameterName<?>, Object> parameters() {
        return this.parameters("p1", PARAMETER_VALUE);
    }

    private Map<ContentDispositionParameterName<?>, Object> parameters(final String name, final Object value) {
        return this.parameters(ContentDispositionParameterName.with(name), value);
    }

    private Map<ContentDispositionParameterName<?>, Object> parameters(final ContentDispositionParameterName<?> name,
                                                                       final Object value) {
        return Maps.of(name, value);
    }

    private Map<ContentDispositionParameterName<?>, Object> parameters(final String name1,
                                                                       final Object value1,
                                                                       final String name2,
                                                                       final Object value2) {
        return this.parameters(ContentDispositionParameterName.with(name1),
            value1,
            ContentDispositionParameterName.with(name2),
            value2);
    }

    private Map<ContentDispositionParameterName<?>, Object> parameters(final ContentDispositionParameterName<?> name1,
                                                                       final Object value1,
                                                                       final ContentDispositionParameterName<?> name2,
                                                                       final Object value2) {
        return Maps.of(name1, value1, name2, value2);
    }

    private void check(final ContentDisposition token) {
        this.check(token, TYPE, token.parameters());
    }

    private void check(final ContentDisposition contentDisposition,
                       final ContentDispositionType type,
                       final Map<ContentDispositionParameterName<?>, Object> parameters) {
        this.checkEquals(type, contentDisposition.type(), "type");
        this.checkParameters(contentDisposition, parameters);
    }

    @Override
    public ContentDisposition createDifferentHeader() {
        return ContentDisposition.with(ContentDispositionType.INLINE);
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

    // class............................................................................................................

    @Override
    public Class<ContentDisposition> type() {
        return ContentDisposition.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
