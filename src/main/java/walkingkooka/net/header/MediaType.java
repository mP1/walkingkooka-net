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

import walkingkooka.Value;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.map.Maps;
import walkingkooka.net.HasQualityFactor;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.text.CharacterConstant;
import walkingkooka.text.HasCaseSensitivity;
import walkingkooka.text.Whitespace;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;


/**
 * A {@link Value} that represents a MIME Type with possible optional parameters.
 * Note parameter order is not important when comparing for equality or calculating the hash code.
 * Note any suffix that may be present in the sub type is not validated in anyway except for valid characters.
 * <a href="https://en.wikipedia.org/wiki/Media_type"></a>.
 * <br>
 * The type and sub types are case insensitive.
 * <a href="http://www.w3.org/Protocols/rfc1341/4_Content-Type.html"></a>
 * <pre>
 * The type, subtype, and parameter names are not case sensitive. For example, TEXT, Text, and TeXt are all equivalent.
 * Parameter values are normally case sensitive, but certain parameters are interpreted to be case- insensitive,
 * depending on the intended use. (For example, multipart boundaries are case-sensitive, but the "access- type" for
 * message/External-body is not case-sensitive.)
 * </pre>
 */
final public class MediaType extends HeaderWithParameters2<MediaType, MediaTypeParameterName<?>, String>
    implements
    HasCaseSensitivity,
    HasQualityFactor,
    Predicate<MediaType> {

    /**
     * The separator character that separates the type and secondary portions within a mime type {@link String}.
     */
    public final static CharacterConstant TYPE_SUBTYPE_SEPARATOR = CharacterConstant.with('/');

    /**
     * The separator character notes the start of a suffix following a sub-type.
     * <br>
     * https://datatracker.ietf.org/doc/draft-ietf-mediaman-suffixes/
     */
    public final static CharacterConstant SUFFIX_SEPARATOR = CharacterConstant.with('+');

    /**
     * No suffix.
     */
    public final static Optional<String> NO_SUFFIX = Optional.empty();

    /**
     * No parameters.
     */
    public final static Map<MediaTypeParameterName<?>, Object> NO_PARAMETERS = Maps.empty();

    // MediaType constants.................................................................................................

    private final static CaseSensitivity CASE_SENSITIVITY = CaseSensitivity.INSENSITIVE;

    /**
     * Holds all constants.
     */
    private final static Map<String, MediaType> CONSTANTS = Maps.sorted(CASE_SENSITIVITY.comparator());

    /**
     * Holds a {@link MediaType} that matches all {@link MediaType text types}.
     */
    public final static MediaType ALL = registerConstant("*/*");

    /**
     * Holds a {@link MediaType} for binary.
     */
    public final static MediaType BINARY = registerConstant("application/octet-stream");

    /**
     * Holds a {@link MediaType} that matches all {@link MediaType text types}.
     */
    public final static MediaType ANY_TEXT = registerConstant("text/*");

    /**
     * Holds a {@link MediaType} for plain text.
     */
    public final static MediaType TEXT_PLAIN = registerConstant("text/plain");

    /**
     * Holds a {@link MediaType} for text/richtext
     */
    public final static MediaType TEXT_RICHTEXT = registerConstant("text/richtext");

    /**
     * Holds a {@link MediaType} for HTML text/html
     */
    public final static MediaType TEXT_HTML = registerConstant("text/html");

    /**
     * Holds a {@link MediaType} for XML text/xml
     */
    public final static MediaType TEXT_XML = registerConstant("text/xml");

    /**
     * Holds a {@link MediaType} for binary payloads that have been base64 encoded, text/base64
     */
    public final static MediaType TEXT_BASE64 = registerConstant("text/base64");

    /**
     * Holds a {@link MediaType} for MULTIPART BYTE RANGES that contains <code>multipart/byteranges</code>.
     */
    public final static MediaType MULTIPART_BYTE_RANGES = registerConstant("multipart/byteranges");

    /**
     * Holds a {@link MediaType} for MULTIPART FORM DATA that contains <code>multipart/form-data</code>.
     */
    public final static MediaType MULTIPART_FORM_DATA = registerConstant("multipart/form-data");

    /**
     * Holds a {@link MediaType} that matches all {@link MediaType image types}.
     */
    public final static MediaType ANY_IMAGE = registerConstant("image/*");

    /**
     * Holds a {@link MediaType} that contains <code>image/bmp</code>
     */
    public final static MediaType IMAGE_BMP = registerConstant("image/bmp");

    /**
     * Holds a {@link MediaType} that contains <code>image/gif</code>
     */
    public final static MediaType IMAGE_GIF = registerConstant("image/gif");

    /**
     * Holds a {@link MediaType} that contains <code>image/jpeg</code>
     */
    public final static MediaType IMAGE_JPEG = registerConstant("image/jpeg");

    /**
     * Holds a {@link MediaType} that contains <code>image/vnd.microsoft.icon</code>
     */
    public final static MediaType IMAGE_MICROSOFT_ICON = registerConstant("image/vnd.microsoft.icon");
    /**
     * Holds a {@link MediaType} that contains <code>image/png</code>
     */
    public final static MediaType IMAGE_PNG = registerConstant("image/png");

    /**
     * Holds a {@link MediaType} that contains <code>image/text</code>
     */
    public final static MediaType IMAGE_TEXT = registerConstant("image/text");

    /**
     * Holds a {@link MediaType} that contains <code>image/x-bmp</code>
     */
    public final static MediaType IMAGE_XBMP = registerConstant("image/x-bmp");

    /**
     * Holds a {@link MediaType} that contains PDF <code>application/pdf</code>
     */
    public final static MediaType APPLICATION_PDF = registerConstant("application/pdf");

    /**
     * Holds a {@link MediaType} that contains JSON <code>application/json</code>
     */
    public final static MediaType APPLICATION_JSON = MediaType.with("application", "json");

    /**
     * Holds a {@link MediaType} that contains ZIP <code>application/zip</code>
     */
    public final static MediaType APPLICATION_ZIP = registerConstant("application/zip");

    /**
     * Holds a {@link MediaType} that contains EXCEL <code>application/ms-excel</code>
     */
    public final static MediaType APPLICATION_MICROSOFT_EXCEL = registerConstant("application/ms-excel");

    /**
     * Holds a {@link MediaType} that contains EXCEL <code>application/vnd.openxmlformats-officedocument.spreadsheetml.sheet</code>
     */
    public final static MediaType APPLICATION_MICROSOFT_EXCEL_XML = registerConstant("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

    /**
     * Holds a {@link MediaType} that contains POWERPOINT <code>application/ms-powerpoint</code>
     */
    public final static MediaType APPLICATION_MICROSOFT_POWERPOINT = registerConstant("application/ms-powerpoint");

    /**
     * Holds a {@link MediaType} that contains WORD <code>application/ms-word</code>
     */
    public final static MediaType APPLICATION_MICROSOFT_WORD = registerConstant("application/ms-word");

    /**
     * Holds a {@link MediaType} that contains OUTLOOK <code>application/vnd.ms-outlook</code>
     */
    public final static MediaType APPLICATION_MICROSOFT_OUTLOOK = registerConstant("application/vnd.ms-outlook");

    /**
     * Holds a {@link MediaType} that contains JAVASCRIPT <code>application/javascript</code>
     */
    public final static MediaType APPLICATION_JAVASCRIPT = registerConstant("application/javascript");

    /**
     * Creates and then registers the constant.
     */
    static private MediaType registerConstant(final String text) {
        final MediaType mimeType = parse(text);
        CONSTANTS.put(text, mimeType);
        return mimeType;
    }

    /**
     * Creates a {@link MediaType} breaking up the {@link String text} into type and sub types, ignoring any optional
     * parameters if they are present.
     */
    public static MediaType parse(final String text) {
        return MediaTypeOneHeaderParser.parseMediaType(
            checkText(text)
        );
    }

    /**
     * Creates a charsets of {@link MediaType}. If the text contains a single header type the results of this will be
     * identical to {@link #parse(String)} except the result will be in a charsets, which will also be
     * sorted by the q factor of each header type.
     */
    public static List<MediaType> parseList(final String text) {
        return MediaTypeListHeaderParser.parseMediaTypeList(
            checkText(text)
        );
    }

    private static String checkText(final String text) {
        return Whitespace.failIfNullOrEmptyOrWhitespace(text, "text");
    }

    /**
     * Creates a {@link MediaType} using the already broken type and sub types. It is not possible to pass parameters with or without values.
     */
    public static MediaType with(final String type, final String subType) {
        checkType(type);
        checkSubType(subType);

        return withParameters(
            type,
            subType,
            NO_SUFFIX,
            NO_PARAMETERS
        );
    }

    /**
     * Factory method called by various setters and parsers, that tries to match constants before creating an actual new
     * instance.
     */
    static MediaType withParameters(final String type,
                                    final String subType,
                                    final Optional<String> suffix,
                                    final Map<MediaTypeParameterName<?>, Object> parameters) {
        final MediaType result = parameters.isEmpty() && false == suffix.isPresent() ?
            CONSTANTS.get(type + TYPE_SUBTYPE_SEPARATOR.character() + subType) :
            null;
        return null != result ?
            result :
            new MediaType(
                type,
                subType,
                suffix,
                parameters
            );
    }

    // ctor ............................................................................................................

    /**
     * Private constructor
     */
    private MediaType(final String type,
                      final String subType,
                      final Optional<String> suffix,
                      final Map<MediaTypeParameterName<?>, Object> parameters) {
        super(
            // note non empty parameters will be appended in super-class
            type +
                TYPE_SUBTYPE_SEPARATOR.character() +
                subType +
                suffix.map(s -> SUFFIX_SEPARATOR + s).orElse(""),
            parameters
        );

        this.type = type;
        this.subType = subType;
        this.suffix = suffix;
    }

    // type ............................................................................................................

    /**
     * Getter that returns the primary component.
     */
    public String type() {
        return this.type;
    }

    /**
     * Would be setter that returns an instance with the new type, creating a new instance if required.
     */
    public MediaType setType(final String type) {
        checkType(type);
        return CASE_SENSITIVITY.equals(this.type, type) ?
            this :
            this.replace(
                type,
                this.subType,
                this.suffix,
                this.parameters
            );
    }

    private final String type;

    private static String checkType(final String type) {
        return MediaTypeHeaderParser.TYPE.failIfNullOrEmptyOrFalse(
            "type",
            type
        );
    }

    // sub type ........................................................................................................

    /**
     * Getter that returns the sub component.
     */
    public String subType() {
        return this.subType;
    }

    /**
     * Would be setter that returns an instance with the new subType, creating a new instance if required.
     */
    public MediaType setSubType(final String subType) {
        checkSubType(subType);
        return CASE_SENSITIVITY.equals(
            this.subType,
            subType
        ) ?
            this :
            this.replace(
                this.type,
                subType,
                this.suffix,
                this.parameters
            );
    }

    private final String subType;

    private static String checkSubType(final String subType) {
        return MediaTypeHeaderParser.SUB_TYPE.failIfNullOrEmptyOrFalse(
            "subType",
            subType
        );
    }

    // suffix...........................................................................................................

    /**
     * Suffix are encoded within the sub-mime-type
     */
    public Optional<String> suffix() {
        return this.suffix;
    }

    /**
     * Would be setter that returns a {@link MediaType} with the given suffix creating a new instance if necessary.
     */
    public MediaType setSuffix(final Optional<String> suffix) {
        checkSuffix(suffix);

        final MediaType mediaType;
        if (this.suffix.equals(suffix)) {
            mediaType = this;
        } else {
            mediaType = this.replace(
                this.type,
                this.subType,
                suffix,
                this.parameters
            );
        }

        return mediaType;
    }

    // https://datatracker.ietf.org/doc/draft-ietf-mediaman-suffixes/
    //
    // A structured suffix is defined as all of the characters to the right
    //   of the left-most "+" sign in a media type, including the left-most
    //   "+" sign itself.  The structured suffix MUST NOT contain more than
    //
    //Sporny & Guy            Expires 21 December 2024                [Page 2]
    //Internet-Draft             Media Type Suffixes                 June 2024
    //
    //   one "+" sign.  As an example, given the "application/foo+bar" media
    //   type: "application" is the top-level type, "foo" is the base subtype
    //   name, and "+bar" is the structured suffix.  A media type such as
    //   "application/foo+bar+baz" is not allowed.
    private final Optional<String> suffix;

    private static Optional<String> checkSuffix(final Optional<String> suffix) {
        Objects.requireNonNull(suffix, "suffix");

        if (suffix.isPresent()) {
            MediaTypeHeaderParser.SUFFIX.failIfNullOrEmptyOrFalse(
                "suffix",
                suffix.get()
            );
        }

        return suffix;
    }

    /**
     * Returns true if this {@link MediaType} is text. Think of this as a guess rather than precise.
     * <br>
     * Currently this returns true if {@link #type} is <code>text</code> or the {@link #subType} is <code>json</code>.
     */
    public boolean isText() {
        return CASE_SENSITIVITY.equals(this.type, "text") ||
            CASE_SENSITIVITY.equals(this.subType, "json");
    }

    // HasCaseSensitivity ..............................................................................................

    @Override
    public CaseSensitivity caseSensitivity() {
        return CASE_SENSITIVITY;
    }

    // parameters ......................................................................................................

    // boundary.........................................................................................................

    public Optional<MediaTypeBoundary> boundary() {
        return MediaTypeParameterName.BOUNDARY.parameterValue(this);
    }

    public MediaType setBoundary(final MediaTypeBoundary boundary) {
        Objects.requireNonNull(boundary, "boundary");

        return this.setParameter(
            MediaTypeParameterName.BOUNDARY,
            boundary
        );
    }

    // setCharset ......................................................................................................

    /**
     * Returns a {@link MediaType} with the given charset creating a new instance if necessary.
     */
    public MediaType setCharset(final CharsetName charset) {
        Objects.requireNonNull(charset, "charset");

        return this.setParameter(
            MediaTypeParameterName.CHARSET,
            charset
        );
    }

    /**
     * Retrieves a charset from this {@link MediaType} provided its supported by the runtime or uses the default.
     * Unsupported charsets are ignored.
     */
    public Charset acceptCharset(final Charset defaultCharset) {
        Objects.requireNonNull(defaultCharset, "defaultCharset");

        return MediaTypeParameterName.CHARSET.parameterValue(this)
            .flatMap(CharsetName::charset)
            .orElse(defaultCharset);
    }

    /**
     * Retrieves a charset from this {@link MediaType} requiring that if present that it is supported.
     * Unsupported charsets will result in an {@link IllegalArgumentException}, if absent the default will be returned.
     */
    public Charset contentTypeCharset(final Charset defaultCharset) {
        Objects.requireNonNull(defaultCharset, "defaultCharset");

        return MediaTypeParameterName.CHARSET.parameterValue(this)
            .map(CharsetName::charsetFailNotSupported)
            .orElse(defaultCharset);
    }

    /**
     * Retrieves the quality factor for this value.
     */
    @Override
    public Optional<Float> qualityFactor() {
        return this.qualityFactor(MediaTypeParameterName.Q);
    }

    /**
     * Returns a {@link MediaType} with no parameters. This is useful when building a message about an invalid media type.
     */
    public MediaType clearParameters() {
        return this.parameters.isEmpty() ?
            this :
            with(
                this.type,
                this.subType
            );
    }

    <T> MediaType setParameter(final MediaTypeParameterName<T> name,
                               final T value) {
        final MediaType after;

        final Map<MediaTypeParameterName<?>, Object> parameters = this.parameters;
        if (value.equals(parameters.get(name))) {
            after = this;
        } else {
            final Map<MediaTypeParameterName<?>, Object> newParameters = Maps.ordered();
            newParameters.putAll(parameters);
            newParameters.put(name, value);

            after = this.replace(
                this.type,
                this.subType,
                this.suffix,
                Maps.readOnly(newParameters)
            );
        }

        return after;
    }

    // replaceParameters ...............................................................................................

    @Override
    MediaType replaceParameters(final Map<MediaTypeParameterName<?>, Object> parameters) {
        return this.replace(
            this.type,
            this.subType,
            this.suffix,
            parameters
        );
    }

    // replace..........................................................................................................

    private MediaType replace(final String type,
                              final String subType,
                              final Optional<String> suffix,
                              final Map<MediaTypeParameterName<?>, Object> parameters) {
        return withParameters(
            type,
            subType,
            suffix,
            parameters
        );
    }

    // Predicate........................................................................................................

    /**
     * Tests if the given {@link MediaType} is compatible and understand wildcards that may appear in the type or sub type components. The
     * {@link MediaType#ALL} will of course be compatible with any other {@link MediaType}.
     */
    @Override
    public boolean test(final MediaType mediaType) {
        return null != mediaType &&
            testComponent(this.type, mediaType.type) &&
            testComponent(this.subType, mediaType.subType);
    }

    private static boolean testComponent(final String component,
                                         final String otherComponent) {
        return WILDCARD.string().equals(component) ||
            CASE_SENSITIVITY.equals(component, otherComponent);
    }

    /**
     * Tests if the given {@link MediaType} throwing a {@link IllegalArgumentException} if the test fails.
     * This useful for handlers wishing to verify the given content-type is compatible with the required type.
     */
    public void requireContentType(final MediaType mediaType) {
        if (null == mediaType) {
            throw new IllegalArgumentException(
                this.requireContentTypeMissingMessage()
            );
        }
        if (false == this.test(mediaType)) {
            // Content-Type: Got xxx/yyy require aaa/bbb
            throw new IllegalArgumentException(
                this.requireContentTypeIncompatibleMessage(mediaType)
            );
        }
    }

    /**
     * The message that will be thrown by {@link #requireContentType(MediaType)} when the {@link MediaType} is null.
     */
    public String requireContentTypeMissingMessage() {
        return HttpHeaderName.CONTENT_TYPE + ": Missing required " + this.clearParameters();
    }

    /**
     * The message thrown by {@link #requireContentType(MediaType)} when the given {@link MediaType} is incompatible,
     * eg giving a text/plain when requiring an image/*.
     */
    public String requireContentTypeIncompatibleMessage(final MediaType mediaType) {
        Objects.requireNonNull(mediaType, "mediaType");

        return HttpHeaderName.CONTENT_TYPE +
            ": Got " +
            mediaType.clearParameters() +
            " require " +
            this.clearParameters();
    }

    // Header...........................................................................................................

    // mime-type COLON sub-mime-type, suffix already included in sub-mime-type
    @Override
    String toHeaderTextValue() {
        return this.value;
    }

    @Override
    String toHeaderTextParameterSeparator() {
        return TO_HEADERTEXT_PARAMETER_SEPARATOR;
    }

    @Override
    public boolean isWildcard() {
        return ALL == this;
    }

    /**
     * Returns an {@link Accept} with this {@link MediaType}.
     */
    public Accept accept() {
        return Accept.with(Lists.of(this));
    }

    // HasHeaderScope ..................................................................................................

    final static boolean IS_MULTIPART = true;

    @Override
    public boolean isMultipart() {
        return IS_MULTIPART;
    }

    final static boolean IS_REQUEST = true;

    @Override
    public boolean isRequest() {
        return IS_REQUEST;
    }

    final static boolean IS_RESPONSE = true;

    @Override
    public boolean isResponse() {
        return IS_RESPONSE;
    }

    // isNonStandard....................................................................................................

    /**
     * https://en.wikipedia.org/wiki/Media_type#Standards_tree
     * <br>
     * Media types that have been widely deployed (with a subtype prefixed with x- or X-) without being registered, should be, if possible, re-registered with a proper prefixed subtype. If this is not possible, the media type can, after an approval by both the media types reviewer and the IESG, be registered in the standards tree with its unprefixed subtype. application/x-www-form-urlencoded is an example of a widely deployed type that ended up registered with the x- prefix.[9]
     * <br>
     * https://www.nih-cfde.org/resource/intro-mime-type/
     * <br>
     * The x- prefix of a MIME subtype-identifier implies that it is non-standard i.e. not registered with IANA.
     * <br>
     * Returns true of the sub-type begins with x-.
     */
    public boolean isNonStandard() {
        return CASE_SENSITIVITY.startsWith(
            this.subType(),
            "x-"
        );
    }

    // isVendorSpecific.................................................................................................

    /**
     * https://www.nih-cfde.org/resource/intro-mime-type/
     * <br>
     * Returns true of the sub-type begins with vnd.
     */
    public boolean isVendorSpecific() {
        return CASE_SENSITIVITY.startsWith(
            this.subType(),
            "vnd."
        );
    }

    // Object...........................................................................................................

    @Override
    int hashCodeValue(final String value) {
        return CASE_SENSITIVITY.hash(value);
    }

    @Override
    boolean canBeEquals(final Object other) {
        return other instanceof MediaType;
    }

    @Override
    boolean equalsValue(final String value,
                        final String otherValue) {
        return CASE_SENSITIVITY.equals(
            this.value,
            otherValue
        );
    }
}
