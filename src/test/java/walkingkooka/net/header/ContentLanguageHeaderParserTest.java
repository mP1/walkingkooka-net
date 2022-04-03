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

public final class ContentLanguageHeaderParserTest extends HeaderParserTestCase<ContentLanguageHeaderParser, ContentLanguage> {

    @Test
    public void testWhitespaceFails() {
        this.parseStringMissingValueFails("  ");
    }

    @Test
    public void testCommentFails() {
        this.parseStringCommentFails("(abc)", 0);
    }

    @Test
    public void testParametersFails() {
        this.parseStringInvalidCharacterFails("en;x=1", ';');
    }

    @Test
    public void testQuotedTextFails() {
        this.parseStringInvalidCharacterFails("\"hello\"", 0);
    }

    @Test
    public void testSlashFails() {
        this.parseStringInvalidCharacterFails("ab/c", '/');
    }

    @Test
    public void testWildcardFails() {
        this.parseStringInvalidCharacterFails("*", '*');
    }

    @Test
    public void testTokenCommaWildcardFails() {
        this.parseStringInvalidCharacterFails("abc, *, def", '*');
    }

    @Test
    public void testTokenCommentFails() {
        this.parseStringCommentFails("en(abc)", 2);
    }

    @Test
    public void testTokenSeparatorFails() {
        this.parseStringInvalidCharacterFails("abc;", ';');
    }

    @Test
    public void testToken() {
        this.parseStringAndCheck2("en",
                this.en());
    }

    @Test
    public void testTokenWhitespace() {
        this.parseStringAndCheck2("en ",
                this.en());
    }

    @Test
    public void testWhitespaceToken() {
        this.parseStringAndCheck2(" en",
                this.en());
    }

    @Test
    public void testTokenCommaToken() {
        this.parseStringAndCheck2("en,fr",
                this.en(),
                this.fr());
    }

    @Test
    public void testTokenWhitespaceCommaWhitespaceTokenCommaWhitespaceToken() {
        this.parseStringAndCheck2("en, fr,  gr",
                this.en(),
                this.fr(),
                this.gr());
    }

    private LanguageName en() {
        return LanguageName.with("en");
    }

    private LanguageName fr() {
        return LanguageName.with("fr");
    }

    private LanguageName gr() {
        return LanguageName.with("gr");
    }

    private void parseStringAndCheck2(final String text, final LanguageName... languages) {
        this.parseStringAndCheck(text, ContentLanguage.with(Lists.of(languages)));
    }

    @Override
    public ContentLanguage parseString(final String text) {
        return ContentLanguageHeaderParser.parseContentLanguage(text);
    }

    @Override
    String valueLabel() {
        return HttpHeaderName.CONTENT_LANGUAGE.toString();
    }

    @Override
    public Class<ContentLanguageHeaderParser> type() {
        return ContentLanguageHeaderParser.class;
    }
}
