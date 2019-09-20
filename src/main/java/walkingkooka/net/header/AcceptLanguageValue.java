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
 * Holds a Language with parameters within a accept-language header.
 */
public final class AcceptLanguageValue extends HeaderValueWithParameters2<AcceptLanguageValue, AcceptLanguageParameterName<?>, LanguageName>
        implements Predicate<LanguageName>,
        HasQualityFactor {

    /**
     * No parameters constant.
     */
    public final static Map<AcceptLanguageParameterName<?>, Object> NO_PARAMETERS = Maps.empty();

    /**
     * A {@link AcceptLanguageValue} wildcard without any parameters.
     */
    public final static AcceptLanguageValue WILDCARD = new AcceptLanguageValue(LanguageName.WILDCARD, NO_PARAMETERS);

    /**
     * Factory that creates a new {@link AcceptLanguageValue}
     */
    public static AcceptLanguageValue with(final LanguageName value) {
        checkValue(value);

        return value.isWildcard() ?
                WILDCARD :
                new AcceptLanguageValue(value, NO_PARAMETERS);
    }

    /**
     * Parsers a header value holding a single tag.
     */
    public static AcceptLanguageValue parse(final String text) {
        return AcceptLanguageValueHeaderValueParser.parseLanguage(text);
    }

    /**
     * Private ctor use factory methods.
     */
    private AcceptLanguageValue(final LanguageName value, final Map<AcceptLanguageParameterName<?>, Object> parameters) {
        super(value, parameters);
    }

    // Predicate.......................................................................................................

    /**
     * Only returns true if the {@link LanguageName} satisfies this {@link AcceptLanguageValue}.
     */
    @Override
    public boolean test(final LanguageName language) {
        return this.value.test(language);
    }

    // AcceptLanguage.test
    boolean testContentLanguage(final ContentLanguage contentLanguage) {
        return contentLanguage.value.stream()
                .filter(this.value)
                .limit(1)
                .count() == 1;
    }

    // value............................................................................................................

    /**
     * Would be setter that returns a {@link AcceptLanguageValue} with the given value creating a new instance as necessary.
     */
    public AcceptLanguageValue setValue(final LanguageName value) {
        checkValue(value);

        return this.value.equals(value) ?
                this :
                this.replace(value);
    }

    private static void checkValue(final LanguageName value) {
        Objects.requireNonNull(value, "value");
    }

    // replace ........................................................................................................

    private AcceptLanguageValue replace(final LanguageName value) {
        return this.replace0(value, this.parameters);
    }

    @Override
    AcceptLanguageValue replace(final Map<AcceptLanguageParameterName<?>, Object> parameters) {
        return this.replace0(this.value, parameters);
    }

    private AcceptLanguageValue replace0(final LanguageName name,
                                         final Map<AcceptLanguageParameterName<?>, Object> parameters) {
        return name.isWildcard() && parameters.isEmpty() ?
                WILDCARD :
                new AcceptLanguageValue(name, parameters);
    }

    // headerValue........................................................................................................

    @Override
    String toHeaderTextValue() {
        return this.value.toString();
    }

    @Override
    String toHeaderTextParameterSeparator() {
        return TO_HEADERTEXT_PARAMETER_SEPARATOR;
    }

    /**
     * Returns true if this Language is a wildcard.
     */
    public boolean isWildcard() {
        return this.value.isWildcard();
    }

    // HasQualityFactor................................................................................................

    /**
     * Retrieves the quality factor for this value.
     */
    public Optional<Float> qualityFactor() {
        return this.qualityFactor(AcceptLanguageParameterName.Q);
    }

    // HasHeaderScope ....................................................................................................

    @Override
    public boolean isMultipart() {
        return false;
    }

    @Override
    public boolean isRequest() {
        return true;
    }

    @Override
    public boolean isResponse() {
        return true;
    }

    // Object..........................................................................................................

    @Override
    int hashCode0(final LanguageName value) {
        return value.hashCode();
    }

    @Override
    boolean canBeEquals(final Object other) {
        return other instanceof AcceptLanguageValue;
    }

    @Override
    boolean equals1(final LanguageName value, final LanguageName otherValue) {
        return value.equals(otherValue);
    }
}


