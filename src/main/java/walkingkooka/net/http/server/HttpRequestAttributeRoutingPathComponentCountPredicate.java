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

import java.util.Objects;
import java.util.function.IntPredicate;
import java.util.function.Predicate;

/**
 * A {@link Predicate} wraps a {@link IntPredicate} delegating all methods including {@link IntPredicate#toString()}.
 */
final class HttpRequestAttributeRoutingPathComponentCountPredicate implements Predicate<Integer> {

    /**
     * Creates a new {@link HttpRequestAttributeRoutingPathComponentCountPredicate}.
     */
    static HttpRequestAttributeRoutingPathComponentCountPredicate with(final IntPredicate predicate) {
        Objects.requireNonNull(predicate, "predicate");

        return new HttpRequestAttributeRoutingPathComponentCountPredicate(predicate);
    }

    /**
     * Private ctor
     */
    private HttpRequestAttributeRoutingPathComponentCountPredicate(final IntPredicate predicate) {
        this.predicate = predicate;
    }

    // Predicate .......................................................................................................

    @Override
    public boolean test(final Integer value) {
        return this.predicate.test(value);
    }

    private final IntPredicate predicate;

    // Object ..........................................................................................................

    @Override
    public int hashCode() {
        return this.predicate.hashCode();
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
                other instanceof HttpRequestAttributeRoutingPathComponentCountPredicate &&
                        this.equals0(Cast.to(other));
    }

    private boolean equals0(final HttpRequestAttributeRoutingPathComponentCountPredicate other) {
        return this.predicate.equals(other.predicate);
    }

    @Override
    public String toString() {
        return this.predicate.toString();
    }
}
