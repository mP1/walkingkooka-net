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
import walkingkooka.net.UrlPathName;
import walkingkooka.predicate.PredicateTesting2;
import walkingkooka.test.ClassTesting2;
import walkingkooka.test.ToStringTesting;
import walkingkooka.type.JavaVisibility;

public final class HttpRequestAttributeRoutingWildcardPredicateTest implements PredicateTesting2<HttpRequestAttributeRoutingWildcardPredicate, UrlPathName>,
        ClassTesting2<HttpRequestAttributeRoutingWildcardPredicate>,
        ToStringTesting<HttpRequestAttributeRoutingWildcardPredicate> {

    @Override
    public void testTestNullFails() {
    }

    @Test
    public void testNonNullTrue() {
        this.testTrue(UrlPathName.with("abc"));
    }

    @Test
    public void testNonNullTrue2() {
        this.testTrue(UrlPathName.with("123"));
    }

    @Test
    public void testWildcardTrue() {
        this.testTrue(UrlPathName.with("*"));
    }

    @Test
    public void testNullFalse() {
        this.testFalse(null);
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createPredicate(), "*");
    }

    @Override
    public HttpRequestAttributeRoutingWildcardPredicate createPredicate() {
        return HttpRequestAttributeRoutingWildcardPredicate.INSTANCE;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }

    @Override
    public Class<HttpRequestAttributeRoutingWildcardPredicate> type() {
        return HttpRequestAttributeRoutingWildcardPredicate.class;
    }
}
