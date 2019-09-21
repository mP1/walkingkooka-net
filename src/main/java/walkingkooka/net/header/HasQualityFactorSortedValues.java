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

import walkingkooka.Value;
import walkingkooka.collect.list.Lists;
import walkingkooka.net.HasQualityFactor;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Includes a getter to return the values sorted by their quality factor.
 */
public interface HasQualityFactorSortedValues<V extends HasQualityFactor> extends Value<List<V>> {

    /**
     * Helper that returns a new immutable {@link List} holding the values after sorting.
     */
    static <V extends HasQualityFactor> List<V> qualityFactorSort(final List<V> value) {
        return Lists.immutable(value.stream()
                .sorted(HasQualityFactor.qualityFactorDescendingComparator())
                .collect(Collectors.toList()));
    }

    /**
     * Returns any values sorted by a quality factor parameter.
     */
    default List<V> qualityFactorSortedValues() {
        return qualityFactorSort(this.value());
    }
}
