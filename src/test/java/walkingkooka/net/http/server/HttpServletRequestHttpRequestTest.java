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
import walkingkooka.net.Url;
import walkingkooka.net.header.HttpHeaderName;
import walkingkooka.net.http.HttpEntity;
import walkingkooka.net.http.HttpMethod;
import walkingkooka.net.http.HttpProtocolVersion;
import walkingkooka.net.http.HttpTransport;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class HttpServletRequestHttpRequestTest extends HttpServletRequestTestCase<HttpServletRequestHttpRequest>
    implements HttpRequestTesting<HttpServletRequestHttpRequest> {

    private final static HttpProtocolVersion PROTOCOL_VERSION = HttpProtocolVersion.VERSION_1_1;
    private final static HttpMethod METHOD = HttpMethod.POST;
    private final static String URL = "/path/file?abc=123";

    private final static HttpHeaderName<?> HEADER1 = HttpHeaderName.CONTENT_LENGTH;
    private final static Long Header1 = 111L;

    private final static HttpHeaderName<?> HEADER2 = HttpHeaderName.SERVER;
    private final static String Header2 = "Server2";

    private final static String PARAMETER1 = "parameter1";
    private final static String VALUE1A = "value1a";
    private final static String VALUE1B = "value1b";

    private final static String PARAMETER2 = "parameter2";
    private final static String VALUE2 = "value2";

    private final static String COOKIENAME = "cookie123";
    private final static String COOKIEVALUE = "cookievalue456";

    private final static byte[] BYTES = new byte[]{1, 2, 3};

    @Test
    public void testWithNullHttpServletRequestFails() {
        assertThrows(NullPointerException.class, () -> HttpServletRequestHttpRequest.with(null));
    }

    @Test
    public void testTransport() {
        assertSame(HttpTransport.SECURED,
            this.createRequest().transport());
    }

    @Test
    public void testProtocolVersion() {
        assertSame(PROTOCOL_VERSION,
            this.createRequest().protocolVersion());
    }

    @Test
    public void testMethod() {
        assertSame(METHOD,
            this.createRequest().method());
    }

    @Test
    public void testUrlMissingQueryString() {
        final String url = "/path1";
        this.checkEquals(Url.parseRelative(url),
            this.createRequest(url).url());
    }

    @Test
    public void testUrlWithQueryString() {
        this.checkEquals(Url.parseRelative(URL),
            this.createRequest().url());
    }

    @Test
    public void testUrlWithEmptyQueryString() {
        final String url = "/path1?";
        this.checkEquals(Url.parseRelative(url),
            this.createRequest(url).url());
    }

    @Test
    public void testHeaders() {
        this.checkEquals(Maps.of(HEADER1, list(Header1), HEADER2, list(Header2)),
            this.createRequest().headers());
    }

    @Test
    public void testBody() {
        assertArrayEquals(BYTES, this.createRequest().body());
        assertArrayEquals(BYTES, this.createRequest().body());
    }

    @Test
    public void testBodyTextCharsetHeaderAbsent() {
        this.checkEquals(new String(BYTES, HttpEntity.DEFAULT_BODY_CHARSET), this.createRequest().bodyText());
    }

    @Test
    public void testBodyTextCharsetHeaderPresent() {
        final Charset charset = StandardCharsets.UTF_16;
        final String text = "ABC123";

        this.checkEquals(
            text,
            HttpServletRequestHttpRequest.with(
                new FakeHttpServletRequest() {

                    @Override
                    public String getHeader(final String header) {
                        checkEquals(HttpHeaderName.CONTENT_TYPE.value(), header);
                        return "text/plain;charset=utf-16";
                    }

                    @Override
                    public Enumeration<String> getHeaders(final String header) {
                        checkEquals(HttpHeaderName.CONTENT_TYPE.value(), header);
                        return enumeration("text/plain;charset=utf-16");
                    }

                    @Override
                    public Map<String, String[]> getParameterMap() {
                        return Maps.empty();
                    }

                    @Override
                    public ServletInputStream getInputStream() {
                        final ByteArrayInputStream bytes = new ByteArrayInputStream(text.getBytes(charset));

                        return new ServletInputStream() {
                            @Override
                            public boolean isFinished() {
                                throw new UnsupportedOperationException();
                            }

                            @Override
                            public boolean isReady() {
                                throw new UnsupportedOperationException();
                            }

                            @Override
                            public void setReadListener(final ReadListener readListener) {
                                throw new UnsupportedOperationException();
                            }

                            @Override
                            public int read() {
                                return bytes.read();
                            }
                        };
                    }
                }).bodyText()
        );
    }

    @Test
    public void testParameters() {
        final Map<HttpRequestParameterName, List<String>> parameters = this.createRequest().parameters();
        this.checkEquals(Lists.of(VALUE1A, VALUE1B),
            parameters.get(HttpRequestParameterName.with(PARAMETER1)));
    }

    @Test
    public void testParameterValues() {
        this.checkEquals(Lists.of(VALUE1A, VALUE1B),
            this.createRequest().parameterValues(HttpRequestParameterName.with(PARAMETER1)));
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createRequest(),
            "SECURED\r\n" +
                "POST /path/file?abc=123 HTTP/1.1\r\n" +
                "Content-Length: 111\r\n" +
                "Server: Server2\r\n");
    }

    @Override
    public HttpServletRequestHttpRequest createRequest() {
        return this.createRequest(URL);
    }

    private HttpServletRequestHttpRequest createRequest(final String url) {
        final int queryStringStart = url.indexOf('?');

        return HttpServletRequestHttpRequest.with(new FakeHttpServletRequest() {

            @Override
            public boolean isSecure() {
                return true;
            }

            @Override
            public String getProtocol() {
                return PROTOCOL_VERSION.value();
            }

            @Override
            public String getMethod() {
                return METHOD.value();
            }

            @Override
            public String getRequestURI() {
                return -1 == queryStringStart ?
                    url :
                    url.substring(0, queryStringStart);
            }

            @Override
            public String getQueryString() {
                return -1 == queryStringStart ?
                    null :
                    url.substring(queryStringStart + 1);
            }

            @Override
            public String getHeader(final String header) {
                if (HEADER1.value().equals(header)) {
                    return "" + Header1;
                }
                if (HEADER2.value().equals(header)) {
                    return Header2;
                }
                return null;
            }

            @Override
            public Enumeration<String> getHeaders(final String header) {
                if (HEADER1.value().equals(header)) {
                    return enumeration("" + Header1);
                }
                if (HEADER2.value().equals(header)) {
                    return enumeration(Header2);
                }
                return null;
            }

            @Override
            public Enumeration<String> getHeaderNames() {
                return enumeration(HEADER1.value(), HEADER2.value());
            }

            @Override
            public Cookie[] getCookies() {
                return new Cookie[]{new Cookie(COOKIENAME, COOKIEVALUE)};
            }

            @Override
            public ServletInputStream getInputStream() {
                final ByteArrayInputStream bytes = new ByteArrayInputStream(BYTES);

                return new ServletInputStream() {
                    @Override
                    public boolean isFinished() {
                        throw new UnsupportedOperationException();
                    }

                    @Override
                    public boolean isReady() {
                        throw new UnsupportedOperationException();
                    }

                    @Override
                    public void setReadListener(final ReadListener readListener) {
                        throw new UnsupportedOperationException();
                    }

                    @Override
                    public int read() {
                        return bytes.read();
                    }
                };
            }

            @Override
            public Map<String, String[]> getParameterMap() {
                return Maps.of(PARAMETER1, array(VALUE1A, VALUE1B), PARAMETER2, array(VALUE2));
            }

            private String[] array(final String... values) {
                return values;
            }
        });
    }

    @Override
    public Class<HttpServletRequestHttpRequest> type() {
        return HttpServletRequestHttpRequest.class;
    }
}
