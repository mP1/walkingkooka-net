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
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.ToStringTesting;
import walkingkooka.compare.ComparableTesting2;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class UrlCredentialsTest implements ClassTesting2<UrlCredentials>,
    HashCodeEqualsDefinedTesting2<UrlCredentials>,
    ComparableTesting2<UrlCredentials>,
    ToStringTesting<UrlCredentials> {

    private final static String USER = "user123";
    private final static String PASSWORD = "password456";

    // with.............................................................................................................

    @Test
    public void testWithNullUserFails() {
        assertThrows(
            NullPointerException.class,
            () -> UrlCredentials.with(null, PASSWORD)
        );
    }

    @Test
    public void testWithNullPasswordFails() {
        assertThrows(
            NullPointerException.class,
            () -> UrlCredentials.with(USER, null)
        );
    }

    @Test
    public void testWith() {
        final UrlCredentials credentials = this.credentials();
        this.checkEquals(USER, credentials.user(), "user");
        this.checkEquals(PASSWORD, credentials.password(), "password");
    }

    // hashCode/equals..................................................................................................

    @Test
    public void testEqualsDifferentUser() {
        this.checkNotEquals(UrlCredentials.with("different", PASSWORD));
    }

    @Test
    public void testEqualsDifferentPassword() {
        this.checkNotEquals(UrlCredentials.with(USER, "different"));
    }

    @Override
    public UrlCredentials createObject() {
        return UrlCredentials.with(USER, PASSWORD);
    }

    // compareTo........................................................................................................

    @Test
    public void testCompareToDifferentCase() {
        this.compareToAndCheckNotEquals(
            UrlCredentials.with(USER.toLowerCase(), PASSWORD.toLowerCase()),
            UrlCredentials.with(USER.toUpperCase(), PASSWORD.toUpperCase())
        );
    }

    @Test
    public void testCompareToLess() {
        this.compareToAndCheckLess(
            UrlCredentials.with("A", PASSWORD.toLowerCase()),
            UrlCredentials.with("B", PASSWORD.toUpperCase())
        );
    }

    @Override
    public UrlCredentials createComparable() {
        return UrlCredentials.with(USER, PASSWORD);
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(this.credentials(), USER + ":" + PASSWORD);
    }

    private UrlCredentials credentials() {
        return UrlCredentials.with(USER, PASSWORD);
    }

    // class............................................................................................................

    @Override
    public Class<UrlCredentials> type() {
        return UrlCredentials.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
