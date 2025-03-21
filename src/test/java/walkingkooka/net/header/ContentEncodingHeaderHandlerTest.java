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
import walkingkooka.collect.list.Lists;
import walkingkooka.naming.Name;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class ContentEncodingHeaderHandlerTest extends NonStringHeaderHandlerTestCase<ContentEncodingHeaderHandler, ContentEncoding> {

    @Test
    public void testParse() {
        this.parseStringAndCheck2("gzip",
            Encoding.GZIP);
    }

    @Test
    public void testParse2() {
        this.parseStringAndCheck2("GZIP",
            Encoding.GZIP);
    }

    @Test
    public void testParseCommaSeparated() {
        this.parseStringAndCheck2("gzip,deflate",
            Encoding.GZIP,
            Encoding.DEFLATE);
    }

    @Test
    public void testParseWhitespaceCommaSeparated() {
        this.parseStringAndCheck2("gzip, deflate,  br",
            Encoding.GZIP,
            Encoding.DEFLATE,
            Encoding.BR);
    }

    private void parseStringAndCheck2(final String text, final Encoding... encodings) {
        this.parseStringAndCheck(text, ContentEncoding.with(Lists.of(encodings)));
    }

    @Test
    public void testCheckEmptyListFails() {
        assertThrows(HeaderException.class, () -> ContentEncodingHeaderHandler.INSTANCE.check(Lists.empty()));
    }

    @Test
    public void testToText() {
        this.toTextAndCheck(ContentEncoding.with(Lists.of(Encoding.BR)),
            "br");
    }

    @Test
    public void testToText2() {
        this.toTextAndCheck(ContentEncoding.with(Lists.of(Encoding.BR, Encoding.GZIP)),
            "br, gzip");
    }

    @Override
    Name name() {
        return HttpHeaderName.CONTENT_ENCODING;
    }

    @Override
    String invalidHeader() {
        return "\0invalid";
    }

    @Override
    String handlerToString() {
        return ContentEncoding.class.getSimpleName();
    }

    @Override
    ContentEncodingHeaderHandler handler() {
        return ContentEncodingHeaderHandler.INSTANCE;
    }

    @Override
    ContentEncoding value() {
        return ContentEncoding.with(Lists.of(Encoding.GZIP, Encoding.DEFLATE));
    }

    @Override
    String valueType() {
        return this.valueType(ContentEncoding.class);
    }

    @Override
    public String typeNamePrefix() {
        return ContentEncoding.class.getSimpleName();
    }

    @Override
    public Class<ContentEncodingHeaderHandler> type() {
        return ContentEncodingHeaderHandler.class;
    }
}
