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

import walkingkooka.collect.map.Maps;
import walkingkooka.net.UrlPathName;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.NoSuchElementException;

/**
 * An {@link Iterator} entry view of all {@link UrlPathName pathNames}.
 */
final class HttpRequestRouterParametersMapPathComponentEntryIterator implements Iterator<Entry<HttpRequestAttribute<?>, Object>> {

    static HttpRequestRouterParametersMapPathComponentEntryIterator with(final UrlPathName[] pathNames) {
        return new HttpRequestRouterParametersMapPathComponentEntryIterator(pathNames);
    }

    private HttpRequestRouterParametersMapPathComponentEntryIterator(final UrlPathName[] pathNames) {
        super();
        this.pathNames = pathNames;
    }

    @Override
    public boolean hasNext() {
        return this.position <= this.pathNames.length;
    }

    @Override
    public Entry<HttpRequestAttribute<?>, Object> next() {
        final int position = this.position;
        if (position > this.pathNames.length) {
            throw new NoSuchElementException();
        }
        this.position = 1 + position;

        return entry(position);
    }

    private Entry<HttpRequestAttribute<?>, Object> entry(final int position) {
        final UrlPathName[] pathNames = this.pathNames;

        return 0 == position ?
            Maps.entry(HttpRequestAttributes.PATH_COMPONENT_COUNT, pathNames.length) :
            Maps.entry(
                HttpRequestAttributes.pathComponent(position - 1),
                pathNames[position - 1]);
    }

    private final UrlPathName[] pathNames;

    /**
     * 0 returns the {@link HttpRequestAttributes#PATH_COMPONENT_COUNT} and remaining positions indicate the {@link HttpRequestAttributes#pathComponent(int)}.
     */
    private int position = 0;

    @Override
    public String toString() {
        final int position = this.position;
        final UrlPathName[] pathNames = this.pathNames;

        return position <= pathNames.length ?
            this.entry(position).toString() :
            "";
    }
}
