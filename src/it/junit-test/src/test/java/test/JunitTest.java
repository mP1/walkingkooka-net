/*
 * Copyright © 2020 Miroslav Pokorny
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
 */
package test;


import com.google.j2cl.junit.apt.J2clTestInput;
import org.junit.Assert;
import org.junit.Test;
import walkingkooka.Either;
import walkingkooka.collect.Range;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.map.Maps;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.HostAddress;
import walkingkooka.net.RelativeUrl;
import walkingkooka.net.Url;
import walkingkooka.net.UrlFragment;
import walkingkooka.net.UrlParameterName;
import walkingkooka.net.UrlPath;
import walkingkooka.net.UrlQueryString;
import walkingkooka.net.UrlScheme;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.net.header.Accept;
import walkingkooka.net.header.AcceptCharset;
import walkingkooka.net.header.AcceptEncoding;
import walkingkooka.net.header.AcceptEncodingValue;
import walkingkooka.net.header.AcceptEncodingValueParameterName;
import walkingkooka.net.header.AcceptLanguage;
import walkingkooka.net.header.CacheControl;
import walkingkooka.net.header.CharsetName;
import walkingkooka.net.header.ContentLanguage;
import walkingkooka.net.header.ContentRange;
import walkingkooka.net.header.Cookie;
import walkingkooka.net.header.ETag;
import walkingkooka.net.header.ETagValidator;
import walkingkooka.net.header.EncodedText;
import walkingkooka.net.header.Encoding;
import walkingkooka.net.header.HttpHeaderName;
import walkingkooka.net.header.IfRange;
import walkingkooka.net.header.LanguageName;
import walkingkooka.net.header.Link;
import walkingkooka.net.header.MediaType;
import walkingkooka.net.header.RangeHeaderValue;
import walkingkooka.net.header.RangeHeaderValueUnit;
import walkingkooka.net.http.HttpEntity;
import walkingkooka.net.http.HttpMethod;
import walkingkooka.net.http.HttpProtocolVersion;
import walkingkooka.net.http.HttpStatusCode;
import walkingkooka.net.http.HttpTransport;
import walkingkooka.net.http.server.FakeHttpRequest;
import walkingkooka.net.http.server.HttpRequest;
import walkingkooka.net.http.server.HttpRequestHttpResponseBiConsumers;
import walkingkooka.net.http.server.HttpRequests;
import walkingkooka.net.http.server.HttpResponse;
import walkingkooka.net.http.server.HttpResponses;
import walkingkooka.net.http.server.WebFile;
import walkingkooka.net.http.server.WebFileException;
import walkingkooka.net.http.server.WebFiles;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@J2clTestInput(JunitTest.class)
public class JunitTest {

    @Test
    public void testUrlParse() {
        Assert.assertEquals(Url.absolute(UrlScheme.HTTPS,
                AbsoluteUrl.NO_CREDENTIALS,
                HostAddress.with("example.com"),
                Optional.empty(),
                UrlPath.parse("/path1/path2"),
                UrlQueryString.EMPTY.addParameter(UrlParameterName.with("query3"), "value3"),
                UrlFragment.EMPTY),
                Url.parse("https://example.com/path1/path2?query3=value3"));
    }

    @Test
    public void testEmailAddressParse() {
        final EmailAddress email = EmailAddress.parse("user4@example5.com");

        Assert.assertEquals(HostAddress.with("example5.com"), email.host());
        Assert.assertEquals("user4", email.user());
    }

    @Test
    public void testContentRangeParse() {
        // https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Content-Range
        final ContentRange contentRange = ContentRange.parse("bytes 2-11/888");
        Assert.assertEquals(RangeHeaderValueUnit.BYTES, contentRange.unit());

        Assert.assertEquals(Optional.of(Range.greaterThanEquals(2L).and(Range.lessThanEquals(11L))), contentRange.range());
        Assert.assertEquals(Optional.of(888L), contentRange.size());
    }

    @Test
    public void testAcceptParse() {
        Accept.parse("a/a;q=1.0,b/b;q=0.75");
    }

    @Test
    public void testAcceptCharsetParse() {
        AcceptCharset.parse("UTF-8;bcd=123 ");
    }

    @Test
    public void testAcceptEncodingParse() {
        // https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Accept-Encoding
        final AcceptEncoding acceptEncoding = AcceptEncoding.parse("a;q=0.5,b");

        Assert.assertEquals(Lists.of(AcceptEncodingValue.with("b"),
                AcceptEncodingValue.with("a").setParameters(Maps.of(AcceptEncodingValueParameterName.with("q"), 0.5f))),
                acceptEncoding.qualityFactorSortedValues());
    }

    @Test
    public void testAcceptLanguageParse() {
        AcceptLanguage.parse("en, *;q=0.5");
    }

    @Test
    public void testCacheControlParse() {
        CacheControl.parse("no-cache, no-store, max-age=123");
    }

    @Test
    public void testCharsetName() {
        CharsetName.with("UTF-8");
    }

    @Test
    public void testClientCookie() {
        Cookie.parseClientHeader("cookie123=value456;");
    }

    @Test
    public void testContentLanguage() {
        ContentLanguage.parse("EN");
    }

