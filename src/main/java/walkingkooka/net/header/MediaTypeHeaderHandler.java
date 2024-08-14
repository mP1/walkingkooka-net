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
 * A {@link HeaderHandler} that converts a {@link String} into one {@link MediaType}.
 */
final class MediaTypeHeaderHandler extends NonStringHeaderHandler<MediaType> {

    /**
     * Singleton
     */
    final static MediaTypeHeaderHandler INSTANCE = new MediaTypeHeaderHandler();

    /**
     * Private ctor use singleton.
     */
    private MediaTypeHeaderHandler() {
        super();
    }

    @Override
    MediaType parse0(final String text) {
        return MediaType.parse(text);
    }

    @Override
    void checkNonNull(final Object value) {
        this.checkType(value,
                v -> v instanceof MediaType,
                MediaType.class
        );
    }

    @Override
    String toText0(final MediaType value, final Name name) {
        return value.toString();
    }

    @Override
    public String toString() {
        return toStringType(MediaType.class);
    }
}
