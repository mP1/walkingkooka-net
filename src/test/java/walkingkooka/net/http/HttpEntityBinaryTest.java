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

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    public final void testRemoveHeader() {
        final HttpEntityBinary entity = this.createHttpEntity();
        assertEquals(HttpEntity.NO_HEADERS, entity.headers());

        final HttpHeaderName<Long> header = HttpHeaderName.CONTENT_LENGTH;
        final Long value = 1L;

        final HttpEntity added = entity.addHeader(header, value);
        final HttpEntity removed = added.removeHeader(header);
        assertNotSame(added, removed);

        this.check(removed, HttpEntity.NO_HEADERS, BINARY);
    }

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
        final HttpEntity different = entity.addHeader(HttpHeaderName.CONTENT_TYPE, MediaType.TEXT_PLAIN.setCharset(CharsetName.UTF_16));
        assertNotSame(entity, different);

        this.check(different, new String(BINARY.value(), StandardCharsets.UTF_16));
    }

    @Test
    public void testSetBodySameText() {
        final HttpEntity entity = this.createHttpEntity();
        assertSame(entity, entity.setBodyText(TEXT));
    }

    // toString ....................................................................................................

    @Test
    public void testToStringText() {
        this.toStringAndCheck(this.createHttpEntity(HttpHeaderName.CONTENT_LENGTH, 257L,
                HttpHeaderName.CONTENT_TYPE, MediaType.TEXT_PLAIN.setCharset(CharsetName.UTF_8),
                HttpHeaderName.SERVER, "Server 123", "AB\nC"),
                "Content-Length: 257\r\nContent-Type: text/plain; charset=UTF-8\r\nServer: Server 123\r\n\r\nAB\nC");
    }

    @Test
    public void testToStringBinary() {
        final String letters = "a";
        this.toStringAndCheck(this.createHttpEntity(HttpHeaderName.CONTENT_LENGTH, 257L, letters),
                "Content-Length: 257\r\n\r\n" +
                        "00000000 61                                              a               " + LineEnding.SYSTEM);
    }

    @Test
    public void testToStringBinaryUnprintable() {
        final String letters = "\0";
        this.toStringAndCheck(this.createHttpEntity(HttpHeaderName.CONTENT_LENGTH, 257L, letters),
                "Content-Length: 257\r\n\r\n" +
                        "00000000 00                                              .               " + LineEnding.SYSTEM);
    }

    @Test
    public void testToStringBinaryMultiLine() {
        final String letters = "abcdefghijklmnopq";
        this.toStringAndCheck(this.createHttpEntity(HttpHeaderName.CONTENT_LENGTH, 257L, letters),
                "Content-Length: 257\r\n\r\n" +
                        "00000000 61 62 63 64 65 66 67 68 69 6a 6b 6c 6d 6e 6f 70 abcdefghijklmnop" + LineEnding.SYSTEM +
                        "00000010 71                                              q               " + LineEnding.SYSTEM);
    }

    @Test
    public void testToStringBinaryMultiLine2() {
        final String letters = "\n\0cdefghijklmnopq";
        this.toStringAndCheck(this.createHttpEntity(HttpHeaderName.CONTENT_LENGTH, 257L, letters),
                "Content-Length: 257\r\n\r\n" +
                        "00000000 0a 00 63 64 65 66 67 68 69 6a 6b 6c 6d 6e 6f 70 ..cdefghijklmnop" + LineEnding.SYSTEM +
                        "00000010 71                                              q               " + LineEnding.SYSTEM);
    }

    @Test
    public void testToStringMultipleHeadersBinary() throws Exception {
        final String letters = "\n\0cdefghijklmnopq";
        this.toStringAndCheck(this.createHttpEntity(HttpHeaderName.CONTENT_LENGTH, 257L,
                HttpHeaderName.CONTENT_TYPE, MediaType.BINARY,
                HttpHeaderName.SERVER, "Server 123", letters),
                "Content-Length: 257\r\n" +
                        "Content-Type: application/octet-stream\r\n" +
                        "Server: Server 123\r\n\r\n" +
                        "00000000 0a 00 63 64 65 66 67 68 69 6a 6b 6c 6d 6e 6f 70 ..cdefghijklmnop" + LineEnding.SYSTEM +
                        "00000010 71                                              q               " + LineEnding.SYSTEM);
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

    @Override
    public Class<HttpEntityBinary> type() {
        return HttpEntityBinary.class;
    }
}
