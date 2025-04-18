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
import walkingkooka.InvalidCharacterException;
import walkingkooka.reflect.IsMethodTesting;
import walkingkooka.reflect.JavaVisibility;

import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

abstract public class CookieTestCase<C extends Cookie> extends HeaderTestCase<C>
    implements IsMethodTesting<C> {

    CookieTestCase() {
        super();
    }

    // constants

    final static CookieName NAME = CookieName.with("cookie123");
    final static String VALUE = "value456";

    // tests

    @Test
    public final void testWithNullNameFails() {
        assertThrows(NullPointerException.class, () -> this.createCookie(null, CookieTestCase.VALUE));
    }

    @Test
    public final void testWithNullValueFails() {
        assertThrows(NullPointerException.class, () -> this.createCookie(CookieTestCase.NAME, null));
    }

    @Test
    public final void testWithInvalidValueFails() {
        assertThrows(InvalidCharacterException.class, () -> this.createCookie(CookieTestCase.NAME, "  "));
    }

    // setName ......................................................................................

    @Test
    public final void testSetNameNullFails() {
        assertThrows(NullPointerException.class, () -> this.createCookie().setName(null));
    }

    @Test
    public final void testSetNameSame() {
        final C cookie = this.createCookie();
        assertSame(cookie, cookie.setName(NAME));
    }

    // setValue ................................................................................................

    @Test
    public final void testSetValueNullFails() {
        assertThrows(NullPointerException.class, () -> this.createCookie().setValue(null));
    }

    @Test
    public final void testSetValueSame() {
        final C cookie = this.createCookie();
        assertSame(cookie, cookie.setValue(VALUE));
    }

    @Test
    public final void testIsWildcard() {
        this.isWildcardAndCheck(false);
    }

    // HashCodeEqualsDefined ..................................................................................................

    @Test final public void testEqualsDifferentName() {
        this.checkNotEquals(this.createCookie(CookieName.with("different"), VALUE));
    }

    @Test final public void testEqualsDifferentValue() {
        this.checkNotEquals(this.createCookie(NAME, "different"));
    }

    // helpers ................................................................................................

    final C createCookie() {
        return this.createCookie(NAME, VALUE);
    }

    abstract C createCookie(final CookieName name, final String value);

    final void checkName(final Cookie cookie) {
        checkName(cookie, NAME);
    }

    final void checkName(final Cookie cookie, final CookieName name) {
        this.checkEquals(name, cookie.name(), "name");
    }

    final void checkValue(final Cookie cookie) {
        checkValue(cookie, VALUE);
    }

    final void checkValue(final Cookie cookie, final String value) {
        this.checkEquals(value, cookie.value(), "value");
    }

    @Override
    public final boolean isMultipart() {
        return false;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }

    // IsMethodTesting..................................................................................................

    @Override
    public final C createIsMethodObject() {
        return this.createCookie();
    }

    @Override
    public final Predicate<String> isMethodIgnoreMethodFilter() {
        return (m) -> m.equals("isRequest") ||
            m.equals("isResponse") ||
            m.equals("isSession") ||
            m.equals("isPermanent");
    }

    @Override
    public String toIsMethodName(final String typeName) {
        return this.toIsMethodNameWithPrefixSuffix(
            typeName,
            "", // drop-prefix
            Cookie.class.getSimpleName() // drop-suffix
        );
    }
}
