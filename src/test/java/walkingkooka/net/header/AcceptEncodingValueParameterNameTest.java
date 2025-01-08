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

final public class AcceptEncodingValueParameterNameTest extends HeaderParameterNameTestCase<AcceptEncodingValueParameterName<?>,
    AcceptEncodingValueParameterName<?>> {

    @Test
    public void testControlCharacterFails() {
        assertThrows(InvalidCharacterException.class, () -> AcceptEncodingValueParameterName.with("parameter\u0001;"));
    }

    @Test
    public void testSpaceFails() {
        assertThrows(InvalidCharacterException.class, () -> AcceptEncodingValueParameterName.with("parameter "));
    }

    @Test
    public void testTabFails() {
        assertThrows(InvalidCharacterException.class, () -> AcceptEncodingValueParameterName.with("parameter\t"));
    }

    @Test
    public void testNonAsciiFails() {
        assertThrows(InvalidCharacterException.class, () -> AcceptEncodingValueParameterName.with("parameter\u0100;"));
    }

    @Test
    public void testValid() {
        this.createNameAndCheck("Custom");
    }

    @Test
    public void testConstantNameReturnsConstant() {
        assertSame(AcceptEncodingValueParameterName.Q, AcceptEncodingValueParameterName.with(AcceptEncodingValueParameterName.Q.value()));
    }

    // parameter value......................................................................................

    @Test
    public void testParameterValueAbsent() {
        this.parameterValueAndCheckAbsent(AcceptEncodingValueParameterName.Q, AcceptEncodingValue.BR);
    }

    @Test
    public void testParameterValuePresent() {
        final AcceptEncodingValueParameterName<Float> parameter = AcceptEncodingValueParameterName.Q;
        final AcceptEncodingValue encoding = AcceptEncodingValue.nonWildcard("xyz", Maps.of(parameter, 0.75f));

        this.parameterValueAndCheckPresent(parameter, encoding, 0.75f);
    }

    @Test
    public void testParameterValuePresent2() {
        final AcceptEncodingValueParameterName<?> parameter = AcceptEncodingValueParameterName.with("abc");
        final String value = "parameter-value-xyz";
        final AcceptEncodingValue encoding = AcceptEncodingValue.nonWildcard("xyz", Maps.of(parameter, value));

        this.parameterValueAndCheckPresent(parameter, encoding, Cast.to(value));
    }

    @Override
    public AcceptEncodingValueParameterName<Object> createName(final String name) {
        return Cast.to(AcceptEncodingValueParameterName.with(name));
    }

    // parse............................................................................................................

    @Test
    public void testParseQFactor() {
        this.parseStringAndCheck(
            AcceptEncodingValueParameterName.Q::parseValue,
            "0.75",
            0.75f
        );
    }

    @Test
    public void testParseString() {
        this.parseStringAndCheck(
            AcceptEncodingValueParameterName.with("xyz")::parseValue,
            "abc",
            "abc"
        );
    }

    // parse............................................................................................................

    @Override
    public Class<AcceptEncodingValueParameterName<?>> type() {
        return Cast.to(AcceptEncodingValueParameterName.class);
    }
}
