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
import walkingkooka.Cast;
import walkingkooka.ToStringTesting;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.map.Maps;
import walkingkooka.net.header.CharsetName;
import walkingkooka.net.header.HttpHeaderName;
import walkingkooka.net.header.MediaType;
import walkingkooka.net.http.HttpEntity;
import walkingkooka.net.http.HttpStatus;
import walkingkooka.net.http.HttpStatusCode;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class HttpHandlerWrapperSharedContentTypeTest extends HttpHandlerWrapperSharedTestCase<HttpHandlerWrapperSharedContentType<FakeHttpHandlerContext>, FakeHttpHandlerContext>
    implements ToStringTesting<HttpHandlerWrapperSharedContentType<FakeHttpHandlerContext>> {

    private final static MediaType CONTENT_TYPE = MediaType.TEXT_PLAIN;
    private final static HttpStatus STATUS = HttpStatusCode.OK.setMessage("OK!");
    private final static HttpEntity ENTITY = HttpEntity.EMPTY.setBodyText("Success123");

    private final static HttpHandler<FakeHttpHandlerContext> HTTP_HANDLER = new HttpHandler<>() {
        @Override
        public void handle(final HttpRequest request,
                           final HttpResponse response,
                           final FakeHttpHandlerContext context) {
            Objects.requireNonNull(request, "request");
            Objects.requireNonNull(response, "response");
            Objects.requireNonNull(context, "context");

            response.setStatus(STATUS);
            response.setEntity(ENTITY);
        }

        @Override
        public String toString() {
            return "HTTP_HANDLER";
        }
    };

    @Test
    public void testWithNullContentTypeFails() {
        assertThrows(
            NullPointerException.class,
            () -> HttpHandlerWrapperSharedContentType.with(null, HTTP_HANDLER)
        );
    }

    // handle...........................................................................................................

    @Test
    public void testHandleMissingContentType() {
        final HttpResponse expected = HttpResponses.recording();
        expected.setStatus(HttpStatusCode.BAD_REQUEST.setMessage("Expected text/plain missing " + HttpHeaderName.CONTENT_TYPE));
        expected.setEntity(HttpEntity.EMPTY);

        this.handleAndCheck(
            this.createHttpHandler(HTTP_HANDLER),
            this.request(),
            expected
        );
    }

    @Test
    public void testHandleInvalidContentType() {
        final HttpResponse expected = HttpResponses.recording();
        expected.setStatus(HttpStatusCode.BAD_REQUEST.setMessage("Expected text/plain got application/octet-stream"));
        expected.setEntity(HttpEntity.EMPTY);

        this.handleAndCheck(
            this.createHttpHandler(HTTP_HANDLER),
            this.request(MediaType.BINARY),
            expected
        );
    }

    @Test
    public void testHandleInvalidContentTypeMessageWithoutParameters() {
        final HttpResponse expected = HttpResponses.recording();
        expected.setStatus(HttpStatusCode.BAD_REQUEST.setMessage("Expected text/plain got application/octet-stream"));
        expected.setEntity(HttpEntity.EMPTY);

        this.handleAndCheck(
            this.createHttpHandler(HTTP_HANDLER),
            this.request(
                MediaType.BINARY.setCharset(CharsetName.UTF_8)
            ),
            expected
        );
    }

    @Test
    public void testHandleValidContentType() {
        final HttpResponse expected = HttpResponses.recording();
        expected.setStatus(STATUS);
        expected.setEntity(ENTITY);

        this.handleAndCheck(
            this.createHttpHandler(HTTP_HANDLER),
            this.request(CONTENT_TYPE),
            expected
        );
    }

    @Override
    HttpHandlerWrapperSharedContentType<FakeHttpHandlerContext> createHttpHandler(final HttpHandler<FakeHttpHandlerContext> httpHandler) {
        return HttpHandlerWrapperSharedContentType.with(
            CONTENT_TYPE,
            httpHandler
        );
    }

    @Override
    public FakeHttpHandlerContext createContext() {
        return new FakeHttpHandlerContext();
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

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
            this.createHttpHandler(HTTP_HANDLER),
            CONTENT_TYPE + " " + HTTP_HANDLER
        );
    }

    // class............................................................................................................

    @Override
    public Class<HttpHandlerWrapperSharedContentType<FakeHttpHandlerContext>> type() {
        return Cast.to(HttpHandlerWrapperSharedContentType.class);
    }
}
