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

package walkingkooka.net.http.server;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.list.Lists;
import walkingkooka.predicate.PredicateTesting2;
import walkingkooka.predicate.Predicates;
import walkingkooka.test.ClassTesting2;
import walkingkooka.test.HashCodeEqualsDefinedTesting;
import walkingkooka.type.JavaVisibility;

import java.util.List;
import java.util.function.Predicate;

public final class HttpRequestAttributeRoutingParameterValuePredicateTest implements ClassTesting2<HttpRequestAttributeRoutingParameterValuePredicate>,
        PredicateTesting2<HttpRequestAttributeRoutingParameterValuePredicate, List<String>>,
        HashCodeEqualsDefinedTesting<HttpRequestAttributeRoutingParameterValuePredicate> {

    private final static String VALUE = "value123";

    @Override
    public void testTestNullFails() {
        throw new UnsupportedOperationException();
    }

    @Test
    public void testValueMissing() {
        this.testFalse(null);
    }

    @Test
    public void testOnlyMatched() {
        this.testTrue(Lists.of(VALUE));
    }

    @Test
    public void testIncluded() {
        this.testTrue(Lists.of("different", VALUE));
    }

    @Test
    public void testNone() {
        this.testFalse(Lists.of("different", "different-2"));
    }

    @Test
    public void testEqualsDifferentPredicate() {
        this.checkNotEquals(HttpRequestAttributeRoutingParameterValuePredicate.with(Predicates.fake()));
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createPredicate(), this.wrappedPredicate().toString());
    }

    // PredicateTesting.................................................................................................

    @Override
    public HttpRequestAttributeRoutingParameterValuePredicate createObject() {
        return this.createPredicate();
    }

    @Override
    public HttpRequestAttributeRoutingParameterValuePredicate createPredicate() {
        return HttpRequestAttributeRoutingParameterValuePredicate.with(this.wrappedPredicate());
    }

    private Predicate<String> wrappedPredicate() {
        return Predicates.is(VALUE);
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<HttpRequestAttributeRoutingParameterValuePredicate> type() {
        return HttpRequestAttributeRoutingParameterValuePredicate.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}