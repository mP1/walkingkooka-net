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
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.ToStringTesting;
import walkingkooka.collect.map.Maps;
import walkingkooka.net.RelativeUrl;
import walkingkooka.net.Url;
import walkingkooka.net.UrlPathName;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class HttpRequestAttributeUrlPathNameTest implements ClassTesting2<HttpRequestAttributeUrlPathName>,
        HashCodeEqualsDefinedTesting2<HttpRequestAttributeUrlPathName>,
        ToStringTesting<HttpRequestAttributeUrlPathName> {

    @Test
    public void testInvalidIndexFails() {
        assertThrows(IllegalArgumentException.class, () -> HttpRequestAttributeUrlPathName.with(-1));
    }

    @Test
    public void testCache() {
        assertSame(HttpRequestAttributeUrlPathName.with(0), HttpRequestAttributeUrlPathName.with(0));
    }

    @Test
    public void testUncached() {
        final int index = HttpRequestAttributeUrlPathName.CONSTANT_COUNT;
        assertNotSame(HttpRequestAttributeUrlPathName.with(index), HttpRequestAttributeUrlPathName.with(index));
    }

    @Test
    public void testParameterValueRequest() {
        final HttpRequestAttributeUrlPathName name = HttpRequestAttributeUrlPathName.with(2);

        assertEquals(Optional.of(UrlPathName.with("path2")),
                name.parameterValue(new FakeHttpRequest() {
                    @Override
                    public RelativeUrl url() {
                        return Url.parseRelative("/path1/path2/path3");
                    }
                }));
    }

    @Test
    public void testParameterValueMap() {
        final HttpRequestAttributeUrlPathName name = HttpRequestAttributeUrlPathName.with(2);
        final UrlPathName value = UrlPathName.with("path2");

        assertEquals(Optional.of(value),
                name.parameterValue(Maps.of(name, value)));
    }

    @Test
    public void testEqualsDifferent() {
        this.checkNotEquals(HttpRequestAttributeUrlPathName.with(1));
    }

    @Test
    public void testSameUncached() {
        final int index = HttpRequestAttributeUrlPathName.CONSTANT_COUNT + 1;
        this.checkEqualsAndHashCode(
                HttpRequestAttributeUrlPathName.with(index),
                HttpRequestAttributeUrlPathName.with(index));
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(HttpRequestAttributeUrlPathName.with(0), "path-0");
    }

    @Override
    public Class<HttpRequestAttributeUrlPathName> type() {
        return HttpRequestAttributeUrlPathName.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }

    @Override
    public HttpRequestAttributeUrlPathName createObject() {
        return HttpRequestAttributeUrlPathName.with(0);
    }
}
