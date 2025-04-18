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

import walkingkooka.Cast;

import java.util.function.Predicate;

/**
 * A {@link Predicate} that tests a component of a request during a routing.
 */
abstract class HttpRequestAttributeRoutingPredicate<T> {

    /**
     * Package private ctor
     */
    HttpRequestAttributeRoutingPredicate(final T predicate) {
        this.predicate = predicate;
    }

    final T predicate;

    // Object ..........................................................................................................

    @Override
    public final int hashCode() {
        return this.predicate.hashCode();
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
            this.canBeEquals(other) &&
                this.equals0(Cast.to(other));
    }

    abstract boolean canBeEquals(final Object other);

    private boolean equals0(final HttpRequestAttributeRoutingPredicate<?> other) {
        return this.predicate.equals(other.predicate);
    }

    @Override
    public final String toString() {
        return this.predicate.toString();
    }
}
