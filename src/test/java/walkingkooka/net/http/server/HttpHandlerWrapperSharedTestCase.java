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
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.reflect.TypeNameTesting;

import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class HttpHandlerWrapperSharedTestCase<H extends HttpHandlerWrapperShared<C>, C extends HttpHandlerContext>
    implements HttpHandlerTesting<H, C>,
    TypeNameTesting<H> {

    HttpHandlerWrapperSharedTestCase() {
        super();
    }

    @Test
    public final void testWithNullHttpHandlerFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createHttpHandler(null)
        );
    }

    @Override
    public final H createHttpHandler() {
        return this.createHttpHandler(
            HttpHandlers.fake()
        );
    }

    abstract H createHttpHandler(final HttpHandler<C> handler);

    @Override
    public final JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }

    @Override
    public final String typeNamePrefix() {
        return HttpHandlerWrapperShared.class.getSimpleName();
    }

    @Override
    public final String typeNameSuffix() {
        return "";
    }
}
