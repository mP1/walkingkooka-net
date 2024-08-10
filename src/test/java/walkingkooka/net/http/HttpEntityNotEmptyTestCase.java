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
import walkingkooka.collect.Range;
import walkingkooka.collect.map.Maps;
import walkingkooka.net.header.AcceptLanguage;
import walkingkooka.net.header.ClientCookie;
import walkingkooka.net.header.Cookie;
import walkingkooka.net.header.HttpHeaderName;
import walkingkooka.net.header.MediaType;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

public abstract class HttpEntityNotEmptyTestCase<H extends HttpEntityNotEmpty> extends HttpEntityTestCase2<H> {

    final static String TEXT = "abcdefghijklmnopqrstuvwxyz";
    final static Binary BINARY = Binary.with(TEXT.getBytes(HttpEntity.DEFAULT_BODY_CHARSET));

    HttpEntityNotEmptyTestCase() {
        super();
    }

    // setHeaders.......................................................................................................

    @Test
    public final void testSetHeaders() {
        final H entity = this.createHttpEntity();
        this.checkEquals(HttpEntity.NO_HEADERS2, entity.headers());

        final HttpHeaderName<Long> header = HttpHeaderName.CONTENT_LENGTH;
        final Long value = 1L;
        final Map<HttpHeaderName<?>, List<?>> headers = Maps.of(header, list(value));

        final HttpEntity set = entity.setHeaders(headers);
        assertNotSame(entity, set);

        this.check(set, headers, entity.body(), entity.bodyText());
    }

    @Test
    public final void testSetHeadersList() {
        final H entity = this.createHttpEntity();
        this.checkEquals(HttpEntity.NO_HEADERS2, entity.headers());

        final HttpHeaderName<List<ClientCookie>> header = HttpHeaderName.COOKIE;
        final List<ClientCookie> value = Cookie.parseClientHeader("cookie1=value1");
        final Map<HttpHeaderName<?>, List<?>> headers = Maps.of(header, list(value));

        final HttpEntity set = entity.setHeaders(headers);
        assertNotSame(entity, set);

        this.check(set, headers, entity.body(), entity.bodyText());
    }

    @Test
    public final void testSetHeadersList2() {
        final H entity = this.createHttpEntity();
        this.checkEquals(HttpEntity.NO_HEADERS2, entity.headers());

        final HttpHeaderName<List<ClientCookie>> header = HttpHeaderName.COOKIE;
        final List<ClientCookie> value = Cookie.parseClientHeader("cookie1=value1;cookie2=value2");
        final Map<HttpHeaderName<?>, List<?>> headers = Maps.of(header, list(value));

        final HttpEntity set = entity.setHeaders(headers);
        assertNotSame(entity, set);

        this.check(set, headers, entity.body(), entity.bodyText());
    }

    // set..............................................................................................................

    @Test
    public final void testSetHeaderNew() {
        final H entity = this.createHttpEntity();
        this.checkEquals(HttpEntity.NO_HEADERS2, entity.headers());

        final HttpHeaderName<Long> header = HttpHeaderName.CONTENT_LENGTH;
        final Long value = 1L;

        final HttpEntity set = entity.setHeader(header, list(value));
        assertNotSame(entity, set);

        this.check(set, map(header, value), entity.body(), entity.bodyText());
    }

    @Test
    public final void testSetHeaderNewList() {
        final H entity = this.createHttpEntity();
        this.checkEquals(HttpEntity.NO_HEADERS2, entity.headers());

        final HttpHeaderName<List<ClientCookie>> header = HttpHeaderName.COOKIE;
        final List<ClientCookie> value = ClientCookie.parseHeader("cookie1=value1");
        this.checkEquals(1, value.size());

        final HttpEntity set = entity.setHeader(header, list(value));
        assertNotSame(entity, set);

        this.check(set, map(header, value), entity.body(), entity.bodyText());
    }

    @Test
    public final void testSetHeaderNewList2() {
        final H entity = this.createHttpEntity();
        this.checkEquals(HttpEntity.NO_HEADERS2, entity.headers());

        final HttpHeaderName<List<ClientCookie>> header = HttpHeaderName.COOKIE;
        final List<ClientCookie> value = ClientCookie.parseHeader("cookie1=value1;cookie2=value2");
        this.checkEquals(2, value.size());

        final HttpEntity set = entity.setHeader(header, list(value));
        assertNotSame(entity, set);

        this.check(set, map(header, value), entity.body(), entity.bodyText());
    }

