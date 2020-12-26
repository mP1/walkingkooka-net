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
import walkingkooka.Cast;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.map.Maps;
import walkingkooka.net.header.HttpHeaderName;
import walkingkooka.net.http.HttpEntity;
import walkingkooka.net.http.HttpProtocolVersion;
import walkingkooka.net.http.HttpStatus;
import walkingkooka.net.http.HttpStatusCode;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class WrapperHttpResponseTestCase<R extends WrapperHttpResponse> extends HttpResponseTestCase2<R> {

    WrapperHttpResponseTestCase() {
        super();
    }

    @Test
    public final void testWithNullResponseFails() {
        assertThrows(NullPointerException.class, () -> this.createResponse(null));
    }

    @Test
    public void testVersionNone() {
        this.versionAndCheck(Optional.empty());
    }

    @Test
    public void testVersion10() {
        this.versionAndCheck(Optional.of(HttpProtocolVersion.VERSION_1_0));
    }

    @Test
    public void testVersion11() {
        this.versionAndCheck(Optional.of(HttpProtocolVersion.VERSION_1_1));
    }

    @Test
    public void testVersion2() {
        this.versionAndCheck(Optional.of(HttpProtocolVersion.VERSION_2));
    }

    private void versionAndCheck(final Optional<HttpProtocolVersion> version) {
        assertEquals(version,
                this.createResponse(new FakeHttpResponse() {
                    @Test
                    public Optional<HttpProtocolVersion> version() {
                        return version;
                    }
                }).version()
        );
    }

    @Test
    public void testSetVersion() {
        this.setVersion = 0;

        final HttpProtocolVersion version = HttpProtocolVersion.VERSION_1_0;
        this.createResponse(new FakeHttpResponse() {
            @Test
            public void setVersion(final HttpProtocolVersion v) {
                assertSame(version, v);
                setVersion++;
            }
        }).setVersion(version);
        assertEquals(1, this.setVersion, "wrapped response setVersion not called");
    }

    private int setVersion;

    @Test
    public void testSetStatus() {
        this.setStatus = 0;

        final HttpStatus status = HttpStatusCode.OK.status();
        this.createResponse(new FakeHttpResponse() {
            @Test
            public void setStatus(final HttpStatus s) {
                assertSame(status, s);
                setStatus++;
            }
        }).setStatus(status);
        assertEquals(1, this.setStatus, "wrapped response setStatus not called");
    }

    private int setStatus;

    @Test
    public final void testToString() {
        final String toString = "Wrapped Http Response";
        this.toStringAndCheck(this.createResponse(new FakeHttpResponse() {

                    @Override
                    public String toString() {
                        return toString;
                    }
                }),
                toString);
    }

    // helpers..................................................................................................

    @Override
    public final R createResponse() {
        return this.createResponse(this.wrappedHttpResponse());
    }

    HttpResponse wrappedHttpResponse() {
        return HttpResponses.fake();
    }

    R createResponse(final HttpResponse response) {
        return this.createResponse(this.createRequest(), response);
    }

    abstract HttpRequest createRequest();

    abstract R createResponse(final HttpRequest request, final HttpResponse response);

    final HttpEntity httpEntity(final Map<HttpHeaderName<?>, List<?>> headers) {
        HttpEntity httpEntity = HttpEntity.EMPTY;

        for (final Entry<HttpHeaderName<?>, List<?>> headerAndValues : headers.entrySet()) {
            httpEntity = httpEntity.setHeader(headerAndValues.getKey(), Cast.to(headerAndValues.getValue()));
        }

        return httpEntity;
    }

    final void setVersionStatusAddEntityAndCheck(final HttpProtocolVersion version,
                                                 final HttpStatus status,
                                                 final HttpEntity entity) {
        this.setVersionStatusAddEntityAndCheck(version,
                status,
                entity,
                status,
                entity);
    }

    final void setVersionStatusAddEntityAndCheck(final HttpProtocolVersion version,
                                                 final HttpStatus status,
                                                 final HttpEntity... entities) {
        this.setVersionStatusAddEntityAndCheck(version,
                status,
                Lists.of(entities),
                version,
                status,
                entities);
    }

    final void setVersionStatusAddEntityAndCheck(final HttpProtocolVersion version,
                                                 final HttpStatus status,
                                                 final HttpEntity entity,
                                                 final HttpStatus expectedStatus,
                                                 final HttpEntity... expectedEntities) {
        this.setVersionStatusAddEntityAndCheck(version,
                status,
                Lists.of(entity),
                version,
                expectedStatus,
                expectedEntities);
    }

    final void setVersionStatusAddEntityAndCheck(final HttpProtocolVersion version,
                                                 final HttpStatus status,
                                                 final List<HttpEntity> entities,
                                                 final HttpProtocolVersion expectedVersion,
                                                 final HttpStatus expectedStatus,
                                                 final HttpEntity... expectedEntities) {
        this.setVersionStatusAddEntityAndCheck(this.createRequest(),
                version,
                status,
                entities,
                expectedVersion,
                expectedStatus,
                expectedEntities);
    }

    final void setVersionStatusAddEntityAndCheck(final HttpRequest request,
                                                 final HttpProtocolVersion version,
                                                 final HttpStatus status,
                                                 final HttpEntity... entities) {
        this.setVersionStatusAddEntityAndCheck(request,
                version,
                status,
                Lists.of(entities),
                version,
                status,
                entities);
    }

    final void setVersionStatusAddEntityAndCheck(final HttpRequest request,
                                                 final HttpProtocolVersion version,
                                                 final HttpStatus status,
                                                 final HttpEntity entity,
                                                 final HttpProtocolVersion expectedVersion,
                                                 final HttpStatus expectedStatus,
                                                 final HttpEntity... expectedEntities) {
        this.setVersionStatusAddEntityAndCheck(request,
                version,
                status,
                Lists.of(entity),
                expectedVersion,
                expectedStatus,
                expectedEntities);
    }

    final void setVersionStatusAddEntityAndCheck(final HttpRequest request,
                                                 final HttpProtocolVersion version,
                                                 final HttpStatus status,
                                                 final List<HttpEntity> entities,
                                                 final HttpProtocolVersion expectedVersion,
                                                 final HttpStatus expectedStatus,
                                                 final HttpEntity... expectedEntities) {
        final RecordingHttpResponse wrapped = RecordingHttpResponse.with();

        final R response = this.createResponse(request, wrapped);
        response.setVersion(version);
        response.setStatus(status);
        entities.forEach(response::addEntity);

        this.checkResponse(wrapped, request, expectedVersion, expectedStatus, expectedEntities);
    }

    static <T> Map<HttpHeaderName<?>, List<?>> map(final HttpHeaderName<T> header,
                                                   final T value) {
        return Maps.of(header, Lists.of(value));
    }

    static <T1, T2> Map<HttpHeaderName<?>, List<?>> map(final HttpHeaderName<T1> header1,
                                                        final T1 value1,
                                                        final HttpHeaderName<T2> header2,
                                                        final T2 value2) {
        return Maps.of(header1, Lists.of(value1), header2, Lists.of(value2));
    }

    static List<Object> list(final Object value) {
        return Lists.of(value);
    }

    static List<Object> list(final Object... values) {
        return Lists.of(values);
    }
}
