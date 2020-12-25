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

public final class CharsetNameUnsupportedCharsetTest extends CharsetNameTestCase<CharsetNameUnsupportedCharset> {

    private final static String NAME = "unsupported";

    @Test
    public final void testTestCharsetSupportedFalse() {
        this.testFalse(CharsetName.UTF_8);
    }

    @Test
    public final void testTestCharsetUnsupportedFalse() {
        this.testFalse(CharsetNameUnsupportedCharset.unsupportedCharset("unsupported-2"));
    }

    @Test
    public final void testTestCharsetUnsupported2() {
        this.testFalse(this.createCharsetName());
    }

    @Override
    CharsetNameUnsupportedCharset createCharsetName() {
        return CharsetNameUnsupportedCharset.unsupportedCharset(NAME);
    }

    @Override
    String charsetNameToString() {
        return NAME;
    }

    @Override
    public CharsetNameUnsupportedCharset createDifferentHeader() {
        return CharsetNameUnsupportedCharset.unsupportedCharset("unsupported-different");
    }

    @Override
    public Class<CharsetNameUnsupportedCharset> type() {
        return CharsetNameUnsupportedCharset.class;
    }
}
