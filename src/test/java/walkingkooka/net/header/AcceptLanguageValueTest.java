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
import walkingkooka.collect.map.Maps;
import walkingkooka.predicate.PredicateTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.test.ParseStringTesting;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class AcceptLanguageValueTest extends HeaderWithParametersTestCase<AcceptLanguageValue,
    AcceptLanguageParameterName<?>>
    implements ParseStringTesting<AcceptLanguageValue>,
    PredicateTesting2<AcceptLanguageValue, LanguageName> {

    @Test
    public void testWithNullFails() {
        assertThrows(NullPointerException.class, () -> AcceptLanguageValue.with(null));
    }

    @Test
    public void testWith() {
        final AcceptLanguageValue language = AcceptLanguageValue.with(this.en());
        this.check(language);
    }

    @Test
    public void testWithCached() {
        assertSame(AcceptLanguageValue.with(LanguageName.WILDCARD), AcceptLanguageValue.with(LanguageName.WILDCARD));
    }

    //setValue..........................................................................................................

    @Test
    public void testSetValueNullFails() {
        assertThrows(NullPointerException.class, () -> AcceptLanguageValue.WILDCARD.setValue(null));
    }

    @Test
    public void testSetValueSame() {
        final AcceptLanguageValue language = AcceptLanguageValue.WILDCARD;
        assertSame(language, language.setValue(LanguageName.WILDCARD));
    }

    @Test
    public void testSetValueSame2() {
        final AcceptLanguageValue language = AcceptLanguageValue.with(this.en());
        assertSame(language, language.setValue(this.en()));
    }

    @Test
    public void testSetValueDifferent() {
        final AcceptLanguageValue language = AcceptLanguageValue.WILDCARD;
        final LanguageName name = this.fr();
        final AcceptLanguageValue different = language.setValue(name);
        assertNotSame(language, different);

        this.check(different, name, AcceptLanguageValue.NO_PARAMETERS);
    }

    @Test
    public void testSetValueDifferent2() {
        final AcceptLanguageValue language = AcceptLanguageValue.with(this.en());
        final LanguageName name = this.fr();
        final AcceptLanguageValue different = language.setValue(name);
        assertNotSame(language, different);

        this.check(different, name, AcceptLanguageValue.NO_PARAMETERS);
    }

    @Test
    public void testSetValueDifferentWithParameters() {
        final AcceptLanguageValue language = AcceptLanguageValue.with(this.en()).setParameters(this.parametersWithQFactor());
        final LanguageName name = this.fr();
        final AcceptLanguageValue different = language.setValue(name);
        assertNotSame(language, different);
        this.check(different, name, this.parametersWithQFactor());
    }

    private void check(final AcceptLanguageValue language) {
        this.check(language, this.en(), AcceptLanguageValue.NO_PARAMETERS);
    }

    // setParameters....................................................................................................

    @Test
    public void testSetParameterDifferent() {
        final AcceptLanguageValue language = this.createHeaderWithParameters();
        final Map<AcceptLanguageParameterName<?>, Object> parameters = this.parametersWithQFactor();
        final AcceptLanguageValue different = language.setParameters(parameters);
        this.check(different, this.en(), parameters);
    }

    Map<AcceptLanguageParameterName<?>, Object> parametersWithQFactor() {
        return Maps.of(AcceptLanguageParameterName.Q, 0.75f);
    }

    @Test
    public void testSetParametersDifferentAndBack() {
        assertSame(AcceptLanguageValue.WILDCARD,
            AcceptLanguageValue.WILDCARD
                .setParameters(this.parametersWithQFactor())
                .setParameters(AcceptLanguageValue.NO_PARAMETERS));
    }

    void check(final AcceptLanguageValue language,
               final LanguageName value,
               final Map<AcceptLanguageParameterName<?>, Object> parameters) {
        this.checkEquals(value, language.value(), "value");
        this.checkParameters(language, parameters);
    }

    // ParseStringTesting ..............................................................................................

    @Test
    public void testParse() {
        this.parseStringAndCheck("en",
            AcceptLanguageValue.with(this.en()));
    }

    @Test
    public void testParseWithParameters() {
        this.parseStringAndCheck("en; abc=123",
            AcceptLanguageValue.with(this.en())
                .setParameters(Maps.of(AcceptLanguageParameterName.with("abc"), "123")));
    }

    @Override
    public AcceptLanguageValue parseString(final String text) {
        return AcceptLanguageValue.parse(text);
    }

    // toHeaderTextList................................................................................................

    @Test
    public void testToHeaderTextList() {
        this.toHeaderTextAndCheck(AcceptLanguageValue.with(this.en()), "en");
    }

    @Test
    public void testToHeaderTextListWithParameters() {
        this.toHeaderTextListAndCheck("en; q=0.75",
            this.en()
                .setParameters(Maps.of(AcceptLanguageParameterName.Q, 0.75f)));
    }

    @Test
    public void testToHeaderTextListWildcard() {
        this.toHeaderTextListAndCheck("*",
            AcceptLanguageValue.WILDCARD);
    }

    // test ............................................................................................................

    @Test
    public void testTestWildcard() {
        this.testTrue(AcceptLanguageValue.WILDCARD, this.en());
    }

    @Test
    public void testTestWildcardNonWildcardFails2() {
        this.testTrue(AcceptLanguageValue.WILDCARD, this.fr());
    }

    @Test
    public void testTestNonWildcardNonWildcard() {
        this.testTrue(AcceptLanguageValue.with(this.en()), this.en());
    }

    @Test
    public void testTestNonWildcardNonWildcard2() {
        this.testFalse(AcceptLanguageValue.with(this.en()), this.fr());
    }

    // equalsIgnoringParameters.........................................................................................

    @Test
    public void testEqualsIgnoringParametersDifferent() {
        this.equalsIgnoringParametersAndCheck(
            AcceptLanguageValue.parse("EN"),
            AcceptLanguageValue.parse("FR"),
            false);
    }

    @Test
    public void testEqualsIgnoringParametersDifferentParameters() {
        this.equalsIgnoringParametersAndCheck(
            AcceptLanguageValue.parse("EN;q=1.0"),
            AcceptLanguageValue.parse("EN;q=0.5"),
            true);
    }

    // equalsOnlyPresentParameters.........................................................................................

    @Test
    public void testEqualsOnlyPresentParametersDifferent() {
        this.equalsOnlyPresentParametersAndCheck(
            AcceptLanguageValue.parse("EN"),
            AcceptLanguageValue.parse("FR"),
            false);
    }

    @Test
    public void testEqualsOnlyPresentParametersDifferentParameters() {
        this.equalsOnlyPresentParametersAndCheck(
            AcceptLanguageValue.parse("EN;q=1.0"),
            AcceptLanguageValue.parse("EN;q=0.5"),
            false);
    }

    @Test
    public void testEqualsOnlyPresentParametersDifferentParameters2() {
        this.equalsOnlyPresentParametersAndCheck(
            AcceptLanguageValue.parse("EN;q=1.0;parameter-2=value2"),
            AcceptLanguageValue.parse("EN;q=1.0"),
            false);
    }

    @Test
    public void testEqualsOnlyPresentParametersSharedParameters() {
        this.equalsOnlyPresentParametersAndCheck(
            AcceptLanguageValue.parse("EN;q=1.0"),
            AcceptLanguageValue.parse("EN;q=1.0"),
            true);
    }

    @Test
    public void testEqualsOnlyPresentParametersSharedAndIgnoredParameters() {
        this.equalsOnlyPresentParametersAndCheck(
            AcceptLanguageValue.parse("EN;q=1.0"),
            AcceptLanguageValue.parse("EN;q=1.0;parameter-2=value2"),
            true);
    }

    // helpers..........................................................................................................

    @Override
    public AcceptLanguageValue createHeaderWithParameters() {
        return AcceptLanguageValue.with(this.en());
    }

    private LanguageName en() {
        return LanguageName.with("en");
    }

    private LanguageName fr() {
        return LanguageName.with("fr");
    }

    @Override
    AcceptLanguageParameterName<?> parameterName() {
        return AcceptLanguageParameterName.with("xyz");
    }

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

    @Override
    public AcceptLanguageValue createDifferentHeader() {
        return AcceptLanguageValue.with(this.fr());
    }

    @Override
    public Class<AcceptLanguageValue> type() {
        return AcceptLanguageValue.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }

    // PredicateTesting2................................................................................................

    @Override
    public AcceptLanguageValue createPredicate() {
        return this.createHeaderWithParameters();
    }

    // TypeNameTesting...................................................................................................

    @Override
    public String typeNamePrefix() {
        return AcceptLanguage.class.getSimpleName() + "Value";
    }

    @Override
    public String typeNameSuffix() {
        return ""; // No "Predicate" suffix.
    }
}
