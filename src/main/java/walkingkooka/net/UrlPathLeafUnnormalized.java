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

import java.util.Iterator;
import java.util.Optional;

/**
 * A full url path that may require normalization to remove empty, dot or double dot components.
 */
final class UrlPathLeafUnnormalized extends UrlPathLeaf {

    static UrlPathLeafUnnormalized withUnnormalized(final String path,
                                                    final UrlPathName name,
                                                    final Optional<UrlPath> parent) {
        return new UrlPathLeafUnnormalized(path, name, parent);
    }

    private UrlPathLeafUnnormalized(final String path,
                                    final UrlPathName name,
                                    final Optional<UrlPath> parent) {
        super(path, name, parent);
    }

    @Override
    UrlPath appendName(final UrlPathName name,
                       final UrlPath parent) {
        return new UrlPathLeafUnnormalized(this.path + separator().character() + name.value(),
                name,
                Optional.of(parent));
    }

    @Override
    public UrlPath normalize() {
        UrlPath normalized = ROOT;

        for (Iterator<UrlPathName> names = this.iterator(); names.hasNext(); ) {
            final UrlPathName name = names.next();

            switch (name.value()) {
                case "":
                    break;
                case ".":
                    break;
                case "..":
                    normalized = normalized.parentOrSelf();
                    break;
                default:
                    normalized = normalized.append(name);
                    break;
            }
        }

        return normalized;
    }

    // Serialization....................................................................................................

    private final static long serialVersionUID = 1L;
}
