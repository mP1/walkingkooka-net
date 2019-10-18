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
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.map.Maps;
import walkingkooka.collect.set.Sets;
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
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.route.RouteMappings;
import walkingkooka.route.Router;
import walkingkooka.route.RouterTesting;

import java.util.Map;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class HttpRequestAttributeRoutingTest extends HttpRequestAttributeRoutingTestCase<HttpRequestAttributeRouting>
        implements RouterTesting {

    private final static String TARGET = "target123";

    // transport .......................................................................................................

    @Test
    public void testTransportNullFails() {
        assertThrows(NullPointerException.class, () -> this.createRouting().transport(null));
    }

    @Test
    public void testTransport() {
        final HttpRequestAttributeRouting routing = this.createRouting();

        final HttpTransport transport = HttpTransport.UNSECURED;
        final HttpRequestAttributeRouting routing2 = routing.transport(transport).transport(transport);
        assertNotSame(routing, routing2);

        this.checkTransports(routing2, HttpTransport.UNSECURED);
        this.checkMethods(routing2);
        this.checkAttributes(routing2, Maps.empty());

        this.check(routing);
    }

    @Test
    public void testTransportDifferent() {
        final HttpRequestAttributeRouting routing = this.createRouting();

        final HttpRequestAttributeRouting routing2 = routing.transport(HttpTransport.UNSECURED)
                .transport(HttpTransport.SECURED);
        assertNotSame(routing, routing2);

        this.checkTransports(routing2, HttpTransport.UNSECURED, HttpTransport.SECURED);
        this.checkMethods(routing2);
        this.checkAttributes(routing2, Maps.empty());

        this.check(routing);
    }

    // method ..........................................................................................................

    @Test
    public void testMethodNullFails() {
        assertThrows(NullPointerException.class, () -> this.createRouting().method(null));
    }

    @Test
    public void testMethod() {
        final HttpRequestAttributeRouting routing = this.createRouting();

        final HttpRequestAttributeRouting routing2 = routing.method(HttpMethod.GET);
        assertNotSame(routing, routing2);

        this.checkTransports(routing2);
        this.checkMethods(routing2, HttpMethod.GET);
        this.checkAttributes(routing2, Maps.empty());

        this.check(routing);
    }

    @Test
    public void testMethodMultiple() {
        final HttpRequestAttributeRouting routing = this.createRouting();

        final HttpRequestAttributeRouting routing2 = routing.method(HttpMethod.GET)
                .method(HttpMethod.POST)
                .method(HttpMethod.PUT);
        assertNotSame(routing, routing2);

        this.checkTransports(routing2);
        this.checkMethods(routing2, HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT);
        this.checkAttributes(routing2, Maps.empty());

        this.check(routing);
    }

    // protocol ........................................................................................................

    @Test
    public void testProtocolVersionNullFails() {
        assertThrows(NullPointerException.class, () -> this.createRouting().protocolVersion(null));
    }

    @Test
    public void testProtocolVersion() {
        final HttpRequestAttributeRouting routing = this.createRouting();
        final HttpProtocolVersion version = HttpProtocolVersion.VERSION_1_0;

        final HttpRequestAttributeRouting routing2 = routing.protocolVersion(version);
        assertNotSame(routing, routing2);

        this.checkTransports(routing2);
        this.checkMethods(routing2);
        this.checkAttributes(routing2, Maps.of(HttpRequestAttributes.HTTP_PROTOCOL_VERSION, Predicates.is(version)));

        this.check(routing);
    }

    @Test
    public void testProtocolVersionMultipleSame() {
        final HttpRequestAttributeRouting routing = this.createRouting();
        final HttpProtocolVersion version = HttpProtocolVersion.VERSION_1_0;

        final HttpRequestAttributeRouting routing2 = routing.protocolVersion(version)
                .protocolVersion(version);
        assertNotSame(routing, routing2);

        this.checkTransports(routing2);
        this.checkMethods(routing2);
        this.checkAttributes(routing2, Maps.of(HttpRequestAttributes.HTTP_PROTOCOL_VERSION, Predicates.is(version)));

        this.check(routing);
    }

    @Test
    public void testProtocolVersionMultipleDifferentFails() {
        final HttpProtocolVersion version = HttpProtocolVersion.VERSION_1_0;

        final HttpRequestAttributeRouting routing = this.createRouting()
                .protocolVersion(version);

        assertThrows(IllegalArgumentException.class, () -> routing.protocolVersion(HttpProtocolVersion.VERSION_1_1));

        this.checkTransports(routing);
        this.checkMethods(routing);
        this.checkAttributes(routing, Maps.of(HttpRequestAttributes.HTTP_PROTOCOL_VERSION, Predicates.is(version)));
    }

    // path UrlPathName, Predicate .....................................................................................

    @Test
    public void testPathNullFails() {
        assertThrows(NullPointerException.class, () -> this.createRouting().path(null));
    }

    @Test
    public void testPathPredicateEmptyPath() {
        final HttpRequestAttributeRouting routing = this.createRouting();
        assertSame(routing, routing.path(UrlPath.EMPTY));

        this.checkAttributes(routing, Maps.empty());
    }

    @Test
    public void testPathPredicatePath() {
        final HttpRequestAttributeRouting routing = this.createRouting();
        final HttpRequestAttributeRouting routing2 = routing.path(UrlPath.parse("/1a/2b"));
        assertNotSame(routing, routing2);

        this.checkTransports(routing2);
        this.checkMethods(routing2);
        this.checkAttributes(routing2,
                Maps.of(HttpRequestAttributes.pathComponent(1), Predicates.is(UrlPathName.with("1a")),
                        HttpRequestAttributes.pathComponent(2), Predicates.is(UrlPathName.with("2b"))));

        this.check(routing);
    }

    @Test
    public void testPathPredicatePathWithoutLeadingSlash() {
        final HttpRequestAttributeRouting routing = this.createRouting();
        final HttpRequestAttributeRouting routing2 = routing.path(UrlPath.parse("1a/2b"));
        assertNotSame(routing, routing2);

        this.checkTransports(routing2);
        this.checkMethods(routing2);
        this.checkAttributes(routing2,
                Maps.of(HttpRequestAttributes.pathComponent(1), Predicates.is(UrlPathName.with("1a")),
                        HttpRequestAttributes.pathComponent(2), Predicates.is(UrlPathName.with("2b"))));

        this.check(routing);
    }

    @Test
    public void testPathPredicatePathWithWildcard() {
        final HttpRequestAttributeRouting routing = this.createRouting();
        final HttpRequestAttributeRouting routing2 = routing.path(UrlPath.parse("1a/*/3c"));
        assertNotSame(routing, routing2);

        this.checkTransports(routing2);
        this.checkMethods(routing2);
        this.checkAttributes(routing2,
                Maps.of(HttpRequestAttributes.pathComponent(1), Predicates.is(UrlPathName.with("1a")),
                        HttpRequestAttributes.pathComponent(2), HttpRequestAttributeRoutingWildcardPredicate.INSTANCE,
                        HttpRequestAttributes.pathComponent(3), Predicates.is(UrlPathName.with("3c"))));

        this.check(routing);
    }

    // pathComponent ...................................................................................................

    @Test
    public void testPathComponentNegativePathComponentIndexFails() {
        assertThrows(IllegalArgumentException.class, () -> this.createRouting().pathComponent(-1, Predicates.fake()));
    }

    @Test
    public void testPathComponentNullPredicateFails() {
        assertThrows(NullPointerException.class, () -> this.createRouting().pathComponent(0, (Predicate<UrlPathName>) null));
    }

    @Test
    public void testPathComponentIndexNameRepeatedPathComponentDifferentNameFails() {
        assertThrows(IllegalArgumentException.class, () -> this.createRouting()
                .pathComponent(0, Predicates.fake())
                .pathComponent(0, Predicates.fake()));
    }

    @Test
    public void testPathComponent() {
        final Predicate<UrlPathName> predicate = Predicates.fake();

        final HttpRequestAttributeRouting routing = this.createRouting();
        final HttpRequestAttributeRouting routing2 = routing.pathComponent(0, predicate);
        assertNotSame(routing, routing2);

        this.checkTransports(routing2);
        this.checkMethods(routing2);
        this.checkAttributes(routing2,
                Maps.of(HttpRequestAttributes.pathComponent(0), predicate));

        this.check(routing);
    }

    @Test
    public void testPathComponentRepeated() {
        final Predicate<UrlPathName> predicate = Predicates.fake();

        final HttpRequestAttributeRouting routing = this.createRouting();
        final HttpRequestAttributeRouting routing2 = routing.pathComponent(0, predicate)
                .pathComponent(0, predicate);
        assertNotSame(routing, routing2);

        this.checkTransports(routing2);
        this.checkMethods(routing2);
        this.checkAttributes(routing2,
                Maps.of(HttpRequestAttributes.pathComponent(0), predicate));

        this.check(routing);
    }

    // header ..........................................................................................................

    @Test
    public void testHeaderNullHeaderNameFails() {
        assertThrows(NullPointerException.class, () -> this.createRouting().header(null, Predicates.fake()));
    }

    @Test
    public void testHeaderNullPredicateFails() {
        assertThrows(NullPointerException.class, () -> this.createRouting().header(HttpHeaderName.CONNECTION, null));
    }

    @Test
    public void testHeaderPredicate() {
        final HttpHeaderName<String> headerName = HttpHeaderName.CONNECTION;
        final Predicate<String> predicate = Predicates.is("value");

        final HttpRequestAttributeRouting routing = this.createRouting();
        final HttpRequestAttributeRouting routing2 = routing.header(headerName, predicate);
        assertNotSame(routing, routing2);

        this.checkTransports(routing2);
        this.checkMethods(routing2);
        this.checkAttributes(routing2, Maps.of(headerName, predicate));

        this.check(routing);
    }

    // cookie ..........................................................................................................

    @Test
    public void testCookieNullCookieNameFails() {
        assertThrows(NullPointerException.class, () -> this.createRouting().cookie(null, Predicates.fake()));
    }

    @Test
    public void testCookieNullPredicateFails() {
        assertThrows(NullPointerException.class, () -> this.createRouting().cookie(cookieName(), null));
    }

    @Test
    public void testCookieSamePredicate() {
        final CookieName cookieName = this.cookieName();
        final Predicate<ClientCookie> predicate = Predicates.is(Cookie.client(cookieName, "value456"));

        final HttpRequestAttributeRouting routing = this.createRouting();
        final HttpRequestAttributeRouting routing2 = routing.cookie(cookieName, predicate);
        assertNotSame(routing, routing2);

        this.checkTransports(routing2);
        this.checkMethods(routing2);
        this.checkAttributes(routing2, Maps.of(cookieName, predicate));

        this.check(routing);
    }

    // queryString .....................................................................................................

    @Test
    public void testQueryStringNullQueryStringFails() {
        assertThrows(NullPointerException.class, () -> this.createRouting().queryString(null, Predicates.fake()));
    }

    @Test
    public void testQueryStringNullPredicateFails() {
        assertThrows(NullPointerException.class, () -> this.createRouting().queryString(UrlQueryString.EMPTY, null));
    }

    @Test
    public void testQueryStringParameterWithMultipleValueFails() {
        assertThrows(IllegalArgumentException.class, () -> this.createRouting().queryString(UrlQueryString.with("a=1&a=2"), Predicates.fake()));
    }

    @Test
    public void testQueryString() {
        this.queryStringAndCheck("a1=b2",
                HttpRequestAttributeRouting.PARAMETER_WILDCARD,
                Maps.of(HttpRequestParameterName.with("a1"), HttpRequestAttributeRoutingParameterValuePredicate.with(Predicates.is("b2"))));
    }

    @Test
    public void testQueryStringValueUrlDecoded() {
        this.queryStringAndCheck("a1=b+2",
                HttpRequestAttributeRouting.PARAMETER_WILDCARD,
                Maps.of(HttpRequestParameterName.with("a1"), HttpRequestAttributeRoutingParameterValuePredicate.with(Predicates.is("b 2"))));
    }

    @Test
    public void testQueryStringWildcard() {
        this.queryStringAndCheck("a1=*",
                HttpRequestAttributeRouting.PARAMETER_WILDCARD,
                Maps.of(HttpRequestParameterName.with("a1"), HttpRequestAttributeRoutingParameterValuePredicate.with(Predicates.always())));
    }

    @Test
    public void testQueryStringMultipleParameters() {
        this.queryStringAndCheck("a1=b2&c3=d4",
                HttpRequestAttributeRouting.PARAMETER_WILDCARD,
                Maps.of(HttpRequestParameterName.with("a1"), HttpRequestAttributeRoutingParameterValuePredicate.with(Predicates.is("b2")),
                        HttpRequestParameterName.with("c3"), HttpRequestAttributeRoutingParameterValuePredicate.with(Predicates.is("d4"))));
    }

    @Test
    public void testQueryStringMultipleParametersIncludingWildcard() {
        this.queryStringAndCheck("a1=*&c3=d4",
                HttpRequestAttributeRouting.PARAMETER_WILDCARD,
                Maps.of(HttpRequestParameterName.with("a1"), HttpRequestAttributeRoutingParameterValuePredicate.with(Predicates.always()),
                        HttpRequestParameterName.with("c3"), HttpRequestAttributeRoutingParameterValuePredicate.with(Predicates.is("d4"))));
    }

    private void queryStringAndCheck(final String queryString,
                                     final Predicate<String> ignoreValue,
                                     final Map<?, ?> expected) {
        final HttpRequestAttributeRouting routing = this.createRouting();
        final HttpRequestAttributeRouting routing2 = routing.queryString(UrlQueryString.with(queryString), ignoreValue);

        assertNotSame(routing, routing2);

        this.checkTransports(routing2);
        this.checkMethods(routing2);
        this.checkAttributes(routing2, expected);
        this.check(routing);
    }

    // parameter .......................................................................................................

    @Test
    public void testParameterNullParameterNameFails() {
        assertThrows(NullPointerException.class, () -> this.createRouting().parameter(null, Predicates.fake()));
    }

    @Test
    public void testParameterNullPredicateFails() {
        assertThrows(NullPointerException.class, () -> this.createRouting().parameter(parameterName(), null));
    }

    @Test
    public void testParameter() {
        final HttpRequestParameterName parameterName = this.parameterName();
        final Predicate<String> predicate = Predicates.is("value");

        final HttpRequestAttributeRouting routing = this.createRouting();
        final HttpRequestAttributeRouting routing2 = routing.parameter(parameterName, predicate);
        assertNotSame(routing, routing2);

        this.checkTransports(routing2);
        this.checkMethods(routing2);
        this.checkAttributes(routing2, Maps.of(parameterName, HttpRequestAttributeRoutingParameterValuePredicate.with(predicate)));

        this.check(routing);
    }

    // parameterAndValue................................................................................................

    @Test
    public void testParameterAndValueNullParameterNameFails() {
        assertThrows(NullPointerException.class, () -> this.createRouting().parameterAndValue(null, "value123"));
    }

    @Test
    public void testParameterAndValueNullValueFails() {
        assertThrows(NullPointerException.class, () -> this.createRouting().parameterAndValue(parameterName(), null));
    }

    @Test
    public void testParameterAndValue() {
        final HttpRequestParameterName parameterName = this.parameterName();
        final String value = "value123";

        final HttpRequestAttributeRouting routing = this.createRouting();
        final HttpRequestAttributeRouting routing2 = routing.parameterAndValue(parameterName, value);
        assertNotSame(routing, routing2);

        this.checkTransports(routing2);
        this.checkMethods(routing2);
        this.checkAttributes(routing2, Maps.of(parameterName, HttpRequestAttributeRoutingParameterValuePredicate.with(Predicates.is(value))));

        this.check(routing);
    }

    // router ...........................................................................................................

    @Test
    public void testBuildEmptyFails() {
        assertThrows(BuilderException.class, () -> this.createRouting().build());
    }

    @Test
    public void testBuild() {
        final HttpRequestAttributeRouting routing = this.createRouting()
                .transport(HttpTransport.SECURED)
                .method(HttpMethod.GET)
                .protocolVersion(HttpProtocolVersion.VERSION_1_0);

        final Router<HttpRequestAttribute<?>, String> router = this.router(routing);

        final Map<HttpRequestAttribute<?>, Object> parameters = Maps.ordered();

        parameters.put(HttpRequestAttributes.TRANSPORT, HttpTransport.SECURED);
        this.routeFails(router, parameters);

        parameters.put(HttpRequestAttributes.METHOD, HttpMethod.GET);
        this.routeFails(router, parameters);

        parameters.put(HttpRequestAttributes.HTTP_PROTOCOL_VERSION, HttpProtocolVersion.VERSION_1_0);
        this.routeAndCheck2(router, parameters);
    }

    @Test
    public void testMethodsBuildAndRoute() {
        final HttpRequestAttributeRouting routing = this.createRouting()
                .transport(HttpTransport.SECURED)
                .method(HttpMethod.GET)
                .method(HttpMethod.POST)
                .protocolVersion(HttpProtocolVersion.VERSION_1_0);

        final Router<HttpRequestAttribute<?>, String> router = this.router(routing);

        final Map<HttpRequestAttribute<?>, Object> parameters = Maps.ordered();

        parameters.put(HttpRequestAttributes.TRANSPORT, HttpTransport.SECURED);
        this.routeFails(router, parameters);

        parameters.put(HttpRequestAttributes.METHOD, HttpMethod.GET);
        this.routeFails(router, parameters);

        parameters.put(HttpRequestAttributes.HTTP_PROTOCOL_VERSION, HttpProtocolVersion.VERSION_1_0);
        this.routeAndCheck2(router, parameters);

        parameters.put(HttpRequestAttributes.METHOD, HttpMethod.POST);
        this.routeAndCheck2(router, parameters);
    }

    @Test
    public void testPathBuildAndRoute() {
        final Router<HttpRequestAttribute<?>, String> router = this.router(this.createRouting()
                .path(UrlPath.parse("/path1/path2")));

        final Map<HttpRequestAttribute<?>, Object> parameters = Maps.ordered();

        final UrlPathName path1 = UrlPathName.with("path1");
        final UrlPathName path2 = UrlPathName.with("path2");

        parameters.put(HttpRequestAttributes.pathComponent(1), path1);
        this.routeFails(router, parameters);

        parameters.put(HttpRequestAttributes.pathComponent(2), path1);
        this.routeFails(router, parameters);

        parameters.put(HttpRequestAttributes.pathComponent(2), path2);
        this.routeAndCheck2(router, parameters);

        parameters.put(HttpRequestAttributes.pathComponent(1), path2);
        parameters.put(HttpRequestAttributes.pathComponent(2), path1);
        this.routeFails(router, parameters);
    }

    @Test
    public void testPathComponentBuildAndRoute() {
        final UrlPathName path1 = UrlPathName.with("path1");
        final UrlPathName path2 = UrlPathName.with("path2");

        final HttpRequestAttributeRouting routing = this.createRouting()
                .pathComponent(1, path1)
                .pathComponent(2, path2);

        final Router<HttpRequestAttribute<?>, String> router = this.router(routing);

        final Map<HttpRequestAttribute<?>, Object> parameters = Maps.ordered();

        parameters.put(HttpRequestAttributes.pathComponent(1), path1);
        this.routeFails(router, parameters);

        parameters.put(HttpRequestAttributes.pathComponent(2), path1);
        this.routeFails(router, parameters);

        parameters.put(HttpRequestAttributes.pathComponent(2), path2);
        this.routeAndCheck2(router, parameters);

        parameters.put(HttpRequestAttributes.pathComponent(1), path2);
        parameters.put(HttpRequestAttributes.pathComponent(2), path1);
        this.routeFails(router, parameters);
    }

    @Test
    public void testPathComponentCountBuildAndRoute() {
        final Router<HttpRequestAttribute<?>, String> router = this.router(this.createRouting()
                .pathComponentCount(i -> i == 3));

        final Map<HttpRequestAttribute<?>, Object> parameters = Maps.ordered();

        parameters.put(HttpRequestAttributes.PATH_COMPONENT_COUNT, 2);
        this.routeFails(router, parameters);

        parameters.put(HttpRequestAttributes.PATH_COMPONENT_COUNT, 3);
        this.routeAndCheck2(router, parameters);

        parameters.put(HttpRequestAttributes.PATH_COMPONENT_COUNT, 4);
        this.routeFails(router, parameters);
    }

    @Test
    public void testHeaderBuildAndRoute() {
        final HttpHeaderName<String> headerName1 = HttpHeaderName.with("header111").stringValues();
        final HttpHeaderName<String> headerName2 = HttpHeaderName.with("header222").stringValues();

        final HttpRequestAttributeRouting routing = this.createRouting()
                .header(headerName1, (c) -> null != c && c.contains("1"))
                .header(headerName2, (c) -> null != c && c.contains("2"));

        final Router<HttpRequestAttribute<?>, String> router = this.router(routing);

        final Map<HttpRequestAttribute<?>, Object> parameters = Maps.ordered();

        parameters.put(headerName1, "value1");
        this.routeFails(router, parameters);

        parameters.put(headerName2, "999");
        this.routeFails(router, parameters);

        parameters.put(headerName2, "value2");
        this.routeAndCheck2(router, parameters);
    }

    @Test
    public void testHeaderAndValueBuildAndRoute() {
        final HttpHeaderName<String> headerName1 = HttpHeaderName.with("header111").stringValues();
        final HttpHeaderName<String> headerName2 = HttpHeaderName.with("header222").stringValues();

        final String headerValue1 = "value1";
        final String headerValue2 = "value2";

        final HttpRequestAttributeRouting routing = this.createRouting()
                .headerAndValue(headerName1, headerValue1)
                .headerAndValue(headerName2, headerValue2);

        final Router<HttpRequestAttribute<?>, String> router = this.router(routing);

        final Map<HttpRequestAttribute<?>, Object> parameters = Maps.ordered();

        parameters.put(headerName1, headerValue1);
        this.routeFails(router, parameters);

        parameters.put(headerName2, "999");
        this.routeFails(router, parameters);

        parameters.put(headerName2, headerValue2);
        this.routeAndCheck2(router, parameters);
    }

    @Test
    public void testCookiesAndRouteCheck() {
        final CookieName cookieName1 = CookieName.with("cookie111");
        final CookieName cookieName2 = CookieName.with("cookie222");

        final HttpRequestAttributeRouting routing = this.createRouting()
                .cookie(cookieName1, (c) -> null != c && c.value().contains("1"))
                .cookie(cookieName2, (c) -> null != c && c.value().contains("2"));

        final Router<HttpRequestAttribute<?>, String> router = this.router(routing);

        final Map<HttpRequestAttribute<?>, Object> parameters = Maps.ordered();

        parameters.put(cookieName1, ClientCookie.client(cookieName1, "value1"));
        this.routeFails(router, parameters);

        parameters.put(cookieName2, ClientCookie.client(cookieName2, "999"));
        this.routeFails(router, parameters);

        parameters.put(cookieName2, ClientCookie.client(cookieName2, "value2"));
        this.routeAndCheck2(router, parameters);
    }

    @Test
    public void testParameterAndValueAndRouteCheck() {
        final HttpRequestParameterName parameter1 = HttpRequestParameterName.with("parameter1");
        final HttpRequestParameterName parameter2 = HttpRequestParameterName.with("parameter2");

        final HttpRequestAttributeRouting routing = this.createRouting()
                .parameterAndValue(parameter1, "value1")
                .parameterAndValue(parameter2, "value2");

        final Router<HttpRequestAttribute<?>, String> router = this.router(routing);

        final Map<HttpRequestAttribute<?>, Object> parameters = Maps.ordered();

        parameters.put(parameter1, Lists.of("value1"));
        this.routeFails(router, parameters);

        parameters.put(parameter2, Lists.of("999"));
        this.routeFails(router, parameters);

        parameters.put(parameter2, Lists.of("999", "value2"));
        this.routeAndCheck2(router, parameters);
    }

    @Test
    public void testUrlQueryPredicateAndRouteCheck() {
        final HttpRequestParameterName parameter1 = HttpRequestParameterName.with("parameter1");
        final HttpRequestParameterName parameter2 = HttpRequestParameterName.with("parameter2");

        final HttpRequestAttributeRouting routing = this.createRouting()
                .queryString(UrlQueryString.with("parameter1=a1&parameter2=b2"), HttpRequestAttributeRouting.PARAMETER_WILDCARD);

        final Router<HttpRequestAttribute<?>, String> router = this.router(routing);

        final Map<HttpRequestAttribute<?>, Object> parameters = Maps.ordered();

        parameters.put(parameter1, Lists.of("a1"));
        this.routeFails(router, parameters);

        parameters.put(parameter2, Lists.of("wrong"));
        this.routeFails(router, parameters);

        parameters.put(parameter2, Lists.of("b2", "wrong"));
        this.routeAndCheck2(router, parameters);
    }

    @Test
    public void testUrlQueryPredicateWithWildcardAndRouteCheck() {
        final HttpRequestParameterName parameter1 = HttpRequestParameterName.with("parameter1");
        final HttpRequestParameterName parameter2 = HttpRequestParameterName.with("parameter2");

        final HttpRequestAttributeRouting routing = this.createRouting()
                .queryString(UrlQueryString.with("parameter1=a1&parameter2=*"), HttpRequestAttributeRouting.PARAMETER_WILDCARD);

        final Router<HttpRequestAttribute<?>, String> router = this.router(routing);

        final Map<HttpRequestAttribute<?>, Object> parameters = Maps.ordered();

        parameters.put(parameter1, Lists.of("a1"));
        this.routeFails(router, parameters);

        parameters.put(parameter2, Lists.of("99"));
        this.routeAndCheck2(router, parameters);

        parameters.put(parameter1, Lists.of("wrong"));
        this.routeFails(router, parameters);
    }

    @Test
    public void testParameterPredicateAndRouteCheck() {
        final HttpRequestParameterName parameter1 = HttpRequestParameterName.with("parameter1");
        final HttpRequestParameterName parameter2 = HttpRequestParameterName.with("parameter2");

        final HttpRequestAttributeRouting routing = this.createRouting()
                .parameter(parameter1, (v) -> null != v && v.contains("1"))
                .parameter(parameter2, (v) -> null != v && v.contains("2"));

        final Router<HttpRequestAttribute<?>, String> router = this.router(routing);

        final Map<HttpRequestAttribute<?>, Object> parameters = Maps.ordered();

        parameters.put(parameter1, Lists.of("value1"));
        this.routeFails(router, parameters);

        parameters.put(parameter2, Lists.of("999"));
        this.routeFails(router, parameters);

        parameters.put(parameter2, Lists.of("999", "value2"));
        this.routeAndCheck2(router, parameters);
    }

    private Router<HttpRequestAttribute<?>, String> router(final HttpRequestAttributeRouting routing) {
        return RouteMappings.<HttpRequestAttribute<?>, String>empty()
                .add(routing.build(), TARGET)
                .router();
    }

    private void routeAndCheck2(final Router<HttpRequestAttribute<?>, String> router,
                               final Map<HttpRequestAttribute<?>, Object> parameters) {
        this.routeAndCheck(router, parameters, TARGET);
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createRouting()
                        .protocolVersion(HttpProtocolVersion.VERSION_1_0)
                        .method(HttpMethod.GET)
                        .pathComponent(0, UrlPathName.with("path1")),
                "GET PROTOCOL_VERSION=HTTP/1.0, path-0=path1");
    }

    @Test
    public void testToString2() {
        this.toStringAndCheck(this.createRouting()
                        .protocolVersion(HttpProtocolVersion.VERSION_1_0)
                        .method(HttpMethod.GET)
                        .method(HttpMethod.POST)
                        .path(UrlPath.parse("/a1/b2/c3/*")),
                "GET POST PROTOCOL_VERSION=HTTP/1.0, path-1=a1, path-2=b2, path-3=c3, path-4=*");
    }

    // helpers..........................................................................................................

    public HttpRequestAttributeRouting createRouting() {
        return HttpRequestAttributeRouting.empty();
    }

    private CookieName cookieName() {
        return CookieName.with("cookie123");
    }

    private HttpRequestParameterName parameterName() {
        return HttpRequestParameterName.with("parameter");
    }

    private void check(final HttpRequestAttributeRouting routing) {
        this.checkTransports(routing);
        this.checkMethods(routing);
        this.checkAttributes(routing, Maps.empty());
    }

    private void checkTransports(final HttpRequestAttributeRouting routing,
                                 final HttpTransport... transports) {
        assertEquals(Sets.of(transports),
                routing.transports,
                "transports");
    }

    private void checkMethods(final HttpRequestAttributeRouting routing,
                              final HttpMethod... methods) {
        assertEquals(Sets.of(methods),
                routing.methods,
                "methods");
    }

    private void checkAttributes(final HttpRequestAttributeRouting routing,
                                 final Map<?, ?> expected) {
        assertEquals(expected,
                routing.attributes,
                "attributes");
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<HttpRequestAttributeRouting> type() {
        return Cast.to(HttpRequestAttributeRouting.class);
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }

    // TypeNameTesting..................................................................................................

    @Override
    public final String typeNameSuffix() {
        return "Routing";
    }
}
