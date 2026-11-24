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

    private final static LanguageName EN = LanguageName.with("en");

    private final static LanguageName FR = LanguageName.with("fr");

    private final static LanguageName GR = LanguageName.with("gr");

    @Test
    public void testParseWhitespaceFails() {
        this.parseStringMissingValueFails("  ");
    }

    @Test
    public void testParseCommentFails() {
        this.parseStringCommentFails(
            "(abc)",
            0
        );
    }

    @Test
    public void testParseParametersFails() {
        this.parseStringInvalidCharacterFails(
            "en;x=1",
            ';'
        );
    }

    @Test
    public void testParseQuotedTextFails() {
        this.parseStringInvalidCharacterFails(
            "\"hello\"",
            0
        );
    }

    @Test
    public void testParseSlashFails() {
        this.parseStringInvalidCharacterFails(
            "ab/c",
            '/'
        );
    }

    @Test
    public void testParseWildcardFails() {
        this.parseStringInvalidCharacterFails(
            "*",
            '*'
        );
    }

    @Test
    public void testParseTokenCommaWildcardFails() {
        this.parseStringInvalidCharacterFails(
            "abc, *, def",
            '*'
        );
    }

    @Test
    public void testParseTokenCommentFails() {
        this.parseStringCommentFails(
            "en(abc)",
            2
        );
    }

    @Test
    public void testParseTokenSeparatorFails() {
        this.parseStringInvalidCharacterFails(
            "abc;",
            ';'
        );
    }

    @Test
    public void testParseToken() {
        this.parseStringAndCheck2("en",
            EN);
    }

    @Test
    public void testParseTokenWhitespace() {
        this.parseStringAndCheck2(
            "en ",
            EN
        );
    }

    @Test
    public void testParseWhitespaceToken() {
        this.parseStringAndCheck2(
            " en",
            EN
        );
    }

    @Test
    public void testParseTokenCommaToken() {
        this.parseStringAndCheck2(
            "en,fr",
            EN,
            FR
        );
    }

    @Test
    public void testParseTokenWhitespaceCommaWhitespaceTokenCommaWhitespaceToken() {
        this.parseStringAndCheck2(
            "en, fr,  gr",
            EN,
            FR,
            GR
        );
    }

    private void parseStringAndCheck2(final String text,
                                      final LanguageName... languages) {
        this.parseStringAndCheck(
            text,
            ContentLanguage.with(
                Lists.of(languages)
            )
        );
    }

    @Override
    ContentLanguage parseHeader(final String text) {
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
