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
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.ToStringTesting;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.map.Maps;
import walkingkooka.net.RelativeUrl;
import walkingkooka.net.Url;
import walkingkooka.net.header.Cookie;
import walkingkooka.net.header.HttpHeaderName;
import walkingkooka.net.header.MediaType;
import walkingkooka.net.http.HttpEntity;
import walkingkooka.net.http.HttpMethod;
import walkingkooka.net.http.HttpProtocolVersion;
import walkingkooka.net.http.HttpTransport;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class HttpRequestValueTest implements ClassTesting2<HttpRequestValue>,
        HashCodeEqualsDefinedTesting2<HttpRequestValue>,
        ToStringTesting<HttpRequestValue> {

    private final static HttpMethod METHOD = HttpMethod.CONNECT;
    private final static HttpTransport TRANSPORT = HttpTransport.SECURED;
    private final static RelativeUrl URL = Url.parseRelative("/path1/path2?query3=value4");
    private final static HttpProtocolVersion PROTOCOL_VERSION = HttpProtocolVersion.VERSION_1_1;

    private final static Charset CHARSET = StandardCharsets.UTF_8;
    private final static Map<HttpHeaderName<?>, List<?>> HEADERS = Maps.of(HttpHeaderName.CONTENT_TYPE, Lists.of(MediaType.parse("text/html;charset=" + CHARSET)),
            HttpHeaderName.COOKIE, Lists.of(Cookie.parseClientHeader("cookie1=value1")));
    private final static String BODY_TEXT = "body-text-123";

    private final static HttpEntity ENTITY = HttpEntity.EMPTY
            .setHeaders(HEADERS)
            .setBodyText(BODY_TEXT);

    private final static HttpEntity[] ENTITIES = new HttpEntity[]{
            ENTITY
    };

    // with..............................................................................................................

    @Test
    public void testWithNullMethodFails() {
        withFails(null, TRANSPORT, URL, PROTOCOL_VERSION, ENTITIES);
    }

    @Test
    public void testWithNullTransportFails() {
        withFails(METHOD, null, URL, PROTOCOL_VERSION, ENTITIES);
    }

    @Test
    public void testWithNullUrlFails() {
        withFails(METHOD, TRANSPORT, null, PROTOCOL_VERSION, ENTITIES);
    }

    @Test
    public void testWithNullProtocolVersionFails() {
        withFails(METHOD, TRANSPORT, URL, null, ENTITIES);
    }

    @Test
    public void testWithNullEntitiesFails() {
        withFails(METHOD, TRANSPORT, URL, PROTOCOL_VERSION, null);
    }

    private void withFails(final HttpMethod method,
                           final HttpTransport transport,
                           final RelativeUrl url,
                           final HttpProtocolVersion protocolVersion,
                           final HttpEntity[] entities) {
        assertThrows(NullPointerException.class, () -> HttpRequestValue.with(method, transport, url, protocolVersion, entities));
    }

    @Test
    public void testMoreThanOneEntityFails() {
        assertThrows(IllegalArgumentException.class, () -> HttpRequestValue.with(METHOD, TRANSPORT, URL, PROTOCOL_VERSION, HttpEntity.EMPTY,
                HttpEntity.EMPTY));
    }

    @Test
    public void testWith() {
        final HttpRequestValue request = HttpRequestValue.with(METHOD, TRANSPORT, URL, PROTOCOL_VERSION, ENTITIES);

        assertEquals(METHOD, request.method(), "method");
        assertEquals(TRANSPORT, request.transport(), "transport");
        assertEquals(URL, request.url(), "url");
        assertEquals(PROTOCOL_VERSION, request.protocolVersion(), "protocolVersion");
        assertEquals(HEADERS, request.headers(), "headers");
        assertEquals(BODY_TEXT, request.bodyText(), "bodyText");
        assertArrayEquals(BODY_TEXT.getBytes(CHARSET), request.body(), "body");
    }

    @Test
    public void testWith2() {
        final HttpMethod method = HttpMethod.DELETE;
        final HttpTransport transport = HttpTransport.UNSECURED;
        final RelativeUrl url = Url.parseRelative("/different");
        final HttpProtocolVersion version = HttpProtocolVersion.VERSION_1_0;
        final HttpEntity entity = HttpEntity.EMPTY
                .addHeader(HttpHeaderName.CONTENT_LENGTH, 123L)
                .setBodyText("different-body-text");

        final HttpRequestValue request = HttpRequestValue.with(method, transport, url, version, entity);

        assertEquals(method, request.method(), "method");
        assertEquals(transport, request.transport(), "transport");
        assertEquals(url, request.url(), "url");
        assertEquals(version, request.protocolVersion(), "protocolVersion");
        assertEquals(entity.headers(), request.headers(), "headers");
        assertEquals(entity.bodyText(), request.bodyText(), "bodyText");
        assertArrayEquals(entity.body().value(), request.body(), "body");
    }

    @Test
    public void testParametersEmpty() {
        assertEquals(HttpRequest.NO_PARAMETERS, this.createObject().parameters(), "parameters");
    }

    // parameters.......................................................................................................

    @Test
    public void testParameterValueEmpty() {
        assertEquals(Lists.empty(), this.createObject().parameterValues(HttpRequestParameterName.with("param1")), "parameters");
    }

    // equals..........................................................................................................

    @Test
    public void testEqualsDifferentMethod() {
        final HttpMethod method = HttpMethod.POST;
        assertNotEquals(METHOD, method);
        this.checkNotEquals(HttpRequestValue.with(method, TRANSPORT, URL, PROTOCOL_VERSION, ENTITIES));
    }

    @Test
    public void testEqualsDifferentTransport() {
        final HttpTransport transport = HttpTransport.UNSECURED;
        assertNotEquals(TRANSPORT, transport);
        this.checkNotEquals(HttpRequestValue.with(METHOD, transport, URL, PROTOCOL_VERSION, ENTITIES));
    }

    @Test
    public void testEqualsDifferentUrl() {
        final RelativeUrl url = Url.parseRelative("/different");
        assertNotEquals(url, URL);
        this.checkNotEquals(HttpRequestValue.with(METHOD, TRANSPORT, url, PROTOCOL_VERSION, ENTITIES));
    }

    @Test
    public void testEqualsDifferentProtocolVersion() {
        final HttpProtocolVersion version = HttpProtocolVersion.VERSION_2;
        assertNotEquals(PROTOCOL_VERSION, version);
        this.checkNotEquals(HttpRequestValue.with(METHOD, TRANSPORT, URL, version, ENTITIES));
    }

    @Test
    public void testEqualsDifferentEntities() {
        this.checkNotEquals(HttpRequestValue.with(METHOD, TRANSPORT, URL, PROTOCOL_VERSION, HttpEntity.EMPTY.setBodyText("different-body-text")));
    }

    // ToString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createObject(),
                "SECURED\r\n" +
                        "CONNECT /path1/path2?query3=value4 HTTP/1.1\r\n" +
                        "Content-Type: text/html; charset=UTF-8\r\n" +
                        "Cookie: cookie1: value1;\r\n" +
                        "\r\n" +
                        "body-text-123");
    }

    // HashCodeEqualsDefinedTesting2....................................................................................

    @Override
    public HttpRequestValue createObject() {
        return HttpRequestValue.with(METHOD, TRANSPORT, URL, PROTOCOL_VERSION, ENTITIES);
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<HttpRequestValue> type() {
        return HttpRequestValue.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}
