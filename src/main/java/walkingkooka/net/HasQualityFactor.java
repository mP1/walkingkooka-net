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

package walkingkooka.net;

import walkingkooka.net.header.Header;

import java.util.Optional;

/**
 * Provides a getter to retrieve the q (quality factor). Mostly implemented by {@link Header} that also have parameters.
 */
public interface HasQualityFactor {

    /**
     * {@see HasQualityFactorComparator}
     */
    static <T extends HasQualityFactor> HasQualityFactorComparator<T> qualityFactorDescendingComparator() {
        return HasQualityFactorComparator.instance();
    }

    /**
     * Gets the q (quality factor).
     */
    Optional<Float> qualityFactor();

    /**
     * Retrieves the q (quality factor) or defaults to 1.0 if absent.
     * Retrieves the q factor or defaults to {@link #DEFAULT_WEIGHT} when absent.
     */
    default Float qualityFactorOrDefault() {
        return this.qualityFactor().orElse(DEFAULT_WEIGHT);
    }

    float DEFAULT_WEIGHT = 1.0f;

    /**
     * Constant when q (quality factor) is absent.
     */
    Optional<Float> QUALITY_FACTOR_EMPTY = Optional.empty();
}
