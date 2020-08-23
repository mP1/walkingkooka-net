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

package walkingkooka.net.http.server;

import org.junit.jupiter.api.Test;
import walkingkooka.ToStringTesting;
import walkingkooka.collect.iterator.IteratorTesting;
import walkingkooka.collect.iterator.Iterators;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.map.Maps;
import walkingkooka.net.header.HttpHeaderName;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.reflect.TypeNameTesting;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class HttpRequestRouterParametersMapHttpHeaderEntryIteratorTest implements IteratorTesting,
        ClassTesting2<HttpRequestRouterParametersMapHttpHeaderEntryIterator>,
        ToStringTesting<HttpRequestRouterParametersMapHttpHeaderEntryIterator>,
        TypeNameTesting<HttpRequestRouterParametersMapHttpHeaderEntryIterator> {

    private final static HttpHeaderName<Long> CONTENT_LENGTH = HttpHeaderName.CONTENT_LENGTH;
    private final static Long CONTENT_LENGTH_VALUE = 123L;
    private final static HttpHeaderName<String> CONNECTION = HttpHeaderName.CONNECTION;
    private final static String CONNECTION_VALUE = "Close";

    @Test
    public void testHasNextAndNextConsumeEmpty() {
        this.iterateAndCheck(true,
                HttpRequest.NO_HEADERS,
                list());
    }

    @Test
    public void testHasNextAndNextConsumeStringHeader() {
        this.iterateAndCheck(true,
                map(CONNECTION, CONNECTION_VALUE),
                list(CONNECTION),
                list(CONNECTION_VALUE));
    }

    @Test
    public void testHasNextAndNextConsumeNonStringHeader() {
        this.iterateAndCheck(true,
                map(CONTENT_LENGTH, CONTENT_LENGTH_VALUE),
                list(CONTENT_LENGTH),
                list(CONTENT_LENGTH_VALUE));
    }

    @Test
    public void testHasNextAndNextConsume() {
        final Map<HttpHeaderName<?>, List<?>> headers = this.headers2();

        this.iterateAndCheck(true,
                headers,
                HttpHeaderName.CONNECTION, HttpHeaderName.CONTENT_LENGTH,
                list(CONNECTION_VALUE), list(CONTENT_LENGTH_VALUE));
    }

    @Test
    public void testNextConsume() {
        final Map<HttpHeaderName<?>, List<?>> headers = this.headers2();

        this.iterateAndCheck(false,
                headers,
                HttpHeaderName.CONNECTION, HttpHeaderName.CONTENT_LENGTH,
                list(CONNECTION_VALUE), list(CONTENT_LENGTH_VALUE));
    }

    private void iterateAndCheck(final boolean checkHasNext,
                                 final Map<HttpHeaderName<?>, List<?>> headers,
                                 final HttpHeaderName<?> headerName1,
                                 final HttpHeaderName<?> headerName2,
                                 final Object headerValue1,
                                 final Object headerValue2) {
        this.iterateAndCheck(checkHasNext,
                headers,
                list(headerName1, headerName2),
                headerValue1, headerValue2);
    }

    private void iterateAndCheck(final boolean checkHasNext,
                                 final Map<HttpHeaderName<?>, List<?>> headers,
                                 final List<HttpHeaderName<?>> headerNames,
                                 final Object... headerValues) {
        assertEquals(headerNames.size(), headerValues.length, "headerNames count != headerValues count");

        final HttpRequestRouterParametersMapHttpHeaderEntryIterator iterator = this.createIterator(headers.entrySet().iterator());

        for (int i = 0; i < headerNames.size(); i++) {
            if (checkHasNext) {
                this.hasNextCheckTrue(iterator, "iterator should have " + (headerValues.length - i) + " entries left");
            }
            this.checkNext(iterator, headerNames.get(i), headerValues[i]);
        }

        this.hasNextCheckFalse(iterator);
        this.nextFails(iterator);
    }

    private void checkNext(final HttpRequestRouterParametersMapHttpHeaderEntryIterator iterator,
                           final HttpHeaderName<?> header,
                           final Object value) {
        final Entry<HttpRequestAttribute<?>, Object> entry = iterator.next();
        assertEquals(header, entry.getKey(), "key");
        assertEquals(value, entry.getValue(), "value");
    }

    @Test
    public void testToString() {
        final Iterator<Entry<HttpHeaderName<?>, List<?>>> iterator = Iterators.fake();
        this.toStringAndCheck(HttpRequestRouterParametersMapHttpHeaderEntryIterator.with(iterator), iterator.toString());
    }

    private HttpRequestRouterParametersMapHttpHeaderEntryIterator createIterator(final Iterator<Entry<HttpHeaderName<?>, List<?>>> headers) {
        return HttpRequestRouterParametersMapHttpHeaderEntryIterator.with(headers);
    }

    private Map<HttpHeaderName<?>, List<?>> headers2() {
        return Maps.of(CONNECTION, list(CONNECTION_VALUE), CONTENT_LENGTH, list(CONTENT_LENGTH_VALUE));
    }

    private <T> Map<HttpHeaderName<?>, List<?>> map(final HttpHeaderName<T> header,
                                                    final T value) {
        return Maps.of(header, list(value));
    }

    private <T> List<T> list(final T... values) {
        return Lists.of(values);
    }

    @Override
    public Class<HttpRequestRouterParametersMapHttpHeaderEntryIterator> type() {
        return HttpRequestRouterParametersMapHttpHeaderEntryIterator.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }

    @Override
    public String typeNamePrefix() {
        return HttpRequestRouterParametersMap.class.getSimpleName();
    }

    @Override
    public String typeNameSuffix() {
        return Iterator.class.getSimpleName();
    }
}
