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

final class UrlPathPredicateComponentStar extends UrlPathPredicateComponent {

    static UrlPathPredicateComponentStar with(final UrlPathPredicateComponent next) {
        return new UrlPathPredicateComponentStar(next);
    }

    private UrlPathPredicateComponentStar(final UrlPathPredicateComponent next) {
        this.next = next;
    }

    @Override
    boolean testNamesEmpty() {
        return false;
    }

    @Override
    boolean testNamesNotEmpty(final Iterator<UrlPathName> names) {
        names.next();
        return this.next.test(names);
    }

    final UrlPathPredicateComponent next;

    @Override
    public String toString() {
        return "/*" + this.next.toString();
    }
}