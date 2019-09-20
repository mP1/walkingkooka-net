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

import java.util.Map;

/**
 * An encoding wildcard that may appear within accept-encoding header
 */
final class AcceptEncodingValueWildcard extends AcceptEncodingValue {

    /**
     * Singleton without parameters/
     */
    final static AcceptEncodingValueWildcard INSTANCE = new AcceptEncodingValueWildcard(NO_PARAMETERS);

    static AcceptEncodingValueWildcard with(final Map<AcceptEncodingValueParameterName<?>, Object> parameters) {
        return new AcceptEncodingValueWildcard(parameters);
    }

    private AcceptEncodingValueWildcard(final Map<AcceptEncodingValueParameterName<?>, Object> parameters) {
        super("*", parameters);
    }

    @Override
    AcceptEncodingValue replace(final Map<AcceptEncodingValueParameterName<?>, Object> parameters) {
        return new AcceptEncodingValueWildcard(parameters);
    }

    @Override
    public boolean isWildcard() {
        return true;
    }

    // Predicate........................................................................................................

    /**
     * Matches any {@link ContentEncoding}.
     */
    @Override
    boolean test0(final ContentEncoding encoding) {
        return true;
    }

    // Object..........................................................................................................

    @Override
    boolean canBeEquals(final Object other) {
        return other instanceof AcceptEncodingValueWildcard;
    }
}
