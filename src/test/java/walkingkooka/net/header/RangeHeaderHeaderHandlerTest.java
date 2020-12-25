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
import walkingkooka.collect.Range;
import walkingkooka.collect.list.Lists;

public final class RangeHeaderHeaderHandlerTest extends
        NonStringHeaderHandlerTestCase<RangeHeaderHeaderHandler, RangeHeader> {

    private final static String TEXT = "bytes=123-456, 789-";

    @Override
    public String typeNamePrefix() {
        return RangeHeader.class.getSimpleName();
    }

    @Test
    public void testParseRangeHeader() {
        this.parseStringAndCheck(TEXT, this.range());
    }

    @Test
    public void testToTextContentRange() {
        this.toTextAndCheck(this.range(), TEXT);
    }

    private RangeHeader range() {
        return RangeHeader.with(RangeHeaderUnit.BYTES,
                Lists.of(Range.greaterThanEquals(123L).and(Range.lessThanEquals(456L)),
                        Range.greaterThanEquals(789L)));
    }

    @Override
    RangeHeaderHeaderHandler handler() {
        return RangeHeaderHeaderHandler.INSTANCE;
    }

    @Override
    HttpHeaderName<RangeHeader> name() {
        return HttpHeaderName.RANGE;
    }

    @Override
    String invalidHeader() {
        return "http://example.com";
    }

    @Override
    RangeHeader value() {
        return RangeHeader.parse(TEXT);
    }

    @Override
    String valueType() {
        return this.valueType(RangeHeader.class);
    }

    @Override
    String handlerToString() {
        return RangeHeader.class.getSimpleName();
    }

    @Override
    public Class<RangeHeaderHeaderHandler> type() {
        return RangeHeaderHeaderHandler.class;
    }
}
