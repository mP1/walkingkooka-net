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

package walkingkooka.net.http.server;

import walkingkooka.collect.list.Lists;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.reflect.TypeNameTesting;

import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

public abstract class HttpServletRequestTestCase<T> implements ClassTesting2<T>,
    TypeNameTesting<T> {

    HttpServletRequestTestCase() {
        super();
    }

    // helper...........................................................................................................

    final Enumeration<String> enumeration(final String... values) {
        final Vector<String> enumeration = new Vector<>();
        enumeration.addAll(list(values));
        return enumeration.elements();
    }

    static <T> List<T> list(final T... values) {
        return Lists.of(values);
    }

    // ClassTesting.....................................................................................................

    @Override
    public final JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}
