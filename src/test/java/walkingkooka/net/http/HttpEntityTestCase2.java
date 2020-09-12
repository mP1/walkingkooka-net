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
import walkingkooka.Cast;
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.ToStringTesting;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.map.Maps;
import walkingkooka.net.header.HeaderValueException;
import walkingkooka.net.header.HttpHeaderName;
import walkingkooka.net.header.MediaType;
import walkingkooka.net.http.server.FakeWebFile;
import walkingkooka.net.http.server.WebFileException;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class HttpEntityTestCase2<H extends HttpEntity> extends HttpEntityTestCase<H>
        implements HashCodeEqualsDefinedTesting2<H>,
        ToStringTesting<H> {

    HttpEntityTestCase2() {
        super();
    }

    @Test
    public final void testSetHeadersInvalidFails() {
        final HttpEntity entity = this.createHttpEntity();

        assertThrows(HeaderValueException.class, () -> entity.setHeaders(map(HttpHeaderName.ACCEPT_CHARSET, MediaType.TEXT_PLAIN)));
    }

    @Test
    public final void testSetHeaderNullNameFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createHttpEntity().setHeader(null, list(1L));
        });
    }

    @Test
    public final void testSetHeaderNullValueFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createHttpEntity().setHeader(HttpHeaderName.CONTENT_LENGTH, null);
        });
    }

    @Test
    public final void testSetHeaderInvalidValueFails() {
        final HttpHeaderName<?> header = HttpHeaderName.CONTENT_LENGTH;

        assertThrows(HeaderValueException.class, () -> {
            this.createHttpEntity()
                    .setHeader(header, Cast.to(list("INVALID VALUE requires Long")));
        });
    }
    
    @Test
    public final void testAddHeaderNullNameFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createHttpEntity().addHeader(null, 1L);
        });
    }

    @Test
    public final void testAddHeaderNullValueFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createHttpEntity().addHeader(HttpHeaderName.CONTENT_LENGTH, null);
        });
    }

    @Test
    public final void testAddHeaderInvalidValueFails() {
        assertThrows(HeaderValueException.class, () -> {
            this.createHttpEntity()
                    .addHeader((HttpHeaderName<?>) HttpHeaderName.CONTENT_LENGTH, Cast.to("INVALID VALUE requires Long"));
        });
    }

    @Test
    public final void testRemoveHeaderNullNameFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createHttpEntity().removeHeader(null);
        });
    }

    @Test
    public final void testSetHeadersNullFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createHttpEntity().setHeaders(null);
        });
    }

    @Test
    public final void testSetHeadersIncludesInvalidValueFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createHttpEntity().setHeaders(Maps.of(HttpHeaderName.ACCEPT, null));
        });
    }

    @Test
    public final void testSetHeadersSame() {
        final H entity = this.createHttpEntity();
        assertSame(entity, entity.setHeaders(entity.headers()));
    }

    @Test
    public final void testSetHeadersSame2() {
        final H entity = this.createHttpEntity();
        assertSame(entity, entity.setHeaders(new HashMap<>(entity.headers())));
    }

    // setBody..........................................................................................................

    @Test
    public final void testSetBodyNullFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createHttpEntity().setBody(null);
        });
    }

    @Test
    public final void testSetBodySame() {
        final H entity = this.createHttpEntity();
        assertSame(entity, entity.setBody(entity.body()));
    }

    @Test
    public final void testSetBodyEmpty() {
        final H entity = this.createHttpEntity();
        assertEquals(HttpEntity.NO_HEADERS, entity.headers());

        final HttpEntity empty = entity.setBody(Binary.EMPTY);
        assertEquals(HttpEntity.EMPTY, empty);

        this.check(empty);
    }

    // setBodyText......................................................................................................

    @Test
    public final void testSetBodyTextNullFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createHttpEntity().setBodyText(null);
        });
    }

    @Test
    public final void testSetBodyTextSame() {
        final H entity = this.createHttpEntity();
        assertSame(entity, entity.setBodyText(entity.bodyText()));
    }

    @Test
    public final void testSetBodyTextEmpty() {
        final H entity = this.createHttpEntity();
        assertEquals(HttpEntity.NO_HEADERS, entity.headers());

        final HttpEntity empty = entity.setBodyText("");
        assertEquals(HttpEntity.EMPTY, empty);

        this.check(empty);
    }

    // setBodyWebFile......................................................................................................

    @Test
    public final void testSetBodyTextNullWebFileFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createHttpEntity().setBody(null, Charset.defaultCharset());
        });
    }

    @Test
    public final void testSetBodyWebFileSame() {
        final H entity = this.createHttpEntity();
        assertSame(entity,
                entity.setBody(new FakeWebFile() {

                                   @Override
                                   public InputStream content() throws WebFileException {
                                       return entity.body().inputStream();
                                   }
                               },
                        Charset.defaultCharset()));
    }

    @Test
    public final void testSetBodyWebFileDifferent() {
        final H entity = this.createHttpEntity();
        final Binary differentBinary = Binary.with("different".getBytes(Charset.forName("UTF-8")));

        final HttpEntity different =
                entity.setBody(new FakeWebFile() {

                                   @Override
                                   public InputStream content() throws WebFileException {
                                       return differentBinary.inputStream();
                                   }
                               },
                        Charset.defaultCharset());
        assertEquals(differentBinary, different.body());
    }

    // helpers..........................................................................................................

    abstract H createHttpEntity();

    static <T> List<T> list(final T... values) {
        return Lists.of(values);
    }

    static Map<HttpHeaderName<?>, List<?>> map(final HttpHeaderName<?> header,
                                                    final Object value) {
        return Maps.of(header, list(value));
    }

    static Map<HttpHeaderName<?>, List<?>> map(final HttpHeaderName<?> header1,
                                                    final Object value1,
                                                    final HttpHeaderName<?> header2,
                                                    final Object value2) {
        return Maps.of(header1, list(value1), header2, list(value2));
    }

    final void check(final HttpEntity entity,
                     final Map<HttpHeaderName<?>, List<?>> headers,
                     final Binary body,
                     final String text) {
        this.check(entity, headers, body);
        this.check(entity, headers, text);
    }

    final void check(final HttpEntity entity,
                     final Map<HttpHeaderName<?>, List<?>> headers,
                     final Binary body) {
        this.check(entity, headers);
        this.check(entity, body);
    }

    final void check(final HttpEntity entity,
                     final Map<HttpHeaderName<?>, List<?>> headers,
                     final String text) {
        this.check(entity, headers);
        this.check(entity, text);
    }

    final void check(final HttpEntity entity,
                     final Map<HttpHeaderName<?>, List<?>> headers) {
        assertEquals(headers, entity.headers(), () -> "" + entity);
        this.check(entity);
    }

    final void check(final HttpEntity entity,
                     final Binary body) {
        assertEquals(body, entity.body(), () -> "" + entity);
        this.check(entity);
    }

    final void check(final HttpEntity entity,
                     final String text) {
        assertEquals(text, entity.bodyText(), () -> "" + entity);
        this.check(entity);
    }

    final void check(final HttpEntity entity) {
        if ((entity.body().size() == 0 || entity.bodyText().isEmpty()) && entity.headers().isEmpty()) {
            assertEquals(HttpEntityEmpty.class, entity.getClass(), () -> "Entity without headers, body/bodyText");
        }

        final Map<HttpHeaderName<?>, List<?>> headers = entity.headers();
        assertThrows(UnsupportedOperationException.class,
                () -> headers.put(HttpHeaderName.CONTENT_LENGTH, Lists.of(1L)),
                () -> "headers should be readonly of " + entity.getClass().getSimpleName() + " " + entity);
    }

    // HashCodeEqualsDefinedTesting.....................................................................................

    public final H createObject() {
        return this.createHttpEntity();
    }
}
