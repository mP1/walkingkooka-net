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

package walkingkooka.net.header;

import walkingkooka.naming.Name;
import walkingkooka.net.http.HttpMethod;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A {@link HeaderHandler} that expects comma separated {@link HttpMethod methods}.
 */
final class HttpMethodListHeaderHandler extends NonStringHeaderHandler<List<HttpMethod>> {

    /**
     * Singleton
     */
    final static HttpMethodListHeaderHandler INSTANCE = new HttpMethodListHeaderHandler();

    /**
     * Private ctor use singleton.
     */
    private HttpMethodListHeaderHandler() {
        super();
    }

    @Override
    List<HttpMethod> parse0(final String text) {
        return Arrays.stream(text.split(","))
            .map(m -> HttpMethod.with(m.trim()))
            .collect(Collectors.toList());
    }

    @Override
    void checkNonNull(final Object value) {
        this.checkListOfType(
            value,
            v -> v instanceof HttpMethod,
            HttpMethod.class
        );
    }

    @Override
    String toText0(final List<HttpMethod> value, final Name name) {
        return value.stream()
            .map(HttpMethod::value)
            .collect(Collectors.joining(", "));
    }

    @Override
    public String toString() {
        return toStringListOf(HttpMethod.class);
    }
}
