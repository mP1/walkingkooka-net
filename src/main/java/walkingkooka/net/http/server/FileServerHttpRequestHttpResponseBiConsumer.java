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
import walkingkooka.collect.map.Maps;
import walkingkooka.io.InputStreams;
import walkingkooka.net.UrlPath;
import walkingkooka.net.header.HttpHeaderName;
import walkingkooka.net.header.MediaType;
import walkingkooka.net.http.HttpEntity;
import walkingkooka.net.http.HttpStatusCode;
import walkingkooka.text.CharSequences;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Serves all files under the given directory, adding content-type, content-length and last-modified headers and body.
 */
final class FileServerHttpRequestHttpResponseBiConsumer implements BiConsumer<HttpRequest, HttpResponse> {

    static FileServerHttpRequestHttpResponseBiConsumer with(final UrlPath pathBase,
                                                            final Path fileBase,
                                                            final Function<FileResponse, MediaType> contentTypeIdentifier) {
        Objects.requireNonNull(pathBase, "pathBase");
        Objects.requireNonNull(fileBase, "fileBase");
        Objects.requireNonNull(contentTypeIdentifier, "contentTypeIdentifier");

        final File directory = fileBase.toFile();
        if (!directory.exists() || !directory.isDirectory()) {
            throw new IllegalArgumentException("File base " + CharSequences.quoteAndEscape(directory.getAbsolutePath()) + " directory does not exist.");
        }

        return new FileServerHttpRequestHttpResponseBiConsumer(pathBase, fileBase, contentTypeIdentifier);
    }

    private FileServerHttpRequestHttpResponseBiConsumer(final UrlPath pathBase,
                                                        final Path fileBase,
                                                        final Function<FileResponse, MediaType> contentTypeIdentifier) {
        super();
        this.pathBase = pathBase;
        this.fileBase = fileBase;
        this.contentTypeIdentifier = contentTypeIdentifier;
    }

    @Override
    public void accept(final HttpRequest request, final HttpResponse response) {
        Objects.requireNonNull(request, "request");
        Objects.requireNonNull(response, "response");

        // extract path and verify is valid.
        final UrlPath fullPath = request.url()
                .path()
                .normalize();
        final UrlPath pathBase = this.pathBase;

        final String fullPathString = fullPath.value();
        final String pathBaseString = pathBase.value();

        if (!fullPathString.startsWith(pathBaseString)) {
            throw new HttpServerException("Request url " + CharSequences.quoteAndEscape(fullPathString) + " mapping mistake does not begin with " + CharSequences.quoteAndEscape(pathBaseString));
        }

        final String filePathInfo = fullPathString.substring(pathBaseString.length());
        final Path filePath = this.fileBase.resolve(filePathInfo);
        final File fileFile = filePath.toFile();
        if (fileFile.isFile()) {
            final LocalDateTime fileLastModified = this.fileLastModified(filePath);

            try (final InputStream fileInput = new FileInputStream(fileFile)) {
                final Binary content = Binary.with(InputStreams.readAllBytes(fileInput));

                final FileResponse fileResponse = FileResponse.with(fileFile.getName(), content);

                response.setStatus(HttpStatusCode.OK.status());
                response.addEntity(HttpEntity.with(
                        Maps.of(HttpHeaderName.CONTENT_LENGTH, Long.valueOf(content.size()),
                                HttpHeaderName.CONTENT_TYPE, this.contentTypeIdentifier.apply(fileResponse),
                                HttpHeaderName.LAST_MODIFIED, fileLastModified),
                        content));
            } catch (final IOException unable) {
                throw new HttpServerException("Failed to read file " + CharSequences.quoteAndEscape(filePath.toString()), unable);
            }
        } else {
            response.setStatus(HttpStatusCode.NOT_FOUND.status());
        }
    }

    private LocalDateTime fileLastModified(final Path file) {
        try {
            return LocalDateTime.ofInstant(Files.getLastModifiedTime(file).toInstant(), ZoneId.systemDefault().getRules().getOffset(LocalDateTime.now()));
        } catch (final IOException unable) {
            throw new HttpServerException("Failed to read file " + CharSequences.quoteAndEscape(file.toString()), unable);
        }
    }

    /**
     * The path base, which is used to extract the path component which will be used to locate the file.
     */
    private final UrlPath pathBase;

    /**
     * The file base where all files live.
     */
    private final Path fileBase;

    /**
     * Used to guess or determine the {@link MediaType} of a file being served.
     */
    private final Function<FileResponse, MediaType> contentTypeIdentifier;

    @Override
    public String toString() {
        return this.fileBase.toString();
    }
}
