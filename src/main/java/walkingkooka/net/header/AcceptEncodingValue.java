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

import walkingkooka.collect.map.Maps;
import walkingkooka.net.HasQualityFactor;
import walkingkooka.predicate.character.CharPredicates;
import walkingkooka.text.CaseSensitivity;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * An individual encoding belonging to a {@link AcceptEncoding}.
 */
public abstract class AcceptEncodingValue extends HeaderWithParameters2<AcceptEncodingValue,
        AcceptEncodingValueParameterName<?>,
        String>
        implements Comparable<AcceptEncodingValue>,
        HasQualityFactor,
        Predicate<ContentEncoding> {

    /**
     * A map holding no parameters.
     */
    public final static Map<AcceptEncodingValueParameterName<?>, Object> NO_PARAMETERS = Maps.empty();

    /**
     * {@see CaseSensitivity}
     */
    final static CaseSensitivity CASE_SENSITIVITY = CaseSensitivity.INSENSITIVE;

    /**
     * Holds all constants.
     */
    final static Map<String, AcceptEncodingValueNonWildcard> CONSTANTS = Maps.sorted(CASE_SENSITIVITY.comparator());

    /**
     * Holds a {@link AcceptEncodingValue} for br.
     */
    public final static AcceptEncodingValue BR = registerConstant("br");

    /**
     * Holds a {@link AcceptEncodingValue} for deflate
     */
    public final static AcceptEncodingValue COMPRESS = registerConstant("compress");

    /**
     * Holds a {@link AcceptEncodingValue} for deflate.
     */
    public final static AcceptEncodingValue DEFLATE = registerConstant("deflate");

    /**
     * Holds a {@link AcceptEncodingValue} for gzip.
     */
    public final static AcceptEncodingValue GZIP = registerConstant("gzip");

    /**
     * Holds a {@link AcceptEncodingValue} for identity.
     */
    public final static AcceptEncodingValue IDENTITY = registerConstant("identity");

    /**
     * Holds a {@link AcceptEncodingValue wildcard}
     */
    @SuppressWarnings("StaticInitializerReferencesSubClass")
    public final static AcceptEncodingValue WILDCARD_ENCODING = AcceptEncodingValueWildcard.with(NO_PARAMETERS);

    /**
     * Creates and then registers the constant.
     */
    static private AcceptEncodingValue registerConstant(final String text) {
        final AcceptEncodingValueNonWildcard contentType = nonWildcard(text, NO_PARAMETERS);
        CONSTANTS.put(text, contentType);
        return contentType;
    }

    /**
     * Parses text into a {@link AcceptEncodingValue}.
     */
    public static AcceptEncodingValue parse(final String text) {
        return AcceptEncodingValueHeaderParser.parseEncoding(text);
    }

    /**
     * {@see AcceptEncodingValue}
     */
    public static AcceptEncodingValue with(final String value) {
        return "*".equals(value) ?
                wildcard(NO_PARAMETERS) :
                nonWildcard(checkValue(value), NO_PARAMETERS);
    }

    private static String checkValue(final String value) {
        CharPredicates.failIfNullOrEmptyOrFalse(value,
                "value",
                AcceptEncodingValueHeaderParser.RFC2045TOKEN);
        return value;
    }

    /**
     * {@see AcceptEncodingValueNonWildcard}
     */
    static AcceptEncodingValueNonWildcard nonWildcard(final String value,
                                                      final Map<AcceptEncodingValueParameterName<?>, Object> parameters) {
        return AcceptEncodingValueNonWildcard.with(value, checkParameters(parameters));
    }

    /**
     * {@see AcceptEncodingValueWildcard}
     */
    private static AcceptEncodingValueWildcard wildcard(final Map<AcceptEncodingValueParameterName<?>, Object> parameters) {
        return AcceptEncodingValueWildcard.with(checkParameters(parameters));
    }

    /**
     * Package private to limit sub classing.
     */
    AcceptEncodingValue(final String value,
                        final Map<AcceptEncodingValueParameterName<?>, Object> parameters) {
        super(value, parameters);
    }

    @Override
    final String toHeaderTextValue() {
        return this.value;
    }

    @Override
    final String toHeaderTextParameterSeparator() {
        return TO_HEADERTEXT_PARAMETER_SEPARATOR;
    }

    // isXXX...........................................................................................................

    /**
     * Returns true if this accept-encoding is a wildcard.
     */
    public abstract boolean isWildcard();

    // HasQualityFactor................................................................................................

    @Override
    public Optional<Float> qualityFactor() {
        return AcceptEncodingValueParameterName.Q.parameterValue(this);
    }

    // HasHeaderScope .................................................................................................

    @Override
    public final boolean isMultipart() {
        return false;
    }

    @Override
    public final boolean isRequest() {
        return true;
    }

    @Override
    public final boolean isResponse() {
        return false;
    }

    // Predicate .....................................................................................................

    @Override
    public boolean test(final ContentEncoding encoding) {
        Objects.requireNonNull(encoding, "encoding");

        return this.test0(encoding);
    }

    abstract boolean test0(final ContentEncoding encoding);

    // Object..........................................................................................................

    @Override
    final int hashCode0(final String value) {
        return CASE_SENSITIVITY.hash(value);
    }

    @Override
    final boolean equals1(final String value, final String otherValue) {
        return CASE_SENSITIVITY.equals(value, otherValue);
    }

    // Comparable......................................................................................................

    @Override
    public final int compareTo(final AcceptEncodingValue other) {
        return CASE_SENSITIVITY.comparator().compare(this.value, other.value);
    }
}
