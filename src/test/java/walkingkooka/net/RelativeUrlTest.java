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
import walkingkooka.compare.ComparableTesting2;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;
import walkingkooka.visit.Visiting;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class RelativeUrlTest extends AbsoluteOrRelativeUrlTestCase<RelativeUrl>
    implements ComparableTesting2<RelativeUrl> {

    @Test
    public void testWithPathMissingStartSlash() {
        final UrlPath path = UrlPath.parse("p1/p2");
        final UrlQueryString queryString = UrlQueryString.parse("a=b,c=d");
        final UrlFragment fragment = UrlFragment.with("xyz");

        final RelativeUrl relativeUrl = RelativeUrl.with(
            path,
            queryString,
            fragment
        );

        this.checkEquals(
            relativeUrl.path(),
            UrlPath.parse("/p1/p2")
        );
        this.checkEquals(
            relativeUrl.query(),
            queryString
        );
        this.checkEquals(
            relativeUrl.fragment(),
            fragment
        );
    }

    @Test
    public void testWithPathWithStartSlash() {
        final UrlPath path = UrlPath.parse("/p1/p2");
        final UrlQueryString queryString = UrlQueryString.parse("a=b,c=d");
        final UrlFragment fragment = UrlFragment.with("xyz");

        final RelativeUrl relativeUrl = RelativeUrl.with(
            path,
            queryString,
            fragment
        );

        this.checkEquals(
            relativeUrl.path(),
            path
        );
        this.checkEquals(
            relativeUrl.query(),
            queryString
        );
        this.checkEquals(
            relativeUrl.fragment(),
            fragment
        );
    }

    // parseRelative..........................................................................................

    @Override
    public void testParseStringEmptyFails() {
    }

    @Test
    public void testParseAbsoluteUrlFails() {
        assertThrows(IllegalArgumentException.class, () -> RelativeUrl.parseRelative0("https://example.com"));
    }

    @Test
    public void testParseEmptyPath() {
        final String string = "";

        final RelativeUrl url = RelativeUrl.parseRelative0(string);
        this.checkPath(url, UrlPath.EMPTY);
        this.queryAndCheck(url, UrlQueryString.EMPTY);
        this.checkFragment(url, UrlFragment.EMPTY);

        this.toStringAndCheck(
            url,
            string
        );
    }

    @Test
    public void testParseSlash() {
        final String string = "/";

        final RelativeUrl url = RelativeUrl.parseRelative0(string);
        this.checkPath(url, UrlPath.parse("/"));
        this.queryAndCheck(url, UrlQueryString.EMPTY);
        this.checkFragment(url, UrlFragment.EMPTY);

        this.toStringAndCheck(
            url,
            string
        );
    }

    @Test
    public void testParsePathEndingSlash() {
        final String string = "/path/file/";

        final RelativeUrl url = RelativeUrl.parseRelative0(string);
        this.checkPath(url, UrlPath.parse("/path/file/"));
        this.queryAndCheck(url, UrlQueryString.EMPTY);
        this.checkFragment(url, UrlFragment.EMPTY);

        this.toStringAndCheck(
            url,
            string
        );
    }

    @Test
    public void testParsePathQueryString() {
        final String string = "/path123?query456";

        final RelativeUrl url = RelativeUrl.parseRelative0(string);
        this.checkPath(url, UrlPath.parse("/path123"));
        this.queryAndCheck(url, UrlQueryString.parse("query456"));
        this.checkFragment(url, UrlFragment.EMPTY);

        this.toStringAndCheck(
            url,
            string
        );
    }

    @Test
    public void testParsePathQueryStringEncoded() {
        final String string = "/path123?abc=1+2";

        final RelativeUrl url = RelativeUrl.parseRelative0(string);
        this.checkPath(url, UrlPath.parse("/path123"));
        this.queryAndCheck(url, UrlQueryString.parse("abc=1+2"));
        this.checkFragment(url, UrlFragment.EMPTY);

        this.toStringAndCheck(
            url,
            string
        );
    }

    @Test
    public void testParsePathQueryStringFragment() {
        final String string = "/path123?query456#fragment789";

        final RelativeUrl url = RelativeUrl.parseRelative0(string);
        this.checkPath(url, UrlPath.parse("/path123"));
        this.queryAndCheck(url, UrlQueryString.parse("query456"));
        this.checkFragment(url, UrlFragment.with("fragment789"));

        this.toStringAndCheck(
            url,
            string
        );
    }

    @Test
    public void testParsePathQueryStringFragmentWithPlus() {
        final String string = "/path123?query456#fragment+789";

        final RelativeUrl url = RelativeUrl.parseRelative0(string);
        this.checkPath(url, UrlPath.parse("/path123"));
        this.queryAndCheck(url, UrlQueryString.parse("query456"));
        this.checkFragment(url, UrlFragment.with("fragment+789"));

        this.toStringAndCheck(
            url,
            string
        );
    }

    @Test
    public void testParseOnlyHash() {
        final RelativeUrl url = RelativeUrl.parseRelative0("#");
        this.checkPath(url, UrlPath.EMPTY);
        this.queryAndCheck(url, UrlQueryString.EMPTY);
        this.checkFragment(url, UrlFragment.EMPTY);
    }

    @Test
    public void testParseOnlyHash2() {
        final RelativeUrl url = RelativeUrl.parseRelative0("#123");
        this.checkPath(url, UrlPath.EMPTY);
        this.queryAndCheck(url, UrlQueryString.EMPTY);
        this.checkFragment(url, UrlFragment.parse("123"));
    }

    @Override
    public RelativeUrl parseString(final String text) {
        return RelativeUrl.parseRelative0(text);
    }

    // UrlVisitor......................................................................................................

    @Test
    public void testAccept() {
        final StringBuilder b = new StringBuilder();
        final RelativeUrl url = this.createUrl();

        new FakeUrlVisitor() {
            @Override
            protected Visiting startVisit(final Url u) {
                assertSame(url, u);
                b.append("1");
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final Url u) {
                assertSame(url, u);
                b.append("2");
            }

            @Override
            protected void visit(final RelativeUrl u) {
                assertSame(url, u);
                b.append("5");
            }
        }.accept(url);
        this.checkEquals("152", b.toString());
    }

    // normalize...................................................................................................

    @Test
    public void testNormalizeRequired() {
        this.normalizeAndCheck(
            "/path1/path2/../path3?query1=2",
            "/path1/path3?query1=2"
        );
    }

    @Test
    public void testNormalizeRequired2() {
        this.normalizeAndCheck(
            "/path1/path2/./path3?query1=2",
            "/path1/path2/path3?query1=2"
        );
    }

    @Test
    public void testNormalizeUnnecessary() {
        this.normalizeAndCheck(
            "/path1/path2/path3?query1=2"
        );
    }

    @Test
    public void testNormalizeWithoutPathUnnecessary() {
        this.normalizeAndCheck(
            "",
            "/"
        );
    }

    @Test
    public void testNormalizeEmptyPathUnnecessary() {
        this.normalizeAndCheck(
            "/"
        );
    }

    @Test
    public void testToRelative() {
        final RelativeUrl url = this.createUrl();
        assertSame(url, url.relativeUrl());
    }

    @Override
    protected RelativeUrl createUrl(final UrlPath path, final UrlQueryString query, final UrlFragment fragment) {
        return Url.relative(path, query, fragment);
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        toStringAndCheck(Url.relative(PATH, QUERY, FRAGMENT), "/path?query=value#fragment");
    }

    @Test
    @Override
    public void testToStringWithoutQuery() {
        toStringAndCheck(Url.relative(PATH, UrlQueryString.EMPTY, FRAGMENT), "/path#fragment");
    }

    @Test
    @Override
    public void testToStringWithoutFragment() {
        toStringAndCheck(Url.relative(PATH, QUERY, UrlFragment.EMPTY), "/path?query=value");
    }

    @Test
    @Override
    public void testToStringWithoutQueryAndFragment() {
        toStringAndCheck(Url.relative(PATH, UrlQueryString.EMPTY, UrlFragment.EMPTY), "/path");
    }

    // compare .........................................................................................................

    @Test
    public void testCompareLess() {
        this.compareToAndCheckLess(
            Url.parseRelative("/path1?query2#fragment3"),
            Url.parseRelative("/path1/path2?query2#fragment3")
        );
    }

    @Test
    public void testCompareCaseSensitivity() {
        this.compareToAndCheckLess(
            Url.parseRelative("/ABC?query2#fragment3"),
            Url.parseRelative("/xyz?query2#fragment3")
        );
    }

    @Override
    public RelativeUrl createComparable() {
        return Url.parseRelative("/path1?query2#fragment3");
    }

    // ClassTesting ....................................................................................................

    @Override
    public Class<RelativeUrl> type() {
        return RelativeUrl.class;
    }

    // json.............................................................................................................

    @Override
    public RelativeUrl unmarshall(final JsonNode node,
                                  final JsonNodeUnmarshallContext context) {
        return Url.unmarshallRelative(node, context);
    }
}
