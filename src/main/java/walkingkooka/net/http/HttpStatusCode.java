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

import walkingkooka.collect.set.Sets;
import walkingkooka.net.header.HttpHeaderName;
import walkingkooka.text.CharSequences;

import java.util.Set;

/**
 * Holds all possible http status codes.<br>
 * <a href="https://en.wikipedia.org/wiki/List_of_HTTP_status_codes"></a>
 */
public enum HttpStatusCode {

    /**
     * Continue={@link javax.servlet.http.HttpServletResponse#SC_CONTINUE}
     */
    CONTINUE(100/*javax.servlet.http.HttpServletResponse.SC_CONTINUE*/, "Continue"),

    /**
     * Switching protocols={@link javax.servlet.http.HttpServletResponse#SC_SWITCHING_PROTOCOLS}
     */
    SWITCHING_PROTOCOLS(101/*javax.servlet.http.HttpServletResponse.SC_SWITCHING_PROTOCOLS*/, "Switching protocols"),

    // Success

    /**
     * OK={@link javax.servlet.http.HttpServletResponse#SC_OK}
     */
    OK(200/*javax.servlet.http.HttpServletResponse.SC_OK*/, "OK"),

    /**
     * Created={@link javax.servlet.http.HttpServletResponse#SC_CREATED}
     */
    CREATED(201/*javax.servlet.http.HttpServletResponse.SC_CREATED*/, "Created"),

    /**
     * Accepted={@link javax.servlet.http.HttpServletResponse#SC_ACCEPTED}
     */
    ACCEPTED(202/*javax.servlet.http.HttpServletResponse.SC_ACCEPTED*/, "Accepted"),

    /**
     * Non Authoritative information={@link javax.servlet.http.HttpServletResponse#SC_NON_AUTHORITATIVE_INFORMATION}
     */
    NON_AUTHORITATIVE_INFORMATION(203/*javax.servlet.http.HttpServletResponse.SC_NON_AUTHORITATIVE_INFORMATION*/, "Non authoritative information"),

    /**
     * No content={@link javax.servlet.http.HttpServletResponse#SC_NO_CONTENT}
     */
    NO_CONTENT(204/*javax.servlet.http.HttpServletResponse.SC_NO_CONTENT*/, "No content"),

    /**
     * Reset content={@link javax.servlet.http.HttpServletResponse#SC_RESET_CONTENT}
     */
    RESET_CONTENT(205/*javax.servlet.http.HttpServletResponse.SC_RESET_CONTENT*/, "Reset content"),

    /**
     * Partial content={@link javax.servlet.http.HttpServletResponse#SC_PARTIAL_CONTENT}
     */
    PARTIAL_CONTENT(206/*javax.servlet.http.HttpServletResponse.SC_PARTIAL_CONTENT*/, "Partial content", HttpHeaderName.RANGE),

    // Redirect

    /**
     * Multiple choices={@link javax.servlet.http.HttpServletResponse#SC_MULTIPLE_CHOICES}
     */
    MULTIPLE_CHOICES(300/*javax.servlet.http.HttpServletResponse.SC_MULTIPLE_CHOICES*/, "Multiple choices"),

    /**
     * Moved permanently={@link javax.servlet.http.HttpServletResponse#SC_MOVED_PERMANENTLY}
     */
    MOVED_PERMANENTLY(301/*javax.servlet.http.HttpServletResponse.SC_MOVED_PERMANENTLY*/, "Moved permanently"),

    /**
     * Found={@link javax.servlet.http.HttpServletResponse#SC_MOVED_TEMPORARILY}
     */
    MOVED_TEMPORARILY(302/*javax.servlet.http.HttpServletResponse.SC_MOVED_TEMPORARILY*/, "Moved temporarily"),

    /**
     * Found={@link javax.servlet.http.HttpServletResponse#SC_FOUND}
     */
    FOUND(302/*javax.servlet.http.HttpServletResponse.SC_FOUND*/, "Found"),

    /**
     * See other={@link javax.servlet.http.HttpServletResponse#SC_SEE_OTHER}
     */
    SEE_OTHER(303/*javax.servlet.http.HttpServletResponse.SC_SEE_OTHER*/, "See other"),

    /**
     * Not Modified={@link javax.servlet.http.HttpServletResponse#SC_NOT_MODIFIED}
     */
    NOT_MODIFIED(304/*javax.servlet.http.HttpServletResponse.SC_NOT_MODIFIED*/, "Not modified"),

