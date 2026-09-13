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
import walkingkooka.net.RelativeUrl;
import walkingkooka.net.Url;

public final class HeaderHandlerNonStringRelativeUrlTest extends
    HeaderHandlerNonStringTestCase<HeaderHandlerNonStringRelativeUrl, RelativeUrl> {

    @Test
    public void testContentLocation() {
        final String url = "/relative/url/file.html";
        this.parseAndToTextAndCheck(url, Url.parseRelative(url));
    }

    @Override
    HeaderHandlerNonStringRelativeUrl handler() {
        return HeaderHandlerNonStringRelativeUrl.INSTANCE;
    }

    @Override
    HttpHeaderName<RelativeUrl> name() {
        return HttpHeaderName.CONTENT_LOCATION;
    }

    @Override
    String invalidHeader() {
        return "https://example.com";
    }

    @Override
    RelativeUrl value() {
        return Url.parseRelative("/file?p1=v1");
    }

    @Override
    String valueType() {
        return this.valueType(RelativeUrl.class);
    }

    @Override
    String handlerToString() {
        return RelativeUrl.class.getSimpleName();
    }

    // class............................................................................................................

    @Override
    public Class<HeaderHandlerNonStringRelativeUrl> type() {
        return HeaderHandlerNonStringRelativeUrl.class;
    }

    @Override
    public String typeNamePrefix() {
        return HeaderHandlerNonString.class.getSimpleName();
    }

    @Override
    public String typeNameSuffix() {
        return RelativeUrl.class.getSimpleName();
    }
}
