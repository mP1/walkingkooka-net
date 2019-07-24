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

public final class LongHeaderValueHandlerTest extends
        NonStringHeaderValueHandlerTestCase<LongHeaderValueHandler, Long> {

    private final static String TEXT = "123";
    private final static Long VALUE = 123L;

    @Override
    public String typeNamePrefix() {
        return Long.class.getSimpleName();
    }

    @Test
    public void testContentLength() {
        this.parseAndToTextAndCheck(TEXT, VALUE);
    }

    @Override
    LongHeaderValueHandler handler() {
        return LongHeaderValueHandler.INSTANCE;
    }

    @Override
    HttpHeaderName<Long> name() {
        return HttpHeaderName.CONTENT_LENGTH;
    }

    @Override
    String invalidHeaderValue() {
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

    @Override
    public Class<LongHeaderValueHandler> type() {
        return LongHeaderValueHandler.class;
    }
}
