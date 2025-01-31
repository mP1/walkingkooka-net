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
import walkingkooka.net.email.EmailAddress;

import java.nio.charset.StandardCharsets;

public final class CharsetNameHeaderHandlerTest extends
    NonStringHeaderHandlerTestCase<CharsetNameHeaderHandler, CharsetName> {

    @Override
    public String typeNamePrefix() {
        return CharsetName.class.getSimpleName();
    }

    @Test
    public void testContent() {
        final String charset = StandardCharsets.UTF_8.name();
        this.parseAndToTextAndCheck(charset,
            CharsetName.with(charset));
    }

    @Test
    public void testUnknownCharset() {
        final String charset = "utf-1";
        final CharsetName charsetName = CharsetName.with(charset);
        this.checkEquals(CharsetName.NO_CHARSET,
            charsetName.charset(),
            "charsetName must have no charset");
        this.parseAndToTextAndCheck(charset,
            charsetName);
    }

    @Override
    CharsetNameHeaderHandler handler() {
        return CharsetNameHeaderHandler.INSTANCE;
    }

    @Override
    HttpHeaderName<EmailAddress> name() {
        return HttpHeaderName.FROM;
    }

    @Override
    String invalidHeader() {
        return "\0";
    }

    @Override
    CharsetName value() {
        return CharsetName.with("utf-8");
    }

    @Override
    String valueType() {
        return this.valueType(CharsetName.class);
    }

    @Override
    String handlerToString() {
        return CharsetName.class.getSimpleName();
    }

    @Override
    public Class<CharsetNameHeaderHandler> type() {
        return CharsetNameHeaderHandler.class;
    }
}
