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

import walkingkooka.collect.map.Maps;
import walkingkooka.text.CharSequences;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * Holds all possible http status codes.<br>
 * <a href="https://en.wikipedia.org/wiki/List_of_HTTP_status_codes"></a>
 */
public final class HttpStatusCode {

    /**
     * Factory that returns a {@link HttpStatusCode} for the given code, reusing a constant where possible.
     */
    public static HttpStatusCode withCode(final int code) {
        final HttpStatusCode status = CONSTANTS.get(code);
        return null != status ?
                status :
                new HttpStatusCode(code, "Status=" + code);
    }

    /**
     * Simulates <code>Enum#values</code> in spirit returning all constants, only intended for test usages.
     */
    public static Collection<HttpStatusCode> values() {
        return Collections.unmodifiableCollection(CONSTANTS.values());
    }

    /**
     * A {@link Map} of integers to the equivalent {@link HttpStatusCode}.
     */
    // @VisibleForTesting
    final static Map<Integer, HttpStatusCode> CONSTANTS = Maps.sorted();

    /**
     * Private factory where each constant registers itself.
     */
    private static HttpStatusCode register(final int code,
                                           final String message) {
        final HttpStatusCode constant = new HttpStatusCode(
                code,
                message
        );
        CONSTANTS.putIfAbsent(code, constant);
        return constant;
    }

    /**
     * Continue={@link javax.servlet.http.HttpServletResponse#SC_CONTINUE}
     */
    public final static HttpStatusCode CONTINUE = register(100/*javax.servlet.http.HttpServletResponse.SC_CONTINUE*/, "Continue");

    /**
     * Switching protocols={@link javax.servlet.http.HttpServletResponse#SC_SWITCHING_PROTOCOLS}
     */
    public final static HttpStatusCode SWITCHING_PROTOCOLS = register(101/*javax.servlet.http.HttpServletResponse.SC_SWITCHING_PROTOCOLS*/, "Switching protocols");

    // Success

    /**
     * OK={@link javax.servlet.http.HttpServletResponse#SC_OK}
     */
    public final static HttpStatusCode OK = register(200/*javax.servlet.http.HttpServletResponse.SC_OK*/, "OK");

    /**
     * Created={@link javax.servlet.http.HttpServletResponse#SC_CREATED}
     */
    public final static HttpStatusCode CREATED = register(201/*javax.servlet.http.HttpServletResponse.SC_CREATED*/, "Created");

    /**
     * Accepted={@link javax.servlet.http.HttpServletResponse#SC_ACCEPTED}
     */
    public final static HttpStatusCode ACCEPTED = register(202/*javax.servlet.http.HttpServletResponse.SC_ACCEPTED*/, "Accepted");

    /**
     * Non Authoritative information={@link javax.servlet.http.HttpServletResponse#SC_NON_AUTHORITATIVE_INFORMATION}
     */
    public final static HttpStatusCode NON_AUTHORITATIVE_INFORMATION = register(203/*javax.servlet.http.HttpServletResponse.SC_NON_AUTHORITATIVE_INFORMATION*/, "Non authoritative information");

    /**
     * No content={@link javax.servlet.http.HttpServletResponse#SC_NO_CONTENT}
     */
    public final static HttpStatusCode NO_CONTENT = register(204/*javax.servlet.http.HttpServletResponse.SC_NO_CONTENT*/, "No content");

    /**
     * Reset content={@link javax.servlet.http.HttpServletResponse#SC_RESET_CONTENT}
     */
    public final static HttpStatusCode RESET_CONTENT = register(205/*javax.servlet.http.HttpServletResponse.SC_RESET_CONTENT*/, "Reset content");

    /**
     * Partial content={@link javax.servlet.http.HttpServletResponse#SC_PARTIAL_CONTENT}
     */
    public final static HttpStatusCode PARTIAL_CONTENT = register(206/*javax.servlet.http.HttpServletResponse.SC_PARTIAL_CONTENT*/, "Partial content");

