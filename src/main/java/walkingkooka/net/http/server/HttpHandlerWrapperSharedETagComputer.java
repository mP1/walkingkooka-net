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
import walkingkooka.net.header.HttpHeaderName;
import walkingkooka.net.http.HttpEntity;
import walkingkooka.net.http.HttpStatus;
import walkingkooka.net.http.HttpStatusCodeCategory;

import java.util.Optional;

/**
 * A {@link HttpHandler} computes the {@link walkingkooka.net.header.ETag} for any 2xx response with a non-empty body.
 */
final class HttpHandlerWrapperSharedETagComputer<C extends HttpHandlerContext> extends HttpHandlerWrapperShared<C> {

    static <C extends HttpHandlerContext> HttpHandlerWrapperSharedETagComputer<C> with(final HttpHandler<C> handler) {
        return new HttpHandlerWrapperSharedETagComputer<>(handler);
    }

    private HttpHandlerWrapperSharedETagComputer(final HttpHandler<C> handler) {
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

        // if 2XX response
        if (response.status()
            .map((HttpStatus httpStatus) -> httpStatus.value().category() == HttpStatusCodeCategory.SUCCESSFUL)
            .orElse(false)) {

            // if BODY present
            final HttpEntity responseHttpEntity = response.entity();
            final Binary responseBinary = responseHttpEntity.binary();
            if (responseBinary.isNotEmpty()) {

                // if missing ETAG
                if (HttpHeaderName.E_TAG.header(responseHttpEntity).isEmpty()) {

                    // compute ETAG
                    final Optional<ETag> responseTag = context.computeETag(responseBinary);
                    if (responseTag.isPresent()) {

                        // save TAG
                        response.setEntity(
                            responseHttpEntity.addHeader(
                                HttpHeaderName.E_TAG,
                                responseTag.get()
                            )
                        );
                    }
                }
            }
        }
    }

    // Object...........................................................................................................

    @Override
    public String toString() {
        return this.handler.toString();
    }
}
