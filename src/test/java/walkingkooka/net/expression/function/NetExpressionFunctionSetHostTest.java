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

package walkingkooka.net.expression.function;

import org.junit.jupiter.api.Test;
import walkingkooka.Cast;
import walkingkooka.collect.list.Lists;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.HasHostAddress;
import walkingkooka.net.HostAddress;
import walkingkooka.net.MailToUrl;
import walkingkooka.net.Url;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.tree.expression.FakeExpressionEvaluationContext;

public final class NetExpressionFunctionSetHostTest extends NetExpressionFunctionTestCase<NetExpressionFunctionSetHost<FakeExpressionEvaluationContext>, HasHostAddress, FakeExpressionEvaluationContext> {

    @Test
    public void testApplyWithAbsoluteUrlAndHostName() {
        final AbsoluteUrl url = Url.parseAbsolute("https://example.com/path1");
        final HostAddress hostAddress = HostAddress.with("different.example.com");

        this.applyAndCheck(
            Lists.of(
                url,
                hostAddress
            ),
            url.setHostAddress(hostAddress)
        );
    }

    @Test
    public void testApplyWithEmailAddressAndHostName() {
        final EmailAddress emailAddress = EmailAddress.parse("user@example");
        final HostAddress hostAddress = HostAddress.with("different.example.com");

        this.applyAndCheck(
            Lists.of(
                emailAddress,
                hostAddress
            ),
            emailAddress.setHostAddress(hostAddress)
        );
    }

    @Test
    public void testApplyWithMailToUrlAndHostName() {
        final MailToUrl mailToUrl = Url.parseMailTo("mailto:user@example");
        final HostAddress hostAddress = HostAddress.with("different.example.com");

        this.applyAndCheck(
            Lists.of(
                mailToUrl,
                hostAddress
            ),
            mailToUrl.setHostAddress(hostAddress)
        );
    }

    @Override
    public NetExpressionFunctionSetHost<FakeExpressionEvaluationContext> createBiFunction() {
        return NetExpressionFunctionSetHost.instance();
    }

    @Override
    public FakeExpressionEvaluationContext createContext() {
        return new FakeExpressionEvaluationContext();
    }

    @Override
    public int minimumParameterCount() {
        return 2;
    }

    @Override
    public Class<NetExpressionFunctionSetHost<FakeExpressionEvaluationContext>> type() {
        return Cast.to(NetExpressionFunctionSetHost.class);
    }
}
