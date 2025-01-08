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

package walkingkooka.net.http;

import org.junit.jupiter.api.Test;
import walkingkooka.Binary;
import walkingkooka.collect.map.Maps;
import walkingkooka.net.header.CharsetName;
import walkingkooka.net.header.HttpHeaderName;
import walkingkooka.net.header.MediaType;
import walkingkooka.text.LineEnding;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

public final class HttpEntityBinaryTest extends HttpEntityNotEmptyTestCase<HttpEntityBinary> {

    @Test
    public void testContentLength() {
        this.contentLengthAndCheck(this.createHttpEntity(), BINARY.size());
    }

    @Test
    public void testSetHeadersDifferent() {
        final HttpEntity entity = this.createHttpEntity();

        final Map<HttpHeaderName<?>, List<?>> headers = map(HttpHeaderName.CONTENT_TYPE, MediaType.TEXT_PLAIN);
        final HttpEntity different = entity.setHeaders(headers);
        assertNotSame(entity, different);

        this.check(different, headers, BINARY);
    }

    @Test
    public void testRemoveHeader() {
        final HttpEntityBinary entity = this.createHttpEntity();
        this.checkEquals(HttpEntity.NO_HEADERS, entity.headers());

        final HttpHeaderName<Long> header = HttpHeaderName.CONTENT_LENGTH;
        final Long value = 1L;

        final HttpEntity added = entity.addHeader(header, value);
        final HttpEntity removed = added.removeHeader(header);
        assertNotSame(added, removed);

        this.check(removed, HttpEntity.NO_HEADERS, BINARY);
    }

    @Test
    public void testContentType() {
        final MediaType contentType = MediaType.TEXT_PLAIN;

        this.contentTypeAndCheck(
            HttpEntity.EMPTY.setContentType(contentType)
                .setBody(
                    Binary.with("Hello".getBytes(Charset.defaultCharset()))
                ),
            contentType
        );
    }

    // isMultipartFormData..............................................................................................

    @Test
    public void testIsMultipartFormDataWhenMultipartFormData() {
        this.isMultipartFormDataAndCheck(
            HttpEntity.EMPTY.setContentType(MediaType.MULTIPART_FORM_DATA)
                .setBody(BINARY),
            true
        );
    }

    @Test
    public void testIsMultipartFormDataWhenTextPlain() {
        this.isMultipartFormDataAndCheck(
            HttpEntity.EMPTY.setContentType(MediaType.TEXT_PLAIN)
                .setBody(BINARY),
            false
        );
    }

    // bodyText.........................................................................................................

    @Test
    public void testBodyText() {
        this.check(this.createHttpEntity(), HttpEntity.NO_HEADERS, TEXT);
    }

    @Test
    public void testBodyTextWithHeaders() {
        final HttpEntity entity = this.createHttpEntity()
            .addHeader(HttpHeaderName.CONTENT_LENGTH, 777L); // ignored
        this.check(entity, TEXT);
    }

    @Test
    public void testBodyTextUsesCharset() {
        final HttpEntity entity = this.createHttpEntity();
        final HttpEntity different = entity.setContentType(
            MediaType.TEXT_PLAIN.setCharset(CharsetName.UTF_16)
        );
        assertNotSame(entity, different);

        this.check(different, new String(BINARY.value(), StandardCharsets.UTF_16));
    }

    @Test
    public void testSetBodySameText() {
        final HttpEntity entity = this.createHttpEntity();
        assertSame(entity, entity.setBodyText(TEXT));
    }

    @Test
    public void testBodyTextBody() {
        final byte[] bytes = new byte[127];
        for (byte i = 0; i < bytes.length; i++) {
            bytes[i] = i;
        }

        final HttpEntity binaryHttpEntity = HttpEntityBinary.EMPTY.setBody(
            Binary.with(bytes)
        );
        this.checkEquals(
            binaryHttpEntity,
            binaryHttpEntity.setBodyText(
                binaryHttpEntity.text()
            )
        );
    }

    @Test
    public void testBodyTextBody2() {
        final byte[] bytes = new byte[127];
        for (byte i = 0; i < bytes.length; i++) {
            bytes[i] = i;
        }

        final HttpEntity binaryHttpEntity = HttpEntityBinary.EMPTY.setBody(
            Binary.with(bytes)
        );
        this.checkEquals(
            binaryHttpEntity,
            HttpEntityText.EMPTY.setBodyText(
                binaryHttpEntity.text()
            )
        );
    }

    @Test
    public void testBodyTextBodyWithJarFile() throws IOException {
        final String text = "Hello";
        final String filename = "Hello.txt";

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        {
            final JarOutputStream jos = new JarOutputStream(baos);

            final JarEntry entry = new JarEntry(filename);
            jos.putNextEntry(entry);
            jos.write(
                text.getBytes(Charset.defaultCharset())
            );
            jos.closeEntry();

            jos.finish();
            jos.flush();
            jos.close();
        }
        final HttpEntity binaryHttpEntity = HttpEntityBinary.EMPTY.setBody(
            Binary.with(baos.toByteArray())
        );
        final HttpEntity textHttpEntity = HttpEntity.EMPTY.setBodyText(
            binaryHttpEntity.text()
        );

        final JarInputStream jis = new JarInputStream(
            textHttpEntity.body().inputStream()
        );

        String text2 = null;

        for (; ; ) {
            final JarEntry entry = jis.getNextJarEntry();
            if (null == entry) {
                break;
            }
            if (entry.getName().equals(filename)) {
                text2 = new String(
                    jis.readAllBytes(),
                    Charset.defaultCharset()
                );
            }
        }

        this.checkEquals(
            text,
            text2
        );
    }

