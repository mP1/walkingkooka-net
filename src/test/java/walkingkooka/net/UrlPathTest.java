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
import walkingkooka.collect.iterable.Iterables;
import walkingkooka.collect.iterator.IteratorTesting;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.set.Sets;
import walkingkooka.naming.PathSeparator;
import walkingkooka.naming.PathTesting;
import walkingkooka.test.ClassTesting2;
import walkingkooka.test.ParseStringTesting;
import walkingkooka.type.JavaVisibility;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class UrlPathTest implements ClassTesting2<UrlPath>,
        IteratorTesting,
        PathTesting<UrlPath, UrlPathName>,
        ParseStringTesting<UrlPath> {

    // appendName.......................................................................................................

    @Test
    public void testAppendNameEmptyToEmpty() {
        this.appendNameAndCheck(UrlPath.EMPTY,
                UrlPathName.ROOT,
                "");
    }

    @Test
    public void testAppendNameToEmpty() {
        this.appendNameAndCheck(UrlPath.EMPTY,
                a1(),
                "a1");
    }

    @Test
    public void testAppendNameEmptyToRoot() {
        this.appendNameAndCheck(UrlPath.ROOT,
                UrlPathName.ROOT,
                "/");
    }

    @Test
    public void testAppendNameToRoot() {
        this.appendNameAndCheck(UrlPath.ROOT,
                a1(),
                "/a1");
    }

    @Test
    public void testAppendNameEmptyToLeaf() {
        this.appendNameAndCheck(UrlPath.unnormalized("/a1", a1(), UrlPath.ROOT_PARENT),
                UrlPathName.ROOT,
                "/a1/");
    }

    @Test
    public void testAppendNameToLeaf() {
        this.appendNameAndCheck(UrlPath.unnormalized("/a1", a1(), UrlPath.ROOT_PARENT),
                b2(),
                "/a1/b2");
    }

    private void appendNameAndCheck(final UrlPath path,
                                    final UrlPathName name,
                                    final String value) {
        final UrlPath appended = path.append(name);
        this.valueCheck(appended, value);
        this.nameCheck(appended, name);
        this.parentCheck(appended, path);
    }

    // appendPath.......................................................................................................

    @Test
    public void testAppendPathEmptyToEmpty() {
        this.appendPathAndCheck(UrlPath.EMPTY,
                UrlPath.EMPTY);
    }

    @Test
    public void testAppendPathRootToEmpty() {
        this.appendPathAndCheck(UrlPath.EMPTY,
                UrlPath.ROOT);
    }

    @Test
    public void testAppendPathNormalizedToEmpty() {
        this.appendPathAndCheck(UrlPath.EMPTY,
                UrlPath.normalized("/a1", a1(), UrlPath.EMPTY_PARENT));
    }

    @Test
    public void testAppendPathUnnormalizedToEmpty() {
        this.appendPathAndCheck(UrlPath.EMPTY,
                UrlPath.unnormalized("/a1", a1(), UrlPath.EMPTY_PARENT));
    }

    @Test
    public void testAppendPathEmptyToRoot() {
        this.appendPathAndCheck(UrlPath.ROOT,
                UrlPath.EMPTY);
    }

    @Test
    public void testAppendPathRootToRoot() {
        this.appendPathAndCheck(UrlPath.ROOT,
                UrlPath.ROOT);
    }

    @Test
    public void testAppendPathNormalizedToRoot() {
        this.appendPathAndCheck(UrlPath.ROOT,
                UrlPath.normalized("/a1", a1(), UrlPath.EMPTY_PARENT));
    }

    @Test
    public void testAppendPathUnnormalizedToRoot() {
        this.appendPathAndCheck(UrlPath.ROOT,
                UrlPath.unnormalized("/a1", a1(), UrlPath.EMPTY_PARENT));
    }

    @Test
    public void testAppendPathRootToUnnormalized() {
        final UrlPath path = UrlPath.unnormalized("/a1", a1(), UrlPath.EMPTY_PARENT);
        this.appendPathAndCheck2(path,
                UrlPath.ROOT);
    }

    @Test
    public void testAppendPathEmptyToUnnormalized() {
        final UrlPath start = UrlPath.normalized("/a1", a1(), UrlPath.EMPTY_PARENT);
        this.appendPathAndCheck2(start,
                UrlPath.EMPTY);
    }

    @Test
    public void testAppendPathNormalizedToUnnormalized() {
        final UrlPath start = UrlPath.unnormalized("/a1", a1(), UrlPath.EMPTY_PARENT);
        final UrlPath path = UrlPath.normalized("/b2", b2(), UrlPath.EMPTY_PARENT);
        this.appendPathAndCheck(start,
                path,
                UrlPath.unnormalized("/a1/b2", b2(), Optional.of(start)));
    }

    @Test
    public void testAppendPathUnnormalizedToUnnormalized() {
        final UrlPath start = UrlPath.unnormalized("/a1", a1(), UrlPath.EMPTY_PARENT);
        final UrlPath path = UrlPath.unnormalized("/b2", b2(), UrlPath.EMPTY_PARENT);
        this.appendPathAndCheck(start,
                path,
                UrlPath.unnormalized("/a1/b2", b2(), Optional.of(start)));
    }

    @Test
    public void testAppendPathDotToUnnormalized() {
        final UrlPath start = UrlPath.unnormalized("/a1", a1(), UrlPath.EMPTY_PARENT);
        final UrlPath path = UrlPath.unnormalized("/.", dot(), UrlPath.EMPTY_PARENT);
        this.appendPathAndCheck(start,
                path,
                UrlPath.unnormalized("/a1/.", dot(), Optional.of(start)));
    }

    @Test
    public void testAppendPathDoubleDotToUnnormalized() {
        final UrlPath start = UrlPath.unnormalized("/a1", a1(), UrlPath.EMPTY_PARENT);
        final UrlPath path = UrlPath.unnormalized("/..", doubleDot(), UrlPath.EMPTY_PARENT);
        this.appendPathAndCheck(start,
                path,
                UrlPath.unnormalized("/a1/..", doubleDot(), Optional.of(start)));
    }

    @Test
    public void testAppendPathRootToNormalized() {
        final UrlPath path = UrlPath.normalized("/a1", a1(), UrlPath.EMPTY_PARENT);
        this.appendPathAndCheck2(path,
                UrlPath.ROOT);
    }

    @Test
    public void testAppendPathEmptyToNormalized() {
        final UrlPath start = UrlPath.normalized("/a1", a1(), UrlPath.EMPTY_PARENT);
        this.appendPathAndCheck2(start, UrlPath.EMPTY);
    }

    @Test
    public void testAppendPathDotToNormalized() {
        final UrlPath start = UrlPath.normalized("/a1", a1(), UrlPath.EMPTY_PARENT);
        this.appendPathAndCheck(start,
                UrlPath.unnormalized(".", dot(), UrlPath.EMPTY_PARENT),
                UrlPath.unnormalized("/a1/.", dot(), Optional.of(start)));
    }

    @Test
    public void testAppendPathDoubleDotToNormalized() {
        final UrlPath start = UrlPath.normalized("/a1", a1(), UrlPath.EMPTY_PARENT);
        this.appendPathAndCheck(start,
                UrlPath.unnormalized("..", doubleDot(), UrlPath.EMPTY_PARENT),
                UrlPath.unnormalized("/a1/..", doubleDot(), Optional.of(start)));
    }

    @Test
    public void testAppendPathNormalizedToNormalized() {
        final UrlPath start = UrlPath.normalized("/a1", a1(), UrlPath.EMPTY_PARENT);
        final UrlPath path = UrlPath.normalized("/b2", b2(), UrlPath.EMPTY_PARENT);
        this.appendPathAndCheck(start,
                path,
                UrlPath.normalized("/a1/b2", b2(), Optional.of(start)));
    }

    @Test
    public void testAppendPathUnnormalizedToNormalized() {
        final UrlPath start = UrlPath.normalized("/a1", a1(), UrlPath.EMPTY_PARENT);
        final UrlPath path = UrlPath.unnormalized("/b2", b2(), UrlPath.EMPTY_PARENT);
        this.appendPathAndCheck(start,
                path,
                UrlPath.normalized("/a1/b2", b2(), Optional.of(start)));
    }

    private void appendPathAndCheck(final UrlPath path,
                                    final UrlPath append) {
        assertSame(append,
                path.append(append),
                () -> path + " append path " + append);
    }

    private void appendPathAndCheck2(final UrlPath path, final UrlPath append) {
        assertSame(path,
                path.append(append),
                () -> path + " append path " + append);
    }

    private void appendPathAndCheck(final UrlPath path,
                                    final UrlPath append,
                                    final UrlPath expected) {
        final UrlPath appended = path.append(append);
        this.valueCheck(appended, expected.value());
        this.parentCheck(appended, path);
        assertEquals(expected.isNormalized(), appended.isNormalized(), () -> "normalized " + appended);

        assertEquals(this.names(expected), this.names(appended), "names");
    }

    @Test
    public void testAppendPath() {
        final UrlPath a1 = path(a1());
        final UrlPath b2 = path(b2());
        final UrlPath a1b2 = a1.append(b2);

        this.valueCheck(a1b2, "/a1/b2");
        this.parentCheck(a1b2, a1);
        this.namesCheck(a1b2, UrlPathName.ROOT, a1(), b2());
    }

    @Test
    public void testAppendPathAppendPath() {
        final UrlPath a1 = path(a1());
        final UrlPath b2 = path(b2());
        final UrlPath a1b2 = a1.append(b2);

        this.valueCheck(a1b2, "/a1/b2");
        this.parentCheck(a1b2, a1);

        final UrlPath c3 = path(c3());
        final UrlPath a1b2c3 = a1b2.append(c3);

        this.valueCheck(a1b2c3, "/a1/b2/c3");
        this.parentCheck(a1b2c3, a1b2);
        this.namesCheck(a1b2c3, UrlPathName.ROOT, a1(), b2(), c3());
    }

    private void namesCheck(final UrlPath path, final UrlPathName... names) {
        assertEquals(Lists.of(names),
                names(path),
                path::toString);
    }

    private List<UrlPathName> names(final UrlPath path) {
        return StreamSupport.stream(Iterables.iterator(path.iterator()).spliterator(), false).collect(Collectors.toList());
    }

    private UrlPath path(final UrlPathName name) {
        return UrlPath.normalized("/" + name,
                name,
                UrlPath.ROOT_PARENT);
    }

    // iterator.........................................................................................................

    @Test
    public void testIterator() {
        this.iterateAndCheck(UrlPath.parse("/a1/b2/c3").iterator(),
                UrlPathName.ROOT,
                a1(),
                b2(),
                c3());
    }

    @Test
    public void testIteratorWhenUnnormalized() {
        this.iterateAndCheck(UrlPath.parse("/a1/../b2/./c3").iterator(),
                UrlPathName.ROOT,
                a1(),
                doubleDot(),
                b2(),
                dot(),
                c3());
    }

    private UrlPathName a1() {
        return UrlPathName.with("a1");
    }

    private UrlPathName b2() {
        return UrlPathName.with("b2");
    }

    private UrlPathName c3() {
        return UrlPathName.with("c3");
    }

    private UrlPathName dot() {
        return UrlPathName.with(".");
    }

    private UrlPathName doubleDot() {
        return UrlPathName.with("..");
    }

    // parse............................................................................................................

    @Test
    public void testParseStringEmpty() {
        this.parseStringAndCheck("", UrlPath.EMPTY);
    }

    @Override
    public void testParseStringEmptyFails() {
        // ignored
    }

    @Test
    public void testParseRoot() {
        assertSame(UrlPath.ROOT, this.parseStringAndCheck("/", UrlPath.ROOT));
    }

    @Test
    public void testParseWithLeadingSlash() {
        final UrlPath path = UrlPath.parse("/a1");
        this.valueCheck(path, "/a1");
        this.parentCheck(path, UrlPath.ROOT);
    }

    @Test
    public void testParseWithoutLeadingSlash() {
        final UrlPath path = UrlPath.parse("without-leading-slash");
        this.valueCheck(path, "without-leading-slash");
        this.parentCheck(path, UrlPath.EMPTY);
    }

    @Test
    public void testParseWithoutLeadingSlash2() {
        final UrlPath path = UrlPath.parse("without/leading-slash");
        this.valueCheck(path, "without/leading-slash");
        this.parentCheck(path, "without");

        final UrlPath parent = path.parent().get();
        this.valueCheck(parent, "without");
        this.parentCheck(parent, UrlPath.EMPTY);
    }

    @Test
    public void testParseDoesntNormalizeEmpty() {
        final UrlPath path = UrlPath.parse("/a1//c3/d4");
        this.valueCheck(path, "/a1//c3/d4");
        this.parentCheck(path, "/a1//c3");
    }

    @Test
    public void testParseDoesntNormalizeDot() {
        final UrlPath path = UrlPath.parse("/a1/./c3/d4");
        this.valueCheck(path, "/a1/./c3/d4");
        this.parentCheck(path, "/a1/./c3");
    }

    @Test
    public void testParseDoesntNormalizeDoubleDot() {
        final UrlPath path = UrlPath.parse("/a1/../c3/d4");
        this.valueCheck(path, "/a1/../c3/d4");
        this.parentCheck(path, "/a1/../c3");
    }

    // addQueryString..................................................................................................

    @Test
    public void testAddQueryStringNullFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createPath().addQueryString(null);
        });
    }

    @Test
    public void testAddQueryString() {
        final UrlPath path = this.createPath();
        final UrlQueryString queryString = UrlQueryString.with("a=b");
        final RelativeUrl url = path.addQueryString(queryString);
        assertSame(path, url.path(), "path");
        assertSame(queryString, url.query(), "queryString");
        assertEquals(UrlFragment.EMPTY, url.fragment(), "fragment");
    }

    // normalize........................................................................................................

    @Test
    public void testNormalizeRoot() {
        this.normalizeAndCheck(UrlPath.ROOT);
    }

    @Test
    public void testNormalizeEmpty() {
        this.normalizeAndCheck(UrlPath.EMPTY, UrlPath.ROOT);
    }

    @Test
    public void testNormalizePath() {
        this.normalizeAndCheck(UrlPath.parse("/a1/b2/c3"));
    }

    @Test
    public void testNormalizePathWithoutLeadingSlash() {
        this.normalizeAndCheck("a1/b2/c3", "/a1/b2/c3");
    }

    @Test
    public void testNormalizeEmptyRemoved() {
        this.normalizeAndCheck("/a1//b2/c3", "/a1/b2/c3");
    }

    @Test
    public void testNormalizeDot() {
        this.normalizeAndCheck("/a1/b2/./c3", "/a1/b2/c3");
    }

    @Test
    public void testNormalizeDot2() {
        this.normalizeAndCheck("/a1/b2/./c3/./d4", "/a1/b2/c3/d4");
    }

    @Test
    public void testNormalizeDot32() {
        this.normalizeAndCheck("/./a1", "/a1");
    }

    @Test
    public void testNormalizeDoubleDot() {
        this.normalizeAndCheck("/a1/b2/../c3/d4", "/a1/c3/d4");
    }

    @Test
    public void testNormalizeDoubleDot2() {
        this.normalizeAndCheck("/a1/../b2/c3/../d4", "/b2/d4");
    }

    @Test
    public void testNormalizeDoubleDot3() {
        this.normalizeAndCheck("/../a1/b2", "/a1/b2");
    }

    @Test
    public void testNormalizeDotAndDoubleDot() {
        this.normalizeAndCheck("/a1/../b2/./c3/d4", "/b2/c3/d4");
    }

    private void normalizeAndCheck(final UrlPath path) {
        this.normalizeAndCheck(path, path);
    }

    private void normalizeAndCheck(final String path,
                                   final String expected) {
        this.normalizeAndCheck(UrlPath.parse(path), UrlPath.parse(expected));
    }

    private void normalizeAndCheck(final UrlPath path,
                                   final UrlPath expected) {
        final UrlPath normalized = path.normalize();

        assertEquals(expected, normalized);
        assertEquals(true, normalized.isNormalized(), () -> "normalized " + path);
    }

    // equals/compare....................................................................................................

    @Test
    public void testEqualsDifferentComponent() {
        this.checkNotEquals(UrlPath.parse("/different/path"));
    }

    @Test
    public void testEqualsDifferentName() {
        this.checkNotEquals(UrlPath.parse("/path/different"));
    }

    @Test
    public void testLess() {
        this.compareToAndCheckLess(UrlPath.parse("/path/to/zzzzz"));
    }

    // toString.........................................................................................................

    @Override
    public void testCheckToStringOverridden() {
    }

    // helpers..........................................................................................................

    @Override
    public UrlPath createPath() {
        return UrlPath.parse("/a1");
    }

    @Override
    public UrlPathName createName(final int n) {
        final char c = (char) ('a' + n);
        return UrlPathName.with(c + "" + n);
    }

    @Override
    public UrlPath root() {
        return UrlPath.ROOT;
    }

    @Override
    public UrlPath parsePath(final String name) {
        return UrlPath.parse(name);
    }

    @Override
    public PathSeparator separator() {
        return UrlPath.SEPARATOR;
    }

    @Override
    public UrlPath createComparable() {
        return UrlPath.parse("/path/to/resource");
    }

    // ConstantTesting .................................................................................................

    @Override
    public Set<UrlPath> intentionalDuplicateConstants() {
        return Sets.of(UrlPath.ROOT);
    }

    // ParseStringTesting ..............................................................................................

    @Override
    public UrlPath parseString(final String text) {
        return UrlPath.parse(text);
    }

    @Override
    public RuntimeException parseStringFailedExpected(final RuntimeException expected) {
        return expected;
    }

    @Override
    public Class<? extends RuntimeException> parseStringFailedExpected(final Class<? extends RuntimeException> expected) {
        return expected;
    }

    // ClassTestCase ...................................................................................................

    @Override
    public Class<UrlPath> type() {
        return UrlPath.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
