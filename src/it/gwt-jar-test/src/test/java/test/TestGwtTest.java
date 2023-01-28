package test;

import com.google.gwt.junit.client.GWTTestCase;

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

import java.util.Optional;

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

    public void testSample() {
        assertEquals(
                Url.absolute(UrlScheme.HTTPS,
                        AbsoluteUrl.NO_CREDENTIALS,
                        HostAddress.with("example.com"),
                        Optional.empty(),
                        UrlPath.parse("/path1/path2"),
                        UrlQueryString.EMPTY.addParameter(UrlParameterName.with("query3"), "value3"),
                        UrlFragment.EMPTY),
                Url.parse("https://example.com/path1/path2?query3=value3")
        );

        final EmailAddress email = EmailAddress.parse("user4@example5.com");
        assertEquals(
                HostAddress.with("example5.com"),
                email.host()
        );
        assertEquals(
                "user4",
                email.user()
        );

        // https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Content-Range
        final ContentRange contentRange = ContentRange.parse("bytes 2-11/888");
        assertEquals(
                RangeHeaderUnit.BYTES,
                contentRange.unit()
        );
        assertEquals(
                Optional.of(Range.greaterThanEquals(2L).and(Range.lessThanEquals(11L))),
                contentRange.range()
        );
        assertEquals(
                Optional.of(888L),
                contentRange.size()
        );

        // https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Accept-Encoding
        final AcceptEncoding acceptEncoding = AcceptEncoding.parse("a;q=0.5,b");
        assertEquals(
                Lists.of(AcceptEncodingValue.with("b"),
                AcceptEncodingValue.with("a").setParameters(Maps.of(AcceptEncodingValueParameterName.with("q"), 0.5f))),
                acceptEncoding.qualityFactorSortedValues()
        );
    }
}