    @Test
    public final void testSetHeaderTwice() {
        final H entity = this.createHttpEntity();
        this.checkEquals(HttpEntity.NO_HEADERS2, entity.headers());

        final HttpHeaderName<Long> header = HttpHeaderName.CONTENT_LENGTH;
        final Long value = 1L;

        final HttpEntity set = entity.setHeader(header, list(value));
        assertNotSame(entity, set);

        this.check(set, map(header, value), entity.body(), entity.bodyText());

        final HttpEntity again = set.setHeader(header, list(value));
        assertSame(set, again);
        this.check(again, map(header, value), entity.body(), entity.bodyText());
    }

    @Test
    public final void testSetHeaderDifferent() {
        final H entity = this.createHttpEntity();
        this.checkEquals(HttpEntity.NO_HEADERS2, entity.headers());

        final HttpHeaderName<Long> header1 = HttpHeaderName.CONTENT_LENGTH;
        final Long value1 = 1L;

        final HttpEntity set1 = entity.setHeader(header1, list(value1));
        assertNotSame(entity, set1);

        this.check(set1, map(header1, value1), entity.body(), entity.bodyText());

        final HttpHeaderName<MediaType> header2 = HttpHeaderName.CONTENT_TYPE;
        final MediaType value2 = MediaType.TEXT_PLAIN;

        final HttpEntity set2 = set1.setHeader(header2, list(value2));
        assertNotSame(entity, set2);

        this.check(set2, map(header1, value1, header2, value2), entity.body(), entity.bodyText());
    }

    @Test
    public final void testSetHeaderDifferent2() {
        final H entity = this.createHttpEntity();
        this.checkEquals(HttpEntity.NO_HEADERS2, entity.headers());

        final HttpHeaderName<Long> header1 = HttpHeaderName.CONTENT_LENGTH;
        final Long value1 = 1L;

        final HttpEntity set1 = entity.setHeader(header1, list(value1));
        assertNotSame(entity, set1);

        this.check(set1, map(header1, value1), entity.body(), entity.bodyText());

        final HttpHeaderName<MediaType> header2 = HttpHeaderName.CONTENT_TYPE;
        final MediaType value2 = MediaType.TEXT_PLAIN;

        final HttpEntity set2 = set1.setHeader(header2, list(value2));
        assertNotSame(entity, set2);

        this.check(set2, map(header1, value1, header2, value2), entity.body(), entity.bodyText());

        final MediaType value3 = MediaType.TEXT_XML;
        final HttpEntity set3 = set2.setHeader(header2, list(value3));
        assertNotSame(entity, set3);

        this.check(set3, map(header1, value1, header2, value3), entity.body(), entity.bodyText());
    }

    @Test
    public final void testSetHeaderEmptyReplace() {
        final H entity = this.createHttpEntity();
        this.checkEquals(HttpEntity.NO_HEADERS2, entity.headers());

        final HttpHeaderName<Long> header1 = HttpHeaderName.CONTENT_LENGTH;
        final Long value1 = 1L;

        final HttpEntity set1 = entity.setHeader(header1, list(value1));
        assertNotSame(entity, set1);

        this.check(set1, map(header1, value1), entity.body(), entity.bodyText());

        final HttpEntity set2 = set1.setHeader(header1, list());
        assertNotSame(set1, set2);
        this.checkEquals(entity, set2);
    }

    @Test
    public final void testSetHeaderEmptyReplace2() {
        final H entity = this.createHttpEntity();
        this.checkEquals(HttpEntity.NO_HEADERS2, entity.headers());

        final HttpHeaderName<Long> header1 = HttpHeaderName.CONTENT_LENGTH;
        final Long value1 = 1L;

        final HttpHeaderName<MediaType> header2 = HttpHeaderName.CONTENT_TYPE;
        final MediaType value2 = MediaType.parse("custom/media-type2");

        final HttpEntity set1 = entity.setHeader(header1, list(value1))
                .setHeader(header2, list(value2));
        assertNotSame(entity, set1);

        this.check(set1, map(header1, value1, header2, value2), entity.body(), entity.bodyText());

        final HttpEntity set2 = set1.setHeader(header2, list());
        assertNotSame(set1, set2);

        this.check(set2, map(header1, value1), entity.body(), entity.bodyText());
    }

    // add..............................................................................................................

    @Test
    public final void testAddHeaderNew() {
        final H entity = this.createHttpEntity();
        this.checkEquals(HttpEntity.NO_HEADERS2, entity.headers());

        final HttpHeaderName<Long> header = HttpHeaderName.CONTENT_LENGTH;
        final Long value = 1L;

        final HttpEntity added = entity.addHeader(header, value);
        assertNotSame(entity, added);

        this.check(added, map(header, value), entity.body(), entity.bodyText());
    }

