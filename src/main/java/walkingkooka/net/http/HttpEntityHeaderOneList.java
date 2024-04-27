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

import java.util.List;

/**
 * A read only {@link List} with operations to append and remove a value returning a new copy.
 * Note it is never
 */
final class HttpEntityHeaderOneList extends HttpEntityHeaderList {

    /**
     * Creates a new {@link HttpEntityHeaderList} complaining if not given a single value.
     */
    static HttpEntityHeaderOneList with(final HttpHeaderName<?> header,
                                        final Object... values) {
        switch (values.length) {
            case 0:
                throw new IllegalArgumentException("Values must not be empty");
            case 1:
                break;
            default:
                throw new IllegalArgumentException("Expected only one value");
        }
        return new HttpEntityHeaderOneList(
                header,
                header.check(values[0])
        );
    }

    /**
     * Private ctor.
     */
    private HttpEntityHeaderOneList(final HttpHeaderName<?> header,
                                    final Object value) {
        super(header);
        this.value = value;
    }

    // AbstractList.....................................................................................................

    @Override
    public Object get(final int index) {
        if (0 != index) {
            throw new ArrayIndexOutOfBoundsException("Invalid index " + index + " not 0");
        }
        return this.value;
    }

    @Override
    public int size() {
        return 1;
    }

    final Object value;
}
