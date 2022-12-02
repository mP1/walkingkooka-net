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
import walkingkooka.ToStringTesting;
import walkingkooka.collect.list.ListTesting2;

import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class HttpEntityHeaderListTestCase2<L extends HttpEntityHeaderList> extends HttpEntityHeaderListTestCase<L>
        implements ListTesting2<L, Object>,
        ToStringTesting<L> {

    HttpEntityHeaderListTestCase2() {
        super();
    }

    @Test
    public final void testWithNoValues() {
        assertThrows(
                IllegalArgumentException.class,
                this::createHttpEntityHeaderList
        );
    }

    abstract L createHttpEntityHeaderList(final Object... values);
}
