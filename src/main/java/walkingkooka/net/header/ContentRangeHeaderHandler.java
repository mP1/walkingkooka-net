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
 * A {@link HeaderHandler} that parses a header value into a {@link ContentRange}.
 */
final class ContentRangeHeaderHandler extends NonStringHeaderHandler<ContentRange> {

    /**
     * Singleton
     */
    final static ContentRangeHeaderHandler INSTANCE = new ContentRangeHeaderHandler();

    /**
     * Private ctor use singleton.
     */
    private ContentRangeHeaderHandler() {
        super();
    }

    @Override
    ContentRange parse0(final String text, final Name name) {
        return ContentRange.parse(text);
    }

    @Override
    void check0(final Object value, final Name name) {
        this.checkType(value,
                v -> v instanceof ContentRange,
                ContentRange.class,
                name);
    }

    @Override
    String toText0(final ContentRange value, final Name name) {
        return value.toString();
    }

    @Override
    public String toString() {
        return this.toStringType(ContentRange.class);
    }
}