    // Redirect

    /**
     * Multiple choices={@link javax.servlet.http.HttpServletResponse#SC_MULTIPLE_CHOICES}
     */
    public final static HttpStatusCode MULTIPLE_CHOICES = register(300/*javax.servlet.http.HttpServletResponse.SC_MULTIPLE_CHOICES*/, "Multiple choices");

    /**
     * Moved permanently={@link javax.servlet.http.HttpServletResponse#SC_MOVED_PERMANENTLY}
     */
    public final static HttpStatusCode MOVED_PERMANENTLY = register(301/*javax.servlet.http.HttpServletResponse.SC_MOVED_PERMANENTLY*/, "Moved permanently");

    /**
     * Found={@link javax.servlet.http.HttpServletResponse#SC_MOVED_TEMPORARILY}
     */
    public final static HttpStatusCode MOVED_TEMPORARILY = register(302/*javax.servlet.http.HttpServletResponse.SC_MOVED_TEMPORARILY*/, "Moved temporarily");

    /**
     * Found={@link javax.servlet.http.HttpServletResponse#SC_FOUND}
     */
    public final static HttpStatusCode FOUND = register(302/*javax.servlet.http.HttpServletResponse.SC_FOUND*/, "Found");

    /**
     * See other={@link javax.servlet.http.HttpServletResponse#SC_SEE_OTHER}
     */
    public final static HttpStatusCode SEE_OTHER = register(303/*javax.servlet.http.HttpServletResponse.SC_SEE_OTHER*/, "See other");

    /**
     * Not Modified={@link javax.servlet.http.HttpServletResponse#SC_NOT_MODIFIED}
     */
    public final static HttpStatusCode NOT_MODIFIED = register(304/*javax.servlet.http.HttpServletResponse.SC_NOT_MODIFIED*/, "Not modified");

    /**
     * User Proxy={@link javax.servlet.http.HttpServletResponse#SC_USE_PROXY}
     */
    public final static HttpStatusCode USE_PROXY = register(305/*javax.servlet.http.HttpServletResponse.SC_USE_PROXY*/, "Use proxy");

    /**
     * Temporary redirect={@link javax.servlet.http.HttpServletResponse#SC_TEMPORARY_REDIRECT}
     */
    public final static HttpStatusCode TEMPORARY_REDIRECT = register(307/*javax.servlet.http.HttpServletResponse.SC_TEMPORARY_REDIRECT*/, "Temporary redirect");

    // Client Error

    /**
     * Bad request={@link javax.servlet.http.HttpServletResponse#SC_BAD_REQUEST}
     */
    public final static HttpStatusCode BAD_REQUEST = register(400/*javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST*/, "Bad request");

    /**
     * Unauthorized={@link javax.servlet.http.HttpServletResponse#SC_UNAUTHORIZED}
     */
    public final static HttpStatusCode UNAUTHORIZED = register(401/*javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED*/, "Unauthorized");

    /**
     * Payment required={@link javax.servlet.http.HttpServletResponse#SC_PAYMENT_REQUIRED}
     */
    public final static HttpStatusCode PAYMENT_REQUIRED = register(402/*javax.servlet.http.HttpServletResponse.SC_PAYMENT_REQUIRED*/, "Payment required");

    /**
     * Forbidden={@link javax.servlet.http.HttpServletResponse#SC_FORBIDDEN}
     */
    public final static HttpStatusCode FORBIDDEN = register(403/*javax.servlet.http.HttpServletResponse.SC_FORBIDDEN*/, "Forbidden");

    /**
     * Not found={@link javax.servlet.http.HttpServletResponse#SC_NOT_FOUND}
     */
    public final static HttpStatusCode NOT_FOUND = register(404/*javax.servlet.http.HttpServletResponse.SC_NOT_FOUND*/, "Not found");