    @Test
    public final void testAddHeaderListNew() {
        final H entity = this.createHttpEntity();
        this.checkEquals(HttpEntity.NO_HEADERS2, entity.headers());

        final HttpHeaderName<List<ClientCookie>> header = HttpHeaderName.COOKIE;
        final List<ClientCookie> value = Cookie.parseClientHeader("cooke1=value1;cookie2=value2");

        final HttpEntity added = entity.addHeader(header, value);
        assertNotSame(entity, added);

        this.check(added, map(header, value), entity.body(), entity.bodyText());
    }

    @Test
    public final void testAddHeaderSameValueTwice() {
        final H entity = this.createHttpEntity();
        this.checkEquals(HttpEntity.NO_HEADERS2, entity.headers());

        final HttpHeaderName<Long> header = HttpHeaderName.CONTENT_LENGTH;
        final Long value = 1L;

        final HttpEntity added = entity.addHeader(header, value);
        assertNotSame(entity, added);

        this.check(added, map(header, value), entity.body(), entity.bodyText());

        final HttpEntity again = added.addHeader(header, value);
        assertSame(added, again);
    }

    @Test
    public final void testAddHeaderSameValueTwiceMulti() {
        final H entity = this.createHttpEntity();
        this.checkEquals(HttpEntity.NO_HEADERS2, entity.headers());

        final HttpHeaderName<AcceptLanguage> header = HttpHeaderName.ACCEPT_LANGUAGE;
        final AcceptLanguage value = AcceptLanguage.parse("EN");

        final HttpEntity added = entity.addHeader(header, value);
        assertNotSame(entity, added);

        this.check(added, map(header, value), entity.body(), entity.bodyText());

        assertSame(added, added.addHeader(header, value));
    }

    @Test
    public final void testAddHeaderDifferentValueMulti() {
        final H entity = this.createHttpEntity();
        this.checkEquals(HttpEntity.NO_HEADERS2, entity.headers());

        final HttpHeaderName<AcceptLanguage> header = HttpHeaderName.ACCEPT_LANGUAGE;
        final AcceptLanguage value1 = AcceptLanguage.parse("EN");

        final HttpEntity added1 = entity.addHeader(header, value1);
        assertNotSame(entity, added1);

        this.check(added1, map(header, value1), entity.body(), entity.bodyText());

        final AcceptLanguage value2 = AcceptLanguage.parse("FR");

        final HttpEntity added2 = added1.addHeader(header, value2);
        assertNotSame(added1, added2);
        this.check(added2, Maps.of(header, list(value1, value2)), entity.body(), entity.bodyText());
    }

    @Test
    public final void testAddHeaderSetBody() {
        final H entity = this.createHttpEntity();
        this.checkEquals(HttpEntity.NO_HEADERS2, entity.headers());

        final HttpHeaderName<Long> header = HttpHeaderName.CONTENT_LENGTH;
        final Long value = 1L;

        final HttpEntity added = entity.addHeader(header, value);
        assertNotSame(entity, added);

        this.check(added, map(header, value), entity.body(), entity.bodyText());

        final Binary different = Binary.with("xyz".getBytes(HttpEntity.DEFAULT_BODY_CHARSET));
        final HttpEntity differentBody = added.setBody(different);
        assertNotSame(added, differentBody);

        this.check(differentBody, map(header, value), different);
    }

    @Test
    public final void testAddHeaderSetBodyText() {
        final H entity = this.createHttpEntity();
        this.checkEquals(HttpEntity.NO_HEADERS2, entity.headers());

        final HttpHeaderName<Long> header = HttpHeaderName.CONTENT_LENGTH;
        final Long value = 1L;

        final HttpEntity added = entity.addHeader(header, value);
        assertNotSame(entity, added);

        this.check(added, map(header, value), entity.body(), entity.bodyText());

        final String different = "different-text";
        final HttpEntity differentBody = added.setBodyText(different);
        assertNotSame(added, differentBody);

        this.check(differentBody, map(header, value), different);
    }

    // remove..............................................................................................................

    @Test
    public final void testRemoveHeaderBecomesEmpty() {
        final H entity = this.createHttpEntity();
        this.checkEquals(HttpEntity.NO_HEADERS2, entity.headers());

        final HttpHeaderName<Long> header = HttpHeaderName.CONTENT_LENGTH;
        final Long value = 1L;

        final HttpEntity added = entity.addHeader(header, value);
        final HttpEntity removed = added.removeHeader(header);
        assertNotSame(added, removed);

        this.check(removed, HttpEntity.NO_HEADERS);
    }

