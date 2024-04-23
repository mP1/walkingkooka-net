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

import java.util.Optional;

/**
 * A full url path that is normalized and does not contain an empty, current directory or double dot components.
 */
final class UrlPathLeafNormalized extends UrlPathLeaf {

    static UrlPathLeafNormalized withNormalized(final String path,
                                                final UrlPathName name,
                                                final Optional<UrlPath> parent) {
        return new UrlPathLeafNormalized(path, name, parent);
    }

    private UrlPathLeafNormalized(final String path,
                                  final UrlPathName name,
                                  final Optional<UrlPath> parent) {
        super(path, name, parent);
    }

    @Override
    UrlPath appendName(final UrlPathName name, final UrlPath parent) {
        final String path = this.path + separator().character() + name.value();
        final Optional<UrlPath> parent2 = Optional.of(parent);

        return name.isNormalized() ?
                new UrlPathLeafNormalized(path, name, parent2) :
                unnormalized(path, name, parent2);
    }

    @Override
    public UrlPath normalize() {
        return this;
    }

    // Serialization....................................................................................................

    private final static long serialVersionUID = 1L;
}
