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
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.net.header.HttpHeaderName;
import walkingkooka.net.http.HttpEntity;
import walkingkooka.net.http.HttpProtocolVersion;
import walkingkooka.net.http.HttpStatus;
import walkingkooka.net.http.HttpStatusCode;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class RecordingHttpResponseTest extends HttpResponseTestCase2<RecordingHttpResponse>
    implements HashCodeEqualsDefinedTesting2<RecordingHttpResponse> {

    @Test
    public void testBuild() {
        final RecordingHttpResponse response = this.createResponse();
        final HttpProtocolVersion version = this.version();
        final HttpStatus status = this.status();
        final HttpEntity entity = this.entity();

        response.setVersion(version);
        response.setStatus(status);
        response.setEntity(entity);

        this.checkEquals(Optional.ofNullable(version), response.version(), "version");
        this.checkEquals(Optional.ofNullable(status), response.status(), "status");
        this.checkEquals(entity, response.entity(), "entity");
    }

    @Test
    public void testCheck() {
        final RecordingHttpResponse response = this.createResponse();

        final HttpProtocolVersion version = this.version();
        final HttpStatus status = this.status();
        final HttpEntity entity = this.entity();

        response.setVersion(version);
        response.setStatus(status);
        response.setEntity(entity);

        this.checkResponse(response, HttpRequests.fake(), version, status, entity);
    }

    @Test
    public void testCheckMultipleEntities() {
        final RecordingHttpResponse response = this.createResponse();

        final HttpProtocolVersion version = this.version();
        final HttpStatus status = this.status();
        final HttpEntity entity = this.entity();
        final HttpEntity entity2 = HttpEntity.EMPTY
            .addHeader(HttpHeaderName.SERVER, "part 2")
            .setBody(Binary.with(new byte[123]));
        response.setVersion(version);
        response.setStatus(status);
        response.setEntity(entity);
        response.setEntity(entity2);

        this.checkResponse(response, HttpRequests.fake(), version, status, entity, entity2);
    }

    @Test
    public void testCheckIncorrectStatusFails() {
        final RecordingHttpResponse response = this.createResponse();

        final HttpProtocolVersion version = this.version();
        final HttpStatus status = this.status();
        final HttpEntity entity = this.entity();

        response.setVersion(version);
        response.setStatus(status);
        response.setEntity(entity);

        assertThrows(AssertionError.class, () -> this.checkResponse(response, HttpRequests.fake(),
            version,
            HttpStatusCode.OK.status(),
            entity));
    }

    @Test
    public void testCheckDifferentEntityFails() {
        final RecordingHttpResponse response = this.createResponse();

        final HttpProtocolVersion version = this.version();
        final HttpStatus status = this.status();
        final HttpEntity entity = HttpEntity.EMPTY
            .addHeader(HttpHeaderName.SERVER, "Server 123")
            .setBody(Binary.with(new byte[123]));

        response.setVersion(version);
        response.setStatus(status);
        response.setEntity(entity);

        assertThrows(AssertionError.class, () -> this.checkResponse(response, HttpRequests.fake(),
            version,
            status,
            HttpEntity.EMPTY
                .addHeader(HttpHeaderName.SERVER, "Server 456")
                .setBody(Binary.with(new byte[456]))));
    }

    // equals...........................................................................................................

    @Test
    public void testEqualsDifferentVersion() {
        final RecordingHttpResponse response = this.createResponse();

        response.setVersion(HttpProtocolVersion.VERSION_1_0);
        response.setStatus(this.status());
        response.setEntity(this.entity());

        this.checkNotEquals(response);
    }

    @Test
    public void testEqualsDifferentStatus() {
        final RecordingHttpResponse response = this.createResponse();

        response.setVersion(this.version());
        response.setStatus(HttpStatusCode.withCode(9999).setMessage("Different"));
        response.setEntity(this.entity());

        this.checkNotEquals(response);
    }

    @Test
    public void testEqualsDifferentEntity() {
        final RecordingHttpResponse response = this.createResponse();

        response.setVersion(this.version());
        response.setStatus(this.status());
        response.setEntity(HttpEntity.EMPTY.setBodyText("Different"));

        this.checkNotEquals(response);
    }

    // toString.........................................................................................................

    @Test
    public void testToStringWithoutVersion() {
        final RecordingHttpResponse response = this.createResponse();
        response.setStatus(this.status());
        response.setEntity(this.entity());

        this.toStringAndCheck(response,
            "503 Problem x y z\r\n" +
                "Server: Server 123\r\n" +
                "\r\n" +
                "00000000 41 42 43                                        ABC             \r\n");
    }

    @Test
    public void testToString() {
        final RecordingHttpResponse response = this.createResponse();
        response.setVersion(this.version());
        response.setStatus(this.status());
        response.setEntity(this.entity());

        this.toStringAndCheck(response,
            "HTTP/1.1 503 Problem x y z\r\n" +
                "Server: Server 123\r\n" +
                "\r\n" +
                "00000000 41 42 43                                        ABC             \r\n");
    }

    @Override
    public RecordingHttpResponse createResponse() {
        return RecordingHttpResponse.with();
    }

    private HttpProtocolVersion version() {
        return HttpProtocolVersion.VERSION_1_1;
    }

    private HttpStatus status() {
        return HttpStatusCode.SERVICE_UNAVAILABLE.setMessage("Problem x y z");
    }

    private HttpEntity entity() {
        return HttpEntity.EMPTY
            .addHeader(HttpHeaderName.SERVER, "Server 123")
            .setBody(Binary.with(new byte[]{65, 66, 67}));
    }

    @Override
    public Class<RecordingHttpResponse> type() {
        return RecordingHttpResponse.class;
    }

    @Override
    public RecordingHttpResponse createObject() {
        final RecordingHttpResponse response = this.createResponse();

        response.setVersion(this.version());
        response.setStatus(this.status());
        response.setEntity(this.entity());

        return response;
    }
}
