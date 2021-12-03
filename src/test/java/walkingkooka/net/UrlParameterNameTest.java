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
import walkingkooka.Cast;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.map.Maps;
import walkingkooka.naming.NameTesting;
import walkingkooka.net.http.server.FakeHttpRequest;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.text.CaseSensitivity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class UrlParameterNameTest implements ClassTesting2<UrlParameterName>,
        NameTesting<UrlParameterName, UrlParameterName> {

    @Override
    public void testEmptyFails() {
        throw new UnsupportedOperationException();
    }

    @Test
    public void testWithEncoding() {
        this.createNameAndCheck("abc%20xyz");
    }

    // firstParameterValue.......................................................................................

    @Test
    public void testFirstParameterValueNullParametersFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createObject().firstParameterValue(null)
        );
    }

    @Test
    public void testFirstParameterValueMissing() {
        this.firstParameterValueAndCheck(Maps.empty(), null);
    }

    @Test
    public void testFirstParameterValueMissing2() {
        this.firstParameterValueAndCheck(Maps.of(UrlParameterName.with("different"), Lists.of("ignored")), null);
    }

    @Test
    public void testFirstParameterValueOne() {
        final String value = "abc123";

        this.firstParameterValueAndCheck(Maps.of(this.createObject(), Lists.of(value)), value);
    }

    @Test
    public void testFirstParameterValueMany() {
        final String value = "abc123";

        this.firstParameterValueAndCheck(Maps.of(this.createObject(), Lists.of(value, "2nd")), value);
    }

    private void firstParameterValueAndCheck(final Map<UrlParameterName, List<String>> parameters,
                                             final String expected) {
        this.checkEquals(
                Optional.ofNullable(expected),
                this.createObject().firstParameterValue(Cast.to(parameters)),
                () -> "firstParameterValue of " + parameters
        );
    }

    // firstParameterValueOrFail.......................................................................................

    @Test
    public void testFirstParameterValueOrFailNullParametersFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createObject().firstParameterValueOrFail(null)
        );
    }

    @Test
    public void testFirstParameterValueOrFailMissing() {
        final IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> this.createObject().firstParameterValueOrFail(Maps.empty())
        );
        this.checkEquals("Missing parameter \"param-1\"", thrown.getMessage(), "message");
    }

    @Test
    public void testFirstParameterValueOrFailMissing2() {
        final IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> this.createObject().firstParameterValueOrFail(Maps.of(UrlParameterName.with("different"), Lists.of("1a", "2b")))
        );
        this.checkEquals("Missing parameter \"param-1\"", thrown.getMessage(), "message");
    }

    @Test
    public void testFirstParameterValueOrFailOne() {
        final String value = "abc123";

        this.firstParameterValueOrFailAndCheck(Maps.of(this.createObject(), Lists.of(value)), value);
    }

    @Test
    public void testFirstParameterValueOrFailMany() {
        final String value = "abc123";

        this.firstParameterValueOrFailAndCheck(Maps.of(this.createObject(), Lists.of(value, "2nd")), value);
    }

    private void firstParameterValueOrFailAndCheck(final Map<UrlParameterName, List<String>> parameters,
                                                   final String expected) {
        this.checkEquals(
                expected,
                this.createObject().firstParameterValueOrFail(Cast.to(parameters)),
                () -> "firstParameterValueOrFail of " + parameters
        );
    }

    // parameterValueOrFail.......................................................................................

    @Test
    public void testParameterValueOrFailNullParametersFail() {
        this.parameterValueOrFail(null,
                Function.identity(),
                NullPointerException.class);
    }

    @Test
    public void testParameterValueOrFailNullConverterFail() {
        this.parameterValueOrFail(Maps.empty(),
                null,
                NullPointerException.class);
    }

    @Test
    public void testParameterValueOrFailMissing() {
        this.parameterValueOrFail(Maps.empty(),
                Function.identity(),
                IllegalArgumentException.class);
    }

    @Test
    public void testParameterValueOrFailSeveralValuesFails() {
        final UrlParameterName parameter = this.createComparable();
        final Map<HttpRequestAttribute<?>, ?> parameters = Maps.of(parameter, Lists.of("1", "2", "3"));

        this.parameterValueOrFail(parameter,
                parameters,
                Function.identity(),
                IllegalArgumentException.class);
    }

    @Test
    public void testParameterValueOrFailConverterFails() {
        final UrlParameterName parameter = this.createComparable();
        final Map<HttpRequestAttribute<?>, ?> parameters = Maps.of(parameter, Lists.of("A!!!"));

        this.parameterValueOrFail(parameter,
                parameters,
                BigDecimal::new,
                IllegalArgumentException.class);
    }

    private void parameterValueOrFail(final Map<HttpRequestAttribute<?>, ?> parameters,
                                      final Function<String, ?> converter,
                                      final Class<? extends Throwable> thrown) {
        this.parameterValueOrFail(this.createComparable(),
                parameters,
                converter,
                thrown);
    }

    private void parameterValueOrFail(final UrlParameterName parameter,
                                      final Map<HttpRequestAttribute<?>, ?> parameters,
                                      final Function<String, ?> converter,
                                      final Class<? extends Throwable> thrown) {
        assertThrows(thrown, () -> this.createComparable().parameterValueOrFail(parameters, converter));
    }

    @Test
    public void testParameterValueOrFail() {
        final UrlParameterName parameter = this.createComparable();
        final Map<HttpRequestAttribute<?>, ?> parameters = Maps.of(parameter, Lists.of("123"));
        this.checkEquals(BigDecimal.valueOf(123),
                parameter.parameterValueOrFail(parameters,
                        BigDecimal::new));
    }

    // HttpRequestAttribute...............................................

    @Test
    public void testParameterValueRequest() {
        final UrlParameterName name = this.createName("param1");
        this.checkEquals(Optional.of(Lists.of("value1")),
                name.parameterValue(new FakeHttpRequest() {

                    @Override
                    public RelativeUrl url() {
                        return Url.parseRelative("/file?param2=value2&param1=value1");
                    }
                }));
    }

    @Test
    public void testParameterValueMap() {
        final UrlParameterName name = this.createName("param1");
        this.checkEquals(Optional.of(Lists.of("value1")),
                name.parameterValue(UrlQueryString.with("param1=value1&param2=value2").parameters()));
    }

    // Comparable.......................................................................................................

    @Test
    public void testCompareToArraySort() {
        final UrlParameterName a1 = UrlParameterName.with("A1");
        final UrlParameterName b2 = UrlParameterName.with("B2");
        final UrlParameterName c3 = UrlParameterName.with("c3");
        final UrlParameterName d4 = UrlParameterName.with("d4");

        this.compareToArraySortAndCheck(d4, a1, c3, b2,
                a1, b2, c3, d4);
    }

    @Override
    public UrlParameterName createName(final String name) {
        return UrlParameterName.with(name);
    }

    @Override
    public CaseSensitivity caseSensitivity() {
        return CaseSensitivity.SENSITIVE;
    }

    @Override
    public String nameText() {
        return "param-1";
    }

    @Override
    public String differentNameText() {
        return "different";
    }

    @Override
    public String nameTextLess() {
        return "aaa";
    }

    @Override
    public Class<UrlParameterName> type() {
        return UrlParameterName.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
