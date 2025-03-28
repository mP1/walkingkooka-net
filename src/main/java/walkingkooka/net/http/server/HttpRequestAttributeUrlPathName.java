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
import walkingkooka.net.UrlPathName;

import java.util.Optional;
import java.util.stream.IntStream;

/**
 * A key to a url path component ({@link walkingkooka.net.UrlPathName}.
 */
final class HttpRequestAttributeUrlPathName implements HttpRequestAttribute<UrlPathName> {

    /**
     * Factory that returns a {@link HttpRequestAttributeUrlPathName}.
     */
    static HttpRequestAttributeUrlPathName with(final int index) {
        if (index < 0) {
            throw new IndexOutOfBoundsException("Index " + index + " < 0");
        }
        return index < CONSTANT_COUNT ?
            CACHE[index] :
            new HttpRequestAttributeUrlPathName(index);
    }

    /**
     * The size of the cache.
     */
    final static int CONSTANT_COUNT = 32;

    /**
     * Fills the cache with instances.
     */
    private final static HttpRequestAttributeUrlPathName[] CACHE = IntStream.range(0, CONSTANT_COUNT)
        .mapToObj(HttpRequestAttributeUrlPathName::new)
        .toArray(HttpRequestAttributeUrlPathName[]::new);

    /**
     * Private ctor use factory.
     */
    private HttpRequestAttributeUrlPathName(final int index) {
        super();
        this.index = index;
    }

    // HttpRequestAttribute..............................................................................................

    /**
     * A typed getter that retrieves a value from a {@link HttpRequest}
     */
    @Override
    public Optional<UrlPathName> parameterValue(final HttpRequest request) {
        UrlPathName pathName = null;

        int i = 0;
        for (UrlPathName possible : request.url().path()) {
            if (i == this.index) {
                pathName = possible;
                break;
            }
            i++;
        }

        return Optional.ofNullable(pathName);
    }

    // Object....................................................................................................

    @Override
    public int hashCode() {
        return this.index;
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
            other instanceof HttpRequestAttributeUrlPathName &&
                this.equals0(Cast.to(other));
    }

    private boolean equals0(final HttpRequestAttributeUrlPathName other) {
        return this.index == other.index;
    }

    @Override
    public String toString() {
        return "path-" + this.index;
    }

    // shared with HttpRequestRouterParametersMap
    final int index;
}
