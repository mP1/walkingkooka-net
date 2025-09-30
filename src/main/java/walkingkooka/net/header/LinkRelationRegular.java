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

package walkingkooka.net.header;

import walkingkooka.predicate.character.CharPredicates;

/**
 * Holds a link relation with text and not url.<br>
 * <a href="https://tools.ietf.org/search/rfc5988"></a>
 */
final class LinkRelationRegular extends LinkRelation<String> {

    /**
     * Unconditionally creates a {@link LinkRelationRegular}
     */
    static LinkRelationRegular regular(final String value) {
        return new LinkRelationRegular(
            CharPredicates.failIfNullOrEmptyOrInitialAndPartFalse(
                value,
                "value",
                INITIAL_CHAR_PREDICATE,
                PART_CHAR_PREDICATE
            )
        );
    }

    /**
     * Package private to limit sub classing, use a constant or factory.
     */
    private LinkRelationRegular(final String value) {
        super(value);
    }

    @Override
    public boolean isUrl() {
        return false;
    }

    // Header ....................................................................................................

    @Override
    public String toHeaderText() {
        return this.value();
    }

    // Header2 ............................................................................................................

    @Override
    boolean canBeEqual(final Object other) {
        return other instanceof LinkRelationRegular;
    }

    // Comparable.......................................................................................................

    @Override
    int comparePriority() {
        return COMPARE_PRIORITY_REGULAR;
    }
}
