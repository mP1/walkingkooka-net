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

import walkingkooka.net.header.MediaType;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;

import java.util.Optional;

public abstract class HttpEntityTestCase<T> implements ClassTesting<T> {

    HttpEntityTestCase() {
        super();
    }

    // contentType......................................................................................................

    final void contentTypeAndCheck(final HttpEntity entity) {
        this.contentTypeAndCheck(
                entity,
                Optional.empty()
        );
    }

    final void contentTypeAndCheck(final HttpEntity entity,
                                   final MediaType expected) {
        this.contentTypeAndCheck(
                entity,
                Optional.of(expected)
        );
    }

    final void contentTypeAndCheck(final HttpEntity entity,
                                   final Optional<MediaType> expected) {
        this.checkEquals(
                expected,
                entity.contentType(),
                entity::toString
        );
    }

    // Class............................................................................................................

    @Override
    public final JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}
