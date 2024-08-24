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

package walkingkooka.net;

import org.junit.jupiter.api.Test;
import walkingkooka.Binary;
import walkingkooka.CanBeEmptyTesting;
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.ToStringTesting;
import walkingkooka.net.header.HttpHeaderName;
import walkingkooka.net.header.MediaType;
import walkingkooka.net.http.HttpEntity;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.text.HasTextTesting;
import walkingkooka.text.printer.TreePrintableTesting;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class WebEntityTest implements CanBeEmptyTesting,
        HashCodeEqualsDefinedTesting2<WebEntity>,
        HasTextTesting,
        ToStringTesting<WebEntity>,
        TreePrintableTesting,
        ClassTesting<WebEntity> {

    private final static Optional<MediaType> CONTENT_TYPE = Optional.of(MediaType.APPLICATION_JSON);

    private final static String BODY_TEXT = "text123";

    // with.............................................................................................................

    @Test
    public void testEmpty() {
        final WebEntity webEntity = WebEntity.empty();

        this.isEmptyAndCheck(
                webEntity,
                true
        );
        this.checkHttpEntity(
                webEntity,
                HttpEntity.EMPTY
        );
    }

    // setContentType...................................................................................................

    @Test
    public void testSetContentTypeNullFails() {
        assertThrows(
                NullPointerException.class,
                () -> WebEntity.empty().setContentType(null)
        );
    }

    @Test
    public void testSetContentTypeSame() {
        final WebEntity webEntity = WebEntity.with(
                HttpEntity.EMPTY.addHeader(
                        HttpHeaderName.CONTENT_TYPE,
                        CONTENT_TYPE.get()
                ).setBodyText(BODY_TEXT)
        );

        assertSame(
                webEntity,
                webEntity.setContentType(CONTENT_TYPE)
        );
    }

    @Test
    public void testSetContentTypeDifferent() {
        final HttpEntity httpEntity = HttpEntity.EMPTY.addHeader(
                HttpHeaderName.CONTENT_TYPE,
                CONTENT_TYPE.get()
        );
        final WebEntity webEntity = WebEntity.with(httpEntity);

        final MediaType differentContentType = MediaType.parse("text/different");
        final WebEntity different = webEntity.setContentType(
                Optional.of(differentContentType)
        );

        assertNotSame(
                different,
                webEntity
        );

        this.checkHttpEntity(
                different,
                httpEntity.setContentType(differentContentType)
        );
    }

    // setBinary........................................................................................................

    @Test
    public void testSetBinaryNullFails() {
        assertThrows(
                NullPointerException.class,
                () -> WebEntity.empty()
                        .setBinary(null)
        );
    }

    @Test
    public void testSetBinarySame() {
        final Binary binary = Binary.with("binary123".getBytes(StandardCharsets.UTF_8));

        final WebEntity webEntity = WebEntity.with(
                HttpEntity.EMPTY.setContentType(CONTENT_TYPE.get())
                        .setBody(binary)
        );

        assertSame(
                webEntity,
                webEntity.setBinary(binary)
        );
    }

    @Test
    public void testSetBinarySameText() {
        final String text = "binary123";
        final Binary binary = Binary.with(
                text.getBytes(StandardCharsets.UTF_8)
        );

        final WebEntity webEntity = WebEntity.with(
                HttpEntity.EMPTY.setContentType(CONTENT_TYPE.get())
                        .setBody(binary)
        );

        assertSame(
                webEntity,
                webEntity.setText(text)
        );
    }

    @Test
    public void testSetBinaryDifferent() {
        final Binary binary = Binary.with(
                "binary123".getBytes(StandardCharsets.UTF_8)
        );

        final HttpEntity httpEntity = HttpEntity.EMPTY.addHeader(
                HttpHeaderName.CONTENT_TYPE,
                CONTENT_TYPE.get()
        ).setBody(binary);

        final WebEntity webEntity = WebEntity.with(httpEntity);

        final Binary differentBinary = Binary.with(
                "text/different".getBytes(StandardCharsets.UTF_8)
        );
        final WebEntity different = webEntity.setBinary(differentBinary);

        assertNotSame(
                different,
                webEntity
        );

        this.checkHttpEntity(
                different,
                httpEntity.setBody(differentBinary)
        );

        this.checkHttpEntity(
                webEntity,
                httpEntity.setBody(binary)
        );
    }

    // setText..........................................................................................................

    @Test
    public void testSetTextNullFails() {
        assertThrows(
                NullPointerException.class,
                () -> WebEntity.empty().setText(null)
        );
    }

    @Test
    public void testSetTextSame() {
        final WebEntity webEntity = WebEntity.with(
                HttpEntity.EMPTY.setContentType(CONTENT_TYPE.get())
                        .setBodyText(BODY_TEXT)
        );

        assertSame(
                webEntity,
                webEntity.setContentType(CONTENT_TYPE)
        );
    }

    @Test
    public void testSetTextDifferent() {
        final HttpEntity httpEntity = HttpEntity.EMPTY.addHeader(
                HttpHeaderName.CONTENT_TYPE,
                CONTENT_TYPE.get()
        ).setBodyText(BODY_TEXT);

        final WebEntity webEntity = WebEntity.with(httpEntity);

        final MediaType differentContentType = MediaType.parse("text/different");
        final WebEntity different = webEntity.setContentType(
                Optional.of(differentContentType)
        );

        assertNotSame(
                different,
                webEntity
        );

        this.checkHttpEntity(
                different,
                httpEntity.setContentType(differentContentType)
        );

        this.textAndCheck(
                different,
                BODY_TEXT
        );
    }

    @Test
    public void testSetTextEmptyAndSetEmptyContentType() {
        assertSame(
                WebEntity.empty(),
                WebEntity.empty()
                        .setText("Hello123")
                        .setContentType(
                                Optional.of(MediaType.TEXT_PLAIN)
                        ).setText("")
                        .setContentType(Optional.empty())
        );
    }

    private void checkHttpEntity(final WebEntity webEntity,
                                 final HttpEntity httpEntity) {
        this.checkEquals(
                httpEntity,
                webEntity.httpEntity
        );
    }

    // hashCode/equals..................................................................................................

    @Test
    public void testEqualsDifferentContentType() {
        this.checkNotEquals(
                WebEntity.with(
                        HttpEntity.EMPTY.addHeader(
                                HttpHeaderName.CONTENT_TYPE,
                                MediaType.parse("text/different")
                        ).setBodyText(BODY_TEXT)
                )
        );
    }

    @Test
    public void testEqualsDifferentText() {
        this.checkNotEquals(
                HttpEntity.EMPTY.addHeader(
                        HttpHeaderName.CONTENT_TYPE,
                        CONTENT_TYPE.get()
                ).setBodyText("different")
        );
    }

    @Test
    public void testEqualsDifferentBinary() {
        this.checkNotEquals(
                HttpEntity.EMPTY.addHeader(
                        HttpHeaderName.CONTENT_TYPE,
                        CONTENT_TYPE.get()
                ).setBody(
                        Binary.with(
                                "different".getBytes(StandardCharsets.UTF_8)
                        )
                )
        );
    }

    @Override
    public WebEntity createObject() {
        return WebEntity.with(
                HttpEntity.EMPTY.addHeader(
                        HttpHeaderName.CONTENT_TYPE,
                        CONTENT_TYPE.get()
                ).setBodyText(BODY_TEXT)
        );
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
                HttpEntity.EMPTY.addHeader(
                        HttpHeaderName.CONTENT_TYPE,
                        CONTENT_TYPE.get()
                ).setBodyText(BODY_TEXT),
                "Content-Type: application/json\r\n" +
                        "\r\n" +
                        "text123"
        );
    }

    // TreePrintable....................................................................................................

    @Test
    public void testTreePrintText() {
        this.treePrintAndCheck(
                WebEntity.empty()
                        .setContentType(
                                Optional.of(MediaType.TEXT_PLAIN)
                        ).setText("Hello123"),
                "WebEntity\n" +
                        "  HttpEntity\n" +
                        "    header(s)\n" +
                        "      Content-Type: text/plain\n" +
                        "    bodyText\n" +
                        "      Hello123\n"
        );
    }

    @Test
    public void testTreePrintBinary() {
        this.treePrintAndCheck(
                WebEntity.empty()
                        .setContentType(
                                Optional.of(MediaType.IMAGE_PNG)
                        ).setBinary(
                                Binary.with(
                                        "png image something something".getBytes(StandardCharsets.UTF_8)
                                )
                        ),
                "WebEntity\n" +
                        "  HttpEntity\n" +
                        "    header(s)\n" +
                        "      Content-Type: image/png\n" +
                        "    body\n" +
                        "      70 6e 67 20 69 6d 61 67 65 20 73 6f 6d 65 74 68 69 6e 67 20  png image something \n" +
                        "      73 6f 6d 65 74 68 69 6e 67                                   something           \n"
        );
    }

    // Class............................................................................................................

    @Override
    public Class<WebEntity> type() {
        return WebEntity.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