    /**
     * Method not allowed={@link javax.servlet.http.HttpServletResponse#SC_METHOD_NOT_ALLOWED}
     */
    public final static HttpStatusCode METHOD_NOT_ALLOWED = register(405/*javax.servlet.http.HttpServletResponse.SC_METHOD_NOT_ALLOWED*/, "Method not allowed");

    /**
     * Not acceptable={@link javax.servlet.http.HttpServletResponse#SC_NOT_ACCEPTABLE}
     */
    public final static HttpStatusCode NOT_ACCEPTABLE = register(406/*javax.servlet.http.HttpServletResponse.SC_NOT_ACCEPTABLE*/, "Not acceptable");

    /**
     * Request timeout={@link javax.servlet.http.HttpServletResponse#SC_REQUEST_TIMEOUT}
     */
    public final static HttpStatusCode REQUEST_TIMEOUT = register(408/*javax.servlet.http.HttpServletResponse.SC_REQUEST_TIMEOUT*/, "Request timeout");

    /**
     * Conflict={@link javax.servlet.http.HttpServletResponse#SC_CONFLICT}
     */
    public final static HttpStatusCode CONFLICT = register(409/*javax.servlet.http.HttpServletResponse.SC_CONFLICT*/, "Conflict");

    /**
     * Gone={@link javax.servlet.http.HttpServletResponse#SC_GONE}
     */
    public final static HttpStatusCode GONE = register(410/*javax.servlet.http.HttpServletResponse.SC_GONE*/, "Gone");

    /**
     * Length required={@link javax.servlet.http.HttpServletResponse#SC_LENGTH_REQUIRED}
     */
    public final static HttpStatusCode LENGTH_REQUIRED = register(411/*javax.servlet.http.HttpServletResponse.SC_LENGTH_REQUIRED*/, "Length required");

    /**
     * Precondition failed={@link javax.servlet.http.HttpServletResponse#SC_PRECONDITION_FAILED}
     */
    public final static HttpStatusCode PRECONDITION_FAILED = register(412/*javax.servlet.http.HttpServletResponse.SC_PRECONDITION_FAILED*/, "Precondition failed");

    /**
     * Proxy Authentication required={@link javax.servlet.http.HttpServletResponse#SC_PROXY_AUTHENTICATION_REQUIRED}
     */
    public final static HttpStatusCode PROXY_AUTHENTICATION_REQUIRED = register(407/*javax.servlet.http.HttpServletResponse.SC_PROXY_AUTHENTICATION_REQUIRED*/, "Proxy Authentication required");

    /**
     * Request Entity Too Large={@link javax.servlet.http.HttpServletResponse#SC_REQUEST_ENTITY_TOO_LARGE}
     */
    public final static HttpStatusCode REQUEST_ENTITY_TOO_LARGE = register(413/*javax.servlet.http.HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE*/, "Request entity too large");

    /**
     * Request-URI Too Long={@link javax.servlet.http.HttpServletResponse#SC_REQUEST_URI_TOO_LONG}
     */
    public final static HttpStatusCode REQUEST_URI_TOO_LONG = register(414/*javax.servlet.http.HttpServletResponse.SC_REQUEST_URI_TOO_LONG*/, "Request URI Too Long");

    /**
     * Unsupported Media Type={@link javax.servlet.http.HttpServletResponse#SC_UNSUPPORTED_MEDIA_TYPE}
     */
    public final static HttpStatusCode UNSUPPORTED_MEDIA_TYPE = register(415/*javax.servlet.http.HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE*/, "Unsupported Media Type");

    /**
     * Requested Range Not Satisfiable={@link javax.servlet.http.HttpServletResponse#SC_REQUESTED_RANGE_NOT_SATISFIABLE}
     */
    public final static HttpStatusCode REQUESTED_RANGE_NOT_SATISFIABLE = register(416/*javax.servlet.http.HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE*/, "Requested Range Not Satisfiable");

    /**
     * Expectation Failed={@link javax.servlet.http.HttpServletResponse#SC_EXPECTATION_FAILED}
     */
    public final static HttpStatusCode EXPECTATION_FAILED = register(417/*javax.servlet.http.HttpServletResponse.SC_EXPECTATION_FAILED*/, "Expectation Failed");

