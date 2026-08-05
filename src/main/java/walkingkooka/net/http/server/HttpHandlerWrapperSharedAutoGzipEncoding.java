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
import walkingkooka.net.header.AcceptEncoding;
import walkingkooka.net.header.ContentEncoding;
import walkingkooka.net.header.HttpHeaderName;
import walkingkooka.net.http.HttpEntity;

import java.io.IOException;
import java.util.Optional;

/**
 * If {@link HttpHeaderName#ACCEPT_ENCODING} supports GZIP encoding and the response body is not empty or encoded, then
 * GZIP encodes
 */
final class HttpHandlerWrapperSharedAutoGzipEncoding<C extends HttpHandlerContext> extends HttpHandlerWrapperShared<C> {

    static <C extends HttpHandlerContext> HttpHandlerWrapperSharedAutoGzipEncoding<C> with(final HttpHandler<C> handler) {
        return new HttpHandlerWrapperSharedAutoGzipEncoding<>(handler);
    }

    private HttpHandlerWrapperSharedAutoGzipEncoding(final HttpHandler<C> handler) {
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

        HttpEntity responseEntity = response.entity();

        if (responseEntity.body().isNotEmpty()) {
            final Optional<AcceptEncoding> acceptEncodings = HttpHeaderName.ACCEPT_ENCODING.header(request);

            if (acceptEncodings.map(ae -> ae.test(ContentEncoding.GZIP)).orElse(Boolean.FALSE)) {

                final Optional<ContentEncoding> contentEncodings = HttpHeaderName.CONTENT_ENCODING.header(responseEntity);
                if (false == contentEncodings.isPresent()) {
                    // content-encoding absent so gzip
                    responseEntity = responseEntity.addHeader(
                        HttpHeaderName.CONTENT_ENCODING,
                        ContentEncoding.GZIP
                    );
                    responseEntity = responseEntity.setBody(
                        gzip(
                            responseEntity.body()
                        )
                    );
                }
            }

            response.setEntity(responseEntity);
        }
    }

    /**
     * Returns the GZIP compressed form of the given byte array.
     */
    // @VisibleForTesting
    static Binary gzip(final Binary body) {
        try {
            return body.gzip();
        } catch (final IOException cause) {
            throw new HttpServerException("Failed to gzip compress bytes, " + cause, cause);
        }
    }

    @Override
    public String toString() {
        return "AutoGzipEncoding " + this.handler;
    }
}
