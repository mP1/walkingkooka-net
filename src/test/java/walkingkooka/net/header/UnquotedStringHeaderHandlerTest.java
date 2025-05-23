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

public final class UnquotedStringHeaderHandlerTest extends QuotedOrUnquotedStringHeaderHandlerTestCase<UnquotedStringHeaderHandler> {

    @Test
    public void testParseOpeningDoubleQuoteFails() {
        this.parseStringFails(
            "\"abc",
            new HeaderException("Invalid character '\\\"' at 0")
        );
    }

    @Test
    public void testParseBackslashFails() {
        this.parseStringFails(
            "a\\bc",
            new HeaderException("Invalid character '\\\\' at 1")
        );
    }

    @Test
    public void testRoundtrip() {
        this.parseAndToTextAndCheck("abc", "abc");
    }

    @Override
    public String typeNamePrefix() {
        return "UnquotedString";
    }

    @Override
    String invalidHeader() {
        return "123";
    }

    @Override
    protected UnquotedStringHeaderHandler handler() {
        return UnquotedStringHeaderHandler.with(this.charPredicate());
    }

    @Override
    public Class<UnquotedStringHeaderHandler> type() {
        return UnquotedStringHeaderHandler.class;
    }
}
