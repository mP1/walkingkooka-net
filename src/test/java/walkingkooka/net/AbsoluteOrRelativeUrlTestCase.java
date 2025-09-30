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

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Base class for testing a {@link Url} with mostly parameter checking tests.
 */
abstract public class AbsoluteOrRelativeUrlTestCase<U extends AbsoluteOrRelativeUrl> extends UrlTestCase<U>
    implements HasUrlQueryStringTesting {

    AbsoluteOrRelativeUrlTestCase() {
        super();
    }

    // constants

    final static UrlPath PATH = UrlPath.parse("/path");
    final static UrlQueryString QUERY = UrlQueryString.parse("query=value");
    final static UrlFragment FRAGMENT = UrlFragment.with("fragment");

    // tests

    @Test
    public void testNullPathFails() {
        assertThrows(NullPointerException.class, () -> this.createUrl(null, QUERY, FRAGMENT));
    }

    @Test
    public void testNullQueryFails() {
        assertThrows(NullPointerException.class, () -> this.createUrl(PATH, null, FRAGMENT));
    }

    @Test
    public void testNullFragmentFails() {
        assertThrows(NullPointerException.class, () -> this.createUrl(PATH, QUERY, null));
    }

    @Test
    public void testWith() {
        final U url = this.createUrl(PATH, QUERY, FRAGMENT);
        this.checkPath(url, PATH);
        this.queryAndCheck(url, QUERY);
        this.checkFragment(url, FRAGMENT);
    }

    @Test
    public void testOnlySlash() {
        final UrlPath path = UrlPath.parse("" + Url.PATH_START);
        final U url = this.createUrl(path, QUERY, FRAGMENT);
        assertSame(path, url.path(), "path");
    }

    @Test
    public void testWithoutQuery() {
        final U url = this.createUrl(PATH, UrlQueryString.EMPTY, FRAGMENT);
        assertSame(PATH, url.path(), "path");
        assertSame(UrlQueryString.EMPTY, url.query(), "query");
        this.checkFragment(url, FRAGMENT);
    }

    @Test
    public void testWithoutFragment() {
        final U url = this.createUrl(PATH, QUERY, UrlFragment.EMPTY);
        assertSame(PATH, url.path(), "path");
        this.queryAndCheck(url, QUERY);
        assertSame(UrlFragment.EMPTY, url.fragment(), "fragment");
    }

    // would be setters

    // setPath .........................................................................................................

    @Test
    public final void testSetPathNullFails() {
        assertThrows(NullPointerException.class, () -> this.createUrl().setPath(null));
    }

    @Test
    public final void testSetPathSame() {
        final U url = this.createUrl();
        assertSame(url, url.setPath(PATH));
    }

    @Test
    public final void testSetPathDifferent() {
        final UrlPath differentPath = UrlPath.parse("/different-path");

        this.setPathAndCheck(
            this.createUrl(),
            differentPath,
            this.createUrl(
                differentPath,
                QUERY,
                FRAGMENT
            )

        );
    }

    @Test
    public final void testSetPathWithEmpty() {
        final UrlPath emptyPath = UrlPath.EMPTY;

        this.setPathAndCheck(
            this.createUrl(
                UrlPath.parse("/path123"),
                QUERY,
                FRAGMENT
            ),
            emptyPath,
            this.createUrl(
                emptyPath,
                QUERY,
                FRAGMENT
            )

        );
    }

    @Test
    public final void testSetPathReplacesEmpty() {
        final UrlPath differentPath = UrlPath.parse("/different-path");

        this.setPathAndCheck(
            this.createUrl(
                UrlPath.EMPTY,
                QUERY,
                FRAGMENT
            ),
            differentPath,
            this.createUrl(
                differentPath,
                QUERY,
                FRAGMENT
            )

        );
    }

    @Test
    public final void testSetPathWithRoot() {
        final UrlPath rootPath = UrlPath.ROOT;

        this.setPathAndCheck(
            this.createUrl(
                UrlPath.EMPTY,
                QUERY,
                FRAGMENT
            ),
            rootPath,
            this.createUrl(
                rootPath,
                QUERY,
                FRAGMENT
            )

        );
    }

    @Test
    public final void testSetPathReplacesRoot() {
        final UrlPath differentPath = UrlPath.parse("/different-path");

        this.setPathAndCheck(
            this.createUrl(
                UrlPath.ROOT,
                QUERY,
                FRAGMENT
            ),
            differentPath,
            this.createUrl(
                differentPath,
                QUERY,
                FRAGMENT
            )

        );
    }

    @Test
    public final void testSetPathDifferentMissingLeadingSlash() {
        final UrlPath differentPath = UrlPath.parse("different-path");

        this.setPathAndCheck(
            this.createUrl(),
            differentPath,
            this.createUrl(
                UrlPath.parse("/" + differentPath),
                QUERY,
                FRAGMENT
            )

        );
    }

    final void setPathAndCheck(final U url,
                               final UrlPath path,
                               final U expected) {
        this.checkEquals(
            expected,
            url.setPath(path),
            () -> url + " setPath " + path
        );
    }

    // appendPath .......................................................................................................

    @Test
    public final void testAppendPathNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createUrl()
                .appendPath(null)
        );
    }

    @Test
    public final void testAppendPathEmpty() {
        final U url = this.createUrl();
        assertSame(
            url,
            url.appendPath(UrlPath.EMPTY)
        );
    }

    @Test
    public final void testAppendPathExtra() {
        final U url = this.createUrl();

        final UrlPath append = UrlPath.parse("/extra-path");
        final Url appended = url.appendPath(append);
        assertNotSame(url, appended);

        this.checkEquals(
            this.createUrl(
                url.path()
                    .appendPath(append),
                QUERY,
                FRAGMENT
            ),
            appended
        );
    }

    @Test
    public final void testAppendPathMissingLeadingSeparator() {
        final U url = this.createUrl();

        final UrlPath append = UrlPath.parse("path456");
        final AbsoluteOrRelativeUrl appended = url.appendPath(append);
        assertNotSame(url, appended);

        this.checkEquals(
            this.createUrl(
                url.path()
                    .appendPath(append),
                QUERY,
                FRAGMENT
            ),
            appended
        );

        this.checkEquals(
            url.path() + "/path456", // extra slash inserted before appending /path456
            appended.path().value()
        );
    }

    @Test
    public final void testAppendPathMissingLeadingSeparator2() {
        final U url = this.createUrl();

        final UrlPath append = UrlPath.parse("path456/path789");
        final AbsoluteOrRelativeUrl appended = url.appendPath(append);
        assertNotSame(url, appended);

        this.checkEquals(
            this.createUrl(
                url.path()
                    .appendPath(append),
                QUERY,
                FRAGMENT
            ),
            appended
        );

        this.checkEquals(
            url.path() + "/path456/path789", // extra slash inserted before appending /path456
            appended.path().value()
        );
    }

    @Test
    public final void testAppendPathTrailingSeparatorMissingLeadingSeparator() {
        final AbsoluteOrRelativeUrl url = this.createUrl()
            .setPath(UrlPath.parse("/path123/"));

        final UrlPath append = UrlPath.parse("path456");
        final AbsoluteOrRelativeUrl appended = url.appendPath(append);
        assertNotSame(url, appended);

        this.checkEquals(
            this.createUrl(
                url.path()
                    .appendPath(append),
                QUERY,
                FRAGMENT
            ),
            appended
        );

        this.checkEquals(
            "/path123/path456",
            appended.path().value()
        );
    }

    @Test
    public final void testAppendPathTrailingSeparatorMissingLeadingSeparator2() {
        final AbsoluteOrRelativeUrl url = this.createUrl()
            .setPath(UrlPath.parse("/path123/"));

        final UrlPath append = UrlPath.parse("path456/path789");
        final AbsoluteOrRelativeUrl appended = url.appendPath(append);
        assertNotSame(url, appended);

        this.checkEquals(
            this.createUrl(
                url.path()
                    .appendPath(append),
                QUERY,
                FRAGMENT
            ),
            appended
        );

        this.checkEquals(
            "/path123/path456/path789",
            appended.path().value()
        );
    }

    // appendPathName .......................................................................................................

    @Test
    public final void testAppendNameNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createUrl()
                .appendPathName(null)
        );
    }

    @Test
    public final void testAppendName() {
        this.appendNameAndCheck(
            this.createUrl(UrlPath.parse("/path1"), UrlQueryString.EMPTY, UrlFragment.EMPTY),
            UrlPathName.with("path2"),
            this.createUrl(UrlPath.parse("/path1/path2"), UrlQueryString.EMPTY, UrlFragment.EMPTY)
        );
    }

    private void appendNameAndCheck(final U url,
                                    final UrlPathName name,
                                    final U expected) {
        this.checkEquals(
            expected,
            url.appendPathName(name),
            () -> url + " appendPathName " + name
        );
    }

    // setQuery .......................................................................................................

    @Test
    public final void testSetQueryNullFails() {
        assertThrows(NullPointerException.class, () -> this.createUrl().setQuery(null));
    }

    @Test
    public final void testSetQuerySame() {
        final U url = this.createUrl();
        assertSame(url, url.setQuery(QUERY));
    }

    @Test
    public final void testSetQueryDifferent() {
        final U url = this.createUrl();

        final UrlQueryString differentQueryString = UrlQueryString.parse("different=value");
        final Url different = url.setQuery(differentQueryString);
        assertNotSame(url, different);
        this.checkEquals(this.createUrl(PATH, differentQueryString, FRAGMENT), different);
    }

    // setFragment .......................................................................................................

    @Test
    public final void testSetFragmentNullFails() {
        assertThrows(NullPointerException.class, () -> this.createUrl().setFragment(null));
    }

    @Test
    public final void testSetFragmentSame() {
        final U url = this.createUrl();
        assertSame(url, url.setFragment(FRAGMENT));
    }

    @Test
    public final void testSetFragmentDifferent() {
        final U url = this.createUrl();

        final UrlFragment differentFragment = UrlFragment.with("different-anchor");
        final Url different = url.setFragment(differentFragment);
        assertNotSame(url, different);
        this.checkEquals(this.createUrl(PATH, QUERY, differentFragment), different);
    }

    // normalize...................................................................................................

    final void normalizeAndCheck(final String url) {
        normalizeAndCheck(
            this.parseString(url)
        );
    }

    final void normalizeAndCheck(final AbsoluteOrRelativeUrl url) {
        assertSame(
            url,
            url.normalize()
        );
    }

    final void normalizeAndCheck(final String url,
                                 final String expected) {
        this.normalizeAndCheck(
            this.parseString(url),
            this.parseString(expected)
        );
    }

    final void normalizeAndCheck(final AbsoluteOrRelativeUrl url,
                                 final AbsoluteOrRelativeUrl expected) {
        this.checkEquals(
            expected,
            url.normalize(),
            () -> url + " normalize"
        );
    }

    // HasUrlFragment...................................................................................................

    @Test
    public final void testHashUrlFragment() {
        final U url = this.createUrl();

        this.checkEquals(
            url.fragment(),
            url.urlFragment()
        );
    }

    // HashCodeEqualsDefined ..................................................................................................

    @Test
    public final void testEqualsDifferentPath() {
        this.checkNotEquals(
            this.createUrl(UrlPath.parse("/different"), QUERY, FRAGMENT));
    }

    @Test
    public final void testEqualsDifferentQuery() {
        this.checkNotEquals(this.createUrl(PATH, UrlQueryString.parse("differentQueryString"), FRAGMENT));
    }

    @Test
    public void testEqualsDifferentAnchor() {
        this.checkNotEquals(this.createUrl(PATH, QUERY, UrlFragment.with("different")));
    }

    // toString........................................................................

    @Test
    abstract public void testToStringWithoutQuery();

    @Test
    abstract public void testToStringWithoutFragment();

    @Test
    abstract public void testToStringWithoutQueryAndFragment();

    // factory

    @Override final U createUrl() {
        return this.createUrl(PATH, QUERY, FRAGMENT);
    }

    abstract U createUrl(final UrlPath path,
                         final UrlQueryString query,
                         final UrlFragment fragment);

    final void checkPath(final AbsoluteOrRelativeUrl url,
                         final UrlPath path) {
        this.checkEquals(path, url.path(), "path");
    }

    final void checkFragment(final AbsoluteOrRelativeUrl url,
                             final UrlFragment fragment) {
        this.checkEquals(fragment, url.fragment(), "fragment");
    }
}
