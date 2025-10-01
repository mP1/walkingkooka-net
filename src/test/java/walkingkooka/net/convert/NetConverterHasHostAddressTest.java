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

package walkingkooka.net.convert;

import org.junit.jupiter.api.Test;
import walkingkooka.Cast;
import walkingkooka.convert.FakeConverterContext;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.HasHostAddress;
import walkingkooka.net.HostAddress;
import walkingkooka.net.MailToUrl;
import walkingkooka.net.Url;
import walkingkooka.net.email.EmailAddress;

public final class NetConverterHasHostAddressTest extends NetConverterTestCase<NetConverterHasHostAddress<FakeConverterContext>, FakeConverterContext> {

    @Test
    public void testConvertStringToHasHostAddressFails() {
        this.convertFails(
            "user@example.com",
            HasHostAddress.class
        );
    }

    @Test
    public void testConvertAbsoluteUrlToHasHostAddress() {
        final AbsoluteUrl url = Url.parseAbsolute("http://example.com");

        this.convertAndCheck(
            url,
            HasHostAddress.class,
            url.hostAddress()
        );
    }

    @Test
    public void testConvertAbsoluteUrlToHostAddress() {
        final AbsoluteUrl url = Url.parseAbsolute("http://example.com");

        this.convertAndCheck(
            url,
            HostAddress.class,
            url.hostAddress()
        );
    }

    @Test
    public void testConvertEmailAddressToHasHostAddress() {
        final EmailAddress emailAddress = EmailAddress.parse("user@example.com");

        this.convertAndCheck(
            emailAddress,
            HasHostAddress.class,
            emailAddress.hostAddress()
        );
    }

    @Test
    public void testConvertEmailAddressToHostAddress() {
        final EmailAddress emailAddress = EmailAddress.parse("user@example.com");

        this.convertAndCheck(
            emailAddress,
            HostAddress.class,
            emailAddress.hostAddress()
        );
    }

    @Test
    public void testConvertMailToUrlToHasHostAddress() {
        final MailToUrl mailTo = Url.parseMailTo("mailto:user@example.com");
        this.convertAndCheck(
            mailTo,
            HasHostAddress.class,
            mailTo.hostAddress()
        );
    }

    @Test
    public void testConvertMailToUrlToHostAddress() {
        final MailToUrl mailTo = Url.parseMailTo("mailto:user@example.com");
        this.convertAndCheck(
            mailTo,
            HostAddress.class,
            mailTo.hostAddress()
        );
    }

    @Test
    public void testConvertRelativeUrlToHasHostAddressFails() {
        this.convertFails(
            "/path1?k1=v1#fragment2",
            HasHostAddress.class
        );
    }

    @Override
    public NetConverterHasHostAddress<FakeConverterContext> createConverter() {
        return NetConverterHasHostAddress.instance();
    }

    @Override
    public FakeConverterContext createContext() {
        return new FakeConverterContext();
    }

    @Override
    public Class<NetConverterHasHostAddress<FakeConverterContext>> type() {
        return Cast.to(NetConverterHasHostAddress.class);
    }
}
