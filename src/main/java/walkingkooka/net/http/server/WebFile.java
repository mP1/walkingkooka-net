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

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * A web file request fetched using a {@link walkingkooka.net.UrlPath}.
 */
public interface WebFile {

    /**
     * Returns the file last modified {@link LocalDateTime}.
     */
    LocalDateTime lastModified() throws HttpServerException;

    /**
     * The {@link MediaType} for the file content.
     */
    MediaType contentType() throws HttpServerException;

    /**
     * Returns the content length in bytes.
     */
    long contentSize() throws HttpServerException;

    /**
     * Returns a read once {@link InputStream} holding the file content.
     */
    InputStream content() throws HttpServerException;

    /**
     * Returns an optionally computed {@link ETag}.
     */
    Optional<ETag> etag() throws HttpServerException;
}