    // toString ....................................................................................................

    @Test
    public void testToStringText() {
        this.toStringAndCheck(
            this.createHttpEntity(
                HttpHeaderName.CONTENT_LENGTH, 257L,
                HttpHeaderName.CONTENT_TYPE, MediaType.TEXT_PLAIN.setCharset(CharsetName.UTF_8),
                HttpHeaderName.SERVER, "Server 123", "AB\nC"),
            "Content-Length: 257\r\nContent-Type: text/plain; charset=UTF-8\r\nServer: Server 123\r\n\r\nAB\nC"
        );
    }

    @Test
    public void testToStringEmptyBinary() {
        final HttpEntityBinary httpEntityBinary = this.createHttpEntity(HttpHeaderName.CONTENT_LENGTH, 257L, "");

        this.isEmptyAndCheck(
            httpEntityBinary.body(),
            true
        );

        this.toStringAndCheck(
            httpEntityBinary,
            "Content-Length: 257\r\n\r\n"
        );
    }

    @Test
    public void testToStringBinary() {
        final String letters = "a";
        this.toStringAndCheck(
            this.createHttpEntity(
                HttpHeaderName.CONTENT_LENGTH,
                257L, letters
            ),
            "Content-Length: 257\r\n\r\n" +
                "00000000 61                                              a               " + LineEnding.CRNL
        );
    }

    @Test
    public void testToStringBinaryUnprintable() {
        final String letters = "\0";
        this.toStringAndCheck(
            this.createHttpEntity(
                HttpHeaderName.CONTENT_LENGTH,
                257L,
                letters
            ),
            "Content-Length: 257\r\n\r\n" +
                "00000000 00                                              .               " + LineEnding.CRNL
        );
    }

    @Test
    public void testToStringBinaryMultiLine() {
        final String letters = "abcdefghijklmnopq";
        this.toStringAndCheck(
            this.createHttpEntity(
                HttpHeaderName.CONTENT_LENGTH,
                257L,
                letters
            ),
            "Content-Length: 257\r\n\r\n" +
                "00000000 61 62 63 64 65 66 67 68 69 6a 6b 6c 6d 6e 6f 70 abcdefghijklmnop" + LineEnding.CRNL +
                "00000010 71                                              q               " + LineEnding.CRNL
        );
    }

    @Test
    public void testToStringBinaryMultiLine2() {
        final String letters = "\n\0cdefghijklmnopq";
        this.toStringAndCheck(
            this.createHttpEntity(
                HttpHeaderName.CONTENT_LENGTH,
                257L,
                letters
            ),
            "Content-Length: 257\r\n\r\n" +
                "00000000 0a 00 63 64 65 66 67 68 69 6a 6b 6c 6d 6e 6f 70 ..cdefghijklmnop" + LineEnding.CRNL +
                "00000010 71                                              q               " + LineEnding.CRNL
        );
    }

    @Test
    public void testToStringMultipleHeadersBinary() {
        final String letters = "\n\0cdefghijklmnopq";
        this.toStringAndCheck(
            this.createHttpEntity(
                HttpHeaderName.CONTENT_LENGTH, 257L,
                HttpHeaderName.CONTENT_TYPE, MediaType.BINARY,
                HttpHeaderName.SERVER, "Server 123",
                letters
            ),
            "Content-Length: 257\r\n" +
                "Content-Type: application/octet-stream\r\n" +
                "Server: Server 123\r\n\r\n" +
                "00000000 0a 00 63 64 65 66 67 68 69 6a 6b 6c 6d 6e 6f 70 ..cdefghijklmnop" + LineEnding.CRNL +
                "00000010 71                                              q               " + LineEnding.CRNL
        );
    }

    // helper...........................................................................................................

    private <T> HttpEntityBinary createHttpEntity(final HttpHeaderName<T> header,
                                                  final T value,
                                                  final String text) {
        return this.createHttpEntity(Maps.of(header, HttpEntityHeaderList.one(header, value)), text);
    }

    private <T1, T2, T3> HttpEntityBinary createHttpEntity(final HttpHeaderName<T1> header1,
                                                           final T1 value1,
                                                           final HttpHeaderName<T2> header2,
                                                           final T2 value2,
                                                           final HttpHeaderName<T3> header3,
                                                           final T3 value3,
                                                           final String text) {
        return this.createHttpEntity(Maps.of(header1, HttpEntityHeaderList.one(header1, value1),
            header2, HttpEntityHeaderList.one(header2, value2),
            header3, HttpEntityHeaderList.one(header3, value3)), text);
    }

