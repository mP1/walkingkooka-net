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
import walkingkooka.collect.map.Maps;
import walkingkooka.naming.NameTesting2;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.text.CaseSensitivity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

final public class ContentDispositionTypeTest implements ClassTesting2<ContentDispositionType>,
    NameTesting2<ContentDispositionType, ContentDispositionType> {

    @Test
    public void testConstantNameReturnsConstant() {
        assertSame(ContentDispositionType.INLINE, ContentDispositionType.with(ContentDispositionType.INLINE.value()));
    }

    @Test
    public void testConstantNameReturnsConstantCaseIgnored() {
        assertSame(ContentDispositionType.INLINE, ContentDispositionType.with(ContentDispositionType.INLINE.value().toUpperCase()));
    }

    // setFilename......................................................................................................

    @Test
    public void testSetFilenameNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> ContentDispositionType.ATTACHMENT.setFilename(null)
        );
    }

    @Test
    public void testSetFilenameWithAttachmentAndEncoded() {
        this.setFilenameAndCheck(
            ContentDispositionType.ATTACHMENT,
            ContentDispositionFileName.notEncoded("readme.txt"),
            "attachment; filename=readme.txt"
        );
    }

    @Test
    public void testSetFilenameWithInlineAndEncoded() {
        this.setFilenameAndCheck(
            ContentDispositionType.INLINE,
            ContentDispositionFileName.notEncoded("readme.txt"),
            "inline; filename=readme.txt"
        );
    }

    @Test
    public void testSetFilenameWithAttachmentAndNotEncoded() {
        this.setFilenameAndCheck(
            ContentDispositionType.ATTACHMENT,
            ContentDispositionFileName.encoded(
                EncodedText.with(
                    CharsetName.UTF_8,
                    EncodedText.NO_LANGUAGE,
                    "readme.txt"
                )
            ),
            // Content-Disposition: attachment; filename="filename.jpg"
            "attachment; filename*=UTF-8''readme.txt"
        );
    }

    private void setFilenameAndCheck(final ContentDispositionType type,
                                     final ContentDispositionFileName filename,
                                     final String expected) {
        this.setFilenameAndCheck(
            type,
            filename,
            ContentDisposition.parse(expected)
        );
    }

    private void setFilenameAndCheck(final ContentDispositionType type,
                                     final ContentDispositionFileName filename,
                                     final ContentDisposition expected) {
        this.checkEquals(
            expected,
            type.setFilename(filename),
            () -> type + " " + filename
        );
    }

    // setParameters....................................................................................................

    @Test
    public void testSetParametersNullFails() {
        assertThrows(NullPointerException.class, () -> ContentDispositionType.ATTACHMENT.setParameters(null));
    }

    @Test
    public void testSetParameters() {
        final ContentDispositionFileName filename = ContentDispositionFileName.notEncoded("readme.txt");
        final ContentDispositionType type = ContentDispositionType.ATTACHMENT;
        final Map<ContentDispositionParameterName<?>, Object> parameters = Maps.of(ContentDispositionParameterName.FILENAME, filename);
        final ContentDisposition disposition = type.setParameters(parameters);
        this.checkEquals(type, disposition.type(), "type");
        this.checkEquals(parameters,
            disposition.parameters(),
            "parameters");
    }

    // isXXX............................................................................................................

    @Test
    public void testIsAttachmentTrue() {
        this.checkEquals(
            true,
            ContentDispositionType.ATTACHMENT.isAttachment()
        );
    }

    @Test
    public void testIsAttachmentFalse() {
        this.checkEquals(
            false,
            ContentDispositionType.FORM_DATA.isAttachment()
        );
    }

    @Test
    public void testIsFormDataTrue() {
        this.checkEquals(
            true,
            ContentDispositionType.FORM_DATA.isFormData()
        );
    }

    @Test
    public void testIsFormDataFalse() {
        this.checkEquals(
            false,
            ContentDispositionType.INLINE.isFormData()
        );
    }

    @Test
    public void testIsInlineTrue() {
        this.checkEquals(
            true,
            ContentDispositionType.INLINE.isInline()
        );
    }

    @Test
    public void testIsInlineFalse() {
        this.checkEquals(
            false,
            ContentDispositionType.ATTACHMENT.isInline()
        );
    }

    // equals...........................................................................................................

    @Test
    public void testEqualsDifferent() {
        this.checkNotEquals(ContentDispositionType.ATTACHMENT);
    }

    @Test
    public void testEqualsDifferentCase() {
        this.checkEquals(ContentDispositionType.with(ContentDispositionType.INLINE.value().toUpperCase()));
    }

    @Test
    @Override
    public void testToString() {
        final String value = "abc123";
        this.toStringAndCheck(ContentDispositionType.with(value), value);
    }

    @Override
    public ContentDispositionType createName(final String name) {
        return ContentDispositionType.with(name);
    }

    @Override
    public CaseSensitivity caseSensitivity() {
        return CaseSensitivity.INSENSITIVE;
    }

    @Override
    public String nameText() {
        return "inline";
    }

    @Override
    public String differentNameText() {
        return "form-data";
    }

    @Override
    public String nameTextLess() {
        return "attachment";
    }

    @Override
    public int minLength() {
        return 1;
    }

    @Override
    public int maxLength() {
        return Integer.MAX_VALUE;
    }

    @Override
    public String possibleValidChars(final int position) {
        return RFC2045;
    }

    @Override
    public String possibleInvalidChars(final int position) {
        return CONTROL + RFC2045_TSPECIAL + BYTE_NON_ASCII;
    }

    @Override
    public Class<ContentDispositionType> type() {
        return Cast.to(ContentDispositionType.class);
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }

    // TypeNameTesting .........................................................................................

    @Override
    public String typeNamePrefix() {
        return ContentDisposition.class.getSimpleName();
    }

    @Override
    public String typeNameSuffix() {
        return "";
    }
}
