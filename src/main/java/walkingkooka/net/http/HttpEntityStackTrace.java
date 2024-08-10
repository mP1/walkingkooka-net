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

package walkingkooka.net.http;

import javaemul.internal.annotations.GwtIncompatible;
import walkingkooka.net.header.MediaType;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Objects;

abstract class HttpEntityStackTrace extends HttpEntityStackTraceJ2cl {

    @GwtIncompatible
    static HttpEntity dumpStackTrace(final Throwable cause) {
        Objects.requireNonNull(cause, "cause");

        HttpEntity result;

        try (final StringWriter stringWriter = new StringWriter()) {
            try (final PrintWriter printWriter = new PrintWriter(stringWriter)) {
                cause.printStackTrace(printWriter);
                printWriter.flush();

                result = HttpEntity.EMPTY
                        .setContentType(MediaType.TEXT_PLAIN)
                        .setBodyText(stringWriter.toString())
                        .setContentLength();
            }
        } catch (final IOException never) {
            result = HttpEntity.EMPTY;
        }

        return result;
    }
}
