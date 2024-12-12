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


import walkingkooka.collect.list.Lists;
import walkingkooka.naming.Path;
import walkingkooka.naming.PathSeparator;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * A {@link Path} which may be part of a {@link Url} after the host and port but before any present query string or anchor.
 */
public abstract class UrlPath implements Path<UrlPath, UrlPathName>,
        Comparable<UrlPath> {

    /**
     * {@link PathSeparator} instance
     */
    public final static PathSeparator SEPARATOR = PathSeparator.requiredAtStart('/');

    /**
     * Constant used to indicate no parent.
     */
    final static Optional<UrlPath> NO_PARENT = Optional.empty();

    /**
     * Singleton {@link UrlPath} with a {@link PathSeparator#string()}.
     */
    @SuppressWarnings("StaticInitializerReferencesSubClass")
    public final static UrlPath ROOT = UrlPathRoot.root();

    final static Optional<UrlPath> ROOT_PARENT = Optional.of(ROOT);

    /**
     * Singleton {@link UrlPath} with an empty {@link String path}.
     */
    @SuppressWarnings("StaticInitializerReferencesSubClass")
    public final static UrlPath EMPTY = UrlPathEmpty.empty();

    final static Optional<UrlPath> EMPTY_PARENT = Optional.of(EMPTY);

    static UrlPathLeafNormalized normalized(final String path,
                                            final UrlPathName name,
                                            final Optional<UrlPath> parent) {
        return UrlPathLeafNormalized.withNormalized(path, name, parent);
    }

    static UrlPathLeafUnnormalized unnormalized(final String path,
                                                final UrlPathName name,
                                                final Optional<UrlPath> parent) {
        return UrlPathLeafUnnormalized.withUnnormalized(path, name, parent);
    }

    // parse............................................................................................................

    /**
     * Creates a new {@link UrlPath}. Note that the entire ancestor hierarchy is created while breaking the path up into components.
     */
    public static UrlPath parse(final String path) {
        Objects.requireNonNull(path, "path");

        return path.isEmpty() ?
                EMPTY :
                path.equals(SEPARATOR.string()) ?
                        ROOT :
                        parse0(path);
    }

    /**
     * Parses and creates the path chain.
     */
    private static UrlPath parse0(final String value) {
        final boolean slash = value.charAt(0) == SEPARATOR.character();
        return parse1(value,
                slash ? 1 : 0,
                slash ? ROOT : EMPTY);
    }

    private static UrlPath parse1(final String value,
                                  final int start,
                                  final UrlPath parent) {
        UrlPath path = parent;

        final char separator = SEPARATOR.character();
        final int length = value.length();
        int begin = start;

        for (; ; ) {
            final int end = value.indexOf(separator, begin);

            // special case for trailing separator, dont want to append an empty UrlPathName
            if (begin == length) {
                path = path.parseTrailingSlash();
                break;
            }

            if (-1 == end) {
                path = path.append(UrlPathName.with(value.substring(begin, length)));
                break;
            }

            path = path.append(
                    UrlPathName.with(
                            value.substring(begin, end)
                    )
            );
            begin = end + 1;
        }

        return path;
    }

    /**
     * Package private to limit sub classing.
     */
    UrlPath() {
        super();
    }

    /**
     * {@link PathSeparator} getter.
     */
    @Override
    public final PathSeparator separator() {
        return UrlPath.SEPARATOR;
    }

    @Override
    public final UrlPath append(final UrlPathName name) {
        Objects.requireNonNull(name, "name");

        return this.appendName(name, this);
    }

    abstract UrlPath appendName(final UrlPathName name, final UrlPath parent);

    @Override
    public final UrlPath append(final UrlPath path) {
        Objects.requireNonNull(path, "path");

        return this.appendPath(path);
    }

    abstract UrlPath appendPath(final UrlPath path);

    abstract UrlPath appendTo(final UrlPathLeaf leaf);

    /**
     * This is only called by {@link #parse1(String, int, UrlPath)}, when the given string ends in a slash.
     * This is necessary otherwise the {@link UrlPath} will have a {@link UrlPathName} that is empty causing
     * an empty path component when a new name is appended.
     */
    abstract UrlPath parseTrailingSlash();

    /**
     * Adds a query string to this path returning a {@link RelativeUrl}
     */
    public final RelativeUrl addQueryString(final UrlQueryString queryString) {
        Objects.requireNonNull(queryString, "queryString");

        return Url.relative(this, queryString, UrlFragment.EMPTY);
    }

    /**
     * Returns true if this path is normalized.
     */
    public final boolean isNormalized() {
        return this instanceof UrlPathLeafNormalized || this instanceof UrlPathRoot;
    }

    /**
     * Returns a {@link UrlPath} that performs the following normalization rules.
     * <ul>
     * <li>An empty path is replaced with "/"</li>
     * <li>Dot segments <code>.</code> are removed</li>
     * <li>Double dot segments <code>..</code> cause the parent to be removed</li>
     * </ul>
     * <br>
     * <a href="https://en.wikipedia.org/wiki/URI_normalization">URI normalization</a>
     * <pre>
     * Removing dot-segments. Dot-segments . and .. in the path component of the URI should be removed by applying the remove_dot_segments algorithm[5] to the path described in RFC 3986.[6] Example:
     * http://example.com/foo/./bar/baz/../qux → http://example.com/foo/bar/qux
     *
     * Converting an empty path to a "/" path. In presence of an authority component, an empty path component should be normalized to a path component of "/".[7] Example:
     * http://example.com → http://example.com/
     *
     * Removing duplicate slashes Paths which include two adjacent slashes could be converted to one. Example:
     * http://example.com/foo//bar.html → http://example.com/foo/bar.html
     * </pre>
     */
    public abstract UrlPath normalize();

    /**
     * Returns the parent of this path or itself if its a root.
     */
    abstract UrlPath parentOrSelf();

    // pathNameList.....................................................................................................

    /**
     * Returns a {@link List} view of all {@link UrlPathName}, effectively the names of all parents.
     */
    public final List<UrlPathName> namesList() {
        final List<UrlPathName> list = Lists.array();

        this.appendNameAndParent(list);

        return Lists.readOnly(list);
    }

    abstract void appendNameAndParent(final List<UrlPathName> names);

    // Comparable........................................................................................................

    @Override
    public final int hashCode() {
        return this.value().hashCode();
    }

    @Override
    public final boolean equals(final Object other) {
        return this == other ||
                other instanceof UrlPath &&
                        this.equals0((UrlPath) other);
    }

    private boolean equals0(final UrlPath other) {
        return this.value().equals(other.value());
    }

    // Comparable........................................................................................................

    @Override
    public final int compareTo(final UrlPath path) {
        return this.toString().compareTo(path.toString());
    }
}
