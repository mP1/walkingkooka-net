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
import walkingkooka.collect.list.Lists;
import walkingkooka.net.header.HttpHeaderName;
import walkingkooka.net.header.MediaType;
import walkingkooka.net.http.HttpEntity;
import walkingkooka.net.http.HttpStatus;
import walkingkooka.net.http.HttpStatusCode;
import walkingkooka.reflect.JavaVisibility;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class StacktraceDumpingHttpHandlerTest implements HttpHandlerTesting<StacktraceDumpingHttpHandler>,
    ToStringTesting<StacktraceDumpingHttpHandler> {

    private final static HttpStatus STATUS = HttpStatusCode.withCode(999).setMessage("Failed!");
    private final static Function<Throwable, HttpStatus> TRANSLATOR = (t) -> STATUS;

    @Test
    public void testWithNullHandlerFails() {
        assertThrows(
            NullPointerException.class,
            () -> StacktraceDumpingHttpHandler.with(
                null,
                TRANSLATOR
            )
        );
    }

    @Test
    public void testWithNullTranslatorFails() {
        assertThrows(
            NullPointerException.class,
            () -> StacktraceDumpingHttpHandler.with(
                (r, s) -> {
                },
                null
            )
        );
    }

    // handle...........................................................................................................

    @Test
    public void testHandleNothingThrown() {
        this.handled = false;

        final HttpRequest request = HttpRequests.fake();
        final HttpResponse response = HttpResponses.fake();

        StacktraceDumpingHttpHandler.with(
                (r, rr) -> {
                    assertSame(r, request);
                    assertSame(rr, response);

                    this.handled = true;
                },
                TRANSLATOR)
            .handle(
                request,
                response
            );

        this.checkEquals(true, this.handled);
    }

    private boolean handled;

    @Test
    public void testHandleThrown() {
        final HttpRequest request = HttpRequests.fake();
        final HttpResponse response = HttpResponses.recording();

        StacktraceDumpingHttpHandler.with(
            (r, rr) -> {
                assertSame(r, request);
                assertSame(rr, response);

                throw new UnsupportedOperationException();
            },
            TRANSLATOR
        ).handle(
            request,
            response
        );

        this.checkEquals(
            Optional.of(STATUS),
            response.status(),
            "status"
        );
        final HttpEntity entity = response.entity();

        this.checkEquals(
            Lists.of(MediaType.TEXT_PLAIN),
            entity.headers()
                .get(HttpHeaderName.CONTENT_TYPE),
            "content-type header"
        );

        final String body = entity.bodyText();
        this.checkEquals(true, body.contains(UnsupportedOperationException.class.getSimpleName()), () -> body);
    }

    @Override
    public StacktraceDumpingHttpHandler createHttpHandler() {
        return StacktraceDumpingHttpHandler.with(
            wrappedHttpHandler(),
            TRANSLATOR
        );
    }

    private HttpHandler wrappedHttpHandler() {
        return new HttpHandler() {
            @Override
            public void handle(final HttpRequest request,
                               final HttpResponse response) {
                Objects.requireNonNull(request, "request");
                Objects.requireNonNull(response, "response");

                throw new UnsupportedOperationException();
            }

            @Override
            public String toString() {
                return "Custom-to-String-123";
            }
        };
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        final HttpHandler wrapped = wrappedHttpHandler();
        this.toStringAndCheck(
            StacktraceDumpingHttpHandler.with(
                wrapped,
                TRANSLATOR
            ),
            wrapped + " " + TRANSLATOR
        );
    }

    // class............................................................................................................

    @Override
    public Class<StacktraceDumpingHttpHandler> type() {
        return StacktraceDumpingHttpHandler.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}
