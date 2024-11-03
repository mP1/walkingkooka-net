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
import walkingkooka.ToStringTesting;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.map.Maps;
import walkingkooka.collect.set.Sets;
import walkingkooka.compare.ComparableTesting2;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.test.ParseStringTesting;
import walkingkooka.text.CharSequences;
import walkingkooka.text.HasTextTesting;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class UrlQueryStringTest implements ClassTesting2<UrlQueryString>,
        ParseStringTesting<UrlQueryString>,
        ToStringTesting<UrlQueryString>,
        ComparableTesting2<UrlQueryString>,
        HasTextTesting {

    @Override
    public void testParseStringEmptyFails() {
        // nop
    }

    @Test
    public void testWithEmpty() {
        final UrlQueryString queryString = UrlQueryString.parse("");
        assertSame(UrlQueryString.EMPTY, queryString);

        this.checkEquals(true, queryString.isEmpty(), "isEmpty");

        this.checkEquals(Maps.empty(), queryString.parameters(), "parameters");
        this.checkEquals(Sets.empty(), queryString.parameters().keySet(), "parameters.keySet");

        this.parameterAbsent(queryString, "abc");
    }

    @Test
    public void testKeyOnly() {
        final UrlQueryString queryString = UrlQueryString.parse("a");

        this.parameterValuesAndCheck(queryString, "a", "");
    }

    @Test
    public void testKeyOnly2() {
        final UrlQueryString queryString = UrlQueryString.parse("abc");

        this.parameterValuesAndCheck(queryString, "abc", "");
    }

    @Test
    public void testKeyValuePair() {
        final UrlQueryString queryString = UrlQueryString.parse("a=z");

        this.parameterValuesAndCheck(queryString, "a", "z");
    }

    @Test
    public void testKeyValuePair2() {
        final UrlQueryString queryString = UrlQueryString.parse("abc=def");

        this.parameterValuesAndCheck(queryString, "abc", "def");
    }

    @Test
    public void testTwoKeyValuePair() {
        final UrlQueryString queryString = UrlQueryString.parse("a=b&c=d");

        this.parameterValuesAndCheck(queryString, "a", "b");
        this.parameterValuesAndCheck(queryString, "c", "d");
    }

    @Test
    public void testTwoKeyValuePair2() {
        final UrlQueryString queryString = UrlQueryString.parse("abc=def&ghi=jkl");

        this.parameterValuesAndCheck(queryString, "abc", "def");
        this.parameterValuesAndCheck(queryString, "ghi", "jkl");
    }

    @Test
    public void testKeyValueSemiColonKeyValue2() {
        final UrlQueryString queryString = UrlQueryString.parse("abc=def;ghi=jkl");

        this.parameterValuesAndCheck(queryString, "abc", "def");
        this.parameterValuesAndCheck(queryString, "ghi", "jkl");
    }

    @Test
    public void testKeyMultipleValues() {
        final UrlQueryString queryString = UrlQueryString.parse("a=1&a=2");

        this.parameterValuesAndCheck(queryString, "a", "1", "2");
    }

    @Test
    public void testKeyMultipleValues2() {
        final UrlQueryString queryString = UrlQueryString.parse("a=1&def=ghi&a=2&a=3");

        this.parameterValuesAndCheck(queryString, "a", "1", "2", "3");
        this.parameterValuesAndCheck(queryString, "def", "ghi");
    }

    // addParameter()

    @Test
    public void testAddParameterToEmpty() {
        final UrlQueryString empty = UrlQueryString.EMPTY;
        final UrlQueryString updated = empty.addParameter(this.name("a"), "1"); // verify encoded...

        this.parameterWithValueCheck(updated, "a", "1");

        this.toStringAndCheck(
                updated,
                "a=1"
        );
    }

    @Test
    public void testAddParameterToEmptyEscaped() {
        final UrlQueryString empty = UrlQueryString.EMPTY;
        final UrlQueryString updated = empty.addParameter(this.name("a b"), "x y"); // verify encoded...

        this.parameterWithValueCheck(updated, "a b", "x y");

        this.toStringAndCheck(
                updated,
                "a+b=x+y"
        ); // escaped form.
    }

    @Test
    public void testAddParameterTwiceToEmpty() {
        final UrlQueryString empty = UrlQueryString.EMPTY;
        final UrlQueryString updated = empty.addParameter(this.name("a"), "1")
                .addParameter(this.name("b"), "2");

        this.parameterWithValueCheck(updated, "a", "1");
        this.parameterWithValueCheck(updated, "b", "2");

        this.toStringAndCheck(
                updated,
                "a=1&b=2"
        );
    }

    @Test
    public void testAddParameterThriceToEmpty() {
        final UrlQueryString empty = UrlQueryString.EMPTY;
        final UrlQueryString updated = empty.addParameter(this.name("a"), "1")
                .addParameter(this.name("b"), "2")
                .addParameter(this.name("c"), "3");

        this.parameterWithValueCheck(updated, "a", "1");
        this.parameterWithValueCheck(updated, "b", "2");
        this.parameterWithValueCheck(updated, "c", "3");

        this.toStringAndCheck(
                updated,
                "a=1&b=2&c=3"
        );
    }

    @Test
    public void testAddParameterThriceToEmpty2() {
        final UrlQueryString empty = UrlQueryString.EMPTY;
        final UrlQueryString updated = empty.addParameter(this.name("a"), "1")
                .addParameter(this.name("b"), "2")
                .addParameter(this.name("a"), "3");

        this.parameterWithValueCheck(updated, "a", "1", "3");
        this.parameterWithValueCheck(updated, "b", "2");

        this.toStringAndCheck(
                updated,
                "a=1&b=2&a=3"
        );
    }

    @Test
    public void testAddParameterNonEmpty() {
        final UrlQueryString first = UrlQueryString.parse("a=1&b=2");
        final UrlQueryString updated = first.addParameter(this.name("c"), "3");

        this.parameterWithValueCheck(updated, "a", "1");
        this.parameterWithValueCheck(updated, "b", "2");
        this.parameterWithValueCheck(updated, "c", "3");

        this.toStringAndCheck(
                updated,
                "a=1&b=2&c=3"
        );
    }

    @Test
    public void testAddParameterNonEmpty2() {
        final UrlQueryString first = UrlQueryString.parse("a=1;b=2");
        final UrlQueryString updated = first.addParameter(this.name("c"), "3");

        this.parameterWithValueCheck(updated, "a", "1");
        this.parameterWithValueCheck(updated, "b", "2");
        this.parameterWithValueCheck(updated, "c", "3");

        this.toStringAndCheck(
                updated,
                "a=1;b=2;c=3"
        );
    }

    // addParameters....................................................................................................

    @Test
    public void testAddParametersNullFails() {
        assertThrows(
                NullPointerException.class,
                () -> UrlQueryString.EMPTY.addParameters(null)
        );
    }

    @Test
    public void testAddParametersFromEmpty() {
        final UrlQueryString queryString = UrlQueryString.parse("a=b");

        assertSame(
                queryString,
                queryString.addParameters(UrlQueryString.EMPTY)
        );
    }


    @Test
    public void testAddParametersToEmpty() {
        final UrlQueryString queryString = UrlQueryString.parse("a=b");

        assertSame(
                queryString,
                UrlQueryString.EMPTY.addParameters(queryString)
        );
    }

    @Test
    public void testAddParameters() {
        this.addParametersAndCheck(
                "a=b",
                "c=d",
                "a=b&c=d"
        );
    }

    @Test
    public void testAddParametersNotReplaced() {
        this.addParametersAndCheck(
                "a=11",
                "a=22",
                "a=11&a=22"
        );
    }

    @Test
    public void testAddParametersNotReplaced2() {
        this.addParametersAndCheck(
                "a=b",
                "a=z&c=d",
                "a=b&a=z&c=d"
        );
    }

    private void addParametersAndCheck(final String before,
                                       final String add,
                                       final String expected) {
        this.addParametersAndCheck(
                UrlQueryString.parse(before),
                UrlQueryString.parse(add),
                UrlQueryString.parse(expected)
        );
    }

    private void addParametersAndCheck(final UrlQueryString before,
                                       final UrlQueryString add,
                                       final UrlQueryString expected) {
        this.checkEquals(
                expected,
                before.addParameters(add),
                () -> before + " addParameters " + add
        );
    }

    // removeParameter(name) ..........................................................................................

    @Test
    public void testRemoveParameterNameEmpty() {
        final UrlQueryString empty = UrlQueryString.EMPTY;
        assertSame(empty, empty.removeParameter(name("absent")));
    }

    @Test
    public void testRemoveParameterNameAbsent() {
        final UrlQueryString queryString = UrlQueryString.parse("a=b");
        assertSame(queryString, queryString.removeParameter(name("absent")));
    }

    @Test
    public void testRemoveParameterNameOnly() {
        final UrlQueryString queryString = UrlQueryString.parse("a=b");
        assertSame(UrlQueryString.EMPTY, queryString.removeParameter(name("a")));
    }

    @Test
    public void testRemoveParameterNameOnlyMultipleValues() {
        final UrlQueryString queryString = UrlQueryString.parse("a=b&a=c");
        assertSame(UrlQueryString.EMPTY, queryString.removeParameter(name("a")));
    }

    @Test
    public void testRemoveParameterNameNotOnly() {
        final UrlQueryString first = UrlQueryString.parse("a=1&b=2&c=3");
        final UrlQueryString updated = first.removeParameter(this.name("a"));

        this.parameterAbsent(updated, "a");
        this.parameterWithValueCheck(updated, "b", "2");
        this.parameterWithValueCheck(updated, "c", "3");

        this.toStringAndCheck(updated, "b=2&c=3");
    }

    @Test
    public void testRemoveParameterNameNotOnly2() {
        final UrlQueryString first = UrlQueryString.parse("a=1;b=2;c=3");
        final UrlQueryString updated = first.removeParameter(this.name("a"));

        this.parameterAbsent(updated, "a");
        this.parameterWithValueCheck(updated, "b", "2");
        this.parameterWithValueCheck(updated, "c", "3");

        this.toStringAndCheck(updated, "b=2;c=3");
    }

    @Test
    public void testRemoveParameterNameNotOnly3() {
        final UrlQueryString first = UrlQueryString.parse("a=1&b=2&c=3");
        final UrlQueryString updated = first.removeParameter(this.name("b"));

        this.parameterAbsent(updated, "b");
        this.parameterWithValueCheck(updated, "a", "1");
        this.parameterWithValueCheck(updated, "c", "3");

        this.toStringAndCheck(updated, "a=1&c=3");
    }

    @Test
    public void testRemoveParameterNameNotOnly4() {
        final UrlQueryString first = UrlQueryString.parse("a=1&b=2&c=3");
        final UrlQueryString updated = first.removeParameter(this.name("c"));

        this.parameterAbsent(updated, "c");
        this.parameterWithValueCheck(updated, "a", "1");
        this.parameterWithValueCheck(updated, "b", "2");

        this.toStringAndCheck(updated, "a=1&b=2");
    }

    // removeParameter(name) ..........................................................................................

    @Test
    public void testRemoveParameterNameValueEmpty() {
        final UrlQueryString empty = UrlQueryString.EMPTY;
        assertSame(empty, empty.removeParameter(name("absent"), "1"));
    }

    @Test
    public void testRemoveParameterNameValueAbsent() {
        final UrlQueryString queryString = UrlQueryString.parse("a=b");
        assertSame(queryString, queryString.removeParameter(name("absent"), "1"));
    }

    @Test
    public void testRemoveParameterNameValueAbsentDifferentValue() {
        final UrlQueryString queryString = UrlQueryString.parse("a=b");
        assertSame(queryString, queryString.removeParameter(name("a"), "different"));
    }

    @Test
    public void testRemoveParameterNameValueOnly() {
        final UrlQueryString queryString = UrlQueryString.parse("a=b");
        assertSame(UrlQueryString.EMPTY, queryString.removeParameter(name("a"), "b"));
    }

    @Test
    public void testRemoveParameterNameValueOnlyMultipleValues() {
        final UrlQueryString queryString = UrlQueryString.parse("a=b&a=b");
        assertSame(UrlQueryString.EMPTY, queryString.removeParameter(name("a"), "b"));
    }

    @Test
    public void testRemoveParameterNameValueNotOnly() {
        final UrlQueryString first = UrlQueryString.parse("a=1&b=2&c=3");
        final UrlQueryString updated = first.removeParameter(this.name("a"), "1");
        assertNotSame(first, updated);

        this.parameterAbsent(updated, "a");
        this.parameterWithValueCheck(updated, "b", "2");
        this.parameterWithValueCheck(updated, "c", "3");

        this.toStringAndCheck(updated, "b=2&c=3");
    }

    @Test
    public void testRemoveParameterNameValueNotOnly2() {
        final UrlQueryString first = UrlQueryString.parse("a=1;b=2;c=3");
        final UrlQueryString updated = first.removeParameter(this.name("a"), "1");
        assertNotSame(first, updated);

        this.parameterAbsent(updated, "a");
        this.parameterWithValueCheck(updated, "b", "2");
        this.parameterWithValueCheck(updated, "c", "3");

        this.toStringAndCheck(updated, "b=2;c=3");
    }

    @Test
    public void testRemoveParameterNameValueNotOnly3() {
        final UrlQueryString first = UrlQueryString.parse("a=1&b=2&c=3");
        final UrlQueryString updated = first.removeParameter(this.name("b"), "2");
        assertNotSame(first, updated);

        this.parameterAbsent(updated, "b");
        this.parameterWithValueCheck(updated, "a", "1");
        this.parameterWithValueCheck(updated, "c", "3");

        this.toStringAndCheck(updated, "a=1&c=3");
    }

    @Test
    public void testRemoveParameterNameValueNotOnly4() {
        final UrlQueryString first = UrlQueryString.parse("a=1&b=2&c=3");
        final UrlQueryString updated = first.removeParameter(this.name("c"), "3");
        assertNotSame(first, updated);

        this.parameterAbsent(updated, "c");
        this.parameterWithValueCheck(updated, "a", "1");
        this.parameterWithValueCheck(updated, "b", "2");

        this.toStringAndCheck(updated, "a=1&b=2");
    }

    @Test
    public void testRemoveParameterNameValueNotOnlyIgnoresDifferentValue() {
        final UrlQueryString first = UrlQueryString.parse("a=1&b=2&c=3&a=4");
        final UrlQueryString updated = first.removeParameter(this.name("a"), "4");
        assertNotSame(first, updated);

        this.parameterWithValueCheck(updated, "a", "1");
        this.parameterWithValueCheck(updated, "b", "2");
        this.parameterWithValueCheck(updated, "c", "3");

        this.toStringAndCheck(updated, "a=1&b=2&c=3");
    }

    @Test
    public void testRemoveParameterNameValueNotOnlyIgnoresDifferentValue2() {
        final UrlQueryString first = UrlQueryString.parse("a=1&b=2&c=3&a=4");
        final UrlQueryString updated = first.removeParameter(this.name("a"), "1");
        assertNotSame(first, updated);

        this.parameterWithValueCheck(updated, "a", "4");
        this.parameterWithValueCheck(updated, "b", "2");
        this.parameterWithValueCheck(updated, "c", "3");

        this.toStringAndCheck(updated, "b=2&c=3&a=4");
    }

    @Test
    public void testRemoveParameterNameValueNotOnlyIgnoresDifferentValue3() {
        final UrlQueryString first = UrlQueryString.parse("a=1&b=2&c=3&a=4&a=1");
        final UrlQueryString updated = first.removeParameter(this.name("a"), "1");
        assertNotSame(first, updated);

        this.parameterWithValueCheck(updated, "a", "4");
        this.parameterWithValueCheck(updated, "b", "2");
        this.parameterWithValueCheck(updated, "c", "3");

        this.toStringAndCheck(updated, "b=2&c=3&a=4");
    }

    @Test
    public void testRemoveParameterNameValueNotOnlyIgnoresDifferentValue4() {
        final UrlQueryString first = UrlQueryString.parse("a=1&b=2&c=3&a=4&a=1");
        final UrlQueryString updated = first.removeParameter(this.name("a"), "4");
        assertNotSame(first, updated);

        this.parameterWithValueCheck(updated, "a", "1", "1");
        this.parameterWithValueCheck(updated, "b", "2");
        this.parameterWithValueCheck(updated, "c", "3");

        this.toStringAndCheck(updated, "a=1&b=2&c=3&a=1");
    }

    // helpers ............................................................................................................

    private void parameterWithValueCheck(final UrlQueryString queryString, final String name, final String... values) {
        this.parameterAndCheck(queryString, name, values[0]);
        this.parameterValuesAndCheck(queryString, name, values);
        this.parametersGetAndCheck(queryString, name, values);
    }

    private void parameterAbsent(final UrlQueryString queryString, final String key) {
        this.parameterAndCheck0(queryString, key, Optional.empty());
        this.parameterValuesAndCheck(queryString, key);
        this.parametersGetAndCheck0(queryString, key, null);
    }

    // UrlQueryString.parameter(String) -> Optional.

    private void parameterAndCheck(final UrlQueryString queryString, final String key, final String value) {
        parameterAndCheck0(queryString, key, Optional.of(value));
    }

    private void parameterAndCheck0(final UrlQueryString queryString, final String key, final Optional<String> value) {
        this.checkEquals(value,
                queryString.parameter(this.name(key)),
                "UrlQueryString.parameter(" + CharSequences.quote(key) + ") in: " + queryString);

        this.checkEquals(
                queryString.toString().isEmpty(),
                queryString.isEmpty(),
                () -> queryString + " isEmpty"
        );
    }

    // UrlQueryString.parameterValues(String) -> List<String> never null.

    private void parameterValuesAndCheck(final UrlQueryString queryString, final String key, final String... values) {
        parameterValuesAndCheck0(queryString, key, Lists.of(values));
    }

    private void parameterValuesAndCheck0(final UrlQueryString queryString,
                                          final String key,
                                          final List<String> values) {
        final List<String> actualValues = queryString.parameterValues(
                this.name(key)
        );

        this.checkNotEquals(
                null,
                actualValues
        );

        this.checkEquals(
                values,
                actualValues,
                "UrlQueryString.parameterValues(" + CharSequences.quote(key) + ") in: " + queryString
        );

        // https://github.com/mP1/walkingkooka/issues/2575
        assertThrows(
                UnsupportedOperationException.class,
                () -> actualValues.add(null)
        );
    }

    // UrlQueryString.parameters().get() -> List

    private void parametersGetAndCheck(final UrlQueryString queryString,
                                       final String key,
                                       final String... values) {
        parametersGetAndCheck0(
                queryString,
                key,
                Lists.of(values)
        );
    }

    private void parametersGetAndCheck0(final UrlQueryString queryString,
                                        final String key,
                                        final List<String> values) {
        final List<String> actualValues = queryString.parameters()
                .get(this.name(key));

        this.checkEquals(
                values,
                actualValues,
                "UrlQueryString.parameter().get(" + CharSequences.quote(key) + ") in: " + queryString
        );

        if (null != values) {
            // https://github.com/mP1/walkingkooka/issues/2575
            assertThrows(
                    UnsupportedOperationException.class,
                    () -> actualValues.add(null)
            );
        }
    }

    private UrlParameterName name(final String name) {
        return UrlParameterName.with(name);
    }

    // text.............................................................................................................

    @Test
    public void testText() {
        final String text = "abc=123&def=456;def=789";
        this.textAndCheck(
                UrlQueryString.parse(text),
                text
        );
    }

    // equals...........................................................................................................

    @Test
    public void testEqualsDifferentParameters() {
        this.checkNotEquals(
                UrlQueryString.parse("a=1"),
                UrlQueryString.parse("b=2")
        );
    }

    @Test
    public void testEqualsEquivalentButQueryStringDifferent() {
        this.checkEquals(
                UrlQueryString.parse("a=%20"),
                UrlQueryString.parse("a= ")
        );
    }

    // CompareTo........................................................................................................

    @Test
    public void testCompareToCaseSensitive() {
        this.compareToAndCheckLess(
                UrlQueryString.parse("XYZ123"),
                UrlQueryString.parse("abc123")
        );
    }

    @Override
    public UrlQueryString createComparable() {
        return UrlQueryString.parse("abc123");
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<UrlQueryString> type() {
        return UrlQueryString.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }

    // ParseStringTesting...............................................................................................

    @Override
    public UrlQueryString parseString(final String queryString) {
        return UrlQueryString.parse(queryString);
    }

    @Override
    public Class<? extends RuntimeException> parseStringFailedExpected(final Class<? extends RuntimeException> type) {
        return type;
    }

    @Override
    public RuntimeException parseStringFailedExpected(final RuntimeException cause) {
        return cause;
    }
}
