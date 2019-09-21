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

import walkingkooka.net.header.ETag;
import walkingkooka.net.header.MediaType;
import walkingkooka.test.Fake;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Optional;

public class FakeWebFile implements WebFile, Fake {

    protected FakeWebFile() {
        super();
    }

    @Override
    public LocalDateTime lastModified() throws HttpServerException {
        throw new UnsupportedOperationException();
    }

    @Override
    public MediaType contentType() throws HttpServerException {
        throw new UnsupportedOperationException();
    }

    @Override
    public long contentSize() throws HttpServerException {
        throw new UnsupportedOperationException();
    }

    @Override
    public InputStream content() throws HttpServerException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<ETag> etag() throws HttpServerException {
        throw new UnsupportedOperationException();
    }
}
