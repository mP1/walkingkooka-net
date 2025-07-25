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

import static org.junit.jupiter.api.Assertions.assertSame;

public final class UrlPathEmptyTest extends UrlPathTestCase<UrlPathEmpty> {

    @Test
    public void testAppendNameEmpty() {
        final UrlPathName name = UrlPathName.ROOT;

        this.appendNameAndCheck(
            name,
            unnormalized("", name),
            ""
        );
    }

    @Test
    public void testAppendNameDot() {
        final UrlPathName name = UrlPathName.with(".");

        this.appendNameAndCheck(
            name,
            unnormalized(".", name),
            "."
        );
    }

    @Test
    public void testAppendNameDoubleDot() {
        final UrlPathName name = UrlPathName.with("..");

        this.appendNameAndCheck(
            name,
            unnormalized("..", name),
            ".."
        );
    }

    @Test
    public void testAppendName() {
        final UrlPathName name = UrlPathName.with("abc");

        this.appendNameAndCheck(
            name,
            unnormalized("abc", name),
            "abc"
        );
    }

    @Test
    public void testAppendName2() {
        final UrlPathName name = UrlPathName.with("2");

        this.appendNameAndCheck(
            name,
            unnormalized("2", name),
            "2"
        );
    }

    // appendPath.......................................................................................................

    @Test
    public void testAppendPathWithEmpty() {
        assertSame(
            UrlPathEmpty.EMPTY_URL_PATH,
            UrlPathEmpty.EMPTY_URL_PATH.append(UrlPathEmpty.EMPTY_URL_PATH)
        );
    }

    @Test
    public void testAppendPathWithRoot() {
        assertSame(
            UrlPath.ROOT,
            UrlPathEmpty.EMPTY_URL_PATH.append(UrlPath.ROOT)
        );
    }

    @Test
    public void testAppendPathWithSlashPath() {
        final UrlPath path = UrlPath.parse("/p1/p2");

        assertSame(
            path,
            UrlPathEmpty.EMPTY_URL_PATH.append(path)
        );
    }

    @Test
    public void testAppendPathWithPath() {
        final UrlPath path = UrlPath.parse("p1/p2");

        assertSame(
            path,
            UrlPathEmpty.EMPTY_URL_PATH.append(path)
        );
    }

    // normalize........................................................................................................

    @Test
    public void testNormalize() {
        this.normalizeAndCheck(
            UrlPathEmpty.EMPTY_URL_PATH,
            UrlPath.ROOT
        );
    }

    // pathAfter........................................................................................................

    @Test
    public void testPathAfterZero() {
        this.pathAfterAndCheck(
            UrlPath.EMPTY,
            0,
            UrlPath.EMPTY
        );
    }

    @Test
    public void testPathAfterNonZeroFails() {
        this.pathAfterFailsAndCheck(
            UrlPath.EMPTY,
            1,
            "Invalid start 1 > 0"
        );
    }

    @Override
    UrlPathEmpty createPath() {
        return Cast.to(UrlPath.EMPTY);
    }

    @Override
    boolean isNormalized() {
        return false;
    }

    @Override
    boolean isRoot() {
        return true;
    }

    @Override
    String expectedToString() {
        return "";
    }

    @Override
    Class<UrlPathEmpty> urlPathType() {
        return UrlPathEmpty.class;
    }
}
