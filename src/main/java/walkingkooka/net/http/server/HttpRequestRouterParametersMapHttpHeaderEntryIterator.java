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

import walkingkooka.Cast;
import walkingkooka.collect.map.Maps;
import walkingkooka.net.header.HttpHeaderName;

import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

/**
 * An {@link Iterator} entry view of all {@link walkingkooka.net.header.HttpHeaderName} and their values.
 */
final class HttpRequestRouterParametersMapHttpHeaderEntryIterator implements Iterator<Entry<HttpRequestAttribute<?>, Object>> {

    static HttpRequestRouterParametersMapHttpHeaderEntryIterator with(final Iterator<Entry<HttpHeaderName<?>, List<?>>> headerAndValues) {
        return new HttpRequestRouterParametersMapHttpHeaderEntryIterator(headerAndValues);
    }

    private HttpRequestRouterParametersMapHttpHeaderEntryIterator(final Iterator<Entry<HttpHeaderName<?>, List<?>>> headerAndValues) {
        super();
        this.headerAndValues = headerAndValues;
    }

    @Override
    public boolean hasNext() {
        return this.headerAndValues.hasNext();
    }

    @Override
    public Entry<HttpRequestAttribute<?>, Object> next() {
        final Entry<HttpHeaderName<?>, List<?>> entry = this.headerAndValues.next();
        final HttpHeaderName<?> header = Cast.to(entry.getKey());
        return Maps.entry(header, entry.getValue());
    }

    private final Iterator<Entry<HttpHeaderName<?>, List<?>>> headerAndValues;

    @Override
    public String toString() {
        return this.headerAndValues.toString();
    }
}
