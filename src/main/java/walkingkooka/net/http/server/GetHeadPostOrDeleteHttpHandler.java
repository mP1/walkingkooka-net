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

import walkingkooka.Binary;
import walkingkooka.collect.list.Lists;
import walkingkooka.net.header.HttpHeaderName;
import walkingkooka.net.header.MediaType;
import walkingkooka.net.http.HttpEntity;
import walkingkooka.net.http.HttpMethod;
import walkingkooka.net.http.HttpStatusCode;

import java.util.Base64;
import java.util.Objects;

/**
 * A {@link HttpHandler} that routes:
 * <ol>
 *     <li>{@link HttpMethod#GET}, {@link HttpMethod#HEAD} to {@link #handleGetOrHead(HttpRequest, HttpResponse, HttpHandlerContext)}</li>
 *     <li>{@link HttpMethod#POST} handles multipart, decodes {@link MediaType#TEXT_BASE64} to {@link #handleNonMultipartPost(HttpRequest, HttpEntity, HttpResponse, HttpHandlerContext)}</li>
 *     <li>{@link HttpMethod#DELETE}, to {@link #handleDelete(HttpRequest, HttpResponse, HttpHandlerContext)}</li>
 * </ol>
 */
public interface GetHeadPostOrDeleteHttpHandler<C extends HttpHandlerContext> extends HttpHandler<C> {

    @Override
    default void handle(final HttpRequest request,
                        final HttpResponse response,
                        final C context) {
        Objects.requireNonNull(request, "request");
        Objects.requireNonNull(response, "response");
        Objects.requireNonNull(context, "context");

        final HttpMethod httpMethod = request.method();
        if (httpMethod.isGetOrHead()) {
            this.handleGetOrHead(
                request,
                response,
                context
            );
        } else {
            if (HttpMethod.POST == httpMethod) {
                this.handlePost(
                    request,
                    response,
                    context
                );
            } else {
                if (HttpMethod.DELETE == httpMethod) {
                    this.handleDelete(
                        request,
                        response,
                        context
                    );
                } else {
                    response.setMethodNotAllowed(
                        httpMethod,
                        Lists.of(
                            HttpMethod.GET,
                            HttpMethod.HEAD,
                            HttpMethod.POST,
                            HttpMethod.DELETE
                        )
                    );
                }
            }
        }
    }

    /**
     * This method is invoked if the method is a GET or HEAD.
     */
    void handleGetOrHead(final HttpRequest request,
                         final HttpResponse response,
                         final C context);

    /**
     * This method is invoked if the method is a POST
     */
    default void handlePost(final HttpRequest request,
                            final HttpResponse response,
                            final C context) {
        HttpEntity content = HttpEntity.EMPTY.setBody(
            Binary.with(
                request.body()
            )
        );

        {
            MediaType contentType = HttpHeaderName.CONTENT_TYPE.header(request)
                .orElse(null);
            if (null != contentType) {
                content = content.setContentType(contentType);
            }
        }

        // keep trying until BINARY match
        for (; ; ) {
            final MediaType contentType = HttpHeaderName.CONTENT_TYPE.header(content)
                .orElse(null);
            if (null == contentType) {
                response.setVersion(request.protocolVersion());
                response.setStatus(HttpStatusCode.BAD_REQUEST.status());
                response.setEntity(
                    HttpEntity.EMPTY.setContentType(MediaType.TEXT_PLAIN)
                        .setBodyText("Missing " + HttpHeaderName.CONTENT_TYPE)
                        .setContentLength()
                );
                break;
            }

            if (MediaType.MULTIPART_FORM_DATA.test(contentType)) {
                content = this.multipartUpload(content); // try again
            } else {
                if (MediaType.TEXT_BASE64.test(contentType)) {
                    content = this.base64File(content); // try again
                } else {
                    this.handleNonMultipartPost(
                        request,
                        content,
                        response,
                        context
                    );
                    break;
                }
            }
        }
    }

    private HttpEntity multipartUpload(final HttpEntity entity) {
        HttpEntity nonMultipart = null;

        for (final HttpEntity part : entity.multiparts()) {
            nonMultipart = part;
            break;
        }

        if (null == nonMultipart) {
            throw new IllegalArgumentException("Multipart parts missing file");
        }

        return nonMultipart.setContentType(MediaType.BINARY);
    }

    private HttpEntity base64File(final HttpEntity entity) {
        return entity.setBody(
            Binary.with(
                Base64.getDecoder()
                    .decode(
                        entity.bodyText()
                    )
            )
        ).setContentType(MediaType.BINARY);
    }

    void handleNonMultipartPost(final HttpRequest request,
                                final HttpEntity content,
                                final HttpResponse response,
                                final C context);

    void handleDelete(final HttpRequest request,
                      final HttpResponse response,
                      final C context);
}
