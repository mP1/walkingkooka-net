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
 * A {@link HeaderHandler} that parses a header value into a {@link ETag}.
 * This is useful for headers such as {@link HttpHeaderName#E_TAG}.
 */
final class ETagHeaderHandler extends NonStringHeaderHandler<ETag> {

    /**
     * Singleton
     */
    final static ETagHeaderHandler INSTANCE = new ETagHeaderHandler();

    /**
     * Private ctor use singleton.
     */
    private ETagHeaderHandler() {
        super();
    }

    @Override
    ETag parse0(final String text) {
        return ETag.parseOne(text);
    }

    @Override
    void checkNonNull(final Object value) {
        this.checkType(value,
            v -> v instanceof ETag,
            ETag.class
        );
    }

    @Override
    String toText0(final ETag value, final Name name) {
        return value.toString();
    }

    @Override
    public String toString() {
        return this.toStringType(ETag.class);
    }
}
