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
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.map.Maps;
import walkingkooka.net.header.HttpHeaderName;
import walkingkooka.net.header.MediaType;
import walkingkooka.net.http.server.FakeHttpRequest;
import walkingkooka.net.http.server.HttpRequest;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class HasHeadersTest implements ClassTesting2<HasHeaders> {

    // Charset..........................................................................................................

    @Test
    public void testCharsetNullFails() {
        assertThrows(NullPointerException.class, () -> this.request("text/plain", new byte[0]).charset(null));
    }

    @Test
    public void testCharsetContentTypeMissing() {
        this.charsetAndCheck(null, StandardCharsets.UTF_16, StandardCharsets.UTF_16);
    }

    @Test
    public void testCharsetContentTypeCharset() {
        this.charsetAndCheck("text/plain;charset=UTF-8", StandardCharsets.UTF_16, StandardCharsets.UTF_8);
    }

    @Test
    public void testCharsetContentTypeDefaults() {
        this.charsetAndCheck("text/plain;", StandardCharsets.UTF_16, StandardCharsets.UTF_16);
    }

    private void charsetAndCheck(final String contentType,
                                 final Charset defaultCharset,
                                 final Charset expected) {
        this.checkEquals(expected,
            this.request(contentType, new byte[0]).charset(defaultCharset),
            () -> "contentType: " + contentType + " default: " + defaultCharset);
    }

    // helper...........................................................................................................

    private HttpRequest request(final String contentType,
                                final byte[] body) {
        return new FakeHttpRequest() {
            @Override
            public Map<HttpHeaderName<?>, List<?>> headers() {
                return null == contentType ?
                    Maps.empty() :
                    Maps.of(HttpHeaderName.CONTENT_TYPE, Lists.of(MediaType.parse(contentType)));
            }

            @Override
            public byte[] body() {
                return body;
            }
        };
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<HasHeaders> type() {
        return HasHeaders.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
