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
import walkingkooka.collect.map.Maps;
import walkingkooka.net.header.HeaderValueException;
import walkingkooka.net.header.HttpHeaderName;
import walkingkooka.net.header.MediaType;

import java.util.HashMap;
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

        assertThrows(HeaderValueException.class, () -> entity.setHeaders(Maps.of(HttpHeaderName.ACCEPT_CHARSET, MediaType.TEXT_PLAIN)));
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

    // helpers..........................................................................................................

    abstract H createHttpEntity();

    final void check(final HttpEntity entity,
                     final Map<HttpHeaderName<?>, Object> headers,
                     final Binary body,
                     final String text) {
        this.check(entity, headers, body);
        this.check(entity, headers, text);
    }

    final void check(final HttpEntity entity,
                     final Map<HttpHeaderName<?>, Object> headers,
                     final Binary body) {
        this.check(entity, headers);
        this.check(entity, body);
    }

    final void check(final HttpEntity entity,
                     final Map<HttpHeaderName<?>, Object> headers,
                     final String text) {
        this.check(entity, headers);
        this.check(entity, text);
    }

    final void check(final HttpEntity entity,
                     final Map<HttpHeaderName<?>, Object> headers) {
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

        final Map<HttpHeaderName<?>, Object> headers = entity.headers();
        assertThrows(UnsupportedOperationException.class,
                () -> headers.put(HttpHeaderName.CONTENT_LENGTH, 1L),
                () -> "headers should be readonly of " + entity.getClass().getSimpleName() + " " + entity);
    }

    // HashCodeEqualsDefinedTesting.....................................................................................

    public final H createObject() {
        return this.createHttpEntity();
    }
}
