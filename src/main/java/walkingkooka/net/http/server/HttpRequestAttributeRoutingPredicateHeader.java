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

import walkingkooka.net.header.Header;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * A {@link Predicate} that tests a {@link Header#equalsOnlyPresentParameters(Object)}.
 */
final class HttpRequestAttributeRoutingPredicateHeader extends HttpRequestAttributeRoutingPredicate<Header>
        implements Predicate<Header> {

    /**
     * Creates a new {@link HttpRequestAttributeRoutingPredicateHeader}.
     */
    static HttpRequestAttributeRoutingPredicateHeader with(final Header header) {
        Objects.requireNonNull(header, "header");

        return new HttpRequestAttributeRoutingPredicateHeader(header);
    }

    /**
     * Private ctor
     */
    private HttpRequestAttributeRoutingPredicateHeader(final Header header) {
        super(header);
    }

    // Predicate .......................................................................................................

    @Override
    public boolean test(final Header header) {
        return this.predicate.equalsOnlyPresentParameters(header);
    }

    @Override
    boolean canBeEquals(final Object other) {
        return other instanceof HttpRequestAttributeRoutingPredicateHeader;
    }
}

