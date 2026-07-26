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
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;

import java.nio.charset.StandardCharsets;

public final class ETagComputerMd5Test implements ETagComputerTesting, ClassTesting2<ETagComputerMd5> {

    @Test
    public void testComputeETagWithEmptyBinary() {
        this.computeETagAndCheck(
            ETagComputerMd5.INSTANCE,
            Binary.EMPTY,
            ETag.strong("d41d8cd98f00b204e9800998ecf8427e")
        );
    }

    @Test
    public void testComputeETagWithNonEmptyBinary() {
        this.computeETagAndCheck(
            ETagComputerMd5.INSTANCE,
            Binary.with(
                "abcdefghijklmnopqrstuvwxyz".getBytes(StandardCharsets.UTF_8)
            ),
            ETag.strong("c3fcd3d76192e4007dfb496cca67e13b")
        );
    }

    // class............................................................................................................

    @Override
    public Class<ETagComputerMd5> type() {
        return ETagComputerMd5.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}
