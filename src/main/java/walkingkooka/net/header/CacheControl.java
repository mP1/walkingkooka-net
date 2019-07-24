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

import walkingkooka.collect.list.Lists;

import java.util.List;
import java.util.Objects;


/**
 * Content encoding
 * <a href="https://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html"></a>
 * <pre>
 * 14.9 Cache-Control
 * The Cache-Control general-header field is used to specify directives that MUST be obeyed by all caching mechanisms along the request/response chain. The directives specify behavior intended to prevent caches from adversely interfering with the request or response. These directives typically override the default caching algorithms. Cache directives are unidirectional in that the presence of a directive in a request does not imply that the same directive is to be given in the response.
 *
 *       Note that HTTP/1.0 caches might not implement Cache-Control and
 *       might only implement Pragma: no-cache (see section 14.32).
 * Cache directives MUST be passed through by a proxy or gateway application, regardless of their significance to that application, since the directives might be applicable to all recipients along the request/response chain. It is not possible to specify a cache- directive for a specific cache.
 *
 *     Cache-Control   = "Cache-Control" ":" 1#cache-directive
 *     cache-directive = cache-request-directive
 *          | cache-response-directive
 *     cache-request-directive =
 *            "no-cache"                          ; Section 14.9.1
 *          | "no-store"                          ; Section 14.9.2
 *          | "max-age" "=" delta-seconds         ; Section 14.9.3, 14.9.4
 *          | "max-stale" [ "=" delta-seconds ]   ; Section 14.9.3
 *          | "min-fresh" "=" delta-seconds       ; Section 14.9.3
 *          | "no-transform"                      ; Section 14.9.5
 *          | "only-if-cached"                    ; Section 14.9.4
 *          | cache-extension                     ; Section 14.9.6
 *      cache-response-directive =
 *            "public"                               ; Section 14.9.1
 *          | "private" [ "=" <"> 1#field-name <"> ] ; Section 14.9.1
 *          | "no-cache" [ "=" <"> 1#field-name <"> ]; Section 14.9.1
 *          | "no-store"                             ; Section 14.9.2
 *          | "no-transform"                         ; Section 14.9.5
 *          | "must-revalidate"                      ; Section 14.9.4
 *          | "proxy-revalidate"                     ; Section 14.9.4
 *          | "max-age" "=" delta-seconds            ; Section 14.9.3
 *          | "s-maxage" "=" delta-seconds           ; Section 14.9.3
 *          | cache-extension                        ; Section 14.9.6
 *     cache-extension = token [ "=" ( token | quoted-string ) ]
 * When a directive appears without any 1#field-name parameter, the directive applies to the entire request or response. When such a directive appears with a 1#field-name parameter, it applies only to the named field or fields, and not to the rest of the request or response. This mechanism supports extensibility; implementations of future versions of the HTTP protocol might apply these directives to header fields not defined in HTTP/1.1.
 *
 * The cache-control directives can be broken down into these general categories:
 *
 *       - Restrictions on what are cacheable; these may only be imposed by
 *         the origin server.
 *       - Restrictions on what may be stored by a cache; these may be
 *         imposed by either the origin server or the user agent.
 *       - Modifications of the basic expiration mechanism; these may be
 *         imposed by either the origin server or the user agent.
 *       - Controls over cache revalidation and reload; these may only be
 *         imposed by a user agent.
 *       - Control over transformation of entities.
 *       - Extensions to the caching system.
 * 14.9.1 What is Cacheable
 * By default, a response is cacheable if the requirements of the request method, request header fields, and the response status indicate that it is cacheable. Section 13.4 summarizes these defaults for cacheability. The following Cache-Control response directives allow an origin server to override the default cacheability of a response:
 *
 * public
 * Indicates that the response MAY be cached by any cache, even if it would normally be non-cacheable or cacheable only within a non- shared cache. (See also Authorization, section 14.8, for additional details.)
 * private
 * Indicates that all or part of the response message is intended for a single user and MUST NOT be cached by a shared cache. This allows an origin server to state that the specified parts of the
 * response are intended for only one user and are not a valid response for requests by other users. A private (non-shared) cache MAY cache the response.
 * Note: This usage of the word private only controls where the response may be cached, and cannot ensure the privacy of the message content.
 *
 * no-cache
 * If the no-cache directive does not specify a field-name, then a cache MUST NOT use the response to satisfy a subsequent request without successful revalidation with the origin server. This allows an origin server to prevent caching even by caches that have been configured to return stale responses to client requests.
 * If the no-cache directive does specify one or more field-names, then a cache MAY use the response to satisfy a subsequent request, subject to any other restrictions on caching. However, the specified field-name(s) MUST NOT be sent in the response to a subsequent request without successful revalidation with the origin server. This allows an origin server to prevent the re-use of certain header fields in a response, while still allowing caching of the rest of the response.
 * Note: Most HTTP/1.0 caches will not recognize or obey this directive.
 *
 * 14.9.2 What May be Stored by Caches
 * no-store
 * The purpose of the no-store directive is to prevent the inadvertent release or retention of sensitive information (for example, on backup tapes). The no-store directive applies to the entire message, and MAY be sent either in a response or in a request. If sent in a request, a cache MUST NOT store any part of either this request or any response to it. If sent in a response, a cache MUST NOT store any part of either this response or the request that elicited it. This directive applies to both non- shared and shared caches. "MUST NOT store" in this context means that the cache MUST NOT intentionally store the information in non-volatile storage, and MUST make a best-effort attempt to remove the information from volatile storage as promptly as possible after forwarding it.
 * Even when this directive is associated with a response, users might explicitly store such a response outside of the caching system (e.g., with a "Save As" dialog). History buffers MAY store such responses as part of their normal operation.
 * The purpose of this directive is to meet the stated requirements of certain users and service authors who are concerned about accidental releases of information via unanticipated accesses to cache data structures. While the use of this directive might improve privacy in some cases, we caution that it is NOT in any way a reliable or sufficient mechanism for ensuring privacy. In particular, malicious or compromised caches might not recognize or obey this directive, and communications networks might be vulnerable to eavesdropping.
 * 14.9.3 Modifications of the Basic Expiration Mechanism
 * The expiration time of an entity MAY be specified by the origin server using the Expires header (see section 14.21). Alternatively, it MAY be specified using the max-age directive in a response. When the max-age cache-control directive is present in a cached response, the response is stale if its current age is greater than the age value given (in seconds) at the time of a new request for that resource. The max-age directive on a response implies that the response is cacheable (i.e., "public") unless some other, more restrictive cache directive is also present.
 *
 * If a response includes both an Expires header and a max-age directive, the max-age directive overrides the Expires header, even if the Expires header is more restrictive. This rule allows an origin server to provide, for a given response, a longer expiration time to an HTTP/1.1 (or later) cache than to an HTTP/1.0 cache. This might be useful if certain HTTP/1.0 caches improperly calculate ages or expiration times, perhaps due to desynchronized clocks.
 *
 * Many HTTP/1.0 cache implementations will treat an Expires value that is less than or equal to the response Date value as being equivalent to the Cache-Control response directive "no-cache". If an HTTP/1.1 cache receives such a response, and the response does not include a Cache-Control header field, it SHOULD consider the response to be non-cacheable in order to retain compatibility with HTTP/1.0 servers.
 *
 * Note: An origin server might wish to use a relatively new HTTP cache control feature, such as the "private" directive, on a network including older caches that do not understand that feature. The origin server will need to combine the new feature with an Expires field whose value is less than or equal to the Date value. This will prevent older caches from improperly caching the response.
 *
 * s-maxage
 * If a response includes an s-maxage directive, then for a shared cache (but not for a private cache), the maximum age specified by this directive overrides the maximum age specified by either the max-age directive or the Expires header. The s-maxage directive also implies the semantics of the proxy-revalidate directive (see section 14.9.4), i.e., that the shared cache must not use the entry after it becomes stale to respond to a subsequent request without first revalidating it with the origin server. The s- maxage directive is always ignored by a private cache.
 * Note that most older caches, not compliant with this specification, do not implement any cache-control directives. An origin server wishing to use a cache-control directive that restricts, but does not prevent, caching by an HTTP/1.1-compliant cache MAY exploit the requirement that the max-age directive overrides the Expires header, and the fact that pre-HTTP/1.1-compliant caches do not observe the max-age directive.
 *
 * Other directives allow a user agent to modify the basic expiration mechanism. These directives MAY be specified on a request:
 *
 * max-age
 * Indicates that the client is willing to accept a response whose age is no greater than the specified time in seconds. Unless max- stale directive is also included, the client is not willing to accept a stale response.
 * min-fresh
 * Indicates that the client is willing to accept a response whose freshness lifetime is no less than its current age plus the specified time in seconds. That is, the client wants a response that will still be fresh for at least the specified number of seconds.
 * max-stale
 * Indicates that the client is willing to accept a response that has exceeded its expiration time. If max-stale is assigned a value, then the client is willing to accept a response that has exceeded its expiration time by no more than the specified number of seconds. If no value is assigned to max-stale, then the client is willing to accept a stale response of any age.
 * If a cache returns a stale response, either because of a max-stale directive on a request, or because the cache is configured to override the expiration time of a response, the cache MUST attach a Warning header to the stale response, using Warning 110 (Response is stale).
 *
 * A cache MAY be configured to return stale responses without validation, but only if this does not conflict with any "MUST"-level requirements concerning cache validation (e.g., a "must-revalidate" cache-control directive).
 *
 * If both the new request and the cached entry include "max-age" directives, then the lesser of the two values is used for determining the freshness of the cached entry for that request.
 *
 * 14.9.4 Cache Revalidation and Reload Controls
 * Sometimes a user agent might want or need to insist that a cache revalidate its cache entry with the origin server (and not just with the next cache along the path to the origin server), or to reload its cache entry from the origin server. End-to-end revalidation might be necessary if either the cache or the origin server has overestimated the expiration time of the cached response. End-to-end reload may be necessary if the cache entry has become corrupted for some reason.
 *
 * End-to-end revalidation may be requested either when the client does not have its own local cached copy, in which case we call it "unspecified end-to-end revalidation", or when the client does have a local cached copy, in which case we call it "specific end-to-end revalidation."
 *
 * The client can specify these three kinds of action using Cache- Control request directives:
 *
 * End-to-end reload
 * The request includes a "no-cache" cache-control directive or, for compatibility with HTTP/1.0 clients, "Pragma: no-cache". Field names MUST NOT be included with the no-cache directive in a request. The server MUST NOT use a cached copy when responding to such a request.
 * Specific end-to-end revalidation
 * The request includes a "max-age=0" cache-control directive, which forces each cache along the path to the origin server to revalidate its own entry, if any, with the next cache or server. The initial request includes a cache-validating conditional with the client's current validator.
 * Unspecified end-to-end revalidation
 * The request includes "max-age=0" cache-control directive, which forces each cache along the path to the origin server to revalidate its own entry, if any, with the next cache or server. The initial request does not include a cache-validating
 * conditional; the first cache along the path (if any) that holds a cache entry for this resource includes a cache-validating conditional with its current validator.
 * max-age
 * When an intermediate cache is forced, by means of a max-age=0 directive, to revalidate its own cache entry, and the client has supplied its own validator in the request, the supplied validator might differ from the validator currently stored with the cache entry. In this case, the cache MAY use either validator in making its own request without affecting semantic transparency.
 * However, the choice of validator might affect performance. The best approach is for the intermediate cache to use its own validator when making its request. If the server replies with 304 (Not Modified), then the cache can return its now validated copy to the client with a 200 (OK) response. If the server replies with a new entity and cache validator, however, the intermediate cache can compare the returned validator with the one provided in the client's request, using the strong comparison function. If the client's validator is equal to the origin server's, then the intermediate cache simply returns 304 (Not Modified). Otherwise, it returns the new entity with a 200 (OK) response.
 * If a request includes the no-cache directive, it SHOULD NOT include min-fresh, max-stale, or max-age.
 * only-if-cached
 * In some cases, such as times of extremely poor network connectivity, a client may want a cache to return only those responses that it currently has stored, and not to reload or revalidate with the origin server. To do this, the client may include the only-if-cached directive in a request. If it receives this directive, a cache SHOULD either respond using a cached entry that is consistent with the other constraints of the request, or respond with a 504 (Gateway Timeout) status. However, if a group of caches is being operated as a unified system with good internal connectivity, such a request MAY be forwarded within that group of caches.
 * must-revalidate
 * Because a cache MAY be configured to ignore a server's specified expiration time, and because a client request MAY include a max- stale directive (which has a similar effect), the protocol also includes a mechanism for the origin server to require revalidation of a cache entry on any subsequent use. When the must-revalidate directive is present in a response received by a cache, that cache MUST NOT use the entry after it becomes stale to respond to a
 * subsequent request without first revalidating it with the origin server. (I.e., the cache MUST do an end-to-end revalidation every time, if, based solely on the origin server's Expires or max-age value, the cached response is stale.)
 * The must-revalidate directive is necessary to support reliable operation for certain protocol features. In all circumstances an HTTP/1.1 cache MUST obey the must-revalidate directive; in particular, if the cache cannot reach the origin server for any reason, it MUST generate a 504 (Gateway Timeout) response.
 * Servers SHOULD send the must-revalidate directive if and only if failure to revalidate a request on the entity could result in incorrect operation, such as a silently unexecuted financial transaction. Recipients MUST NOT take any automated action that violates this directive, and MUST NOT automatically provide an unvalidated copy of the entity if revalidation fails.
 * Although this is not recommended, user agents operating under severe connectivity constraints MAY violate this directive but, if so, MUST explicitly warn the user that an unvalidated response has been provided. The warning MUST be provided on each unvalidated access, and SHOULD require explicit user confirmation.
 * proxy-revalidate
 * The proxy-revalidate directive has the same meaning as the must- revalidate directive, except that it does not apply to non-shared user agent caches. It can be used on a response to an authenticated request to permit the user's cache to store and later return the response without needing to revalidate it (since it has already been authenticated once by that user), while still requiring proxies that service many users to revalidate each time (in order to make sure that each user has been authenticated). Note that such authenticated responses also need the public cache control directive in order to allow them to be cached at all.
 * 14.9.5 No-Transform Directive
 * no-transform
 * Implementors of intermediate caches (proxies) have found it useful to convert the media type of certain entity bodies. A non- transparent proxy might, for example, convert between image formats in order to save cache space or to reduce the amount of traffic on a slow link.
 * Serious operational problems occur, however, when these transformations are applied to entity bodies intended for certain kinds of applications. For example, applications for medical
 * imaging, scientific data analysis and those using end-to-end authentication, all depend on receiving an entity body that is bit for bit identical to the original entity-body.
 * Therefore, if a message includes the no-transform directive, an intermediate cache or proxy MUST NOT change those headers that are listed in section 13.5.2 as being subject to the no-transform directive. This implies that the cache or proxy MUST NOT change any aspect of the entity-body that is specified by these headers, including the value of the entity-body itself.
 * 14.9.6 Cache Control Extensions
 * The Cache-Control header field can be extended through the use of one or more cache-extension tokens, each with an optional assigned value. Informational extensions (those which do not require a change in cache behavior) MAY be added without changing the semantics of other directives. Behavioral extensions are designed to work by acting as modifiers to the existing base of cache directives. Both the new directive and the standard directive are supplied, such that applications which do not understand the new directive will default to the behavior specified by the standard directive, and those that understand the new directive will recognize it as modifying the requirements associated with the standard directive. In this way, extensions to the cache-control directives can be made without requiring changes to the base protocol.
 *
 * This extension mechanism depends on an HTTP cache obeying all of the cache-control directives defined for its native HTTP-version, obeying certain extensions, and ignoring all directives that it does not understand.
 *
 * For example, consider a hypothetical new response directive called community which acts as a modifier to the private directive. We define this new directive to mean that, in addition to any non-shared cache, any cache which is shared only by members of the community named within its value may cache the response. An origin server wishing to allow the UCI community to use an otherwise private response in their shared cache(s) could do so by including
 *
 *        Cache-Control: private, community="UCI"
 * A cache seeing this header field will act correctly even if the cache does not understand the community cache-extension, since it will also see and understand the private directive and thus default to the safe behavior.
 *
 * Unrecognized cache-directives MUST be ignored; it is assumed that any cache-directive likely to be unrecognized by an HTTP/1.1 cache will be combined with standard directives (or the response's default cacheability) such that the cache behavior will remain minimally correct even if the cache does not understand the extension(s).
 * </pre>
 */
