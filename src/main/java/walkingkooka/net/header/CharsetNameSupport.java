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
import java.util.Set;

/**
 * This class has several helpers which include APIS that are not available in the J2cl emulated JRE
 */
final class CharsetNameSupport {

    /**
     * J2cl will require a version of this class that does nothing because {@link Charset#aliases()}.
     */
    static void registerCharsetAliases(final Charset charset,
                                       final Map<String, CharsetName> constants) {
        for (final String alias : charset.aliases()) {
            constants.put(alias, CharsetNameSupportedCharset.with(alias, charset));
        }
    }

    /**
     * The J2cl version of this method will contain no logic and simply return false.
     */
    static boolean testCharsetAliases(final Charset charset,
                                      final CharsetNameSupportedCharset contentType) {
        final Set<String> contentTypeAliases = contentType.charset.aliases();

        return charset.aliases()
                .stream()
                .anyMatch(contentTypeAliases::contains);
    }
}
