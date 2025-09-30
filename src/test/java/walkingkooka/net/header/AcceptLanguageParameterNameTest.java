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
import walkingkooka.Cast;
import walkingkooka.InvalidCharacterException;
import walkingkooka.collect.map.Maps;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

final public class AcceptLanguageParameterNameTest extends HeaderParameterNameTestCase<AcceptLanguageParameterName<?>,
    AcceptLanguageParameterName<?>> {

    @Test
    public void testWithIncludesWhitespaceFails() {
        assertThrows(InvalidCharacterException.class, () -> AcceptLanguageParameterName.with("paramet er"));
    }

    @Test
    @Override
    public void testWith() {
        this.createNameAndCheck("abc123");
    }

    @Test
    public void testConstantNameReturnsConstant() {
        assertSame(AcceptLanguageParameterName.Q,
            AcceptLanguageParameterName.with(AcceptLanguageParameterName.Q.value()));
    }

    @Test
    public void testConstantNameCaseInsensitiveReturnsConstant() {
        final String differentCase = AcceptLanguageParameterName.Q.value().toUpperCase();
        this.checkNotEquals(differentCase, AcceptLanguageParameterName.Q.value());
        assertSame(AcceptLanguageParameterName.Q, AcceptLanguageParameterName.with(differentCase));
    }

    // parameter value......................................................................................

    @Test
    public void testParameterValueAbsent() {
        this.parameterValueAndCheckAbsent(AcceptLanguageParameterName.Q,
            this.languageTag());
    }

    @Test
    public void testParameterValuePresent() {
        final AcceptLanguageParameterName<Float> parameter = AcceptLanguageParameterName.Q;
        final Float value = 0.75f;

        this.parameterValueAndCheckPresent(parameter,
            this.languageTag().setParameters(Maps.of(parameter, value)),
            value);
    }

    private AcceptLanguageValue languageTag() {
        return AcceptLanguageValue.WILDCARD;
    }

    @Override
    public AcceptLanguageParameterName<Object> createName(final String name) {
        return Cast.to(AcceptLanguageParameterName.with(name));
    }

    @Override
    public Class<AcceptLanguageParameterName<?>> type() {
        return Cast.to(AcceptLanguageParameterName.class);
    }
}
