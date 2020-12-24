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
import walkingkooka.predicate.PredicateTesting2;
import walkingkooka.reflect.JavaVisibility;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class CharsetNameTestCase<N extends CharsetName> extends HeaderTestCase<N>
        implements PredicateTesting2<N, CharsetName> {

    CharsetNameTestCase() {
        super();
    }

    @Override
    public final void testTypeNaming() {
    }

    @Test
    public final void testIsSupported() {
        final N charsetName = this.createCharsetName();
        assertEquals(this.type() == CharsetNameSupportedCharset.class,
                charsetName.isSupported(),
                () -> charsetName + " isSupported");
    }

    @Test
    public final void testIsUnsupported() {
        final N charsetName = this.createCharsetName();
        assertEquals(this.type() == CharsetNameUnsupportedCharset.class,
                charsetName.isUnsupported(),
                () -> charsetName + " isUnsupported");
    }

    @Test
    public final void testIsWildcard() {
        this.isWildcardAndCheck(CharsetNameWildcard.class.equals(this.type()));
    }

    // Predicate........................................................................................................

    @Test
    public final void testTestWildcardFails() {
        assertThrows(HeaderException.class, () -> this.createCharsetName().test(CharsetName.WILDCARD_CHARSET));
    }

    @Test
    public final void testHeaderText() {
        this.toHeaderTextAndCheck(this.charsetNameToString());
    }

    @Test
    public final void testToString() {
        this.toStringAndCheck(this.createCharsetName(), this.charsetNameToString());
    }

    @Override
    public final N createHeader() {
        return this.createCharsetName();
    }

    abstract N createCharsetName();

    abstract String charsetNameToString();

    @Override
    public final boolean isMultipart() {
        return false;
    }

    @Override
    public final boolean isRequest() {
        return true;
    }

    @Override
    public final boolean isResponse() {
        return true;
    }

    // ClassTesting.....................................................................................................

    @Override
    public final JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }

    // PredicateTesting.................................................................................................

    @Override
    public final N createPredicate() {
        return this.createCharsetName();
    }
}
