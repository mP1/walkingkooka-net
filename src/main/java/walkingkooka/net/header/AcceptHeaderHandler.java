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

/**
 * A {@link HeaderHandler} that converts a {@link String} into an {@link Accept}.
 */
final class AcceptHeaderHandler extends NonStringHeaderHandler<Accept> {

    /**
     * Singleton
     */
    final static AcceptHeaderHandler INSTANCE = new AcceptHeaderHandler();

    /**
     * Private ctor use singleton.
     */
    private AcceptHeaderHandler() {
        super();
    }

    @Override
    Accept parse0(final String text, final Name name) {
        return Accept.with(MediaType.parseList(text));
    }

    @Override
    void check0(final Object value, final Name name) {
        this.checkType(value,
                (v) -> v instanceof Accept,
                Accept.class,
                name);
    }

    @Override
    String toText0(final Accept accept, final Name name) {
        return accept.toHeaderText();
    }

    @Override
    public String toString() {
        return this.toStringType(Accept.class);
    }
}
