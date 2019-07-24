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
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.map.MapTesting2;
import walkingkooka.collect.map.Maps;
import walkingkooka.test.ClassTesting2;
import walkingkooka.type.JavaVisibility;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class HttpServletRequestHttpRequestParametersMapTest implements ClassTesting2<HttpServletRequestHttpRequestParametersMap>,
        MapTesting2<HttpServletRequestHttpRequestParametersMap, HttpRequestParameterName, List<String>> {

    private final static String KEY1 = "parameter1";
    private final static String VALUE1A = "value1a";
    private final static String VALUE1B = "value1b";

    private final static String KEY2 = "parameter2";
    private final static String VALUE2 = "value2";

    @Test
    public void testContainsKey() {
        this.containsKeyAndCheck(this.createMap(), HttpRequestParameterName.with(KEY1));
    }

    @Test
    public void testContainsKey2() {
        this.containsKeyAndCheck(this.createMap(), HttpRequestParameterName.with(KEY2));
    }

    @Test
    public void testContainsKeyAbsent() {
        this.containsKeyAndCheckAbsent(HttpRequestParameterName.with("absent"));
    }

    @Test
    public void testGet() {
        this.getAndCheck(this.createMap(),
                HttpRequestParameterName.with(KEY1),
                Lists.of(VALUE1A, VALUE1B));
    }

    @Test
    public void testGet2() {
        this.getAndCheck(this.createMap(),
                HttpRequestParameterName.with(KEY2),
                Lists.of(VALUE2));
    }

    @Test
    public void testGetOrDefaultPresent() {
        this.getDefaultAndCheck(HttpRequestParameterName.with(KEY2),
                Lists.of("DEFAULT!"),
                Lists.of(VALUE2));
    }

    @Test
    public void testGetOrDefaultAbsentNonHttpRequestParameterNameKey() {
        final List<String> defaultValue = Lists.of("DEFAULT!");

        this.getDefaultAndCheck("Unknown",
                defaultValue,
                defaultValue);
    }

    @Test
    public void testGetOrDefaultAbsent() {
        final List<String> defaultValue = Lists.of("DEFAULT!");

        this.getDefaultAndCheck(HttpRequestParameterName.with("Unknown"),
                defaultValue,
                defaultValue);
    }

    private void getDefaultAndCheck(final Object key,
                                    final List<String> defaultValue,
                                    final Object value) {
        final HttpServletRequestHttpRequestParametersMap map = this.createMap();
        assertEquals(value,
                map.getOrDefault(key, defaultValue),
                () -> "get " + key + " with default " + defaultValue + " from " + map);
    }

    @Test
    public void testSize() {
        this.sizeAndCheck(this.createMap(), 2);
    }

    @Test
    public void testToStringEmpty() {
        this.toStringAndCheck(HttpServletRequestHttpRequestParametersMap.with(Maps.empty()), "{}");
    }

    @Test
    public void testToStringNotEmpty() {
        this.toStringAndCheck(this.createMap(), "{parameter1=\"value1a\", \"value1b\", parameter2=\"value2\"}");
    }

    @Override
    public HttpServletRequestHttpRequestParametersMap createMap() {
        return HttpServletRequestHttpRequestParametersMap.with(Maps.of(KEY1, array(VALUE1A, VALUE1B), KEY2, array(VALUE2)));
    }

    private static String[] array(final String... values) {
        return values;
    }

    @Override
    public Class<HttpServletRequestHttpRequestParametersMap> type() {
        return HttpServletRequestHttpRequestParametersMap.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}
