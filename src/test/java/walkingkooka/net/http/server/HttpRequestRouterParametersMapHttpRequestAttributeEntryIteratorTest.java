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
import walkingkooka.collect.iterator.IteratorTesting;
import walkingkooka.net.http.HttpMethod;
import walkingkooka.net.http.HttpProtocolVersion;
import walkingkooka.net.http.HttpTransport;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.reflect.TypeNameTesting;

import java.util.Iterator;
import java.util.Map.Entry;

public final class HttpRequestRouterParametersMapHttpRequestAttributeEntryIteratorTest implements ClassTesting2<HttpRequestRouterParametersMapHttpRequestAttributeEntryIterator>,
    IteratorTesting,
    ToStringTesting<HttpRequestRouterParametersMapHttpRequestAttributeEntryIterator>,
    TypeNameTesting<HttpRequestRouterParametersMapHttpRequestAttributeEntryIterator> {

    private final static HttpTransport TRANSPORT = HttpTransport.UNSECURED;
    private final static HttpMethod METHOD = HttpMethod.HEAD;
    private final static HttpProtocolVersion PROTOCOL = HttpProtocolVersion.VERSION_1_1;

    @Test
    public void testHasNextAndNextConsole() {
        this.iterateAndCheck(TRANSPORT, METHOD, PROTOCOL);
    }

    @Test
    public void testHasNextAndNextConsole2() {
        this.iterateAndCheck(HttpTransport.SECURED, HttpMethod.GET, HttpProtocolVersion.VERSION_1_0);
    }

    @Test
    public void testNextConsole() {
        this.iterateAndCheck(TRANSPORT, METHOD, PROTOCOL);
    }

    @Test
    public void testNextConsole2() {
        this.iterateAndCheck(HttpTransport.SECURED, HttpMethod.GET, HttpProtocolVersion.VERSION_1_0);
    }

    private void iterateAndCheck(final HttpTransport transport,
                                 final HttpMethod method,
                                 final HttpProtocolVersion version) {
        final HttpRequestRouterParametersMapHttpRequestAttributeEntryIterator iterator = this.createIterator(transport, method, version);

        this.hasNextCheckTrue(iterator, "iterator should have 3 entries");
        this.checkNext(iterator, HttpRequestAttributes.TRANSPORT, transport);

        this.hasNextCheckTrue(iterator, "iterator should have 2 entries");
        this.checkNext(iterator, HttpRequestAttributes.METHOD, method);

        this.hasNextCheckTrue(iterator, "iterator should have 1 entries");
        this.checkNext(iterator, HttpRequestAttributes.HTTP_PROTOCOL_VERSION, version);

        this.hasNextCheckFalse(iterator);
        this.nextFails(iterator);
    }

    private void checkNext(final HttpRequestRouterParametersMapHttpRequestAttributeEntryIterator iterator,
                           final HttpRequestAttributes<?> key,
                           final Object value) {
        final Entry<HttpRequestAttribute<?>, Object> entry = iterator.next();
        this.checkEquals(key, entry.getKey(), "key");
        this.checkEquals(value, entry.getValue(), "value");
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createIterator(), HttpRequestAttributes.TRANSPORT + "=" + TRANSPORT);
    }

    @Test
    public void testToString2() {
        final HttpRequestRouterParametersMapHttpRequestAttributeEntryIterator iterator = this.createIterator();
        iterator.next();
        this.toStringAndCheck(iterator, HttpRequestAttributes.METHOD + "=" + METHOD);
    }

    @Test
    public void testToStringWhenEmpty() {
        this.toStringAndCheck(this.createIterator(), "TRANSPORT=UNSECURED");
    }

    private HttpRequestRouterParametersMapHttpRequestAttributeEntryIterator createIterator() {
        return this.createIterator(TRANSPORT, METHOD, PROTOCOL);
    }

    private HttpRequestRouterParametersMapHttpRequestAttributeEntryIterator createIterator(final HttpTransport transport,
                                                                                           final HttpMethod method,
                                                                                           final HttpProtocolVersion version) {
        return HttpRequestRouterParametersMapHttpRequestAttributeEntryIterator.with(this.request(transport, method, version));
    }

    private HttpRequest request(final HttpTransport transport,
                                final HttpMethod method,
                                final HttpProtocolVersion version) {
        return new FakeHttpRequest() {

            @Override
            public HttpTransport transport() {
                return transport;
            }

            @Override
            public HttpMethod method() {
                return method;
            }

            @Override
            public HttpProtocolVersion protocolVersion() {
                return version;
            }
        };
    }

    @Override
    public Class<HttpRequestRouterParametersMapHttpRequestAttributeEntryIterator> type() {
        return HttpRequestRouterParametersMapHttpRequestAttributeEntryIterator.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }

    @Override
    public String typeNamePrefix() {
        return HttpRequestRouterParametersMap.class.getSimpleName();
    }

    @Override
    public String typeNameSuffix() {
        return Iterator.class.getSimpleName();
    }
}
