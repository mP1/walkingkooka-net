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
import walkingkooka.ToStringTesting;
import walkingkooka.net.Url;
import walkingkooka.net.header.HeaderException;
import walkingkooka.net.header.HttpHeaderName;
import walkingkooka.net.header.MediaType;
import walkingkooka.net.http.HttpEntity;
import walkingkooka.net.http.HttpProtocolVersion;
import walkingkooka.net.http.HttpTransport;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.test.ParseStringTesting;

public final class HttpRequestParserTest implements ClassTesting2<HttpRequestParser>,
        ParseStringTesting<HttpRequest>,
        ToStringTesting<HttpRequestParser> {

    private final static HttpTransport TRANSPORT = HttpTransport.UNSECURED;

    @Test
    public void testEmptyFails() {
        this.parseStringFails("", new IllegalArgumentException("Missing request line"));
    }

    @Test
    public void testInvalidRequestLineFails() {
        this.parseStringFails("invalid 2 3 4", new IllegalArgumentException("Request line invalid: \"invalid 2 3 4\""));
    }

    @Test
    public void testRequestLineMissingUrlFails() {
        this.parseStringFails("GET", new IllegalArgumentException("Request line invalid: \"GET\""));
    }

    @Test
    public void testRequestLineMissingVersionFails() {
        this.parseStringFails("GET /", new IllegalArgumentException("Request line invalid: \"GET /\""));
    }

    @Test
    public void testInvalidHeaderFails() {
        this.parseStringFails("GET / HTTP/1.0\r\nInvalid-Header", new IllegalArgumentException("Header missing separator/value=\"Invalid-Header\""));
    }

    @Test
    public void testInvalidHeaderFails2() {
        this.parseStringFails("GET / HTTP/1.0\r\nContent-Length:1\r\nInvalid-Header2", new IllegalArgumentException("Header missing separator/value=\"Invalid-Header2\""));
    }

    @Test
    public void testInvalidHeaderFails3() {
        this.parseStringFails("GET / HTTP/1.0\r\nContent-Length:A", new HeaderException("Failed to convert \"Content-Length\" value \"A\", message: For input string: \"A\""));
    }

    @Test
    public void testGet() {
        this.parseAndCheck("GET / HTTP/1.0\r\n\r\n",
                HttpRequests.get(TRANSPORT,
                        Url.parseRelative("/"),
                        HttpProtocolVersion.VERSION_1_0,
                        HttpEntity.EMPTY));
    }

    @Test
    public void testGetWithHeader() {
        this.parseAndCheck("GET / HTTP/1.0\r\nContent-Length:123\r\n\r\n",
                HttpRequests.get(TRANSPORT,
                        Url.parseRelative("/"),
                        HttpProtocolVersion.VERSION_1_0,
                        HttpEntity.EMPTY.addHeader(HttpHeaderName.CONTENT_LENGTH, 123L)));
    }

    @Test
    public void testGetWithHeader2() {
        this.parseAndCheck("GET /file?abc=123 HTTP/1.1\r\nContent-Length:123\r\n\r\n",
                HttpRequests.get(TRANSPORT,
                        Url.parseRelative("/file?abc=123"),
                        HttpProtocolVersion.VERSION_1_1,
                        HttpEntity.EMPTY.addHeader(HttpHeaderName.CONTENT_LENGTH, 123L)));
    }

    @Test
    public void testGetWithTwoHeaders() {
        this.parseAndCheck(
                "GET /file?abc=123 HTTP/1.1\r\nContent-Length:123\r\nContent-Type: text/plain\r\n\r\n",
                HttpRequests.get(TRANSPORT,
                        Url.parseRelative("/file?abc=123"),
                        HttpProtocolVersion.VERSION_1_1,
                        HttpEntity.EMPTY.addHeader(HttpHeaderName.CONTENT_LENGTH, 123L)
                                .setContentType(MediaType.TEXT_PLAIN)
                )
        );
    }

    @Test
    public void testPost() {
        this.parseAndCheck("POST / HTTP/1.0\r\nContent-Length:123\r\n\r\nBody123",
                HttpRequests.post(TRANSPORT,
                        Url.parseRelative("/"),
                        HttpProtocolVersion.VERSION_1_0,
                        HttpEntity.EMPTY.addHeader(HttpHeaderName.CONTENT_LENGTH, 123L)
                                .setBodyText("Body123")));
    }

    @Test
    public void testPostWithMultipleHeaders() {
        this.parseAndCheck(
                "POST / HTTP/1.0\r\nContent-Length:123\r\nContent-Type: text/plain\r\n\r\nBody123",
                HttpRequests.post(TRANSPORT,
                        Url.parseRelative("/"),
                        HttpProtocolVersion.VERSION_1_0,
                        HttpEntity.EMPTY.addHeader(HttpHeaderName.CONTENT_LENGTH, 123L)
                                .setContentType(MediaType.TEXT_PLAIN)
                                .setBodyText("Body123")
                )
        );
    }

    @Test
    public void testBodyIncludesCr() {
        this.parseAndCheck(
                "POST / HTTP/1.0\r\nContent-Length:123\r\nContent-Type: text/plain\r\n\r\nBody\r123",
                HttpRequests.post(TRANSPORT,
                        Url.parseRelative("/"),
                        HttpProtocolVersion.VERSION_1_0,
                        HttpEntity.EMPTY.addHeader(HttpHeaderName.CONTENT_LENGTH, 123L)
                                .setContentType(MediaType.TEXT_PLAIN)
                                .setBodyText("Body\r123")
                )
        );
    }

    @Test
    public void testBodyCr() {
        this.parseAndCheck(
                "POST / HTTP/1.0\r\nContent-Length:123\r\nContent-Type: text/plain\r\n\r\n\r",
                HttpRequests.post(TRANSPORT,
                        Url.parseRelative("/"),
                        HttpProtocolVersion.VERSION_1_0,
                        HttpEntity.EMPTY.addHeader(HttpHeaderName.CONTENT_LENGTH, 123L)
                                .setContentType(MediaType.TEXT_PLAIN)
                                .setBodyText("\r")
                )
        );
    }

    private void parseAndCheck(final String text,
                               final HttpRequest expected) {
        this.checkEquals(expected, this.parseString(text));
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<HttpRequestParser> type() {
        return HttpRequestParser.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }

    // ParseStringTesting...............................................................................................

    @Override
    public HttpRequest parseString(final String text) {
        return HttpRequestParser.parse(TRANSPORT, text);
    }

    @Override
    public Class<? extends RuntimeException> parseStringFailedExpected(final Class<? extends RuntimeException> expected) {
        return expected;
    }

    @Override
    public RuntimeException parseStringFailedExpected(final RuntimeException expected) {
        return expected;
    }
}
