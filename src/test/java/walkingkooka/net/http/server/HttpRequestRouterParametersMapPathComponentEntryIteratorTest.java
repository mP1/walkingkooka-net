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

package walkingkooka.net.http.server;

import org.junit.jupiter.api.Test;
import walkingkooka.ToStringTesting;
import walkingkooka.collect.iterator.IteratorTesting;
import walkingkooka.net.UrlPathName;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.reflect.TypeNameTesting;

import java.util.Iterator;
import java.util.Map.Entry;

public final class HttpRequestRouterParametersMapPathComponentEntryIteratorTest implements ClassTesting2<HttpRequestRouterParametersMapPathComponentEntryIterator>,
    IteratorTesting,
    ToStringTesting<HttpRequestRouterParametersMapPathComponentEntryIterator>,
    TypeNameTesting<HttpRequestRouterParametersMapPathComponentEntryIterator> {

    private final static UrlPathName NAME1 = UrlPathName.with("a");
    private final static UrlPathName NAME2 = UrlPathName.with("b");
    private final static UrlPathName NAME3 = UrlPathName.with("c");

    @Test
    public void testHasNextAndNextConsumeEmpty() {
        this.iterateAndCheck(true);
    }

    @Test
    public void testHasNextAndNextConsumeOneCookie() {
        this.iterateAndCheck(true, NAME1);
    }

    @Test
    public void testHasNextAndNextConsume() {
        this.iterateAndCheck(true, NAME1, NAME2);
    }

    @Test
    public void testHasNextAndNextConsume2() {
        this.iterateAndCheck(true, NAME2, NAME1);
    }

    @Test
    public void testHasNextAndNextConsume3() {
        this.iterateAndCheck(true, NAME1, NAME2, NAME3);
    }

    @Test
    public void testNextOnlyConsume() {
        this.iterateAndCheck(false, NAME2, NAME1);
    }

    private void iterateAndCheck(final boolean checkHasNext,
                                 final UrlPathName... names) {
        final HttpRequestRouterParametersMapPathComponentEntryIterator iterator = this.createIterator(names);
        this.hasNextCheckTrue(iterator);

        final Entry<HttpRequestAttribute<?>, Object> entry = iterator.next();
        this.checkEquals(HttpRequestAttributes.PATH_COMPONENT_COUNT, entry.getKey(), "key");
        this.checkEquals(names.length, entry.getValue(), "value");

        for (int i = 0; i < names.length; i++) {
            if (checkHasNext) {
                this.hasNextCheckTrue(iterator, "iterator should have " + (names.length - i) + " entries left");
            }
            this.checkNext(iterator, i, names[i]);
        }

        this.hasNextCheckFalse(iterator);
        this.nextFails(iterator);
    }

    private void checkNext(final HttpRequestRouterParametersMapPathComponentEntryIterator iterator,
                           final int position,
                           final UrlPathName name) {
        final Entry<HttpRequestAttribute<?>, Object> entry = iterator.next();
        this.checkEquals(HttpRequestAttributes.pathComponent(position), entry.getKey(), "key");
        this.checkEquals(name, entry.getValue(), "value");
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createIterator(NAME1), "PATH_COMPONENT_COUNT=1");
    }

    @Test
    public void testToStringAfterPathComponent() {
        final HttpRequestRouterParametersMapPathComponentEntryIterator iterator = this.createIterator(NAME1);
        iterator.next();
        this.toStringAndCheck(iterator, "path-0=a");
    }

    @Test
    public void testToStringEmpty() {
        final HttpRequestRouterParametersMapPathComponentEntryIterator iterator = this.createIterator(NAME1);
        iterator.next();
        iterator.next();
        this.toStringAndCheck(iterator, "");
    }

    private HttpRequestRouterParametersMapPathComponentEntryIterator createIterator(final UrlPathName... names) {
        return HttpRequestRouterParametersMapPathComponentEntryIterator.with(names);
    }

    @Override
    public Class<HttpRequestRouterParametersMapPathComponentEntryIterator> type() {
        return HttpRequestRouterParametersMapPathComponentEntryIterator.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }

    @Override
    public String typeNamePrefix() {
        return HttpRequestRouterParametersMap.class.getSimpleName();
    }

    @Override
    public String typeNameSuffix() {
        return Iterator.class.getSimpleName();
    }
}
