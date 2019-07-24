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
import walkingkooka.build.BuilderException;
import walkingkooka.build.BuilderTesting;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.map.Maps;
import walkingkooka.net.UrlPath;
import walkingkooka.net.UrlPathName;
import walkingkooka.net.UrlQueryString;
import walkingkooka.net.header.ClientCookie;
import walkingkooka.net.header.Cookie;
import walkingkooka.net.header.CookieName;
import walkingkooka.net.header.HttpHeaderName;
import walkingkooka.net.http.HttpMethod;
import walkingkooka.net.http.HttpProtocolVersion;
import walkingkooka.net.http.HttpTransport;
import walkingkooka.predicate.Predicates;
import walkingkooka.routing.Router;
import walkingkooka.routing.RouterBuilder;
import walkingkooka.routing.Routing;
import walkingkooka.test.ClassTesting2;
import walkingkooka.type.JavaVisibility;

import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class HttpRequestAttributeRoutingBuilderTest implements ClassTesting2<HttpRequestAttributeRoutingBuilder<String>>,
        BuilderTesting<HttpRequestAttributeRoutingBuilder<String>,
                Routing<HttpRequestAttribute<?>, String>> {

    private final static String TARGET = "target123";

    @Test
    public void testWithNullTargetFails() {
        assertThrows(NullPointerException.class, () -> {
            HttpRequestAttributeRoutingBuilder.with(null);
        });
    }

    // transport ...............................................................................

    @Test
    public void testTransportNullFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createBuilder().transport(null);
        });
    }

    @Test
    public void testTransportMultipleSame() {
        final HttpRequestAttributeRoutingBuilder<String> builder = this.createBuilder();

        final HttpTransport transport = HttpTransport.UNSECURED;
        builder.transport(transport)
                .transport(transport);
    }

    @Test
    public void testTransportMultipleDifferentFails() {
        final HttpRequestAttributeRoutingBuilder<String> builder = this.createBuilder();
        builder.transport(HttpTransport.UNSECURED);

        assertThrows(IllegalArgumentException.class, () -> {
            builder.transport(HttpTransport.SECURED);
        });
    }

    // method ...............................................................................

    @Test
    public void testMethodNullFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createBuilder().method(null);
        });
    }

    @Test
    public void testMethodMultiple() {
        final HttpRequestAttributeRoutingBuilder<String> builder = this.createBuilder();
        builder.method(HttpMethod.GET)
                .method(HttpMethod.POST);
    }

    // path UrlPathName, Predicate ...............................................................................

    @Test
    public void testPathPredicateNegativePathComponentIndexFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createBuilder().path(null, Predicates.fake());
        });
    }

    @Test
    public void testPathPredicateNullPredicateFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createBuilder().path(UrlPath.EMPTY, null);
        });
    }

    @Test
    public void testPathPredicateEmptyPath() {
        final HttpRequestAttributeRoutingBuilder<?> builder = this.createBuilder();
        assertSame(builder, builder.path(UrlPath.EMPTY, Predicates.fake()));

        this.checkAttributeToPredicate(builder, Maps.empty());
    }

    @Test
    public void testPathPredicatePath() {
        final HttpRequestAttributeRoutingBuilder<?> builder = this.createBuilder();
        assertSame(builder, builder.path(UrlPath.parse("/1a/2b"), Predicates.never()));

        this.checkAttributeToPredicate(builder,
                Maps.of(HttpRequestAttributes.pathComponent(1), Predicates.is(UrlPathName.with("1a")),
                        HttpRequestAttributes.pathComponent(2), Predicates.is(UrlPathName.with("2b"))));
    }

    @Test
    public void testPathPredicatePathWithoutLeadingSlash() {
        final HttpRequestAttributeRoutingBuilder<?> builder = this.createBuilder();
        assertSame(builder, builder.path(UrlPath.parse("1a/2b"), Predicates.never()));

        this.checkAttributeToPredicate(builder,
                Maps.of(HttpRequestAttributes.pathComponent(1), Predicates.is(UrlPathName.with("1a")),
                        HttpRequestAttributes.pathComponent(2), Predicates.is(UrlPathName.with("2b"))));
    }

    @Test
    public void testPathPredicatePathWithWildcard() {
        final HttpRequestAttributeRoutingBuilder<?> builder = this.createBuilder();
        assertSame(builder, builder.path(UrlPath.parse("1a/*/3c"), HttpRequestAttributeRoutingBuilder.PATH_WILDCARD));

        this.checkAttributeToPredicate(builder,
                Maps.of(HttpRequestAttributes.pathComponent(1), Predicates.is(UrlPathName.with("1a")),
                        HttpRequestAttributes.pathComponent(3), Predicates.is(UrlPathName.with("3c"))));
    }

    // pathComponent .................................................................................

    @Test
    public void testPathComponentNegativePathComponentIndexFails() {
        assertThrows(IllegalArgumentException.class, () -> {
            this.createBuilder().pathComponent(-1, Predicates.fake());
        });
    }

    @Test
    public void testPathComponentNullPredicateFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createBuilder().pathComponent(0, (Predicate<UrlPathName>) null);
        });
    }

    @Test
    public void testPathComponentIndexNameRepeatedPathComponentDifferentNameFails() {
        assertThrows(IllegalArgumentException.class, () -> {
            this.createBuilder()
                    .pathComponent(0, Predicates.fake())
                    .pathComponent(0, Predicates.fake());

        });
    }

    @Test
    public void testPathComponentRepeatedPathComponentSamePredicate() {
        final Predicate<UrlPathName> predicate = Predicates.fake();
        this.createBuilder()
                .pathComponent(0, predicate)
                .pathComponent(0, predicate);
    }

    // protocol ...............................................................................

    @Test
    public void testProtocolVersionNullFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createBuilder().protocolVersion(null);
        });
    }

    @Test
    public void testProtocolVersionMultipleSame() {
        final HttpRequestAttributeRoutingBuilder<String> builder = this.createBuilder();
        final HttpProtocolVersion protocolVersion = HttpProtocolVersion.VERSION_1_0;
        builder.protocolVersion(protocolVersion)
                .protocolVersion(protocolVersion);
    }

    @Test
    public void testProtocolVersionMultipleDifferentFails() {
        final HttpRequestAttributeRoutingBuilder<String> builder = this.createBuilder();
        builder.protocolVersion(HttpProtocolVersion.VERSION_1_0);
        assertThrows(IllegalArgumentException.class, () -> {
            builder.protocolVersion(HttpProtocolVersion.VERSION_1_1);
        });
    }

    // header .................................................................................................

    @Test
    public void testHeaderNullHeaderNameFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createBuilder().header(null, Predicates.fake());
        });
    }

    @Test
    public void testHeaderNullPredicateFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createBuilder().header(HttpHeaderName.CONNECTION, (Predicate<String>) null);
        });
    }

    @Test
    public void testHeaderSamePredicate() {
        final HttpHeaderName<String> headerName = HttpHeaderName.CONNECTION;
        final Predicate<String> predicate = Predicates.is("value");
        this.createBuilder()
                .header(headerName, predicate)
                .header(headerName, predicate);
    }

    // cookie ...............................................................................

    @Test
    public void testCookieNullCookieNameFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createBuilder().cookie(null, Predicates.fake());
        });
    }

    @Test
    public void testCookieNullPredicateFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createBuilder().cookie(cookieName(), null);
        });
    }

    @Test
    public void testCookieSamePredicate() {
        final CookieName cookieName = this.cookieName();
        final Predicate<ClientCookie> predicate = Predicates.is(Cookie.client(cookieName, "value456"));
        this.createBuilder()
                .cookie(cookieName, predicate)
                .cookie(cookieName, predicate);
    }

    // queryString .................................................................................................

    @Test
    public void testQueryStringNullQueryStringFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createBuilder().queryString(null, Predicates.fake());
        });
    }

    @Test
    public void testQueryStringNullPredicateFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createBuilder().queryString(UrlQueryString.EMPTY, null);
        });
    }

    @Test
    public void testQueryStringParameterWithMultipleValueFails() {
        assertThrows(IllegalArgumentException.class, () -> {
            this.createBuilder().queryString(UrlQueryString.with("a=1&a=2"), Predicates.fake());
        });
    }

    @Test
    public void testQueryString() {
        final HttpRequestAttributeRoutingBuilder<?> builder = this.createBuilder();
        assertSame(builder, builder.queryString(UrlQueryString.with("a1=b2"), HttpRequestAttributeRoutingBuilder.PARAMETER_WILDCARD));

        this.checkAttributeToPredicate(builder,
                Maps.of(HttpRequestParameterName.with("a1"), HttpRequestAttributeRoutingBuilderParameterValuePredicate.with(Predicates.is("b2"))));
    }

    @Test
    public void testQueryStringValueUrlDecoded() {
        final HttpRequestAttributeRoutingBuilder<?> builder = this.createBuilder();
        assertSame(builder, builder.queryString(UrlQueryString.with("a1=b+2"), HttpRequestAttributeRoutingBuilder.PARAMETER_WILDCARD));

        this.checkAttributeToPredicate(builder,
                Maps.of(HttpRequestParameterName.with("a1"), HttpRequestAttributeRoutingBuilderParameterValuePredicate.with(Predicates.is("b 2"))));
    }

    @Test
    public void testQueryStringWildcard() {
        final HttpRequestAttributeRoutingBuilder<?> builder = this.createBuilder();
        assertSame(builder, builder.queryString(UrlQueryString.with("a1=*"), HttpRequestAttributeRoutingBuilder.PARAMETER_WILDCARD));

        this.checkAttributeToPredicate(builder,
                Maps.of(HttpRequestParameterName.with("a1"), HttpRequestAttributeRoutingBuilderParameterValuePredicate.with(Predicates.always())));
    }

    @Test
    public void testQueryStringMultipleParameters() {
        final HttpRequestAttributeRoutingBuilder<?> builder = this.createBuilder();
        assertSame(builder, builder.queryString(UrlQueryString.with("a1=b2&c3=d4"), HttpRequestAttributeRoutingBuilder.PARAMETER_WILDCARD));

        this.checkAttributeToPredicate(builder,
                Maps.of(HttpRequestParameterName.with("a1"), HttpRequestAttributeRoutingBuilderParameterValuePredicate.with(Predicates.is("b2")),
                        HttpRequestParameterName.with("c3"), HttpRequestAttributeRoutingBuilderParameterValuePredicate.with(Predicates.is("d4"))));
    }

    @Test
    public void testQueryStringMultipleParametersIncludingWildcard() {
        final HttpRequestAttributeRoutingBuilder<?> builder = this.createBuilder();
        assertSame(builder, builder.queryString(UrlQueryString.with("a1=*&c3=d4"), HttpRequestAttributeRoutingBuilder.PARAMETER_WILDCARD));

        this.checkAttributeToPredicate(builder,
                Maps.of(HttpRequestParameterName.with("a1"), HttpRequestAttributeRoutingBuilderParameterValuePredicate.with(Predicates.always()),
                        HttpRequestParameterName.with("c3"), HttpRequestAttributeRoutingBuilderParameterValuePredicate.with(Predicates.is("d4"))));
    }

    // parameter .................................................................................................

    @Test
    public void testParameterNullParameterNameFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createBuilder().parameter(null, Predicates.fake());
        });
    }

    @Test
    public void testParameterNullPredicateFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createBuilder().parameter(parameterName(), null);
        });
    }

    @Test
    public void testParameterSamePredicate() {
        final HttpRequestParameterName parameterName = this.parameterName();
        final Predicate<String> predicate = Predicates.is("value");
        this.createBuilder()
                .parameter(parameterName, predicate)
                .parameter(parameterName, predicate);
    }

    // build ...................................................................................................

    @Test
    public void testBuildEmptyFails() {
        assertThrows(BuilderException.class, () -> {
            this.createBuilder().build();
        });
    }

    @Test
    public void testBuild() {
        final HttpRequestAttributeRoutingBuilder<String> builder = this.createBuilder();
        assertSame(builder, builder.transport(HttpTransport.SECURED));
        assertSame(builder, builder.method(HttpMethod.GET));
        assertSame(builder, builder.protocolVersion(HttpProtocolVersion.VERSION_1_0));

        final Router<HttpRequestAttribute<?>, String> router = this.build(builder);

        final Map<HttpRequestAttribute<?>, Object> parameters = Maps.ordered();

        parameters.put(HttpRequestAttributes.TRANSPORT, HttpTransport.SECURED);
        this.routeFails(router, parameters);

        parameters.put(HttpRequestAttributes.METHOD, HttpMethod.GET);
        this.routeFails(router, parameters);

        parameters.put(HttpRequestAttributes.HTTP_PROTOCOL_VERSION, HttpProtocolVersion.VERSION_1_0);
        this.routeAndCheck(router, parameters);
    }

    @Test
    public void testMethods() {
        final HttpRequestAttributeRoutingBuilder<String> builder = this.createBuilder();
        assertSame(builder, builder.transport(HttpTransport.SECURED));
        assertSame(builder, builder.method(HttpMethod.GET).method(HttpMethod.POST));
        assertSame(builder, builder.protocolVersion(HttpProtocolVersion.VERSION_1_0));

        final Router<HttpRequestAttribute<?>, String> router = this.build(builder);

        final Map<HttpRequestAttribute<?>, Object> parameters = Maps.ordered();

        parameters.put(HttpRequestAttributes.TRANSPORT, HttpTransport.SECURED);
        this.routeFails(router, parameters);

        parameters.put(HttpRequestAttributes.METHOD, HttpMethod.GET);
        this.routeFails(router, parameters);

        parameters.put(HttpRequestAttributes.HTTP_PROTOCOL_VERSION, HttpProtocolVersion.VERSION_1_0);
        this.routeAndCheck(router, parameters);

        parameters.put(HttpRequestAttributes.METHOD, HttpMethod.POST);
        this.routeAndCheck(router, parameters);
    }

    @Test
    public void testPath() {
        final HttpRequestAttributeRoutingBuilder<String> builder = this.createBuilder();

        assertSame(builder, builder.path(UrlPath.parse("/path1/path2"), HttpRequestAttributeRoutingBuilder.PATH_WILDCARD));

        final Router<HttpRequestAttribute<?>, String> router = this.build(builder);

        final Map<HttpRequestAttribute<?>, Object> parameters = Maps.ordered();

        final UrlPathName path1 = UrlPathName.with("path1");
        final UrlPathName path2 = UrlPathName.with("path2");

        parameters.put(HttpRequestAttributes.pathComponent(1), path1);
        this.routeFails(router, parameters);

        parameters.put(HttpRequestAttributes.pathComponent(2), path1);
        this.routeFails(router, parameters);

        parameters.put(HttpRequestAttributes.pathComponent(2), path2);
        this.routeAndCheck(router, parameters);

        parameters.put(HttpRequestAttributes.pathComponent(1), path2);
        parameters.put(HttpRequestAttributes.pathComponent(2), path1);
        this.routeFails(router, parameters);
    }

    @Test
    public void testPathComponent() {
        final HttpRequestAttributeRoutingBuilder<String> builder = this.createBuilder();

        final UrlPathName path1 = UrlPathName.with("path1");
        assertSame(builder, builder.pathComponent(1, path1));

        final UrlPathName path2 = UrlPathName.with("path2");
        assertSame(builder, builder.pathComponent(2, path2));

        final Router<HttpRequestAttribute<?>, String> router = this.build(builder);

        final Map<HttpRequestAttribute<?>, Object> parameters = Maps.ordered();

        parameters.put(HttpRequestAttributes.pathComponent(1), path1);
        this.routeFails(router, parameters);

        parameters.put(HttpRequestAttributes.pathComponent(2), path1);
        this.routeFails(router, parameters);

        parameters.put(HttpRequestAttributes.pathComponent(2), path2);
        this.routeAndCheck(router, parameters);

        parameters.put(HttpRequestAttributes.pathComponent(1), path2);
        parameters.put(HttpRequestAttributes.pathComponent(2), path1);
        this.routeFails(router, parameters);
    }

    @Test
    public void testHeader() {
        final HttpRequestAttributeRoutingBuilder<String> builder = this.createBuilder();

        final HttpHeaderName<String> headerName1 = HttpHeaderName.with("header111").stringValues();
        final HttpHeaderName<String> headerName2 = HttpHeaderName.with("header222").stringValues();

        assertSame(builder, builder.header(headerName1, (c) -> null != c && c.contains("1")));
        assertSame(builder, builder.header(headerName2, (c) -> null != c && c.contains("2")));

        final Router<HttpRequestAttribute<?>, String> router = this.build(builder);

        final Map<HttpRequestAttribute<?>, Object> parameters = Maps.ordered();

        parameters.put(headerName1, "value1");
        this.routeFails(router, parameters);

        parameters.put(headerName2, "999");
        this.routeFails(router, parameters);

        parameters.put(headerName2, "value2");
        this.routeAndCheck(router, parameters);
    }

    @Test
    public void testHeaderAndValue() {
        final HttpRequestAttributeRoutingBuilder<String> builder = this.createBuilder();

        final HttpHeaderName<String> headerName1 = HttpHeaderName.with("header111").stringValues();
        final HttpHeaderName<String> headerName2 = HttpHeaderName.with("header222").stringValues();

        final String headerValue1 = "value1";
        final String headerValue2 = "value2";

        assertSame(builder, builder.headerAndValue(headerName1, headerValue1));
        assertSame(builder, builder.headerAndValue(headerName2, headerValue2));

        final Router<HttpRequestAttribute<?>, String> router = this.build(builder);

        final Map<HttpRequestAttribute<?>, Object> parameters = Maps.ordered();

        parameters.put(headerName1, headerValue1);
        this.routeFails(router, parameters);

        parameters.put(headerName2, "999");
        this.routeFails(router, parameters);

        parameters.put(headerName2, headerValue2);
        this.routeAndCheck(router, parameters);
    }

    @Test
    public void testCookies() {
        final HttpRequestAttributeRoutingBuilder<String> builder = this.createBuilder();

        final CookieName cookieName1 = CookieName.with("cookie111");
        final CookieName cookieName2 = CookieName.with("cookie222");

        assertSame(builder, builder.cookie(cookieName1, (c) -> null != c && c.value().contains("1")));
        assertSame(builder, builder.cookie(cookieName2, (c) -> null != c && c.value().contains("2")));

        final Router<HttpRequestAttribute<?>, String> router = this.build(builder);

        final Map<HttpRequestAttribute<?>, Object> parameters = Maps.ordered();

        parameters.put(cookieName1, ClientCookie.client(cookieName1, "value1"));
        this.routeFails(router, parameters);

        parameters.put(cookieName2, ClientCookie.client(cookieName2, "999"));
        this.routeFails(router, parameters);

        parameters.put(cookieName2, ClientCookie.client(cookieName2, "value2"));
        this.routeAndCheck(router, parameters);
    }

    @Test
    public void testParameterAndValue() {
        final HttpRequestAttributeRoutingBuilder<String> builder = this.createBuilder();

        final HttpRequestParameterName parameter1 = HttpRequestParameterName.with("parameter1");
        final HttpRequestParameterName parameter2 = HttpRequestParameterName.with("parameter2");

        assertSame(builder, builder.parameterAndValue(parameter1, "value1"));
        assertSame(builder, builder.parameterAndValue(parameter2, "value2"));

        final Router<HttpRequestAttribute<?>, String> router = this.build(builder);

        final Map<HttpRequestAttribute<?>, Object> parameters = Maps.ordered();

        parameters.put(parameter1, Lists.of("value1"));
        this.routeFails(router, parameters);

        parameters.put(parameter2, Lists.of("999"));
        this.routeFails(router, parameters);

        parameters.put(parameter2, Lists.of("999", "value2"));
        this.routeAndCheck(router, parameters);
    }

    @Test
    public void testUrlQueryPredicate() {
        final HttpRequestAttributeRoutingBuilder<String> builder = this.createBuilder();

        final HttpRequestParameterName parameter1 = HttpRequestParameterName.with("parameter1");
        final HttpRequestParameterName parameter2 = HttpRequestParameterName.with("parameter2");

        assertSame(builder, builder.queryString(UrlQueryString.with("parameter1=a1&parameter2=b2"), HttpRequestAttributeRoutingBuilder.PARAMETER_WILDCARD));

        final Router<HttpRequestAttribute<?>, String> router = this.build(builder);

        final Map<HttpRequestAttribute<?>, Object> parameters = Maps.ordered();

        parameters.put(parameter1, Lists.of("a1"));
        this.routeFails(router, parameters);

        parameters.put(parameter2, Lists.of("wrong"));
        this.routeFails(router, parameters);

        parameters.put(parameter2, Lists.of("b2", "wrong"));
        this.routeAndCheck(router, parameters);
    }

    @Test
    public void testUrlQueryPredicateWithWildcard() {
        final HttpRequestAttributeRoutingBuilder<String> builder = this.createBuilder();

        final HttpRequestParameterName parameter1 = HttpRequestParameterName.with("parameter1");
        final HttpRequestParameterName parameter2 = HttpRequestParameterName.with("parameter2");

        assertSame(builder, builder.queryString(UrlQueryString.with("parameter1=a1&parameter2=*"), HttpRequestAttributeRoutingBuilder.PARAMETER_WILDCARD));

        final Router<HttpRequestAttribute<?>, String> router = this.build(builder);

        final Map<HttpRequestAttribute<?>, Object> parameters = Maps.ordered();

        parameters.put(parameter1, Lists.of("a1"));
        this.routeFails(router, parameters);

        parameters.put(parameter2, Lists.of("99"));
        this.routeAndCheck(router, parameters);

        parameters.put(parameter1, Lists.of("wrong"));
        this.routeFails(router, parameters);
    }

    @Test
    public void testParameterPredicate() {
        final HttpRequestAttributeRoutingBuilder<String> builder = this.createBuilder();

        final HttpRequestParameterName parameter1 = HttpRequestParameterName.with("parameter1");
        final HttpRequestParameterName parameter2 = HttpRequestParameterName.with("parameter2");

        assertSame(builder, builder.parameter(parameter1, (v) -> null != v && v.contains("1")));
        assertSame(builder, builder.parameter(parameter2, (v) -> null != v && v.contains("2")));

        final Router<HttpRequestAttribute<?>, String> router = this.build(builder);

        final Map<HttpRequestAttribute<?>, Object> parameters = Maps.ordered();

        parameters.put(parameter1, Lists.of("value1"));
        this.routeFails(router, parameters);

        parameters.put(parameter2, Lists.of("999"));
        this.routeFails(router, parameters);

        parameters.put(parameter2, Lists.of("999", "value2"));
        this.routeAndCheck(router, parameters);
    }

    private Router<HttpRequestAttribute<?>, String> build(final HttpRequestAttributeRoutingBuilder<String> builder) {
        return RouterBuilder.<HttpRequestAttribute<?>, String>empty()
                .add(builder.build())
                .build();
    }

    private void routeAndCheck(final Router<HttpRequestAttribute<?>, String> routers,
                               final Map<HttpRequestAttribute<?>, Object> parameters) {
        assertEquals(Optional.of(TARGET),
                routers.route(parameters),
                "Routing of parameters=" + parameters + " failed");
    }

    private void routeFails(final Router<HttpRequestAttribute<?>, String> routers,
                            final Map<HttpRequestAttribute<?>, Object> parameters) {
        assertEquals(Optional.empty(),
                routers.route(parameters),
                "Routing of parameters=" + parameters + " should have failed");
    }

    // helpers.................................................................................................

    private void checkAttributeToPredicate(final HttpRequestAttributeRoutingBuilder<?> builder,
                                           final Map<?, ?> expected) {
        assertEquals(expected,
                builder.attributeToPredicate,
                "attributeToPredicate");
    }

    @Override
    public HttpRequestAttributeRoutingBuilder<String> createBuilder() {
        return HttpRequestAttributeRoutingBuilder.with(TARGET);
    }

    private CookieName cookieName() {
        return CookieName.with("cookie123");
    }

    private HttpRequestParameterName parameterName() {
        return HttpRequestParameterName.with("parameter");
    }

    @Override
    public Class<HttpRequestAttributeRoutingBuilder<String>> type() {
        return Cast.to(HttpRequestAttributeRoutingBuilder.class);
    }

    @Override
    public Class<Routing<HttpRequestAttribute<?>, String>> builderProductType() {
        return Cast.to(Routing.class);
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
