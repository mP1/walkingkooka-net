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

import walkingkooka.ToStringBuilder;
import walkingkooka.UsesToStringBuilder;
import walkingkooka.Value;
import walkingkooka.compare.Comparators;

import java.util.Arrays;

/**
 * Base class for either {@link Ip4Address} or {@link Ip6Address}.
 */
abstract public class IpAddress implements Value<byte[]>,
    UsesToStringBuilder {

    /**
     * {@see IpAddress4}
     */
    public static Ip4Address ip4(final byte[] components) {
        return Ip4Address.with(components);
    }

    /**
     * {@see IpAddress6}
     */
    public static Ip6Address ip6(final byte[] components) {
        return Ip6Address.with(components);
    }

    /**
     * Package private to limit sub classing.
     */
    IpAddress(final byte[] components) {
        super();
        this.components = clone(components);
    }

    // IpAddress

    /**
     * Returns true only {@link Ip4Address}.
     */
    abstract public boolean isIp4();

    /**
     * Returns true only for {@link Ip6Address}.
     */
    abstract public boolean isIp6();

    // Value

    @Override
    public final byte[] value() {
        return clone(this.components); // always copy never share
    }

    final byte[] components;

    // Object

    @Override
    public final int hashCode() {
        return Arrays.hashCode(this.components);
    }

    @Override
    public final boolean equals(final Object other) {
        return this == other ||
            this.canBeEqual(other) && this.equals0((IpAddress) other);
    }

    abstract boolean canBeEqual(Object other);

    private boolean equals0(final IpAddress other) {
        return Arrays.equals(this.components, other.components);
    }

    // Object

    @Override
    public final String toString() {
        return ToStringBuilder.buildFrom(this);
    }

    // Comparable

    final int compare0(final IpAddress address) {
        int result = Comparators.EQUAL;

        final byte[] components = this.components;
        final byte[] otherComponents = address.components;
        for (int i = 0; i < components.length; i++) {
            result = IpAddress.toUnsignedInt(components[i]) - IpAddress.toUnsignedInt(otherComponents[i]);
            if (Comparators.EQUAL != result) {
                break;
            }
        }
        return result;
    }

    private static int toUnsignedInt(final byte value) {
        return 0xFF & value;
    }

    static byte[] clone(final byte[] array) {
        return Arrays.copyOf(array, array.length); // array clone not supported in j2cl
    }
}
