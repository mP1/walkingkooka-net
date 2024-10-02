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

import walkingkooka.Cast;
import walkingkooka.naming.Name;
import walkingkooka.predicate.character.CharPredicate;
import walkingkooka.predicate.character.CharPredicates;
import walkingkooka.text.CaseSensitivity;

/**
 * Holds the filename for a {@link WebEntity}.
 */
public final class WebEntityFileName implements Name,
        Comparable<WebEntityFileName> {

    public final static int MIN_LENGTH = 1;

    public final static int MAX_LENGTH = 255;

    public static WebEntityFileName with(final String value) {
        CharPredicates.failIfNullOrEmptyOrFalse(value, "filename", FILENAME);
        Name.checkLength(
                "filename",
                value,
                MIN_LENGTH,
                MAX_LENGTH
        );

        return new WebEntityFileName(value);
    }

    // @VisibleForTesting
    final static CharPredicate FILENAME = CharPredicates.rfc2045Token();

    private WebEntityFileName(final String value) {
        this.value = value;
    }

    // Name ............................................................................................................

    @Override
    public String value() {
        return this.value;
    }

    private final String value;

    // Object ..........................................................................................................

    @Override
    public int hashCode() {
        return this.value().hashCode();
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
                other instanceof WebEntityFileName &&
                        this.equals0(Cast.to(other));
    }

    private boolean equals0(final WebEntityFileName other) {
        return this.value.equals(other.value);
    }

    @Override
    public String toString() {
        return this.value();
    }

    // HasCaseSensitivity...............................................................................................

    @Override
    public CaseSensitivity caseSensitivity() {
        return CASE_SENSITIVITY;
    }

    // @VisibleForTesting
    final static CaseSensitivity CASE_SENSITIVITY = CaseSensitivity.SENSITIVE;

    // Comparable.......................................................................................................

    @Override
    public int compareTo(final WebEntityFileName other) {
        return CASE_SENSITIVITY.comparator()
                .compare(
                        this.value,
                        other.value
                );
    }
}
