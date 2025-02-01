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

import walkingkooka.net.header.HasStatus;
import walkingkooka.net.http.HttpEntity;
import walkingkooka.net.http.HttpProtocolVersion;
import walkingkooka.net.http.HttpStatus;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.text.printer.TreePrintable;

import java.util.Optional;

/**
 * Defines a mutable HTTP response that accepts status and entities and provides methods as these may be filtered
 * or processed in some way.
 */
public interface HttpResponse extends HasStatus,
    TreePrintable {

    /**
     * Version setter.
     */
    void setVersion(final HttpProtocolVersion version);

    /**
     * Version getter
     */
    Optional<HttpProtocolVersion> version();

    /**
     * Sets the response status
     */
    void setStatus(final HttpStatus status);

    /**
     * Returns the {@link HttpStatus}
     */
    @Override
    Optional<HttpStatus> status();

    /**
     * Adds an entity to the response.
     */
    void setEntity(final HttpEntity entity);

    /**
     * Returns the {@link HttpEntity}.
     */
    HttpEntity entity();

    // TreePrintable....................................................................................................

    @Override
    default void printTree(final IndentingPrinter printer) {
        printer.println(HttpResponse.class.getSimpleName());
        printer.indent();
        {
            final HttpStatus status = this.status()
                .orElse(null);
            if (null != status) {
                printer.println(status.toString());
            }
            this.entity()
                .printTree(printer);
            printer.lineStart();
        }
        printer.outdent();
    }
}
