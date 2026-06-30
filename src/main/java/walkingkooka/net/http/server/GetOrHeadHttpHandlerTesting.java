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
import walkingkooka.collect.list.Lists;
import walkingkooka.net.http.HttpMethod;

/**
 * Mixin interface for testing {@link GetOrHeadHttpHandler}
 */
public interface GetOrHeadHttpHandlerTesting<H extends GetOrHeadHttpHandler<C>, C extends HttpHandlerContext> extends HttpHandlerTesting<H, C> {

    @Test
    default void testHandleConnect() {
        this.handleMethodNotAllowedCheck(HttpMethod.CONNECT);
    }

    @Test
    default void testHandleDelete() {
        this.handleMethodNotAllowedCheck(HttpMethod.DELETE);
    }

    @Test
    default void testHandlePatch() {
        this.handleMethodNotAllowedCheck(HttpMethod.PATCH);
    }

    @Test
    default void testHandlePost() {
        this.handleMethodNotAllowedCheck(HttpMethod.POST);
    }

    @Test
    default void testHandleTrace() {
        this.handleMethodNotAllowedCheck(HttpMethod.POST);
    }

    private void handleMethodNotAllowedCheck(final HttpMethod method) {
        final HttpResponse expected = HttpResponses.recording();
        expected.setMethodNotAllowed(
            method,
            Lists.of(
                HttpMethod.GET,
                HttpMethod.HEAD
            )
        );

        this.handleAndCheck(
            this.createHttpHandler(),
            new FakeHttpRequest() {
                @Override
                public HttpMethod method() {
                    return method;
                }
            },
            this.createContext(),
            expected
        );
    }
}
