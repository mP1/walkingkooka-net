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
import walkingkooka.net.header.HttpHeaderName;
import walkingkooka.net.header.MediaType;
import walkingkooka.net.http.HttpEntity;
import walkingkooka.net.http.HttpStatus;
import walkingkooka.net.http.HttpStatusCode;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class ContentTypeHttpHandlerTest extends HttpHandlerTestCase2<ContentTypeHttpHandler> {

    private final static MediaType CONTENT_TYPE = MediaType.TEXT_PLAIN;
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
    public void testWithNullContentTypeFails() {
        assertThrows(NullPointerException.class, () -> ContentTypeHttpHandler.with(null, HANDLER));
    }

    @Test
    public void testWithNullHandlerFails() {
        assertThrows(NullPointerException.class, () -> ContentTypeHttpHandler.with(CONTENT_TYPE, null));
    }

    // accept...........................................................................................................

    @Test
    public void testMissingContentType() {
        final HttpRequest request = this.request();
        final HttpResponse response = HttpResponses.recording();

        this.createHttpHandler()
                .handle(
                        request,
                        response
                );

        final HttpResponse expected = HttpResponses.recording();
        expected.setStatus(HttpStatusCode.BAD_REQUEST.setMessage("Expected text/plain missing " + HttpHeaderName.CONTENT_TYPE));
        expected.setEntity(HttpEntity.EMPTY);

        this.checkResponse(request, response, expected);
    }

    @Test
    public void testInvalidContentType() {
        final HttpRequest request = this.request(MediaType.BINARY);
        final HttpResponse response = HttpResponses.recording();

        this.createHttpHandler()
                .handle(
                        request,
                        response
                );

        final HttpResponse expected = HttpResponses.recording();
        expected.setStatus(HttpStatusCode.BAD_REQUEST.setMessage("Expected text/plain got application/octet-stream"));
        expected.setEntity(HttpEntity.EMPTY);

        this.checkResponse(request, response, expected);
    }

    @Test
    public void testValidContentType() {
        final HttpRequest request = this.request(CONTENT_TYPE);
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
                () -> "response.status " + request
        );
        this.checkEquals(
                expected.entity(),
                response.entity(),
                () -> "response.entity " + request
        );
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createHttpHandler(), CONTENT_TYPE + " " + HANDLER);
    }

    // helpers..........................................................................................................

    private ContentTypeHttpHandler createHttpHandler() {
        return ContentTypeHttpHandler.with(CONTENT_TYPE, HANDLER);
    }

    private HttpRequest request(final MediaType... contentType) {
        return new FakeHttpRequest() {

            @Override
            public Map<HttpHeaderName<?>, List<?>> headers() {
                return Maps.of(HttpHeaderName.CONTENT_TYPE, Lists.of(contentType));
            }

            @Override
            public String toString() {
                return Arrays.toString(contentType);
            }
        };
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<ContentTypeHttpHandler> type() {
        return ContentTypeHttpHandler.class;
    }
}
