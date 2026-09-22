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

import walkingkooka.Cast;
import walkingkooka.logging.LoggerPath;
import walkingkooka.logging.LoggingContext;
import walkingkooka.net.header.HasContentType;
import walkingkooka.net.header.HttpHeaderName;
import walkingkooka.net.header.MediaType;
import walkingkooka.net.http.HttpEntity;
import walkingkooka.net.http.HttpStatus;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * A {@link HttpHandler} that logs the request and response, without altering either.
 */
final class HttpHandlerWrappedSharedLogging<C extends HttpHandlerContext> extends HttpHandlerWrapperShared<C> {

    static <C extends HttpHandlerContext> HttpHandlerWrappedSharedLogging<C> with(final HttpHandler<C> handler) {
        return new HttpHandlerWrappedSharedLogging<>(handler);
    }

    private HttpHandlerWrappedSharedLogging(final HttpHandler<C> handler) {
        super(handler);
    }

    private final static LoggerPath HTTP_REQUEST = LoggerPath.parse("http.request");
    private final static LoggerPath HTTP_REQUEST_HEADERS = LoggerPath.parse("http.request.headers");
    private final static LoggerPath HTTP_REQUEST_BODY = LoggerPath.parse("http.request.body");

    private final static LoggerPath HTTP_HANDLER = LoggerPath.parse("http.handler");

    private final static LoggerPath HTTP_RESPONSE = LoggerPath.parse("http.response");
    private final static LoggerPath HTTP_RESPONSE_HEADERS = LoggerPath.parse("http.response.headers");
    private final static LoggerPath HTTP_RESPONSE_BODY = LoggerPath.parse("http.response.body");

    @Override
    void handle0(final HttpRequest request,
                 final HttpResponse response,
                 final C context) {
        try {
            context.logEnter(HTTP_REQUEST);

            // GET / HTTP/1.0
            if (context.isInfoEnabled()) {
                context.info(request.method() + " " + request.url() + " " + request.protocolVersion());
            }

            logHeaders(
                HTTP_REQUEST_HEADERS,
                request.headers(),
                context // LoggingContext
            );

            if (isText(request)) {
                logBodyText(
                    HTTP_RESPONSE_BODY,
                    request.bodyText(),
                    context
                );
            }

            this.invokeHandler(
                request,
                response,
                context
            );
        } finally {
            context.logExit(); // http.request
        }
        try {
            context.logEnter(HTTP_RESPONSE);

            if (context.isInfoEnabled()) {
                context.info(
                    response.status()
                        .map(HttpStatus::toString)
                        .orElse("")
                );
            }

            final HttpEntity entity = response.entity();

            logHeaders(
                HTTP_RESPONSE_HEADERS,
                entity.headers(),
                context // LoggingContext
            );

            if (isText(entity)) {
                logBodyText(
                    HTTP_RESPONSE_BODY,
                    entity.bodyText(),
                    context
                );
            }
        } finally {
            context.logExit();
        }
    }

    private static void logHeaders(final LoggerPath logger,
                                   final Map<HttpHeaderName<?>, List<?>> headers,
                                   final LoggingContext context) {
        if (context.isDebugEnabled()) {
            try {
                context.logEnter(logger);

                for (final Entry<HttpHeaderName<?>, List<?>> headerAndValue : headers.entrySet()) {
                    final HttpHeaderName<?> header = headerAndValue.getKey();

                    for (final Object value : headerAndValue.getValue()) {
                        context.debug(
                            "" +
                                header +
                                HttpEntity.HEADER_NAME_SEPARATOR +
                                " " +
                                header.headerText(
                                    Cast.to(value)
                                )
                        );
                    }
                }
            } finally {
                context.logExit();
            }
        }
    }

    private static boolean isText(final HasContentType hasContentType) {
        return hasContentType.contentType()
            .map(MediaType::isText)
            .orElse(false);
    }

    private static void logBodyText(final LoggerPath logger,
                                    final String bodyText,
                                    final LoggingContext context) {
        if (false == bodyText.isEmpty()) {
            try {
                context.logEnter(logger);
                context.debug(bodyText);
            } finally {
                context.logExit();
            }
        }
    }

    private void invokeHandler(final HttpRequest request,
                               final HttpResponse response,
                               final C context) {
        try {
            context.logEnter(HTTP_HANDLER);
            this.handler.handle(
                request,
                response,
                context
            );
        } finally {
            context.logExit();
        }
    }

    @Override
    public String toString() {
        return "logging " + this.handler;
    }
}
