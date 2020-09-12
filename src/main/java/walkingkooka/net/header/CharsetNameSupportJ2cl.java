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

import java.nio.charset.Charset;
import java.util.Map;

/**
 * This class has several helpers which shadow methods in {@link CharsetNameSupport} which will be eliminated as they
 * are marked with @GwtIncompatible.
 */
class CharsetNameSupportJ2cl {
    
    static void registerCharsetAliases(final Charset charset,
                                       final Map<String, CharsetName> constants) {
        // j2cl Charset has no aliases.
    }

    static boolean testCharsetAliases(final Charset charset,
                                      final Charset aliasSource) {
        return false;
    }
}
