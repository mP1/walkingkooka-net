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

public final class Ip6AddressTest extends IpAddressTestCase<Ip6Address> {

    @Test
    public void testDifferent() {
        this.checkNotEquals(Ip6Address.with(new byte[]{9, 10, 11, 12, 13, 14, 15, 16, 0, 0, 0, 0, 0, 0, 0, 0}));
    }

    @Test
    public void testEqualsIp4() {
        this.checkNotEquals(Ip4Address.with(new byte[]{1, 2, 3, 4}));
    }

    @Test
    public void testLess() {
        this.compareToAndCheckLess(
                Ip6Address.with(new byte[]{(byte) 255, 2, 3, 4, 5, 6, 7, 8, 0, 0, 0, 0, 0, 0, 0, 0}));
    }

    @Override
    public Ip6Address createComparable() {
        return Ip6Address.with(new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 0, 0, 0, 0, 0, 0, 0, 0});
    }

    @Test
    public void testToStringWith0SignificantOctets() {
        this.toStringAndCheck(this.createAddress(new byte[Ip6Address.OCTET_COUNT]),
                "0::");
    }

    @Test
    public void testToStringWith4Octets() {
        this.toStringAndCheck(this.createAddress(new byte[]{1, 2, 3, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}),
                "1:2:3:4::");
    }

    @Test
    public void testToStringWith8SignificantOctets() {
        this.toStringAndCheck(this.createAddress(new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 0, 0, 0, 0, 0, 0, 0, 0}),
                "1:2:3:4:5:6:7:8::");
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createAddress(new byte[]{(byte) 0xFF, 2, 3, 4, 5, 6, 7, 8, 0, 0, 0, 0, 0, 0, 0, 0}),
                "FF:2:3:4:5:6:7:8::");
    }

    @Test
    public void testToStringFilled() {
        this.toStringAndCheck(this.createAddress(new byte[]{(byte) 0xFF, 2, 3, 4, 5, 6, 7, 8, 9, (byte) 0xFF, 1, 2, 3, 4, 5, 6}),
                "FF:2:3:4:5:6:7:8:9:FF:1:2:3:4:5:6");
    }

    @Override
    Ip6Address createAddress(final byte[] components) {
        return Ip6Address.with(components);
    }

    @Override
    int bitCount() {
        return Ip6Address.BIT_COUNT;
    }

    @Override
    public Class<Ip6Address> type() {
        return Ip6Address.class;
    }

    @Override
    public Ip6Address serializableInstance() {
        return Ip6Address.with(new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 0, 0, 0, 0, 0, 0, 0, 0});
    }
}
