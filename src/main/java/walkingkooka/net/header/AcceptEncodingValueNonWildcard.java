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
 * An accept-encoding encoding with optional parameters.
 */
final class AcceptEncodingValueNonWildcard extends AcceptEncodingValue {

    static AcceptEncodingValueNonWildcard with(final String value, final Map<AcceptEncodingValueParameterName<?>, Object> parameters) {
        return parameters.isEmpty() ?
            maybeConstant(value) :
            new AcceptEncodingValueNonWildcard(value, parameters);
    }

    private static AcceptEncodingValueNonWildcard maybeConstant(final String value) {
        final AcceptEncodingValueNonWildcard acceptEncodings = CONSTANTS.get(value);
        return null != acceptEncodings ?
            acceptEncodings :
            new AcceptEncodingValueNonWildcard(value, NO_PARAMETERS);
    }

    private AcceptEncodingValueNonWildcard(final String value, final Map<AcceptEncodingValueParameterName<?>, Object> parameters) {
        super(value, parameters);
    }

    @Override
    public boolean isWildcard() {
        return false;
    }

    @Override
    AcceptEncodingValueNonWildcard replaceParameters(final Map<AcceptEncodingValueParameterName<?>, Object> parameters) {
        return new AcceptEncodingValueNonWildcard(this.value, parameters);
    }

    // Predicate........................................................................................................

    @Override
    boolean testNonNull(final ContentEncoding encoding) {
        return encoding.value.stream()
            .filter(this::filter)
            .count() == 1;
    }

    private boolean filter(final Encoding encoding) {
        return CASE_SENSITIVITY.equals(this.value(), encoding.value());
    }
}
