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
import walkingkooka.ToStringTesting;
import walkingkooka.collect.list.ListTesting2;
import walkingkooka.collect.list.Lists;
import walkingkooka.net.header.HttpHeaderName;
import walkingkooka.reflect.PackagePrivateClassTesting;
import walkingkooka.reflect.TypeNameTesting;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class HttpEntityHeaderValueListTest implements PackagePrivateClassTesting<HttpEntityHeaderValueList<String>>,
    ListTesting2<HttpEntityHeaderValueList<String>, String>,
    ToStringTesting,
    TypeNameTesting<HttpEntityHeaderValueList<String>> {

    private final static HttpHeaderName<String> HEADER = HttpHeaderName.SERVER;
    private final static String VALUE = "Server 123";

    @Test
    public void testWithListNullHeaderFails() {
        assertThrows(
            NullPointerException.class,
            () -> HttpEntityHeaderValueList.with(null)
        );
    }

    @Test
    public void testSetElementsListWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createList()
                .setElements(
                    Lists.of(
                        VALUE,
                        null
                    )
                )
        );
    }

    @Test
    public void testSetElementsListIncludesInvalidValueFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> this.createList()
                .setElements(
                    Cast.to(
                        Lists.of(
                            VALUE,
                            500
                        )
                    )
                )
        );
    }

    @Override
    public HttpEntityHeaderValueList<String> createList() {
        return new HttpEntityHeaderValueList<>(
            HEADER,
            Lists.of(VALUE)
        );
    }

    // class............................................................................................................

    @Override
    public Class<HttpEntityHeaderValueList<String>> type() {
        return Cast.to(HttpEntityHeaderValueList.class);
    }
}
