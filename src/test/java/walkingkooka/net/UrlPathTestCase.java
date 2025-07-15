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

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class UrlPathTestCase<P extends UrlPath> implements ClassTesting2<UrlPath>, ToStringTesting<UrlPath> {

    UrlPathTestCase() {
        super();
    }

    @Test
    public final void testIsNormalized() {
        this.checkEquals(
            this.isNormalized(),
            this.createPath()
                .isNormalized()
        );
    }

    abstract boolean isNormalized();

    @Test
    public final void testIsRoot() {
        this.checkEquals(
            this.isRoot(),
            this.createPath().isRoot()
        );
    }

    abstract boolean isRoot();

    abstract P createPath();

    final void appendNameAndCheck(final UrlPathName name,
                                  final UrlPath expected,
                                  final String expectedPathString) {
        final P path = this.createPath();
        final UrlPath pathAppendedWithName = path.append(name);

        this.checkEquals(
            expected,
            pathAppendedWithName,
            () -> path + " append name " + name
        );

        this.checkEquals(
            expectedPathString,
            pathAppendedWithName.value(),
            () -> path + " append name " + name
        );
    }

    final UrlPathLeafNormalized normalized(final String path,
                                           final UrlPathName name) {
        return UrlPathLeafNormalized.withNormalized(
            path,
            name,
            Optional.of(
                this.createPath()
            )
        );
    }

    final UrlPathLeafUnnormalized unnormalized(final String path,
                                               final UrlPathName name) {
        return UrlPathLeafUnnormalized.withUnnormalized(
            path,
            name,
            Optional.of(
                this.createPath()
            )
        );
    }

    // pathAfter........................................................................................................

    @Test
    public final void testPathAfterWithNegativeStart() {
        final IllegalArgumentException thrown = assertThrows(
            IllegalArgumentException.class,
            () -> this.createPath()
                .pathAfter(-1)
        );

        this.checkEquals(
            "Invalid start -1 < 0",
            thrown.getMessage()
        );
    }

    @Test
    public final void testPathAfterWithZero() {
        final UrlPath path = this.createPath();

        assertSame(
            path,
            path.pathAfter(0)
        );
    }

    final void pathAfterAndCheck(final String path,
                                 final int start,
                                 final String expected) {
        this.pathAfterAndCheck(
            UrlPath.parse(path),
            start,
            UrlPath.parse(expected)
        );
    }

    final void pathAfterAndCheck(final UrlPath path,
                                 final int start,
                                 final UrlPath expected) {
        this.checkEquals(
            expected,
            path.pathAfter(start),
            () -> path + " pathAfter " + start
        );
    }

    final void pathAfterFailsAndCheck(final String path,
                                      final int start,
                                      final String expected) {
        this.pathAfterFailsAndCheck(
            UrlPath.parse(path),
            start,
            expected
        );
    }

    final void pathAfterFailsAndCheck(final UrlPath path,
                                      final int start,
                                      final String expected) {
        final IllegalArgumentException thrown = assertThrows(
            IllegalArgumentException.class,
            () -> path.pathAfter(start)
        );
        this.checkEquals(
            expected,
            thrown.getMessage(),
            () -> path + " pathAfter " + start
        );
    }

    // ToString.........................................................................................................

    @Test
    public final void testToString() {
        this.toStringAndCheck(
            this.createPath(),
            this.expectedToString()
        );
    }

    abstract String expectedToString();

    // Class............................................................................................................

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
