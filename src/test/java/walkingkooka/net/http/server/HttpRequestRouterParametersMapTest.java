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
import walkingkooka.collect.map.MapTesting2;
import walkingkooka.collect.map.Maps;
import walkingkooka.net.RelativeUrl;
import walkingkooka.net.Url;
import walkingkooka.net.UrlParameterName;
import walkingkooka.net.UrlPathName;
import walkingkooka.net.header.ClientCookie;
import walkingkooka.net.header.Cookie;
import walkingkooka.net.header.CookieName;
import walkingkooka.net.header.HttpHeaderName;
import walkingkooka.net.http.HttpMethod;
import walkingkooka.net.http.HttpProtocolVersion;
import walkingkooka.net.http.HttpTransport;
import walkingkooka.test.ClassTesting2;
import walkingkooka.type.JavaVisibility;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public final class HttpRequestRouterParametersMapTest implements ClassTesting2<HttpRequestRouterParametersMap>,
        MapTesting2<HttpRequestRouterParametersMap, HttpRequestAttribute<?>, Object> {

    @Test
    public void testContainsAndGet() {
        this.getAndCheck(HttpRequestAttributes.TRANSPORT, this.transport());
    }

    @Test
    public void testEntrySet() {
        final Iterator<Entry<HttpRequestAttribute<?>, Object>> iterator = this.createMap().entrySet().iterator();

        this.checkEntry(iterator, HttpRequestAttributes.TRANSPORT, this.transport());
        this.checkEntry(iterator, HttpRequestAttributes.METHOD, this.method());
        this.checkEntry(iterator, HttpRequestAttributes.HTTP_PROTOCOL_VERSION, this.protocolVersion());

        // paths
        int i = 0;
        for (UrlPathName name : this.url().path()) {
            this.checkEntry(iterator, HttpRequestAttributes.pathComponent(i), name);
            i++;
        }

        // query string parameters
        for (Entry<UrlParameterName, List<String>> nameAndValue : this.url().query().parameters().entrySet()) {
            this.checkEntry(iterator, nameAndValue.getKey(), nameAndValue.getValue());
            i++;
        }

        // headers
        for (Entry<HttpHeaderName<?>, Object> nameAndValue : this.headers().entrySet()) {
            final HttpHeaderName<?> header = nameAndValue.getKey();
            this.checkEntry(iterator,
                    header,
                    nameAndValue.getValue());
        }

        // cookies
        for (ClientCookie cookie : this.cookies()) {
            this.checkEntry(iterator,
                    cookie.name(),
                    cookie);
        }

        // parameters
        for (Entry<HttpRequestParameterName, List<String>> nameAndValue : this.parameters().entrySet()) {
            this.checkEntry(iterator,
                    nameAndValue.getKey(),
                    nameAndValue.getValue());
        }
    }

    private void checkEntry(final Iterator<Entry<HttpRequestAttribute<?>, Object>> iterator,
                            final HttpRequestAttribute<?> key,
                            final Object value) {
        assertTrue(iterator.hasNext(), "has next");
        this.checkEntry0(iterator.next(), key, value);
    }

    private void checkEntry0(final Entry<HttpRequestAttribute<?>, Object> entry,
                             final HttpRequestAttribute<?> key,
                             final Object value) {
        assertEquals(key, entry.getKey(), "entry key");
        assertEquals(value, entry.getValue(), "entry value");
    }

    @Test
    public void testGetPathInvalid() {
        this.getAndCheckAbsent(HttpRequestAttributes.pathComponent(3));
    }

    @Test
    public void testGetPath() {
        this.getPathAndCheck(1, "path-1a");
    }

    @Test
    public void testGetPath2() {
        this.getPathAndCheck(2, "file-2b");
    }

    private void getPathAndCheck(final int pathComponent,
                                 final String expected) {
        this.getPathAndCheck(this.createMap(),
                pathComponent,
                expected);
    }

    @Test
    public void testSize() {
        this.sizeAndCheck(this.createMap(), 11);
    }

    @Test
    public void testUrlNormalized() {
        final HttpRequestRouterParametersMap map = this.createMap(transport(),
                this.method(),
                Url.parseRelative("/a1/b2/./deleted-by-double-dot-after/../c3"),
                this.protocolVersion(),
                this.headers(),
                this.parameters());
        this.getPathAndCheck(map, 0, "");
        this.getPathAndCheck(map, 1, "a1");
        this.getPathAndCheck(map, 2, "b2");
        this.getPathAndCheck(map, 3, "c3");
    }

    private void getPathAndCheck(final HttpRequestRouterParametersMap map,
                                 final int pathComponent,
                                 final String expected) {
        this.getAndCheck(map, HttpRequestAttributes.pathComponent(pathComponent), UrlPathName.with(expected));
    }

    @Override
    public HttpRequestRouterParametersMap createMap() {
        return this.createMap(transport(),
                this.method(),
                this.url(),
                this.protocolVersion(),
                this.headers(),
                this.parameters());
    }

    private HttpTransport transport() {
        return HttpTransport.SECURED; // 1
    }

    private HttpMethod method() {
        return HttpMethod.GET; // 1
    }

    private RelativeUrl url() {
        return Url.parseRelative("/path-1a/file-2b?param1=value1A&param1=value1B&param2=value2"); // 3 + 2
    }

    private HttpProtocolVersion protocolVersion() {
        return HttpProtocolVersion.VERSION_1_1; // 1
    }

    private Map<HttpHeaderName<?>, Object> headers() {
        return Maps.of(HttpHeaderName.CONNECTION, "Close", HttpHeaderName.COOKIE, this.cookies());
    }

    private List<ClientCookie> cookies() {
        return Lists.of(Cookie.client(CookieName.with("cookie123"), "cookie-value-456")); // 1
    }

    private Map<HttpRequestParameterName, List<String>> parameters() {
        return HttpRequest.NO_PARAMETERS;
    }

    private HttpRequestRouterParametersMap createMap(final HttpTransport transport,
                                                     final HttpMethod method,
                                                     final RelativeUrl url,
                                                     final HttpProtocolVersion protocolVersion,
                                                     final Map<HttpHeaderName<?>, Object> headers,
                                                     final Map<HttpRequestParameterName, List<String>> parameters) {
        return HttpRequestRouterParametersMap.with(new FakeHttpRequest() {

            @Override
            public HttpTransport transport() {
                return transport;
            }

            @Override
            public HttpMethod method() {
                return method;
            }

            @Override
            public RelativeUrl url() {
                return url;
            }

            @Override
            public HttpProtocolVersion protocolVersion() {
                return protocolVersion;
            }

            @Override
            public Map<HttpHeaderName<?>, Object> headers() {
                return headers;
            }

            @Override
            public Map<HttpRequestParameterName, List<String>> parameters() {
                return parameters;
            }

            @Override
            public String toString() {
                return transport + " " + method + " " + url + " " + protocolVersion + " " + headers + " " + parameters;
            }
        });
    }

    @Override
    public Class<HttpRequestRouterParametersMap> type() {
        return HttpRequestRouterParametersMap.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}
