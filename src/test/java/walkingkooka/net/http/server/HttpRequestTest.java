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
import walkingkooka.collect.map.Maps;
import walkingkooka.net.RelativeUrl;
import walkingkooka.net.header.HttpHeaderName;
import walkingkooka.net.header.MediaType;
import walkingkooka.net.http.HttpMethod;
import walkingkooka.net.http.HttpProtocolVersion;
import walkingkooka.net.http.HttpTransport;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class HttpRequestTest implements ClassTesting<HttpRequest> {
    @Test
    public void testBodyTextCharsetHeaderAbsent() {
        final Charset charset = HttpRequest.DEFAULT_BODY_CHARSET;
        final String text = "ABC123";

        this.bodyTextAndCheck(null,
                text.getBytes(charset),
                text);
    }

    @Test
    public void testBodyTextCharsetHeaderPresent() {
        final Charset charset = Charset.forName("UTF-16");
        final String text = "ABC123";

        this.bodyTextAndCheck("text/plain;charset=" + charset.name(),
                text.getBytes(charset),
                text);
    }

    private void bodyTextAndCheck(final String contentType,
                                  final byte[] body,
                                  final String expected) {
        assertEquals(expected,
                new HttpRequest() {
                    @Override
                    public Map<HttpHeaderName<?>, Object> headers() {
                        return null == contentType ?
                                Maps.empty() :
                                Maps.of(HttpHeaderName.CONTENT_TYPE, MediaType.parse(contentType));
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
                }.bodyText());
    }

    @Override
    public Class<HttpRequest> type() {
        return HttpRequest.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}