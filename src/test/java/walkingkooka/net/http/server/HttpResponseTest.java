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
import walkingkooka.net.header.HttpHeaderName;
import walkingkooka.net.header.MediaType;
import walkingkooka.net.http.HttpEntity;
import walkingkooka.net.http.HttpProtocolVersion;
import walkingkooka.net.http.HttpStatusCode;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallException;
import walkingkooka.tree.json.marshall.JsonNodeMarshallingTesting;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class HttpResponseTest implements ClassTesting<HttpResponse>, JsonNodeMarshallingTesting<HttpResponse> {

    @Test
    public void testVersionMissingJsonMarshallFails() {
        final HttpResponse response = HttpResponses.recording();
        response.setStatus(HttpStatusCode.withCode(987).setMessage("Custom Status Message"));
        response.addEntity(HttpEntity.EMPTY);

        assertThrows(JsonNodeMarshallException.class, () -> this.marshallContext().marshall(response));
    }

    @Test
    public void testStatusMissingJsonMarshallFails() {
        final HttpResponse response = HttpResponses.recording();
        response.setVersion(HttpProtocolVersion.VERSION_1_0);
        response.addEntity(HttpEntity.EMPTY);

        assertThrows(JsonNodeMarshallException.class, () -> this.marshallContext().marshall(response));
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<HttpResponse> type() {
        return HttpResponse.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }

    // JsonNodeMarshallingTesting.......................................................................................

    @Override
    public HttpResponse createJsonNodeMarshallingValue() {
        final HttpResponse response = HttpResponses.recording();
        response.setVersion(HttpProtocolVersion.VERSION_1_0);
        response.setStatus(HttpStatusCode.withCode(987).setMessage("Custom Status Message"));
        response.addEntity(HttpEntity.EMPTY
                .addHeader(HttpHeaderName.CONTENT_LENGTH, 123L)
                .addHeader(HttpHeaderName.CONTENT_TYPE, MediaType.TEXT_PLAIN)
                .setBodyText("body-text-123"));
        return response;
    }

    @Override
    public final HttpResponse unmarshall(final JsonNode from,
                                         final JsonNodeUnmarshallContext context) {
        return HttpResponses.parse(from.stringOrFail());
    }
}
