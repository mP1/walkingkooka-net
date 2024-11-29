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

final class ContentDispositionFileNameSupport extends ContentDispositionFileNameSupportJ2cl {

    /**
     * J2cl will replaceParameters this method with one that returns false.
     */
    @GwtIncompatible
    static boolean isFileSeperator(final char c) {
        return java.io.File.separatorChar == c;
    }
}
