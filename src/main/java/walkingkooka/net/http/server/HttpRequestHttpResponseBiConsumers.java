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
import java.util.function.BiConsumer;
import java.util.function.Function;

public final class HttpRequestHttpResponseBiConsumers implements PublicStaticHelper {

    /**
     * {@see ContentTypeHttpRequestHttpResponseBiConsumer}
     */
    public static ContentTypeHttpRequestHttpResponseBiConsumer contentType(final MediaType contentType,
                                                                           final BiConsumer<HttpRequest, HttpResponse> handler) {
        return ContentTypeHttpRequestHttpResponseBiConsumer.with(contentType, handler);
    }

    /**
     * {@see HeadersCopyHttpRequestHttpResponseBiConsumer}
     */
    public static BiConsumer<HttpRequest, HttpResponse> headerCopy(final Set<HttpHeaderName<?>> headers,
                                                                   final BiConsumer<HttpRequest, HttpResponse> handler) {
        return HeadersCopyHttpRequestHttpResponseBiConsumer.with(headers, handler);
    }

    /**
     * {@see HeadersCopyHttpRequestHttpResponseBiConsumer}
     */
    public static BiConsumer<HttpRequest, HttpResponse> methodNotAllowed(final HttpMethod method,
                                                                         final BiConsumer<HttpRequest, HttpResponse> handler) {
        return MethodNotAllowedHttpRequestHttpResponseBiConsumer.with(method, handler);
    }

    /**
     * {@see RouterHttpRequestHttpResponseBiConsumer}
     */
    public static BiConsumer<HttpRequest, HttpResponse> router(final Router<HttpRequestAttribute<?>, BiConsumer<HttpRequest, HttpResponse>> router,
                                                               final BiConsumer<HttpRequest, HttpResponse> notFound) {
        return RouterHttpRequestHttpResponseBiConsumer.with(router, notFound);
    }

    /**
     * {@see StacktraceDumpingHttpRequestHttpResponseBiConsumer}
     */
    public static BiConsumer<HttpRequest, HttpResponse> stacktraceDumping(final BiConsumer<HttpRequest, HttpResponse> handler,
                                                                          final Function<Throwable, HttpStatus> throwableTranslator) {
        return StacktraceDumpingHttpRequestHttpResponseBiConsumer.with(handler, throwableTranslator);
    }

    /**
     * {@see ThrowableHttpStatusTranslatorFunction}
     */
    public static Function<Throwable, HttpStatus> throwableTranslator() {
        return ThrowableHttpStatusTranslatorFunction.INSTANCE;
    }

    /**
     * {@see WebFileHttpRequestHttpResponseBiConsumer}
     */
    public static BiConsumer<HttpRequest, HttpResponse> webFile(final UrlPath basePath,
                                                                final Function<UrlPath, Either<WebFile, HttpStatus>> files) {
        return WebFileHttpRequestHttpResponseBiConsumer.with(basePath, files);
    }

    /**
     * Stop creation
     */
    private HttpRequestHttpResponseBiConsumers() {
        throw new UnsupportedOperationException();
    }
}
