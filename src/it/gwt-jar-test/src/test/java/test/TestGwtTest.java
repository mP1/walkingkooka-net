package test;

import com.google.gwt.junit.client.GWTTestCase;

import walkingkooka.Binary;
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
import walkingkooka.net.header.AcceptEncoding;
import walkingkooka.net.header.AcceptEncodingValue;
import walkingkooka.net.header.AcceptEncodingValueParameterName;
import walkingkooka.net.header.ContentRange;
import walkingkooka.net.header.RangeHeaderUnit;
import walkingkooka.net.http.HttpEntity;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

@walkingkooka.j2cl.locale.LocaleAware
public class TestGwtTest extends GWTTestCase {

    @Override
    public String getModuleName() {
        return "test.Test";
    }

    public void testAssertEquals() {
        assertEquals(
            1,
            1
        );
    }

    public void testAbsoluteUrl() {
        assertEquals(
            Url.absolute(UrlScheme.HTTPS,
                AbsoluteUrl.NO_CREDENTIALS,
                HostAddress.with("example.com"),
                Optional.empty(),
                UrlPath.parse("/path1/path2"),
                UrlQueryString.EMPTY.addParameter(UrlParameterName.with("query3"), "value3"),
                UrlFragment.with("fragment ")),
            Url.parse("https://example.com/path1/path2?query3=value3#fragment%20")
        );
    }

    public void testEmail() {
        final EmailAddress email = EmailAddress.parse("user4@example5.com");
        assertEquals(HostAddress.with("example5.com"), email.host());
        assertEquals("user4", email.user());
    }

    public void testContentRange() {
        // https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Content-Range
        final ContentRange contentRange = ContentRange.parse("bytes 2-11/888");
        assertEquals(RangeHeaderUnit.BYTES, contentRange.unit());
        assertEquals(Optional.of(Range.greaterThanEquals(2L).and(Range.lessThanEquals(11L))), contentRange.range());
        assertEquals(Optional.of(888L), contentRange.size());
    }

    public void testAcceptEncoding() {
        // https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Accept-Encoding
        final AcceptEncoding acceptEncoding = AcceptEncoding.parse("a;q=0.5,b");
        assertEquals(
            Lists.of(
                AcceptEncodingValue.with("b"),
                AcceptEncodingValue.with("a").setParameters(Maps.of(AcceptEncodingValueParameterName.with("q"), 0.5f))
            ),
            acceptEncoding.qualityFactorSortedValues());
    }

    public void testHttpEntitySetBodyBinary() {
        final String text = "Text123";

        final HttpEntity entity = HttpEntity.EMPTY.setBody(
            Binary.with(text.getBytes(StandardCharsets.UTF_8))
        );
        assertEquals(
            text,
            entity.bodyText()
        );
    }
}
