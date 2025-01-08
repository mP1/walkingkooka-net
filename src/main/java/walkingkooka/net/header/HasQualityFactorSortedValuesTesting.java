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

import walkingkooka.collect.list.Lists;
import walkingkooka.net.HasQualityFactor;
import walkingkooka.test.Testing;

import java.util.List;

/**
 * A mixin which has helpers to test {@link HasQualityFactorSortedValues#qualityFactorSortedValues()}.
 */
public interface HasQualityFactorSortedValuesTesting extends Testing {

    @SuppressWarnings("unchecked")
    default <V extends HasQualityFactor> void qualitySortedValuesAndCheck(final HasQualityFactorSortedValues<V> has,
                                                                          final V... sorted) {
        this.qualitySortedValuesAndCheck(has, Lists.of(sorted));
    }

    default <V extends HasQualityFactor> void qualitySortedValuesAndCheck(final HasQualityFactorSortedValues<V> has,
                                                                          final List<V> sorted) {
        this.checkEquals(sorted,
            has.qualityFactorSortedValues(),
            () -> "" + has);
    }
}
