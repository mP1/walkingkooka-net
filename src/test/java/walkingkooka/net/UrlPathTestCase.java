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

package walkingkooka.net;

import org.junit.jupiter.api.Test;
import walkingkooka.Cast;
import walkingkooka.ToStringTesting;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class UrlPathTestCase<P extends UrlPath> implements ClassTesting2<UrlPath>, ToStringTesting<UrlPath> {

    UrlPathTestCase() {
        super();
    }

    @Test
    public final void testIsNormalized() {
        assertEquals(this.isNormalized(), this.createPath().isNormalized());
    }

    @Test
    public final void testIsRoot() {
        assertEquals(this.isRoot(), this.createPath().isRoot());
    }

    @Test
    public final void testToString() {
        this.toStringAndCheck(this.createPath(), this.expectedToString());
    }

    abstract P createPath();

    abstract boolean isNormalized();

    abstract boolean isRoot();

    abstract String expectedToString();

    final void appendNameAndCheck(final UrlPathName name,
                                  final UrlPath expected) {
        final P path = this.createPath();

        assertEquals(expected,
                path.append(name),
                () -> path + " append name " + name);
    }

    final UrlPathLeafNormalized normalized(final String path,
                                           final UrlPathName name) {
        return UrlPathLeafNormalized.withNormalized(path, name, Optional.of(this.createPath()));
    }

    final UrlPathLeafUnnormalized unnormalized(final String path,
                                               final UrlPathName name) {
        return UrlPathLeafUnnormalized.withUnnormalized(path, name, Optional.of(this.createPath()));
    }

    // ClassTesting.....................................................................................................

    @Override
    public final Class<UrlPath> type() {
        return Cast.to(this.urlPathType());
    }

    abstract Class<P> urlPathType();

    @Override
    public final JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}
