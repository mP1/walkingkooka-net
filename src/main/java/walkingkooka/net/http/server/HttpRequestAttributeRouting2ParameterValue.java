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

import java.util.List;
import java.util.function.Predicate;

/**
 * A {@link Predicate} that tries all parameter values against the wrapped {@link Predicate}.
 */
final class HttpRequestAttributeRouting2ParameterValue extends HttpRequestAttributeRouting2<Predicate<String>>
        implements Predicate<List<String>> {

    /**
     * Creates a new {@link HttpRequestAttributeRouting2ParameterValue}.
     */
    static HttpRequestAttributeRouting2ParameterValue with(final Predicate<String> predicate) {
        return new HttpRequestAttributeRouting2ParameterValue(predicate);
    }

    /**
     * Private ctor
     */
    private HttpRequestAttributeRouting2ParameterValue(final Predicate<String> predicate) {
        super(predicate);
    }

    // Predicate .......................................................................................................

    @Override
    public boolean test(final List<String> values) {
        return null != values &&
                values.stream()
                        .anyMatch(this.predicate);
    }

    boolean canBeEquals(final Object other) {
        return other instanceof HttpRequestAttributeRouting2ParameterValue;
    }
}

