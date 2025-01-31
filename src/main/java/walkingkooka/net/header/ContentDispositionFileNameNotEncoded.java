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

import walkingkooka.predicate.character.CharPredicate;
import walkingkooka.predicate.character.CharPredicates;

import java.util.Optional;

/**
 * The value of the filename parameter within a content disposition.
 */
final class ContentDispositionFileNameNotEncoded extends ContentDispositionFileName {

    /**
     * Factory that creates a {@link ContentDispositionFileNameNotEncoded}.
     */
    static ContentDispositionFileNameNotEncoded with(final String name) {
        FILENAME.failIfNullOrEmptyOrFalse(
            "filename",
            name
        );

        return new ContentDispositionFileNameNotEncoded(name);
    }

    /**
     * A {@link CharPredicate} that only returns true if the given character does not require an encoded filename.
     */
    final static CharPredicate FILENAME = CharPredicates.rfc2045Token()
        .or(
            CharPredicates.is('/')
        );

    /**
     * Private constructor use factory.
     */
    private ContentDispositionFileNameNotEncoded(final String name) {
        super();
        this.name = name;
    }

    @Override
    public ContentDispositionParameterName<?> parameterName() {
        return ContentDispositionParameterName.FILENAME;
    }

    // Value .................................................................................

    @Override
    public String value() {
        return this.name;
    }

    private final String name;

    @Override
    public Optional<CharsetName> charsetName() {
        return NO_CHARSET;
    }

    @Override
    public Optional<LanguageName> language() {
        return NO_LANGUAGE;
    }

    @Override ContentDispositionFileNameNotEncoded computeWithoutPath() {
        final String without = removePathIfNecessaryOrNull(this.name);

        return null == without ?
            this :
            new ContentDispositionFileNameNotEncoded(without);
    }

    @Override
    public Optional<ContentDispositionFileName> toNotEncoded() {
        // always works!
        return Optional.of(this);
    }

    @Override
    Object equalsValue() {
        return this.name;
    }

    // Header .........................................................................................................

    @Override
    public String toHeaderText() {
        return ContentDispositionFileNameNotEncodedHeaderHandler.INSTANCE.toText(this, HttpHeaderName.CONTENT_DISPOSITION);
    }
}
