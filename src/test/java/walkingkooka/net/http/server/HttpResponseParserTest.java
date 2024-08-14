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
import walkingkooka.net.header.HeaderException;
import walkingkooka.net.header.HttpHeaderName;
import walkingkooka.net.header.MediaType;
import walkingkooka.net.http.HttpEntity;
import walkingkooka.net.http.HttpProtocolVersion;
import walkingkooka.net.http.HttpStatusCode;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.test.ParseStringTesting;

public final class HttpResponseParserTest implements ClassTesting2<HttpResponseParser>,
        ParseStringTesting<HttpResponse>,
        ToStringTesting<HttpResponseParser> {

    @Test
    public void testEmptyFails() {
        this.parseStringFails("", new IllegalArgumentException("Missing version and status"));
    }

    @Test
    public void testInvalidProtocolFails() {
        this.parseStringFails("invalid 2 3 4", new IllegalArgumentException("Unknown protocol=\"invalid\""));
    }

    @Test
    public void testInvalidStatusCodeFails() {
        this.parseStringFails("HTTP/1.0 INVALID OK", new IllegalArgumentException("Invalid status code \"HTTP/1.0 INVALID OK\""));
    }

    @Test
    public void testMissingStatusMessageFails() {
        this.parseStringFails("HTTP/1.0 200", new IllegalArgumentException("Invalid status \"HTTP/1.0 200\""));
    }

    @Test
    public void testInvalidHeaderFails() {
        this.parseStringFails(
                "HTTP/1.0 200 OK\r\nContent-Length:A",
                new HeaderException("Failed to convert \"Content-Length\" value \"A\", message: Invalid number in \"A\"")
        );
    }

    @Test
    public void testWithoutHeadersVersion10() {
        final HttpResponse response = HttpResponses.recording();
        response.setVersion(HttpProtocolVersion.VERSION_1_0);
        response.setStatus(HttpStatusCode.OK.status());

        this.parseAndCheck("HTTP/1.0 200 OK\r\n\r\n", response);
    }

    @Test
    public void testWithoutHeadersVersion11() {
        final HttpResponse response = HttpResponses.recording();
        response.setVersion(HttpProtocolVersion.VERSION_1_1);
        response.setStatus(HttpStatusCode.OK.status());

        this.parseAndCheck("HTTP/1.1 200 OK\r\n\r\n", response);
    }

    @Test
    public void testWithoutHeaders() {
        final HttpResponse response = HttpResponses.recording();

        response.setVersion(HttpProtocolVersion.VERSION_1_0);
        response.setStatus(HttpStatusCode.CREATED.setMessage("Something Created"));

        this.parseAndCheck("HTTP/1.0 201 Something Created\r\n\r\n", response);
    }

    @Test
    public void testWithHeader() {
        final HttpResponse response = HttpResponses.recording();

        response.setVersion(HttpProtocolVersion.VERSION_1_0);
        response.setStatus(HttpStatusCode.withCode(299).setMessage("Custom Message"));
        response.setEntity(HttpEntity.EMPTY.addHeader(HttpHeaderName.CONTENT_LENGTH, 123L));

        this.parseAndCheck("HTTP/1.0 299 Custom Message\r\nContent-Length: 123\r\n\r\n", response);
    }

    @Test
    public void testWithTwoHeaders() {
        final HttpResponse response = HttpResponses.recording();

        response.setVersion(HttpProtocolVersion.VERSION_1_0);
        response.setStatus(HttpStatusCode.withCode(299).setMessage("Custom Message"));
        response.setEntity(
                HttpEntity.EMPTY.addHeader(HttpHeaderName.CONTENT_LENGTH, 123L)
                        .setContentType(MediaType.TEXT_PLAIN)
        );

        this.parseAndCheck("HTTP/1.0 299 Custom Message\r\nContent-Length: 123\r\nContent-Type: text/plain\r\n\r\n", response);
    }

    @Test
    public void testOnlyBody() {
        final HttpResponse response = HttpResponses.recording();

        response.setVersion(HttpProtocolVersion.VERSION_1_0);
        response.setStatus(HttpStatusCode.withCode(299).setMessage("Custom Message"));
        response.setEntity(HttpEntity.EMPTY.setBodyText("Body123"));

        this.parseAndCheck("HTTP/1.0 299 Custom Message\r\n\r\nBody123", response);
    }

    @Test
    public void testHeadersAndBody() {
        final HttpResponse response = HttpResponses.recording();

        response.setVersion(HttpProtocolVersion.VERSION_1_0);
        response.setStatus(HttpStatusCode.withCode(299).setMessage("Custom Message"));
        response.setEntity(HttpEntity.EMPTY.addHeader(HttpHeaderName.CONTENT_LENGTH, 123L).setBodyText("Body123"));

        this.parseAndCheck("HTTP/1.0 299 Custom Message\r\nContent-Length: 123\r\n\r\nBody123", response);
    }

    @Test
    public void testBodyIncludesCr() {
        final HttpResponse response = HttpResponses.recording();

        response.setVersion(HttpProtocolVersion.VERSION_1_0);
        response.setStatus(HttpStatusCode.withCode(299).setMessage("Custom Message"));
        response.setEntity(HttpEntity.EMPTY.addHeader(HttpHeaderName.CONTENT_LENGTH, 123L).setBodyText("Body\r123"));

        this.parseAndCheck("HTTP/1.0 299 Custom Message\r\nContent-Length: 123\r\n\r\nBody\r123", response);
    }

    private void parseAndCheck(final String text,
                               final HttpResponse expected) {
        this.checkEquals(expected, this.parseString(text));
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<HttpResponseParser> type() {
        return HttpResponseParser.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }

    // ParseStringTesting...............................................................................................

    @Override
    public HttpResponse parseString(final String text) {
        return HttpResponseParser.parse(text);
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
