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
final class HttpEntityHeaderMultiList extends HttpEntityHeaderList {

    /**
     * If the {@link List} is not a {@link HttpEntityHeaderMultiList} make a copy of using its values.
     */
    static HttpEntityHeaderMultiList with(final HttpHeaderName<?> header,
                                                 final Object... values) {
        if (values.length == 0) {
            throw new IllegalArgumentException(header + " expected at least one value");
        }

        for (final Object value : values) {
            header.checkValue(value);
        }

        return new HttpEntityHeaderMultiList(Arrays.copyOf(values, values.length));
    }

    /**
     * Private ctor.
     */
    private HttpEntityHeaderMultiList(final Object[] values) {
        super();
        this.values = values;
    }

    // AbstractList.....................................................................................................

    @Override
    public Object get(final int index) {
        final Object[] values = this.values;
        if (index < 0 || index > values.length) {
            throw new ArrayIndexOutOfBoundsException("Invalid index " + index + " not between 0 and " + values.length);
        }
        return Cast.to(values[index]);
    }

    @Override
    public int size() {
        return this.values.length;
    }

    final Object[] values;

    // HttpEntityHeaderList.............................................................................................

    /**
     * Creates a new {@link HttpEntityHeaderMultiList} appending the given value to the current array.
     */
    @Override //
    <T> HttpEntityHeaderMultiList append(final HttpHeaderName<T> header,
                                        final T value) {
        final int index = this.indexOf(value);
        return -1 != index ?
                this :
                this.append0(header, value);
    }


    private HttpEntityHeaderMultiList append0(final HttpHeaderName<?> header,
                                                 final Object value) {
        final Object[] old = this.values;
        final int length = old.length;

        final Object[] appended = new Object[length + 1];
        System.arraycopy(old, 0, appended, 0, length);
        appended[length] = value;

        if (false == header.isMultiple()) {
            throw new IllegalArgumentException(header + " does not support multiple entries: " + Arrays.toString(appended));
        }

        return new HttpEntityHeaderMultiList(appended);
    }

    /**
     * If the value exists creates a new {@link HttpEntityHeaderMultiList} with the value removed.
     */
    @Override
    HttpEntityHeaderMultiList removeValue(final Object value) {
        final int index = this.indexOf(value);
        return -1 == index ?
                this :
                this.values.length == 1 ?
                        null :
                        this.removeAndCompact(index);
    }

    private HttpEntityHeaderMultiList removeAndCompact(final int index) {
        final Object[] values = this.values;
        final int length = values.length;

        final Object[] removedValues = new Object[length - 1];
        System.arraycopy(values, 0, removedValues, 0, index);
        System.arraycopy(values, index + 1, removedValues, index, length - index - 1);

        return new HttpEntityHeaderMultiList(removedValues);
    }
}
