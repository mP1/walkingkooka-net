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
import walkingkooka.test.ClassTesting2;
import walkingkooka.type.JavaVisibility;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class ETagValidatorTest implements ClassTesting2<ETagValidator> {

    @Test
    public void testSetValue() {
        final ETagValidator validator = ETagValidator.WEAK;
        final String value = "1234567890ABCDEF";

        final ETag tag = validator.setValue(value);
        assertEquals(value, tag.value(), "value");
        assertEquals(validator, tag.validator(), "validator");
    }

    @Override
    public Class<ETagValidator> type() {
        return ETagValidator.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
