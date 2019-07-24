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
import walkingkooka.compare.Range;

import java.util.Optional;

public final class ContentRangeHeaderValueHandlerTest extends
        NonStringHeaderValueHandlerTestCase<ContentRangeHeaderValueHandler, ContentRange> {

    private final static String TEXT = "bytes 123-456/789";

    @Override
    public String typeNamePrefix() {
        return ContentRange.class.getSimpleName();
    }

    @Test
    public void testParseRangeHeader() {
        this.parseAndCheck(TEXT, this.contentRange());
    }

    @Test
    public void testToTextContentRange() {
        this.toTextAndCheck(this.contentRange(), TEXT);
    }

    @Test
    public void testRoundTrip() {
        this.parseAndToTextAndCheck(TEXT, this.contentRange());
    }

    private ContentRange contentRange() {
        return ContentRange.with(RangeHeaderValueUnit.BYTES,
                Optional.of(Range.greaterThanEquals(123L).and(Range.lessThanEquals(456L))),
                Optional.of(789L));
    }

    @Override
    ContentRangeHeaderValueHandler handler() {
        return ContentRangeHeaderValueHandler.INSTANCE;
    }

    @Override
    HttpHeaderName<ContentRange> name() {
        return HttpHeaderName.CONTENT_RANGE;
    }

    @Override
    String invalidHeaderValue() {
        return "http://example.com";
    }

    @Override
    ContentRange value() {
        return ContentRange.parse(TEXT);
    }

    @Override
    String valueType() {
        return this.valueType(ContentRange.class);
    }

    @Override
    String handlerToString() {
        return ContentRange.class.getSimpleName();
    }

    @Override
    public Class<ContentRangeHeaderValueHandler> type() {
        return ContentRangeHeaderValueHandler.class;
    }
}
