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

import org.junit.jupiter.api.Test;
import walkingkooka.Binary;
import walkingkooka.Either;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.map.Maps;
import walkingkooka.net.RelativeUrl;
import walkingkooka.net.Url;
import walkingkooka.net.UrlPath;
import walkingkooka.net.header.ETag;
import walkingkooka.net.header.HttpHeaderName;
import walkingkooka.net.header.MediaType;
import walkingkooka.net.http.HttpEntity;
import walkingkooka.net.http.HttpStatus;
import walkingkooka.net.http.HttpStatusCode;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class WebFileHttpHandlerTest extends HttpHandlerTestCase2<WebFileHttpHandler> {

    private final static LocalDateTime NO_LAST_MODIFIED = null;
    private final static LocalDateTime LAST_MODIFIED1 = LocalDateTime.of(1999, 12, 31, 6, 28, 29);
    private final static LocalDateTime LAST_MODIFIED2 = LocalDateTime.of(2000, 12, 31, 6, 28, 29);

    private final static String FILE1 = "file1.txt";
    private final static String FILE2 = "dir/file2";

    private final static MediaType CONTENT_TYPE1 = MediaType.parse("custom/file1");
    private final static MediaType CONTENT_TYPE2 = MediaType.parse("custom/file2");

    private final static Binary CONTENT1 = Binary.with("a1b2".getBytes(Charset.defaultCharset()));
    private final static Binary CONTENT2 = Binary.with("c3d4e5".getBytes(Charset.defaultCharset()));

    private final static HttpStatus FILE_NOT_FOUND = HttpStatusCode.NOT_FOUND.setMessage("File not found!");

    private final static ETag NO_ETAG = null;
    private final static ETag ETAG2 = ETag.parseOne("W/\"222\"");

    private final Function<UrlPath, Either<WebFile, HttpStatus>> FILES = (f) -> {
        switch (f.value()) {
            case FILE1:
                return Either.left(new WebFile() {
                    @Override
                    public LocalDateTime lastModified() {
                        return LAST_MODIFIED1;
                    }

                    @Override
                    public MediaType contentType() {
                        return CONTENT_TYPE1;
                    }

                    @Override
                    public long contentSize() {
                        return CONTENT1.size();
                    }

                    @Override
                    public InputStream content() {
                        return CONTENT1.inputStream();
                    }

                    @Override
                    public Optional<ETag> etag() {
                        return Optional.empty();
                    }
                });
            case FILE2:
                return Either.left(new WebFile() {
                    @Override
                    public LocalDateTime lastModified() {
                        return LAST_MODIFIED2;
                    }

                    @Override
                    public MediaType contentType() {
                        return CONTENT_TYPE2;
                    }

                    @Override
                    public long contentSize() {
                        return CONTENT2.size();
                    }

                    @Override
                    public InputStream content() {
                        return CONTENT2.inputStream();
                    }

                    @Override
                    public Optional<ETag> etag() {
                        return Optional.of(ETAG2);
                    }
                });
            default:
                return Either.right(FILE_NOT_FOUND);
        }
    };

    @Test
    public void testWithUrlNullUrlPathBaseFails() {
        assertThrows(NullPointerException.class, () -> WebFileHttpHandler.with(null, FILES)
        );
    }

    @Test
    public void testWithNullFilesFails() {
        assertThrows(NullPointerException.class, () -> WebFileHttpHandler.with(this.baseUrlPath(), null)
        );
    }

    // accept...........................................................................................................

    @Test
    public void testFileNotFound() {
        final HttpRequest request = this.request("file-not-found", NO_ETAG, NO_LAST_MODIFIED);
        final HttpResponse response = HttpResponses.recording();

        this.createHttpHandler()
                .handle(request, response);

        final HttpResponse expected = HttpResponses.recording();
        expected.setStatus(FILE_NOT_FOUND);
        expected.setEntity(HttpEntity.EMPTY);

        this.checkResponse(request, response, expected);
    }

    @Test
    public void testRequestWithoutIfNotModifiedHeader() {
        final HttpRequest request = this.request(FILE1, NO_ETAG, NO_LAST_MODIFIED);
        final HttpResponse response = HttpResponses.recording();

        this.createHttpHandler()
                .handle(request, response);

        final HttpResponse expected = HttpResponses.recording();
        expected.setStatus(HttpStatusCode.OK.status());

        expected.setEntity(
                HttpEntity.EMPTY
                        .addHeader(HttpHeaderName.LAST_MODIFIED, LAST_MODIFIED1)
                        .addHeader(HttpHeaderName.CONTENT_LENGTH, (long) CONTENT1.size())
                        .setContentType(CONTENT_TYPE1)
                        .setBody(CONTENT1)
        );

        this.checkResponse(request, response, expected);
    }

    @Test
    public void testRequestUrlRequiresNormalizationFileFound() {
        final HttpRequest request = this.request("/deleted/../" + FILE1, NO_ETAG, NO_LAST_MODIFIED);
        final HttpResponse response = HttpResponses.recording();

        this.createHttpHandler()
                .handle(request, response);

        final HttpResponse expected = HttpResponses.recording();
        expected.setStatus(HttpStatusCode.OK.status());

        expected.setEntity(
                HttpEntity.EMPTY
                        .addHeader(HttpHeaderName.LAST_MODIFIED, LAST_MODIFIED1)
                        .addHeader(HttpHeaderName.CONTENT_LENGTH, (long) CONTENT1.size())
                        .setContentType(CONTENT_TYPE1)
                        .setBody(CONTENT1));

        this.checkResponse(request, response, expected);
    }

    @Test
    public void testFileFound() {
        final HttpRequest request = this.request(FILE2, NO_ETAG, NO_LAST_MODIFIED);
        final HttpResponse response = HttpResponses.recording();

        this.createHttpHandler()
                .handle(request, response);

        final HttpResponse expected = HttpResponses.recording();
        expected.setStatus(HttpStatusCode.OK.status());

        expected.setEntity(HttpEntity.EMPTY
                .addHeader(HttpHeaderName.LAST_MODIFIED, LAST_MODIFIED2)
                .addHeader(HttpHeaderName.CONTENT_LENGTH, (long) CONTENT2.size())
                .addHeader(HttpHeaderName.CONTENT_TYPE, CONTENT_TYPE2)
                .addHeader(HttpHeaderName.E_TAG, ETAG2)
                .setBody(CONTENT2));

        this.checkResponse(request, response, expected);
    }

    @Test
    public void testRequestIfNotModifiedMatch() {
        final HttpRequest request = this.request(FILE1, NO_ETAG, LAST_MODIFIED1);
        final HttpResponse response = HttpResponses.recording();

        this.createHttpHandler()
                .handle(request, response);

        final HttpResponse expected = HttpResponses.recording();
        expected.setStatus(HttpStatusCode.NOT_MODIFIED.status());

        expected.setEntity(HttpEntity.EMPTY
                .addHeader(HttpHeaderName.LAST_MODIFIED, LAST_MODIFIED1)
                .addHeader(HttpHeaderName.CONTENT_LENGTH, (long) CONTENT1.size())
                .addHeader(HttpHeaderName.CONTENT_TYPE, CONTENT_TYPE1));

        this.checkResponse(request, response, expected);
    }

    @Test
    public void testRequestIfNotModifiedOlder() {
        final HttpRequest request = this.request(FILE1, NO_ETAG, LAST_MODIFIED1.minusSeconds(10));
        final HttpResponse response = HttpResponses.recording();

        this.createHttpHandler()
                .handle(request, response);

        final HttpResponse expected = HttpResponses.recording();
        expected.setStatus(HttpStatusCode.OK.status());

        expected.setEntity(HttpEntity.EMPTY
                .addHeader(HttpHeaderName.LAST_MODIFIED, LAST_MODIFIED1)
                .addHeader(HttpHeaderName.CONTENT_LENGTH, (long) CONTENT1.size())
                .addHeader(HttpHeaderName.CONTENT_TYPE, CONTENT_TYPE1)
                .setBody(CONTENT1));

        this.checkResponse(request, response, expected);
    }

    @Test
    public void testRequestIfMatchETag() {
        final HttpRequest request = this.request(FILE2, ETAG2, LAST_MODIFIED2);
        final HttpResponse response = HttpResponses.recording();

        this.createHttpHandler()
                .handle(request, response);

        final HttpResponse expected = HttpResponses.recording();
        expected.setStatus(HttpStatusCode.NOT_MODIFIED.status());

        expected.setEntity(HttpEntity.EMPTY
                .addHeader(HttpHeaderName.LAST_MODIFIED, LAST_MODIFIED2)
                .addHeader(HttpHeaderName.CONTENT_LENGTH, (long) CONTENT2.size())
                .addHeader(HttpHeaderName.CONTENT_TYPE, CONTENT_TYPE2)
                .addHeader(HttpHeaderName.E_TAG, ETAG2));

        this.checkResponse(request, response, expected);
    }

    private void checkResponse(final HttpRequest request,
                               final HttpResponse response,
                               final HttpResponse expected) {
        this.checkEquals(
                expected.status(),
                response.status(),
                () -> "response.status " + request
        );
        this.checkEquals(
                expected.entity(),
                response.entity(),
                () -> "response.entity " + request
        );
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createHttpHandler(), this.baseUrlPath().toString());
    }

    // helpers..........................................................................................................

    private WebFileHttpHandler createHttpHandler() {
        return WebFileHttpHandler.with(this.baseUrlPath(), FILES);
    }

    private UrlPath baseUrlPath() {
        return UrlPath.parse("/files/server/");
    }

    private HttpRequest request(final String url,
                                final ETag ifMatch,
                                final LocalDateTime ifNotModified) {
        return new FakeHttpRequest() {

            @Override
            public RelativeUrl url() {
                return Url.parseRelative(baseUrlPath() + url);
            }

            @Override
            public Map<HttpHeaderName<?>, List<?>> headers() {
                Map<HttpHeaderName<?>, List<?>> headers = Maps.sorted();

                if (null != ifMatch) {
                    headers.put(HttpHeaderName.IF_MATCH, list(ifMatch));
                }
                if (null != ifNotModified) {
                    headers.put(HttpHeaderName.IF_MODIFIED_SINCE, list(ifNotModified));
                }

                return headers;
            }

            @Override
            public String toString() {
                return this.url() + " " + this.headers();
            }
        };
    }

    private static List<Object> list(final Object... values) {
        return Lists.of(values);
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<WebFileHttpHandler> type() {
        return WebFileHttpHandler.class;
    }
}
