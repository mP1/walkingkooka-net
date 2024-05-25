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

import walkingkooka.net.header.HttpHeaderName;
import walkingkooka.net.header.MediaType;
import walkingkooka.net.http.HttpEntity;
import walkingkooka.net.http.HttpStatusCode;

import java.util.Objects;
import java.util.Optional;

/**
 * A {@link HttpHandler} that tests if the request content type matches the requested {@link MediaType} or replies with
 * a {@link HttpStatusCode#BAD_REQUEST}.
 */
final class ContentTypeHttpHandler implements HttpHandler {

    static ContentTypeHttpHandler with(final MediaType contentType,
                                       final HttpHandler handler) {
        Objects.requireNonNull(contentType, "contentType");
        Objects.requireNonNull(handler, "handler");

        return new ContentTypeHttpHandler(contentType, handler);
    }

    private ContentTypeHttpHandler(final MediaType contentType,
                                   final HttpHandler handler) {
        super();
        this.contentType = contentType;
        this.handler = handler;
    }

    @Override
    public void handle(final HttpRequest request,
                       final HttpResponse response) {
        final MediaType expected = this.contentType;
        final Optional<MediaType> mediaType = HttpHeaderName.CONTENT_TYPE.header(request);
        if (mediaType.isPresent() && expected.test(mediaType.get())) {
            this.handler.handle(request, response);
        } else {
            response.setStatus(
                    HttpStatusCode.BAD_REQUEST
                            .setMessage("Expected " + expected +
                                    (mediaType.isPresent() ? " got " + mediaType.get() : " missing " + HttpHeaderName.CONTENT_TYPE))
            );
            response.addEntity(HttpEntity.EMPTY);
        }
    }

    /**
     * The expected content type.
     */
    private final MediaType contentType;

    private final HttpHandler handler;

    @Override
    public String toString() {
        return this.contentType + " " + this.handler;
    }
}
