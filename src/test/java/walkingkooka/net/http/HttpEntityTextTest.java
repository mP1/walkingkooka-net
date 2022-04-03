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
import walkingkooka.Cast;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.map.Maps;
import walkingkooka.net.header.CharsetName;
import walkingkooka.net.header.HttpHeaderName;
import walkingkooka.net.header.MediaType;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

public final class HttpEntityTextTest extends HttpEntityNotEmptyTestCase<HttpEntityText> {

    private final static String DIFFERENT_TEXT = "different-text";

    @Test
    public void testContentLength() {
        this.contentLengthAndCheck(this.createHttpEntity(), TEXT.length());
    }

    @Test
    public void testContentLengthTextAndByteLengthDifferentContentTypeUtf8() {
        final String text = "\u0256\u0257";
        final int bytesLength = text.getBytes(StandardCharsets.UTF_8).length;
        this.checkNotEquals(text.length(), bytesLength, "text and encoded byte lengths should be different");

        this.contentLengthAndCheck(HttpEntityText.with(Cast.to(Maps.of(HttpHeaderName.CONTENT_TYPE, Lists.of(MediaType.parse("text/plain; charset=UTF8")))), text), bytesLength);
    }

    @Test
    public void testSetHeadersDifferent() {
        final HttpEntity entity = this.createHttpEntity();

        final Map<HttpHeaderName<?>, List<?>> headers = map(HttpHeaderName.CONTENT_TYPE, MediaType.TEXT_PLAIN);
        final HttpEntity different = entity.setHeaders(headers);
        assertNotSame(entity, different);

        this.check(different, headers, TEXT);
    }

    @Test
    public void testRemoveHeader() {
        final HttpEntityText entity = this.createHttpEntity();
        this.checkEquals(HttpEntity.NO_HEADERS, entity.headers());

        final HttpHeaderName<Long> header = HttpHeaderName.CONTENT_LENGTH;
        final Long value = 1L;

        final HttpEntity added = entity.addHeader(header, value);
        final HttpEntity removed = added.removeHeader(header);
        assertNotSame(added, removed);

        this.check(removed, HttpEntity.NO_HEADERS, TEXT);
    }

    @Test
    public void testBodyText() {
        final HttpEntity entity = this.createHttpEntity();
        final HttpEntity different = entity
                .setBodyText(DIFFERENT_TEXT);
        assertNotSame(entity, different);

        this.check(different, HttpEntity.NO_HEADERS, DIFFERENT_TEXT);
    }

    @Test
    public void testBodyTextWithHeaders() {
        final HttpEntity entity = this.createHttpEntity()
                .addHeader(HttpHeaderName.CONTENT_LENGTH, 777L);

        final HttpEntity different = entity
                .setBodyText(DIFFERENT_TEXT);
        assertNotSame(entity, different);

        this.check(entity, map(HttpHeaderName.CONTENT_LENGTH, 777L), TEXT);
    }

    @Test
    public void testSetBodySameBinary() {
        final HttpEntity entity = this.createHttpEntity();
        assertSame(entity, entity.setBody(BINARY));
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(HttpEntityText.with(Maps.of(HttpHeaderName.CONTENT_LENGTH, HttpEntityHeaderList.one(HttpHeaderName.CONTENT_LENGTH, 257L),
                        HttpHeaderName.CONTENT_TYPE, HttpEntityHeaderList.one(HttpHeaderName.CONTENT_TYPE, MediaType.TEXT_PLAIN.setCharset(CharsetName.UTF_8)),
                        HttpHeaderName.SERVER, HttpEntityHeaderList.one(HttpHeaderName.SERVER, "Server 123")), "AB\nC"),
                "Content-Length: 257\r\nContent-Type: text/plain; charset=UTF-8\r\nServer: Server 123\r\n\r\nAB\nC");
    }

    // helpers..........................................................................................................

    @Override
    HttpEntityText createHttpEntity(final Map<HttpHeaderName<?>, HttpEntityHeaderList> headers) {
        return HttpEntityText.with(headers, TEXT);
    }

    @Override
    public Class<HttpEntityText> type() {
        return HttpEntityText.class;
    }
}
