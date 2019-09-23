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

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;


/**
 * A {@link HeaderValueWithParameters} that represents a single charset name with optional parameters.
 */
final public class AcceptCharsetValue extends HeaderValueWithParameters2<AcceptCharsetValue, AcceptCharsetValueParameterName<?>, CharsetName>
        implements HasQualityFactor,
        Predicate<CharsetName> {

    /**
     * No parameters.
     */
    public final static Map<AcceptCharsetValueParameterName<?>, Object> NO_PARAMETERS = Maps.empty();

    // MediaType constants.................................................................................................

    /**
     * Holds all constants.
     */
    private final static Map<CharsetName, AcceptCharsetValue> CONSTANTS = Maps.sorted();

    /**
     * Holds a {@link AcceptCharsetValue} that matches all {@link AcceptCharsetValue text types}.
     */
    public final static AcceptCharsetValue WILDCARD_VALUE = registerConstant(CharsetName.WILDCARD_CHARSET);

    /**
     * Creates and then registers the constant.
     */
    static private AcceptCharsetValue registerConstant(final CharsetName charsetName) {
        final AcceptCharsetValue acceptCharsetValue = new AcceptCharsetValue(charsetName, NO_PARAMETERS);
        CONSTANTS.put(charsetName, acceptCharsetValue);

        return acceptCharsetValue;
    }

    /**
     * Creates a {@link AcceptCharsetValue} using the already broken type and sub types. It is not possible to pass parameters with or without values.
     */
    public static AcceptCharsetValue with(final CharsetName charsetName) {
        checkValue(charsetName);

        final AcceptCharsetValue acceptCharsetValue = CONSTANTS.get(charsetName);
        return null != acceptCharsetValue ?
                acceptCharsetValue :
                new AcceptCharsetValue(charsetName, NO_PARAMETERS);
    }

    /**
     * Factory method called by various setters and parsers, that tries to match constants before creating an actual new
     * instance.
     */
    private static AcceptCharsetValue withParameters(final CharsetName charset,
                                                     final Map<AcceptCharsetValueParameterName<?>, Object> parameters) {
        final AcceptCharsetValue result = parameters.isEmpty() ?
                CONSTANTS.get(charset) :
                null;
        return null != result ?
                result :
                new AcceptCharsetValue(charset, parameters);
    }

    // ctor ...................................................................................................

    /**
     * Private constructor
     */
    private AcceptCharsetValue(final CharsetName charsetName,
                               final Map<AcceptCharsetValueParameterName<?>, Object> parameters) {
        super(charsetName, parameters);
    }

    // charsetName .......................................................................................................

    /**
     * Would be setter that returns an instance with the new {@link CharsetName}, creating a new instance if required.
     */
    public AcceptCharsetValue setValue(final CharsetName charsetName) {
        checkValue(charsetName);

        return this.value.equals(charsetName) ?
                this :
                this.replace(charsetName, this.parameters);
    }

    private static void checkValue(final CharsetName charsetName) {
        Objects.requireNonNull(charsetName, "charsetName");
    }

    // parameters ...............................................................................................

    /**
     * Retrieves the quality factor for this value.
     */
    public final Optional<Float> qualityFactor() {
        return this.qualityFactor(AcceptCharsetValueParameterName.Q);
    }

    // replace .................................................................................................

    @Override
    AcceptCharsetValue replace(final Map<AcceptCharsetValueParameterName<?>, Object> parameters) {
        return this.replace(this.value, parameters);
    }

    private AcceptCharsetValue replace(final CharsetName charsetName,
                                       final Map<AcceptCharsetValueParameterName<?>, Object> parameters) {
        return AcceptCharsetValue.withParameters(charsetName, parameters);
    }

    // HeaderValue................................................................................................................

    @Override
    String toHeaderTextValue() {
        return this.value.toString();
    }

    @Override
    String toHeaderTextParameterSeparator() {
        return TO_HEADERTEXT_PARAMETER_SEPARATOR;
    }

    @Override
    public boolean isWildcard() {
        return false;
    }

    // HasHeaderScope ....................................................................................................

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
        return true;
    }

    @Override
    int hashCode0(final CharsetName value) {
        return value.hashCode();
    }

    @Override
    boolean equals1(final CharsetName value, final CharsetName otherValue) {
        return value.equals(otherValue);
    }

    @Override
    boolean canBeEquals(final Object other) {
        return other instanceof AcceptCharsetValue;
    }

    // Predicate........................................................................................................

    @Override
    public boolean test(final CharsetName charsetName) {
        return this.value.test(charsetName);
    }
}
