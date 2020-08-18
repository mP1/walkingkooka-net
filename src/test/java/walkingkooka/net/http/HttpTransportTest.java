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

package walkingkooka.net.http;

import org.junit.jupiter.api.Test;
import walkingkooka.ToStringTesting;
import walkingkooka.net.UrlScheme;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class HttpTransportTest implements ClassTesting2<HttpTransport>,
        ToStringTesting<HttpTransport> {

    @Test
    public void testUnsecured() {
        this.urlSchemeAndCheck(HttpTransport.UNSECURED, UrlScheme.HTTP);
    }

    @Test
    public void testSecured() {
        this.urlSchemeAndCheck(HttpTransport.SECURED, UrlScheme.HTTPS);
    }

    private void urlSchemeAndCheck(final HttpTransport transport,
                                   final UrlScheme expected) {
        assertEquals(expected,
                transport.urlScheme(),
                () -> "" + transport + " .urlScheme()");
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<HttpTransport> type() {
        return HttpTransport.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
