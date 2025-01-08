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

import walkingkooka.naming.Name;

import java.util.List;

/**
 * A {@link HeaderHandler} that converts a {@link String} into a list of {@link LinkRelation}.
 */
final class LinkRelationHeaderHandler extends NonStringHeaderHandler<List<LinkRelation<?>>> {

    /**
     * Singleton
     */
    final static LinkRelationHeaderHandler INSTANCE = new LinkRelationHeaderHandler();

    /**
     * Private ctor use singleton.
     */
    private LinkRelationHeaderHandler() {
        super();
    }

    @Override
    List<LinkRelation<?>> parse0(final String text) {
        return LinkRelationHeaderParser.parseLinkRelationList(text);
    }

    @Override
    void checkNonNull(final Object value) {
        this.checkListOfType(
            value,
            v -> v instanceof LinkRelation,
            LinkRelation.class
        );
    }

    /**
     * Join all values separated by a single space.
     */
    @Override
    String toText0(final List<LinkRelation<?>> value, final Name name) {
        return LinkRelation.toHeaderTextList(value);
    }

    @Override
    public String toString() {
        return toStringListOf(LinkRelation.class);
    }
}
