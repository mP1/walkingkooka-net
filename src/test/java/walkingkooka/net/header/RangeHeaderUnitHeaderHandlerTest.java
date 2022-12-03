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

public final class RangeHeaderUnitHeaderHandlerTest extends
        NonStringHeaderHandlerTestCase<RangeHeaderUnitHeaderHandler, RangeHeaderUnit> {

    private final static String TEXT = "bytes";

    @Override
    public String typeNamePrefix() {
        return RangeHeaderUnit.class.getSimpleName();
    }

    @Test
    public void testParseRangeHeader() {
        this.parseStringAndCheck(TEXT, this.range());
    }

    @Test
    public void testToTextContentRange() {
        this.toTextAndCheck(this.range(), TEXT);
    }

    private RangeHeaderUnit range() {
        return RangeHeaderUnit.BYTES;
    }

    @Override
    RangeHeaderUnitHeaderHandler handler() {
        return RangeHeaderUnitHeaderHandler.INSTANCE;
    }

    @Override
    HttpHeaderName<RangeHeaderUnit> name() {
        return HttpHeaderName.ACCEPT_RANGES;
    }

    @Override
    String invalidHeader() {
        return "https://example.com";
    }

    @Override
    RangeHeaderUnit value() {
        return RangeHeaderUnit.parse(TEXT);
    }

    @Override
    String valueType() {
        return this.valueType(RangeHeaderUnit.class);
    }

    @Override
    String handlerToString() {
        return RangeHeaderUnit.class.getSimpleName();
    }

    @Override
    public Class<RangeHeaderUnitHeaderHandler> type() {
        return RangeHeaderUnitHeaderHandler.class;
    }
}
