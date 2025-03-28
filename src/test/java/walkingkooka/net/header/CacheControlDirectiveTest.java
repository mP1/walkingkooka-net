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
import walkingkooka.reflect.JavaVisibility;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class CacheControlDirectiveTest extends HeaderTestCase<CacheControlDirective<?>> {

    // with.............................................................................................................

    @Test
    public void testWithNameNullFails() {
        assertThrows(NullPointerException.class, () -> CacheControlDirective.with(null, Optional.empty()));
    }

    @Test
    public void testWithParameterNullFails() {
        assertThrows(NullPointerException.class, () -> CacheControlDirective.with(CacheControlDirectiveName.PUBLIC, null));
    }

    @Test
    public void testWithParameterInvalidFails() {
        assertThrows(HeaderException.class, () -> CacheControlDirective.with(CacheControlDirectiveName.MAX_AGE, Optional.empty()));
    }

    @Test
    public void testWith() {
        final CacheControlDirectiveName<Long> name = CacheControlDirectiveName.MAX_AGE;
        final Optional<Long> parameter = Optional.of(123L);
        this.check(CacheControlDirective.with(name, parameter),
            name,
            parameter);
    }

    // setParameter.....................................................................................................

    @Test
    public void testSetParameterNullFails() {
        assertThrows(NullPointerException.class, () -> CacheControlDirective.NO_STORE.setParameter(null));
    }

    @Test
    public void testSetParameterInvalidFails() {
        assertThrows(HeaderException.class, () -> CacheControlDirectiveName.MAX_AGE.setParameter(Optional.of(123L))
            .setParameter(Optional.empty()));
    }

    @Test
    public void testSetParameterSame() {
        final CacheControlDirectiveName<Long> name = CacheControlDirectiveName.MAX_AGE;
        final Optional<Long> parameter = Optional.of(123L);
        final CacheControlDirective<Long> directive = name.setParameter(parameter);
        assertSame(directive, directive.setParameter(parameter));
    }

    @Test
    public void testSetParameterSame2() {
        final CacheControlDirective<Void> directive = CacheControlDirective.NO_CACHE;
        assertSame(directive, directive.setParameter(Optional.empty()));
    }

    @Test
    public void testSetParameterDifferent() {
        this.setParameterAndCheck(CacheControlDirectiveName.MAX_AGE.setParameter(Optional.of(123L)),
            Optional.of(456L),
            CacheControlDirectiveName.MAX_AGE);
    }

    @Test
    public void testSetParameterMaxStaleDifferent() {
        this.setParameterAndCheck(CacheControlDirectiveName.MAX_STALE.setParameter(Optional.of(123L)),
            Optional.of(456L),
            CacheControlDirectiveName.MAX_STALE);
    }

    @Test
    public void testSetParameterMaxStaleDifferent2() {
        this.setParameterAndCheck(CacheControlDirective.MAX_STALE,
            Optional.of(123L),
            CacheControlDirectiveName.MAX_STALE);
    }

    @Test
    public void testSetParameterMaxStaleDifferent3() {
        this.setParameterAndCheck(CacheControlDirectiveName.MAX_STALE.setParameter(Optional.of(123L)),
            Optional.empty(),
            CacheControlDirectiveName.MAX_STALE);
    }

    private <T> void setParameterAndCheck(final CacheControlDirective<T> directive,
                                          final Optional<T> parameter,
                                          final CacheControlDirectiveName<T> name) {
        this.checkNotEquals(parameter,
            directive.parameter(),
            "new parameter must be different from old");
        final CacheControlDirective<T> different = directive.setParameter(parameter);
        assertNotSame(directive,
            different,
            "directive set parameter" + parameter + " must not return same");

        this.check(different, name, parameter);
    }

    private <T> void check(final CacheControlDirective<T> directive,
                           final CacheControlDirectiveName<T> name,
                           final Optional<T> parameter) {
        this.checkEquals(name, directive.value(), "value");
        this.checkEquals(parameter, directive.parameter(), "parameter");
    }

    // scope ...........................................................................................................

    @Test
    public void testScopeMaxAge() {
        this.checkScope(CacheControlDirectiveName.MAX_AGE.setParameter(Optional.of(123L)));
    }

    @Test
    public void testScopeMaxStale() {
        this.checkScope(CacheControlDirectiveName.MAX_STALE.setParameter(Optional.of(123L)));
    }

    @Test
    public void testScopeOnlyIfCached() {
        this.checkScope(CacheControlDirective.ONLY_IF_CACHED);
    }

    private void checkScope(final CacheControlDirective<?> directive) {
        this.checkEquals(false,
            directive.isMultipart(),
            directive + " isMultipart");
        this.checkEquals(directive.value().isRequest(),
            directive.isRequest(),
            directive + " isRequest");
        this.checkEquals(directive.value().isResponse(),
            directive.isResponse(),
            directive + " isResponse");
    }

    // wildcard.........................................................................................................

    @Test
    public void testIsWildcard() {
        this.isWildcardAndCheck(false);
    }

    // toHeaderText ....................................................................................................

    @Test
    public void testToHeaderTextMaxAge() {
        this.toHeaderTextAndCheck(CacheControlDirectiveName.MAX_AGE.setParameter(Optional.of(123L)),
            "max-age=123");
    }

    @Test
    public void testToHeaderTextNoCache() {
        this.toHeaderTextAndCheck(CacheControlDirective.NO_CACHE, "no-cache");
    }

    // toHeaderTextList.................................................................................................

    @Test
    public void testToHeaderTextListNoCache() {
        this.toHeaderTextListAndCheck("no-cache",
            CacheControlDirective.NO_CACHE);
    }

    @Test
    public void testToHeaderTextListMaxAge() {
        this.toHeaderTextListAndCheck("max-age=123",
            CacheControlDirectiveName.MAX_AGE.setParameter(Optional.of(123L)));
    }

    @Test
    public void testToHeaderTextListMaxAgeNoCacheNoStore() {
        this.toHeaderTextListAndCheck("max-age=123, no-cache, no-store",
            CacheControlDirectiveName.MAX_AGE.setParameter(Optional.of(123L)),
            CacheControlDirective.NO_CACHE,
            CacheControlDirective.NO_STORE);
    }

    // equals ..........................................................................................................

    private final static Optional<Long> PARAMETER = Optional.of(123L);

    @Test
    public void testEqualsDifferentParameter() {
        this.checkNotEquals(this.directiveName().setParameter(Optional.of(456L)));
    }

    @Test
    public void testEqualsDifferentName() {
        this.checkNotEquals(CacheControlDirectiveName.S_MAXAGE.setParameter(PARAMETER));
    }

    @Override
    public void testEqualsIgnoringParametersDifferent() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testEqualsOnlyPresentParametersDifferentFalse() {
        throw new UnsupportedOperationException();
    }

    private CacheControlDirectiveName<Long> directiveName() {
        return CacheControlDirectiveName.MAX_AGE;
    }

    @Override
    public CacheControlDirective<Long> createHeader() {
        return CacheControlDirectiveName.MAX_AGE.setParameter(Optional.of(123L));
    }

    @Override
    public CacheControlDirective<Long> createDifferentHeader() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isMultipart() {
        return false;
    }

    @Override
    public boolean isRequest() {
        return true;
    }

    @Override
    public boolean isResponse() {
        return true;
    }

    @Override
    public Class<CacheControlDirective<?>> type() {
        return Cast.to(CacheControlDirective.class);
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
