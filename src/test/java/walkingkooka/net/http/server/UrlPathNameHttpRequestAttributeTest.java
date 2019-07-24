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
import walkingkooka.collect.map.Maps;
import walkingkooka.net.RelativeUrl;
import walkingkooka.net.Url;
import walkingkooka.net.UrlPathName;
import walkingkooka.test.ClassTesting2;
import walkingkooka.test.HashCodeEqualsDefinedTesting;
import walkingkooka.test.ToStringTesting;
import walkingkooka.type.JavaVisibility;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class UrlPathNameHttpRequestAttributeTest implements ClassTesting2<UrlPathNameHttpRequestAttribute>,
        HashCodeEqualsDefinedTesting<UrlPathNameHttpRequestAttribute>,
        ToStringTesting<UrlPathNameHttpRequestAttribute> {

    @Test
    public void testInvalidIndexFails() {
        assertThrows(IllegalArgumentException.class, () -> {
            UrlPathNameHttpRequestAttribute.with(-1);
        });
    }

    @Test
    public void testCache() {
        assertSame(UrlPathNameHttpRequestAttribute.with(0), UrlPathNameHttpRequestAttribute.with(0));
    }

    @Test
    public void testUncached() {
        final int index = UrlPathNameHttpRequestAttribute.CONSTANT_COUNT;
        assertNotSame(UrlPathNameHttpRequestAttribute.with(index), UrlPathNameHttpRequestAttribute.with(index));
    }

    @Test
    public void testParameterValueRequest() {
        final UrlPathNameHttpRequestAttribute name = UrlPathNameHttpRequestAttribute.with(2);

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
        final UrlPathNameHttpRequestAttribute name = UrlPathNameHttpRequestAttribute.with(2);
        final UrlPathName value = UrlPathName.with("path2");

        assertEquals(Optional.of(value),
                name.parameterValue(Maps.of(name, value)));
    }

    @Test
    public void testEqualsDifferent() {
        this.checkNotEquals(UrlPathNameHttpRequestAttribute.with(1));
    }

    @Test
    public void testSameUncached() {
        final int index = UrlPathNameHttpRequestAttribute.CONSTANT_COUNT + 1;
        this.checkEqualsAndHashCode(
                UrlPathNameHttpRequestAttribute.with(index),
                UrlPathNameHttpRequestAttribute.with(index));
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(UrlPathNameHttpRequestAttribute.with(0), "path-0");
    }

    @Override
    public Class<UrlPathNameHttpRequestAttribute> type() {
        return UrlPathNameHttpRequestAttribute.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }

    @Override
    public UrlPathNameHttpRequestAttribute createObject() {
        return UrlPathNameHttpRequestAttribute.with(0);
    }
}
