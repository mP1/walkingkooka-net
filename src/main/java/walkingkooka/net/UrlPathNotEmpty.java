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
abstract class UrlPathNotEmpty extends UrlPath {

    /**
     * Private package constructor
     */
    UrlPathNotEmpty(final String path, final UrlPathName name,
                    final Optional<UrlPath> parent) {
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
    UrlPath appendTo(final UrlPathNotEmpty leaf) {
        final UrlPathName name = this.name;

        return this.parent.orElse(UrlPath.EMPTY)
            .appendTo(leaf)
            .append(name);
    }

    @Override
    public final boolean isRoot() {
        return false;
    }

    @Override //
    final UrlPath parentOrSelf() {
        return this.parent.get();
    }

    @Override //
    final UrlPath pathAfterNotFirst(final int start) {
        final String value = this.path;

        final int length = value.length();
        int componentIndex = value.charAt(0) == SEPARATOR_CHAR ?
            0 :
            1;
        String path = value;

        int i = 0;
        while (i < length) {
            if (SEPARATOR_CHAR == value.charAt(i)) {
                if (start == componentIndex) {
                    path = value.substring(
                        i,
                        length
                    );
                    break;
                }

                componentIndex++;
            }
            i++;
        }

        if (start > componentIndex + 1) {
            throw new IllegalArgumentException("Invalid start " + start + " > " + componentIndex);
        }

        return start > componentIndex || length == i ?
            EMPTY :
            parse(path);
    }

    // pathNameList.....................................................................................................

    @Override
    void gatherPathNames(final List<UrlPathName> names) {
        final UrlPath parent = this.parent.orElse(null);
        if (null != parent) {
            parent.gatherPathNames(names);
        }

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
