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


import walkingkooka.naming.Name;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.text.CharSequences;

import java.util.Optional;

/**
 * A {@link HeaderValueHandler} that parses a header value into a {@link EmailAddress}.
 * This is useful for headers such as {@link HttpHeaderName#FROM}.
 */
final class EmailAddressHeaderValueHandler extends NonStringHeaderValueHandler<EmailAddress> {

    /**
     * Singleton
     */
    final static EmailAddressHeaderValueHandler INSTANCE = new EmailAddressHeaderValueHandler();

    /**
     * Private ctor use singleton.
     */
    private EmailAddressHeaderValueHandler() {
        super();
    }

    @Override
    EmailAddress parse0(final String text, final Name name) {
        final Optional<EmailAddress> emailAddress = EmailAddress.tryParse(text);
        if (!emailAddress.isPresent()) {
            throw new IllegalArgumentException(name + " contains invalid email " + CharSequences.quote(text));
        }
        return emailAddress.get();
    }

    @Override
    void check0(final Object value, final Name name) {
        this.checkType(value, EmailAddress.class, name);
    }

    @Override
    String toText0(final EmailAddress value, final Name name) {
        return value.toString();
    }

    @Override
    public String toString() {
        return toStringType(EmailAddress.class);
    }
}
