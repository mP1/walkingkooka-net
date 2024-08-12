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
import walkingkooka.net.header.ClientCookie;
import walkingkooka.net.header.Cookie;
import walkingkooka.net.header.HttpHeaderName;
import walkingkooka.net.header.MediaType;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

public final class HttpEntityEmptyTest extends HttpEntityTestCase2<HttpEntityEmpty> {

    @Test
    public void testContentLength() {
        this.contentLengthAndCheck(this.createHttpEntity(), 0L);
    }

    // setHeaders........................................................................................................

    @Test
    public void testSetHeadersDifferent() {
        final HttpEntity entity = this.createHttpEntity();

        final Map<HttpHeaderName<?>, List<?>> headers = map(HttpHeaderName.CONTENT_TYPE, MediaType.TEXT_PLAIN);
        final HttpEntity different = entity.setHeaders(headers);
        assertNotSame(entity, different);

        this.check(different, headers);
    }

    @Test
    public void testSetHeaders() {
        final HttpEntityEmpty entity = this.createHttpEntity();
        this.checkEquals(
                HttpEntity.NO_HEADERS,
                entity.headers()
        );

        final HttpHeaderName<Long> header = HttpHeaderName.CONTENT_LENGTH;
        final Long value = 1L;
        final Map<HttpHeaderName<?>, List<?>> headers = Maps.of(header, list(value));

        final HttpEntity set = entity.setHeaders(headers);
        assertNotSame(entity, set);

        this.check(set, headers, entity.body(), entity.bodyText());
    }

    @Test
    public void testSetHeadersList() {
        final HttpEntityEmpty entity = this.createHttpEntity();
        this.checkEquals(
                HttpEntity.NO_HEADERS,
                entity.headers()
        );

        final HttpHeaderName<List<ClientCookie>> header = HttpHeaderName.COOKIE;
        final List<ClientCookie> value = Cookie.parseClientHeader("cookie1=value1");
        final Map<HttpHeaderName<?>, List<?>> headers = Maps.of(header, list(value));

        final HttpEntity set = entity.setHeaders(headers);
        assertNotSame(entity, set);

        this.check(set, headers, entity.body(), entity.bodyText());
    }

    // setHeader........................................................................................................

    @Test
    public void testSetHeader() {
        final HttpEntityEmpty entity = this.createHttpEntity();

        final HttpHeaderName<Long> header = HttpHeaderName.CONTENT_LENGTH;
        final List<Long> value = list(111L);
        final HttpEntity added = entity.setHeader(header, value);

        assertNotSame(entity, added);

        this.check(added,
                Maps.of(header, value),
                Binary.EMPTY);
    }

    @Test
    public void testAddHeader() {
        final HttpEntity entity = this.createHttpEntity();

        final HttpHeaderName<Long> header = HttpHeaderName.CONTENT_LENGTH;
        final Long value = 111L;
        final HttpEntity added = entity.addHeader(header, value);

        assertNotSame(entity, added);

        this.check(added,
                map(header, value),
                Binary.EMPTY);
    }

    @Test
    public void testRemoveHeader() {
        final HttpEntityEmpty entity = HttpEntityEmpty.instance();
        assertSame(entity, entity.removeHeader(HttpHeaderName.ACCEPT));
    }

    // contentType......................................................................................................

    @Test
    public void testContentType() {
        this.contentTypeAndCheck(
                HttpEntity.EMPTY
        );
    }

    // setContentType...................................................................................................

    @Test
    public void testSetContentType() {
        this.setContentTypeAndCheck(
                HttpEntity.EMPTY,
                MediaType.TEXT_PLAIN,
                HttpEntity.EMPTY.setContentType(MediaType.TEXT_PLAIN)
        );
    }

    // isMultipartFormData..............................................................................................

    @Test
    public void testIsMultipartFormData() {
        this.isMultipartFormDataAndCheck(
                HttpEntity.EMPTY,
                false
        );
    }

    // body.............................................................................................................

    @Test
    public void testSetBody() {
        final HttpEntity entity = this.createHttpEntity();

        final Binary body = Binary.with(new byte[1]);
        final HttpEntity set = entity.setBody(body);
        assertNotSame(entity, set);

        this.check(set,
                HttpEntity.NO_HEADERS,
                body);
    }

    @Test
    public void testSetBodyText() {
        final HttpEntity entity = this.createHttpEntity();

        final String text = "abc";
        final HttpEntity set = entity.setBodyText(text);
        assertNotSame(entity, set);

        this.check(set,
                HttpEntity.NO_HEADERS,
                text);
    }

    @Override
    HttpEntityEmpty createHttpEntity() {
        return HttpEntityEmpty.instance();
    }

    // TreePrintable....................................................................................................

    @Test
    public void testTreePrint() {
        this.treePrintAndCheck(
                HttpEntityEmpty.instance(),
                "HttpEntity\n"
        );
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck("", this.createHttpEntity().toString());
    }

    // class...........................................................................................................

    @Override
    public Class<HttpEntityEmpty> type() {
        return HttpEntityEmpty.class;
    }
}