    /**
     * User Proxy={@link javax.servlet.http.HttpServletResponse#SC_USE_PROXY}
     */
    USE_PROXY(305/*javax.servlet.http.HttpServletResponse.SC_USE_PROXY*/, "User proxy"),

    /**
     * Temporary redirect={@link javax.servlet.http.HttpServletResponse#SC_TEMPORARY_REDIRECT}
     */
    TEMPORARY_REDIRECT(307/*javax.servlet.http.HttpServletResponse.SC_TEMPORARY_REDIRECT*/, "Temporary redirect"),

    // Client Error

    /**
     * Bad request={@link javax.servlet.http.HttpServletResponse#SC_BAD_REQUEST}
     */
    BAD_REQUEST(400/*javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST*/, "Bad request"),

    /**
     * Unauthorized={@link javax.servlet.http.HttpServletResponse#SC_UNAUTHORIZED}
     */
    UNAUTHORIZED(401/*javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED*/, "Unauthorized"),

    /**
     * Payment required={@link javax.servlet.http.HttpServletResponse#SC_PAYMENT_REQUIRED}
     */
    PAYMENT_REQUIRED(402/*javax.servlet.http.HttpServletResponse.SC_PAYMENT_REQUIRED*/, "Payment required"),

    /**
     * Forbidden={@link javax.servlet.http.HttpServletResponse#SC_FORBIDDEN}
     */
    FORBIDDEN(403/*javax.servlet.http.HttpServletResponse.SC_FORBIDDEN*/, "Forbidden"),

    /**
     * Not found={@link javax.servlet.http.HttpServletResponse#SC_NOT_FOUND}
     */
    NOT_FOUND(404/*javax.servlet.http.HttpServletResponse.SC_NOT_FOUND*/, "Not found"),

    /**
     * Method not allowed={@link javax.servlet.http.HttpServletResponse#SC_METHOD_NOT_ALLOWED}
     */
    METHOD_NOT_ALLOWED(405/*javax.servlet.http.HttpServletResponse.SC_METHOD_NOT_ALLOWED*/, "Method not allowed"),

    /**
     * Not acceptable={@link javax.servlet.http.HttpServletResponse#SC_NOT_ACCEPTABLE}
     */
    NOT_ACCEPTABLE(406/*javax.servlet.http.HttpServletResponse.SC_NOT_ACCEPTABLE*/, "Not acceptable"),

    /**
     * Request timeout={@link javax.servlet.http.HttpServletResponse#SC_REQUEST_TIMEOUT}
     */
    REQUEST_TIMEOUT(408/*javax.servlet.http.HttpServletResponse.SC_REQUEST_TIMEOUT*/, "Request timeout"),

    /**
     * Conflict={@link javax.servlet.http.HttpServletResponse#SC_CONFLICT}
     */
    CONFLICT(409/*javax.servlet.http.HttpServletResponse.SC_CONFLICT*/, "Conflict"),

    /**
     * Gone={@link javax.servlet.http.HttpServletResponse#SC_GONE}
     */
    GONE(410/*javax.servlet.http.HttpServletResponse.SC_GONE*/, "Gone"),

    /**
     * Length required={@link javax.servlet.http.HttpServletResponse#SC_LENGTH_REQUIRED}
     */
    LENGTH_REQUIRED(411/*javax.servlet.http.HttpServletResponse.SC_LENGTH_REQUIRED*/, "Length required"),

    /**
     * Precondition failed={@link javax.servlet.http.HttpServletResponse#SC_PRECONDITION_FAILED}
     */
    PRECONDITION_FAILED(412/*javax.servlet.http.HttpServletResponse.SC_PRECONDITION_FAILED*/, "Precondition failed"),

    /**
     * Proxy Authentication required={@link javax.servlet.http.HttpServletResponse#SC_PROXY_AUTHENTICATION_REQUIRED}
     */
    PROXY_AUTHENTICATION_REQUIRED(407/*javax.servlet.http.HttpServletResponse.SC_PROXY_AUTHENTICATION_REQUIRED*/, "Proxy Authentication required"),

    /**
     * Request Entity Too Large={@link javax.servlet.http.HttpServletResponse#SC_REQUEST_ENTITY_TOO_LARGE}
     */
    REQUEST_ENTITY_TOO_LARGE(413/*javax.servlet.http.HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE*/, "Request entity too large"),

