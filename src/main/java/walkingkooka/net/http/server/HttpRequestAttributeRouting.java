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

import walkingkooka.ToStringBuilder;
import walkingkooka.build.Builder;
import walkingkooka.build.BuilderException;
import walkingkooka.collect.map.Maps;
import walkingkooka.collect.set.Sets;
import walkingkooka.net.UrlParameterName;
import walkingkooka.net.UrlPath;
import walkingkooka.net.UrlPathName;
import walkingkooka.net.UrlQueryString;
import walkingkooka.net.header.ClientCookie;
import walkingkooka.net.header.CookieName;
import walkingkooka.net.header.HttpHeaderName;
import walkingkooka.net.http.HttpMethod;
import walkingkooka.net.http.HttpProtocolVersion;
import walkingkooka.net.http.HttpTransport;
import walkingkooka.predicate.Predicates;
import walkingkooka.routing.Routing;
import walkingkooka.text.CharSequences;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

/**
 * An immutable {@link Builder} that builds a {@link Map} of {@link HttpRequestAttribute attributes} to {@link Predicate predicates}.
 */
final public class HttpRequestAttributeRouting implements Builder<Map<HttpRequestAttribute<?>, Predicate<?>>> {

    /**
     * Creates an empty builder without any predicates.
     */
    public static HttpRequestAttributeRouting empty() {
        return EMPTY;
    }

    /**
     * An empty {@link HttpRequestAttributeRouting} singleton.
     */
    private final static HttpRequestAttributeRouting EMPTY = new HttpRequestAttributeRouting(Sets.empty(),
            Sets.empty(),
            map());

    /**
     * Private ctor use factory.
     */
    private HttpRequestAttributeRouting(final Set<HttpTransport> transports,
                                        final Set<HttpMethod> methods,
                                        final Map<HttpRequestAttribute<?>, Predicate<?>> attributes) {
        super();
        this.transports = transports;
        this.methods = methods;
        this.attributes = attributes;
    }

    // transport .......................................................................................................

    /**
     * Adds a requirement for a particular {@link HttpTransport}
     */
    public HttpRequestAttributeRouting transport(final HttpTransport transport) {
        Objects.requireNonNull(transport, "transport");

        final Set<HttpTransport> copy = Sets.ordered();
        copy.addAll(this.transports);
        return copy.add(transport) ?
                new HttpRequestAttributeRouting(copy, this.methods, this.attributes) :
                this;
    }

    // VisibleForTesting
    final Set<HttpTransport> transports;

    // protocol ........................................................................................................

    /**
     * Adds a requirement for a particular {@link HttpProtocolVersion}.
     */
    public HttpRequestAttributeRouting protocolVersion(final HttpProtocolVersion protocolVersion) {
        return this.addAttribute(HttpRequestAttributes.HTTP_PROTOCOL_VERSION, Predicates.is(protocolVersion));
    }

    // methods .........................................................................................................

    /**
     * Adds a requirement for a particular {@link HttpMethod}. Subsequent calls change the test to require any of the methods.
     */
    public HttpRequestAttributeRouting method(final HttpMethod method) {
        Objects.requireNonNull(method, "method");

        final Set<HttpMethod> copy = Sets.ordered();
        copy.addAll(this.methods);
        return copy.add(method) ?
                new HttpRequestAttributeRouting(this.transports, copy, this.attributes) :
                this;
    }

    // VisibleForTesting
    final Set<HttpMethod> methods;

    // path ............................................................................................................

    /**
     * Adds all path components with support for wildcards. A wildcard simply matches any path components value that is present.
     */
    public HttpRequestAttributeRouting path(final UrlPath path) {
        Objects.requireNonNull(path, "path");

        HttpRequestAttributeRouting that = this;

        int i = 0;
        for (UrlPathName name : path) {
            if (0 != i) {
                if (WILDCARD.equals(name)) {
                    that = that.pathComponent(i, HttpRequestAttributeRoutingWildcardPredicate.INSTANCE);
                } else {
                    that = that.pathComponent(i, name);
                }
            }
            i++;
        }

        return that;
    }

    /**
     * A wildcard path component.
     */
    private final static UrlPathName WILDCARD = UrlPathName.with("*");

    /**
     * Adds a requirement for a particular path component by name.
     */
    public HttpRequestAttributeRouting pathComponent(final int pathComponent,
                                                     final UrlPathName pathName) {
        return this.pathComponent(pathComponent, Predicates.is(pathName));
    }

    /**
     * Adds a {@link Predicate} for a path component. Note the {@link Predicate} must be <code>null</code> safe, because nulls
     * will be passed with the path component is absent from the request.
     */
    public HttpRequestAttributeRouting pathComponent(final int pathComponent,
                                                     final Predicate<UrlPathName> predicate) {
        if (pathComponent < 0) {
            throw new IllegalArgumentException("Invalid path component " + pathComponent + " < 0");
        }
        return this.addAttribute(HttpRequestAttributes.pathComponent(pathComponent), predicate);
    }

    // header ..........................................................................................................

    /**
     * Adds a requirement that a {@link HttpHeaderName} has the provided value.
     * An exception will be thrown if multiple different predicates for the same header are set.
     */
    public <H> HttpRequestAttributeRouting headerAndValue(final HttpHeaderName<H> header,
                                                          final H headerValue) {
        return this.header(header, Predicates.is(headerValue));
    }

    /**
     * Adds a requirement for a particular {@link HttpHeaderName}.<br>
     * An exception will be thrown if multiple different predicates for the same header are set.
     */
    public <H> HttpRequestAttributeRouting header(final HttpHeaderName<H> header,
                                                  final Predicate<H> headerValue) {
        Objects.requireNonNull(header, "header");
        Objects.requireNonNull(headerValue, "headerValue");

        return this.addAttribute(header, headerValue);
    }

