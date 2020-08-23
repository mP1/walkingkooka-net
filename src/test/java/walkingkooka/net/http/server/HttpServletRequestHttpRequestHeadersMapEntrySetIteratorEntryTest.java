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
import walkingkooka.collect.map.EntryTesting;
import walkingkooka.net.header.HttpHeaderName;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class HttpServletRequestHttpRequestHeadersMapEntrySetIteratorEntryTest
        extends HttpServletRequestTestCase<HttpServletRequestHttpRequestHeadersMapEntrySetIteratorEntry>
        implements EntryTesting<HttpServletRequestHttpRequestHeadersMapEntrySetIteratorEntry, HttpHeaderName<?>, List<?>>,
        HashCodeEqualsDefinedTesting2<HttpServletRequestHttpRequestHeadersMapEntrySetIteratorEntry> {

    private final static String HEADER_NAME = HttpHeaderName.CONTENT_LENGTH.value();
    private final static Long CONTENT_LENGTH = 123L;

    @Test
    public void testGetKey() {
        this.getKeyAndCheck(this.createEntry(), HttpHeaderName.CONTENT_LENGTH);
    }

    @Test
    public void testGetKeyCached() {
        final HttpServletRequestHttpRequestHeadersMapEntrySetIteratorEntry entry = this.createEntry();
        assertSame(entry.getKey(), entry.getKey());
    }

    @Test
    public void testGetValue() {
        this.getValueAndCheck(this.createEntry(), list(CONTENT_LENGTH));
    }

    @Test
    public void testGetValueCached() {
        final HttpServletRequestHttpRequestHeadersMapEntrySetIteratorEntry entry = this.createEntry();
        assertSame(entry.getValue(), entry.getValue());
    }

    @Test
    public void testSetValueFails() {
        assertThrows(UnsupportedOperationException.class, () -> this.createEntry().setValue(list(99L)));
    }

    @Test
    public void testEqualsDifferentHeaderName() {
        final String value = "abc123";

        this.checkNotEquals(this.createEntry(HttpHeaderName.SERVER.value(), value),
                this.createEntry(HttpHeaderName.USER_AGENT.value(), value));
    }

    @Test
    public void testEqualsDifferentHeaderValue() {
        this.checkNotEquals(this.createEntry(HEADER_NAME, "" + (CONTENT_LENGTH + 999L)));
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createEntry().toString(), "Content-Length: [123]");
    }

    @Override
    public HttpServletRequestHttpRequestHeadersMapEntrySetIteratorEntry createEntry() {
        return HttpServletRequestHttpRequestHeadersMapEntrySetIteratorEntry.with(HEADER_NAME, request(HEADER_NAME, "" + CONTENT_LENGTH));
    }

    @Override
    public Class<HttpServletRequestHttpRequestHeadersMapEntrySetIteratorEntry> type() {
        return HttpServletRequestHttpRequestHeadersMapEntrySetIteratorEntry.class;
    }

    @Override
    public HttpServletRequestHttpRequestHeadersMapEntrySetIteratorEntry createObject() {
        return this.createEntry();
    }

    private HttpServletRequestHttpRequestHeadersMapEntrySetIteratorEntry createEntry(final String headerName,
                                                                                     final String value) {
        return HttpServletRequestHttpRequestHeadersMapEntrySetIteratorEntry.with(headerName, request(headerName, value));
    }

    private static HttpServletRequest request(final String headerName,
                                              final String value) {
        return new FakeHttpServletRequest() {
            @Override
            public Enumeration<String> getHeaders(final String header) {
                assertEquals(headerName, header, "header");

                final Vector<String> vector = new Vector<>();
                vector.add(value);
                return vector.elements();
            }
        };
    }

    @Override
    public String typeNamePrefix() {
        return HttpServletRequestHttpRequestHeadersMapEntrySetIterator.class.getSimpleName();
    }
}
