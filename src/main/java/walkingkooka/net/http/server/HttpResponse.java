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
import walkingkooka.net.header.HttpHeaderName;
import walkingkooka.net.http.HttpEntity;
import walkingkooka.net.http.HttpMethod;
import walkingkooka.net.http.HttpProtocolVersion;
import walkingkooka.net.http.HttpStatus;
import walkingkooka.net.http.HttpStatusCode;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.text.printer.TreePrintable;

import java.util.List;
import java.util.Objects;
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
     * <a href="https://restfulapi.net/http-status-codes/"></a>
     * <pre>
     * 405 (Method Not Allowed)
     * The API responds with a 405 error to indicate that the client tried to use an HTTP method that the resource does not allow. For instance, a read-only resource could support only GET and HEAD, while a controller resource might allow GET and POST, but not PUT or DELETE.
     *
     * A 405 response must include the Allow header, which lists the HTTP methods that the resource supports. For example:
     *
     * Allow: GET, POST
     * </pre>>
     */
    default void setMethodNotAllowed(final HttpMethod method,
                                     final List<HttpMethod> allowed) {
        Objects.requireNonNull(method, "method");
        Objects.requireNonNull(allowed, "allowed");

        this.setStatus(
            HttpStatusCode.METHOD_NOT_ALLOWED.setMessage(
                "Method " + method.value() + " not allowed"
            )
        );
        this.setEntity(
            this.entity()
                .addHeader(HttpHeaderName.ALLOW, allowed)
        );
    }

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