    @Test
    public void testContentRange() {
        ContentRange.parse("bytes 2-11/888");
    }

    @Test
    public void testEncodedText() {
        EncodedText.with(CharsetName.ISO_8859_1, Optional.of(LanguageName.with("en")), "hello123");
    }

    @Test
    public void testEncoding() {
        Encoding.parse("gzip");
    }

    @Test
    public void testEtag() {
        ETag.parseOne("W/\"abc123\"");
    }

    @Test
    public void testIfRange() {
        final ETag etag = ETagValidator.WEAK.setValue("abc");
        IfRange.parse(etag.toHeaderText());
    }

    @Test
    public void testLanguageName() {
        LanguageName.with("au");
    }

    @Test
    public void testLinkParse() {
        Link.parse("<http://example.com>;type=text/plain");
    }

    @Test
    public void testRangeHeaderValue() {
        RangeHeaderValue.parse("bytes=123-");
    }

    @Test
    public void testServerCookie() {
        Cookie.parseClientHeader("cookie123=value456;");
    }

    @Test
    public void testHttpHeaderNameAndMediaType() {
        HttpHeaderName.CONTENT_TYPE.checkValue(MediaType.TEXT_PLAIN);
    }

    @Test
    public void testHttpEntity() {
        final String text = "abc123";
        final HttpEntity entity = HttpEntity.EMPTY
                .setBodyText(text);

        Assert.assertEquals(text, entity.bodyText());
    }

    @Test
    public void testHttpEntityEmptyAddHeader() {
        final HttpHeaderName<String> header = HttpHeaderName.with("X-Custom-Header-1").stringValues();
        final String value = "value-1";

        final HttpEntity entity = HttpEntity.EMPTY
                .addHeader(header, value);

        Assert.assertEquals(Maps.of(header, list(value)), entity.headers());
    }

    @Test
    public void testHttpEntityEmptyAddHeaderAddHeader() {
        final HttpHeaderName<String> header1 = HttpHeaderName.with("X-Custom-Header-1").stringValues();
        final String value1 = "value-1";

        final HttpHeaderName<String> header2 = HttpHeaderName.with("X-Custom-Header-2").stringValues();
        final String value2 = "value-2";

        final HttpEntity entity = HttpEntity.EMPTY
                .addHeader(header1, value1)
                .addHeader(header2, value2);

        Assert.assertEquals(Maps.of(header1, list(value1), header2, list(value2)), entity.headers());
    }

    @Test
    public void testHttpEntityEmptySetHeader() {
        final HttpHeaderName<String> header = HttpHeaderName.with("X-Custom-Header-1").stringValues();
        final String value = "value-1";

        final HttpEntity entity = HttpEntity.EMPTY
                .setHeader(header, list(value));

        Assert.assertEquals(Maps.of(header, list(value)), entity.headers());
    }

    private static <T> List<T> list(final T...values) {
        return Lists.of(values);
    }

    @Test
    public void testHttpRequest() {
        HttpRequests.value(HttpMethod.POST,
                HttpTransport.SECURED,
                Url.parseRelative("/path1/path2?query3=value4"),
                HttpProtocolVersion.VERSION_1_1,
                HttpEntity.EMPTY
                        .addHeader(HttpHeaderName.CONTENT_LENGTH, 123L)
                        .setBodyText("different-body-text"));
    }

    @Test
    public void testHttpResponsesRecording() {
        HttpResponses.recording();
    }

    @Test
    public void testWebFilesFake() {
        WebFiles.fake();
    }

    @Test
    public void testHttpRequestHttpResponseBiConsumersWebFile() {
        final HttpRequest request = new FakeHttpRequest() {

            @Override
            public RelativeUrl url() {
                return Url.parseRelative("/base/file/file123");
            }

            @Override
            public Map<HttpHeaderName<?>, List<?>> headers() {
                return HttpRequest.NO_HEADERS;
            }

            @Override
            public String toString() {
                return this.url() + " " + this.headers();
            }
        };

        final HttpResponse response = HttpResponses.recording();
        final String body = "Body123";

        HttpRequestHttpResponseBiConsumers.webFile(UrlPath.parse("/base/file/"),
                (urlPath -> Either.left(new WebFile() {
                    @Override
                    public LocalDateTime lastModified() throws WebFileException {
                        return LocalDateTime.now();
                    }

                    @Override
                    public MediaType contentType() throws WebFileException {
                        return MediaType.TEXT_PLAIN;
                    }

                    @Override
                    public long contentSize() throws WebFileException {
                        return body.length();
                    }

                    @Override
                    public InputStream content() throws WebFileException {
                        return new ByteArrayInputStream(body.getBytes(Charset.forName("UTF-8")));
                    }

                    @Override
                    public Optional<ETag> etag() throws WebFileException {
                        return Optional.empty();
                    }
                })
                ))
                .accept(request, response);

        Assert.assertEquals("http response status code\n" + response,
                HttpStatusCode.OK,
                response.status()
                        .orElse(HttpStatusCode.BAD_REQUEST.status())
                        .value());
        Assert.assertEquals("response body",
                body,
                response.entities().get(0).bodyText());
    }
}
