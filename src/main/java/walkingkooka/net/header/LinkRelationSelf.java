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

/**
 * Holds a link relation with text and not url.<br>
 * <a href="https://tools.ietf.org/search/rfc5988"></a>
 */
final class LinkRelationSelf extends LinkRelation<String> {

    /**
     * Unconditionally creates a {@link LinkRelationSelf}
     */
    static LinkRelationSelf create() {
        return new LinkRelationSelf();
    }

    /**
     * Private use instance.
     */
    private LinkRelationSelf() {
        super("self");
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
        return other instanceof LinkRelationSelf;
    }

    // Comparable.......................................................................................................

    @Override
    int comparePriority() {
        return COMPARE_PRIORITY_SELF;
    }
}
