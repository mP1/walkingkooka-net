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

import walkingkooka.Cast;
import walkingkooka.net.header.HttpHeaderName;

import java.util.Arrays;
import java.util.List;

/**
 * A read only {@link List} with operations to append and remove a value returning a new copy for headers that accept
 * multiple header entries.
 */
final class HttpEntityHeaderListMulti extends HttpEntityHeaderList {

    /**
     * If the {@link List} is not a {@link HttpEntityHeaderListMulti} make a copy of using its values.
     */
    static HttpEntityHeaderListMulti with(final HttpHeaderName<?> header,
                                          final Object... values) {
        if (values.length == 0) {
            throw new IllegalArgumentException(header + " expected at least one value");
        }

        for (final Object value : values) {
            header.checkValue(value);
        }

        return new HttpEntityHeaderListMulti(
            header,
            Arrays.copyOf(values, values.length)
        );
    }

    /**
     * Private ctor.
     */
    private HttpEntityHeaderListMulti(final HttpHeaderName<?> header,
                                      final Object[] values) {
        super(header);
        this.values = values;
    }

    // AbstractList.....................................................................................................

    @Override
    public Object get(final int index) {
        final Object[] values = this.values;
        if (index < 0 || index >= values.length) {
            throw new IndexOutOfBoundsException("Invalid index " + index + " not between 0 and " + values.length);
        }
        return Cast.to(values[index]);
    }

    @Override
    public int size() {
        return this.values.length;
    }

    final Object[] values;
}