    /**
     * Request-URI Too Long={@link javax.servlet.http.HttpServletResponse#SC_REQUEST_URI_TOO_LONG}
     */
    REQUEST_URI_TOO_LONG(414/*javax.servlet.http.HttpServletResponse.SC_REQUEST_URI_TOO_LONG*/, "Request-URI Too Long"),

    /**
     * Unsupported Media Type={@link javax.servlet.http.HttpServletResponse#SC_UNSUPPORTED_MEDIA_TYPE}
     */
    UNSUPPORTED_MEDIA_TYPE(415/*javax.servlet.http.HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE*/, "Unsupported Media Type"),

    /**
     * Requested Range Not Satisfiable={@link javax.servlet.http.HttpServletResponse#SC_REQUESTED_RANGE_NOT_SATISFIABLE}
     */
    REQUESTED_RANGE_NOT_SATISFIABLE(416/*javax.servlet.http.HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE*/, "Requested Range Not Satisfiable"),

    /**
     * Expectation Failed={@link javax.servlet.http.HttpServletResponse#SC_EXPECTATION_FAILED}
     */
    EXPECTATION_FAILED(417/*javax.servlet.http.HttpServletResponse.SC_EXPECTATION_FAILED*/, "Expectation Failed"),

    // Server Error

    /**
     * Internal Server Error={@link javax.servlet.http.HttpServletResponse#SC_INTERNAL_SERVER_ERROR}
     */
    INTERNAL_SERVER_ERROR(500/*javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR*/, "Internal Server Error"),

    /**
     * Not Implemented={@link javax.servlet.http.HttpServletResponse#SC_NOT_IMPLEMENTED}
     */
    NOT_IMPLEMENTED(501/*javax.servlet.http.HttpServletResponse.SC_NOT_IMPLEMENTED*/, "Not implemented"),

    /**
     * Bad Gateway={@link javax.servlet.http.HttpServletResponse#SC_BAD_GATEWAY}
     */
    BAD_GATEWAY(502/*javax.servlet.http.HttpServletResponse.SC_BAD_GATEWAY*/, "Bad Gateway"),

    /**
     * Service Unavailable={@link javax.servlet.http.HttpServletResponse#SC_SERVICE_UNAVAILABLE}
     */
    SERVICE_UNAVAILABLE(503/*javax.servlet.http.HttpServletResponse.SC_SERVICE_UNAVAILABLE*/, "Service Unavailable"),

    /**
     * Gateway Timeout={@link javax.servlet.http.HttpServletResponse#SC_GATEWAY_TIMEOUT}
     */
    GATEWAY_TIMEOUT(504/*javax.servlet.http.HttpServletResponse.SC_GATEWAY_TIMEOUT*/, "Gateway Timeout"),

    /**
     * HTTP Version Not Supported={@link javax.servlet.http.HttpServletResponse#SC_HTTP_VERSION_NOT_SUPPORTED}
     */
    HTTP_VERSION_NOT_SUPPORTED(505/*javax.servlet.http.HttpServletResponse.SC_HTTP_VERSION_NOT_SUPPORTED*/, "HTTP Version Not Supported");

    HttpStatusCode(final int code,
                   final String message,
                   final HttpHeaderName<?>...requiredHttpHeaders) {
        this.code = code;
        this.message = message;

        final HttpStatusCodeCategory category = HttpStatusCodeCategory.category(code);
        this.category = category;
        this.status = HttpStatus.with(this, this.message);

        // all redirects except for multiple choices require a location header.
        this.requiredHttpHeaders = isNotMultipleChoicesAndDirect(code, category) ?
                Sets.of(HttpHeaderName.LOCATION) :
                Sets.of(requiredHttpHeaders);
    }

    private static boolean isNotMultipleChoicesAndDirect(final int code, final HttpStatusCodeCategory category) {
        return javax.servlet.http.HttpServletResponse.SC_MULTIPLE_CHOICES != code && category == HttpStatusCodeCategory.REDIRECTION;
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

    /**
     * Some http status codes require one or more heades, eg a {@link #TEMPORARY_REDIRECT} require a {@link HttpHeaderName#LOCATION} header.
     */
    public Set<HttpHeaderName<?>> requiredHttpHeaders() {
        return this.requiredHttpHeaders;
    }

    private final Set<HttpHeaderName<?>> requiredHttpHeaders;
}
