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

import javaemul.internal.annotations.GwtIncompatible;
import walkingkooka.net.RelativeUrl;
import walkingkooka.net.http.HttpEntity;
import walkingkooka.net.http.HttpMethod;
import walkingkooka.net.http.HttpProtocolVersion;
import walkingkooka.net.http.HttpTransport;
import walkingkooka.reflect.PublicStaticHelper;

import javax.servlet.http.HttpServletRequest;

public final class HttpRequests implements PublicStaticHelper {

    /**
     * Creates a request with DELETE
     */
    public static HttpRequest delete(final HttpTransport transport,
                                     final RelativeUrl url,
                                     final HttpProtocolVersion protocolVersion,
                                     final HttpEntity... entities) {
        return value(HttpMethod.DELETE, transport, url, protocolVersion, entities);
    }

    /**
     * {@see FakeHttpRequest}
     */
    public static HttpRequest fake() {
        return new FakeHttpRequest();
    }

    /**
     * Creates a request with GET.
     */
    public static HttpRequest get(final HttpTransport transport,
                                  final RelativeUrl url,
                                  final HttpProtocolVersion protocolVersion,
                                  final HttpEntity entity) {
        return value(HttpMethod.GET, transport, url, protocolVersion, entity);
    }

    /**
     * {@see HeaderScopeHttpRequest}
     */
    @GwtIncompatible
    public static HttpRequest headerScope(final HttpRequest request) {
        return HeaderScopeHttpRequest.with(request);
    }

    /**
     * {@see HttpServletRequestHttpRequest}
     */
    @GwtIncompatible
    public static HttpRequest httpServletRequest(final HttpServletRequest request) {
        return HttpServletRequestHttpRequest.with(request);
    }

    /**
     * Creates a request with PATCH
     */
    public static HttpRequest patch(final HttpTransport transport,
                                    final RelativeUrl url,
                                    final HttpProtocolVersion protocolVersion,
                                    final HttpEntity... entities) {
        return value(HttpMethod.PATCH, transport, url, protocolVersion, entities);
    }

    /**
     * Creates a request with POST
     */
    public static HttpRequest post(final HttpTransport transport,
                                   final RelativeUrl url,
                                   final HttpProtocolVersion protocolVersion,
                                   final HttpEntity... entities) {
        return value(HttpMethod.POST, transport, url, protocolVersion, entities);
    }

    /**
     * Creates a request with PUT
     */
    public static HttpRequest put(final HttpTransport transport,
                                  final RelativeUrl url,
                                  final HttpProtocolVersion protocolVersion,
                                  final HttpEntity... entities) {
        return value(HttpMethod.PUT, transport, url, protocolVersion, entities);
    }

    /**
     * {@see HttpRequestValue}
     */
    public static HttpRequest value(final HttpMethod method,
                                    final HttpTransport transport,
                                    final RelativeUrl url,
                                    final HttpProtocolVersion protocolVersion,
                                    final HttpEntity... entities) {
        return HttpRequestValue.with(method, transport, url, protocolVersion, entities);
    }

    /**
     * Stop creation
     */
    private HttpRequests() {
        throw new UnsupportedOperationException();
    }
}
