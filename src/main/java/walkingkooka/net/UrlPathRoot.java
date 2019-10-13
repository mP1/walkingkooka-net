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
 * A {@link UrlPath} holding the root.
 */
final class UrlPathRoot extends UrlPath {

    /**
     * Used to create the {@link UrlPath#ROOT} constant
     */
    static UrlPathRoot root() {
        return new UrlPathRoot();
    }

    private UrlPathRoot() {
        super();
    }

    @Override
    public String value() {
        return this.name().value();
    }

    @Override
    public UrlPathName name() {
        return NAME;
    }

    private final static UrlPathName NAME = UrlPathName.ROOT;

    @Override
    public Optional<UrlPath> parent() {
        return NO_PARENT;
    }

    @Override
    UrlPath appendName(final UrlPathName name, final UrlPath parent) {
        final String value = SEPARATOR.string() + name.value();
        final Optional<UrlPath> parent2 = Optional.of(parent);

        return name.isNormalized() ?
                UrlPathLeafNormalized.withNormalized(value, name, parent2) :
                UrlPathLeafUnnormalized.withUnnormalized(value, name, parent2);
    }

    @Override
    UrlPath appendPath(final UrlPath path) {
        return path;
    }

    @Override
    UrlPath appendTo(final UrlPathLeaf leaf) {
        return leaf;
    }

    @Override
    public boolean isRoot() {
        return true;
    }

    @Override
    public UrlPath normalize() {
        return this;
    }

    @Override
    UrlPath parentOrSelf() {
        return this;
    }

    // Object...........................................................................................................

    @Override
    public String toString() {
        return this.separator().toString();
    }

    // Serialization....................................................................................................

    private final static long serialVersionUID = 1L;
}
