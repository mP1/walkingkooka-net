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

/**
 * The name of any parameter that accompanies a language-tag.
 */
final public class AcceptLanguageParameterName<V> extends HeaderParameterName<V> implements Comparable<AcceptLanguageParameterName<?>> {

    private final static HeaderParameterNameConstants<AcceptLanguageParameterName<?>> CONSTANTS = HeaderParameterNameConstants.empty(
        AcceptLanguageParameterName::new,
        HeaderHandler.quotedUnquotedString(
            AcceptLanguageOrAcceptLanguageValueHeaderParser.QUOTED_PARAMETER_VALUE,
            true,
            AcceptLanguageOrAcceptLanguageValueHeaderParser.UNQUOTED_PARAMETER_VALUE)
    );

    /**
     * The q (quality factor) parameter.
     */
    public final static AcceptLanguageParameterName<Float> Q = CONSTANTS.register("q", HeaderHandler.qualityFactor());

    /**
     * Factory that creates a {@link AcceptLanguageParameterName}
     */
    public static AcceptLanguageParameterName<?> with(final String name) {
        return CONSTANTS.lookup(name);
    }

    /**
     * Private ctor use factory.
     */
    private AcceptLanguageParameterName(final String value,
                                        final HeaderHandler<V> handler) {
        super(value, handler);
    }

    // Comparable......................................................................................................

    @Override
    public int compareTo(final AcceptLanguageParameterName<?> other) {
        return this.compareTo0(other);
    }

    // HeaderName2......................................................................................................

    @Override
    boolean canBeEqual(final Object other) {
        return other instanceof AcceptLanguageParameterName;
    }
}
