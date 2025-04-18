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
import walkingkooka.reflect.IsMethodTesting;
import walkingkooka.reflect.JavaVisibility;

import java.util.Optional;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertSame;

public abstract class ContentDispositionFileNameTestCase<F extends ContentDispositionFileName> extends
    HeaderTestCase<F>
    implements IsMethodTesting<F> {

    ContentDispositionFileNameTestCase() {
        super();
    }

    @Test
    public final void testWithoutPathCached() {
        final ContentDispositionFileName contentDisposition = this.createHeader();
        assertSame(contentDisposition.withoutPath(), contentDisposition.withoutPath());
    }

    final void parameterNameAndCheck(final F filename,
                                     final ContentDispositionParameterName<?> expected) {
        this.checkEquals(
            expected,
            filename.parameterName(),
            filename::toString
        );
    }

    final void check(final ContentDispositionFileName filename,
                     final String value,
                     final Optional<CharsetName> charsetName,
                     final Optional<LanguageName> language) {
        this.checkEquals(value, filename.value(), "value");
        this.checkEquals(charsetName, filename.charsetName(), "charsetName");
        this.checkEquals(language, filename.language(), "language");
    }

    final void checkWithoutPath(final ContentDispositionFileName contentDisposition,
                                final ContentDispositionFileName withoutPath) {
        this.checkEquals(withoutPath, contentDisposition.withoutPath, "withoutPath");
    }

    final void toNotEncodedAndCheck(final ContentDispositionFileName filename,
                                    final Optional<ContentDispositionFileName> expected) {
        this.checkEquals(
            expected,
            filename.toNotEncoded(),
            filename::toString
        );
    }

    final void toNotEncodedIfPossibleAndCheck(final ContentDispositionFileName filename) {
        assertSame(
            filename,
            filename.toNotEncodedIfPossible(),
            filename::toString
        );
    }

    final void toNotEncodedIfPossibleAndCheck(final ContentDispositionFileName filename,
                                              final ContentDispositionFileName expected) {
        this.checkEquals(
            expected,
            filename.toNotEncodedIfPossible(),
            filename::toString
        );
    }

    @Test
    public final void testIsWildcard() {
        this.isWildcardAndCheck(false);
    }

    @Override
    public final boolean isMultipart() {
        return true;
    }

    @Override
    public final boolean isRequest() {
        return true;
    }

    @Override
    public final boolean isResponse() {
        return true;
    }

    @Test
    public final void testWithoutPathCacheEqualsUnimportant() {
        this.checkEqualsAndHashCode(this.createHeader().withoutPath(), this.createHeader());
    }

    // isMethodTesting..................................................................................................

    @Override
    public final F createIsMethodObject() {
        return this.createHeader();
    }

    @Override
    public final Predicate<String> isMethodIgnoreMethodFilter() {
        return (m) -> {
            final boolean ignore;

            switch (m) {
                case "isWildcard":
                case "isRequest":
                case "isResponse":
                case "isMultipart":
                    ignore = true;
                    break;
                default:
                    ignore = false;
                    break;
            }

            return ignore;
        };
    }

    @Override
    public String toIsMethodName(final String typeName) {
        return this.toIsMethodNameWithPrefixSuffix(
            typeName,
            ContentDispositionFileName.class.getSimpleName(), // drop-prefix
            "" // drop-suffix
        );
    }

    // class............................................................................................................

    @Override
    public final JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}
