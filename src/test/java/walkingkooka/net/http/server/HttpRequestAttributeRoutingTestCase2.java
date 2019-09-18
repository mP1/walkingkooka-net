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

import walkingkooka.predicate.PredicateTesting2;
import walkingkooka.test.HashCodeEqualsDefined;
import walkingkooka.test.HashCodeEqualsDefinedTesting2;
import walkingkooka.type.JavaVisibility;

import java.util.function.Predicate;

public abstract class HttpRequestAttributeRoutingTestCase2<P extends Predicate<T> & HashCodeEqualsDefined, T>
        extends HttpRequestAttributeRoutingTestCase<P>
        implements HashCodeEqualsDefinedTesting2<P>,
        PredicateTesting2<P, T> {

    HttpRequestAttributeRoutingTestCase2() {
        super();
    }

    // ClassTesting.....................................................................................................

    @Override
    public final JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }

    // HashCodeEqualsDefined.....................................................................................................

    @Override
    public final P createObject() {
        return this.createPredicate();
    }
}
