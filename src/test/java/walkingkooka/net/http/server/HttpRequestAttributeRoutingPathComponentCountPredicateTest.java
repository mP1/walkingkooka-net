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

import java.util.function.IntPredicate;

public final class HttpRequestAttributeRoutingPathComponentCountPredicateTest extends HttpRequestAttributeRoutingTestCase2<HttpRequestAttributeRoutingPathComponentCountPredicate, Integer> {

    @Test
    public void testTestNullFalse() {
        this.testFalse(null);
    }

    @Test
    public void testTrue() {
        this.testTrue(1);
    }

    @Test
    public void testFalse() {
        this.testFalse(2);
    }

    @Test
    public void testTrue2() {
        this.testTrue(3);
    }

    @Test
    public void testEqualsDifferentPredicate() {
        this.checkNotEquals(HttpRequestAttributeRoutingPathComponentCountPredicate.with(i -> (i & 1) == 1));
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createPredicate(), INT_PREDICATE.toString());
    }

    // PredicateTesting.................................................................................................

    @Override
    public HttpRequestAttributeRoutingPathComponentCountPredicate createPredicate() {
        return HttpRequestAttributeRoutingPathComponentCountPredicate.with(INT_PREDICATE);
    }

    private final static IntPredicate INT_PREDICATE = new IntPredicate() {
        @Override
        public boolean test(final int value) {
            return (value & 1) == 1;
        }

        @Override
        public String toString() {
            return "odd";
        }
    };

    // ClassTesting.....................................................................................................

    @Override
    public Class<HttpRequestAttributeRoutingPathComponentCountPredicate> type() {
        return HttpRequestAttributeRoutingPathComponentCountPredicate.class;
    }
}
