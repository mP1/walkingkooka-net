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
import walkingkooka.net.RelativeUrl;
import walkingkooka.net.header.HttpHeaderName;
import walkingkooka.net.http.HasHeaders;
import walkingkooka.net.http.HttpMethod;
import walkingkooka.net.http.HttpProtocolVersion;
import walkingkooka.net.http.HttpTransport;

import java.util.List;
import java.util.Map;

/**
 * Defines a HTTP request.
 */
public interface HttpRequest extends HasHeaders {

    /**
     * An empty {@link Map} with no headers.
     */
    Map<HttpHeaderName<?>, Object> NO_HEADERS = Maps.empty();

    /**
     * An empty {@link Map} with no parameters.
     */
    Map<HttpRequestParameterName, List<String>> NO_PARAMETERS = Maps.empty();

    /**
     * The transport used to make the request.
     */
    HttpTransport transport();

    /**
     * Returns the HTTP protocol which appears on the request line.
     */
    HttpProtocolVersion protocolVersion();

    /**
     * Returns the url that appears on the request line.
     */
    RelativeUrl url();

    /**
     * Returns the {@link HttpMethod method} used to make the request.
     */
    HttpMethod method();

    /**
     * Returns the body accompanying the request.
     */
    byte[] body();

    /**
     * Returns a {@link Map} of parameters which may be taken from the query string or post data etc, depending on the method.
     */
    Map<HttpRequestParameterName, List<String>> parameters();

    /**
     * Returns all values for the requested parameter name.
     */
    List<String> parameterValues(final HttpRequestParameterName parameterName);

    /**
     * Returns a {@link Map} of all parameters.
     */
    default Map<HttpRequestAttribute<?>, Object> routingParameters() {
        return RouterHttpRequestParametersMap.with(this);
    }
}
