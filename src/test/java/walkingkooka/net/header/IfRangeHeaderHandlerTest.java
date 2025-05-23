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

public final class IfRangeHeaderHandlerTest extends
    NonStringHeaderHandlerTestCase<IfRangeHeaderHandler, IfRange<?>> {

    @Override
    public String typeNamePrefix() {
        return IfRange.class.getSimpleName();
    }

    @Test
    public void testIfRange() {
        final String header = this.etag().toHeaderText();
        this.parseAndToTextAndCheck(header, IfRange.with(this.etag()));
    }

    @Override
    IfRangeHeaderHandler handler() {
        return IfRangeHeaderHandler.INSTANCE;
    }

    @Override
    HttpHeaderName<IfRange<?>> name() {
        return HttpHeaderName.IF_RANGE;
    }

    @Override
    String invalidHeader() {
        return "///";
    }

    @Override
    IfRange<?> value() {
        return IfRange.with(this.etag());
    }

    private ETag etag() {
        return ETag.with("abc123", ETagValidator.WEAK);
    }

    @Override
    String valueType() {
        return this.valueType(IfRange.class);
    }

    @Override
    String handlerToString() {
        return "IfRange";
    }

    @Override
    public Class<IfRangeHeaderHandler> type() {
        return IfRangeHeaderHandler.class;
    }
}
