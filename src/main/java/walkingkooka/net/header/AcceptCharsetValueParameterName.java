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


/**
 * The {@link Name} of an optional parameter accompanying a {@link MediaType}. Note the name may not contain the whitespace, equals sign, semi colons
 * or commas.
 */
final public class AcceptCharsetValueParameterName<V> extends HeaderParameterName<V>
    implements Comparable<AcceptCharsetValueParameterName<?>> {

    // constants

    private final static HeaderParameterNameConstants<AcceptCharsetValueParameterName<?>> CONSTANTS = HeaderParameterNameConstants.empty(
        AcceptCharsetValueParameterName::new,
        HeaderHandler.quotedUnquotedString(
            AcceptCharsetHeaderParser.QUOTED_PARAMETER_VALUE,
            false,
            AcceptCharsetHeaderParser.UNQUOTED_PARAMETER_VALUE));

    /**
     * The q (quality factor) parameter.
     */
    public final static AcceptCharsetValueParameterName<Float> Q = CONSTANTS.register("q", HeaderHandler.qualityFactor());

    /**
     * Factory that creates a {@link AcceptCharsetValueParameterName}
     */
    public static AcceptCharsetValueParameterName<?> with(final String name) {
        return CONSTANTS.lookup(name);
    }

    /**
     * Private ctor use factory.
     */
    private AcceptCharsetValueParameterName(final String value,
                                            final HeaderHandler<V> handler) {
        super(value, handler);
    }

    // Comparable..................................................................................................

    @Override
    public int compareTo(final AcceptCharsetValueParameterName<?> other) {
        return this.compareTo0(other);
    }

    // Header2.................................................................................................

    @Override
    boolean canBeEqual(final Object other) {
        return other instanceof AcceptCharsetValueParameterName;
    }
}