    private HttpEntityBinary createHttpEntity(final Map<HttpHeaderName<?>, HttpEntityHeaderList> headers,
                                              final String text) {
        return HttpEntityBinary.with(headers, Binary.with(text.getBytes(HttpEntity.DEFAULT_BODY_CHARSET)));
    }

    @Override
    HttpEntityBinary createHttpEntity(final Map<HttpHeaderName<?>, HttpEntityHeaderList> headers) {
        return HttpEntityBinary.with(headers, BINARY);
    }

    // TreePrintable....................................................................................................

    @Test
    public void testTreePrintHeader() {
        this.treePrintAndCheck(
            HttpEntity.EMPTY
                .setContentType(MediaType.TEXT_PLAIN),
            "HttpEntity\n" +
                "  header(s)\n" +
                "    Content-Type: text/plain\n"
        );
    }

    @Test
    public void testTreePrintSeveralHeader() {
        this.treePrintAndCheck(
            HttpEntity.EMPTY
                .setContentType(MediaType.TEXT_PLAIN)
                .addHeader(HttpHeaderName.CONTENT_LENGTH, 123L)
                .addHeader(HttpHeaderName.SERVER, "Server123"),
            "HttpEntity\n" +
                "  header(s)\n" +
                "    Content-Length: 123\n" +
                "    Content-Type: text/plain\n" +
                "    Server: Server123\n"
        );
    }

    @Test
    public void testTreePrintHeaderContentTypeTextAndBody() {
        this.treePrintAndCheck(
            HttpEntity.EMPTY
                .setContentType(MediaType.TEXT_PLAIN)
                .setBody(
                    binary("Body123")
                ),
            "HttpEntity\n" +
                "  header(s)\n" +
                "    Content-Type: text/plain\n" +
                "  bodyText\n" +
                "    Body123\n"
        );
    }

    @Test
    public void testTreePrintHeaderContentTypeTextAndBodyMultiLine() {
        this.treePrintAndCheck(
            HttpEntity.EMPTY
                .setContentType(MediaType.TEXT_PLAIN)
                .setBody(
                    binary("Line1\rLine2\nLine3\r\n")
                ),
            "HttpEntity\n" +
                "  header(s)\n" +
                "    Content-Type: text/plain\n" +
                "  bodyText\n" +
                "    Line1\r" +
                "    Line2\n" +
                "    Line3\r\n" +
                "    \n"
        );
    }

    @Test
    public void testTreePrintBodyBinary() {
        this.treePrintAndCheck(
            HttpEntity.EMPTY
                .setBody(
                    Binary.with(
                        new byte[]{
                            0,
                            1,
                            2,
                            3,
                            4,
                            5,
                            6,
                            7,
                            8,
                            9,
                            10
                        }
                    )
                ),
            "HttpEntity\n" +
                "  body\n" +
                "    00 01 02 03 04 05 06 07 08 09 0a                             ...........         \n"
        );
    }

    @Test
    public void testTreePrintBodyContenTypeNonTextBinary() {
        this.treePrintAndCheck(
            HttpEntity.EMPTY
                .setContentType(MediaType.BINARY)
                .setBody(
                    Binary.with(
                        new byte[]{
                            0,
                            1,
                            2,
                            3,
                            4,
                            5,
                            6,
                            7,
                            8,
                            9,
                            10
                        }
                    )
                ),
            "HttpEntity\n" +
                "  header(s)\n" +
                "    Content-Type: application/octet-stream\n" +
                "  body\n" +
                "    00 01 02 03 04 05 06 07 08 09 0a                             ...........         \n"
        );
    }

    @Test
    public void testTreePrintBody() {
        this.treePrintAndCheck(
            HttpEntity.EMPTY
                .setBody(
                    binary("Body123")
                ),
            "HttpEntity\n" +
                "  body\n" +
                "    42 6f 64 79 31 32 33                                         Body123             \n"
        );
    }

    @Test
    public void testTreePrintBodyIncludesLineBreaks() {
        this.treePrintAndCheck(
            HttpEntity.EMPTY
                .setBody(
                    binary("Line1\nLine2\rLine3\r\n")
                ),
            "HttpEntity\n" +
                "  body\n" +
                "    4c 69 6e 65 31 0a 4c 69 6e 65 32 0d 4c 69 6e 65 33 0d 0a     Line1.Line2.Line3.. \n"
        );
    }

    @Test
    public void testTreePrintBodyTextContentTypeApplicationJson() {
        this.treePrintAndCheck(
            HttpEntity.EMPTY
                .setContentType(MediaType.APPLICATION_JSON)
                .setBody(binary("Body123")),
            "HttpEntity\n" +
                "  header(s)\n" +
                "    Content-Type: application/json\n" +
                "  bodyText\n" +
                "    Body123\n"
        );
    }

    private static Binary binary(final String text) {
        return Binary.with(
            text.getBytes(Charset.defaultCharset())
        );
    }

    // Class............................................................................................................

    @Override
    public Class<HttpEntityBinary> type() {
        return HttpEntityBinary.class;
    }
}
