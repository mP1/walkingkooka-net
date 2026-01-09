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
import walkingkooka.collect.list.Lists;
import walkingkooka.convert.ConverterContexts;
import walkingkooka.convert.Converters;
import walkingkooka.datetime.DateTimeContexts;
import walkingkooka.environment.EnvironmentContexts;
import walkingkooka.locale.LocaleContexts;
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.DataUrl;
import walkingkooka.net.HostAddress;
import walkingkooka.net.MailToUrl;
import walkingkooka.net.RelativeUrl;
import walkingkooka.net.Url;
import walkingkooka.net.convert.NetConverters;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.reflect.PublicStaticHelperTesting;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.text.LineEnding;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.ExpressionEvaluationContexts;
import walkingkooka.tree.expression.ExpressionFunctionName;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.expression.ExpressionReference;
import walkingkooka.tree.expression.function.UnknownExpressionFunctionException;

import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

public final class NetExpressionFunctionsTest implements PublicStaticHelperTesting<NetExpressionFunctions> {

    // emailAddress.....................................................................................................

    @Test
    public void testEmailAddressWithCharSequenceEmailAddress() {
        final EmailAddress emailAddress = EmailAddress.parse("user@example.com");

        this.evaluateAndCheck(
            "emailAddress",
            Lists.of(
                new StringBuilder(
                    emailAddress.text()
                )
            ),
            emailAddress
        );
    }

    @Test
    public void testEmailAddressWithStringEmailAddress() {
        final EmailAddress emailAddress = EmailAddress.parse("user@example.com");

        this.evaluateAndCheck(
            "emailAddress",
            Lists.of(
                emailAddress.text()
            ),
            emailAddress
        );
    }

    @Test
    public void testEmailAddressWithEmailAddress() {
        final EmailAddress emailAddress = EmailAddress.parse("user@example.com");

        this.evaluateAndCheck(
            "emailAddress",
            Lists.of(
                emailAddress
            ),
            emailAddress
        );
    }

    // getHost..........................................................................................................

    @Test
    public void testGetHostWithAbsoluteUrl() {
        final AbsoluteUrl url = AbsoluteUrl.parseAbsolute("http://example.com/path1");

        this.evaluateAndCheck(
            "getHost",
            Lists.of(
                url
            ),
            url.hostAddress()
        );
    }

    @Test
    public void testGetHostWithEmailAddress() {
        final EmailAddress emailAddress = EmailAddress.parse("user@example.com");

        this.evaluateAndCheck(
            "getHost",
            Lists.of(
                emailAddress
            ),
            emailAddress.hostAddress()
        );
    }

    @Test
    public void testGetHostWithStringWithAbsoluteUrl() {
        final AbsoluteUrl url = AbsoluteUrl.parseAbsolute("http://example.com/path1");

        this.evaluateAndCheck(
            "getHost",
            Lists.of(
                url.toString()
            ),
            url.hostAddress()
        );
    }

    @Test
    public void testGetHostWithStringWithEmailAddress() {
        final EmailAddress emailAddress = EmailAddress.parse("user@example.com");

        this.evaluateAndCheck(
            "getHost",
            Lists.of(
                emailAddress.text()
            ),
            emailAddress.hostAddress()
        );
    }

    // setHost..........................................................................................................

    @Test
    public void testSetHostWithAbsoluteUrlAndHostAddress() {
        final AbsoluteUrl url = Url.parseAbsolute("https://example.com/path1");
        final HostAddress hostAddress = HostAddress.with("different.com");

        this.evaluateAndCheck(
            "setHost",
            Lists.of(
                url,
                hostAddress
            ),
            url.setHost(hostAddress)
        );
    }

    @Test
    public void testSetHostWithAbsoluteUrlAndStringHostAddress() {
        final AbsoluteUrl url = Url.parseAbsolute("https://example.com/path1");
        final HostAddress hostAddress = HostAddress.with("different.com");

        this.evaluateAndCheck(
            "setHost",
            Lists.of(
                url,
                hostAddress.text()
            ),
            url.setHost(hostAddress)
        );
    }

    @Test
    public void testSetHostWithStringAbsoluteUrlAndStringHostAddress() {
        final AbsoluteUrl url = Url.parseAbsolute("https://example.com/path1");
        final HostAddress hostAddress = HostAddress.with("different.com");

        this.evaluateAndCheck(
            "setHost",
            Lists.of(
                url.text(),
                hostAddress.text()
            ),
            url.setHost(hostAddress)
        );
    }

    @Test
    public void testSetHostWithEmailAddressAndHostAddress() {
        final EmailAddress emailAddress = EmailAddress.parse("user@example.com");
        final HostAddress hostAddress = HostAddress.with("different.com");

        this.evaluateAndCheck(
            "setHost",
            Lists.of(
                emailAddress,
                hostAddress
            ),
            emailAddress.setHostAddress(hostAddress)
        );
    }

    @Test
    public void testSetHostWithStringEmailAddressAndStringHostAddress() {
        final EmailAddress emailAddress = EmailAddress.parse("user@example.com");
        final HostAddress hostAddress = HostAddress.with("different.com");

        this.evaluateAndCheck(
            "setHost",
            Lists.of(
                emailAddress.text(),
                hostAddress.text()
            ),
            emailAddress.setHostAddress(hostAddress)
        );
    }

