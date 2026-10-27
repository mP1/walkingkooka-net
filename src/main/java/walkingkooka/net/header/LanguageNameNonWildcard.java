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

import walkingkooka.collect.map.Maps;
import walkingkooka.locale.LocaleLanguageTag;

import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * Holds a language.<br>
 * <a href="https://tools.ietf.org/html/bcp47"></a>
 */
final class LanguageNameNonWildcard extends LanguageName {

    private final static Map<String, LanguageNameNonWildcard> CONSTANTS = registerConstants();

    /**
     * Creates constants for all the available {@link Locale locales}
     */
    private static Map<String, LanguageNameNonWildcard> registerConstants() {
        final Map<String, LanguageNameNonWildcard> constants = Maps.sorted(CASE_SENSITIVITY.comparator());

        for (Locale locale : Locale.getAvailableLocales()) {
            if (LocaleLanguageTag.isValidLocale(locale)) {
                final String languageTag = locale.toLanguageTag();
                constants.put(
                    languageTag,
                    new LanguageNameNonWildcard(
                        languageTag,
                        Optional.of(
                            LocaleLanguageTag.fromLocale(locale)
                        )
                    )
                );
            }
        }

        return constants;
    }

    /**
     * Factory that creates a new {@link LanguageNameNonWildcard}
     */
    static LanguageNameNonWildcard nonWildcard(final String localeLanguageTag) {
        final LanguageNameNonWildcard constant = CONSTANTS.get(localeLanguageTag);
        return null != constant ?
            constant :
            new LanguageNameNonWildcard(
                localeLanguageTag,
                Optional.of(
                    LocaleLanguageTag.parse(localeLanguageTag)
                )
            );
    }

    /**
     * Private ctor use factory
     */
    private LanguageNameNonWildcard(final String value,
                                    final Optional<LocaleLanguageTag> localeLanguageTag) {
        super(value);
        this.localeLanguageTag = localeLanguageTag;
    }

    @Override
    public Optional<LocaleLanguageTag> localeLanguageTag() {
        return this.localeLanguageTag;
    }

    private final Optional<LocaleLanguageTag> localeLanguageTag;

    // isXXX............................................................................................................

    @Override
    public boolean isWildcard() {
        return false;
    }

    // Predicate........................................................................................................

    /**
     * True if the languages are equal.
     */
    @Override
    boolean testNonNullNonWildcard(final LanguageName language) {
        return CASE_SENSITIVITY.equals(this.name, language.name);
    }

    @Override
    void testFailIfWildcard() {
    }

    // Object............................................................................................................

}
