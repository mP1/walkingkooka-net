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
import walkingkooka.collect.list.Lists;
import walkingkooka.net.header.HttpHeaderName;

import java.util.AbstractList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * A read only {@link java.util.List} with operations to append and remove a value returning a new copy.
 */
final class HttpEntityHeaderValueList<T> extends AbstractList<T>
    implements ImmutableListDefaults<ImmutableList<T>, T> {

    static <T> HttpEntityHeaderValueList<T> with(final HttpHeaderName<T> header) {
        Objects.requireNonNull(header, "header");

        return new HttpEntityHeaderValueList<>(
            header,
            Lists.empty()
        );
    }

    /**
     * Private ctor to limit sub classing.
     */
    // @VisibleForTesting
    HttpEntityHeaderValueList(final HttpHeaderName<T> header,
                              final List<T> values) {
        super();
        this.header = header;
        this.values = values;
    }

    // ImmutableListDefaults............................................................................................

    @Override
    public T get(final int index) {
        return this.values.get(index);
    }

    @Override
    public int size() {
        return this.values.size();
    }

    final List<T> values;

    @Override
    public void elementCheck(final Object value) {
        Objects.requireNonNull(value, "value");
    }

    @Override
    public HttpEntityHeaderValueList<T> setElements(final Collection<T> values) {
        final HttpEntityHeaderValueList<T> httpEntityHeaderValueList;

        if (values instanceof HttpEntityHeaderValueList) {
            httpEntityHeaderValueList = (HttpEntityHeaderValueList<T>) values;
        } else {
            final HttpHeaderName<T> header = this.header;
            final List<T> copy = Lists.linkedList();

            for (final T value : values) {
                header.checkValue(value);

                copy.add(value);
            }

            httpEntityHeaderValueList = this.equals(copy) ?
                this :
                new HttpEntityHeaderValueList<>(
                    header,
                    copy
                );
        }

        return httpEntityHeaderValueList;
    }

    private final HttpHeaderName<T> header;
}
