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

import org.junit.jupiter.api.Test;
import walkingkooka.InvalidCharacterException;
import walkingkooka.collect.map.Maps;

import java.util.Map;

public abstract class AcceptLanguageOrLanguageHeaderValueParserTestCase<P extends AcceptLanguageOrLanguageHeaderValueParser, V> extends HeaderValueParserWithParametersTestCase<P, V> {

    AcceptLanguageOrLanguageHeaderValueParserTestCase() {
        super();
    }

    @Test
    public final void testWildcard() {
        this.parseStringAndCheck2("*", LanguageWithParameters.WILDCARD);
    }

    @Test
    public final void testWildcardKeyValueSeparatorFails() {
        this.parseStringInvalidCharacterFails("*;=");
    }

    @Test
    public final void testWildcardKeyValueSeparatorFails2() {
        this.parseStringInvalidCharacterFails("*; =");
    }

    @Test
    public final void testWildcardParameterNameFails() {
        this.parseStringMissingParameterValueFails("*; parameter");
    }

    @Test
    public final void testWildcardParameterNameKeyValueSeparatorFails() {
        this.parseStringMissingParameterValueFails("*; parameter=");
    }

    @Test
    public final void testWildcardQWeightInvalidValueFails() {
        this.parseStringFails("*; q=ABC",
                "Failed to convert \"q\" value \"ABC\", message: For input string: \"ABC\"");
    }

    @Test
    public final void testWildcardWithQWeight() {
        this.parseStringAndCheck2("*; q=0.75",
                LanguageWithParameters.WILDCARD.setParameters(Maps.of(LanguageParameterName.Q, 0.75f)));
    }

    @Test
    public final void testWildcardWithParameters() {
        final Map<LanguageParameterName<?>, Object> parameters = Maps.of(LanguageParameterName.with("a"), "b",
                LanguageParameterName.with("c"), "d");

        this.parseStringAndCheck2("*; a=b; c=d",
                LanguageWithParameters.WILDCARD.setParameters(parameters));
    }

    @Test
    public final void testLanguage_en() {
        this.parseStringAndCheck2("en", LanguageWithParameters.with(LanguageName.with("en")));
    }

    @Test
    public final void testLanguage_de_CH() {
        this.parseStringAndCheck2("de-CH", LanguageWithParameters.with(LanguageName.with("de-CH")));
    }

    abstract void parseStringAndCheck2(final String text, final LanguageWithParameters expected);

    @Test
    public final void testQuotedFails() {
        this.parseStringFails("\"quoted\"", InvalidCharacterException.class);
    }

    @Override
    final String valueLabel() {
        return AcceptLanguageOrLanguageHeaderValueParser.LANGUAGE;
    }
}
