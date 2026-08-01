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

public final class ETagComputerMessageDigestSha1Test extends ETagComputerMessageDigestTestCase<ETagComputerMessageDigestSha1> {

    @Test
    public void testComputeETagWithEmptyBinary() {
        this.computeETagAndCheck(
            ETagComputerMessageDigestSha1.INSTANCE,
            Binary.EMPTY,
            ETag.strong("da39a3ee5e6b4b0d3255bfef95601890afd80709")
        );
    }

    @Test
    public void testComputeETagWithNonEmptyBinary() {
        this.computeETagAndCheck(
            ETagComputerMessageDigestSha1.INSTANCE,
            Binary.with(
                "abcdefghijklmnopqrstuvwxyz".getBytes(StandardCharsets.UTF_8)
            ),
            ETag.strong("32d10c7b8cf96570ca04ce37f2a19d84240d3a89")
        );
    }

    // class............................................................................................................

    @Override
    public Class<ETagComputerMessageDigestSha1> type() {
        return ETagComputerMessageDigestSha1.class;
    }
}
