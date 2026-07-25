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

import walkingkooka.Binary;

import java.util.Optional;

/**
 * A computer which can be used to generate or compute the {@link ETag} for a {@link Binary}.
 */
public interface ETagComputer {

    /**
     * Compute {@link ETag} for the given {@link Binary}
     */
    Optional<ETag> computeETag(final Binary binary);
}
