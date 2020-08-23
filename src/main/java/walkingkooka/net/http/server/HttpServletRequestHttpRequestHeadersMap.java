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

import javax.servlet.http.HttpServletRequest;
import java.util.AbstractMap;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;

/**
 * A read only parameter map view of the request from a {@link javax.servlet.http.HttpServletRequest}.
 */
final class HttpServletRequestHttpRequestHeadersMap extends AbstractMap<HttpHeaderName<?>, List<?>> {

    static {
        Maps.registerImmutableType(HttpServletRequestHttpRequestHeadersMap.class);
    }

    static HttpServletRequestHttpRequestHeadersMap with(final HttpServletRequest request) {
        return new HttpServletRequestHttpRequestHeadersMap(request);
    }

    private HttpServletRequestHttpRequestHeadersMap(final HttpServletRequest request) {
        super();
        this.request = request;
    }

    @Override
    public boolean containsKey(final Object key) {
        return key instanceof HttpHeaderName<?> &&
                this.containsHeader(Cast.to(key));
    }

    private boolean containsHeader(final HttpHeaderName<?> key) {
        return null != this.request.getHeader(key.value());
    }

    @Override
    public Set<Entry<HttpHeaderName<?>, List<?>>> entrySet() {
        if (null == this.entrySet) {
            this.entrySet = HttpServletRequestHttpRequestHeadersMapEntrySet.with(this.request);
        }
        return this.entrySet;
    }

    private HttpServletRequestHttpRequestHeadersMapEntrySet entrySet;

    @Override
    public List<?> get(final Object key) {
        return this.getOrDefault(key, NO_HEADER_VALUES);
    }

    private final static List<Object> NO_HEADER_VALUES = null;

    @Override
    public List<?> getOrDefault(final Object key,
                                final List<?> defaultValue) {
        return key instanceof HttpHeaderName<?> ?
                this.getHeaderOrDefaultValue(Cast.to(key), defaultValue) :
                defaultValue;
    }

    private List<?> getHeaderOrDefaultValue(final HttpHeaderName<?> header,
                                            final List<?> defaultValue) {
        final Enumeration<String> values = this.request.getHeaders(header.value());
        return null != values ?
                HttpServletRequestHttpRequest.toList(header, values) :
                defaultValue;
    }

    /**
     * The request from the original request.
     */
    private final HttpServletRequest request;

    @Override
    public String toString() {
        return this.request.toString();
    }
}
