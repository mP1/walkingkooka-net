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
import walkingkooka.logging.FakeLoggingContext;
import walkingkooka.net.header.ETag;
import walkingkooka.text.Indentation;
import walkingkooka.text.LineEnding;

import java.nio.charset.Charset;
import java.util.Optional;

public class FakeHttpHandlerContext extends FakeLoggingContext implements HttpHandlerContext {

    public FakeHttpHandlerContext() {
        super();
    }

    // BinaryTextContext................................................................................................

    @Override
    public Indentation indentation() {
        throw new UnsupportedOperationException();
    }

    @Override
    public LineEnding lineEnding() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Charset charset() {
        throw new UnsupportedOperationException();
    }

    // ETagComputer.....................................................................................................

    @Override
    public Optional<ETag> computeETag(final Binary binary) {
        throw new UnsupportedOperationException();
    }
}
