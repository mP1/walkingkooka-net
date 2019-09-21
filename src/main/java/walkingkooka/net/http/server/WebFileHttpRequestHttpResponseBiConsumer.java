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
import walkingkooka.Either;
import walkingkooka.collect.map.Maps;
import walkingkooka.io.InputStreams;
import walkingkooka.net.UrlPath;
import walkingkooka.net.header.HttpHeaderName;
import walkingkooka.net.header.MediaType;
import walkingkooka.net.http.HttpEntity;
import walkingkooka.net.http.HttpStatus;
import walkingkooka.net.http.HttpStatusCode;
import walkingkooka.text.CharSequences;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Serves all files under the given directory. If a file exists and the if-last-modified header in the request matches a 304
 * NOT_MODIFIED will be returned with an empty body. The content-type, last-modified headers will always be added to the response,
 * and a content-length when the body contains the file content.
 */
final class WebFileHttpRequestHttpResponseBiConsumer implements BiConsumer<HttpRequest, HttpResponse> {

    static WebFileHttpRequestHttpResponseBiConsumer with(final UrlPath basePath,
                                                         final Function<UrlPath, Either<WebFile, HttpStatus>> files) {
        Objects.requireNonNull(basePath, "basePath");
        Objects.requireNonNull(files, "files");

        return new WebFileHttpRequestHttpResponseBiConsumer(basePath, files);
    }

    /**
     * Private ctor use factory
     */
    private WebFileHttpRequestHttpResponseBiConsumer(final UrlPath basePath,
                                                     final Function<UrlPath, Either<WebFile, HttpStatus>> files) {
        super();
        this.basePath = basePath;
        this.files = files;
    }

    @Override
    public void accept(final HttpRequest request, final HttpResponse response) {
        Objects.requireNonNull(request, "request");
        Objects.requireNonNull(response, "response");

        // extract path and verify is valid.
        final UrlPath fullPath = request.url()
                .path()
                .normalize();
        final UrlPath basePath = this.basePath;

        final String fullPathString = fullPath.value();
        final String basePathString = basePath.value();

        if (!fullPathString.startsWith(basePathString)) {
            throw new HttpServerException("Request url " + CharSequences.quoteAndEscape(fullPathString) + " mapping mistake does not begin with " + CharSequences.quoteAndEscape(basePathString));
        }

        this.files.apply(UrlPath.parse(fullPathString.substring(basePathString.length())))
                .accept((webFile) -> this.found(webFile, request, response),
                        (status) -> this.notFound(status, response));
    }

    /**
     * The base {@link UrlPath}, everything after is considered the file path.
     */
    private final UrlPath basePath;

    /**
     * A {@link Function} which uses the {@link UrlPath} to resolve a real file.
     */
    private final Function<UrlPath, Either<WebFile, HttpStatus>> files;

    /**
     * Performs a {@link HttpHeaderName#LAST_MODIFIED} and then {@link HttpHeaderName#IF_MATCH} and then serves the full response.
     */
    private void found(final WebFile file,
                       final HttpRequest request,
                       final HttpResponse response) {

        // if last modified header present check if matches file.last modified
        HttpHeaderName.IF_MODIFIED_SINCE.parameterValue(request)
                .filter(header -> fileLastModifiedTest(header, file.lastModified()))
                .ifPresentOrElse(
                        (ifModifiedSince) -> this.notModified(file, response),
                        () -> this.modified(file, request, response));
    }

    /**
     * Returns true if the file last modified and last modified header match.
     */
    private static boolean fileLastModifiedTest(final LocalDateTime ifModifiedSince,
                                                final LocalDateTime fileLastModified) {
        return ifModifiedSince.isEqual(fileLastModified);
    }

    /**
     * Sets a not-modified response with the last-modified header set to the filesystem file last modified.
     */
    private void notModified(final WebFile file,
                             final HttpResponse response) {
        response.setStatus(HttpStatusCode.NOT_MODIFIED.status());
        response.addEntity(HttpEntity.with(this.headers(file), Binary.EMPTY));
    }

    /**
     * Creates a response with an OK, with the last-modified and content-length and file content as then body.
     */
    private void modified(final WebFile file,
                          final HttpRequest request,
                          final HttpResponse response) {
        try (final InputStream content = file.content()) {
            final Binary binaryContent = Binary.with(InputStreams.readAllBytes(content));
            final MediaType contentType = file.contentType();

            if (HttpHeaderName.ACCEPT.parameterValue(request).map(accept -> accept.test(contentType)).orElse(true)) {
                response.setStatus(HttpStatusCode.OK.status());
                response.addEntity(HttpEntity.with(this.headers(file), binaryContent));
            } else {
                response.setStatus(HttpStatusCode.NOT_ACCEPTABLE.status());
                response.addEntity(HttpEntity.EMPTY);
            }
        } catch (final IOException unable) {
            throw new HttpServerException("Failed to read file", unable);
        }
    }

    private Map<HttpHeaderName<?>, Object> headers(final WebFile file) {
        final Long contentSize = Long.valueOf(file.contentSize());

        return file.etag()
                .map( etag ->
                        Maps.of(HttpHeaderName.CONTENT_LENGTH, contentSize,
                                HttpHeaderName.CONTENT_TYPE, file.contentType(),
                                HttpHeaderName.LAST_MODIFIED, file.lastModified(),
                                HttpHeaderName.E_TAG, etag))
                .orElse(
                        Maps.of(HttpHeaderName.CONTENT_LENGTH, contentSize,
                                HttpHeaderName.CONTENT_TYPE, file.contentType(),
                                HttpHeaderName.LAST_MODIFIED, file.lastModified())
                );
    }

    /**
     * Either failed return the given {@link HttpStatus} which is probably {@link HttpStatusCode#NOT_FOUND}
     */
    private void notFound(final HttpStatus status,
                          final HttpResponse response) {
        response.setStatus(status);
    }

    @Override
    public String toString() {
        return this.basePath.toString();
    }
}
