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

public final class AcceptEncodingTest extends HeaderValue2TestCase<AcceptEncoding, List<AcceptEncodingValue>>
        implements HasQualityFactorSortedValuesTesting,
        ParseStringTesting<AcceptEncoding>,
        PredicateTesting2<AcceptEncoding, ContentEncoding> {

    @Test
    public void testWithNullFails() {
        assertThrows(NullPointerException.class, () -> {
           AcceptEncoding.with(null);
        });
    }

    // HasQualityFactorSortedValuesTesting..............................................................................

    @Test
    public void testQualityFactorSort() {
        final AcceptEncoding accept = AcceptEncoding.parse("a;q=1.0,b");
        this.qualitySortedValuesAndCheck(accept, accept.value());
    }

    @Test
    public void testQualityFactorSort2() {
        this.qualitySortedValuesAndCheck(AcceptEncoding.parse("a;q=0.5,b,c;q=0.75,d;q=1.0"),
                AcceptEncoding.parse("b,d;q=1.0,a;q=0.5,c;q=0.75").value());
    }

    // predicate.......................................................................................................

    @Test
    public void testTestWildcard() {
        this.testTrue(acceptEncoding(AcceptEncodingValue.WILDCARD_ENCODING), ContentEncoding.GZIP);
    }

    @Test
    public void testTestWildcard2() {
        this.testTrue(acceptEncoding(AcceptEncodingValue.WILDCARD_ENCODING), ContentEncoding.parse("abc"));
    }

    @Test
    public void testTestDifferentWildcard() {
        this.testTrue(acceptEncoding(AcceptEncodingValue.with("different"), AcceptEncodingValue.WILDCARD_ENCODING), ContentEncoding.parse("abc"));
    }

    @Test
    public void testTestDifferentNonWildcard() {
        this.testFalse(acceptEncoding(AcceptEncodingValue.with("different")), ContentEncoding.parse("abc"));
    }

    @Test
    public void testTestNonWildcardDifferentCase() {
        this.testTrue(acceptEncoding(AcceptEncodingValue.with("ABC")), ContentEncoding.parse("abc"));
    }

    @Test
    public void testTestNonWildcardSameCase() {
        this.testTrue(acceptEncoding(AcceptEncodingValue.with("xyz")), ContentEncoding.parse("xyz"));
    }

    @Test
    public void testTestNonWildcardSameCaseMany() {
        this.testTrue(acceptEncoding(AcceptEncodingValue.with("xyz")), ContentEncoding.parse("xyz, abc"));
    }

    @Test
    public void testTestNonWildcardSameCaseMany2() {
        this.testTrue(acceptEncoding(AcceptEncodingValue.with("xyz")), ContentEncoding.parse("abc, xyz"));
    }

    @Test
    public void testTestNonWildcardSameCase2() {
        this.testTrue(acceptEncoding(AcceptEncodingValue.with("different"), AcceptEncodingValue.with("ABC")), ContentEncoding.parse("ABC"));
    }

    // parse...........................................................................................................

    @Test
    public void testParse() {
        this.parseStringAndCheck("gzip, *;q=0.5",
                AcceptEncoding.with(Lists.of(AcceptEncodingValue.GZIP,
                AcceptEncodingValue.WILDCARD_ENCODING.setParameters(Maps.of(AcceptEncodingValueParameterName.Q, 0.5f)))));
    }

    // helpers.......................................................................................................

    @Override
    AcceptEncoding createHeaderValue(final List<AcceptEncodingValue> value) {
        return AcceptEncoding.with(value);
    }

    private AcceptEncoding acceptEncoding(final AcceptEncodingValue... value) {
        return this.createHeaderValue(Lists.of(value));
    }

    @Override
    List<AcceptEncodingValue> value() {
        return Lists.of(AcceptEncodingValue.BR, AcceptEncodingValue.GZIP);
    }

    @Override
    List<AcceptEncodingValue> differentValue() {
        return Lists.of(AcceptEncodingValue.GZIP);
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
    public Class<AcceptEncoding> type() {
        return AcceptEncoding.class;
    }

    // ParseStringTesting ........................................................................................

    @Override
    public AcceptEncoding parseString(final String text) {
        return AcceptEncoding.parse(text);
    }

    // Predicate.......................................................................................................

    @Override
    public AcceptEncoding createPredicate() {
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
        return "AcceptEncoding";
    }

    @Override
    public String typeNameSuffix() {
        return "";
    }
}
