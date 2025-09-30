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
import walkingkooka.text.printer.TreePrintableTesting;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public interface HasHostAddressTesting<T extends HasHostAddress> extends TreePrintableTesting {

    default void hostAddressAndCheck(final HasHostAddress has,
                                     final String expected) {
        this.checkEquals(
            expected,
            has.hostAddress(),
            has::toString
        );
    }

    // setHostAddress...................................................................................................

    @Test
    default void testSetHostAddressWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createHasHostAddress()
                .setHostAddress(null)
        );
    }

    @Test
    default void testSetHostAddressWithSame() {
        final T has = this.createHasHostAddress();
        assertSame(
            has,
            has.setHostAddress(has.hostAddress())
        );
    }

    default void setHostAddressAndCheck(final T has,
                                        final HostAddress hostAddress,
                                        final T expected) {
        this.checkEquals(
            expected,
            has.setHostAddress(hostAddress)
        );
    }

    T createHasHostAddress();
}
