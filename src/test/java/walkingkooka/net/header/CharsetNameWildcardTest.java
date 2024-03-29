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

public final class CharsetNameWildcardTest extends CharsetNameTestCase<CharsetNameWildcard> {

    @Test
    public void testTestCharsetSupportedUtf8True() {
        this.testTrue(CharsetName.UTF_8);
    }

    @Test
    public void testTestCharsetSupportedUtf16True() {
        this.testTrue(CharsetName.UTF_16);
    }

    @Test
    public void testTestCharsetUnsupportedFalse() {
        this.testFalse(CharsetNameUnsupportedCharset.unsupportedCharset("x-unsupported"));
    }

    @Override
    public void testEqualsIgnoringParametersDifferent() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testEqualsOnlyPresentParametersDifferentFalse() {
        throw new UnsupportedOperationException();
    }

    @Override
    CharsetNameWildcard createCharsetName() {
        return CharsetNameWildcard.INSTANCE;
    }

    @Override
    String charsetNameToString() {
        return "*";
    }

    @Override
    public CharsetNameWildcard createDifferentHeader() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Class<CharsetNameWildcard> type() {
        return CharsetNameWildcard.class;
    }
}
