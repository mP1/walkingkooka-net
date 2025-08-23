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
import walkingkooka.predicate.PredicateTesting2;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class UrlPathPredicateTest implements PredicateTesting2<UrlPathPredicate, UrlPath> {

    // with.............................................................................................................

    @Test
    public void testWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> UrlPathPredicate.with(null)
        );
    }

    @Test
    public void testWithIncludesStarStarFails() {
        final IllegalArgumentException thrown = assertThrows(
            IllegalArgumentException.class,
            () -> UrlPathPredicate.with(
                UrlPath.parse("/path1/**/path3")
            )
        );

        this.checkEquals(
            "Pattern should only contain \"**\" at the end, \"/path1/**/path3\"",
            thrown.getMessage()
        );
    }

    // test.............................................................................................................

    @Test
    public void testTestNullFalse() {
        this.testFalse(null);
    }

    @Test
    public void testSlashTestSlashTrue() {
        this.testTrue(
            "/",
            "/"
        );
    }

    @Test
    public void testSlashTestSlashNameFalse() {
        this.testFalse(
            "/",
            "/path"
        );
    }

    @Test
    public void testSlashStarStarTestSlashTrue() {
        this.testTrue(
            "/**",
            "/"
        );
    }

    @Test
    public void testSlashNameTestSlashName() {
        this.testTrue(
            "/path1",
            "/path1"
        );
    }

    @Test
    public void testSlashNameTestSlashDifferentName() {
        this.testFalse(
            "/path1",
            "/different1"
        );
    }

    @Test
    public void testSlashNameTestSlashDifferentNameName() {
        this.testFalse(
            "/path1",
            "/different1/path2"
        );
    }

    @Test
    public void testSlashNameTestSlashNameDifferentName() {
        this.testFalse(
            "/path1",
            "/path1/different2"
        );
    }

    @Test
    public void testSlashStarStarTestSlashNameNameTrue() {
        this.testTrue(
            "/**",
            "/path1"
        );
    }

    @Test
    public void testSlashStarStarTestSlashNameNameTrue2() {
        this.testTrue(
            "/**",
            "/path1/path2"
        );
    }

    @Test
    public void testNameStarTestNameNameTrue() {
        this.testTrue(
            "/path1/*",
            "/path1/path2"
        );
    }

    @Test
    public void testNameStarTestNameNameNameFalse() {
        this.testFalse(
            "/path1/*",
            "/path1/path2/path3"
        );
    }

    @Test
    public void testNameStarNameTestNameNameNameTrue() {
        this.testTrue(
            "/path1/*/path3",
            "/path1/path2/path3"
        );
    }

    @Test
    public void testNameStarNameTestDifferentNameNameFalse() {
        this.testFalse(
            "/path1/*/path3",
            "/different1/path2"
        );
    }

    @Test
    public void testNameStarNameTestDifferentNameNameNameFalse() {
        this.testFalse(
            "/path1/*/path3",
            "/different1/path2/path3"
        );
    }

    @Test
    public void testNameStarNameTestNameNameDifferentNameFalse() {
        this.testFalse(
            "/path1/*/path3",
            "/path1/path2/different"
        );
    }

    @Test
    public void testNameStarStarTestNameNameNameTrue() {
        this.testTrue(
            "/path1/**",
            "/path1/path2/path3"
        );
    }

    @Test
    public void testNameStarStarTestDifferentNameNameNameFalse() {
        this.testFalse(
            "/path1/**",
            "/different1/path2/path3"
        );
    }

    @Test
    public void testNameStarNameStarStarTestNameNameNameTrue() {
        this.testTrue(
            "/path1/*/path3/**",
            "/path1/path2/path3"
        );
    }

    @Test
    public void testNameStarNameStarStarTestNameNameNameNameTrue() {
        this.testTrue(
            "/path1/*/path3/**",
            "/path1/path2/path3/path4"
        );
    }

    @Test
    public void testNameStarNameStarStarTestNameNameNameNameNameTrue() {
        this.testTrue(
            "/path1/*/path3/**",
            "/path1/path2/path3/path4/path5"
        );
    }

    @Test
    public void testNameStarNameStarStarTestDifferentNameNameNameFalse() {
        this.testFalse(
            "/path1/*/path3/**",
            "/different/path2/path3/path4"
        );
    }

    @Test
    public void testNameIncludesStar() {
        this.testFalse(
            "/path1*/path2",
            "/path1/path2"
        );
    }

    @Override
    public UrlPathPredicate createPredicate() {
        return UrlPathPredicate.with(
            UrlPath.parse("/path1/*")
        );
    }

    private void testFalse(final String path,
                           final String test) {
        this.testFalse(
            UrlPath.parse(path).predicate(),
            UrlPath.parse(test)
        );
    }

    private void testTrue(final String path,
                          final String test) {
        this.testTrue(
            UrlPath.parse(path).predicate(),
            UrlPath.parse(test)
        );
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        final String pattern = "/path1/path2/";

        this.toStringAndCheck(
            UrlPathPredicate.with(
                UrlPath.parse(pattern)
            ),
            pattern
        );
    }

    @Test
    public void testToStringStar() {
        final String pattern = "/path1/path2/*";

        this.toStringAndCheck(
            UrlPathPredicate.with(
                UrlPath.parse(pattern)
            ),
            pattern
        );
    }

    @Test
    public void testToStringStarStar() {
        final String pattern = "/path1/path2/**";

        this.toStringAndCheck(
            UrlPathPredicate.with(
                UrlPath.parse(pattern)
            ),
            pattern
        );
    }

    // class............................................................................................................

    @Override
    public Class<UrlPathPredicate> type() {
        return UrlPathPredicate.class;
    }
}
