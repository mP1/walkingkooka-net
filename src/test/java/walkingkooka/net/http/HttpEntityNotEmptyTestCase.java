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
import walkingkooka.net.header.HttpHeaderName;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

public abstract class HttpEntityNotEmptyTestCase<H extends HttpEntityNotEmpty> extends HttpEntityTestCase2<H> {

    final static String TEXT = "abcdefghijklmnopqrstuvwxyz";
    final static Binary BINARY = Binary.with(TEXT.getBytes(HttpEntity.DEFAULT_BODY_CHARSET));

    HttpEntityNotEmptyTestCase() {
        super();
    }

    // add..............................................................................................................

    @Test
    public final void testAddHeaderNew() {
        final H entity = this.createHttpEntity();
        assertEquals(HttpEntity.NO_HEADERS, entity.headers());

        final HttpHeaderName<Long> header = HttpHeaderName.CONTENT_LENGTH;
        final Long value = 1L;

        final HttpEntity added = entity.addHeader(header, value);
        assertNotSame(entity, added);

        this.check(added, Maps.of(header, value), entity.body(), entity.bodyText());
    }

    @Test
    public final void testAddHeaderExisting() {
        final H entity = this.createHttpEntity();
        assertEquals(HttpEntity.NO_HEADERS, entity.headers());

        final HttpHeaderName<Long> header = HttpHeaderName.CONTENT_LENGTH;
        final Long value = 1L;

        final HttpEntity added = entity.addHeader(header, value);
        assertNotSame(entity, added);

        this.check(added, Maps.of(header, value), entity.body(), entity.bodyText());

        final HttpEntity again = added.addHeader(header, value);
        assertSame(added, again);
        this.check(again);
    }

    @Test
    public final void testAddHeaderReplaceValue() {
        final H entity = this.createHttpEntity();
        assertEquals(HttpEntity.NO_HEADERS, entity.headers());

        final HttpHeaderName<Long> header = HttpHeaderName.CONTENT_LENGTH;
        final Long value = 1L;

        final HttpEntity added = entity.addHeader(header, value);
        assertNotSame(entity, added);

        this.check(added, Maps.of(header, value), entity.body(), entity.bodyText());

        final Long replacedValue = 999L;
        final HttpEntity replaced = entity.addHeader(header, replacedValue);
        assertNotSame(added, replaced);

        this.check(replaced, Maps.of(header, replacedValue), entity.body());
    }

    @Test
    public final void testAddHeaderDifferentTwice() {
        final H entity = this.createHttpEntity();
        assertEquals(HttpEntity.NO_HEADERS, entity.headers());

        final HttpHeaderName<Long> header1 = HttpHeaderName.CONTENT_LENGTH;
        final Long value1 = 1L;

        final HttpEntity added1 = entity.addHeader(header1, value1);
        assertNotSame(entity, added1);

        this.check(added1, Maps.of(header1, value1), entity.body(), entity.bodyText());

        final HttpHeaderName<String> header2 = HttpHeaderName.SERVER;
        final String value2 = "Server1";

        final HttpEntity added2 = added1.addHeader(header2, value2);
        assertNotSame(added1, added2);
        this.check(added2, Maps.of(header1, value1, header2, value2), entity.body(), entity.bodyText());
    }

    @Test
    public final void testAddHeaderSetBody() {
        final H entity = this.createHttpEntity();
        assertEquals(HttpEntity.NO_HEADERS, entity.headers());

        final HttpHeaderName<Long> header = HttpHeaderName.CONTENT_LENGTH;
        final Long value = 1L;

        final HttpEntity added = entity.addHeader(header, value);
        assertNotSame(entity, added);

        this.check(added, Maps.of(header, value), entity.body(), entity.bodyText());

        final Binary different = Binary.with("xyz".getBytes(HttpEntity.DEFAULT_BODY_CHARSET));
        final HttpEntity differentBody = added.setBody(different);
        assertNotSame(added, differentBody);

        this.check(differentBody, Maps.of(header, value), different);
    }

    @Test
    public final void testAddHeaderSetBodyText() {
        final H entity = this.createHttpEntity();
        assertEquals(HttpEntity.NO_HEADERS, entity.headers());

        final HttpHeaderName<Long> header = HttpHeaderName.CONTENT_LENGTH;
        final Long value = 1L;

        final HttpEntity added = entity.addHeader(header, value);
        assertNotSame(entity, added);

        this.check(added, Maps.of(header, value), entity.body(), entity.bodyText());

        final String different = "different-text";
        final HttpEntity differentBody = added.setBodyText(different);
        assertNotSame(added, differentBody);

        this.check(differentBody, Maps.of(header, value), different);
    }

    // remove..............................................................................................................

    @Test
    public final void testRemoveHeaderBecomesEmpty() {
        final H entity = this.createHttpEntity();
        assertEquals(HttpEntity.NO_HEADERS, entity.headers());

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
        assertEquals(HttpEntity.NO_HEADERS, entity.headers());

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
                Maps.of(HttpHeaderName.CONTENT_LENGTH, Long.valueOf(TEXT.length())),
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
                Maps.of(HttpHeaderName.CONTENT_LENGTH, Long.valueOf(TEXT.length())),
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
        assertEquals(entity.setBody(expected),
                entity.extractRange(range),
                () -> entity + " extractRange " + range + " failed");
    }

    // equals...........................................................................................................

    @Test
    public final void testDifferentHeaders() {
        this.checkNotEquals(this.createHttpEntity(Maps.of(HttpHeaderName.CONTENT_LENGTH, 777L)));
    }

    @Test
    public final void testDifferentHeaders2() {
        this.checkNotEquals(this.createHttpEntity(Maps.of(HttpHeaderName.CONTENT_LENGTH, 777L)),
                this.createHttpEntity(Maps.of(HttpHeaderName.CONTENT_LENGTH, 999L)));
    }

    @Test
    public final void testDifferentBody() {
        this.checkNotEquals(HttpEntityBinary.with(HttpEntity.NO_HEADERS, Binary.with(new byte[1])));
    }

    @Test
    public final void testDifferentText() {
        this.checkNotEquals(HttpEntityText.with(HttpEntity.NO_HEADERS, "different"));
    }

    @Override final H createHttpEntity() {
        return this.createHttpEntity(HttpEntity.NO_HEADERS);
    }

    abstract H createHttpEntity(final Map<HttpHeaderName<?>, Object> headers);
}
