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

public final class HttpEntityHeaderValueListOneTest extends HttpEntityHeaderValueListTestCase<HttpEntityHeaderValueListOne> {

    private final static HttpHeaderName<String> HEADER = HttpHeaderName.SERVER;
    private final static String VALUE = "Server 123";

    @Test
    public void testWithTwoValues() {
        assertThrows(
            IllegalArgumentException.class,
            () -> HttpEntityHeaderValueListOne.with(
                HEADER,
                "Server1",
                "Server2"
            )
        );
    }

    @Test
    public void testWithInvalidValue() {
        assertThrows(
            HeaderException.class,
            () -> HttpEntityHeaderValueListOne.with(
                HEADER,
                this
            )
        );
    }

    @Test
    public void testWith() {
        final HttpEntityHeaderValueListOne list = HttpEntityHeaderValueListOne.with(
            HEADER,
            VALUE
        );
        assertSame(
            VALUE,
            list.value
        );
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createList(), Lists.of(VALUE).toString());
    }

    @Override
    HttpEntityHeaderValueListOne createHttpEntityHeaderList(final Object... values) {
        return HttpEntityHeaderValueListOne.with(HEADER, values);
    }

    @Override
    public HttpEntityHeaderValueListOne createList() {
        return HttpEntityHeaderValueListOne.with(HEADER, VALUE);
    }

    @Override
    public Class<HttpEntityHeaderValueListOne> type() {
        return Cast.to(HttpEntityHeaderValueListOne.class);
    }
}
