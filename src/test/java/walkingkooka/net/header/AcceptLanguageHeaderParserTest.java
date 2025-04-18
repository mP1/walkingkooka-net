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

import org.junit.jupiter.api.Test;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.map.Maps;

public final class AcceptLanguageHeaderParserTest extends AcceptLanguageOrAcceptLanguageValueHeaderParserTestCase<AcceptLanguageHeaderParser,
    AcceptLanguage> {

    @Test
    public void testMultipleLanguages() {
        this.parseStringAndCheck3("en-US,en;q=0.5",
            this.language("en-US"),
            this.language("en", 0.5f));
    }

    @Test
    public void testMultipleLanguagesSorted() {
        this.parseStringAndCheck3("de;q=0.75,fr;q=0.25,en;q=0.5",
            this.language("de", 0.75f),
            this.language("en", 0.5f),
            this.language("fr", 0.25f));
    }

    private AcceptLanguageValue language(final String language, final float qFactor) {
        return this.language(language)
            .setParameters(Maps.of(AcceptLanguageParameterName.Q, qFactor));
    }

    private AcceptLanguageValue language(final String language) {
        return AcceptLanguageValue.with(LanguageName.with(language));
    }

    private void parseStringAndCheck3(final String text,
                                      final AcceptLanguageValue... languages) {
        this.parseStringAndCheck(text, AcceptLanguage.with(Lists.of(languages)));
    }

    @Override
    void parseStringAndCheck2(final String text, final AcceptLanguageValue language) {
        this.parseStringAndCheck3(text, language);
    }

    @Override
    public AcceptLanguage parseString(final String text) {
        return AcceptLanguageHeaderParser.parseAcceptLanguage(text);
    }

    @Override
    public Class<AcceptLanguageHeaderParser> type() {
        return AcceptLanguageHeaderParser.class;
    }
}
