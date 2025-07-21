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

import java.util.Optional;

public final class UrlPathLeafUnnormalizedTest extends UrlPathTestCase<UrlPathLeafUnnormalized> {

    @Test
    public void testAppendNameEmpty() {
        final UrlPathName name = UrlPathName.ROOT;

        this.appendNameAndCheck(
            name,
            unnormalized("/a1//", name),
            "/a1//"
        );
    }

    @Test
    public void testAppendNameDot() {
        final UrlPathName name = UrlPathName.with(".");

        this.appendNameAndCheck(
            name,
            unnormalized("/a1/.", name),
            "/a1/."
        );
    }

    @Test
    public void testAppendNameDoubleDot() {
        final UrlPathName name = UrlPathName.with("..");

        this.appendNameAndCheck(
            name,
            unnormalized("/a1/..", name),
            "/a1/.."
        );
    }

    @Test
    public void testAppendName() {
        final UrlPathName name = UrlPathName.with("b2");

        this.appendNameAndCheck(
            name,
            unnormalized("/a1/b2", name),
            "/a1/b2"
        );
    }

    @Test
    public void testAppendName2() {
        final UrlPathName name = UrlPathName.with("2");

        this.appendNameAndCheck(
            name,
            unnormalized("/a1/2", name),
            "/a1/2"
        );
    }

    // normalize........................................................................................................

    @Test
    public void testNormalizeMissingStartingSlash() {
        this.normalizeAndCheck(
            "path1/./path2",
            "/path1/path2"
        );
    }

    @Test
    public void testNormalizeMissingStartingSlash2() {
        this.normalizeAndCheck(
            "path1/../path2",
            "/path2"
        );
    }

    @Test
    public void testNormalizeStartsWithSlash() {
        this.normalizeAndCheck(
            "/path1/./path2",
            "/path1/path2"
        );
    }

    @Test
    public void testNormalizeStartsWithSlash2() {
        this.normalizeAndCheck(
            "/path1/../path2",
            "/path2"
        );
    }

    // pathAfter........................................................................................................

    @Test
    public void testPathAfter1() {
        this.pathAfterAndCheck(
            "/dir1/../file3",
            1,
            "/../file3"
        );
    }

    @Test
    public void testPathAfter2() {
        this.pathAfterAndCheck(
            "/dir1/../file3",
            2,
            "/file3"
        );
    }

    @Test
    public void testPathAfter3() {
        this.pathAfterAndCheck(
            "/dir1/../file3",
            3,
            ""
        );
    }

    @Test
    public void testPathAfterFails() {
        this.pathAfterFailsAndCheck(
            "/dir1/../file3",
            4,
            "Invalid start 4 > 3"
        );
    }

    @Override
    UrlPathLeafUnnormalized createPath() {
        return UrlPathLeafUnnormalized.withUnnormalized("/a1",
            UrlPathName.with("a1"),
            Optional.of(UrlPath.ROOT));
    }

    @Override
    boolean isNormalized() {
        return false;
    }

    @Override
    boolean isRoot() {
        return false;
    }

    @Override
    String expectedToString() {
        return "/a1";
    }

    @Override
    Class<UrlPathLeafUnnormalized> urlPathType() {
        return UrlPathLeafUnnormalized.class;
    }
}
