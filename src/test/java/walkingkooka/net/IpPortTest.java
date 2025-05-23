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
import walkingkooka.collect.set.Sets;
import walkingkooka.collect.set.SortedSets;
import walkingkooka.compare.ComparableTesting2;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.ConstantsTesting;
import walkingkooka.reflect.JavaVisibility;

import java.util.Iterator;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


public final class IpPortTest implements ClassTesting2<IpPort>,
    ConstantsTesting<IpPort>,
    ComparableTesting2<IpPort>,
    ToStringTesting<IpPort> {

    @Test
    public void testIsPort() {
        assertTrue(IpPort.isPort(0));
        assertTrue(IpPort.isPort(80));
        assertTrue(IpPort.isPort(65535));
        assertFalse(IpPort.isPort(-1));
        assertFalse(IpPort.isPort(65536));
    }

    @Test
    public void testPortAboveRangeFails() {
        assertThrows(IllegalArgumentException.class, () -> IpPort.with(65536));
    }

    @Test
    public void testPortBelowRangeFails() {
        assertThrows(IllegalArgumentException.class, () -> IpPort.with(-1));
    }

    @Test
    public void testWith() {
        final int port = 65000;
        final IpPort ipPort = IpPort.with(port);
        this.checkEquals(port, ipPort.value(), "value");
    }

    @Test
    public void testNonConstantsNotSingletons() {
        final int port = 65000;
        assertNotSame(IpPort.with(port), IpPort.with(port));
    }

    @Test
    public void testFree() {
        assertNotNull(IpPort.free());
    }

    @Test
    public void testArraySort() {
        final IpPort port80 = IpPort.with(80);
        final IpPort port443 = IpPort.with(443);
        final IpPort port22 = IpPort.with(22);

        this.compareToArraySortAndCheck(port443, port22, port80,
            port22, port80, port443);
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(IpPort.HTTP, "80");
    }

    @Override
    public Class<IpPort> type() {
        return IpPort.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }

    @Override
    public Set<IpPort> intentionalDuplicateConstants() {
        return Sets.empty();
    }

    private final static int PORT = 80;

    @Test
    public void testEqualsDifferentPort() {
        this.checkNotEquals(IpPort.HTTPS);
    }

    @Test
    public void testEquals() {
        this.checkEqualsAndHashCode(IpPort.with(PORT));
    }

    @Test
    public void testCompareToLess() {
        this.compareToAndCheckLess(IpPort.HTTPS);
    }

    @Test
    public void testInSortedSet() {
        final IpPort one = IpPort.with(1);
        final IpPort two = IpPort.with(2);
        final IpPort three = IpPort.with(3);

        final Set<IpPort> set = SortedSets.tree();
        set.add(one);
        set.add(two);
        set.add(three);

        final Iterator<IpPort> values = set.iterator();
        this.checkEquals(one, values.next());
        this.checkEquals(two, values.next());
        this.checkEquals(three, values.next());
    }

    @Override
    public IpPort createComparable() {
        return IpPort.HTTP;
    }
}
