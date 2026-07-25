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

import java.util.Objects;
import java.util.Optional;

final class NeverETagComputer implements ETagComputer {

    final static NeverETagComputer INSTANCE = new NeverETagComputer();

    private NeverETagComputer() {
        super();
    }

    @Override
    public Optional<ETag> computeETag(final Binary binary) {
        Objects.requireNonNull(binary, "binary");
        return Optional.empty();
    }

    // Object...........................................................................................................

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
