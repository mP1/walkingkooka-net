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

import walkingkooka.collect.map.Maps;
import walkingkooka.locale.LocaleContext;
import walkingkooka.net.RelativeUrl;
import walkingkooka.net.UrlParameterName;
import walkingkooka.net.header.HttpHeaderName;
import walkingkooka.net.http.HasHeaders;
import walkingkooka.net.http.HttpMethod;
import walkingkooka.net.http.HttpProtocolVersion;
import walkingkooka.net.http.HttpTransport;
import walkingkooka.text.CharSequences;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.OptionalInt;

/**
 * Defines a HTTP request.
 */
public interface HttpRequest extends HasHeaders {

    /**
     * The transport used to make the request.
     */
    HttpTransport transport();

    /**
     * Returns the {@link HttpMethod method} used to make the request.
     */
    HttpMethod method();

    /**
     * Returns the url that appears on the request line.
     */
    RelativeUrl url();

    /**
     * Returns the HTTP protocol which appears on the request line.
     */
    HttpProtocolVersion protocolVersion();

    /**
     * An empty {@link Map} with no headers.
     */
    Map<HttpHeaderName<?>, List<?>> NO_HEADERS = Maps.empty();

    /**
     * Returns the body accompanying the request.
     */
    byte[] body();

    /**
     * Returns the body as text. An added guard is made to ensure a null body returns an empty {@link String}.
     */
    default String bodyText() {
        final byte[] body = this.body();
        return null != body ?
            new String(
                body,
                this.charset()
            ) :
            "";
    }

    /**
     * Returns the content length of the body/bodyText
     */
    default long bodyLength() {
        return this.body().length;
    }

    /**
     * An empty {@link Map} with no parameters.
     */
    Map<HttpRequestParameterName, List<String>> NO_PARAMETERS = Maps.empty();

    /**
     * Returns a {@link Map} of parameters which may be taken from the query string or post data etc, depending on the method.
     */
    Map<HttpRequestParameterName, List<String>> parameters();

    /**
     * Returns all values for the requested parameter name.
     */
    List<String> parameterValues(final HttpRequestParameterName parameterName);

    // routerParameters.................................................................................................

    /**
     * Returns a {@link Map} of all {@link HttpRequestAttribute} parameters.
     */
    default Map<HttpRequestAttribute<?>, Object> routerParameters() {
        return HttpRequestRouterParametersMap.with(this);
    }

    /**
     * Returns the count parameter as an integer.
     */
    static OptionalInt count(final Map<HttpRequestAttribute<?>, Object> parameters) {
        return get(
            UrlParameterName.COUNT,
            parameters
        );
    }

    static Locale locale(final Map<HttpRequestAttribute<?>, Object> parameters,
                         final LocaleContext context) {
        return UrlParameterName.LOCALE.firstParameterValue(parameters)
            .map((Locale::forLanguageTag))
            .orElse(context.locale());
    }

    /**
     * Returns the offset parameter as an integer.
     */
    static OptionalInt offset(final Map<HttpRequestAttribute<?>, Object> parameters) {
        return get(
            UrlParameterName.OFFSET,
            parameters
        );
    }

    private static OptionalInt get(final UrlParameterName parameter,
                                   final Map<HttpRequestAttribute<?>, Object> parameters) {
        return parameter.firstParameterValue(parameters)
            .map(s -> parseInt(s, parameter))
            .orElse(OptionalInt.empty());
    }

    private static OptionalInt parseInt(final String text,
                                        final UrlParameterName parameter) {
        try {
            return OptionalInt.of(
                Integer.parseInt(text)
            );
        } catch (final NumberFormatException cause) {
            throw new IllegalArgumentException("Invalid " + parameter + " parameter got " + CharSequences.quoteAndEscape(text));
        }
    }
}
