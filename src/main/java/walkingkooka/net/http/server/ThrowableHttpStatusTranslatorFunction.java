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

package walkingkooka.net.http.server;

import walkingkooka.net.http.HttpStatus;
import walkingkooka.net.http.HttpStatusCode;

import java.util.Objects;
import java.util.function.Function;

/**
 * A {@link Function} that handles translating the following exceptions.
 * <ul>
 *     <li>{@link IllegalArgumentException} -> {@link HttpStatusCode#BAD_REQUEST}</li>
 *     <li>{@link IllegalStateException} -> {@link HttpStatusCode#INTERNAL_SERVER_ERROR}</li>
 *     <li>{@link HttpResponseHttpServerException} -> {@link HttpResponseHttpServerException#status()} the entity is ignored</li>
 *     <li>{@link UnsupportedOperationException} -> {@link HttpStatusCode#NOT_IMPLEMENTED}</li>
 *     <li>default -> {@link HttpStatusCode#INTERNAL_SERVER_ERROR}</li>
 * </ul>
 */
final class ThrowableHttpStatusTranslatorFunction implements Function<Throwable, HttpStatus> {

    /**
     * Singleton
     */
    final static ThrowableHttpStatusTranslatorFunction INSTANCE = new ThrowableHttpStatusTranslatorFunction();

    private ThrowableHttpStatusTranslatorFunction() {
        super();
    }

    @Override
    public HttpStatus apply(final Throwable throwable) {
        Objects.requireNonNull(throwable, "throwable");

        final HttpStatus status;

        do {
            final String message = throwable.getMessage();
            final String first = null != message ? HttpStatus.firstLineOfText(message) : message;
            if (throwable instanceof IllegalArgumentException) {
                status = HttpStatusCode.BAD_REQUEST.setMessageOrDefault(first);
                break;
            }
            if (throwable instanceof IllegalStateException) {
                status = HttpStatusCode.INTERNAL_SERVER_ERROR.setMessageOrDefault(first);
                break;
            }
            if (throwable instanceof HttpResponseHttpServerException) {
                status = ((HttpResponseHttpServerException) throwable).status();
                break;
            }
            if (throwable instanceof UnsupportedOperationException) {
                status = HttpStatusCode.NOT_IMPLEMENTED.setMessageOrDefault(first);
                break;
            }

            status = HttpStatusCode.INTERNAL_SERVER_ERROR.setMessageOrDefault(first);
        } while (false);

        return status;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
