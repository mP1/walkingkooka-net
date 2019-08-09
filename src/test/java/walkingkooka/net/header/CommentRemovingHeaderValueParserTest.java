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

public final class CommentRemovingHeaderValueParserTest extends HeaderValueParserTestCase<CommentRemovingHeaderValueParser, String> {

    @Test
    public void testUnterminatedCommentFails() {
        this.parseStringFails("(unclosed", "Missing closing ')' in \"(unclosed\"");
    }

    @Test
    public void testUnterminatedDoubleQuotedTextFails() {
        this.parseMissingClosingQuoteFails("\"abc");
    }

    @Override
    public void testParseStringEmptyFails() {
    }

    @Test
    public void testEmpty() {
        this.parseStringAndCheck("");
    }

    @Test
    public void testSpace() {
        this.parseStringAndCheck(" ");
    }

    @Test
    public void testLetters() {
        this.parseStringAndCheck("abc");
    }

    @Test
    public void testDigits() {
        this.parseStringAndCheck("123");
    }

    @Test
    public void testSlash() {
        this.parseStringAndCheck("/");
    }

    @Test
    public void testWildcard() {
        this.parseStringAndCheck("*");
    }

    @Test
    public void testMimeType() {
        this.parseStringAndCheck("text/plain");
    }

    @Test
    public void testMimeTypeStar() {
        this.parseStringAndCheck("text/*");
    }

    @Test
    public void testMimeTypeTokenSeparator() {
        this.parseStringAndCheck("text/plain;");
    }

    @Test
    public void testMimeTypeWithParameters() {
        this.parseStringAndCheck("text/plain;q=0.5");
    }

    @Test
    public void testTokenWithParameters() {
        this.parseStringAndCheck("abc;q=0.5");
    }

    @Test
    public void testTokenWithManyParameters() {
        this.parseStringAndCheck("abc;q=0.5,r=2");
    }

    @Test
    public void testDoubleQuotedText() {
        this.parseStringAndCheck("\"abc123\"");
    }

    @Test
    public void testDoubleQuotedTextWithEscaped() {
        this.parseStringAndCheck("\"abc\\t123\"");
    }

    @Test
    public void testEmptyComment() {
        this.parseStringAndCheck("()", "");
    }

    @Test
    public void testComment() {
        this.parseStringAndCheck("(comment-a1)", "");
    }

    @Test
    public void testCommentComment() {
        this.parseStringAndCheck("(comment-a1)(comment-b2)", "");
    }

    @Test
    public void testCommentCommentText() {
        this.parseStringAndCheck("(comment-a1)(comment-b2)abc", "abc");
    }

    @Test
    public void testCommentTextComment() {
        this.parseStringAndCheck("(comment-a1)bcd(comment-d2)", "bcd");
    }

    @Test
    public void testTextCommentTextComment() {
        this.parseStringAndCheck("a1(comment-1)b2(comment-2)c3", "a1b2c3");
    }

    @Test
    public void testTextCommentDoubleQuotedText() {
        this.parseStringAndCheck("a1(comment-1)\"xyz\"", "a1\"xyz\"");
    }

    @Test
    public void testTextCommentDoubleQuotedTextTextSingleQuoted() {
        this.parseStringAndCheck("a1(comment-1)\"xyz\".'qrs'", "a1\"xyz\".'qrs'");
    }

    @Test
    public void testNoise() {
        this.parseStringAndCheck("a*1;/=\"xyz\",q", "a*1;/=\"xyz\",q");
    }

    private void parseStringAndCheck(final String text) {
        this.parseStringAndCheck(text, text);
    }

    @Override
    public String parseString(final String text) {
        return CommentRemovingHeaderValueParser.removeComments(text);
    }

    @Override
    String valueLabel() {
        return "without comments";
    }

    @Override
    public Class<CommentRemovingHeaderValueParser> type() {
        return CommentRemovingHeaderValueParser.class;
    }
}
