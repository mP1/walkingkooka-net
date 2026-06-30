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

import walkingkooka.Either;
import walkingkooka.net.UrlPath;
import walkingkooka.net.header.HttpHeaderName;
import walkingkooka.net.header.MediaType;
import walkingkooka.net.http.HttpMethod;
import walkingkooka.net.http.HttpStatus;
import walkingkooka.reflect.PublicStaticHelper;
import walkingkooka.route.Router;

import java.util.Set;
import java.util.function.Function;

public final class HttpHandlers implements PublicStaticHelper {

    /**
     * {@see ContentTypeHttpHandler}
     */
    public static <C extends HttpHandlerContext> HttpHandler<C> contentType(final MediaType contentType,
                                                                            final HttpHandler<C> handler) {
        return ContentTypeHttpHandler.with(contentType, handler);
    }

    /**
     * {@see FakeHttpHandler}
     */
    public static <C extends HttpHandlerContext> HttpHandler<C> fake() {
        return new FakeHttpHandler<>();
    }

    /**
     * {@see HeadersCopyHttpHandler}
     */
    public static <C extends HttpHandlerContext> HttpHandler<C> headerCopy(final Set<HttpHeaderName<?>> headers,
                                                                           final HttpHandler<C> handler) {
        return HeadersCopyHttpHandler.with(headers, handler);
    }

    /**
     * {@see MethodNotAllowedHttpHandler}
     */
    public static <C extends HttpHandlerContext> HttpHandler<C> methodNotAllowed(final HttpMethod method,
                                                                                 final HttpHandler<C> handler) {
        return MethodNotAllowedHttpHandler.with(method, handler);
    }

    /**
     * {@see RouterHttpHandler}
     */
    public static <C extends HttpHandlerContext> HttpHandler<C> router(final Router<HttpRequestAttribute<?>, HttpHandler<C>> router,
                                                                       final HttpHandler<C> notFound) {
        return RouterHttpHandler.with(router, notFound);
    }

    /**
     * {@see StacktraceDumpingHttpHandler}
     */
    public static <C extends HttpHandlerContext> HttpHandler<C> stacktraceDumping(final HttpHandler<C> handler,
                                                                                  final Function<Throwable, HttpStatus> throwableTranslator) {
        return StacktraceDumpingHttpHandler.with(
            handler,
            throwableTranslator
        );
    }

    /**
     * {@see ThrowableHttpStatusTranslatorFunction}
     */
    public static Function<Throwable, HttpStatus> throwableTranslator() {
        return ThrowableHttpStatusTranslatorFunction.INSTANCE;
    }

    /**
     * {@see WebFileHttpHandler}
     */
    public static <C extends HttpHandlerContext> HttpHandler<C> webFile(final UrlPath basePath,
                                                                        final Function<UrlPath, Either<WebFile, HttpStatus>> files) {
        return WebFileHttpHandler.with(basePath, files);
    }

    /**
     * Stop creation
     */
    private HttpHandlers() {
        throw new UnsupportedOperationException();
    }
}
