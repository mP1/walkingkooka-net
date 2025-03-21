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
 * A {@link HeaderHandler} that handles content-language headers.<br>
 * <pre>
 * Content-Language: de-DE
 * Content-Language: en-US
 * Content-Language: de-DE, en-CA
 * </pre>
 */
final class ContentLanguageHeaderHandler extends NonStringHeaderHandler<ContentLanguage> {

    /**
     * Singleton
     */
    final static ContentLanguageHeaderHandler INSTANCE = new ContentLanguageHeaderHandler();

    /**
     * Private ctor use singleton.
     */
    private ContentLanguageHeaderHandler() {
        super();
    }

    @Override
    ContentLanguage parse0(final String text) {
        return ContentLanguageHeaderParser.parseContentLanguage(text);
    }

    @Override
    void checkNonNull(final Object value) {
        this.checkType(value,
            v -> v instanceof ContentLanguage,
            ContentLanguage.class
        );
    }

    @Override
    String toText0(final ContentLanguage contentLanguage, final Name name) {
        return contentLanguage.toHeaderText();
    }

    @Override
    public String toString() {
        return ContentLanguage.class.getSimpleName();
    }
}
