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

import walkingkooka.collect.list.ImmutableList;
import walkingkooka.collect.list.ImmutableListDefaults;
import walkingkooka.net.header.HttpHeaderName;

import java.util.AbstractList;
import java.util.List;
import java.util.Objects;

/**
 * A read only {@link java.util.List} with operations to append and remove a value returning a new copy.
 * Note it is never
 */
abstract class HttpEntityHeaderList extends AbstractList<Object> implements ImmutableListDefaults<ImmutableList<Object>, Object> {

    /**
     * If the {@link List} is not a {@link HttpEntityHeaderList} make a copy of using its values.
     */
    static HttpEntityHeaderList one(final HttpHeaderName<?> header,
                                    final Object value) {
        return header.isMultiple() ?
            HttpEntityHeaderListMulti.with(header, value) :
            HttpEntityHeaderListOne.with(header, value);
    }

    /**
     * If the {@link List} is not a {@link HttpEntityHeaderList} make a copy of using its values.
     */
    static HttpEntityHeaderList copy(final HttpHeaderName<?> header,
                                     final List<?> values) {
        return values instanceof HttpEntityHeaderList ?
            checkHttpEntityHeaderList(header, (HttpEntityHeaderList) values) : /* lgtm [java/abstract-to-concrete-cast] */
            copyAndCreate(header, values.toArray());
    }

    /**
     * While taking a copy of the array checks each value using the {@link HttpHeaderName}
     */
    private static HttpEntityHeaderList checkHttpEntityHeaderList(final HttpHeaderName<?> header,
                                                                  final HttpEntityHeaderList values) {
        return header.isMultiple() == values.isMultipleHeaders() ?
            checkHttpEntityHeaderList0(header, values) : // already the correct HttpEntityHeaderList sub class, simply check values.
            copyAndCreate(header, values.toArray()); // wrong HttpEntityHeaderList copy and create another
    }

    /**
     * Checks that the values are correct values for the given header.
     */
    private static HttpEntityHeaderList checkHttpEntityHeaderList0(final HttpHeaderName<?> header,
                                                                   final HttpEntityHeaderList values) {
        values.forEach(header::checkValue);
        return values;
    }

    /**
     * While taking a copy of the array checks each value using the {@link HttpHeaderName}
     */
    private static HttpEntityHeaderList copyAndCreate(final HttpHeaderName<?> header,
                                                      final Object[] values) {
        return values.length == 0 ?
            null :
            header.isMultiple() ?
                HttpEntityHeaderListMulti.with(header, values) :
                HttpEntityHeaderListOne.with(header, values);
    }

    /**
     * Package private ctor to limit sub classing.
     */
    HttpEntityHeaderList(final HttpHeaderName<?> header) {
        super();
        this.header = header;
    }

    // HttpEntityHeaderList..............................................................................................

    /**
     * Only returns true if a list for holding multiple header values.
     */
    final boolean isMultipleHeaders() {
        return this instanceof HttpEntityHeaderListMulti;
    }

    // ImmutableListDefaults............................................................................................

    @Override
    public final void elementCheck(final Object value) {
        Objects.requireNonNull(value, "value");
    }

    @Override
    public final ImmutableList<Object> setElements(final List<Object> values) {
        final ImmutableList<Object> copy = copy(this.header, values);
        return this.equals(copy) ?
            this :
            copy;
    }

    private final HttpHeaderName<?> header;
}
