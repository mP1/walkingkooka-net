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

package walkingkooka.net.sample;

import org.junit.jupiter.api.Assertions;
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
import walkingkooka.net.header.RangeHeaderValueUnit;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Sample {

    public static void main(final String[] args) {
        assertEquals(Url.absolute(UrlScheme.HTTPS,
                AbsoluteUrl.NO_CREDENTIALS,
                HostAddress.with("example.com"),
                Optional.empty(),
                UrlPath.parse("/path1/path2"),
                UrlQueryString.EMPTY.addParameter(UrlParameterName.with("query3"), "value3"),
                UrlFragment.EMPTY),
                Url.parse("https://example.com/path1/path2?query3=value3"));

        final EmailAddress email = EmailAddress.parse("user4@example5.com");
        Assertions.assertEquals(HostAddress.with("example5.com"), email.host());
        Assertions.assertEquals("user4", email.user());

        // https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Content-Range
        final ContentRange contentRange = ContentRange.parse("bytes 2-11/888");
        assertEquals(RangeHeaderValueUnit.BYTES, contentRange.unit());
        assertEquals(Optional.of(Range.greaterThanEquals(2L).and(Range.lessThanEquals(11L))), contentRange.range());
        assertEquals(Optional.of(888L), contentRange.size());

        // https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Accept-Encoding
        final AcceptEncoding acceptEncoding = AcceptEncoding.parse("a;q=0.5,b");
        assertEquals(Lists.of(AcceptEncodingValue.with("b"),
                AcceptEncodingValue.with("a").setParameters(Maps.of(AcceptEncodingValueParameterName.with("q"), 0.5f))),
                acceptEncoding.qualityFactorSortedValues());
    }
}
