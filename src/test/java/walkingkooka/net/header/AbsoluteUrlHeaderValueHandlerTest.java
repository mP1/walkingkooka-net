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
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.Url;

public final class AbsoluteUrlHeaderValueHandlerTest extends
        NonStringHeaderValueHandlerTestCase<AbsoluteUrlHeaderValueHandler, AbsoluteUrl> {

    private final static String URL = "http://example.com";

    @Override
    public String typeNamePrefix() {
        return AbsoluteUrl.class.getSimpleName();
    }

    @Test
    public void testReferer() {
        this.parseAndToTextAndCheck(URL, Url.parseAbsolute(URL));
    }

    @Override
    AbsoluteUrlHeaderValueHandler handler() {
        return AbsoluteUrlHeaderValueHandler.INSTANCE;
    }

    @Override
    HttpHeaderName<AbsoluteUrl> name() {
        return HttpHeaderName.REFERER;
    }

    @Override
    String invalidHeaderValue() {
        return "/relative/url/must/fail";
    }

    @Override
    AbsoluteUrl value() {
        return Url.parseAbsolute(URL);
    }

    @Override
    String valueType() {
        return this.valueType(AbsoluteUrl.class);
    }

    @Override
    String handlerToString() {
        return AbsoluteUrl.class.getSimpleName();
    }

    @Override
    public Class<AbsoluteUrlHeaderValueHandler> type() {
        return AbsoluteUrlHeaderValueHandler.class;
    }


}
