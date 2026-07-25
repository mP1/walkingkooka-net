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
import walkingkooka.Binary;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

public final class ETagComputerTestingTest implements ETagComputerTesting {

    @Test
    public void testComputeETagEmpty() {
        this.computeETagAndCheck(
            new ETagComputer() {
                @Override
                public Optional<ETag> computeETag(final Binary binary) {
                    return Optional.empty();
                }
            },
            Binary.EMPTY
        );
    }

    @Test
    public void testComputeETagSuccess() {
        final ETag etag = ETag.with(
            "123",
            ETagValidator.STRONG
        );

        this.computeETagAndCheck(
            new ETagComputer() {
                @Override
                public Optional<ETag> computeETag(final Binary binary) {
                    return Optional.of(etag);
                }
            },
            Binary.EMPTY,
            etag
        );
    }

    @Test
    public void testETagComputerConstants() {
        this.computeETagAndCheck(
            ETAG_COMPUTER,
            Binary.with(
                "Hello".getBytes(StandardCharsets.UTF_8)
            ),
            ETag.with(
                "5",
                ETagValidator.WEAK
            )
        );
    }
}
