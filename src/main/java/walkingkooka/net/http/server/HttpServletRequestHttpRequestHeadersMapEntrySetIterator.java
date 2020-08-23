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

import walkingkooka.collect.list.Lists;
import walkingkooka.collect.map.Maps;
import walkingkooka.net.header.HttpHeaderName;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Iterator of all header map entries.
 */
final class HttpServletRequestHttpRequestHeadersMapEntrySetIterator implements Iterator<Entry<HttpHeaderName<?>, List<?>>> {

    static HttpServletRequestHttpRequestHeadersMapEntrySetIterator with(final HttpServletRequest request) {
        return new HttpServletRequestHttpRequestHeadersMapEntrySetIterator(request);
    }

    private HttpServletRequestHttpRequestHeadersMapEntrySetIterator(final HttpServletRequest request) {
        super();
        this.request = request;
        this.headers = request.getHeaderNames();
    }

    @Override
    public boolean hasNext() {
        return this.headers.hasMoreElements();
    }

    @Override
    public Entry<HttpHeaderName<?>, List<?>> next() {
        return HttpServletRequestHttpRequestHeadersMapEntrySetIteratorEntry.with(this.headers.nextElement(),
                this.request);
    }

    private final Enumeration<String> headers;

    private final HttpServletRequest request;

    @Override
    public String toString() {
        final Map<String, List<String>> map = Maps.ordered();
        final HttpServletRequest request = this.request;

        for(final Enumeration<String> headerNames = request.getHeaderNames(); headerNames.hasMoreElements();) {
            final String name = headerNames.nextElement();
            final Enumeration<String> values = request.getHeaders(name);
            final List<String> list = Lists.array();
            while(values.hasMoreElements()) {
                list.add(values.nextElement());
            }

            map.put(name, list);
        }

        return map.entrySet().toString();
    }
}
