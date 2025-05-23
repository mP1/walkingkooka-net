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

import org.junit.jupiter.api.Test;
import walkingkooka.ToStringTesting;
import walkingkooka.reflect.TypeNameTesting;

import java.util.Map;

/**
 * Mixin interface for testing {@link HttpRequest}
 */
public interface HttpRequestTesting<R extends HttpRequest> extends ToStringTesting<R>,
    TypeNameTesting<R> {

    @Test
    default void testRoutingParameters() {
        final Map<HttpRequestAttribute<?>, Object> routingParameters = this.createRequest().routerParameters();
        this.checkNotEquals(null, routingParameters.get(HttpRequestAttributes.METHOD), "method absent");
        this.checkNotEquals(null, routingParameters.get(HttpRequestAttributes.TRANSPORT), "transport absent");
        this.checkNotEquals(null, routingParameters.get(HttpRequestAttributes.HTTP_PROTOCOL_VERSION), "protocol absent");
        this.checkNotEquals(null, routingParameters.get(HttpRequestAttributes.PATH_COMPONENT_COUNT), "HttpRequestAttributes.PATH_COMPONENT_COUNT");
    }

    R createRequest();

    // TypeNameTesting .........................................................................................

    @Override
    default String typeNamePrefix() {
        return "";
    }

    @Override
    default String typeNameSuffix() {
        return HttpRequest.class.getSimpleName();
    }
}
