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
import walkingkooka.net.header.MediaTypeDetector;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * A {@link WebFile} view of a file {@link Path}.
 */
final class FileSystemWebFile implements WebFile {

    /**
     * Creates a new {@link FileSystemWebFile} assuming the file exists.
     */
    static FileSystemWebFile with(final Path path,
                                  final MediaTypeDetector contentTypeGuesser,
                                  final Function<Binary, Optional<ETag>> etagComputer) {
        Objects.requireNonNull(path, "path");
        Objects.requireNonNull(contentTypeGuesser, "contentTypeGuesser");
        Objects.requireNonNull(etagComputer, "etagComputer");

        if (false == Files.isRegularFile(path)) {
            throw new WebFileException("Path " + path + " is not a file");
        }
        return new FileSystemWebFile(path,
            contentTypeGuesser,
            etagComputer);
    }

    /**
     * Private ctor
     */
    private FileSystemWebFile(final Path path,
                              final MediaTypeDetector contentTypeGuesser,
                              final Function<Binary, Optional<ETag>> etagComputer) {
        super();
        this.path = path;
        this.contentTypeGuesser = contentTypeGuesser;
        this.etagComputer = etagComputer;
    }

    // WebFile..........................................................................................................

    /**
     * Lazily fetches the last modified filestamp.
     */
    @Override
    public LocalDateTime lastModified() throws WebFileException {
        if (null == this.lastModified) {
            try {
                final FileTime fileTime = Files.getLastModifiedTime(this.path);
                this.lastModified = LocalDateTime.ofInstant(fileTime.toInstant(), ZoneId.systemDefault());
            } catch (final IOException cause) {
                throw new WebFileException("last modified failed", cause);
            }
        }

        return this.lastModified;
    }

    private LocalDateTime lastModified;

    @Override
    public MediaType contentType() throws WebFileException {
        if (null == this.contentType) {
            this.contentType = this.contentTypeGuesser.detect(
                this.path.getFileName()
                    .toString(),
                this.binary()
            );
        }
        return this.contentType;
    }

    private MediaType contentType;

    @Override
    public long contentSize() throws WebFileException {
        return this.binary().size();
    }

    @Override
    public InputStream content() throws WebFileException {
        return this.binary().inputStream();
    }

    @Override
    public Optional<ETag> etag() throws WebFileException {
        return this.etagComputer.apply(this.binary());
    }

    private Binary binary() {
        if (null == this.binary) {
            final Path path = this.path;

            try {
                this.binary = Binary.with(Files.readAllBytes(path));
            } catch (final IOException cause) {
                throw new WebFileException("Unable to read file content of " + path, cause);
            }
        }
        return this.binary;
    }

    private Binary binary;

    /**
     * A {@link Path} to the file.
     */
    final Path path;

    /**
     * Attempts to determine the {@link MediaType} by examining the filename and content.
     */
    private final MediaTypeDetector contentTypeGuesser;

    /**
     * Attempts to determine the {@link MediaType} by examining the filename and content.
     */
    private final Function<Binary, Optional<ETag>> etagComputer;

    // Object...........................................................................................................

    @Override
    public String toString() {
        return this.path.toString();
    }
}
