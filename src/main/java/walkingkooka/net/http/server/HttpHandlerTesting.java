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
import walkingkooka.reflect.ClassTesting;
import walkingkooka.text.printer.TreePrintableTesting;

import static org.junit.jupiter.api.Assertions.assertThrows;

public interface HttpHandlerTesting<H extends HttpHandler> extends ClassTesting<H>,
    TreePrintableTesting {

    @Test
    default void testHandleWithNullRequestFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createHttpHandler()
                .handle(
                    null,
                    HttpResponses.fake()
                )
        );
    }

    @Test
    default void testHandleWithNullResponseFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createHttpHandler()
                .handle(
                    HttpRequests.fake(),
                    null
                )
        );
    }

    default void handleAndCheck(final HttpRequest request,
                                final HttpResponse expected) {
        this.handleAndCheck(
            this.createHttpHandler(),
            request,
            expected
        );
    }

    default void handleAndCheck(final H handler,
                                final HttpRequest request,
                                final HttpResponse expected) {
        final HttpResponse response = HttpResponses.recording();

        handler.handle(
            request,
            response
        );

        this.checkEquals(
            expected,
            response,
            request::toString
        );
    }

    H createHttpHandler();
}
