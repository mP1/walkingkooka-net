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

package walkingkooka.net.header;

import org.junit.jupiter.api.Test;
import walkingkooka.datetime.DateTimeSymbols;
import walkingkooka.math.DecimalNumberSymbols;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;

import java.text.DateFormatSymbols;
import java.text.DecimalFormatSymbols;
import java.util.Currency;
import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class HasContentTypeTest implements ClassTesting2<HasContentType> {

    // tryContentType...................................................................................................

    @Test
    public void testTryContentTypeWithNullTypeFails() {
        assertThrows(
            NullPointerException.class,
            () -> HasContentType.tryContentType(null)
        );
    }

    @Test
    public void testTryContentTypeWithUnknown() {
        this.tryContentTypeAndCheck(
            EmailAddress.parse("user@example.com")
        );
    }

    @Test
    public void testTryContentTypeWithCurrency() {
        this.tryContentTypeAndCheck(
            Currency.getInstance("AUD"),
            "application/json+java.util.Currency"
        );
    }

    @Test
    public void testTryContentTypeWithDateTimeSymbols() {
        this.tryContentTypeAndCheck(
            DateTimeSymbols.fromDateFormatSymbols(
                new DateFormatSymbols(
                    Locale.ENGLISH
                )
            ),
            "application/json+walkingkooka.datetime.DateTimeSymbols"
        );
    }

    @Test
    public void testTryContentTypeWithDecimalNumberSymbols() {
        this.tryContentTypeAndCheck(
            DecimalNumberSymbols.fromDecimalFormatSymbols(
                '+',
                DecimalFormatSymbols.getInstance(
                    Locale.ENGLISH
                )
            ),
            "application/json+walkingkooka.math.DecimalNumberSymbols"
        );
    }

    @Test
    public void testTryContentTypeWithLocale() {
        this.tryContentTypeAndCheck(
            Locale.ENGLISH,
            "application/json+java.util.Locale"
        );
    }

    private final static MediaType MEDIA_TYPE = MediaType.parse("test123/test456; charset=utf-8");

    @Test
    public void testTryContentTypeWithHasContentType() {
        this.tryContentTypeAndCheck(
            new TestHasContentType(),
            MEDIA_TYPE
        );
    }

    private final static class TestHasContentType implements HasContentType {

        @Override
        public Optional<MediaType> contentType() {
            return Optional.of(MEDIA_TYPE);
        }
    }

    private void tryContentTypeAndCheck(final Object value) {
        this.tryContentTypeAndCheck(
            value,
            Optional.empty()
        );
    }

    private void tryContentTypeAndCheck(final Object value,
                                        final String expected) {
        this.tryContentTypeAndCheck(
            value,
            MediaType.parse(expected)
        );
    }

    private void tryContentTypeAndCheck(final Object value,
                                        final MediaType expected) {
        this.tryContentTypeAndCheck(
            value,
            Optional.of(expected)
        );
    }

    private void tryContentTypeAndCheck(final Object value,
                                        final Optional<MediaType> expected) {
        this.checkEquals(
            expected,
            HasContentType.tryContentType(value),
            () -> value.getClass()
                .getName()
        );
    }

    // json.............................................................................................................

    @Test
    public void testJsonWithNullTypeFails() {
        assertThrows(
            NullPointerException.class,
            () -> HasContentType.json(null)
        );
    }

    @Test
    public void testJson() {
        this.jsonAndCheck(
            EmailAddress.class,
            MediaType.parse("application/json+walkingkooka.net.email.EmailAddress")
        );
    }

    private void jsonAndCheck(final Class<?> type,
                              final MediaType expected) {
        this.checkEquals(
            expected,
            HasContentType.json(type)
        );
    }

    // class............................................................................................................

    @Override
    public Class<HasContentType> type() {
        return HasContentType.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}