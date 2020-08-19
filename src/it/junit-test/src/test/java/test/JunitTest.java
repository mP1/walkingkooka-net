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

import walkingkooka.collect.Range;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.map.Maps;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.HostAddress;
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
import walkingkooka.net.header.EncodedText;
import walkingkooka.net.header.Encoding;
import walkingkooka.net.header.ETag;
import walkingkooka.net.header.ETagValidator;
import walkingkooka.net.header.HttpHeaderName;
import walkingkooka.net.header.IfRange;
import walkingkooka.net.header.LanguageName;
import walkingkooka.net.header.Link;
import walkingkooka.net.header.MediaType;
import walkingkooka.net.header.RangeHeaderValue;
import walkingkooka.net.header.RangeHeaderValueUnit;

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
}
