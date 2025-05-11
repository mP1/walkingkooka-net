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

import walkingkooka.collect.iterator.Iterators;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.set.ImmutableSetDefaults;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

/**
 * The {@link Set} view of all entries in a parameters {@link Map}.
 */
final class HttpServletRequestHttpRequestParametersMapEntrySet extends AbstractSet<Entry<HttpRequestParameterName, List<String>>>
    implements ImmutableSetDefaults<HttpServletRequestHttpRequestParametersMapEntrySet, Entry<HttpRequestParameterName, List<String>>> {

    static HttpServletRequestHttpRequestParametersMapEntrySet with(final Set<Entry<String, String[]>> parameters) {
        return new HttpServletRequestHttpRequestParametersMapEntrySet(parameters);
    }

    private HttpServletRequestHttpRequestParametersMapEntrySet(final Set<Entry<String, String[]>> parameters) {
        super();
        this.parameters = parameters;
    }

    @Override
    public Iterator<Entry<HttpRequestParameterName, List<String>>> iterator() {
        return Iterators.mapping(this.parameters.iterator(), HttpServletRequestHttpRequestParametersMapEntrySet::mapper);
    }

    private static Entry<HttpRequestParameterName, List<String>> mapper(final Entry<String, String[]> entry) {
        return Map.entry(HttpRequestParameterName.with(entry.getKey()), Lists.of(entry.getValue()));
    }

    @Override
    public boolean remove(final Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int size() {
        return this.parameters.size();
    }

    private final Set<Entry<String, String[]>> parameters;

    // ImmutableSetDefaults.............................................................................................

    @Override
    public void elementCheck(final Entry<HttpRequestParameterName, List<String>> entry) {
        Objects.requireNonNull(entry, "entry");
    }

    @Override
    public HttpServletRequestHttpRequestParametersMapEntrySet setElements(final Set<Entry<HttpRequestParameterName, List<String>>> elements) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<Entry<HttpRequestParameterName, List<String>>> toSet() {
        throw new UnsupportedOperationException();
    }
}
