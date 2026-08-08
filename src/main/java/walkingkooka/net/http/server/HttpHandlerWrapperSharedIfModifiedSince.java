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
import walkingkooka.net.http.HttpEntity;
import walkingkooka.net.http.HttpStatus;
import walkingkooka.net.http.HttpStatusCode;
import walkingkooka.net.http.HttpStatusCodeCategory;

import java.time.LocalDateTime;

/**
 * Wraps another {@link HttpHandler} removing the body of a response if an incoming {@link HttpHeaderName#IF_MODIFIED_SINCE}
 * matches the response {@link HttpHeaderName#LAST_MODIFIED}.
 * <br>
 * https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/If-Modified-Since
 * <br>
 * The HTTP If-Modified-Since request header makes a request conditional. The server sends back the requested resource, with a 200 status, only if it has been modified after the date in the If-Modified-Since header. If the resource has not been modified since, the response is a 304 without any body, and the Last-Modified response header of the previous request contains the date of the last modification.
 * <p>
 * Unlike If-Unmodified-Since, If-Modified-Since can only be used with a GET or HEAD. When used in combination with If-None-Match, it is ignored, unless the server doesn't support If-None-Match.
 * <p>
 * The most common use case is to update a cached entity that has no associated ETag.
 */
final class HttpHandlerWrapperSharedIfModifiedSince<C extends HttpHandlerContext> extends HttpHandlerWrapperShared<C> {

    static <C extends HttpHandlerContext> HttpHandlerWrapperSharedIfModifiedSince<C> with(final HttpHandler<C> handler) {
        return new HttpHandlerWrapperSharedIfModifiedSince<>(handler);
    }

    private HttpHandlerWrapperSharedIfModifiedSince(final HttpHandler<C> handler) {
        super(handler);
    }

    @Override
    void handle0(final HttpRequest request,
                 final HttpResponse response,
                 final C context) {
        this.handler.handle(
            request,
            response,
            context
        );

        // https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/If-Modified-Since
        //
        // The HTTP If-Modified-Since request header makes a request conditional. The server sends back the requested resource,
        // with a 200 status, only if it has been modified after the date in the If-Modified-Since header.
        // If the resource has not been modified since, the response is a 304 without any body, and the Last-Modified
        // response header of the previous request contains the date of the last modification.
        // Unlike If-Unmodified-Since, If-Modified-Since can only be used with a GET or HEAD. When used in combination
        // with If-None-Match, it is ignored, unless the server doesn't support If-None-Match.
        //
        //The most common use case is to update a cached entity that has no associated ETag.

        if (request.method().isGetOrHead()) {

            final HttpStatus httpStatus = response.status()
                .orElse(null);
            if (null != httpStatus && httpStatus.value().category() == HttpStatusCodeCategory.SUCCESSFUL) {

                final LocalDateTime requestIfModifiedSince = HttpHeaderName.IF_MODIFIED_SINCE.header(request)
                    .orElse(null);
                if (null != requestIfModifiedSince) {

                    final HttpEntity responseHttpEntity = response.entity();

                    final LocalDateTime lastModifiedOrNull = responseHttpEntity.lastModified()
                        .orElse(null);
                    if (null != lastModifiedOrNull && false == requestIfModifiedSince.isAfter(lastModifiedOrNull)) {
                        response.setStatus(
                            HttpStatusCode.NOT_MODIFIED.status()
                        );
                        response.setEntity(HttpEntity.EMPTY);
                    }
                }
            }
        }
    }

    @Override
    public String toString() {
        return HttpHeaderName.IF_MODIFIED_SINCE + " " + this.handler;
    }
}
