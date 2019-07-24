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

package walkingkooka.net.header;

import org.junit.jupiter.api.Test;

public final class ServerCookieHeaderValueHandlerTest extends
        NonStringHeaderValueHandlerTestCase<ServerCookieHeaderValueHandler, ServerCookie> {

    @Override
    public String typeNamePrefix() {
        return ServerCookie.class.getSimpleName();
    }

    @Test
    public void testServerCookie() {
        final String header = "cookie123=value456;";
        this.parseAndToTextAndCheck(header, Cookie.parseServerHeader(header));
    }

    @Override
    protected ServerCookieHeaderValueHandler handler() {
        return ServerCookieHeaderValueHandler.INSTANCE;
    }

    @Override
    HttpHeaderName<ServerCookie> name() {
        return HttpHeaderName.SET_COOKIE;
    }

    @Override
    String invalidHeaderValue() {
        return "///";
    }

    @Override
    ServerCookie value() {
        return Cookie.parseServerHeader("cookie1=value1;secure;");
    }

    @Override
    String valueType() {
        return this.valueType(ServerCookie.class);
    }

    @Override
    String handlerToString() {
        return "ServerCookie";
    }

    @Override
    public Class<ServerCookieHeaderValueHandler> type() {
        return ServerCookieHeaderValueHandler.class;
    }
}
