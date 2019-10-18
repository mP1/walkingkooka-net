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
                Lists.empty());
    }

    @Test
    public void testHasNextAndNextConsumeStringHeader() {
        this.iterateAndCheck(true,
                Maps.of(CONNECTION, CONNECTION_VALUE),
                CONNECTION,
                CONNECTION_VALUE);
    }

    @Test
    public void testHasNextAndNextConsumeNonStringHeader() {
        this.iterateAndCheck(true,
                Maps.of(CONTENT_LENGTH, CONTENT_LENGTH_VALUE),
                CONTENT_LENGTH,
                CONTENT_LENGTH_VALUE);
    }

    @Test
    public void testHasNextAndNextConsume() {
        final Map<HttpHeaderName<?>, Object> headers = this.headers2();

        this.iterateAndCheck(true,
                headers,
                HttpHeaderName.CONNECTION, HttpHeaderName.CONTENT_LENGTH,
                CONNECTION_VALUE, CONTENT_LENGTH_VALUE);
    }

    @Test
    public void testNextConsume() {
        final Map<HttpHeaderName<?>, Object> headers = this.headers2();

        this.iterateAndCheck(false,
                headers,
                HttpHeaderName.CONNECTION, HttpHeaderName.CONTENT_LENGTH,
                CONNECTION_VALUE, CONTENT_LENGTH_VALUE);
    }

    private void iterateAndCheck(final boolean checkHasNext,
                                 final Map<HttpHeaderName<?>, Object> headers,
                                 final HttpHeaderName<?> headerName,
                                 final Object headerValue) {
        this.iterateAndCheck(checkHasNext, headers, Lists.of(headerName), headerValue);
    }

    private void iterateAndCheck(final boolean checkHasNext,
                                 final Map<HttpHeaderName<?>, Object> headers,
                                 final HttpHeaderName<?> headerName1,
                                 final HttpHeaderName<?> headerName2,
                                 final Object headerValue1,
                                 final Object headerValue2) {
        this.iterateAndCheck(checkHasNext,
                headers,
                Lists.of(headerName1, headerName2),
                headerValue1, headerValue2);
    }

    private void iterateAndCheck(final boolean checkHasNext,
                                 final Map<HttpHeaderName<?>, Object> headers,
                                 final List<HttpHeaderName<?>> headerNames,
                                 final Object... headerValues) {
        assertEquals(headerNames.size(), headerValues.length, "headerNames count != headerValues count");

        final HttpRequestRouterParametersMapHttpHeaderEntryIterator iterator = this.createIterator(headers);

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
        final Iterator<Entry<HttpHeaderName<?>, Object>> iterator = Iterators.fake();
        this.toStringAndCheck(HttpRequestRouterParametersMapHttpHeaderEntryIterator.with(iterator), iterator.toString());
    }

    private HttpRequestRouterParametersMapHttpHeaderEntryIterator createIterator(final Map<HttpHeaderName<?>, Object> headers) {
        return HttpRequestRouterParametersMapHttpHeaderEntryIterator.with(headers.entrySet().iterator());
    }

    private Map<HttpHeaderName<?>, Object> headers2() {
        return Maps.of(CONNECTION, CONNECTION_VALUE, CONTENT_LENGTH, CONTENT_LENGTH_VALUE);
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
