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

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class OffsetDateTimeHeaderHandlerTest extends
    NonStringHeaderHandlerTestCase<OffsetDateTimeHeaderHandler, OffsetDateTime> {

    @Test
    public void testyParseEmptyFails() {
        this.parseStringFails(
            "",
            new HeaderException("Empty \"text\"")
        );
    }

    @Test
    public void testyParseMissingOpeningDoubleQuoteFails() {
        this.parseStringFails(
            "abc\"",
            new HeaderException("Invalid character 'a' at 0")
        );
    }

    @Test
    public void testyParseMissingClosingDoubleQuoteFails() {
        this.parseStringFails(
            "\"abc",
            new HeaderException("Invalid character 'c' at 3")
        );
    }

    @Override
    public String typeNamePrefix() {
        return OffsetDateTime.class.getSimpleName();
    }

    @Test
    public void testDateWithGmtFails() {
        assertThrows(HeaderException.class, () -> OffsetDateTimeHeaderHandler.INSTANCE.parse("\"Wed, 21 Oct 2015 07:28:00 GMT\""
        ));
    }

    @Test
    public void testContentDispositionCreationDateNegativeOffset() {
        this.parseAndToTextAndCheck("\"Wed, 21 Oct 2015 07:28:00 -0500\"",
            OffsetDateTime.of(2015,
                10,
                21,
                7,
                28,
                0,
                0,
                ZoneOffset.ofHours(-5)));
    }

    @Test
    public void testContentDispositionCreationDatePositiveOffset() {
        this.parseAndToTextAndCheck("\"Wed, 21 Oct 2015 07:28:00 +0500\"",
            OffsetDateTime.of(2015,
                10,
                21,
                7,
                28,
                0,
                0,
                ZoneOffset.ofHours(+5)));
    }

    @Override
    OffsetDateTimeHeaderHandler handler() {
        return OffsetDateTimeHeaderHandler.INSTANCE;
    }

    @Override
    ContentDispositionParameterName<OffsetDateTime> name() {
        return ContentDispositionParameterName.CREATION_DATE;
    }

    @Override
    String invalidHeader() {
        return "abc";
    }

    @Override
    OffsetDateTime value() {
        return OffsetDateTime.of(2000,
            12,
            31,
            6,
            28,
            29,
            0,
            ZoneOffset.ofHours(+10));
    }

    @Override
    String valueType() {
        return this.valueType(OffsetDateTime.class);
    }

    @Override
    String handlerToString() {
        return OffsetDateTime.class.getSimpleName();
    }

    @Override
    public Class<OffsetDateTimeHeaderHandler> type() {
        return OffsetDateTimeHeaderHandler.class;
    }
}
