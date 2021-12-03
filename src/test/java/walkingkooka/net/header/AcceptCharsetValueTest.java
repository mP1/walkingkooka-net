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
import walkingkooka.predicate.PredicateTesting;
import walkingkooka.reflect.JavaVisibility;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class AcceptCharsetValueTest extends HeaderWithParametersTestCase<AcceptCharsetValue, AcceptCharsetValueParameterName<?>>
        implements PredicateTesting {

    private final static CharsetName VALUE = CharsetName.UTF_8;
    private final static String PARAMETER_VALUE = "v1";
    private final static float Q = 0.5f;

    // with.........................................................................................

    @Test
    public void testWithNullValueFails() {
        assertThrows(NullPointerException.class, () -> AcceptCharsetValue.with(null));
    }

    @Test
    public void testWith() {
        final AcceptCharsetValue charset = this.acceptCharsetValue();
        this.check(charset);
    }

    // setValue ...........................................................................................

    @Test
    public void testSetValueNullFails() {
        assertThrows(NullPointerException.class, () -> this.acceptCharsetValue().setValue(null));
    }

    @Test
    public void testSetValueSame() {
        final AcceptCharsetValue charset = this.acceptCharsetValue();
        assertSame(charset, charset.setValue(VALUE));
    }

    @Test
    public void testSetValueDifferent() {
        final AcceptCharsetValue charset = this.acceptCharsetValue();
        final CharsetName value = CharsetName.with("different");
        this.check(charset.setValue(value), value, this.parameters());
        this.check(charset);
    }

    // setParameters ...........................................................................................

    @Test
    public void testSetParametersInvalidParameterValueFails() {
        assertThrows(HeaderException.class, () -> this.acceptCharsetValue().setParameters(this.parameters("Q", "INVALID!")));
    }

    @Test
    public void testSetParametersDifferent() {
        final AcceptCharsetValue charset = this.acceptCharsetValue();
        final Map<AcceptCharsetValueParameterName<?>, Object> parameters = this.parameters("different", "2");
        this.check(charset.setParameters(parameters), VALUE, parameters);
        this.check(charset);
    }

    @Test
    public void testWildcardSetParametersDifferent() {
        final AcceptCharsetValue charset = AcceptCharsetValue.WILDCARD_VALUE;
        final Map<AcceptCharsetValueParameterName<?>, Object> parameters = this.parameters("different", "2");
        this.check(charset.setParameters(parameters), CharsetName.WILDCARD_CHARSET, parameters);
        this.check(charset, CharsetName.WILDCARD_CHARSET, AcceptCharsetValue.NO_PARAMETERS);
    }

    @Test
    public void testWildcardSetParametersDifferent2() {
        final AcceptCharsetValue charset = AcceptCharsetValue.WILDCARD_VALUE;
        final Map<AcceptCharsetValueParameterName<?>, Object> parameters = this.parameters("different", "2");
        assertSame(charset, charset.setParameters(parameters).setParameters(AcceptCharsetValue.NO_PARAMETERS));
    }

    // toHeaderText ...........................................................................................

    @Test
    public void testToHeaderTextNoParameters() {
        this.toHeaderTextAndCheck(AcceptCharsetValue.with(VALUE),
                VALUE.toHeaderText());
    }

    @Test
    public void testToHeaderTextWithParameters() {
        this.toHeaderTextAndCheck(this.acceptCharsetValue(),
                VALUE + "; p1=v1");
    }

    @Test
    public void testToHeaderTextWithSeveralParameters() {
        this.toHeaderTextAndCheck(AcceptCharsetValue.with(VALUE)
                        .setParameters(this.parameters("p1", "v1", "p2", "v2")),
                VALUE + "; p1=v1; p2=v2");
    }

    // Predicate. ......................................................................................................

    @Test
    public void testTestCharsetNameTrue() {
        final CharsetName charsetName = CharsetName.with("UTF-16");

        this.testTrue(AcceptCharsetValue.with(charsetName),
                charsetName);
    }

    @Test
    public void testTestCharsetNameTrue2() {
        this.testTrue(AcceptCharsetValue.with(CharsetName.with("utf-16")), CharsetName.with("UTF-16"));
    }

    @Test
    public void testTestCharsetNameFalse() {
        this.testFalse(AcceptCharsetValue.with(CharsetName.with("UTF-8")),
                CharsetName.with("UTF-16"));
    }

    // isWildcard ......................................................................................................

    @Test
    public void testIsWildcard() {
        this.isWildcardAndCheck(false);
    }

    // HashCodeEqualsDefined ..................................................................................................

    @Test
    public void testEqualsDifferentCharset() {
        this.checkNotEquals(AcceptCharsetValue.with(CharsetName.UTF_16)
                .setParameters(this.parameters()));
    }

    @Test
    public void testEqualsDifferentParameterValue() {
        this.checkNotEquals(AcceptCharsetValue.with(VALUE)
                .setParameters(this.parameters(AcceptCharsetValueParameterName.Q, Q + 0.5f)));
    }

    @Test
    public void testEqualsDifferentParameter() {
        this.checkNotEquals(AcceptCharsetValue.with(VALUE)
                .setParameters(this.parameters(AcceptCharsetValueParameterName.with("different"), "xyz")));
    }

    // equalsIgnoringParameters.........................................................................................

    @Test
    public void testEqualsIgnoringParametersDifferent() {
        this.equalsIgnoringParametersAndCheck(
                AcceptCharsetValue.with(CharsetName.UTF_8),
                AcceptCharsetValue.with(CharsetName.UTF_16),
                false);
    }

    @Test
    public void testEqualsIgnoringParametersDifferentParameters() {
        this.equalsIgnoringParametersAndCheck(
                AcceptCharsetValue.with(CharsetName.UTF_8).setParameters(Maps.of(AcceptCharsetValueParameterName.Q, 1.0f)),
                AcceptCharsetValue.with(CharsetName.UTF_8).setParameters(Maps.of(AcceptCharsetValueParameterName.Q, 0.5f)),
                true);
    }

    // equalsOnlyPresentParameters.........................................................................................

    @Test
    public void testEqualsOnlyPresentParametersDifferent() {
        this.equalsOnlyPresentParametersAndCheck(
                AcceptCharsetValue.with(CharsetName.UTF_8),
                AcceptCharsetValue.with(CharsetName.UTF_16),
                false);
    }

    @Test
    public void testEqualsOnlyPresentParametersDifferentParameters() {
        this.equalsOnlyPresentParametersAndCheck(
                AcceptCharsetValue.with(CharsetName.UTF_8).setParameters(Maps.of(AcceptCharsetValueParameterName.Q, 1.0f)),
                AcceptCharsetValue.with(CharsetName.UTF_8).setParameters(Maps.of(AcceptCharsetValueParameterName.Q, 0.5f)),
                false);
    }

    @Test
    public void testEqualsOnlyPresentParametersDifferentParameters2() {
        this.equalsOnlyPresentParametersAndCheck(
                AcceptCharsetValue.with(CharsetName.UTF_8).setParameters(Maps.of(AcceptCharsetValueParameterName.Q, 1.0f, AcceptCharsetValueParameterName.with("parameter2"), "value2")),
                AcceptCharsetValue.with(CharsetName.UTF_8).setParameters(Maps.of(AcceptCharsetValueParameterName.Q, 1.0f)),
                false);
    }

    @Test
    public void testEqualsOnlyPresentParametersSharedParameters() {
        this.equalsOnlyPresentParametersAndCheck(
                AcceptCharsetValue.with(CharsetName.UTF_8).setParameters(Maps.of(AcceptCharsetValueParameterName.Q, 1.0f)),
                AcceptCharsetValue.with(CharsetName.UTF_8).setParameters(Maps.of(AcceptCharsetValueParameterName.Q, 1.0f)),
                true);
    }

    @Test
    public void testEqualsOnlyPresentParametersExtraParametersIgnored() {
        this.equalsOnlyPresentParametersAndCheck(
                AcceptCharsetValue.with(CharsetName.UTF_8).setParameters(Maps.of(AcceptCharsetValueParameterName.Q, 1.0f)),
                AcceptCharsetValue.with(CharsetName.UTF_8).setParameters(Maps.of(AcceptCharsetValueParameterName.Q, 1.0f, AcceptCharsetValueParameterName.with("parameter2"), "value2")),
                true);
    }

    // toString ...........................................................................................

    @Test
    public void testToStringNoParameters() {
        this.toStringAndCheck(AcceptCharsetValue.with(VALUE),
                VALUE.toHeaderText());
    }

    @Test
    public void testToStringWithParameters() {
        this.toStringAndCheck(this.acceptCharsetValue(),
                VALUE + "; p1=v1");
    }

    @Test
    public void testToStringWithSeveralParameters() {
        this.toStringAndCheck(AcceptCharsetValue.with(VALUE)
                        .setParameters(this.parameters("p1", "v1", "p2", "v2")),
                VALUE + "; p1=v1; p2=v2");
    }

    // toHeaderTextList ...........................................................................................

    @Test
    public void testToHeaderTextListOne() {
        final String text = "a";
        this.toHeaderTextListAndCheck(text,
                AcceptCharsetValue.with(CharsetName.with(text)));
    }

    @Test
    public void testToHeaderTextListOneWithParameters() {
        this.toHeaderTextListAndCheck("a; p1=v1",
                AcceptCharsetValue.with(CharsetName.with("a"))
                        .setParameters(this.parameters()));
    }

    @Test
    public void testToHeaderTextListSeveral() {
        this.toHeaderTextListAndCheck("a, b",
                AcceptCharsetValue.with(CharsetName.with("a")),
                AcceptCharsetValue.with(CharsetName.with("b")));
    }

    // helpers ...........................................................................................

    @Override
    public AcceptCharsetValue createHeaderWithParameters() {
        return this.acceptCharsetValue();
    }

    private AcceptCharsetValue acceptCharsetValue() {
        return AcceptCharsetValue.with(VALUE).setParameters(this.parameters());
    }

    @Override
    AcceptCharsetValueParameterName<?> parameterName() {
        return AcceptCharsetValueParameterName.with("xyz");
    }

    private Map<AcceptCharsetValueParameterName<?>, Object> parameters() {
        return this.parameters("p1", PARAMETER_VALUE);
    }

    private Map<AcceptCharsetValueParameterName<?>, Object> parameters(final String name,
                                                                       final Object value) {
        return this.parameters(AcceptCharsetValueParameterName.with(name), value);
    }

    private Map<AcceptCharsetValueParameterName<?>, Object> parameters(final AcceptCharsetValueParameterName<?> name,
                                                                       final Object value) {
        return Maps.of(name, value);
    }

    private Map<AcceptCharsetValueParameterName<?>, Object> parameters(final String name1,
                                                                       final Object value1,
                                                                       final String name2,
                                                                       final Object value2) {
        return this.parameters(AcceptCharsetValueParameterName.with(name1),
                value1,
                AcceptCharsetValueParameterName.with(name2),
                value2);
    }

    private Map<AcceptCharsetValueParameterName<?>, Object> parameters(final AcceptCharsetValueParameterName<?> name1,
                                                                       final Object value1,
                                                                       final AcceptCharsetValueParameterName<?> name2,
                                                                       final Object value2) {
        return Maps.of(name1, value1, name2, value2);
    }

    private void check(final AcceptCharsetValue acceptCharsetValue) {
        this.check(acceptCharsetValue, VALUE, acceptCharsetValue.parameters());
    }

    private void check(final AcceptCharsetValue acceptCharsetValue,
                       final CharsetName value,
                       final Map<AcceptCharsetValueParameterName<?>, Object> parameters) {
        this.checkEquals(value, acceptCharsetValue.value(), "value");
        this.checkParameters(acceptCharsetValue, parameters);
    }

    @Override
    public AcceptCharsetValue createDifferentHeader() {
        return AcceptCharsetValue.with(CharsetName.UTF_16);
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

    // ClassTesting.....................................................................................................

    @Override
    public Class<AcceptCharsetValue> type() {
        return AcceptCharsetValue.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