    @Test
    public void testSetHostWithMailToUrlAndHostAddress() {
        final MailToUrl mailToUrl = Url.parseMailTo("mailto:user@example.com");
        final HostAddress hostAddress = HostAddress.with("different.com");

        this.evaluateAndCheck(
            "setHost",
            Lists.of(
                mailToUrl,
                hostAddress
            ),
            mailToUrl.setHostAddress(hostAddress)
        );
    }

    @Test
    public void testSetHostWithStringMailToUrlAndStringHostAddress() {
        final MailToUrl mailToUrl = Url.parseMailTo("mailto:user@example.com");
        final HostAddress hostAddress = HostAddress.with("different.com");

        this.evaluateAndCheck(
            "setHost",
            Lists.of(
                mailToUrl.text(),
                hostAddress.text()
            ),
            mailToUrl.setHostAddress(hostAddress)
        );
    }

    // url..............................................................................................................

    @Test
    public void testSetHostWithAbsoluteUrl() {
        final AbsoluteUrl url = Url.parseAbsolute("https://example.com/path1");

        this.evaluateAndCheck(
            "url",
            Lists.of(
                url
            ),
            url
        );
    }

    @Test
    public void testSetHostWithStringAbsoluteUrl() {
        final AbsoluteUrl url = Url.parseAbsolute("https://example.com/path1");

        this.evaluateAndCheck(
            "url",
            Lists.of(
                url.text()
            ),
            url
        );
    }

    @Test
    public void testSetHostWithDataUrl() {
        final DataUrl url = Url.parseData("data:,Hello%2C%20World%21");

        this.evaluateAndCheck(
            "url",
            Lists.of(
                url
            ),
            url
        );
    }

    @Test
    public void testSetHostWithStringDataUrl() {
        final DataUrl url = Url.parseData("data:,Hello%2C%20World%21");

        this.evaluateAndCheck(
            "url",
            Lists.of(
                url.text()
            ),
            url
        );
    }

    @Test
    public void testSetHostWithMailToUrl() {
        final MailToUrl url = Url.parseMailTo("mailto:user@example.com");

        this.evaluateAndCheck(
            "url",
            Lists.of(
                url
            ),
            url
        );
    }

    @Test
    public void testSetHostWithStringMailToUrl() {
        final MailToUrl url = Url.parseMailTo("mailto:user@example.com");

        this.evaluateAndCheck(
            "url",
            Lists.of(
                url.text()
            ),
            url
        );
    }

    @Test
    public void testSetHostWithRelativeUrl() {
        final RelativeUrl url = Url.parseRelative("/path1");

        this.evaluateAndCheck(
            "url",
            Lists.of(
                url
            ),
            url
        );
    }

    @Test
    public void testSetHostWithStringRelativeUrl() {
        final RelativeUrl url = Url.parseRelative("/path1");

        this.evaluateAndCheck(
            "url",
            Lists.of(
                url.text()
            ),
            url
        );
    }

    private void evaluateAndCheck(final String functionName,
                                  final List<Object> parameters,
                                  final Object expected) {
        this.checkEquals(
            expected,
            Expression.call(
                Expression.namedFunction(
                    ExpressionFunctionName.with(functionName)
                ),
                parameters.stream()
                    .map(Expression::value)
                    .collect(Collectors.toList())
            ).toValue(
                ExpressionEvaluationContexts.basic(
                    ExpressionNumberKind.BIG_DECIMAL,
                    (e, c) -> {
                        throw new UnsupportedOperationException();
                    },
                    (name) -> {
                        switch (name.value()) {
                            case "emailAddress":
                                return NetExpressionFunctions.emailAddress();
                            case "getHost":
                                return NetExpressionFunctions.getHost();
                            case "setHost":
                                return NetExpressionFunctions.setHost();
                            case "url":
                                return NetExpressionFunctions.url();
                            default:
                                throw new UnknownExpressionFunctionException(name);
                        }
                    }, // name -> function
                    (final RuntimeException cause) -> {
                        throw cause;
                    },
                    (ExpressionReference reference) -> {
                        throw new UnsupportedOperationException();
                    },
                    (ExpressionReference reference) -> {
                        throw new UnsupportedOperationException();
                    },
                    CaseSensitivity.SENSITIVE,
                    ConverterContexts.basic(
                        false, // canNumbersHaveGroupSeparator
                        Converters.EXCEL_1900_DATE_SYSTEM_OFFSET, // dateTimeOffset
                        LineEnding.NL,
                        ',', // valueSeparator
                        NetConverters.net(),
                        DateTimeContexts.fake(),
                        DecimalNumberContexts.fake()
                    ),
                    EnvironmentContexts.fake(),
                    LocaleContexts.fake()
                )
            )
        );
    }

    // class............................................................................................................

    @Override
    public boolean canHavePublicTypes(final Method method) {
        return false;
    }

    @Override
    public Class<NetExpressionFunctions> type() {
        return NetExpressionFunctions.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
