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
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class HttpEntityHeaderListMultiTest extends HttpEntityHeaderListTestCase2<HttpEntityHeaderListMulti> {

    private final static HttpHeaderName<AcceptLanguage> HEADER = HttpHeaderName.ACCEPT_LANGUAGE;
    private final static AcceptLanguage VALUE1 = AcceptLanguage.parse("EN");
    private final static AcceptLanguage VALUE2 = AcceptLanguage.parse("FR");

    @Test
    public void testWithInvalidValue() {
        assertThrows(HeaderException.class, () -> HttpEntityHeaderListMulti.with(HEADER, this));
    }

    @Test
    public void testWithInvalidValue2() {
        assertThrows(HeaderException.class, () -> HttpEntityHeaderListMulti.with(HEADER, VALUE1, this));
    }

    @Test
    public void testWithOne() {
        this.check(HttpEntityHeaderListMulti.with(HEADER, VALUE1), VALUE1);
    }

    @Test
    public void testWithTwo() {
        this.check(HttpEntityHeaderListMulti.with(HEADER, VALUE1, VALUE2), VALUE1, VALUE2);
    }

    @Test
    public void testGetInvalidIndexFails() {
        this.getFails(
            this.createList(),
            1
        );
    }

    @Test
    public void testConcat() {
        final HttpEntityHeaderListMulti list = this.createList();
        final HttpEntityHeaderListMulti appended = (HttpEntityHeaderListMulti) list.concat(VALUE2);
        assertNotSame(list, appended);

        this.check(appended, VALUE1, VALUE2);
    }

    @Test
    public void testAppendNonMultiValueFails() {
        final HttpHeaderName<Long> header = HttpHeaderName.CONTENT_LENGTH;
        this.checkEquals(false, header.isMultiple(), "isMultiple should be false");
        final HttpEntityHeaderListMulti list = HttpEntityHeaderListMulti.with(header, 1L);

        assertThrows(IllegalArgumentException.class, () -> list.concat(999L));
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createList(), Lists.of(VALUE1).toString());
    }

    @Override
    HttpEntityHeaderListMulti createHttpEntityHeaderList(final Object... values) {
        return HttpEntityHeaderListMulti.with(HEADER, values);
    }

    private void check(final HttpEntityHeaderListMulti list,
                       final Object... values) {
        assertArrayEquals(values, list.values, "values");
    }

    @Override
    public HttpEntityHeaderListMulti createList() {
        return HttpEntityHeaderListMulti.with(HEADER, VALUE1);
    }

    @Override
    public Class<HttpEntityHeaderListMulti> type() {
        return Cast.to(HttpEntityHeaderListMulti.class);
    }
}
