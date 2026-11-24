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

public final class CommentRemovingHeaderParserTest extends HeaderParserTestCase<CommentRemovingHeaderParser, String> {

    @Test
    public void testParseUnterminatedCommentFails() {
        this.parseStringFails(
            "(unclosed",
            "Missing closing ')' in \"(unclosed\""
        );
    }

    @Test
    public void testParseUnterminatedDoubleQuotedTextFails() {
        this.parseMissingClosingQuoteFails("\"abc");
    }

    @Override
    public void testParseStringEmptyFails() {
    }

    @Test
    public void testParseEmpty() {
        this.parseStringAndCheck("");
    }

    @Test
    public void testParseSpace() {
        this.parseStringAndCheck(" ");
    }

    @Test
    public void testParseLetters() {
        this.parseStringAndCheck("abc");
    }

    @Test
    public void testParseDigits() {
        this.parseStringAndCheck("123");
    }

    @Test
    public void testParseSlash() {
        this.parseStringAndCheck("/");
    }

    @Test
    public void testParseWildcard() {
        this.parseStringAndCheck("*");
    }

    @Test
    public void testParseMimeType() {
        this.parseStringAndCheck("text/plain");
    }

    @Test
    public void testParseMimeTypeStar() {
        this.parseStringAndCheck("text/*");
    }

    @Test
    public void testParseMimeTypeTokenSeparator() {
        this.parseStringAndCheck("text/plain;");
    }

    @Test
    public void testParseMimeTypeWithParameters() {
        this.parseStringAndCheck("text/plain;q=0.5");
    }

    @Test
    public void testParseTokenWithParameters() {
        this.parseStringAndCheck("abc;q=0.5");
    }

    @Test
    public void testParseTokenWithManyParameters() {
        this.parseStringAndCheck("abc;q=0.5,r=2");
    }

    @Test
    public void testParseDoubleQuotedText() {
        this.parseStringAndCheck("\"abc123\"");
    }

    @Test
    public void testParseDoubleQuotedTextWithEscaped() {
        this.parseStringAndCheck("\"abc\\t123\"");
    }

    @Test
    public void testParseEmptyComment() {
        this.parseStringAndCheck(
            "()",
            ""
        );
    }

    @Test
    public void testParseComment() {
        this.parseStringAndCheck(
            "(comment-a1)",
            ""
        );
    }

    @Test
    public void testParseCommentComment() {
        this.parseStringAndCheck(
            "(comment-a1)(comment-b2)",
            ""
        );
    }

    @Test
    public void testParseCommentCommentText() {
        this.parseStringAndCheck(
            "(comment-a1)(comment-b2)abc",
            "abc"
        );
    }

    @Test
    public void testParseCommentTextComment() {
        this.parseStringAndCheck(
            "(comment-a1)bcd(comment-d2)",
            "bcd"
        );
    }

    @Test
    public void testParseTextCommentTextComment() {
        this.parseStringAndCheck(
            "a1(comment-1)b2(comment-2)c3",
            "a1b2c3"
        );
    }

    @Test
    public void testParseTextCommentDoubleQuotedText() {
        this.parseStringAndCheck(
            "a1(comment-1)\"xyz\"",
            "a1\"xyz\""
        );
    }

    @Test
    public void testParseTextCommentDoubleQuotedTextTextSingleQuoted() {
        this.parseStringAndCheck(
            "a1(comment-1)\"xyz\".'qrs'",
            "a1\"xyz\".'qrs'"
        );
    }

    @Test
    public void testParseNoise() {
        this.parseStringAndCheck(
            "a*1;/=\"xyz\",q",
            "a*1;/=\"xyz\",q"
        );
    }

    private void parseStringAndCheck(final String text) {
        this.parseStringAndCheck(text, text);
    }

    @Override
    public String parseString(final String text) {
        return CommentRemovingHeaderParser.removeComments(text);
    }

    @Override
    String valueLabel() {
        return "without comments";
    }

    @Override
    public Class<CommentRemovingHeaderParser> type() {
        return CommentRemovingHeaderParser.class;
    }
}
