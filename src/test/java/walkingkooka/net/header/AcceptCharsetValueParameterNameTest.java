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

final public class AcceptCharsetValueParameterNameTest extends HeaderParameterNameTestCase<AcceptCharsetValueParameterName<?>,
    AcceptCharsetValueParameterName<?>> {

    @Test
    public void testControlCharacterFails() {
        assertThrows(InvalidCharacterException.class, () -> AcceptCharsetValueParameterName.with("parameter\u0001;"));
    }

    @Test
    public void testSpaceFails() {
        assertThrows(InvalidCharacterException.class, () -> AcceptCharsetValueParameterName.with("parameter "));
    }

    @Test
    public void testTabFails() {
        assertThrows(InvalidCharacterException.class, () -> AcceptCharsetValueParameterName.with("parameter\t"));
    }

    @Test
    public void testNonAsciiFails() {
        assertThrows(InvalidCharacterException.class, () -> AcceptCharsetValueParameterName.with("parameter\u0100;"));
    }

    @Test
    public void testValid() {
        this.createNameAndCheck("Custom");
    }

    @Test
    public void testConstantNameReturnsConstant() {
        assertSame(AcceptCharsetValueParameterName.Q,
            AcceptCharsetValueParameterName.with(AcceptCharsetValueParameterName.Q.value()));
    }

    // parameter value......................................................................................

    @Test
    public void testParameterValueAbsent() {
        this.parameterValueAndCheckAbsent(AcceptCharsetValueParameterName.with("absent-parameter"),
            this.acceptCharsetValue());
    }

    @Test
    public void testParameterValuePresent() {
        final AcceptCharsetValueParameterName<Float> parameter = AcceptCharsetValueParameterName.Q;
        final float value = 0.5f;

        this.parameterValueAndCheckPresent(parameter,
            this.acceptCharsetValue(),
            value);
    }

    private AcceptCharsetValue acceptCharsetValue() {
        return AcceptCharsetValue.with(CharsetName.UTF_8)
            .setParameters(Maps.of(AcceptCharsetValueParameterName.Q, 0.5f));
    }

    @Override
    public AcceptCharsetValueParameterName<Object> createName(final String name) {
        return Cast.to(AcceptCharsetValueParameterName.with(name));
    }

    // parse............................................................................................................

    @Test
    public void testParseQFactor() {
        this.parseStringAndCheck(
            AcceptCharsetValueParameterName.Q::parseValue,
            "0.5",
            0.5f
        );
    }

    @Test
    public void testParseString() {
        this.parseStringAndCheck(
            AcceptCharsetValueParameterName.with("xyz")::parseValue,
            "abc",
            "abc"
        );
    }

    // class............................................................................................................

    @Override
    public Class<AcceptCharsetValueParameterName<?>> type() {
        return Cast.to(AcceptCharsetValueParameterName.class);
    }
}
