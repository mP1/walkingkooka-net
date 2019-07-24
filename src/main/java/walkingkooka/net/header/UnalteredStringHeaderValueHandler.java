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
 * A {@link HeaderValueHandler} that handles returns {@link String} header values as they are which may or may not include comments etc.
 */
final class UnalteredStringHeaderValueHandler extends StringHeaderValueHandler {

    /**
     * Singleton
     */
    final static UnalteredStringHeaderValueHandler INSTANCE = new UnalteredStringHeaderValueHandler();

    /**
     * Package private to limit sub classing.
     */
    UnalteredStringHeaderValueHandler() {
        super();
    }

    @Override
    String parse0(final String text, final Name name) {
        return text;
    }

    @Override
    void check0(final Object value, final Name name) {
        this.checkType(value, String.class, name);
    }

    @Override
    String toText0(final String value, final Name name) {
        return value;
    }

    @Override
    public String toString() {
        return "String";
    }
}
