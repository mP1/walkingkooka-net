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
import walkingkooka.net.header.ETag;
import walkingkooka.net.header.ETagList;
import walkingkooka.net.header.HttpHeaderName;
import walkingkooka.net.http.HttpEntity;
import walkingkooka.net.http.HttpStatusCode;

/**
 * Wraps another {@link HttpHandler} removing the body of a response if an incoming {@link HttpHeaderName#IF_NONE_MATCHED}
 * matches the response {@link ETag}.
 */
final class HttpHandlerWrapperSharedIfNoneMatch<C extends HttpHandlerContext> extends HttpHandlerWrapperShared<C> {

    static <C extends HttpHandlerContext> HttpHandlerWrapperSharedIfNoneMatch<C> with(final HttpHandler<C> handler) {
        return new HttpHandlerWrapperSharedIfNoneMatch<>(handler);
    }

    private HttpHandlerWrapperSharedIfNoneMatch(final HttpHandler<C> handler) {
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

        // https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/If-None-Match
        //
        // When the condition fails for GET and HEAD methods, the server must return a 304 Not Modified and any of the
        // following header fields that would have been sent in a 200 response to the same request: Cache-Control,
        // Content-Location, Date, ETag, Expires, and Vary.
        final ETag ifNoneMatchOrNull = HttpHeaderName.IF_NONE_MATCHED.header(request)
            .flatMap(ETagList::firstOrEmpty)
            .orElse(null);

        if (null != ifNoneMatchOrNull) {
            final HttpEntity responseEntity = response.entity();

            // same then not modified and clear body
            if (ifNoneMatchOrNull.test(HttpHeaderName.E_TAG.header(responseEntity).orElse(null))) {

                response.setStatus(HttpStatusCode.NOT_MODIFIED.status());
                response.setEntity(
                    responseEntity.removeHeader(HttpHeaderName.E_TAG)
                        .removeHeader(HttpHeaderName.CONTENT_TYPE)
                        .removeHeader(HttpHeaderName.CONTENT_LENGTH)
                        .setBody(Binary.EMPTY)
                );
            }
        }
    }

    @Override
    public String toString() {
        return HttpHeaderName.IF_NONE_MATCHED + " " + this.handler;
    }
}
