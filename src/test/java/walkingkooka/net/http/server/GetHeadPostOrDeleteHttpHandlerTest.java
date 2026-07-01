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
import walkingkooka.net.RelativeUrl;
import walkingkooka.net.Url;
import walkingkooka.net.header.HttpHeaderName;
import walkingkooka.net.header.MediaType;
import walkingkooka.net.header.MediaTypeBoundary;
import walkingkooka.net.http.HttpEntity;
import walkingkooka.net.http.HttpProtocolVersion;
import walkingkooka.net.http.HttpStatus;
import walkingkooka.net.http.HttpStatusCode;
import walkingkooka.net.http.HttpTransport;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.reflect.ThrowableTesting;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public final class GetHeadPostOrDeleteHttpHandlerTest implements GetHeadPostOrDeleteHttpHandlerTesting<GetHeadPostOrDeleteHttpHandler<FakeHttpHandlerContext>, FakeHttpHandlerContext>,
    ThrowableTesting {

    private final static HttpStatus GET_STATUS = HttpStatusCode.OK.setMessage("GET OK");
    private final static HttpStatus POST_STATUS = HttpStatusCode.OK.setMessage("POST OK");
    private final static HttpStatus DELETE_STATUS = HttpStatusCode.OK.setMessage("DELETE OK");

    private final static RelativeUrl URL = Url.parseRelative("/api/Hello");

    private final static MediaType GET_RESPONSE_CONTENT_TYPE = MediaType.parse("text/123");

    private final static MediaType DELETE_RESPONSE_CONTENT_TYPE = MediaType.parse("text/456");

    private final static HttpProtocolVersion HTTP_PROTOCOL_VERSION = HttpProtocolVersion.VERSION_1_0;

    @Test
    public void testHandleDelete() {
        final String bodyText = "Request Body 123";

        final HttpResponse response = HttpResponses.recording();

        response.setVersion(HTTP_PROTOCOL_VERSION);
        response.setStatus(DELETE_STATUS);
        response.setEntity(
            HttpEntity.EMPTY.setBodyText("DELETE\n" + bodyText)
                .setContentType(DELETE_RESPONSE_CONTENT_TYPE)
                .setContentLength()
        );

        this.handleAndCheck(
            HttpRequests.delete(
                HttpTransport.UNSECURED,
                URL,
                HTTP_PROTOCOL_VERSION,
                HttpEntity.EMPTY.setBodyText(bodyText)
            ),
            this.createContext(),
            response
        );
    }

    @Test
    public void testHandleGet() {
        final String bodyText = "Request Body 123";

        final HttpResponse response = HttpResponses.recording();

        response.setVersion(HTTP_PROTOCOL_VERSION);
        response.setStatus(GET_STATUS);
        response.setEntity(
            HttpEntity.EMPTY.setBodyText("GET\n" + bodyText)
                .setContentType(GET_RESPONSE_CONTENT_TYPE)
                .setContentLength()
        );

        this.handleAndCheck(
            HttpRequests.get(
                HttpTransport.UNSECURED,
                URL,
                HTTP_PROTOCOL_VERSION,
                HttpEntity.EMPTY.setBodyText(bodyText)
            ),
            this.createContext(),
            response
        );
    }

    @Test
    public void testHandleHead() {
        final String bodyText = "Request Body 123";

        final HttpResponse response = HttpResponses.recording();

        response.setVersion(HTTP_PROTOCOL_VERSION);
        response.setStatus(GET_STATUS);
        response.setEntity(
            HttpEntity.EMPTY.setBodyText("HEAD\n" + bodyText)
                .setContentType(GET_RESPONSE_CONTENT_TYPE)
                .setContentLength()
        );

        this.handleAndCheck(
            HttpRequests.head(
                HttpTransport.UNSECURED,
                URL,
                HTTP_PROTOCOL_VERSION,
                HttpEntity.EMPTY.setBodyText(bodyText)
            ),
            this.createContext(),
            response
        );
    }

    @Test
    public void testHandlePostMissingContentTypeFails() {
        this.handleAndCheck(
            HttpRequests.post(
                HttpTransport.UNSECURED,
                URL,
                HTTP_PROTOCOL_VERSION,
                HttpEntity.EMPTY.addHeader(
                    HttpHeaderName.ACCEPT,
                    MediaType.ANY_TEXT.accept()
                )
            ),
            HttpResponses.parse(
                "HTTP/1.0 400 Bad request\r\n" +
                    "Content-Type: text/plain\r\n" +
                    "Content-Length: 20\r\n" +
                    "\r\n" +
                    "Missing Content-Type"
            )
        );
    }

    @Test
    public void testHandlePostContentTypeTextPlain() {
        this.handleAndCheck(
            HttpRequests.post(
                HttpTransport.UNSECURED,
                URL,
                HTTP_PROTOCOL_VERSION,
                HttpEntity.EMPTY.addHeader(
                        HttpHeaderName.ACCEPT,
                        MediaType.TEXT_PLAIN.accept()
                    ).setContentType(MediaType.TEXT_PLAIN)
                    .setBodyText("Request body 123")
            ),
            HttpResponses.parse(
                "HTTP/1.0 200 POST OK\r\n" +
                    "Content-Type: text/plain\r\n" +
                    "Content-Length: 21\r\n" +
                    "\r\n" +
                    "POST\n" +
                    "Request body 123"
            )
        );
    }

    private final static String POST_CONTENT = "HelloWorld111";

    @Test
    public void testHandlePostContentTypeBase64() {
        this.handleAndCheck(
            HttpRequests.post(
                HttpTransport.UNSECURED,
                URL,
                HTTP_PROTOCOL_VERSION,
                HttpEntity.EMPTY.addHeader(
                        HttpHeaderName.ACCEPT,
                        MediaType.TEXT_PLAIN.accept()
                    ).setContentType(MediaType.TEXT_BASE64)
                    .setBodyText(
                        Base64.getEncoder()
                            .encodeToString(
                                POST_CONTENT.getBytes(StandardCharsets.UTF_8)
                            )
                    )
            ),
            HttpResponses.parse(
                "HTTP/1.0 200 POST OK\r\n" +
                    "Content-Type: application/octet-stream\r\n" +
                    "Content-Length: 18\r\n" +
                    "\r\n" +
                    "POST\n" +
                    POST_CONTENT
            )
        );
    }

    @Test
    public void testHandlePostMultipart() {
        final String boundary = "delimiter12345";

        final HttpEntity multipart = HttpEntity.EMPTY.setContentType(
            MediaType.MULTIPART_FORM_DATA.setBoundary(MediaTypeBoundary.parse(boundary))
        ).setBodyText(
            "--delimiter12345\r\n" +
                "Content-Disposition: form-data; name=\"field2\"; filename=\"abc.txt\"\r\n" +
                "\r\n" +
                POST_CONTENT +
                "\r\n" +
                "--" + boundary + "--"
        );

        this.handleAndCheck(
            HttpRequests.post(
                HttpTransport.UNSECURED,
                URL,
                HTTP_PROTOCOL_VERSION,
                multipart
            ),
            HttpResponses.parse(
                "HTTP/1.0 200 POST OK\r\n" +
                    "Content-Type: application/octet-stream\r\n" +
                    "Content-Length: 18\r\n" +
                    "\r\n" +
                    "POST\n" +
                    POST_CONTENT
            )
        );
    }

    @Override
    public GetHeadPostOrDeleteHttpHandler<FakeHttpHandlerContext> createHttpHandler() {
        return new GetHeadPostOrDeleteHttpHandler<>() {
            @Override
            public void handleGetOrHead(final HttpRequest request,
                                        final HttpResponse response,
                                        final FakeHttpHandlerContext context) {
                response.setVersion(HTTP_PROTOCOL_VERSION);
                response.setStatus(GET_STATUS);
                response.setEntity(
                    HttpEntity.EMPTY.setBodyText(
                            request.method() +
                                "\n" +
                                request.bodyText()
                        ).setContentType(GET_RESPONSE_CONTENT_TYPE)
                        .setContentLength()
                );
            }

            @Override
            public void handleNonMultipartPost(final HttpRequest request,
                                               final HttpEntity content,
                                               final HttpResponse response,
                                               final FakeHttpHandlerContext context) {
                response.setVersion(HTTP_PROTOCOL_VERSION);
                response.setStatus(POST_STATUS);
                response.setEntity(
                    HttpEntity.EMPTY.setBodyText(
                        "POST\n" +
                            content.bodyText()
                    ).setContentType(
                        HttpHeaderName.CONTENT_TYPE.headerOrFail(content)
                    ).setContentLength()
                );
            }

            @Override
            public void handleDelete(final HttpRequest request,
                                     final HttpResponse response,
                                     final FakeHttpHandlerContext context) {
                response.setVersion(HTTP_PROTOCOL_VERSION);
                response.setStatus(DELETE_STATUS);
                response.setEntity(
                    HttpEntity.EMPTY.setBodyText(
                            "DELETE\n" +
                                request.bodyText()
                        ).setContentType(DELETE_RESPONSE_CONTENT_TYPE)
                        .setContentLength()
                );
            }
        };
    }

    @Override
    public FakeHttpHandlerContext createContext() {
        return new FakeHttpHandlerContext();
    }

    // class............................................................................................................

    @Override
    public Class<GetHeadPostOrDeleteHttpHandler<FakeHttpHandlerContext>> type() {
        return Cast.to(GetHeadPostOrDeleteHttpHandler.class);
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
