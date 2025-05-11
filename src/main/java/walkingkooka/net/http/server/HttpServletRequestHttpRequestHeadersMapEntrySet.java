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
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.set.ImmutableSetDefaults;
import walkingkooka.net.header.HttpHeaderName;

import javax.servlet.http.HttpServletRequest;
import java.util.AbstractSet;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

/**
 * The {@link Set} view of all entries in a headers from a request.
 */
final class HttpServletRequestHttpRequestHeadersMapEntrySet extends AbstractSet<Entry<HttpHeaderName<?>, List<?>>>
    implements ImmutableSetDefaults<HttpServletRequestHttpRequestHeadersMapEntrySet, Entry<HttpHeaderName<?>, List<?>>> {

    static HttpServletRequestHttpRequestHeadersMapEntrySet with(final HttpServletRequest request) {
        return new HttpServletRequestHttpRequestHeadersMapEntrySet(request);
    }

    private HttpServletRequestHttpRequestHeadersMapEntrySet(final HttpServletRequest request) {
        super();
        this.request = request;
    }

    @Override
    public Iterator<Entry<HttpHeaderName<?>, List<?>>> iterator() {
        return Iterators.mapping(Iterators.enumeration(this.request.getHeaderNames()), this::mapper);
    }

    /**
     * Creates an {@link Entry} with the typed {@link HttpHeaderName} and its values from the original String strings.
     */
    private Entry<HttpHeaderName<?>, List<?>> mapper(final String headerName) {
        final HttpHeaderName<?> header = HttpHeaderName.with(headerName);

        final List<?> values = Lists.array();
        for (final Enumeration<String> stringValues = this.request.getHeaders(headerName); stringValues.hasMoreElements(); ) {
            values.add(
                Cast.to(
                    header.parseValue(
                        stringValues.nextElement()
                    )
                )
            );
        }


        return Map.entry(header, Lists.readOnly(values));
    }

    @Override
    public int size() {
        if (-1 == this.size) {
            final Enumeration<String> headers = this.request.getHeaderNames();
            int i = 0;
            while (headers.hasMoreElements()) {
                headers.nextElement();
                i++;
            }
            this.size = i;
        }
        return this.size;
    }

    private int size = -1;

    private final HttpServletRequest request;

    // ImmutableSetDefaults.............................................................................................

    @Override
    public void elementCheck(final Entry<HttpHeaderName<?>, List<?>> entry) {
        Objects.requireNonNull(entry, "entry");
    }

    @Override
    public HttpServletRequestHttpRequestHeadersMapEntrySet setElements(final Set<Entry<HttpHeaderName<?>, List<?>>> elements) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<Entry<HttpHeaderName<?>, List<?>>> toSet() {
        throw new UnsupportedOperationException();
    }
}
