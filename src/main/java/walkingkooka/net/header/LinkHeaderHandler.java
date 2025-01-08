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
 * A {@link HeaderHandler} that converts a {@link String} into a list of {@link Link}.
 */
final class LinkHeaderHandler extends NonStringHeaderHandler<List<Link>> {

    /**
     * Singleton
     */
    final static LinkHeaderHandler INSTANCE = new LinkHeaderHandler();

    /**
     * Private ctor use singleton.
     */
    private LinkHeaderHandler() {
        super();
    }

    @Override
    List<Link> parse0(final String text) {
        return LinkHeaderParser.parseLink(text);
    }

    @Override
    void checkNonNull(final Object value) {
        this.checkListOfType(value,
            v -> v instanceof Link,
            Link.class
        );
    }

    @Override
    String toText0(final List<Link> value, final Name name) {
        return Link.toHeaderTextList(value);
    }

    @Override
    public String toString() {
        return toStringListOf(Link.class);
    }
}
