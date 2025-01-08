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
import walkingkooka.naming.Name;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class CacheControlDirectiveExtensionHeaderHandlerTest extends
    NonStringHeaderHandlerTestCase<CacheControlDirectiveExtensionHeaderHandler, Object> {

    @Test
    public void testCheckLong() {
        CacheControlDirectiveExtensionHeaderHandler.INSTANCE.check(123L);
    }

    @Test
    public void testCheckString() {
        CacheControlDirectiveExtensionHeaderHandler.INSTANCE.check("abc123");
    }

    @Test
    public void testParseNumber() {
        this.parseStringAndCheck("123", 123L);
    }

    @Test
    public void testParseText() {
        this.parseStringAndCheck("abc", "abc");
    }

    @Test
    public void testToTextNumber() {
        this.toTextAndCheck(123L, "123");
    }

    @Test
    public void testToTextText() {
        this.toTextAndCheck("abc123", "abc123");
    }

    @Test
    public void testToTextInvalidValueFail() {
        assertThrows(HeaderException.class, () -> CacheControlDirectiveExtensionHeaderHandler.INSTANCE
            .toText(this,
                CacheControlDirectiveName.MAX_STALE));
    }

    @Override
    public String typeNamePrefix() {
        return "CacheControlDirectiveExtension";
    }

    @Override
    String invalidHeader() {
        return ",";
    }

    @Override
    String handlerToString() {
        return "CacheControlDirectiveExtension";
    }

    @Override
    CacheControlDirectiveExtensionHeaderHandler handler() {
        return CacheControlDirectiveExtensionHeaderHandler.INSTANCE;
    }

    @Override
    Name name() {
        return CacheControlDirectiveName.with("extension");
    }

    @Override
    Object value() {
        return 123L;
    }

    @Override
    String valueType() {
        return this.valueType(String.class);
    }

    @Override
    public Class<CacheControlDirectiveExtensionHeaderHandler> type() {
        return CacheControlDirectiveExtensionHeaderHandler.class;
    }
}
