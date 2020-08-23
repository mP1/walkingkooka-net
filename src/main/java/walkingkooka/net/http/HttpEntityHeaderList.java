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

import walkingkooka.net.header.HttpHeaderName;

import java.util.AbstractList;
import java.util.List;

/**
 * A read only {@link java.util.List} with operations to append and remove a value returning a new copy.
 * Note it is never
 */
abstract class HttpEntityHeaderList extends AbstractList<Object> {

    /**
     * If the {@link List} is not a {@link HttpEntityHeaderList} make a copy of using its values.
     */
    static HttpEntityHeaderList one(final HttpHeaderName<?> header,
                                    final Object value) {
        return header.isMultiple() ?
                HttpEntityHeaderMultiList.with(header, value) :
                HttpEntityHeaderOneList.with(header, value);
    }

    /**
     * If the {@link List} is not a {@link HttpEntityHeaderList} make a copy of using its values.
     */
    static HttpEntityHeaderList copy(final HttpHeaderName<?> header,
                                     final List<?> values) {
        return values instanceof HttpEntityHeaderList ?
                check(header, (HttpEntityHeaderList)values) :
                copy0(header, values.toArray());
    }

    /**
     * While taking a copy of the array checks each value using the {@link HttpHeaderName}
     */
    private static HttpEntityHeaderList check(final HttpHeaderName<?> header,
                                              final HttpEntityHeaderList values) {
        return header.isMultiple() == values.isMultipleHeaders() ?
                check0(header, values) : // already the correct HttpEntityHeaderList sub class, simply check values.
                copy0(header, values.toArray()); // wrong HttpEntityHeaderList copy and create another
    }

    /**
     * Checks that the values are correct values for the given header.
     */
    private static HttpEntityHeaderList check0(final HttpHeaderName<?> header,
                                              final HttpEntityHeaderList values) {
        values.forEach(header::checkValue);
        return values;
    }

    /**
     * While taking a copy of the array checks each value using the {@link HttpHeaderName}
     */
    private static HttpEntityHeaderList copy0(final HttpHeaderName<?> header,
                                              final Object[] values) {
        return header.isMultiple() ?
                HttpEntityHeaderMultiList.with(header, values) :
                HttpEntityHeaderOneList.with(header, values);
    }

    /**
     * Package private ctor to limit sub classing.
     */
    HttpEntityHeaderList() {
        super();
    }

    // HttpEntityHeaderList..............................................................................................

    /**
     * Only returns true if a list for holding multiple header values.
     */
    final boolean isMultipleHeaders() {
        return this instanceof HttpEntityHeaderMultiList;
    }

    /**
     * Creates a new {@link HttpEntityHeaderList} appending the given value to the current array.
     */
    abstract <T> HttpEntityHeaderList append(final HttpHeaderName<T> header,
                                             final T value);

    /**
     * Removes the value if it is present or null if empty.
     */
    abstract HttpEntityHeaderList removeValue(final Object value);
}
