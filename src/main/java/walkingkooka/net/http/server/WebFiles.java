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

package walkingkooka.net.http.server;

import walkingkooka.Binary;
import walkingkooka.net.header.ETag;
import walkingkooka.net.header.MediaType;
import walkingkooka.reflect.PublicStaticHelper;

import java.nio.file.Path;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

public final class WebFiles implements PublicStaticHelper {

    /**
     * {@see FakeWebFile}
     */
    public static WebFile fake() {
        return new FakeWebFile();
    }

    /**
     * {@see FileSystemWebFile}
     */
    public static WebFile file(final Path path,
                               final BiFunction<String, Binary, MediaType> contentTypeGuesser,
                               final Function<Binary, Optional<ETag>> etagComputer) {
        return FileSystemWebFile.with(path,
                contentTypeGuesser,
                etagComputer);
    }

    /**
     * Stop creation
     */
    private WebFiles() {
        throw new UnsupportedOperationException();
    }
}
