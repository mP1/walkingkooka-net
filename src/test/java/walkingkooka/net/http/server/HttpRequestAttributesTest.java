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
import walkingkooka.collect.map.Maps;
import walkingkooka.net.RelativeUrl;
import walkingkooka.net.Url;
import walkingkooka.net.UrlPathName;
import walkingkooka.net.header.Cookie;
import walkingkooka.net.header.CookieName;
import walkingkooka.net.header.HttpHeaderName;
import walkingkooka.net.http.HttpMethod;
import walkingkooka.net.http.HttpProtocolVersion;
import walkingkooka.net.http.HttpTransport;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;

import java.util.Map;
import java.util.Optional;

public final class HttpRequestAttributesTest implements ClassTesting2<HttpRequestAttributes<?>> {

    @Test
    public void testParameterValueCookieName() {
        final CookieName name1 = CookieName.with("cookie111");
        this.parameterValueAndCheck(name1, Cookie.client(name1, "value111"));

        final CookieName name2 = CookieName.with("cookie222");
        this.parameterValueAndCheck(name2, Cookie.client(name2, "value222"));
    }

    @Test
    public void testParameterValueHeaderName() {
        this.parameterValueAndCheck(HttpHeaderName.CONTENT_LENGTH, 123L);
        this.parameterValueAndCheck(HttpHeaderName.SERVER, "Server 123");
    }

    @Test
    public void testParameterValueHttpMethod() {
        this.parameterValueAndCheck(HttpRequestAttributes.METHOD, HttpMethod.GET);
        this.parameterValueAndCheck(HttpRequestAttributes.METHOD, HttpMethod.POST);
    }

    @Test
    public void testParameterValueHttpProtocol() {
        this.parameterValueAndCheck(HttpRequestAttributes.HTTP_PROTOCOL_VERSION, HttpProtocolVersion.VERSION_1_0);
    }

    @Test
    public void testParameterValuePath() {
        this.parameterValueAndCheck(HttpRequestAttributes.pathComponent(2), UrlPathName.with("222"));
        this.parameterValueAndCheck(HttpRequestAttributes.pathComponent(3), UrlPathName.with("path-3"));
    }

    @Test
    public void testParameterValuePathComponentCount() {
        this.parameterValueAndCheck(new FakeHttpRequest() {
                @Override
                public RelativeUrl url() {
                    return Url.parseRelative("/path1/path2/path3/path4");
                }
            }.routerParameters(),
            HttpRequestAttributes.PATH_COMPONENT_COUNT,
            5);
    }

    @Test
    public void testParameterValueTransport() {
        this.parameterValueAndCheck(HttpRequestAttributes.TRANSPORT, HttpTransport.SECURED);
        this.parameterValueAndCheck(HttpRequestAttributes.TRANSPORT, HttpTransport.UNSECURED);
    }

    private <T> void parameterValueAndCheck(final HttpRequestAttribute<T> parameter,
                                            final T values) {
        this.parameterValueAndCheck(Maps.of(parameter, values),
            parameter,
            values);
    }

    private <T> void parameterValueAndCheck(final Map<HttpRequestAttribute<?>, Object> parameters,
                                            final HttpRequestAttribute<T> parameter,
                                            final T values) {
        this.checkEquals(Optional.of(values), parameter.parameterValue(parameters), parameters::toString);
    }

    @Override
    public Class<HttpRequestAttributes<?>> type() {
        return Cast.to(HttpRequestAttributes.class);
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
