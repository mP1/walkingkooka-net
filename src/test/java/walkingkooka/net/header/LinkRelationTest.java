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
import walkingkooka.collect.list.Lists;
import walkingkooka.compare.ComparableTesting;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.UrlPathName;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.test.ParseStringTesting;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class LinkRelationTest extends LinkRelationTestCase<LinkRelation<Object>, Object>
    implements ComparableTesting,
    ParseStringTesting<List<LinkRelation<?>>> {

    private final static String TEXT = "abc123";
    private final static String URL_TEXT = "https://example.com";

    @Override
    public void testWith2() {
        // ignored
    }

    @Test
    public void testWithText() {
        final LinkRelation<?> linkRelation = LinkRelation.with(TEXT);
        this.checkEquals(TEXT, linkRelation.value(), "value");
    }

    @Test
    public void testWithUrl() {
        final LinkRelation<?> linkRelation = LinkRelation.with(URL_TEXT);
        this.checkEquals(AbsoluteUrl.parse(URL_TEXT), linkRelation.value(), "value");
    }

    @Test
    public void testHeaderTextText() {
        this.toHeaderTextAndCheck(LinkRelation.with(TEXT), TEXT);
    }

    @Test
    public void testHeaderTextUrl() {
        this.toHeaderTextAndCheck(LinkRelation.with(URL_TEXT), URL_TEXT);
    }

    @Test
    public void testParse() {
        this.parseStringAndCheck("self https://example.com", Lists.of(LinkRelation.SELF, LinkRelation.with("https://example.com")));
    }

    @Test
    public void testSortSelfBeforeRegular() {
        this.compareToArraySortAndCheck(LinkRelation.SELF,
            LinkRelation.ABOUT,
            LinkRelation.BOOKMARK,
            LinkRelation.CONTENTS,
            LinkRelation.SELF,
            LinkRelation.ABOUT,
            LinkRelation.BOOKMARK,
            LinkRelation.CONTENTS);
    }

    @Test
    public void testSortSelfBeforeRegular2() {
        this.compareToArraySortAndCheck(LinkRelation.BOOKMARK,
            LinkRelation.CONTENTS,
            LinkRelation.SELF,
            LinkRelation.ABOUT,
            LinkRelation.SELF,
            LinkRelation.ABOUT,
            LinkRelation.BOOKMARK,
            LinkRelation.CONTENTS);
    }

    @Test
    public void testSortRegularBeforeHyperlinked() {
        final LinkRelation<?> about = LinkRelation.ABOUT;
        final LinkRelation<?> bookmark = LinkRelation.BOOKMARK;
        final LinkRelation<?> url = LinkRelation.with("https://example.com");

        this.compareToArraySortAndCheck(bookmark, url, about,
            about, bookmark, url);
    }

    @Test
    public void testSortRegularBeforeHyperlinked2() {
        final LinkRelation<?> about = LinkRelation.ABOUT;
        final LinkRelation<?> custom = LinkRelation.with("custom123");
        final LinkRelation<?> url = LinkRelation.with("https://example.com");

        this.compareToArraySortAndCheck(url, custom, about,
            about, custom, url);
    }

    @Test
    public void testSortSelfBeforeHyperlinked() {
        final LinkRelation<?> self = LinkRelation.SELF;
        final LinkRelation<?> url = LinkRelation.with("https://example.com");

        this.compareToArraySortAndCheck(url, self,
            self, url);
    }

    @Test
    public void testSortSelfThenRegularThenHyperlinked() {
        final LinkRelation<?> about = LinkRelation.ABOUT;
        final LinkRelation<?> bookmark = LinkRelation.BOOKMARK;
        final LinkRelation<?> self = LinkRelation.SELF;
        final LinkRelation<?> url = LinkRelation.with("https://example.com");
        final LinkRelation<?> url2 = LinkRelation.with("https://example2.com");

        this.compareToArraySortAndCheck(url2, bookmark, about, self, url,
            self, about, bookmark, url, url2);
    }

    @Override
    boolean url() {
        return false; // TEXT is not a URL_TEXT
    }

    @Override
    LinkRelation<Object> createLinkRelation(final Object value) {
        return Cast.to(LinkRelation.with((String) value));
    }

    @Override
    Object value() {
        return TEXT;
    }

    @Override
    Object differentValue() {
        return URL_TEXT;
    }

    @Override
    public Class<LinkRelation<Object>> type() {
        return Cast.to(LinkRelation.class);
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
    // ParseStringTesting ........................................................................................

    @Override
    public List<LinkRelation<?>> parseString(final String text) {
        return LinkRelation.parse(text);
    }

    // ToUrlPathName....................................................................................................

    @Test
    public void testToUrlPathNameSelfFails() {
        assertThrows(
            IllegalStateException.class,
            () -> LinkRelation.SELF.toUrlPathName()
        );
    }

    @Test
    public void testToUrlPathNameUrlFails() {
        assertThrows(
            IllegalStateException.class,
            () -> LinkRelation.parse("https://example.com/123")
                .get(0)
                .toUrlPathName()
        );
    }

    @Test
    public void testToUrlPathNameAbout() {
        this.toUrlPathNameAndCheck(
            LinkRelation.ABOUT,
            UrlPathName.with("about")
        );
    }

    @Test
    public void testToUrlPathNameNext() {
        this.toUrlPathNameAndCheck(
            LinkRelation.NEXT,
            UrlPathName.with("next")
        );
    }

    private void toUrlPathNameAndCheck(final LinkRelation<?> linkRelation,
                                       final UrlPathName expected) {
        this.checkEquals(
            expected,
            linkRelation.toUrlPathName()
        );
    }
}
