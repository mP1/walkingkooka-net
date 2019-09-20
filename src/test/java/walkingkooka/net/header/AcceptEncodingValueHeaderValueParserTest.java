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
import walkingkooka.collect.map.Maps;

public final class AcceptEncodingValueHeaderValueParserTest extends HeaderValueParserTestCase<AcceptEncodingValueHeaderValueParser, AcceptEncodingValue> {

    @Test
    public void testWhitespaceFails() {
        this.parseStringMissingValueFails("  ");
    }

    @Test
    public void testSlashFails() {
        this.parseStringInvalidCharacterFails("ab/c", '/');
    }

    @Test
    public void testCommentFails() {
        this.parseStringCommentFails("(abc)", 0);
    }

    @Test
    public void testTokenCommentFails() {
        this.parseStringCommentFails("gzip(abc)", 4);
    }

    @Test
    public void testQuotedTextFails() {
        this.parseStringInvalidCharacterFails("\"quoted text 123\"", 0);
    }

    @Test
    public void testTokenQuotedTextFails() {
        this.parseStringInvalidCharacterFails("abc \"quoted text 123\"", '"');
    }

    @Test
    public void testToken() {
        this.parseStringAndCheck("gzip",
                AcceptEncodingValue.GZIP);
    }

    @Test
    public void testTokenWhitespace() {
        this.parseStringAndCheck("gzip ",
                AcceptEncodingValue.GZIP);
    }

    @Test
    public void testWhitespaceToken() {
        this.parseStringAndCheck(" gzip",
                AcceptEncodingValue.GZIP);
    }

    @Test
    public void testTokenParameter() {
        this.parseStringAndCheck("gzip;q=0.5",
                AcceptEncodingValue.GZIP.setParameters(Maps.of(AcceptEncodingValueParameterName.Q, 0.5f)));
    }

    @Test
    public void testTokenParameterSemiParameter() {
        this.parseStringAndCheck("gzip;q=0.5;abc=xyz",
                AcceptEncodingValue.GZIP.setParameters(Maps.of(AcceptEncodingValueParameterName.Q, 0.5f,
                        AcceptEncodingValueParameterName.with("abc"), "xyz")));
    }

    @Test
    public void testWildcard() {
        this.parseStringAndCheck("*",
                AcceptEncodingValue.WILDCARD_ENCODING);
    }

    @Test
    public void testWildcardParameter() {
        this.parseStringAndCheck("*;q=0.5",
                AcceptEncodingValue.WILDCARD_ENCODING.setParameters(Maps.of(AcceptEncodingValueParameterName.Q, 0.5f)));
    }

    @Test
    public void testTokenCommaToken() {
        this.parseStringInvalidCharacterFails("gzip,deflate", ',');
    }

    @Override
    public AcceptEncodingValue parseString(final String text) {
        return AcceptEncodingValueHeaderValueParser.parseEncoding(text);
    }

    @Override
    String valueLabel() {
        return "AcceptEncodingValue";
    }

    @Override
    public Class<AcceptEncodingValueHeaderValueParser> type() {
        return AcceptEncodingValueHeaderValueParser.class;
    }
}
