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
import walkingkooka.net.email.EmailAddress;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class HasContentTypeTest implements ClassTesting2<HasContentType> {

    // json.............................................................................................................

    @Test
    public void testJsonWithNullTypeFails() {
        assertThrows(
            NullPointerException.class,
            () -> HasContentType.json(null)
        );
    }

    @Test
    public void testJson() {
        this.jsonAndCheck(
            EmailAddress.class,
            MediaType.parse("application/json+walkingkooka.net.email.EmailAddress")
        );
    }

    private void jsonAndCheck(final Class<?> type,
                              final MediaType expected) {
        this.checkEquals(
            expected,
            HasContentType.json(type)
        );
    }

    // class............................................................................................................

    @Override
    public Class<HasContentType> type() {
        return HasContentType.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}