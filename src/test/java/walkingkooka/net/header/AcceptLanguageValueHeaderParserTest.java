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

public final class AcceptLanguageValueHeaderParserTest extends AcceptLanguageOrAcceptLanguageValueHeaderParserTestCase<AcceptLanguageValueHeaderParser, AcceptLanguageValue> {

    @Test
    public void testLanguageTagValueSeparatorFails() {
        this.parseStringInvalidCharacterFails("en,");
    }

    @Override
    void parseStringAndCheck2(final String text, final AcceptLanguageValue expected) {
        this.parseStringAndCheck(text, expected);
    }

    @Override
    public AcceptLanguageValue parseString(final String text) {
        return AcceptLanguageValueHeaderParser.parseLanguage(text);
    }

    @Override
    public Class<AcceptLanguageValueHeaderParser> type() {
        return AcceptLanguageValueHeaderParser.class;
    }
}
