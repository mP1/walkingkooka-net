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

    static UrlPathPredicate with(final String pattern) {
        final UrlPath path = UrlPath.parse(pattern);

        return new UrlPathPredicate(
                pattern,
                component(
                        path.namesList()
                                .iterator(),
                        pattern
                )
        );
    }

    private static UrlPathPredicateComponent component(final Iterator<UrlPathName> names,
                                                       final String pattern) {
        final UrlPathPredicateComponent component;

        if (names.hasNext()) {
            final UrlPathName name = names.next();
            final String nameText = name.value();
            switch (nameText) {
                case "*":
                    component = UrlPathPredicateComponent.star(
                            component(
                                    names,
                                    pattern
                            )
                    );
                    break;
                case "**":
                    if (names.hasNext()) {
                        throw new IllegalArgumentException("Pattern should only contain \"**\" at the end, " + CharSequences.quoteAndEscape(pattern));
                    }
                    component = UrlPathPredicateComponent.starStar();
                    break;
                default:
                    component = UrlPathPredicateComponent.name(
                            name,
                            component(
                                    names,
                                    pattern
                            )
                    );
                    break;
            }
        } else {
            component = UrlPathPredicateComponent.terminal();
        }

        return component;
    }

    private UrlPathPredicate(final String pattern,
                             final UrlPathPredicateComponent first) {
        this.pattern = pattern;
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
        return UrlPathName.CASE_SENSITIVITY.hash(this.pattern);
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
                other instanceof UrlPathPredicate && this.equals0((UrlPathPredicate) other);
    }

    private boolean equals0(final UrlPathPredicate other) {
        return UrlPathName.CASE_SENSITIVITY.equals(
                this.pattern,
                other.pattern
        );
    }

    @Override
    public String toString() {
        return this.pattern;
    }

    private final String pattern;

    private final UrlPathPredicateComponent first;
}
