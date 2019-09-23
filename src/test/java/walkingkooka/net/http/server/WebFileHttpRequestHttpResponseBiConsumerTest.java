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
import walkingkooka.test.ClassTesting2;
import walkingkooka.test.ToStringTesting;
import walkingkooka.type.JavaVisibility;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class WebFileHttpRequestHttpResponseBiConsumerTest implements ClassTesting2<WebFileHttpRequestHttpResponseBiConsumer>,
        ToStringTesting<WebFileHttpRequestHttpResponseBiConsumer> {

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
        assertThrows(NullPointerException.class, () -> {
                    WebFileHttpRequestHttpResponseBiConsumer.with(null, FILES);
                }
        );
    }

    @Test
    public void testWithNullFilesFails() {
        assertThrows(NullPointerException.class, () -> {
                    WebFileHttpRequestHttpResponseBiConsumer.with(this.baseUrlPath(), null);
                }
        );
    }

    // accept...........................................................................................................

    @Test
    public void testFileNotFound() {
        final HttpRequest request = this.request("file-not-found", NO_ETAG, NO_LAST_MODIFIED);
        final RecordingHttpResponse response = HttpResponses.recording();

        this.createBiConsumer()
                .accept(request, response);

        final RecordingHttpResponse expected = HttpResponses.recording();
        expected.setStatus(FILE_NOT_FOUND);
        expected.addEntity(HttpEntity.EMPTY);

        this.checkResponse(request, response, expected);
    }

    @Test
    public void testRequestWithoutIfNotModifiedHeader() {
        final HttpRequest request = this.request(FILE1, NO_ETAG, NO_LAST_MODIFIED);
        final RecordingHttpResponse response = HttpResponses.recording();

        this.createBiConsumer()
                .accept(request, response);

        final RecordingHttpResponse expected = HttpResponses.recording();
        expected.setStatus(HttpStatusCode.OK.status());

        final Map<HttpHeaderName<?>, Object> headers = Maps.sorted();
        headers.put(HttpHeaderName.LAST_MODIFIED, LAST_MODIFIED1);
        headers.put(HttpHeaderName.CONTENT_LENGTH, Long.valueOf(CONTENT1.size()));
        headers.put(HttpHeaderName.CONTENT_TYPE, CONTENT_TYPE1);

        expected.addEntity(HttpEntity.with(headers, CONTENT1));

        this.checkResponse(request, response, expected);
    }

    @Test
    public void testRequestUrlRequiresNormalizationFileFound() {
        final HttpRequest request = this.request("/deleted/../" + FILE1, NO_ETAG, NO_LAST_MODIFIED);
        final RecordingHttpResponse response = HttpResponses.recording();

        this.createBiConsumer()
                .accept(request, response);

        final RecordingHttpResponse expected = HttpResponses.recording();
        expected.setStatus(HttpStatusCode.OK.status());

        final Map<HttpHeaderName<?>, Object> headers = Maps.sorted();
        headers.put(HttpHeaderName.LAST_MODIFIED, LAST_MODIFIED1);
        headers.put(HttpHeaderName.CONTENT_LENGTH, Long.valueOf(CONTENT1.size()));
        headers.put(HttpHeaderName.CONTENT_TYPE, CONTENT_TYPE1);

        expected.addEntity(HttpEntity.with(headers, CONTENT1));

        this.checkResponse(request, response, expected);
    }

    @Test
    public void testFileFound() {
        final HttpRequest request = this.request(FILE2, NO_ETAG, NO_LAST_MODIFIED);
        final RecordingHttpResponse response = HttpResponses.recording();

        this.createBiConsumer()
                .accept(request, response);

        final RecordingHttpResponse expected = HttpResponses.recording();
        expected.setStatus(HttpStatusCode.OK.status());

        final Map<HttpHeaderName<?>, Object> headers = Maps.sorted();
        headers.put(HttpHeaderName.LAST_MODIFIED, LAST_MODIFIED2);
        headers.put(HttpHeaderName.CONTENT_LENGTH, Long.valueOf(CONTENT2.size()));
        headers.put(HttpHeaderName.CONTENT_TYPE, CONTENT_TYPE2);
        headers.put(HttpHeaderName.E_TAG, ETAG2);

        expected.addEntity(HttpEntity.with(headers, CONTENT2));

        this.checkResponse(request, response, expected);
    }

    @Test
    public void testRequestIfNotModifiedMatch() {
        final HttpRequest request = this.request(FILE1, NO_ETAG, LAST_MODIFIED1);
        final RecordingHttpResponse response = HttpResponses.recording();

        this.createBiConsumer()
                .accept(request, response);

        final RecordingHttpResponse expected = HttpResponses.recording();
        expected.setStatus(HttpStatusCode.NOT_MODIFIED.status());

        final Map<HttpHeaderName<?>, Object> headers = Maps.sorted();
        headers.put(HttpHeaderName.LAST_MODIFIED, LAST_MODIFIED1);
        headers.put(HttpHeaderName.CONTENT_LENGTH, Long.valueOf(CONTENT1.size()));
        headers.put(HttpHeaderName.CONTENT_TYPE, CONTENT_TYPE1);

        expected.addEntity(HttpEntity.with(headers, HttpEntity.NO_BODY));

        this.checkResponse(request, response, expected);
    }

    @Test
    public void testRequestIfNotModifiedOlder() {
        final HttpRequest request = this.request(FILE1, NO_ETAG, LAST_MODIFIED1.minusSeconds(10));
        final RecordingHttpResponse response = HttpResponses.recording();

        this.createBiConsumer()
                .accept(request, response);

        final RecordingHttpResponse expected = HttpResponses.recording();
        expected.setStatus(HttpStatusCode.OK.status());

        final Map<HttpHeaderName<?>, Object> headers = Maps.sorted();
        headers.put(HttpHeaderName.LAST_MODIFIED, LAST_MODIFIED1);
        headers.put(HttpHeaderName.CONTENT_LENGTH, Long.valueOf(CONTENT1.size()));
        headers.put(HttpHeaderName.CONTENT_TYPE, CONTENT_TYPE1);

        expected.addEntity(HttpEntity.with(headers, CONTENT1));

        this.checkResponse(request, response, expected);
    }

    @Test
    public void testRequestIfMatchETag() {
        final HttpRequest request = this.request(FILE2, ETAG2, LAST_MODIFIED2);
        final RecordingHttpResponse response = HttpResponses.recording();

        this.createBiConsumer()
                .accept(request, response);

        final RecordingHttpResponse expected = HttpResponses.recording();
        expected.setStatus(HttpStatusCode.NOT_MODIFIED.status());

        final Map<HttpHeaderName<?>, Object> headers = Maps.sorted();
        headers.put(HttpHeaderName.LAST_MODIFIED, LAST_MODIFIED2);
        headers.put(HttpHeaderName.CONTENT_LENGTH, Long.valueOf(CONTENT2.size()));
        headers.put(HttpHeaderName.CONTENT_TYPE, CONTENT_TYPE2);
        headers.put(HttpHeaderName.E_TAG, ETAG2);

        expected.addEntity(HttpEntity.with(headers, HttpEntity.NO_BODY));

        this.checkResponse(request, response, expected);
    }

    private void checkResponse(final HttpRequest request,
                               final RecordingHttpResponse response,
                               final RecordingHttpResponse expected) {
        assertEquals(expected,
                response,
                () -> "request: " + request);
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createBiConsumer(), this.baseUrlPath().toString());
    }

    // helpers..........................................................................................................

    private WebFileHttpRequestHttpResponseBiConsumer createBiConsumer() {
        return WebFileHttpRequestHttpResponseBiConsumer.with(this.baseUrlPath(), FILES);
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
            public Map<HttpHeaderName<?>, Object> headers() {
                Map<HttpHeaderName<?>, Object> headers = Maps.sorted();

                if(null!= ifMatch) {
                    headers.put(HttpHeaderName.IF_MATCH, ifMatch);
                }
                if(null!=ifNotModified) {
                    headers.put(HttpHeaderName.IF_MODIFIED_SINCE, ifNotModified);
                }

                return headers;
            }

            @Override
            public String toString() {
                return this.url() + " " + this.headers();
            }
        };
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<WebFileHttpRequestHttpResponseBiConsumer> type() {
        return WebFileHttpRequestHttpResponseBiConsumer.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}