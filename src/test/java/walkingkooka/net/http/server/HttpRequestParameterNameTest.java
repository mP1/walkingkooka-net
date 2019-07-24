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
import walkingkooka.collect.map.Maps;
import walkingkooka.naming.NameTesting;
import walkingkooka.test.ClassTesting2;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.type.JavaVisibility;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

final public class HttpRequestParameterNameTest implements ClassTesting2<HttpRequestParameterName>,
        NameTesting<HttpRequestParameterName, HttpRequestParameterName> {

    @Test
    public void testParameterValueRequest() {
        final HttpRequestParameterName name = HttpRequestParameterName.with("parameter1");
        final List<String> value = Lists.of("a", "b", "c");

        assertEquals(Optional.of(value),
                name.parameterValue(new FakeHttpRequest() {
                    @Override
                    public List<String> parameterValues(final HttpRequestParameterName n) {
                        assertSame(name, n);
                        return value;
                    }
                }));
    }

    @Test
    public void testParameterValueMap() {
        final HttpRequestParameterName name = HttpRequestParameterName.with("parameter1");
        final List<String> value = Lists.of("a", "b", "c");

        assertEquals(Optional.of(value),
                name.parameterValue(Maps.of(name, value)));
    }

    @Test
    public void testCompareToArraySort() {
        final HttpRequestParameterName a1 = HttpRequestParameterName.with("A1");
        final HttpRequestParameterName b2 = HttpRequestParameterName.with("B2");
        final HttpRequestParameterName c3 = HttpRequestParameterName.with("c3");
        final HttpRequestParameterName d4 = HttpRequestParameterName.with("d4");

        this.compareToArraySortAndCheck(d4, a1, c3, b2,
                a1, b2, c3, d4);
    }

    @Override
    public HttpRequestParameterName createName(final String name) {
        return HttpRequestParameterName.with(name);
    }

    @Override
    public CaseSensitivity caseSensitivity() {
        return CaseSensitivity.SENSITIVE;
    }

    @Override
    public String nameText() {
        return "param2";
    }

    @Override
    public String differentNameText() {
        return "param99";
    }

    @Override
    public String nameTextLess() {
        return "param1";
    }

    @Override
    public Class<HttpRequestParameterName> type() {
        return HttpRequestParameterName.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
