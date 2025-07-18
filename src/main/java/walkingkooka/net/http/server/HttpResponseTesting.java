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
import walkingkooka.net.http.HttpEntity;
import walkingkooka.net.http.HttpProtocolVersion;
import walkingkooka.net.http.HttpStatus;
import walkingkooka.reflect.TypeNameTesting;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertThrows;

public interface HttpResponseTesting<R extends HttpResponse> extends ToStringTesting<R>, TypeNameTesting<R> {

    @Test
    default void testSetVersionNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createResponse()
                .setVersion(null)
        );
    }

    @Test
    default void testSetStatusNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createResponse()
                .setStatus(null)
        );
    }

    @Test
    default void testAddEntityNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createResponse()
                .setEntity(null)
        );
    }

    R createResponse();

    default void checkResponse(final RecordingHttpResponse response,
                               final HttpRequest request,
                               final HttpProtocolVersion version,
                               final HttpStatus status,
                               final HttpEntity... entities) {
        final HttpResponse expected = HttpResponses.recording();
        expected.setVersion(version);
        expected.setStatus(status);

        Arrays.stream(entities)
            .forEach(expected::setEntity);

        this.checkEquals(expected,
            response,
            request::toString);
    }

    // TypeNameTesting .........................................................................................

    @Override
    default String typeNamePrefix() {
        return "";
    }

    @Override
    default String typeNameSuffix() {
        return HttpResponse.class.getSimpleName();
    }
}
