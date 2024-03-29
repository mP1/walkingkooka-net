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
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;

import static org.junit.jupiter.api.Assertions.assertSame;

public final class HttpEntityTest implements ClassTesting<HttpEntity> {

    @Test
    public void testEmptySingleton() {
        assertSame(HttpEntity.EMPTY, HttpEntity.EMPTY);
    }

    @Test
    public void testEmptySetBodyText() {
        final String text = "abc";
        final HttpEntity entity = HttpEntity.EMPTY.setBodyText(text);
        this.checkEquals(text, entity.bodyText());
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<HttpEntity> type() {
        return HttpEntity.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
