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
import walkingkooka.Cast;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public final class CharsetNameSupportedCharsetTest extends CharsetNameTestCase<CharsetNameSupportedCharset> {

    @Test
    public void testTestCharsetAliasSupportedTrue() {
        this.testTrue(CharsetName.with("utf-8"), CharsetName.with("UTF-8"));
    }

    @Test
    public void testTestCharsetAliasSupportedTrue2() {
        this.testTrue(CharsetName.with("Utf-8"), CharsetName.with("utF-8"));
    }

    @Test
    public void testTestCharsetAliasSupportedTrue3() {
        this.testTrue(CharsetName.UTF_8);
    }

    @Test
    public void testTestDifferentFalse() {
        this.testFalse(CharsetName.UTF_16);
    }

    @Test
    public void testTestCharsetSupported2() {
        final Charset utf8 = StandardCharsets.UTF_8;
        final Charset unsupported = Charset.availableCharsets()
                .values()
                .stream()
                .filter(c -> !utf8.contains(c))
                .findFirst()
                .get();

        this.testFalse(CharsetName.with(utf8.name()),
                CharsetName.with(unsupported.name()));
    }

    @Test
    public void testTestCharsetUnsupportedFalse() {
        this.testFalse(CharsetNameUnsupportedCharset.unsupportedCharset("x-unsupported"));
    }

    @Override
    CharsetNameSupportedCharset createCharsetName() {
        return Cast.to(CharsetName.UTF_8);
    }

    @Override
    public CharsetNameSupportedCharset createDifferentHeader() {
        return CharsetNameSupportedCharset.with("different", StandardCharsets.UTF_16);
    }

    @Override
    String charsetNameToString() {
        return "UTF-8";
    }

    @Override
    public Class<CharsetNameSupportedCharset> type() {
        return CharsetNameSupportedCharset.class;
    }
}
