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

/**
 * Base-class for all components within a url path predicate.
 */
abstract class UrlPathPredicateComponent {

    /**
     * {@see UrlPathPredicateComponentName}
     */
    static UrlPathPredicateComponentName name(final UrlPathName name,
                                              final UrlPathPredicateComponent next) {
        return UrlPathPredicateComponentName.with(
            name,
            next
        );
    }

    /**
     * {@see UrlPathPredicateComponentStar}
     */
    static UrlPathPredicateComponentStar star(final UrlPathPredicateComponent next) {
        return UrlPathPredicateComponentStar.with(next);
    }

    /**
     * {@see UrlPathPredicateComponentStarStar}
     */
    static UrlPathPredicateComponentStarStar starStar() {
        return UrlPathPredicateComponentStarStar.INSTANCE;
    }

    /**
     * {@see UrlPathPredicateComponentTerminal}
     */
    static UrlPathPredicateComponentTerminal terminal() {
        return UrlPathPredicateComponentTerminal.INSTANCE;
    }

    UrlPathPredicateComponent() {
        super();
    }

    final boolean test(final Iterator<UrlPathName> names) {
        return names.hasNext() ?
            this.testNamesNotEmpty(names) :
            this.testNamesEmpty();
    }

    abstract boolean testNamesEmpty();

    abstract boolean testNamesNotEmpty(Iterator<UrlPathName> names);
}
