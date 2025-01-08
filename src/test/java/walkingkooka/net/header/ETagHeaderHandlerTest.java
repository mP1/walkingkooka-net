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

public final class ETagHeaderHandlerTest extends
    NonStringHeaderHandlerTestCase<ETagHeaderHandler, ETag> {

    @Override
    public String typeNamePrefix() {
        return ETag.class.getSimpleName();
    }

    @Test
    public void testParse() {
        this.parseStringAndCheck("W/\"123\"", ETag.with("123", ETagValidator.WEAK));
    }

    @Test
    public void testToText() {
        this.toTextAndCheck(ETag.with("123", ETagValidator.WEAK), "W/\"123\"");
    }

    @Override
    ETagHeaderHandler handler() {
        return ETagHeaderHandler.INSTANCE;
    }

    @Override
    HttpHeaderName<ETag> name() {
        return HttpHeaderName.E_TAG;
    }

    @Override
    String invalidHeader() {
        return "I/";
    }

    @Override
    ETag value() {
        return ETag.with("01234567890", ETagValidator.WEAK);
    }

    @Override
    String valueType() {
        return this.valueType(ETag.class);
    }

    @Override
    String handlerToString() {
        return ETag.class.getSimpleName();
    }

    @Override
    public Class<ETagHeaderHandler> type() {
        return ETagHeaderHandler.class;
    }
}
