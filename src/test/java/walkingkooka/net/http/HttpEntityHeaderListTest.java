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

package walkingkooka.net.http;

import org.junit.jupiter.api.Test;
import walkingkooka.net.header.AcceptLanguage;
import walkingkooka.net.header.HttpHeaderName;
import walkingkooka.net.header.MediaType;

public final class HttpEntityHeaderListTest extends HttpEntityHeaderListTestCase<HttpEntityHeaderList> {

    @Test
    public void testOneNonMultiHeader() {
        final HttpHeaderName<MediaType> contentType = HttpHeaderName.CONTENT_TYPE;
        this.checkEquals(false, contentType.isMultiple());
        this.checkEquals(HttpEntityHeaderListOne.class, HttpEntityHeaderList.one(contentType, MediaType.TEXT_PLAIN).getClass());
    }

    @Test
    public void testOneMultiHeader() {
        final HttpHeaderName<AcceptLanguage> acceptLanguage = HttpHeaderName.ACCEPT_LANGUAGE;
        this.checkEquals(true, acceptLanguage.isMultiple());
        this.checkEquals(HttpEntityHeaderListMulti.class, HttpEntityHeaderList.one(acceptLanguage, AcceptLanguage.parse("EN")).getClass());
    }

    @Override
    public Class<HttpEntityHeaderList> type() {
        return HttpEntityHeaderList.class;
    }
}
