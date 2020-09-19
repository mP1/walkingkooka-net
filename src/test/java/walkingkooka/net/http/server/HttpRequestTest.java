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

package walkingkooka.net.http.server;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.map.Maps;
import walkingkooka.net.RelativeUrl;
import walkingkooka.net.Url;
import walkingkooka.net.header.HttpHeaderName;
import walkingkooka.net.header.MediaType;
import walkingkooka.net.http.HttpEntity;
import walkingkooka.net.http.HttpMethod;
import walkingkooka.net.http.HttpProtocolVersion;
import walkingkooka.net.http.HttpTransport;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallingTesting;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class HttpRequestTest implements ClassTesting<HttpRequest>, JsonNodeMarshallingTesting<HttpRequest> {

    @Test
    public void testBodyTextWhenBodyNullAndCharsetNull() {
        this.bodyTextAndCheck(null,
                null,
                "");
    }

    @Test
    public void testBodyTextWhenBodyNull() {
        this.bodyTextAndCheck("text/plain;charset=UTF-8",
                null,
                "");
    }

    @Test
    public void testBodyTextCharsetHeaderAbsent() {
        final Charset charset = HttpEntity.DEFAULT_BODY_CHARSET;
        final String text = "ABC123";

        this.bodyTextAndCheck(null,
                text.getBytes(charset),
                text);
    }

    @Test
    public void testBodyTextCharsetHeaderUnsupportedFails() {
        assertThrows(IllegalArgumentException.class,
                () -> this.request("text/plain;charset=UTF-99", new byte[1])
                        .bodyText());
    }

    @Test
    public void testBodyTextCharsetHeaderPresent() {
        final Charset charset = StandardCharsets.UTF_16;
        final String text = "ABC123";

        this.bodyTextAndCheck("text/plain;charset=" + charset.name(),
                text.getBytes(charset),
                text);
    }

    private void bodyTextAndCheck(final String contentType,
                                  final byte[] body,
                                  final String expected) {
        assertEquals(expected,
                this.request(contentType, body).bodyText());
    }

    @Test
    public void testBodyLength() {
        assertEquals(123L, this.request("text/plain", new byte[123]));
    }

    private HttpRequest request(final String contentType,
                                final byte[] body) {
        return new HttpRequest() {
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

            @Override
            public HttpTransport transport() {
                throw new UnsupportedOperationException();
            }

            @Override
            public HttpProtocolVersion protocolVersion() {
                throw new UnsupportedOperationException();
            }

            @Override
            public RelativeUrl url() {
                throw new UnsupportedOperationException();
            }

            @Override
            public HttpMethod method() {
                throw new UnsupportedOperationException();
            }

            @Override
            public Map<HttpRequestParameterName, List<String>> parameters() {
                throw new UnsupportedOperationException();
            }

            @Override
            public List<String> parameterValues(final HttpRequestParameterName parameterName) {
                throw new UnsupportedOperationException();
            }
        };
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<HttpRequest> type() {
        return HttpRequest.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }

    // JsonNodeMarshallingTesting.......................................................................................

    @Override
    public HttpRequest createJsonNodeMappingValue() {
        return HttpRequests.value(HttpMethod.POST,
                HttpTransport.SECURED,
                Url.parseRelative("/path1/file2?query3"),
                HttpProtocolVersion.VERSION_1_0,
                HttpEntity.EMPTY
                        .addHeader(HttpHeaderName.CONTENT_LENGTH, 123L)
                        .addHeader(HttpHeaderName.CONTENT_TYPE, MediaType.TEXT_PLAIN)
                        .setBodyText("body-text-123"));
    }

    @Override
    public final HttpRequest unmarshall(final JsonNode from,
                                         final JsonNodeUnmarshallContext context) {
        return HttpRequests.parse(HttpTransport.SECURED, from.stringValueOrFail());
    }
}
