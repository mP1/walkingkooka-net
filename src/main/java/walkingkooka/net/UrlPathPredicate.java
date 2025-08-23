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

import walkingkooka.text.CharSequences;

import java.util.Iterator;
import java.util.function.Predicate;

/**
 * A {@link Predicate} which tests {@link UrlPath} against a pattern.
 * <pre>
 * /path1/&star;/path3
 * /&star;
 * /&star&star;
 * </pre>
 */
final class UrlPathPredicate implements Predicate<UrlPath> {

    static UrlPathPredicate with(final UrlPath path) {
        return new UrlPathPredicate(
            path,
            component(
                path.namesList()
                    .iterator(),
                path
            )
        );
    }

    private static UrlPathPredicateComponent component(final Iterator<UrlPathName> names,
                                                       final UrlPath path) {
        final UrlPathPredicateComponent component;

        if (names.hasNext()) {
            final UrlPathName name = names.next();
            final String nameText = name.value();
            switch (nameText) {
                case "*":
                    component = UrlPathPredicateComponent.star(
                        component(
                            names,
                            path
                        )
                    );
                    break;
                case "**":
                    if (names.hasNext()) {
                        throw new IllegalArgumentException(
                            "Pattern should only contain \"**\" at the end, " +
                                CharSequences.quoteAndEscape(
                                    path.value()
                                )
                        );
                    }
                    component = UrlPathPredicateComponent.starStar();
                    break;
                default:
                    component = UrlPathPredicateComponent.name(
                        name,
                        component(
                            names,
                            path
                        )
                    );
                    break;
            }
        } else {
            component = UrlPathPredicateComponent.terminal();
        }

        return component;
    }

    private UrlPathPredicate(final UrlPath path,
                             final UrlPathPredicateComponent first) {
        this.path = path;
        this.first = first;
    }

    // Predicate........................................................................................................

    @Override
    public boolean test(final UrlPath path) {
        return null != path && this.first.test(
            path.namesList()
                .iterator()
        );
    }

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return this.path.hashCode();
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
            other instanceof UrlPathPredicate && this.equals0((UrlPathPredicate) other);
    }

    private boolean equals0(final UrlPathPredicate other) {
        return this.path.equals(other.path);
    }

    @Override
    public String toString() {
        // for readability return the un-encoded String
        return this.path.value();
    }

    private final UrlPath path;

    private final UrlPathPredicateComponent first;
}
