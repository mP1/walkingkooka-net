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

import walkingkooka.reflect.StaticHelper;

import java.io.File;

final class ContentDispositionFileNameSupport implements StaticHelper {

    /**
     * J2cl will replace this method with one that returns false.
     */
    static boolean isFileSeperator(final char c) {
        return File.separatorChar == c;
    }

    private ContentDispositionFileNameSupport() {
        throw new UnsupportedOperationException();
    }
}
