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

package walkingkooka.net.email;

import walkingkooka.EmptyTextException;
import walkingkooka.InvalidCharacterException;
import walkingkooka.InvalidTextLengthException;

/**
 * The {@link EmailAddressParser} which throws an {@link IllegalArgumentException} on any failed tests.
 */
final class EmailAddressParserParse extends EmailAddressParser {

    static EmailAddress parseOrFail(final String email) {
        return new EmailAddressParserParse().parse(email);
    }

    static EmailAddressParserParse with() {
        return new EmailAddressParserParse();
    }

    private EmailAddressParserParse() {
        super();
    }

    @Override
    void emptyText() {
        throw new EmptyTextException(EmailAddress.class.getSimpleName());
    }

    @Override
    void emailTooLong(final String email) {
        throw new InvalidTextLengthException("Email", email, 0, EmailAddress.MAX_EMAIL_LENGTH);
    }

    @Override
    void missingUser() {
        this.fail(
            EmailAddress.missingUser()
        );
    }

    @Override
    void userNameTooLong(final int length,
                         final String email) {
        throw new InvalidTextLengthException(
            "Email username",
            email,
            0,
            EmailAddress.MAX_LOCAL_LENGTH
        );
    }

    @Override
    void missingHost() {
        this.fail(
            EmailAddress.missingHost()
        );
    }

    @Override
    void invalidCharacter(final int at, final String email) {
        throw new InvalidCharacterException(email, at);
    }

    @Override
    void invalidHostAddress(final RuntimeException failed) {
        throw failed;
    }

    private void fail(final String message) {
        throw new IllegalArgumentException(message);
    }

    @Override
    public String toString() {
        return "EmailAddress.with";
    }
}
