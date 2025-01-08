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

import walkingkooka.compare.Comparators;
import walkingkooka.text.CaseSensitivity;

import java.nio.charset.Charset;
import java.util.Comparator;
import java.util.Optional;

/**
 * A {@link CharsetName} holding a supported/known {@link Charset}.
 */
final class CharsetNameSupportedCharset extends CharsetName {
    /**
     * Factory that creates a new {@link CharsetNameSupportedCharset}. This should
     * only be called by {@link CharsetName} when registering constants for all available charsets.
     */
    static CharsetNameSupportedCharset with(final String name, final Charset charset) {
        return new CharsetNameSupportedCharset(name, charset);
    }

    private CharsetNameSupportedCharset(final String name, final Charset charset) {
        super(name);
        this.charsetOptional = Optional.of(charset);
        this.charset = charset;
    }

    @Override
    public Optional<Charset> charset() {
        return this.charsetOptional;
    }

    private final Optional<Charset> charsetOptional;

    private final Charset charset;

    // Predicate........................................................................................................

    @Override
    boolean test0(final CharsetName possible) {
        return possible.test1(this);
    }

    /**
     * If the names match (ignoring case) or any alias exists in the other returns true.
     */
    @Override
    boolean test1(final CharsetNameSupportedCharset contentType) {
        return CHARSET_NAME_COMPARATOR.compare(this.charset.name(), contentType.name) == Comparators.EQUAL ||
            CharsetNameSupport.testCharsetAliases(this.charset, contentType.charset);
    }

    private final static Comparator<String> CHARSET_NAME_COMPARATOR = CaseSensitivity.SENSITIVE.comparator();

    @Override
    boolean test1(final CharsetNameUnsupportedCharset contentType) {
        return false;
    }
}