public final class CacheControl extends HeaderValue2<List<CacheControlDirective<?>>> {

    /**
     * Parses a header value that contains one or more directives.
     */
    public static CacheControl parse(final String text) {
        return CacheControlHeaderValueParser.parseCacheControl(text);
    }

    /**
     * Factory that creates a new {@link CacheControl}
     */
    public static CacheControl with(final List<CacheControlDirective<?>> directives) {
        return new CacheControl(checkDirectives(directives));
    }

    /**
     * Private ctor use factory
     */
    private CacheControl(final List<CacheControlDirective<?>> values) {
        super(Lists.immutable(values));
    }

    /**
     * Would be setter that returns a {@link CacheControl} with the given {@link CacheControlDirective} creating a new instance if necessary.
     */
    public CacheControl setValue(final List<CacheControlDirective<?>> directives) {
        final List<CacheControlDirective<?>> copy = checkDirectives(directives);
        return this.value.equals(copy) ?
                this :
                new CacheControl(copy);
    }

    private static List<CacheControlDirective<?>> checkDirectives(final List<CacheControlDirective<?>> directives) {
        Objects.requireNonNull(directives, "directives");

        final List<CacheControlDirective<?>> copy = Lists.immutable(directives);
        copy.forEach(v -> Objects.requireNonNull(v, "directives includes null"));
        if (copy.isEmpty()) {
            throw new IllegalArgumentException("Directives empty");
        }
        return copy;
    }

    // HeaderValue.....................................................................................................

    @Override
    public String toHeaderText() {
        return HeaderValue.toHeaderTextList(this.value, SEPARATOR);
    }

    final static String SEPARATOR = HeaderValue.SEPARATOR.string().concat(" ");

    @Override
    public boolean isWildcard() {
        return false;
    }

    @Override
    public boolean isMultipart() {
        return this.value().stream()
                .allMatch(CacheControlDirective::isMultipart);
    }

    // https://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html
    // The Cache-Control general-header field is used to specify directives that MUST be obeyed by all caching mechanisms along the request/response chain.

    @Override
    public boolean isRequest() {
        return this.value().stream()
                .allMatch(CacheControlDirective::isRequest);
    }

    @Override
    public boolean isResponse() {
        return this.value().stream()
                .allMatch(CacheControlDirective::isResponse);
    }

    @Override
    boolean canBeEqual(final Object other) {
        return other instanceof CacheControl;
    }
}
