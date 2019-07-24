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
import walkingkooka.Binary;
import walkingkooka.collect.list.Lists;
import walkingkooka.net.http.HttpEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class HeaderScopeHttpResponseTest extends WrapperHttpResponseTestCase<HeaderScopeHttpResponse> {

    @Test
    public void testAddEntity() {
        final List<HttpEntity> added = Lists.array();
        final HttpEntity entity = HttpEntity.EMPTY.setBody(Binary.with(new byte[123]));

        HeaderScopeHttpResponse.with(new FakeHttpResponse() {

            @Override
            public void addEntity(final HttpEntity e) {
                added.add(e);
            }

        }).addEntity(entity);

        assertEquals(Lists.of(entity), added, "added entities");
    }

    @Override
    HeaderScopeHttpResponse createResponse(final HttpRequest request,
                                           final HttpResponse response) {
        return HeaderScopeHttpResponse.with(response);
    }

    @Override
    HttpRequest createRequest() {
        return HttpRequests.fake();
    }

    @Override
    public Class<HeaderScopeHttpResponse> type() {
        return HeaderScopeHttpResponse.class;
    }
}
