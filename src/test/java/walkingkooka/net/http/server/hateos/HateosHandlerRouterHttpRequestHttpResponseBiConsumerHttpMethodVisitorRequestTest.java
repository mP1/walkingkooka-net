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

package walkingkooka.net.http.server.hateos;

import org.junit.jupiter.api.Test;
import walkingkooka.Cast;
import walkingkooka.collect.map.Maps;
import walkingkooka.net.Url;
import walkingkooka.net.http.HttpMethodVisitor;
import walkingkooka.net.http.server.HttpRequest;
import walkingkooka.net.http.server.HttpRequests;
import walkingkooka.net.http.server.HttpResponse;
import walkingkooka.net.http.server.HttpResponses;
import walkingkooka.test.ToStringTesting;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.FromJsonNodeContexts;
import walkingkooka.tree.json.marshall.ToJsonNodeContexts;
import walkingkooka.type.JavaVisibility;

import java.util.function.BiConsumer;

public final class HateosHandlerRouterHttpRequestHttpResponseBiConsumerHttpMethodVisitorRequestTest extends HateosHandlerRouterTestCase<HateosHandlerRouterHttpRequestHttpResponseBiConsumerHttpMethodVisitorRequest<JsonNode>>
        implements ToStringTesting<HateosHandlerRouterHttpRequestHttpResponseBiConsumerHttpMethodVisitorRequest<JsonNode>> {

    @Test
    public void testToString() {
        final HateosHandlerRouter<?> router = HateosHandlerRouter.with(Url.parseAbsolute("http://example.com"),
                HateosContentType.json(FromJsonNodeContexts.fake(), ToJsonNodeContexts.fake()),
                Maps.empty());
        this.toStringAndCheck(HateosHandlerRouterHttpRequestHttpResponseBiConsumerHttpMethodVisitorRequest.with(router,
                HttpRequests.fake(),
                HttpResponses.fake()),
                router.toString());
    }

    @Override
    public Class<HateosHandlerRouterHttpRequestHttpResponseBiConsumerHttpMethodVisitorRequest<JsonNode>> type() {
        return Cast.to(HateosHandlerRouterHttpRequestHttpResponseBiConsumerHttpMethodVisitorRequest.class);
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }

    @Override
    String typeNamePrefix2() {
        return HttpRequest.class.getSimpleName() + HttpResponse.class.getSimpleName();
    }

    @Override
    public String typeNameSuffix() {
        return BiConsumer.class.getSimpleName() + HttpMethodVisitor.class.getSimpleName() + "Request";
    }
}
