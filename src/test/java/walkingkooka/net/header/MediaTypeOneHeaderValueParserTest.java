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

import java.util.Map;

public final class MediaTypeOneHeaderValueParserTest extends MediaTypeHeaderValueParserTestCase<MediaTypeOneHeaderValueParser,
        MediaType> {
    @Test
    public void testParameterSeparatorFails() {
        this.parseMissingValueFails(";", 0);
    }

    @Test
    public void testTypeSlashSubTypeValueSeparatorFails() {
        this.parseInvalidCharacterFails("type/subtype,");
    }

    @Test
    public void testTypeSlashSubTypeParameterSeparatorParameterNameKeyValueSeparatorParameterValueValueSeparatorFails() {
        this.parseInvalidCharacterFails("type/subtype;p=v,");
    }

    @Override
    final void parseAndCheck(final String text,
                             final String type,
                             final String subtype,
                             final Map<MediaTypeParameterName<?>, Object> parameters) {
        this.check(MediaTypeOneHeaderValueParser.parseMediaType(text), type, subtype, parameters);
    }

    @Override
    public MediaType parse(final String text) {
        return MediaTypeOneHeaderValueParser.parseMediaType(text);
    }

    @Override
    public Class<MediaTypeOneHeaderValueParser> type() {
        return MediaTypeOneHeaderValueParser.class;
    }
}
