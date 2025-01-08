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

import java.util.List;

public final class LinkRelationHeaderParserTest extends HeaderParserTestCase<LinkRelationHeaderParser,
    List<LinkRelation<?>>> {

    @Test
    public void testTokenSeparatorFails() {
        this.parseStringInvalidCharacterFails(";");
    }

    @Test
    public void testKeyValueSeparatorFails() {
        this.parseStringInvalidCharacterFails("=");
    }

    @Test
    public void testMultiValueSeparatorFails() {
        this.parseStringInvalidCharacterFails(",");
    }

    @Test
    public void testWildcardFails() {
        this.parseStringInvalidCharacterFails("*");
    }

    @Test
    public void testSlashFails() {
        this.parseStringInvalidCharacterFails("/");
    }

    @Override
    public List<LinkRelation<?>> parseString(final String text) {
        return LinkRelationHeaderParser.parseLinkRelationList(text);
    }

    @Override
    String valueLabel() {
        return "value";
    }

    @Override
    public Class<LinkRelationHeaderParser> type() {
        return LinkRelationHeaderParser.class;
    }
}
