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

package walkingkooka.net.http.j2cl;

import org.junit.jupiter.api.Test;
import walkingkooka.Binary;
import walkingkooka.Cast;
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.ToStringTesting;
import walkingkooka.collect.map.Maps;
import walkingkooka.javashader.ShadedClassTesting;
import walkingkooka.net.header.CharsetName;
import walkingkooka.net.header.ETag;
import walkingkooka.net.header.ETagValidator;
import walkingkooka.net.header.HeaderValueException;
import walkingkooka.net.header.HttpHeaderName;
import walkingkooka.net.header.MediaType;
import walkingkooka.predicate.Predicates;
import walkingkooka.reflect.PackageName;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class HttpEntityTest implements ShadedClassTesting<HttpEntity>,
        HashCodeEqualsDefinedTesting2,
        ToStringTesting<HttpEntity> {

    private final static HttpHeaderName<MediaType> HEADER = HttpHeaderName.CONTENT_TYPE;
    private final static MediaType HEADER_VALUE = MediaType.TEXT_PLAIN;

    private final static Map<HttpHeaderName<?>, Object> HEADERS = Maps.of(HEADER, HEADER_VALUE);
    private final static Map<HttpHeaderName<?>, Object> INVALID_HEADERS = Maps.of(HttpHeaderName.SERVER, 999L);

    private final static HttpHeaderName<?> DIFFERENT_HEADER = HttpHeaderName.SERVER;

    // constants.......................................................................................

    @Test
    public void testEmptyConstant() {
        this.check(HttpEntity.EMPTY, HttpEntity.NO_HEADERS, new byte[0]);
    }

    // setHeaders ....................................................................................................

    @Test
    public void testSetHeadersNullFails() {
        assertThrows(NullPointerException.class, () -> this.createHttpEntity().setHeaders(null));
    }

    @Test
    public void testSetHeaderInvalidFails() {
        assertThrows(HeaderValueException.class, () -> this.createHttpEntity().setHeaders(INVALID_HEADERS));
    }

    @Test
    public void testSetHeadersSame() {
        final HttpEntity entity = this.createHttpEntity();
        assertSame(entity, entity.setHeaders(entity.headers()));
    }

    @Test
    public void testSetHeadersSame2() {
        final HttpEntity entity = this.createHttpEntity().setHeaders(HEADERS);
        assertSame(entity, entity.setHeaders(HEADERS));
    }

    @Test
    public void testSetHeadersDifferent() {
        final HttpEntity entity = this.createHttpEntity();
        final Map<HttpHeaderName<?>, Object> headers = Maps.of(HttpHeaderName.E_TAG, ETag.with("different", ETagValidator.STRONG));
        final HttpEntity different = entity.setHeaders(headers);
        this.check(different, headers, this.body());
    }

    // addHeader ....................................................................................................

    @Test
    public void testAddHeaderNullNameFails() {
        assertThrows(NullPointerException.class, () -> this.createHttpEntity().addHeader(null, "*value*"));
    }

    @Test
    public void testAddHeaderNullValueFails() {
        assertThrows(NullPointerException.class, () -> this.createHttpEntity().addHeader(HttpHeaderName.SERVER, null));
    }

    @Test
    public void testAddHeaderInvalidValueFails() {
        assertThrows(HeaderValueException.class, () -> this.createHttpEntity().addHeader(Cast.to(HttpHeaderName.CONTENT_LENGTH), "*invalid*"));
    }

    @Test
    public void testAddHeaderExisting() {
        final HttpEntity entity = this.createHttpEntity();
        assertSame(entity, entity.addHeader(HttpHeaderName.CONTENT_TYPE, MediaType.TEXT_PLAIN));
    }

    @Test
    public void testAddHeaderReplaceValue() {
        final HttpEntity entity = this.createHttpEntity();

        final MediaType headerValue = MediaType.ANY_IMAGE;
        this.check(entity.addHeader(HEADER, headerValue),
                Maps.of(HEADER, headerValue, HttpHeaderName.CONTENT_LENGTH, 26L),
                this.body());
    }

    @Test
    public void testAddHeader() {
        final HttpEntity entity = this.createHttpEntity();

        final HttpHeaderName<String> header = HttpHeaderName.SERVER;
        final String headerValue = "*Server*";

        final HttpEntity different = entity.addHeader(header, headerValue);

        final Map<HttpHeaderName<?>, Object> headers = Maps.ordered();
        headers.putAll(entity.headers());
        headers.put(header, headerValue);

        this.check(different, headers, this.body());
    }

    // setContentLength ................................................................................................

    @Test
    public void testSetContentLengthMissing() {
        final HttpEntity entity = HttpEntity.text(MediaType.TEXT_PLAIN, "abc")
                .setHeaders(HttpEntity.NO_HEADERS);
        final HttpEntity withContentLength = entity.setContentLength();
        assertNotSame(entity, withContentLength);

        assertEquals(Maps.of(HttpHeaderName.CONTENT_LENGTH, 3L), withContentLength.headers(), "headers");
        assertEquals(entity.bodyText(), withContentLength.bodyText(), "bodyText");
    }

    @Test
    public void testSetContentLengthReplaces() {
        final HttpEntity entity = HttpEntity.text(MediaType.TEXT_PLAIN, "abc")
                .setHeaders(Maps.of(HttpHeaderName.CONTENT_LENGTH, 999L));
        final HttpEntity withContentLength = entity.setContentLength();
        assertNotSame(entity, withContentLength);

        assertEquals(Maps.of(HttpHeaderName.CONTENT_LENGTH, 3L), withContentLength.headers(), "headers");
        assertEquals(entity.bodyText(), withContentLength.bodyText(), "body");
    }

    @Test
    public void testSetContentLengthUnnecessary() {
        final HttpEntity entity = HttpEntity.text(MediaType.TEXT_HTML, "hello");
        assertEquals(Long.valueOf(entity.bodyText().length()), entity.headers().get(HttpHeaderName.CONTENT_LENGTH));
        assertSame(entity, entity.setContentLength());
    }

    // removeHeader ....................................................................................................

    @Test
    public void testRemoveHeaderNullFails() {
        assertThrows(NullPointerException.class, () -> this.createHttpEntity().removeHeader(null));
    }

    @Test
    public void testRemoveHeaderSame() {
        final HttpEntity entity = this.createHttpEntity();
        assertSame(entity, entity.removeHeader(DIFFERENT_HEADER));
    }

    @Test
    public void testRemoveHeader() {
        final HttpEntity entity = HttpEntity.text(MediaType.TEXT_PLAIN, this.bodyText());
        this.check(entity.removeHeader(HttpHeaderName.CONTENT_TYPE),
                Maps.of(HttpHeaderName.CONTENT_LENGTH, 26L),
                this.body());
    }

    // setBodyText ....................................................................................................

    @Test
    public void testSetBodyTextNullFails() {
        assertThrows(NullPointerException.class, () -> this.createHttpEntity().setBodyText(null));
    }

    @Test
    public void testSetBodyTextSame() {
        final HttpEntity entity = this.createHttpEntity();
        assertSame(entity, entity.setBodyText(entity.bodyText()));
    }

    @Test
    public void testSetBodyTextDifferent() {
        final HttpEntity entity = this.createHttpEntity();
        final String bodyText = "different";
        final HttpEntity different = entity.setBodyText(bodyText);
        this.check(different, entity.headers(), bodyText.getBytes(CHARSET));
    }

    @Test
    public void testSetBodyTextDifferent2() {
        final HttpEntity entity = this.createHttpEntity().setHeaders(HEADERS);
        final String bodyText = "different";
        final HttpEntity different = entity.setBodyText(bodyText);
        this.check(different, HEADERS, bodyText.getBytes(CHARSET));
    }

    // toString ....................................................................................................

    @Test
    public void testToStringText() {
        final Map<HttpHeaderName<?>, Object> headers = Maps.of(HttpHeaderName.CONTENT_LENGTH, 257L,
                HttpHeaderName.CONTENT_TYPE, MediaType.TEXT_PLAIN.setCharset(CharsetName.UTF_8),
                HttpHeaderName.SERVER, "Server 123");

        this.toStringAndCheck(HttpEntity.text(MediaType.TEXT_PLAIN, "ABC")
                        .setHeaders(headers),
                "Content-Length: 257\r\nContent-Type: text/plain; charset=UTF-8\r\nServer: Server 123\r\n\r\nABC");
    }

    // factory text............................................................................................

    @Test
    public void testTextContentTypeNullFails() {
        assertThrows(NullPointerException.class, () -> HttpEntity.text(null,
                this.text()));
    }

    @Test
    public void testTextBodyNullFails() {
        assertThrows(NullPointerException.class, () -> HttpEntity.text(this.contentType(),
                null));
    }

    @Test
    public void testTextContentTypeUnsupportedCharsetFails() {
        this.textAndCheck("ABC123", MediaType.TEXT_HTML, HttpEntity.DEFAULT_BODY_CHARSET);
    }

    @Test
    public void testTextContentTypeMissingCharset() {
        this.textAndCheck("ABC123", MediaType.TEXT_HTML, HttpEntity.DEFAULT_BODY_CHARSET);
    }

    @Test
    public void testTextContentType() {
        this.textAndCheck("ABC123", MediaType.parse("text/html;charset=UTF-8"), Charset.forName("UTF-8"));
    }

    private void textAndCheck(final String text,
                              final MediaType contentType,
                              final Charset charset) {
        final byte[] body = text.getBytes(charset);

        final Map<HttpHeaderName<?>, Object> headers = Maps.ordered();
        headers.put(HttpHeaderName.CONTENT_LENGTH, (long) body.length);
        headers.put(HttpHeaderName.CONTENT_TYPE, contentType);

        this.check(HttpEntity.text(contentType, text),
                headers,
                body);
    }

    private MediaType contentType() {
        return this.contentType(CharsetName.UTF_8);
    }

    private MediaType contentType(final CharsetName charsetName) {
        return MediaType.TEXT_PLAIN.setCharset(charsetName);
    }

    private String text() {
        return "abc123";
    }

    // equals.............................................................................................

    @Test
    public void testEqualsDifferentHeaders() {
        this.checkNotEquals(this.createHttpEntity().setHeaders(Maps.of(HttpHeaderName.SERVER, "different")));
    }

    @Test
    public void testEqualsDifferentBodyText() {
        this.checkNotEquals(this.createHttpEntity().setBodyText("different"));
    }

    // helpers................................................................................................

    private HttpEntity createHttpEntity() {
        return HttpEntity.text(MediaType.TEXT_PLAIN, this.bodyText());
    }

    private void check(final HttpEntity entity,
                       final Map<HttpHeaderName<?>, Object> headers,
                       final byte[] body) {
        this.check(entity, headers, Binary.with(body));
    }

    private void check(final HttpEntity entity,
                       final Map<HttpHeaderName<?>, Object> headers,
                       final Binary body) {
        assertEquals(headers, entity.headers(), "headers");
        assertEquals(new String(body.value(), CHARSET), entity.bodyText(), "bodyText");
    }

    private Binary body() {
        return Binary.with(bodyText().getBytes(CHARSET));
    }

    private String bodyText() {
        return "abcdefghijklmnopqrstuvwxyz";
    }


    private final static Charset CHARSET = Charset.forName("utf8");

    // ShadedClassTesting...............................................................................................

    @Override
    public Class<HttpEntity> type() {
        return HttpEntity.class;
    }

    @Override
    public final Predicate<Constructor> requiredConstructors() {
        return Predicates.always();
    }

    @Override
    public final Predicate<Method> requiredMethods() {
        return Predicates.always();
    }

    @Override
    public final Predicate<Field> requiredFields() {
        return Predicates.always();
    }

    @Override
    public UnaryOperator<Class<?>> typeMapper() {
        return ShadedClassTesting.typeMapper(PackageName.from(this.getClass().getPackage()),
                PackageName.from(walkingkooka.net.http.HttpEntity.class.getPackage()));
    }

    // HashCodeEqualsDefinedTesting2....................................................................................

    @Override
    public HttpEntity createObject() {
        return this.createHttpEntity();
    }
}
