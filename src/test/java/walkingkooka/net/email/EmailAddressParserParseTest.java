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

package walkingkooka.net.email;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class EmailAddressParserParseTest extends EmailAddressParserTestCase<EmailAddressParserParse> {

    @Test
    public void testToString() {
        this.toStringAndCheck(EmailAddressParserParse.with(), "EmailAddress.with");
    }

    @Override
    void parseString(final String text) {
        EmailAddressParserParse.parseOrFail(text);
    }

    @Override
    void parseStringFails(final String text) {
        assertThrows(RuntimeException.class, () -> EmailAddressParserParse.parseOrFail(text));
    }

    @Override
    public Class<EmailAddressParserParse> type() {
        return EmailAddressParserParse.class;
    }
}
