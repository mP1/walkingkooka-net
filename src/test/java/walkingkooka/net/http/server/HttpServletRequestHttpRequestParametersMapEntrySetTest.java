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
import walkingkooka.collect.set.SetTesting2;
import walkingkooka.collect.set.Sets;

import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

public final class HttpServletRequestHttpRequestParametersMapEntrySetTest extends HttpServletRequestTestCase<HttpServletRequestHttpRequestParametersMapEntrySet>
        implements SetTesting2<HttpServletRequestHttpRequestParametersMapEntrySet, Entry<HttpRequestParameterName, List<String>>> {

    private final static String KEY1 = "parameter1";
    private final static String VALUE1A = "value1a";
    private final static String VALUE1B = "value1b";

    private final static String KEY2 = "parameter2";
    private final static String VALUE2 = "value2";

    @Test
    public void testAddFails() {
        this.addFails(this.createSet(),
                this.entry(HttpRequestParameterName.with(KEY1), VALUE1A));
    }

    @Test
    public void testContains() {
        this.containsAndCheck(this.createSet(),
                this.entry(HttpRequestParameterName.with(KEY1), VALUE1A, VALUE1B));
    }

    @Test
    public void testContains2() {
        this.containsAndCheck(this.createSet(),
                this.entry(HttpRequestParameterName.with(KEY2), VALUE2));
    }

    @Test
    public void testRemoveFails() {
        this.removeFails(this.createSet(),
                this.entry(HttpRequestParameterName.with(KEY1), VALUE1A));
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createSet(),
                "[parameter1=[value1a, value1b], parameter2=[value2]]");
    }

    @Override
    public HttpServletRequestHttpRequestParametersMapEntrySet createSet() {
        final Set<Entry<String, String[]>> parameters = Sets.ordered();
        parameters.add(this.entry(KEY1, VALUE1A, VALUE1B));
        parameters.add(this.entry(KEY2, VALUE2));

        return HttpServletRequestHttpRequestParametersMapEntrySet.with(parameters);
    }

    private Entry<String, String[]> entry(final String key, final String... values) {
        return Maps.entry(key, values.clone());
    }

    private HttpServletRequestHttpRequestParametersMapEntrySetIteratorEntry entry(final HttpRequestParameterName key,
                                                                                  final String... values) {
        return HttpServletRequestHttpRequestParametersMapEntrySetIteratorEntry.with(this.entry(key.value(), values));
    }

    @Override
    public Class<HttpServletRequestHttpRequestParametersMapEntrySet> type() {
        return HttpServletRequestHttpRequestParametersMapEntrySet.class;
    }
}
