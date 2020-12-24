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

public final class ETagOneHeaderParserTest extends ETagHeaderParserTestCase<ETagOneHeaderParser> {

    @Test
    public void testValueSeparatorFails() {
        this.parseStringInvalidCharacterFails(",");
    }

    @Test
    public final void testSeparatorFails() {
        this.parseStringInvalidCharacterFails("\"ABC\",", ',');
    }

    @Test
    public final void testSeparatorWhitespaceFails() {
        this.parseStringInvalidCharacterFails("\"ABC\", ", ',');
    }

    @Test
    public final void testWeakSeparatorWhitespaceFails() {
        this.parseStringInvalidCharacterFails("W/\"ABC\", ", ',');
    }

    @Test
    public void testManyTags() {
        this.parseStringInvalidCharacterFails("\"A\",\"B\"", ',');
    }

    @Override
    public ETag parseString(final String text) {
        return ETagOneHeaderParser.parseOne(text);
    }

    @Override
    public Class<ETagOneHeaderParser> type() {
        return ETagOneHeaderParser.class;
    }
}
