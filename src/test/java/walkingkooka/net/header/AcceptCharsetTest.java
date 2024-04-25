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

package walkingkooka.net.header;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.map.Maps;
import walkingkooka.predicate.PredicateTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.test.ParseStringTesting;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertSame;

public final class AcceptCharsetTest extends Header2TestCase<AcceptCharset, List<AcceptCharsetValue>>
        implements HasQualityFactorSortedValuesTesting,
        ParseStringTesting<AcceptCharset>,
        PredicateTesting {

    // charset...................................................................................................

    @Test
    public void testCharset() {
        this.charsetAndCheck(this.createHeader(), CharsetName.UTF_8.charset());
    }

    @Test
    public void testCharset2() {
        this.charsetAndCheck(
                this.createHeader(AcceptCharsetValue.with(CharsetName.UTF_8)),
                CharsetName.UTF_8.charset());
    }

    @Test
    public void testCharsetWithout() {
        this.charsetAndCheck(
                this.createHeader(AcceptCharsetValue.with(CharsetName.with("X-custom"))));
    }

    private void charsetAndCheck(final AcceptCharset acceptCharset) {
        this.charsetAndCheck(acceptCharset, Optional.empty());
    }

    private void charsetAndCheck(final AcceptCharset acceptCharset, final Optional<Charset> expected) {
        this.checkEquals(expected,
                acceptCharset.charset(),
                acceptCharset + " .charset()");
    }

    // HasQualityFactorSortedValuesTesting..............................................................................

    @Test
    public void testQualityFactorSort() {
        final AcceptCharset accept = AcceptCharset.parse("UTF8;q=1.0,UTF16;q=0.75");
        this.qualitySortedValuesAndCheck(accept, accept.value());
    }

    @Test
    public void testQualityFactorSort2() {
        this.qualitySortedValuesAndCheck(AcceptCharset.parse("UTF8;q=0.5,UTF16"),
                AcceptCharset.parse("UTF16,UTF8;q=0.5").value());
    }

    // parse.......................................................................................................

    @Test
    public void testParse() {
        this.parseStringAndCheck("UTF-8;bcd=123 ",
                AcceptCharset.with(Lists.of(AcceptCharsetValue.with(CharsetName.UTF_8).setParameters(Maps.of(AcceptCharsetValueParameterName.with("bcd"), "123")))));
    }

    @Test
    public void testParseUtf8Constant() {
        assertSame(
                AcceptCharset.UTF_8,
                AcceptCharset.parse("utf-8")
        );
    }

    // Predicate........................................................................................................

    @Test
    public void testTestCharsetMissingFromContentType() {
        this.testFalse(AcceptCharset.parse("UTF-8"), MediaType.TEXT_HTML);
    }

    @Test
    public void testTestCharsetPresentTrue() {
        this.testTrue(AcceptCharset.parse("UTF-8"), MediaType.parse("text/plain;charset=UTF-8"));
    }

    @Test
    public void testTestCharsetDifferentFalse() {
        this.testFalse(AcceptCharset.parse("UTF-8"), MediaType.parse("text/plain;charset=UTF-16"));
    }

    @Test
    public void testTestCharsetPresentTrueIgnoresOtherMediaTypeParameters() {
        this.testTrue(AcceptCharset.parse("UTF-8"), MediaType.parse("text/plain;charset=UTF-8;a1=b2"));
    }

    @Test
    public void testTestCharsetLastAcceptCharsetValueMatchTrue() {
        this.testTrue(AcceptCharset.parse("UTF32,UTF16,UTF-8"), MediaType.parse("text/plain;charset=UTF-8"));
    }

    @Test
    public void testTestCharsetAbsentFalse() {
        this.testFalse(AcceptCharset.parse("US-ASCII,UTF8"), MediaType.parse("text/plain;charset=UTF-16"));
    }

    @Test
    public void testTestCharsetIncludesParametersFalse() {
        this.testFalse(AcceptCharset.parse("US-ASCII;q=0.7"), MediaType.parse("text/plain;charset=UTF-8"));
    }

    @Test
    public void testTestCharsetIncludesParametersTrue() {
        this.testTrue(AcceptCharset.parse("UTF8;q=0.7"), MediaType.parse("text/plain;charset=UTF-8"));
    }

    // helpers..........................................................................................................

    @Override
    AcceptCharset createHeader(final List<AcceptCharsetValue> value) {
        return AcceptCharset.with(value);
    }

    private AcceptCharset createHeader(final AcceptCharsetValue... value) {
        return this.createHeader(Lists.of(value));
    }

    @Override
    List<AcceptCharsetValue> value() {
        return Lists.of(AcceptCharsetValue.with(CharsetName.with("X-custom")),
                AcceptCharsetValue.with(CharsetName.UTF_8),
                AcceptCharsetValue.with(CharsetName.UTF_16));
    }

    @Override
    List<AcceptCharsetValue> differentValue() {
        return Lists.of(AcceptCharsetValue.with(CharsetName.UTF_16));
    }

    @Override
    public boolean isMultipart() {
        return false;
    }

    @Override
    public boolean isRequest() {
        return true;
    }

    @Override
    public boolean isResponse() {
        return false;
    }

    @Override
    public Class<AcceptCharset> type() {
        return AcceptCharset.class;
    }

    // ParseStringTesting ........................................................................................

    @Override
    public AcceptCharset parseString(final String text) {
        return AcceptCharset.parse(text);
    }

    // ClassTestCase ............................................................................................

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
