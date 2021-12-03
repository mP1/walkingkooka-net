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
import walkingkooka.collect.map.Maps;
import walkingkooka.net.RelativeUrl;
import walkingkooka.net.Url;
import walkingkooka.net.header.CacheControl;
import walkingkooka.net.header.HttpHeaderName;
import walkingkooka.net.header.MediaType;
import walkingkooka.net.http.HttpEntity;
import walkingkooka.net.http.HttpMethod;
import walkingkooka.net.http.HttpProtocolVersion;
import walkingkooka.net.http.HttpTransport;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.reflect.PublicStaticHelperTesting;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

public final class HttpRequestsTest implements PublicStaticHelperTesting<HttpRequests> {

    private final static HttpTransport TRANSPORT = HttpTransport.UNSECURED;
    private final static RelativeUrl URL = Url.parseRelative("/path/query1=value2");
    private final static HttpProtocolVersion VERSION = HttpProtocolVersion.VERSION_1_1;
    private final static Map<HttpHeaderName<?>, List<?>> HEADERS = Maps.of(HttpHeaderName.CONTENT_TYPE, Lists.of(MediaType.TEXT_PLAIN),
            HttpHeaderName.CACHE_CONTROL, Lists.of(CacheControl.parse("close")));
    private static final HttpEntity ENTITY_WITH_BODY = HttpEntity.EMPTY.setHeaders(HEADERS).setBodyText("body123");

    @Test
    public void testGet() {
        final HttpEntity entity = HttpEntity.EMPTY.setHeaders(HEADERS);
        this.check(HttpRequests.get(TRANSPORT, URL, VERSION, entity), HttpMethod.GET, entity);
    }

    @Test
    public void testDelete() {
        final HttpEntity entity = HttpEntity.EMPTY.setHeaders(HEADERS).setBodyText("body123");
        this.check(HttpRequests.delete(TRANSPORT, URL, VERSION, entity), HttpMethod.DELETE, entity);
    }

    @Test
    public void testPatch() {
        final HttpEntity entity = HttpEntity.EMPTY.setHeaders(HEADERS).setBodyText("body123");
        this.check(HttpRequests.patch(TRANSPORT, URL, VERSION, entity), HttpMethod.PATCH, entity);
    }

    @Test
    public void testPost() {
        final HttpEntity entity = HttpEntity.EMPTY.setHeaders(HEADERS).setBodyText("body123");
        this.check(HttpRequests.post(TRANSPORT, URL, VERSION, entity), HttpMethod.POST, entity);
    }

    @Test
    public void testPut() {
        this.check(HttpRequests.put(TRANSPORT, URL, VERSION, ENTITY_WITH_BODY), HttpMethod.PUT, ENTITY_WITH_BODY);
    }

    private void check(final HttpRequest request,
                       final HttpMethod method,
                       final HttpEntity... entites) {
        check(request, method, TRANSPORT, URL, VERSION, entites);
    }

    private void check(final HttpRequest request,
                       final HttpMethod method,
                       final HttpTransport transport,
                       final RelativeUrl url,
                       final HttpProtocolVersion version,
                       final HttpEntity... entites) {
        this.checkEquals(transport, request.transport(), "transport");
        this.checkEquals(url, request.url(), "url");
        this.checkEquals(version, request.protocolVersion(), "version");
        this.checkEquals(method, request.method(), "method");

        this.checkEquals(entites[0].headers(), request.headers(), "headers");
        this.checkEquals(entites[0].bodyText(), request.bodyText(), "bodyText");
    }

    // PublicStaticHelperTesting........................................................................................

    @Override
    public Class<HttpRequests> type() {
        return HttpRequests.class;
    }

    @Override
    public boolean canHavePublicTypes(final Method method) {
        return false;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
