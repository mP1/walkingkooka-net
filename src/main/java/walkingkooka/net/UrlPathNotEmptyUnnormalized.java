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

import java.util.Collection;
import java.util.Optional;

/**
 * A full url path that may require normalization to remove empty, dot or double dot components.
 */
final class UrlPathNotEmptyUnnormalized extends UrlPathNotEmpty {

    static UrlPathNotEmptyUnnormalized withUnnormalized(final String path,
                                                        final UrlPathName name,
                                                        final Optional<UrlPath> parent) {
        return new UrlPathNotEmptyUnnormalized(path, name, parent);
    }

    private UrlPathNotEmptyUnnormalized(final String path,
                                        final UrlPathName name,
                                        final Optional<UrlPath> parent) {
        super(path, name, parent);
    }

    @Override
    UrlPath appendName(final UrlPathName name,
                       final boolean nameNormalized) {
        final String path = this.path;
        final String nameString = name.value();

        final String newPath;

        if (nameString.isEmpty()) {
            newPath = path + SEPARATOR_CHAR + SEPARATOR_CHAR;
        } else {
            if (path.endsWith(SEPARATOR_STRING)) {
                newPath = path + nameString;
            } else {
                newPath = path + SEPARATOR_CHAR + nameString;
            }
        }

        return new UrlPathNotEmptyUnnormalized(
            newPath,
            name,
            Optional.of(this)
        );
    }

    @Override
    UrlPath parseTrailingSlash() {
        return new UrlPathNotEmptyUnnormalized(
            this.path + this.separator().character(), // path
            UrlPathName.ROOT, // name
            Optional.of(this)
        );
    }

    @Override
    public UrlPath normalize() {
        UrlPath normalized = ROOT;

        final Collection<UrlPathName> names = this.namesList();

        int i = 0;
        final int last = names.size() - 1;

        for (final UrlPathName name : names) {
            if (name.isEmpty()) {
                if (i != last) {
                    i++;
                    continue;
                }
            }
            i++;
            normalized = name.normalize(normalized);
        }

        return normalized;
    }

    // Serialization....................................................................................................

    private final static long serialVersionUID = 1L;
}
