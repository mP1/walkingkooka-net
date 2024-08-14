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
 * A {@link HeaderHandler} that expects comma separated {@link ETag tags}.
 */
final class ETagListHeaderHandler extends NonStringHeaderHandler<List<ETag>> {

    /**
     * Singleton
     */
    final static ETagListHeaderHandler INSTANCE = new ETagListHeaderHandler();

    /**
     * Private ctor use singleton.
     */
    private ETagListHeaderHandler() {
        super();
    }

    @Override
    List<ETag> parse0(final String text) {
        return ETag.parseList(text);
    }

    @Override
    void checkNonNull(final Object value) {
        this.checkListOfType(value,
                v -> v instanceof ETag,
                ETag.class
        );
    }

    @Override
    String toText0(final List<ETag> value, final Name name) {
        return Header.toHeaderTextList(value, SEPARATOR);
    }

    @Override
    public String toString() {
        return toStringListOf(ETag.class);
    }
}
