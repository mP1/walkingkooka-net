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
 * A {@link HeaderValueHandler} that parses a content header value into a {@link CharsetName}.
 */
final class CharsetNameHeaderValueHandler extends NonStringHeaderValueHandler<CharsetName> {

    /**
     * Singleton
     */
    final static CharsetNameHeaderValueHandler INSTANCE = new CharsetNameHeaderValueHandler();

    /**
     * Private ctor use singleton.
     */
    private CharsetNameHeaderValueHandler() {
        super();
    }

    @Override
    CharsetName parse0(final String text, final Name name) {
        return CharsetName.with(text);
    }

    @Override
    void check0(final Object value, final Name name) {
        this.checkType(value, CharsetName.class, name);
    }

    @Override
    String toText0(final CharsetName value, final Name name) {
        return value.toHeaderText();
    }

    @Override
    public String toString() {
        return CharsetName.class.getSimpleName();
    }
}
