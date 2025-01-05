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

import java.util.Objects;
import java.util.Optional;

/**
 * The value of the filename parameter within a content disposition.
 */
final class ContentDispositionFileNameEncoded extends ContentDispositionFileName {

    /**
     * Factory that creates a {@link ContentDispositionFileNameEncoded}.
     */
    static ContentDispositionFileNameEncoded with(final EncodedText encodedText) {
        Objects.requireNonNull(encodedText, "encodedText");

        return new ContentDispositionFileNameEncoded(encodedText);
    }

    /**
     * Private constructor use factory.
     */
    private ContentDispositionFileNameEncoded(final EncodedText encodedText) {
        super();
        this.encodedText = encodedText;
    }

    // Value .................................................................................

    @Override
    public String value() {
        return this.encodedText.value();
    }

    @Override
    public Optional<CharsetName> charsetName() {
        return Optional.of(this.encodedText.charset());
    }

    @Override
    public Optional<LanguageName> language() {
        return this.encodedText.language();
    }

    @Override ContentDispositionFileNameEncoded computeWithoutPath() {
        final EncodedText encodedText = this.encodedText;
        final String value = encodedText.value();
        final String without = removePathIfNecessaryOrNull(value);

        return null == without ?
                this :
                new ContentDispositionFileNameEncoded(EncodedText.with(encodedText.charset(), encodedText.language(), without));
    }

    /**
     * Potentially may return an {@link Optional#empty()} if any of the given characters requires encoding.
     */
    @Override
    public Optional<ContentDispositionFileName> toNotDecoded() {
        final String value = this.encodedText.value();
        boolean notEncoded = true;

        for (final char c : value.toCharArray()) {
            if (false == ContentDispositionFileNameNotEncoded.FILENAME.test(c)) {
                notEncoded = false;
                break;
            }
        }

        return Optional.ofNullable(
                notEncoded ?
                        ContentDispositionFileName.notEncoded(value) :
                        null
        );
    }

    @Override
    Object equalsValue() {
        return this.encodedText;
    }

    // Header ..........................................................................................................

    @Override
    public String toHeaderText() {
        return this.encodedText.toHeaderText();
    }

    private final EncodedText encodedText;
}
