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
import walkingkooka.collect.map.Maps;
import walkingkooka.collect.set.SetTesting2;
import walkingkooka.net.header.HttpHeaderName;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class HttpServletRequestHttpRequestHeadersMapEntrySetTest extends HttpServletRequestTestCase<HttpServletRequestHttpRequestHeadersMapEntrySet>
        implements SetTesting2<HttpServletRequestHttpRequestHeadersMapEntrySet, Entry<HttpHeaderName<?>, List<?>>> {

    private final static HttpHeaderName<?> HEADER1 = HttpHeaderName.CONTENT_LENGTH;
    private final static Object VALUE1 = 111L;

    private final static HttpHeaderName<?> HEADER2 = HttpHeaderName.SERVER;
    private final static Object VALUE2 = "Server2";

    @Test
    public void testAddFails() {
        this.addFails(this.createSet(),
                Maps.entry(HEADER1, list(VALUE1)));
    }

    @Test
    public void testIterator() {
        final Map<HttpHeaderName<?>, List<?>> entries = Maps.ordered();

        final HttpServletRequest request = this.request();
        for (final Entry<HttpHeaderName<?>, List<?>> e : HttpServletRequestHttpRequestHeadersMapEntrySet.with(request)) {
            entries.put(e.getKey(), e.getValue());
        }

        assertEquals(Maps.of(HEADER1, list(VALUE1), HEADER2, list(VALUE2)), entries, "iterator entries");
    }

    @Test
    public void testRemoveFails() {
        this.removeFails(this.createSet(),
                Maps.entry(HEADER1, list(VALUE1)));
    }

    @Test
    public void testSize() {
        this.sizeAndCheck(this.createSet(), 2);
    }

    @Override
    public HttpServletRequestHttpRequestHeadersMapEntrySet createSet() {
        return HttpServletRequestHttpRequestHeadersMapEntrySet.with(this.request());
    }

    private HttpServletRequest request() {
        return new FakeHttpServletRequest() {

            @Override
            public Enumeration<String> getHeaders(final String header) {
                if (HEADER1.value().equalsIgnoreCase(header)) {
                    return enumeration("" + VALUE1);
                }
                if (HEADER2.value().equalsIgnoreCase(header)) {
                    return enumeration("" + VALUE2);
                }

                throw new UnsupportedOperationException();
            }

            @Override
            public Enumeration<String> getHeaderNames() {
                return enumeration(HEADER1.value(), HEADER2.value());
            }
        };
    }

    @Override
    public Class<HttpServletRequestHttpRequestHeadersMapEntrySet> type() {
        return HttpServletRequestHttpRequestHeadersMapEntrySet.class;
    }
}
