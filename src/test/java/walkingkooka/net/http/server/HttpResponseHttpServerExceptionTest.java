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
import walkingkooka.net.http.HttpEntity;
import walkingkooka.net.http.HttpStatus;
import walkingkooka.net.http.HttpStatusCode;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.reflect.ThrowableTesting;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

final public class HttpResponseHttpServerExceptionTest implements ClassTesting<HttpResponseHttpServerException>,
        ThrowableTesting {

    @Test
    public void testWithNullStatusFails() {
        assertThrows(NullPointerException.class, () -> new HttpResponseHttpServerException(null, HttpResponseHttpServerException.NO_ENTITY));
    }

    @Test
    public void testWithNullEntityFails() {
        assertThrows(NullPointerException.class, () -> new HttpResponseHttpServerException(HttpStatusCode.OK.status(), null));
    }

    @Test
    public void testNewHttpStatus() {
        this.newAndCheck(
                HttpResponseHttpServerException.NO_ENTITY
        );
    }

    @Test
    public void testNewHttpStatusAndEmptyEntity() {
        this.newAndCheck(
                Optional.of(HttpEntity.EMPTY)
        );
    }

    @Test
    public void testNewHttpStatusAndEntity() {
        this.newAndCheck(
                Optional.of(
                        HttpEntity.EMPTY
                                .setBodyText("Hello")
                )
        );
    }

    private void newAndCheck(final Optional<HttpEntity> entity) {
        for (final HttpStatusCode code : HttpStatusCode.values()) {
            final HttpStatus status = code.status();

            final HttpResponseHttpServerException exception = new HttpResponseHttpServerException(status, entity);
            this.checkMessage(exception, status.message());
            this.checkCause(exception, null);

            assertSame(status, exception.status(), "status");
            assertSame(entity, exception.entity(), "entity");
        }
    }

    @Override
    public Class<HttpResponseHttpServerException> type() {
        return HttpResponseHttpServerException.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
