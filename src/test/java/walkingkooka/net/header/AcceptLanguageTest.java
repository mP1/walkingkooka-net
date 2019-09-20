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
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.map.Maps;
import walkingkooka.predicate.PredicateTesting2;
import walkingkooka.test.ParseStringTesting;
import walkingkooka.type.JavaVisibility;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class AcceptLanguageTest extends HeaderValue2TestCase<AcceptLanguage, List<AcceptLanguageValue>>
        implements ParseStringTesting<AcceptLanguage>,
        PredicateTesting2<AcceptLanguage, ContentLanguage> {

    @Test
    public void testWithNullFails() {
        assertThrows(NullPointerException.class, () -> {
           AcceptLanguage.with(null);
        });
    }

    // predicate.......................................................................................................

    @Test
    public void testTestWildcard() {
        this.testTrue(acceptLanguage(AcceptLanguageValue.WILDCARD), ContentLanguage.parse("en"));
    }

    @Test
    public void testTestWildcard2() {
        this.testTrue(acceptLanguage(AcceptLanguageValue.WILDCARD), ContentLanguage.parse("fr"));
    }

    @Test
    public void testTestDifferentWildcard() {
        this.testTrue(acceptLanguage(this.en(), AcceptLanguageValue.WILDCARD), ContentLanguage.parse("abc"));
    }

    @Test
    public void testTestDifferentNonWildcard() {
        this.testFalse(ContentLanguage.parse("abc"));
    }

    @Test
    public void testTestNonWildcardDifferentCase() {
        this.testTrue(ContentLanguage.parse("EN"));
    }

    @Test
    public void testTestNonWildcardSameCase() {
        this.testTrue(ContentLanguage.parse("en"));
    }

    @Test
    public void testTestNonWildcardSameCaseMany() {
        this.testTrue(ContentLanguage.parse("en, xyz")); // matches first
    }

    @Test
    public void testTestNonWildcardSameCaseMany2() {
        this.testTrue(ContentLanguage.parse("xyz, en")); // matches second
    }

    @Test
    public void testTestManyAndMany() {
        this.testTrue(AcceptLanguage.parse("fr, en"), ContentLanguage.parse("xyz, en")); // second and second
    }

    // parse.............................................................................................................

    @Test
    public void testParse() {
        this.parseStringAndCheck("en, *;q=0.5",
                AcceptLanguage.with(Lists.of(AcceptLanguageValue.parse("en"),
                AcceptLanguageValue.WILDCARD.setParameters(Maps.of(AcceptLanguageParameterName.Q, 0.5f)))));
    }

    // helpers..........................................................................................................

    @Override
    AcceptLanguage createHeaderValue(final List<AcceptLanguageValue> value) {
        return AcceptLanguage.with(value);
    }

    private AcceptLanguage acceptLanguage(final AcceptLanguageValue... value) {
        return this.createHeaderValue(Lists.of(value));
    }

    @Override
    List<AcceptLanguageValue> value() {
        return Lists.of(AcceptLanguageValue.parse("en"), AcceptLanguageValue.parse("fr"));
    }

    private AcceptLanguageValue en() {
        return AcceptLanguageValue.parse("en");
    }

    private AcceptLanguageValue fr() {
        return AcceptLanguageValue.parse("fr");
    }

    @Override
    List<AcceptLanguageValue> differentValue() {
        return Lists.of(AcceptLanguageValue.with(LanguageName.with("diff")));
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
        return false;
    }

    @Override
    public Class<AcceptLanguage> type() {
        return AcceptLanguage.class;
    }

    // ParseStringTesting ........................................................................................

    @Override
    public AcceptLanguage parseString(final String text) {
        return AcceptLanguage.parse(text);
    }

    // Predicate.......................................................................................................

    @Override
    public AcceptLanguage createPredicate() {
        return this.createHeaderValue();
    }

    // ClassTestCase ............................................................................................

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }

    // TypeNaming ............................................................................................

    @Override
    public String typeNamePrefix() {
        return "AcceptLanguage";
    }

    @Override
    public String typeNameSuffix() {
        return "";
    }
}
