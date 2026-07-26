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

import javaemul.internal.annotations.GwtIncompatible;
import walkingkooka.Binary;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import java.util.Optional;

/**
 * An {@link ETagComputer} that computes the MD5 for any given {@link Binary}.
 */
@GwtIncompatible
final class ETagComputerMd5 implements ETagComputer {

    /**
     * Singleton
     */
    final static ETagComputerMd5 INSTANCE = new ETagComputerMd5();

    private ETagComputerMd5() {
        super();
    }

    @Override
    public Optional<ETag> computeETag(final Binary binary) {
        Objects.requireNonNull(binary, "binary");

        ETag etag;

        try {
            final MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(
                binary.value()
            );

            final StringBuilder b = new StringBuilder();
            for (byte digest : md.digest()) {
                b.append(
                    BYTE_TO_CHAR[(digest >> 4) & 0xf]
                );

                b.append(
                    BYTE_TO_CHAR[digest & 0xf]
                );
            }

            etag = ETag.strong(
                b.toString()
            );
        } catch (final NoSuchAlgorithmException ignore) {
            etag = null;
        }
        return Optional.ofNullable(etag);
    }

    private final static char[] BYTE_TO_CHAR = "0123456789abcdef".toCharArray();

    // Object...........................................................................................................

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
