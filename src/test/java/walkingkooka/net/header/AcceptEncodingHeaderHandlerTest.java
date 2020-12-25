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
import walkingkooka.collect.map.Maps;
import walkingkooka.naming.Name;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class AcceptEncodingHeaderHandlerTest extends NonStringHeaderHandlerTestCase<AcceptEncodingHeaderHandler, AcceptEncoding> {

    @Test
    public void testParseToken() {
        this.parseStringAndCheck2("gzip",
                AcceptEncodingValue.GZIP);
    }

    @Test
    public void testParseTokenToken() {
        this.parseStringAndCheck2("gzip; q=0.5, *",
                AcceptEncodingValue.WILDCARD_ENCODING,
                AcceptEncodingValue.GZIP.setParameters(Maps.of(AcceptEncodingValueParameterName.Q, 0.5f)));
    }

    private void parseStringAndCheck2(final String text, final AcceptEncodingValue... encodings) {
        this.parseStringAndCheck(text, AcceptEncoding.with(Lists.of(encodings)));
    }

    @Test
    public void testCheckEmptyListFails() {
        assertThrows(HeaderException.class, () -> AcceptEncodingHeaderHandler.INSTANCE.check(Lists.empty(), HttpHeaderName.ACCEPT_ENCODING));
    }

    @Test
    public void testToText() {
        this.toTextAndCheck(acceptEncoding(AcceptEncodingValue.WILDCARD_ENCODING),
                "*");
    }

    @Test
    public void testToText2() {
        this.toTextAndCheck(acceptEncoding(AcceptEncodingValue.GZIP),
                "gzip");
    }

    @Test
    public void testToText3() {
        this.toTextAndCheck(acceptEncoding(AcceptEncodingValue.WILDCARD_ENCODING, AcceptEncodingValue.GZIP),
                "*, gzip");
    }

    @Test
    public void testToTextWithParameters() {
        this.toTextAndCheck(acceptEncoding(AcceptEncodingValue.with("abc").setParameters(Maps.of(AcceptEncodingValueParameterName.Q, 0.5f))),
                "abc; q=0.5");
    }

    @Override
    Name name() {
        return HttpHeaderName.ACCEPT_ENCODING;
    }

    @Override
    String invalidHeader() {
        return "\0invalid";
    }

    @Override
    String handlerToString() {
        return AcceptEncoding.class.getSimpleName();
    }

    @Override
    AcceptEncodingHeaderHandler handler() {
        return AcceptEncodingHeaderHandler.INSTANCE;
    }

    @Override
    AcceptEncoding value() {
        return acceptEncoding(AcceptEncodingValue.GZIP, AcceptEncodingValue.DEFLATE);
    }

    private static AcceptEncoding acceptEncoding(final AcceptEncodingValue...encodings) {
        return AcceptEncoding.with(Lists.of(encodings));
    }

    @Override
    String valueType() {
        return this.valueType(AcceptEncoding.class);
    }

    @Override
    public String typeNamePrefix() {
        return AcceptEncoding.class.getSimpleName();
    }

    @Override
    public Class<AcceptEncodingHeaderHandler> type() {
        return AcceptEncodingHeaderHandler.class;
    }
}