    @Test
    public final void testRemoveHeaderUnknown() {
        final H entity = this.createHttpEntity();
        this.checkEquals(HttpEntity.NO_HEADERS2, entity.headers());

        final HttpHeaderName<Long> header = HttpHeaderName.CONTENT_LENGTH;
        final Long value = 1L;

        final HttpEntity added = entity.addHeader(header, value);
        final HttpEntity removed = added.removeHeader(HttpHeaderName.ACCEPT);
        assertSame(added, removed);

        this.check(removed);
    }

    // setContentLength.................................................................................................

    @Test
    public final void testSetContentLengthMissing() {
        final HttpEntity entity = this.createHttpEntity();
        final HttpEntity set = entity.setContentLength();
        assertNotSame(entity, set);

        this.check(set,
                map(HttpHeaderName.CONTENT_LENGTH, Long.valueOf(TEXT.length())),
                BINARY,
                TEXT);
    }

    @Test
    public final void testSetContentLengthReplaces() {
        final HttpEntity entity = this.createHttpEntity()
                .addHeader(HttpHeaderName.CONTENT_LENGTH, 9999L);
        final HttpEntity set = entity.setContentLength();
        assertNotSame(entity, set);

        this.check(set,
                map(HttpHeaderName.CONTENT_LENGTH, Long.valueOf(TEXT.length())),
                BINARY,
                TEXT);
    }

    @Test
    public final void testSetContentLengthUnnecessary() {
        final HttpEntity entity = this.createHttpEntity();
        final HttpEntity set = entity.setContentLength();
        final HttpEntity already = set.setContentLength();
        assertSame(set, already);
    }

    // setContentType...................................................................................................

    @Test
    public final void testSetContentType() {
        final HttpEntity httpEntity = this.createHttpEntity();

        this.setContentTypeAndCheck(
                httpEntity,
                MediaType.TEXT_PLAIN,
                httpEntity.setContentType(MediaType.TEXT_PLAIN)
        );
    }

    // extractRange................................................................................................

    @Test
    public final void testExtractRangeSameWildcard() {
        final HttpEntity entity = this.createHttpEntity();
        assertSame(entity, entity.extractRange(Range.all()));
    }

    @Test
    public final void testExtractRange() {
        this.extractRangeAndCheck(0,
                0,
                new byte[]{'a'});
    }

    @Test
    public final void testExtractRange2() {
        this.extractRangeAndCheck(1,
                2,
                new byte[]{'b', 'c'});
    }

    @Test
    public final void testExtractRange3() {
        this.extractRangeAndCheck(22,
                25,
                new byte[]{'w', 'x', 'y', 'z'});
    }

    @Test
    public final void testExtractRange4() {
        this.extractRangeAndCheck(Range.greaterThanEquals(22L),
                Binary.with(new byte[]{'w', 'x', 'y', 'z'}));
    }

    private void extractRangeAndCheck(final long lower,
                                      final long upper,
                                      final byte[] expected) {
        this.extractRangeAndCheck(Range.greaterThanEquals(lower).and(Range.lessThanEquals(upper)),
                Binary.with(expected));
    }

    private void extractRangeAndCheck(final Range<Long> range,
                                      final Binary expected) {
        final HttpEntity entity = this.createHttpEntity();
        this.checkEquals(entity.setBody(expected),
                entity.extractRange(range),
                () -> entity + " extractRange " + range + " failed");
    }

    // equals...........................................................................................................

    @Test
    public final void testEqualsDifferentHeaders() {
        this.checkNotEquals(this.createHttpEntity(HttpHeaderName.CONTENT_LENGTH, 777L));
    }

    @Test
    public final void testEqualsDifferentHeaders2() {
        this.checkNotEquals(this.createHttpEntity(HttpHeaderName.CONTENT_LENGTH, 777L),
                this.createHttpEntity(HttpHeaderName.CONTENT_LENGTH, 999L));
    }

    @Test
    public final void testEqualsDifferentBody() {
        this.checkNotEquals(HttpEntityBinary.with(HttpEntity.NO_HEADERS2, Binary.with(new byte[1])));
    }

    @Test
    public final void testEqualsDifferentText() {
        this.checkNotEquals(HttpEntityText.with(HttpEntity.NO_HEADERS2, "different"));
    }

    @Override //
    final H createHttpEntity() {
        return this.createHttpEntity(HttpEntity.NO_HEADERS2);
    }

    final <T> H createHttpEntity(final HttpHeaderName<T> header, final T value) {
        return this.createHttpEntity(Maps.of(header, HttpEntityHeaderList.copy(header, list(value))));
    }

    abstract H createHttpEntity(final Map<HttpHeaderName<?>, HttpEntityHeaderList> headers);
}
