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

package walkingkooka.net.header;

import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.text.CaseSensitivity;

public abstract class HeaderName2TestCase<N extends HeaderName2<?>, C extends Comparable<C>>
    implements ClassTesting2<N>,
    HeaderNameTesting<N, C> {

    HeaderName2TestCase() {
        super();
    }

    @Override
    public final CaseSensitivity caseSensitivity() {
        return CaseSensitivity.INSENSITIVE;
    }

    // ParseStringTesting...............................................................................................

    @Override
    public final void testParseStringNullFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public final void testParseStringEmptyFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public final Object parseString(final String text) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final Class<? extends RuntimeException> parseStringFailedExpected(final Class<? extends RuntimeException> expected) {
        return expected;
    }

    @Override
    public final RuntimeException parseStringFailedExpected(final RuntimeException expected) {
        return expected;
    }

    // class............................................................................................................

    @Override
    public final JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
