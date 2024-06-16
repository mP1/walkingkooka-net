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

package walkingkooka.net;

import walkingkooka.Binary;
import walkingkooka.Cast;
import walkingkooka.net.header.MediaType;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.text.CharSequences;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;
import java.util.Optional;

/**
 * The content of a data url.
 * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Basics_of_HTTP/Data_URIs"></a>
 * <pre>
 * Data URLs are composed of four parts: a prefix (data:), a MIME type indicating the type of data, an optional base64 token if non-textual, and the data itself:
 *
 * data:[<mediatype>][;base64],<data>
 * The mediatype is a MIME type string, such as 'image/jpeg' for a JPEG image file. If omitted, defaults to text/plain;charset=US-ASCII
 *
 * If the data is textual, you can simply embed the text (using the appropriate entities or escapes based on the enclosing document's type). Otherwise, you can specify base64 to embed base64-encoded binary data.
 *
 * A few examples:
 *
 * data:,Hello%2C%20World!
 * Simple text/plain data
 * data:text/plain;base64,SGVsbG8sIFdvcmxkIQ%3D%3D
 * base64-encoded version of the above
 * data:text/html,%3Ch1%3EHello%2C%20World!%3C%2Fh1%3E
 * An HTML document with <h1>Hello, World!</h1>
 * data:text/html,<script>alert('hi');</script>
 * An HTML document that executes a JavaScript alert. Note that the closing script tag is required.
 * </pre>
 */
public final class DataUrl extends Url {

    final static String SCHEME = "data:";

    /**
     * <pre>
     * SyntaxSection
     * Data URLs are composed of four parts: a prefix (data:), a MIME type indicating the type of data, an optional base64 token if non-textual, and the data itself:
     *
     * data:[<mediatype>][;base64],<data>
     * </pre>
     */
    static DataUrl parseData0(final String url) {
        CharSequences.failIfNullOrEmpty(url, "url");

        if (!CaseSensitivity.INSENSITIVE.startsWith(url, SCHEME)) {
            throw new IllegalArgumentException("Url missing data: =" + CharSequences.quoteAndEscape(url));
        }

        final int comma = url.indexOf(',');
        if (-1 == comma) {
            throw new IllegalArgumentException("Url missing ',' data start in " + CharSequences.quoteAndEscape(url));
        }

        final int semi = url.indexOf(';');

        // mime type
        final String mimeTypeString = url.substring(
                "data:".length(),
                -1 == semi || semi > comma ?
                        comma :
                        semi
        );
        final Optional<MediaType> mediaType =
                Optional.ofNullable(
                        mimeTypeString.isEmpty() ?
                                null :
                                complainIfWithParameters(
                                        MediaType.parse(mimeTypeString)
                                )
                );

        final String encoding;
        if (-1 != semi && semi < comma) {
            encoding = url.substring(
                    semi + 1,
                    comma
            );
        } else {
            encoding = "";
        }

        final String encodedData = url.substring(comma + 1);
        final boolean base64;
        final byte[] binary;

        switch (encoding) {
            case "":
                base64 = false;
                binary = URLDecoder.decode(encodedData)
                        .getBytes(StandardCharsets.UTF_8);
                break;
            case "base64":
                base64 = true;
                binary = Base64.getDecoder()
                        .decode(encodedData);
                break;
            default:
                throw new IllegalArgumentException("Got unknown encoding " + CharSequences.quoteAndEscape(encoding) + " in " + CharSequences.quoteAndEscape(url));
        }

        return new DataUrl(
                mediaType,
                base64,
                Binary.with(binary)
        );
    }

    /**
     * Creates a data url.
     */
    public static DataUrl with(final Optional<MediaType> mediaType,
                               final boolean base64,
                               final Binary binary) {
        Objects.requireNonNull(mediaType, "mediaType");
        mediaType.ifPresent(DataUrl::complainIfWithParameters);

        Objects.requireNonNull(binary, "binary");

        return new DataUrl(
                mediaType,
                base64,
                binary
        );
    }

    private static MediaType complainIfWithParameters(final MediaType mediaType) {
        if (mediaType.parameters().size() > 0) {
            throw new IllegalArgumentException("Media type must not have parameters: " + mediaType);
        }
        return mediaType;
    }

    private DataUrl(final Optional<MediaType> mediaType,
                    final boolean base64,
                    final Binary binary) {
        super();
        this.mediaType = mediaType;
        this.base64 = base64;
        this.binary = binary;
    }

    public Optional<MediaType> mediaType() {
        return this.mediaType;
    }

    private final Optional<MediaType> mediaType;

    /**
     * Builds a data {@link Url}
     * <pre>
     * data:text/plain;base64,SGVsbG8sIFdvcmxkIQ%3D%3D
     * </pre>
     */
    @Override
    public String value() {
        final StringBuilder b = new StringBuilder()
                .append(SCHEME)
                .append(
                        this.mediaType.map(MediaType::toString)
                                .orElse("")
                );

        final byte[] binary = this.binary()
                .value();
        if (this.base64) {
            b.append(";base64,")
                    .append(
                            Base64.getEncoder()
                                    .encodeToString(binary)
                    );
        } else {
            b.append(',')
                    .append(
                            URLEncoder.encode(
                                    new String(
                                            binary,
                                            StandardCharsets.UTF_8
                                    )
                            )
                    );
        }

        return b.toString();
    }

    /**
     * Returns true if the value should be base64 encoded.
     */
    public boolean isBase64() {
        return this.base64;
    }

    private boolean base64;

    /**
     * Getter that returns the data as {@link Binary}.
     */
    public Binary binary() {
        return this.binary;
    }

    private final Binary binary;

    // UrlVisitor........................................................................................................

    @Override
    void accept(final UrlVisitor visitor) {
        visitor.visit(this);
    }

    // Object...........................................................................................................

    public int hashCode() {
        return Objects.hash(
                this.mediaType,
                this.base64,
                this.binary
        );
    }

    public boolean equals(final Object other) {
        return this == other ||
                other instanceof DataUrl && this.equals0(Cast.to(other));
    }

    private boolean equals0(final DataUrl other) {
        return this.mediaType.equals(other.mediaType) &&
                this.base64 == other.base64 &&
                this.binary.equals(other.binary);
    }

    @Override
    public String toString() {
        return this.value();
    }
}
