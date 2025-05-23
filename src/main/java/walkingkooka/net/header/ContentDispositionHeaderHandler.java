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
 * A {@link HeaderHandler} that handles converting header values to {@link ContentDisposition}.
 */
final class ContentDispositionHeaderHandler extends NonStringHeaderHandler<ContentDisposition> {

    /**
     * Singleton
     */
    final static ContentDispositionHeaderHandler INSTANCE = new ContentDispositionHeaderHandler();

    /**
     * Private ctor use singleton.
     */
    private ContentDispositionHeaderHandler() {
        super();
    }

    @Override
    ContentDisposition parse0(final String text) {
        return ContentDisposition.parse(text);
    }

    @Override
    void checkNonNull(final Object value) {
        this.checkType(value,
            (v) -> v instanceof ContentDisposition,
            ContentDisposition.class
        );
    }

    @Override
    String toText0(final ContentDisposition value, final Name name) {
        return value.toHeaderText();
    }

    @Override
    public String toString() {
        return ContentDisposition.class.getSimpleName();
    }
}
