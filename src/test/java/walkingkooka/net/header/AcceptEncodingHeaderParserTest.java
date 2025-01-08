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

public final class AcceptEncodingHeaderParserTest extends HeaderParserTestCase<AcceptEncodingHeaderParser, AcceptEncoding> {

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
        this.parseStringAndCheck2("gzip",
            AcceptEncodingValue.GZIP);
    }

    @Test
    public void testTokenWhitespace() {
        this.parseStringAndCheck2("gzip ",
            AcceptEncodingValue.GZIP);
    }

    @Test
    public void testWhitespaceToken() {
        this.parseStringAndCheck2(" gzip",
            AcceptEncodingValue.GZIP);
    }

    @Test
    public void testTokenParameter() {
        this.parseStringAndCheck2("gzip;q=0.5",
            AcceptEncodingValue.GZIP.setParameters(Maps.of(AcceptEncodingValueParameterName.Q, 0.5f)));
    }

    @Test
    public void testTokenParameterSemiParameter() {
        this.parseStringAndCheck2("gzip;q=0.5;abc=xyz",
            AcceptEncodingValue.GZIP.setParameters(Maps.of(AcceptEncodingValueParameterName.Q, 0.5f,
                AcceptEncodingValueParameterName.with("abc"), "xyz")));
    }

    @Test
    public void testWildcard() {
        this.parseStringAndCheck2("*",
            AcceptEncodingValue.WILDCARD_ENCODING);
    }

    @Test
    public void testWildcardParameter() {
        this.parseStringAndCheck2("*;q=0.5",
            AcceptEncodingValue.WILDCARD_ENCODING.setParameters(Maps.of(AcceptEncodingValueParameterName.Q, 0.5f)));
    }

    @Test
    public void testTokenCommaToken() {
        this.parseStringAndCheck2("gzip,deflate",
            AcceptEncodingValue.GZIP,
            AcceptEncodingValue.DEFLATE);
    }

    @Test
    public void testTokenCommaWildcard() {
        this.parseStringAndCheck2("gzip,*",
            AcceptEncodingValue.GZIP,
            AcceptEncodingValue.WILDCARD_ENCODING);
    }

    @Test
    public void testWildcardCommaToken() {
        this.parseStringAndCheck2("*,gzip",
            AcceptEncodingValue.WILDCARD_ENCODING,
            AcceptEncodingValue.GZIP);
    }

    @Test
    public void testTokenWhitespaceCommaWhitespaceTokenCommaWhitespaceToken() {
        this.parseStringAndCheck2("gzip, deflate,  br",
            AcceptEncodingValue.GZIP,
            AcceptEncodingValue.DEFLATE,
            AcceptEncodingValue.BR);
    }

    @Test
    public void testSortedByQFactor() {
        this.parseStringAndCheck2("gzip;q=0.8, deflate, br;q=0.9",
            AcceptEncodingValue.DEFLATE,
            AcceptEncodingValue.BR.setParameters(Maps.of(AcceptEncodingValueParameterName.Q, 0.9f)),
            AcceptEncodingValue.GZIP.setParameters(Maps.of(AcceptEncodingValueParameterName.Q, 0.8f)));
    }

    @Test
    public void testSortedByQFactor2() {
        this.parseStringAndCheck2("gzip;q=0.8, deflate;q=1.0",
            AcceptEncodingValue.DEFLATE.setParameters(Maps.of(AcceptEncodingValueParameterName.Q, 1.0f)),
            AcceptEncodingValue.GZIP.setParameters(Maps.of(AcceptEncodingValueParameterName.Q, 0.8f)));
    }

    private void parseStringAndCheck2(final String text,
                                      final AcceptEncodingValue... encodings) {
        this.parseStringAndCheck(text, AcceptEncoding.with(Lists.of(encodings)));
    }

    @Override
    public AcceptEncoding parseString(final String text) {
        return AcceptEncodingHeaderParser.parseAcceptEncoding(text);
    }

    @Override
    String valueLabel() {
        return HttpHeaderName.ACCEPT_ENCODING.toString();
    }

    @Override
    public Class<AcceptEncodingHeaderParser> type() {
        return AcceptEncodingHeaderParser.class;
    }
}
