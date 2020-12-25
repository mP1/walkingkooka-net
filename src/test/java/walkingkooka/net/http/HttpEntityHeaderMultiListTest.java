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
import walkingkooka.net.header.AcceptLanguage;
import walkingkooka.net.header.HeaderException;
import walkingkooka.net.header.HttpHeaderName;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class HttpEntityHeaderMultiListTest extends HttpEntityHeaderListTestCase2<HttpEntityHeaderMultiList> {

    private final static HttpHeaderName<AcceptLanguage> HEADER = HttpHeaderName.ACCEPT_LANGUAGE;
    private final static AcceptLanguage VALUE1 = AcceptLanguage.parse("EN");
    private final static AcceptLanguage VALUE2 = AcceptLanguage.parse("FR");

    @Test
    public void testWithInvalidValue() {
        final HttpHeaderName<?> header = HEADER;
        assertThrows(HeaderException.class, () -> HttpEntityHeaderMultiList.with(header, this));
    }

    @Test
    public void testWithInvalidValue2() {
        final HttpHeaderName<?> header = HEADER;
        assertThrows(HeaderException.class, () -> HttpEntityHeaderMultiList.with(header, VALUE1, this));
    }

    @Test
    public void testWithOne() {
        this.check(HttpEntityHeaderMultiList.with(HEADER, VALUE1), VALUE1);
    }

    @Test
    public void testWithTwo() {
        this.check(HttpEntityHeaderMultiList.with(HEADER, VALUE1, VALUE2), VALUE1, VALUE2);
    }

    @Test
    public void testGetInvalidIndexFails() {
        assertThrows(IndexOutOfBoundsException.class, () -> {
            Lists.of(VALUE1).get(1);
        });
        assertThrows(IndexOutOfBoundsException.class, () -> {
            this.createList().get(1);
        });
    }

    @Test
    public void testAppend() {
        final HttpEntityHeaderMultiList list = this.createList();
        final HttpEntityHeaderMultiList appended = list.append(HEADER, VALUE2);
        assertNotSame(list, appended);

        this.check(appended, VALUE1, VALUE2);
    }

    @Test
    public void testAppendNonMultiValueFails() {
        final HttpHeaderName<Long> header = HttpHeaderName.CONTENT_LENGTH;
        assertEquals(false, header.isMultiple(), "isMultiple should be false");
        final HttpEntityHeaderMultiList list = HttpEntityHeaderMultiList.with(header, 1L);

        assertThrows(IllegalArgumentException.class, () -> list.append(header, 999L));
    }

    @Test
    public void testAppendExistingValue() {
        final HttpEntityHeaderMultiList list = this.createList();
        final HttpEntityHeaderMultiList appended = list.append(HEADER, VALUE1);
        assertSame(list, appended);
    }

    @Test
    public void testAppendExistingValue2() {
        final HttpEntityHeaderMultiList list = HttpEntityHeaderMultiList.with(HEADER, VALUE1, VALUE2);
        final HttpEntityHeaderMultiList appended = list.append(HEADER, VALUE2);
        assertSame(list, appended);
    }

    @Test
    public void testRemoveValueDifferentValue() {
        final HttpEntityHeaderMultiList list = this.createList();
        assertSame(list, list.removeValue("different"));
    }

    @Test
    public void testRemoveValueOnlyValue() {
        final HttpEntityHeaderMultiList list = this.createHttpEntityHeaderList(VALUE1);
        assertSame(null, list.removeValue(VALUE1));
    }

    @Test
    public void testRemoveValue() {
        final HttpEntityHeaderMultiList list = HttpEntityHeaderMultiList.with(HEADER, VALUE1, VALUE2);
        final HttpEntityHeaderMultiList removed = list.removeValue(VALUE1);
        assertNotSame(list, removed);

        this.check(removed, VALUE2);
    }

    @Test
    public void testRemoveValue2() {
        final HttpEntityHeaderMultiList list = HttpEntityHeaderMultiList.with(HEADER, VALUE1, VALUE2);
        final HttpEntityHeaderMultiList removed = list.removeValue(VALUE2);
        assertNotSame(list, removed);

        this.check(removed, VALUE1);
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createList(), Lists.of(VALUE1).toString());
    }

    @Override
    HttpEntityHeaderMultiList createHttpEntityHeaderList(final Object... values) {
        return HttpEntityHeaderMultiList.with(HEADER, values);
    }

    private void check(final HttpEntityHeaderMultiList list,
                       final Object... values) {
        assertArrayEquals(values, list.values, "values");
    }

    @Override
    public HttpEntityHeaderMultiList createList() {
        return HttpEntityHeaderMultiList.with(HEADER, VALUE1);
    }

    @Override
    public Class<HttpEntityHeaderMultiList> type() {
        return Cast.to(HttpEntityHeaderMultiList.class);
    }
}
