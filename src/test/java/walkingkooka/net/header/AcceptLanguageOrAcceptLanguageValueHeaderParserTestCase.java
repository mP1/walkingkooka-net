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

public abstract class AcceptLanguageOrAcceptLanguageValueHeaderParserTestCase<P extends AcceptLanguageOrAcceptLanguageValueHeaderParser, V> extends HeaderParserWithParametersTestCase<P, V> {

    AcceptLanguageOrAcceptLanguageValueHeaderParserTestCase() {
        super();
    }

    @Test
    public final void testParseWildcard() {
        this.parseStringAndCheck2(
            "*",
            AcceptLanguageValue.WILDCARD
        );
    }

    @Test
    public final void testParseWildcardKeyValueSeparatorFails() {
        this.parseStringInvalidCharacterFails("*;=");
    }

    @Test
    public final void testParseWildcardKeyValueSeparatorFails2() {
        this.parseStringInvalidCharacterFails("*; =");
    }

    @Test
    public final void testParseWildcardParameterNameFails() {
        this.parseStringMissingParameterValueFails("*; parameter");
    }

    @Test
    public final void testParseWildcardParameterNameKeyValueSeparatorFails() {
        this.parseStringMissingParameterValueFails("*; parameter=");
    }

    @Test
    public final void testParseWildcardQWeightInvalidValueFails() {
        this.parseStringFails(
            "*; q=ABC",
            new IllegalArgumentException("Invalid \"Q factor\" number in \"ABC\"")
        );
    }

    @Test
    public final void testParseWildcardWithQWeight() {
        this.parseStringAndCheck2(
            "*; q=0.75",
            AcceptLanguageValue.WILDCARD.setParameters(
                Maps.of(
                    AcceptLanguageParameterName.Q,
                    0.75f
                )
            )
        );
    }

    @Test
    public final void testParseWildcardWithParameters() {
        final Map<AcceptLanguageParameterName<?>, Object> parameters = Maps.of(
            AcceptLanguageParameterName.with("a"), "b",
            AcceptLanguageParameterName.with("c"), "d"
        );

        this.parseStringAndCheck2(
            "*; a=b; c=d",
            AcceptLanguageValue.WILDCARD.setParameters(parameters)
        );
    }

    @Test
    public final void testParseLanguage_en() {
        this.parseStringAndCheck2(
            "en",
            AcceptLanguageValue.with(
                LanguageName.with("en")
            )
        );
    }

    @Test
    public final void testParseLanguage_de_CH() {
        this.parseStringAndCheck2(
            "de-CH",
            AcceptLanguageValue.with(
                LanguageName.with("de-CH")
            )
        );
    }

    abstract void parseStringAndCheck2(final String text,
                                       final AcceptLanguageValue expected);

    @Test
    public final void testParseQuotedFails() {
        this.parseStringFails(
            "\"quoted\"",
            InvalidCharacterException.class
        );
    }

    @Override final String valueLabel() {
        return AcceptLanguageOrAcceptLanguageValueHeaderParser.LANGUAGE;
    }
}