    // Server Error

    /**
     * Internal Server Error={@link javax.servlet.http.HttpServletResponse#SC_INTERNAL_SERVER_ERROR}
     */
    public final static HttpStatusCode INTERNAL_SERVER_ERROR = register(500/*javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR*/, "Internal Server Error");

    /**
     * Not Implemented={@link javax.servlet.http.HttpServletResponse#SC_NOT_IMPLEMENTED}
     */
    public final static HttpStatusCode NOT_IMPLEMENTED = register(501/*javax.servlet.http.HttpServletResponse.SC_NOT_IMPLEMENTED*/, "Not implemented");

    /**
     * Bad Gateway={@link javax.servlet.http.HttpServletResponse#SC_BAD_GATEWAY}
     */
    public final static HttpStatusCode BAD_GATEWAY = register(502/*javax.servlet.http.HttpServletResponse.SC_BAD_GATEWAY*/, "Bad Gateway");

    /**
     * Service Unavailable={@link javax.servlet.http.HttpServletResponse#SC_SERVICE_UNAVAILABLE}
     */
    public final static HttpStatusCode SERVICE_UNAVAILABLE = register(503/*javax.servlet.http.HttpServletResponse.SC_SERVICE_UNAVAILABLE*/, "Service Unavailable");

    /**
     * Gateway Timeout={@link javax.servlet.http.HttpServletResponse#SC_GATEWAY_TIMEOUT}
     */
    public final static HttpStatusCode GATEWAY_TIMEOUT = register(504/*javax.servlet.http.HttpServletResponse.SC_GATEWAY_TIMEOUT*/, "Gateway Timeout");

    /**
     * HTTP Version Not Supported={@link javax.servlet.http.HttpServletResponse#SC_HTTP_VERSION_NOT_SUPPORTED}
     */
    public final static HttpStatusCode HTTP_VERSION_NOT_SUPPORTED = register(505/*javax.servlet.http.HttpServletResponse.SC_HTTP_VERSION_NOT_SUPPORTED*/, "HTTP Version Not Supported");

    /**
     * Package private ctor, use factory.
     */
    private HttpStatusCode(final int code,
                           final String message) {
        this.code = code;
        this.message = message;

        final HttpStatusCodeCategory category = HttpStatusCodeCategory.category(code);
        this.category = category;
        this.status = HttpStatus.with(this, this.message);
    }

    /**
     * Creates a new {@link HttpStatus} with this code and the given message.
     */
    public HttpStatus setMessage(final String message) {
        return this.message.equals(message) ?
                this.status() :
                HttpStatus.with(this, message);
    }

    /**
     * Creates a new {@link HttpStatus} with this code and the given message.
     * If the message is null or empty the status with its default message is returned.
     */
    public HttpStatus setMessageOrDefault(final String message) {
        return CharSequences.isNullOrEmpty(message) || message.trim().isEmpty() || this.message.equals(message) ?
                this.status() :
                HttpStatus.with(this, message);
    }

    /**
     * Returns a {@link HttpStatus} with this code and a default message.
     */
    public HttpStatus status() {
        return this.status;
    }

    private final HttpStatus status;

    /**
     * The numeric value of this code.
     */
    public int code() {
        return this.code;
    }

    private final int code;

    /**
     * Returns the category for this code.
     */
    public HttpStatusCodeCategory category() {
        return this.category;
    }

    private final HttpStatusCodeCategory category;

    /**
     * The default message for this code.
     */
    final String message;

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(this.code, this.message);
    }

    @Override
    public boolean equals(final Object other) {
        return this == other || other instanceof HttpStatusCode && this.equals0((HttpStatusCode) other);
    }

    private boolean equals0(final HttpStatusCode other) {
        return this.code == other.code && this.message.equals(other.message);
    }

    @Override
    public String toString() {
        return String.valueOf(this.code);
    }
}
