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

import org.junit.jupiter.api.Test;
import walkingkooka.Cast;
import walkingkooka.Value;
import walkingkooka.collect.list.ListTesting;
import walkingkooka.reflect.ClassTesting2;

import java.util.List;

/**
 * Base class for all {@link Header} in this package.
 */
public abstract class HeaderTestCase<V extends Header> implements ClassTesting2<V>,
        HeaderTesting<V>,
        ListTesting {

    HeaderTestCase() {
        super();
    }

    /**
     * {@link Header} that implement {@link Value} should have read only {@link List}.
     */
    @Test
    public final void testValueIfListReadOnly() {
        final V header = this.createHeader();
        if(header instanceof Value) {
            final Object value = ((Value<?>) header).value();
            if(value instanceof List) {
                final List<?> list = Cast.to(value);
                this.addFails(list, null);
            }
        }
    }

    @Override
    public final RuntimeException parseStringFailedExpected(final RuntimeException expected) {
        return new HeaderException(expected.getMessage(), expected);
    }

    @Override
    public final Class<? extends RuntimeException> parseStringFailedExpected(final Class<? extends RuntimeException> expected) {
        return HeaderException.class;
    }
}
