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
import walkingkooka.type.JavaVisibility;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class CharsetNameTestCase<N extends CharsetName> extends HeaderValueTestCase<N> {

    CharsetNameTestCase() {
        super();
    }

    @Test
    public final void testIsWildcard() {
        this.isWildcardAndCheck(CharsetNameWildcard.class.equals(this.type()));
    }

    // matches.......................................................

    @Test
    public final void testMatchesWildcardFails() {
        assertThrows(HeaderValueException.class, () -> {
            this.createCharsetName().matches(CharsetName.WILDCARD_CHARSET);
        });
    }

    final void matches(final CharsetName contentType,
                       final boolean matches) {
        this.matches(this.createCharsetName(),
                contentType,
                matches);
    }

    final void matches(final CharsetName charsetName,
                       final CharsetName contentType,
                       final boolean matches) {
        assertEquals(matches,
                charsetName.matches(contentType),
                charsetName + " matches " + contentType);
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
    public final N createHeaderValue() {
        return this.createCharsetName();
    }

    abstract N createCharsetName();

    abstract String headerText();

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

    @Override
    public final JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}
