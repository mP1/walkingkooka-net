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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * A web file request fetched using a {@link walkingkooka.net.UrlPath}.
 */
public interface WebFile {

    /**
     * Returns the file last modified {@link LocalDateTime}.
     */
    LocalDateTime lastModified() throws WebFileException;

    /**
     * The {@link MediaType} for the file content.
     */
    MediaType contentType() throws WebFileException;

    /**
     * Returns the content length in bytes.
     */
    long contentSize() throws WebFileException;

    /**
     * Returns a read once {@link InputStream} holding the file content.
     */
    InputStream content() throws WebFileException;

    /**
     * Helper that returns the content as text. This should be implemented by all j2cl implmentations.
     */
    default String contentText(final Charset defaultCharset) throws WebFileException {
        try (final InputStream in = this.content()) {
            final byte[] buffer = new byte[4096];
            try (final ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                for (; ; ) {
                    final int read = in.read(buffer);
                    if (-1 == read) {
                        break;
                    }
                    out.write(buffer, 0, read);
                }

                out.flush();
                return new String(out.toByteArray(), contentType().contentTypeCharset(defaultCharset));
            }
        } catch (final IOException readFailed) {
            throw new WebFileException("Failed to read content: " + readFailed, readFailed);
        }
    }

    /**
     * Returns an optionally computed {@link ETag}.
     */
    Optional<ETag> etag() throws WebFileException;
}
