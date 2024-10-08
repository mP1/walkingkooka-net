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
import walkingkooka.net.http.HttpEntity;
import walkingkooka.net.http.HttpMethod;
import walkingkooka.net.http.HttpStatus;
import walkingkooka.net.http.HttpStatusCode;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class MethodNotAllowedHttpHandlerTest extends HttpHandlerTestCase2<MethodNotAllowedHttpHandler> {

    private final static HttpMethod METHOD = HttpMethod.PATCH;
    private final static HttpStatus STATUS = HttpStatusCode.OK.setMessage("OK!");
    private final static HttpEntity ENTITY = HttpEntity.EMPTY.setBodyText("Success123");

    private final static HttpHandler HANDLER = new HttpHandler() {
        @Override
        public void handle(final HttpRequest request,
                           final HttpResponse response) {
            response.setStatus(STATUS);
            response.setEntity(ENTITY);
        }
    };

    @Test
    public void testWithNullMethodFails() {
        assertThrows(NullPointerException.class, () -> MethodNotAllowedHttpHandler.with(null, HANDLER));
    }

    @Test
    public void testWithNullHandlerFails() {
        assertThrows(NullPointerException.class, () -> MethodNotAllowedHttpHandler.with(METHOD, null));
    }

    // accept...........................................................................................................

    @Test
    public void testInvalidMethod() {
        final HttpRequest request = this.request(HttpMethod.with("invalid"));
        final HttpResponse response = HttpResponses.recording();

        this.createHttpHandler()
                .handle(request, response);

        final HttpResponse expected = HttpResponses.recording();
        expected.setStatus(HttpStatusCode.METHOD_NOT_ALLOWED.setMessage("Expected PATCH got invalid"));
        expected.setEntity(HttpEntity.EMPTY);

        this.checkResponse(request, response, expected);
    }

    @Test
    public void testValidMethod() {
        final HttpRequest request = this.request(HttpMethod.PATCH);
        final HttpResponse response = HttpResponses.recording();

        this.createHttpHandler()
                .handle(
                        request,
                        response
                );

        final HttpResponse expected = HttpResponses.recording();
        expected.setStatus(STATUS);
        expected.setEntity(ENTITY);

        this.checkResponse(request, response, expected);
    }

    private void checkResponse(final HttpRequest request,
                               final HttpResponse response,
                               final HttpResponse expected) {
        this.checkEquals(
                expected.status(),
                response.status(),
                () -> "response.status " + request);
        this.checkEquals(
                expected.entity(),
                response.entity(),
                () -> "response.entities " + request
        );
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createHttpHandler(), METHOD + " " + HANDLER);
    }

    // helpers..........................................................................................................

    private MethodNotAllowedHttpHandler createHttpHandler() {
        return MethodNotAllowedHttpHandler.with(METHOD, HANDLER);
    }

    private HttpRequest request(final HttpMethod method) {
        return new FakeHttpRequest() {

            @Override
            public HttpMethod method() {
                return method;
            }

            @Override
            public String toString() {
                return this.method().toString();
            }
        };
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<MethodNotAllowedHttpHandler> type() {
        return MethodNotAllowedHttpHandler.class;
    }
}
