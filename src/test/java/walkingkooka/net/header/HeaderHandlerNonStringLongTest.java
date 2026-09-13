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

public final class HeaderHandlerNonStringLongTest extends
    HeaderHandlerNonStringTestCase<HeaderHandlerNonStringLong, Long> {

    private final static String TEXT = "123";
    private final static Long VALUE = 123L;

    @Test
    public void testParseEmptyStringFails() {
        this.parseStringFails(
            "",
            new HeaderException("Empty \"text\"")
        );
    }

    @Test
    public void testParseInvalidNumberFails() {
        this.parseStringFails(
            "ABC",
            new HeaderException("Invalid number in \"ABC\"")
        );
    }

    @Test
    public void testContentLength() {
        this.parseAndToTextAndCheck(TEXT, VALUE);
    }

    @Override
    HeaderHandlerNonStringLong handler() {
        return HeaderHandlerNonStringLong.INSTANCE;
    }

    @Override
    HttpHeaderName<Long> name() {
        return HttpHeaderName.CONTENT_LENGTH;
    }

    @Override
    String invalidHeader() {
        return "abc";
    }

    @Override
    Long value() {
        return VALUE;
    }

    @Override
    String valueType() {
        return this.valueType(Long.class);
    }

    @Override
    String handlerToString() {
        return Long.class.getSimpleName();
    }

    // class............................................................................................................

    @Override
    public Class<HeaderHandlerNonStringLong> type() {
        return HeaderHandlerNonStringLong.class;
    }

    @Override
    public String typeNamePrefix() {
        return HeaderHandlerNonString.class.getSimpleName();
    }

    @Override
    public String typeNameSuffix() {
        return Long.class.getSimpleName();
    }
}
