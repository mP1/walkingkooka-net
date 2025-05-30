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
import walkingkooka.collect.iterator.Iterators;
import walkingkooka.collect.map.Maps;
import walkingkooka.collect.set.ImmutableSetDefaults;
import walkingkooka.net.header.ClientCookie;
import walkingkooka.net.header.HttpHeaderName;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

/**
 * The {@link Map#entrySet()} for {@link HttpRequestRouterParametersMap}.
 */
final class HttpRequestRouterParametersMapEntrySet extends AbstractSet<Entry<HttpRequestAttribute<?>, Object>>
    implements ImmutableSetDefaults<HttpRequestRouterParametersMapEntrySet, Entry<HttpRequestAttribute<?>, Object>> {

    /**
     * Factory only called by {@link HttpRequestRouterParametersMap}.
     */
    static HttpRequestRouterParametersMapEntrySet with(final HttpRequestRouterParametersMap map) {
        return new HttpRequestRouterParametersMapEntrySet(map);
    }

    /**
     * Private ctor
     */
    private HttpRequestRouterParametersMapEntrySet(final HttpRequestRouterParametersMap map) {
        super();
        this.map = map;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Iterator<Entry<HttpRequestAttribute<?>, Object>> iterator() {
        // attributes, path, url-parameters, headers, cookies, request parameters
        final HttpRequestRouterParametersMap map = this.map;
        final HttpRequest request = map.request;

        final Iterator<Entry<HttpRequestAttribute<?>, Object>> attributes = HttpRequestRouterParametersMapHttpRequestAttributeEntryIterator.with(request);

        final Iterator<Entry<HttpRequestAttribute<?>, Object>> pathNames = HttpRequestRouterParametersMapPathComponentEntryIterator.with(
            map.pathNames()
        );

        final Iterator<Entry<HttpRequestAttribute<?>, Object>> urlParameterNames = Cast.to(
            map.urlParameters()
                .entrySet()
                .iterator()
        );

        final Iterator<Entry<HttpRequestAttribute<?>, Object>> headers = Cast.to(
            request.headers()
                .entrySet()
                .iterator()
        );

        final Iterator<Entry<HttpRequestAttribute<?>, Object>> cookies = Iterators.mapping(
            HttpHeaderName.COOKIE.header(request)
                .orElse(ClientCookie.NO_COOKIES)
                .iterator(),
            HttpRequestRouterParametersMapEntrySet::cookieEntry
        );

        final Iterator<Entry<HttpRequestAttribute<?>, Object>> parameters = Cast.to(
            request.parameters()
                .entrySet()
                .iterator()
        );

        return Iterators.chain(
            attributes,
            pathNames,
            urlParameterNames,
            headers,
            cookies,
            parameters
        );
    }

    private static Entry<HttpRequestAttribute<?>, Object> cookieEntry(final ClientCookie cookie) {
        return Maps.entry(
            cookie.name(),
            cookie
        );
    }

    @Override
    public int size() {
        return this.map.size();
    }

    private final HttpRequestRouterParametersMap map;

    @Override
    public String toString() {
        return this.map.toString();
    }

    // ImmutableSetDefaults.............................................................................................

    @Override
    public void elementCheck(final Entry<HttpRequestAttribute<?>, Object> entry) {
        Objects.requireNonNull(entry, "entry");
    }

    @Override
    public HttpRequestRouterParametersMapEntrySet setElements(final Set<Entry<HttpRequestAttribute<?>, Object>> elements) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<Entry<HttpRequestAttribute<?>, Object>> toSet() {
        throw new UnsupportedOperationException();
    }
}
