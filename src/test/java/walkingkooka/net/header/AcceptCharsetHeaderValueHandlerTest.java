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
import walkingkooka.net.email.EmailAddress;

import java.nio.charset.Charset;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class AcceptCharsetHeaderValueHandlerTest extends
        NonStringHeaderValueHandlerTestCase<AcceptCharsetHeaderValueHandler, AcceptCharset> {

    @Override
    public String typeNamePrefix() {
        return AcceptCharset.class.getSimpleName();
    }

    @Test
    public void testContentType() {
        final String charset = Charset.forName("utf8").name();
        this.parseAndToTextAndCheck2(charset,
                CharsetHeaderValue.with(CharsetName.with(charset)));
    }

    @Test
    public void testUnknownCharset() {
        final String charset = "utf-1";
        final CharsetName charsetName = CharsetName.with(charset);
        assertEquals(CharsetName.NO_CHARSET,
                charsetName.charset(),
                "charsetName must have no charset");
        this.parseAndToTextAndCheck2(charset,
                CharsetHeaderValue.with(CharsetName.with(charset)));
    }

    private void parseAndToTextAndCheck2(final String text, final CharsetHeaderValue... values) {
        this.parseAndToTextAndCheck(text, AcceptCharset.with(Lists.of(values)));
    }

    @Override
    AcceptCharsetHeaderValueHandler handler() {
        return AcceptCharsetHeaderValueHandler.INSTANCE;
    }

    @Override
    HttpHeaderName<EmailAddress> name() {
        return HttpHeaderName.FROM;
    }

    @Override
    String invalidHeaderValue() {
        return "\0";
    }

    @Override
    AcceptCharset value() {
        return AcceptCharset.with(Lists.of(CharsetHeaderValue.with(CharsetName.UTF_8)));
    }

    @Override
    String valueType() {
        return this.valueType(AcceptCharset.class);
    }

    @Override
    String handlerToString() {
        return AcceptCharset.class.getSimpleName();
    }

    @Override
    public Class<AcceptCharsetHeaderValueHandler> type() {
        return AcceptCharsetHeaderValueHandler.class;
    }
}
