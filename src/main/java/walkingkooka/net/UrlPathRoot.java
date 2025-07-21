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

import java.util.List;
import java.util.Optional;

/**
 * A {@link UrlPath} holding the root.
 */
final class UrlPathRoot extends UrlPath {

    /**
     * Used to create the {@link UrlPath#ROOT} constant
     */
    final static UrlPathRoot INSTANCE = new UrlPathRoot();

    private UrlPathRoot() {
        super();
    }

    // https://en.wikipedia.org/wiki/URI_normalization
    //
    // * Removing the default port. An empty or default port component of the URI (port 80 for the http scheme)
    // * with its ":" delimiter should be removed.[8] Example:
    // * http://example.com:80/ → http://example.com/
    @Override
    public String value() {
        return this.separator().string();
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
    UrlPath appendName(final UrlPathName name,
                       final UrlPath parent) {
        final String nameString = name.value();
        final String value;

        final String separatorString = SEPARATOR.string();

        if (nameString.isEmpty()) {
            value = separatorString + separatorString;
        } else {
            value = separatorString + nameString;
        }

        final Optional<UrlPath> parent2 = Optional.of(parent);

        return name.isNormalized() ?
            UrlPathLeafNormalized.withNormalized(value, name, parent2) :
            UrlPathLeafUnnormalized.withUnnormalized(value, name, parent2);
    }

    @Override
    UrlPath appendPath(final UrlPath path) {
        return path.isRoot() || path.isEmpty() ?
            this :
            path.isStartsWithSeparator() ?
                path :
                parse(
                    SEPARATOR_STRING + path.value()
                );
    }

    @Override
    UrlPath appendTo(final UrlPathLeaf leaf) {
        return leaf;
    }

    @Override
    UrlPath parseTrailingSlash() {
        throw new UnsupportedOperationException();
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

    @Override
    UrlPath pathAfterNotFirst(final int start) {
        throw new IllegalArgumentException("Invalid start " + start + " > 0");
    }

    // pathNameList.....................................................................................................

    @Override
    void gatherPathNames(final List<UrlPathName> names) {
        names.add(NAME);
    }

    // Object...........................................................................................................

    @Override
    public String toString() {
        return this.separator().toString();
    }

    // Serialization....................................................................................................

    private final static long serialVersionUID = 1L;
}
