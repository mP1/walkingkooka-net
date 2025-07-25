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
import walkingkooka.ToStringBuilderOption;
import walkingkooka.collect.list.Lists;
import walkingkooka.net.RelativeUrl;
import walkingkooka.net.Url;
import walkingkooka.net.UrlQueryString;
import walkingkooka.net.header.HttpHeaderName;
import walkingkooka.net.http.HasHeaders;
import walkingkooka.net.http.HttpMethod;
import walkingkooka.net.http.HttpProtocolVersion;
import walkingkooka.net.http.HttpTransport;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * An adapter that presents a {@link HttpRequest} from a {@link HttpServletRequest}.
 */
final class HttpServletRequestHttpRequest implements HttpRequest {

    static HttpServletRequestHttpRequest with(final HttpServletRequest request) {
        Objects.requireNonNull(request, "request");

        return new HttpServletRequestHttpRequest(request);
    }

    private HttpServletRequestHttpRequest(final HttpServletRequest request) {
        super();
        this.request = request;
        this.headers = HttpServletRequestHttpRequestHeadersMap.with(request);
        this.parameters = HttpServletRequestHttpRequestParametersMap.with(request.getParameterMap());
    }

    @Override
    public HttpTransport transport() {
        return this.request.isSecure() ?
            HttpTransport.SECURED :
            HttpTransport.UNSECURED;
    }

    @Override
    public HttpMethod method() {
        if (null == this.method) {
            this.method = HttpMethod.with(this.request.getMethod());
        }
        return this.method;
    }

    private HttpMethod method;

    /**
     * Lazily parses the request URL into a {@link RelativeUrl}.
     */
    @Override
    public RelativeUrl url() {
        if (null == this.url) {
            final HttpServletRequest request = this.request;

            final RelativeUrl relativeUrl = Url.parseRelative(request.getRequestURI()); // getRequestURI does not include queryString
            final String queryString = request.getQueryString();

            this.url = null != queryString ?
                relativeUrl.setQuery(UrlQueryString.parse(queryString)) :
                relativeUrl;
        }
        return this.url;
    }

    private RelativeUrl url;

    @Override
    public HttpProtocolVersion protocolVersion() {
        return HttpProtocolVersion.with(this.request.getProtocol());
    }

    /**
     * The header map view
     */
    @Override
    public Map<HttpHeaderName<?>, List<?>> headers() {
        return this.headers;
    }

    private final HttpServletRequestHttpRequestHeadersMap headers;

    @Override
    public byte[] body() {
        if (null == this.body) {
            try {
                try (final ServletInputStream inputStream = this.request.getInputStream();
                     final ByteArrayOutputStream bytes = new ByteArrayOutputStream()) {
                    final byte[] buffer = new byte[4096];

                    for (; ; ) {
                        final int count = inputStream.read(buffer);
                        if (-1 == count) {
                            break;
                        }
                        bytes.write(buffer, 0, count);
                    }

                    this.body = bytes.toByteArray();
                }
            } catch (final IOException cause) {
                throw new HttpServerException(cause.getMessage(), cause);
            }
        }
        return this.body.clone();
    }

    private byte[] body;

    @Override
    public Map<HttpRequestParameterName, List<String>> parameters() {
        return this.parameters;
    }

    private final HttpServletRequestHttpRequestParametersMap parameters;

    @Override
    public List<String> parameterValues(final HttpRequestParameterName parameterName) {
        return this.parameters().get(parameterName);
    }

    private final HttpServletRequest request;

    /**
     * The {@link String} returned looks something similar to a http request in text form.
     */
    @Override
    public String toString() {
        final String eol = HasHeaders.LINE_ENDING.toString();

        final ToStringBuilder b = ToStringBuilder.empty();
        b.disable(ToStringBuilderOption.QUOTE);
        b.disable(ToStringBuilderOption.SKIP_IF_DEFAULT_VALUE);
        b.separator(" ");

        b.value(this.transport());
        b.append(eol);

        b.value(this.method()).value(this.url()).value(this.protocolVersion());
        b.append(eol);

        b.valueSeparator(eol);
        b.labelSeparator(": ");
        b.value(this.headers());
        b.append(eol);

        return b.build();
    }

    /**
     * Creates a read only {@link List} of values from the {@link Enumeration} of header value {@link String}.
     */
    static List<Object> toList(final HttpHeaderName<?> header,
                               final Enumeration<String> values) {
        final List<Object> list = Lists.array();
        while (values.hasMoreElements()) {
            list.add(
                header.parseValue(
                    values.nextElement()
                )
            );
        }

        return Lists.readOnly(list);
    }
}
