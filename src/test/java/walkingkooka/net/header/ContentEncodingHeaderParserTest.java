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

public final class ContentEncodingHeaderParserTest extends HeaderParserTestCase<ContentEncodingHeaderParser, ContentEncoding> {

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
            "gzip;x=1",
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
            "gzip(abc)",
            4
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
        this.parseStringAndCheck2(
            "gzip",
            Encoding.GZIP
        );
    }

    @Test
    public void testParseTokenWhitespace() {
        this.parseStringAndCheck2(
            "gzip ",
            Encoding.GZIP
        );
    }

    @Test
    public void testParseWhitespaceToken() {
        this.parseStringAndCheck2(
            " gzip",
            Encoding.GZIP
        );
    }

    @Test
    public void testParseTokenCommaToken() {
        this.parseStringAndCheck2(
            "gzip,deflate",
            Encoding.GZIP,
            Encoding.DEFLATE
        );
    }

    @Test
    public void testParseTokenWhitespaceCommaWhitespaceTokenCommaWhitespaceToken() {
        this.parseStringAndCheck2(
            "gzip, deflate,  br",
            Encoding.GZIP,
            Encoding.DEFLATE,
            Encoding.BR
        );
    }

    private void parseStringAndCheck2(final String text,
                                      final Encoding... encodings) {
        this.parseStringAndCheck(
            text,
            ContentEncoding.with(
                Lists.of(encodings)
            )
        );
    }

    @Override
    public ContentEncoding parseString(final String text) {
        return ContentEncodingHeaderParser.parseContentEncoding(text);
    }

    @Override
    String valueLabel() {
        return HttpHeaderName.CONTENT_ENCODING.toString();
    }

    @Override
    public Class<ContentEncodingHeaderParser> type() {
        return ContentEncodingHeaderParser.class;
    }
}
