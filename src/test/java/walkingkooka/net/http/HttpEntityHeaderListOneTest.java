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

package walkingkooka.net.http;

import org.junit.jupiter.api.Test;
import walkingkooka.Cast;
import walkingkooka.collect.list.Lists;
import walkingkooka.net.header.HeaderException;
import walkingkooka.net.header.HttpHeaderName;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class HttpEntityHeaderListOneTest extends HttpEntityHeaderListTestCase2<HttpEntityHeaderListOne> {

    private final static HttpHeaderName<String> HEADER = HttpHeaderName.SERVER;
    private final static String VALUE = "Server 123";

    @Test
    public void testWithTwo() {
        assertThrows(IllegalArgumentException.class, () -> HttpEntityHeaderListOne.with(HEADER, "Server1", "Server2"));
    }

    @Test
    public void testWithInvalidValue() {
        assertThrows(HeaderException.class, () -> HttpEntityHeaderListOne.with(HEADER, this));
    }

    @Test
    public void testWith() {
        final HttpEntityHeaderListOne list = HttpEntityHeaderListOne.with(HEADER, VALUE);
        assertSame(VALUE, list.value);
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createList(), Lists.of(VALUE).toString());
    }

    @Override
    HttpEntityHeaderListOne createHttpEntityHeaderList(final Object... values) {
        return HttpEntityHeaderListOne.with(HEADER, values);
    }

    @Override
    public HttpEntityHeaderListOne createList() {
        return HttpEntityHeaderListOne.with(HEADER, VALUE);
    }

    @Override
    public Class<HttpEntityHeaderListOne> type() {
        return Cast.to(HttpEntityHeaderListOne.class);
    }
}
