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

import walkingkooka.collect.map.Maps;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Represents a content disposition header and its component values.<br>
 * <a href="https://en.wikipedia.org/wiki/MIME#Content-Disposition"></a>
 */
public final class ContentDisposition extends HeaderWithParameters2<ContentDisposition,
    ContentDispositionParameterName<?>,
    ContentDispositionType> {
    /**
     * A constants with no parameters.
     */
    public final static Map<ContentDispositionParameterName<?>, Object> NO_PARAMETERS = Maps.empty();

    /**
     * Parses a header value into tokens, which aill also be sorted using their q (quality factor).
     * <pre>
     * Content-Disposition: inline
     * Content-Disposition: attachment
     * Content-Disposition: attachment; filename="filename.jpg"
     * ...
     * Content-Disposition: form-data
     * Content-Disposition: form-data; name="fieldName"
     * Content-Disposition: form-data; name="fieldName"; filename="filename.jpg"
     * </pre>
     */
    public static ContentDisposition parse(final String text) {
        return ContentDispositionHeaderParser.parseContentDisposition(text);
    }

    /**
     * Factory that creates a new {@link ContentDisposition}
     */
    public static ContentDisposition with(final ContentDispositionType type) {
        checkType(type);

        return new ContentDisposition(type, NO_PARAMETERS);
    }

    /**
     * Private ctor use factory
     */
    private ContentDisposition(final ContentDispositionType type, final Map<ContentDispositionParameterName<?>, Object> parameters) {
        super(type, parameters);
    }

    // type .....................................................

    /**
     * Getter that retrieves the type
     */
    public ContentDispositionType type() {
        return this.value();
    }

    /**
     * Would be setter that returns a {@link ContentDisposition} with the given type creating a new instance if necessary.
     */
    public ContentDisposition setType(final ContentDispositionType type) {
        checkType(type);
        return this.value.equals(type) ?
            this :
            this.replace(type, this.parameters);
    }

    private static void checkType(final ContentDispositionType type) {
        Objects.requireNonNull(type, "type");
    }

    // replaceParameters ..................................................................................................

    @Override
    ContentDisposition replaceParameters(final Map<ContentDispositionParameterName<?>, Object> parameters) {
        return this.replace(this.value, parameters);
    }

    private ContentDisposition replace(final ContentDispositionType type, final Map<ContentDispositionParameterName<?>, Object> parameters) {
        return new ContentDisposition(type, parameters);
    }

    // filename.........................................................................................................

    /**
     * Getter that returns any filename that is present preferring to return FILENAME* before FILENAME>
     * <br>
     * https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Content-Disposition
     * <pre>
     * The parameters filename and filename* differ only in that filename* uses the encoding defined in RFC 5987.
     * When both filename and filename* are present in a single header field value, filename* is preferred over filename
     * when both are understood. It's recommended to include both for maximum compatibility, and you can convert filename*
     * to filename by substituting non-ASCII characters with ASCII equivalents (such as converting é to e).
     * You may want to avoid percent escape sequences in filename, because they are handled inconsistently across browsers.
     * (Firefox and Chrome decode them, while Safari does not.)
     * </pre>
     */
    public Optional<ContentDispositionFileName> filename() {
        Optional<ContentDispositionFileName> filename = Optional.empty();

        final ContentDispositionType type = this.type();
        if (type.isAttachment() || type.isFormData()) {
            filename = ContentDispositionParameterName.FILENAME_STAR.parameterValue(this);

            if (false == filename.isPresent()) {
                filename = ContentDispositionParameterName.FILENAME.parameterValue(this);
            }
        }

        return filename;
    }

    // Header.................................................................

    @Override
    String toHeaderTextValue() {
        return this.value.toString();
    }

    @Override
    String toHeaderTextParameterSeparator() {
        return TO_HEADERTEXT_PARAMETER_SEPARATOR;
    }

    @Override
    public boolean isWildcard() {
        return false;
    }

    // HasHeaderScope ....................................................................................................

    @Override
    public boolean isMultipart() {
        return true;
    }

    @Override
    public boolean isRequest() {
        return true;
    }

    @Override
    public boolean isResponse() {
        return true;
    }

    // Object ..........................................................................................................

    @Override
    int hashCodeValue(final ContentDispositionType value) {
        return value.hashCode();
    }

    @Override
    boolean equalsValue(final ContentDispositionType value,
                        final ContentDispositionType otherValue) {
        return value.equals(otherValue);
    }

    @Override
    boolean canBeEquals(final Object other) {
        return other instanceof ContentDisposition;
    }
}
