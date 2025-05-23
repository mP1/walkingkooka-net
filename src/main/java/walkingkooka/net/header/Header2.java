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

import walkingkooka.Cast;
import walkingkooka.Value;
import walkingkooka.collect.list.Lists;

import java.util.List;
import java.util.Objects;

/**
 * Abstract templated class for {@link Header} that hold a single typed {@link Value}.
 */
abstract class Header2<V> implements Header, Value<V> {

    static <V> List<V> nonEmptyImmutableList(final List<V> list,
                                             final String label) {
        Objects.requireNonNull(list, label);
        final List<V> copy = Lists.immutable(list);
        copy
            .forEach(v -> Objects.requireNonNull(v, label + " includes null"));
        if (copy.isEmpty()) {
            throw new IllegalArgumentException("Empty " + label);
        }
        return copy;
    }

    /**
     * Package private to limit sub-classing.
     */
    Header2(final V value) {
        super();
        this.value = value;
    }

    @Override
    public final V value() {
        return this.value;
    }

    final V value;

    @Override
    public final int hashCode() {
        return this.value.hashCode();
    }

    @Override
    public final boolean equals(final Object other) {
        return this == other ||
            this.canBeEqual(other) &&
                this.equals0(Cast.to(other));
    }

    abstract boolean canBeEqual(final Object other);

    private boolean equals0(final Header2<?> other) {
        return this.value.equals(other.value);
    }

    @Override
    public final String toString() {
        return this.toHeaderText();
    }
}
