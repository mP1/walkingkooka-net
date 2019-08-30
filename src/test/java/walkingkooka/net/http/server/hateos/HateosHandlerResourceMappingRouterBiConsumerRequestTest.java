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
import walkingkooka.collect.set.Sets;
import walkingkooka.net.Url;
import walkingkooka.net.http.server.HttpRequest;
import walkingkooka.net.http.server.HttpRequests;
import walkingkooka.net.http.server.HttpResponse;
import walkingkooka.net.http.server.HttpResponses;
import walkingkooka.tree.json.marshall.FromJsonNodeContexts;
import walkingkooka.tree.json.marshall.ToJsonNodeContexts;

public final class HateosHandlerResourceMappingRouterBiConsumerRequestTest extends HateosHandlerResourceMappingTestCase2<HateosHandlerResourceMappingRouterBiConsumerRequest> {

    // toString.........................................................................................................

    @Test
    public void testToString() {
        final HateosHandlerResourceMappingRouter router = HateosHandlerResourceMappingRouter.with(Url.parseAbsolute("http://example.com"),
                HateosContentType.json(FromJsonNodeContexts.fake(), ToJsonNodeContexts.fake()),
                Sets.empty());
        final HttpRequest request = HttpRequests.fake();
        final HttpResponse response = HttpResponses.fake();

        this.toStringAndCheck(HateosHandlerResourceMappingRouterBiConsumerRequest.with(request, response, router),
                router + " " + request + " " + response);
    }

    // ClassTesting......................................................................................................

    @Override
    public Class<HateosHandlerResourceMappingRouterBiConsumerRequest> type() {
        return HateosHandlerResourceMappingRouterBiConsumerRequest.class;
    }

    // TypeNameTesting..................................................................................................

    @Override
    public String typeNamePrefix() {
        return HateosHandlerResourceMappingRouterBiConsumer.class.getSimpleName();
    }

    @Override
    public String typeNameSuffix() {
        return "Request";
    }
}