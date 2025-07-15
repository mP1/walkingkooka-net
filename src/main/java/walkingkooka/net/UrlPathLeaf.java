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


import walkingkooka.naming.Path;

import java.util.List;
import java.util.Optional;

/**
 * A {@link Path} leaf which is a non root and may or may not be normalized.
 */
abstract class UrlPathLeaf extends UrlPath {

    /**
     * Private package constructor
     */
    UrlPathLeaf(final String path, final UrlPathName name, final Optional<UrlPath> parent) {
        super();

        this.path = path;
        this.name = name;
        this.parent = parent;
    }

    @Override
    public final UrlPathName name() {
        return this.name;
    }

    private transient final UrlPathName name;

    @Override
    public final Optional<UrlPath> parent() {
        return this.parent;
    }

    private transient final Optional<UrlPath> parent;

    @Override
    public final String value() {
        return this.path;
    }

    final String path;

    @Override
    UrlPath appendPath(final UrlPath path) {
        return path.appendTo(this);
    }

    @Override
    UrlPath appendTo(final UrlPathLeaf leaf) {
        final UrlPathName name = this.name;
        return this.parent.get().appendTo(leaf).append(name);
    }

    @Override
    public final boolean isRoot() {
        return false;
    }

    @Override final UrlPath parentOrSelf() {
        return this.parent.get();
    }

    // pathNameList.....................................................................................................

    @Override
    void gatherPathNames(final List<UrlPathName> names) {
        this.parent.get()
            .gatherPathNames(names);

        names.add(this.name);
    }

    // Object...........................................................................................................

    @Override
    public final String toString() {
        return this.path;
    }

    // Serialization....................................................................................................

    private final static long serialVersionUID = 1L;
}
