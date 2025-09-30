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
import walkingkooka.collect.map.Maps;
import walkingkooka.compare.ComparableTesting2;
import walkingkooka.predicate.PredicateTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.test.ParseStringTesting;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotSame;

public abstract class AcceptEncodingValueTestCase<A extends AcceptEncodingValue> extends HeaderWithParametersTestCase<AcceptEncodingValue, AcceptEncodingValueParameterName<?>>
    implements ComparableTesting2<AcceptEncodingValue>,
    ParseStringTesting<AcceptEncodingValue>,
    PredicateTesting2<AcceptEncodingValue, ContentEncoding> {

    AcceptEncodingValueTestCase() {
        super();
    }

    @Test
    public void testWith() {
        this.checkValue(this.createHeader());
    }

    @Test
    @Override
    public void testWith2() {
        final String text = "unknown";
        this.checkValue(AcceptEncodingValue.with(text),
            text,
            AcceptEncodingValue.NO_PARAMETERS);
    }

    @Test
    public final void testSetParametersDifferent() {
        final A acceptEncoding = this.createHeaderWithParameters();

        final Map<AcceptEncodingValueParameterName<?>, Object> parameters = Maps.of(AcceptEncodingValueParameterName.Q, 0.5f);
        final AcceptEncodingValue different = acceptEncoding.setParameters(parameters);
        assertNotSame(parameters, different);

        this.checkValue(different, acceptEncoding.value(), parameters);
        this.checkValue(acceptEncoding);
    }

    final void checkValue(final AcceptEncodingValue encoding) {
        this.checkValue(encoding, this.value(), AcceptEncodingValue.NO_PARAMETERS);
    }

    final void checkValue(final AcceptEncodingValue encoding,
                          final String value,
                          final Map<AcceptEncodingValueParameterName<?>, Object> parameters) {
        this.checkEquals(value, encoding.value(), "value");
        this.checkEquals(parameters, encoding.parameters(), "parameters");
    }

    @Test
    public final void testHeaderText() {
        final String text = this.value();
        this.toHeaderTextAndCheck(AcceptEncodingValue.with(text), text);
    }

    @Test
    public final void testHeaderTextWithParameters() {
        final String text = this.value();
        this.toHeaderTextAndCheck(AcceptEncodingValue.with(text).setParameters(Maps.of(AcceptEncodingValueParameterName.Q, 0.5f)),
            text + "; q=0.5");
    }

    // HashCodeEqualsDefined ...........................................................................................

    @Test
    public final void testEqualsNonWildcardDifferentValue() {
        this.checkNotEquals(AcceptEncodingValue.with("different"));
    }

    @Test
    public final void testEqualsDifferentParameters() {
        this.checkNotEquals(this.createHeaderWithParameters().setParameters(Maps.of(AcceptEncodingValueParameterName.Q, 0.5f)));
    }

    @Test
    public final void testEqualsParameters() {
        final Map<AcceptEncodingValueParameterName<?>, Object> parameters = Maps.of(AcceptEncodingValueParameterName.Q, 0.5f);
        final A acceptEncoding = this.createHeaderWithParameters();

        this.checkEqualsAndHashCode(acceptEncoding.setParameters(parameters), acceptEncoding.setParameters(parameters));
    }

    @Test
    public final void testToString() {
        this.toStringAndCheck(this.createHeader(), this.value());
    }

    @Test
    public final void testToStringWithParameters() {
        final String value = this.value();
        this.toStringAndCheck(AcceptEncodingValue.with(value).setParameters(Maps.of(AcceptEncodingValueParameterName.Q, 0.5f)),
            value + "; q=0.5");
    }

    @Override
    public abstract A createHeaderWithParameters();

    abstract String value();

    @Override final AcceptEncodingValueParameterName<?> parameterName() {
        return AcceptEncodingValueParameterName.with("xyz");
    }

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
        return false;
    }

    // ClassTesting................................................................................................

    @Override
    public final JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }

    @Override
    public final Class<AcceptEncodingValue> type() {
        return Cast.to(this.encodingType());
    }

    abstract Class<A> encodingType();

    // ComparableTesting................................................................................................

    @Override
    public final A createObject() {
        return this.createComparable();
    }

    @Override
    public final A createComparable() {
        return this.createHeaderWithParameters();
    }


    // ParsingStringTest................................................................................................

    @Override
    public AcceptEncodingValue parseString(final String text) {
        return AcceptEncodingValue.parse(text);
    }

    // PredicateTesting2...............................................................................................

    @Override
    public final AcceptEncodingValue createPredicate() {
        return this.createHeaderWithParameters();
    }

    // ClassName..Testing...............................................................................................

    @Override
    public final String typeNamePrefix() {
        return AcceptEncodingValue.class.getSimpleName();
    }

    @Override
    public final String typeNameSuffix() {
        return "";
    }
}
