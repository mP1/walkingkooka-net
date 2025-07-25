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

public final class UrlPathRootTest extends UrlPathTestCase<UrlPathRoot> {

    @Test
    public void testAppendNameEmpty() {
        final UrlPathName name = UrlPathName.ROOT;

        this.appendNameAndCheck(
            name,
            unnormalized("//", name),
            "//"
        );
    }

    @Test
    public void testAppendNameDot() {
        final UrlPathName name = UrlPathName.with(".");

        this.appendNameAndCheck(
            name,
            unnormalized("/.", name),
            "/."
        );
    }

    @Test
    public void testAppendNameDoubleDot() {
        final UrlPathName name = UrlPathName.with("..");

        this.appendNameAndCheck(
            name,
            unnormalized("/..", name),
            "/.."
        );
    }

    @Test
    public void testAppendName() {
        final UrlPathName name = UrlPathName.with("abc");

        this.appendNameAndCheck(
            name,
            normalized("/abc", name),
            "/abc"
        );
    }

    @Test
    public void testAppendName2() {
        final UrlPathName name = UrlPathName.with("2");

        this.appendNameAndCheck(
            name,
            normalized("/2", name),
            "/2"
        );
    }

    // appendPath.......................................................................................................

    @Test
    public void testAppendPathEmptyPath() {
        assertSame(
            UrlPathRoot.ROOT,
            UrlPathRoot.ROOT.append(UrlPath.EMPTY)
        );
    }

    @Test
    public void testAppendPathNotEmptySlashPath() {
        final UrlPath path = UrlPath.parse("/a1");

        assertSame(
            path,
            UrlPathRoot.ROOT.append(path)
        );
    }

    @Test
    public void testAppendPathNotEmptyPath() {
        final UrlPath path = UrlPath.parse("a1");

        appendPathAndCheck(
            UrlPath.ROOT,
            path,
            UrlPath.normalized(
                "/a1",
                UrlPathName.with("a1"),
                UrlPath.ROOT_PARENT
            )
        );
    }

    private void appendPathAndCheck(final UrlPath path,
                                    final UrlPath append,
                                    final UrlPath expected) {
        this.checkEquals(
            expected,
            path.appendPath(append)
        );
    }

    // normalize........................................................................................................

    @Test
    public void testNormalize() {
        this.normalizeAndCheck(
            UrlPathRoot.ROOT
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
    UrlPathRoot createPath() {
        return Cast.to(UrlPath.ROOT);
    }

    @Override
    boolean isNormalized() {
        return true;
    }

    @Override
    boolean isRoot() {
        return true;
    }

    @Override
    String expectedToString() {
        return "/";
    }

    @Override
    Class<UrlPathRoot> urlPathType() {
        return UrlPathRoot.class;
    }
}