    // cookies ..........................................................................................................

    /**
     * Adds a requirement for a particular {@link CookieName}.<br>
     * This method will throw an exception if called more than once for a particular cookie name.
     */
    public HttpRequestAttributeRouting cookie(final CookieName cookieName,
                                              final Predicate<ClientCookie> cookie) {
        Objects.requireNonNull(cookieName, "cookieName");
        Objects.requireNonNull(cookie, "cookie");

        return this.addAttribute(cookieName, cookie);
    }

    // parameters ......................................................................................................

    /**
     * Adds a parameter test for each {@link UrlQueryString#parameters()} with the limitation that parameters only
     * contain a single value at most. If the {@link Predicate} matches a parameter, then the test requirement is only
     * that the parameter exists.<br>
     * This is used as a shorthand for adding any sort of parameter and not limited to query string parameters.
     */
    public HttpRequestAttributeRouting queryString(final UrlQueryString queryString,
                                                   final Predicate<String> ignoreValue) {
        Objects.requireNonNull(queryString, "queryString");
        Objects.requireNonNull(ignoreValue, "ignoreValue");

        HttpRequestAttributeRouting that = this;

        for (Entry<UrlParameterName, List<String>> parameterAndValue : queryString.parameters().entrySet()) {
            final UrlParameterName parameter = parameterAndValue.getKey();
            final List<String> values = parameterAndValue.getValue();

            final int valueCount = values.size();
            final String value;
            switch (valueCount) {
                case 0:
                    value = null;
                    break;
                case 1:
                    value = values.get(0);
                    break;
                default:
                    throw new IllegalArgumentException("Parameter " + CharSequences.quote(parameter.value()) + " must not contain more than 1 value: " + queryString);
            }

            that = that.parameter(HttpRequestParameterName.with(parameter.value()),
                    ignoreValue.test(value) ?
                            PARAMETER_ANY_VALUE :
                            Predicates.is(value));
        }

        return that;
    }

    /**
     * Used to match parameter=* parameters which results in a requirement only that the parameter exists with the
     * value being irrelevant.
     */
    public final static Predicate<String> PARAMETER_WILDCARD = Predicates.is("*");

    /**
     * Used as the {@link Predicate} that will match any parameter value.
     */
    private final static Predicate<String> PARAMETER_ANY_VALUE = Predicates.always();

    // parameter .......................................................................................................

    /**
     * Adds a requirement that a {@link HttpRequestParameterName} has the provided value.
     * An exception will be thrown if multiple different predicates for the same parameter are set.
     */
    public HttpRequestAttributeRouting parameterAndValue(final HttpRequestParameterName parameter,
                                                         final String parameterValue) {
        return this.parameter(parameter, Predicates.is(parameterValue));
    }

    /**
     * Adds a requirement for a particular {@link HttpRequestParameterName}.<br>
     * An exception will be thrown if multiple different predicates for the same parameter are set.
     */
    public HttpRequestAttributeRouting parameter(final HttpRequestParameterName parameter,
                                                 final Predicate<String> parameterValue) {
        Objects.requireNonNull(parameter, "parameter");
        Objects.requireNonNull(parameterValue, "parameterValue");

        return this.addAttribute(parameter, HttpRequestAttributeRoutingParameterValuePredicate.with(parameterValue));
    }

    // helpers .........................................................................................................

    /**
     * Creates a new {@link HttpRequestAttributeRouting} if the new attribute is actually new or different.
     */
    private <A> HttpRequestAttributeRouting addAttribute(final HttpRequestAttribute<A> attribute,
                                                         final Predicate<A> value) {
        final Map<HttpRequestAttribute<?>, Predicate<?>> copy = map();
        copy.putAll(this.attributes);

        final Predicate<?> replace = copy.put(attribute, value);
        if (null != replace && false == value.equals(replace)) {
            throw new IllegalArgumentException("Attribute " + attribute + " replacement fails=" + value);
        }
        return value.equals(replace) ?
                this :
                new HttpRequestAttributeRouting(this.transports, this.methods, copy);
    }

    private static Map<HttpRequestAttribute<?>, Predicate<?>> map() {
        return Maps.ordered();
    }

    /**
     * Each added predicate will set this to false.
     */
    // VisibleForTesting
    final Map<HttpRequestAttribute<?>, Predicate<?>> attributes;

    // build ............................................................................................................

    /**
     * Builds a {@link Routing} from the captured {@link HttpRequestAttribute attributes}.
     */
    @Override
    public Map<HttpRequestAttribute<?>, Predicate<?>> build() throws BuilderException {
        final Set<HttpTransport> transports = this.transports;
        final Set<HttpMethod> methods = this.methods;
        final Map<HttpRequestAttribute<?>, Predicate<?>> attributes = this.attributes;

        if (transports.size() + methods.size() + attributes.size() == 0) {
            throw new BuilderException("Builder empty!");
        }

        final Map<HttpRequestAttribute<?>, Predicate<?>> keyToPredicates = Maps.ordered();
        keyToPredicates.putAll(this.attributes);

        if (false == transports.isEmpty()) {
            keyToPredicates.put(HttpRequestAttributes.TRANSPORT, Predicates.setContains(transports));
        }

        if (false == methods.isEmpty()) {
            keyToPredicates.put(HttpRequestAttributes.METHOD, Predicates.setContains(methods));
        }

        return keyToPredicates;
    }

    // toString.........................................................................................................

    @Override
    public String toString() {
        return ToStringBuilder.empty()
                .value(this.transports)
                .valueSeparator(" ")
                .value(this.methods)
                .valueSeparator(", ")
                .value(this.attributes).build();
    }
}
