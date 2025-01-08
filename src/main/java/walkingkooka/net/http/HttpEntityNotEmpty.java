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

import walkingkooka.Binary;
import walkingkooka.collect.map.Maps;
import walkingkooka.net.header.HttpHeaderName;
import walkingkooka.net.header.MediaType;
import walkingkooka.text.Ascii;
import walkingkooka.text.CharSequences;
import walkingkooka.text.Indentation;
import walkingkooka.text.LineEnding;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.text.printer.Printers;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

/**
 * A {@link HttpEntity} that captures the common functionalitu of {@link HttpEntityBinary} and {@link HttpEntityText}.
 */
abstract class HttpEntityNotEmpty extends HttpEntity {

    /**
     * Package private
     */
    HttpEntityNotEmpty(final Map<HttpHeaderName<?>, HttpEntityHeaderList> headers) {
        super();
        this.headers = headers;
    }

    // headers..........................................................................................................

    @Override //
    final Map<HttpHeaderName<?>, HttpEntityHeaderList> headers2() {
        return this.headers;
    }

    private final Map<HttpHeaderName<?>, HttpEntityHeaderList> headers;

    @Override //
    final <T> HttpEntity setHeader0(final HttpHeaderName<T> header,
                                    final HttpEntityHeaderList value) {
        final HttpEntityHeaderList values = this.headers2().get(header);
        return value.equals(values) ?
            this :
            this.setHeader1(header, value);
    }

    /**
     * Copy all headers into a new {@link Map} and set the new header with a single value.
     */
    private <T> HttpEntity setHeader1(final HttpHeaderName<T> header,
                                      final HttpEntityHeaderList value) {
        final Map<HttpHeaderName<?>, HttpEntityHeaderList> updated = Maps.ordered();
        updated.putAll(this.headers2());
        updated.put(header, value);

        return this.replaceHeaders(Maps.readOnly(updated));
    }

    @Override//
    final <T> HttpEntity addHeader0(final HttpHeaderName<T> header,
                                    final T value) {
        final HttpEntity added;

        final Map<HttpHeaderName<?>, HttpEntityHeaderList> headers = this.headers2();
        final HttpEntityHeaderList values = headers.get(header);
        if (null == values) {
            // add a new header
            final Map<HttpHeaderName<?>, HttpEntityHeaderList> updated = Maps.ordered();
            updated.putAll(headers);
            updated.put(header, HttpEntityHeaderList.one(header, value));

            added = this.replaceHeaders(Maps.readOnly(updated));
        } else {
            final Map<HttpHeaderName<?>, HttpEntityHeaderList> updated = Maps.ordered();
            updated.putAll(headers);

            if (values.contains(value)) {
                added = this; // already contains header+value return this;
            } else {
                // append value and return new entity
                updated.put(
                    header,
                    (HttpEntityHeaderList) values.concat(value)
                );
                added = this.replaceHeaders(Maps.readOnly(updated));
            }
        }

        return added;
    }

    @Override//
    final HttpEntity remove0(final HttpHeaderName<?> header) {
        final Map<HttpHeaderName<?>, HttpEntityHeaderList> removed = Maps.ordered();
        boolean changed = false;

        for (final Entry<HttpHeaderName<?>, HttpEntityHeaderList> headerAndValue : this.headers2().entrySet()) {
            final HttpHeaderName<?> possibleHeader = headerAndValue.getKey();
            HttpEntityHeaderList values = headerAndValue.getValue();
            if (possibleHeader.equals(header)) {
                changed = true;
                continue;
            }

            removed.put(possibleHeader, values);
        }

        return changed ?
            this.replaceHeaders(removed) :
            this;
    }

    // contentType......................................................................................................

    @Override
    public Optional<MediaType> contentType() {
        return HttpHeaderName.CONTENT_TYPE.header(this);
    }

    // setBodyText......................................................................................................

    @Override //
    final HttpEntity replaceBodyText(final String bodyText) {
        final Map<HttpHeaderName<?>, HttpEntityHeaderList> headers = this.headers2();

        return bodyText.isEmpty() && headers.isEmpty() ?
            EMPTY :
            this.bodyText().equals(bodyText) ?
                this :
                HttpEntityText.with(headers, bodyText);
    }

    // Object..........................................................................................................

    @Override
    public final String toString() {
        final StringBuilder b = new StringBuilder();

        final IndentingPrinter printer = Printers.stringBuilder(b, LineEnding.CRNL)
            .indenting(Indentation.EMPTY);
        this.printHeaders(
            this.alphaSortedHeaders(),
            printer
        );

        printer.println();

        if (this.isText()) {
            this.toStringText(b);
        } else {
            this.toStringBinary(b);
        }

        return b.toString();
    }

    private void toStringBinary(final StringBuilder b) {
        final Binary body = this.body();
        byte[] bodyBytes = body.value();

        if (null != bodyBytes && false == body.isEmpty()) {
            final int length = bodyBytes.length;
            for (int i = 0; i < length; i = i + TOSTRING_HEX_DUMP_WIDTH) {
                // offset
                b.append(CharSequences.padLeft(Integer.toHexString(i), 8, '0').toString());
                b.append(' ');

                for (int j = 0; j < TOSTRING_HEX_DUMP_WIDTH; j++) {
                    final int k = i + j;
                    if (k < length) {
                        b.append(
                            hex(
                                bodyBytes[k]
                            )
                        );
                    } else {
                        b.append("  ");
                    }

                    b.append(' ');
                }

                for (int j = 0; j < TOSTRING_HEX_DUMP_WIDTH; j++) {
                    final int k = i + j;
                    if (k < length) {
                        final char c = (char) bodyBytes[k];
                        b.append(
                            Ascii.isPrintable(c) ?
                                c :
                                UNPRINTABLE_CHAR
                        );
                    } else {
                        b.append(' ');
                    }
                }

                b.append(LineEnding.CRNL);
            }
        }
    }

    private final static int TOSTRING_HEX_DUMP_WIDTH = 16;

    private void toStringText(final StringBuilder b) {
        b.append(
            this.bodyText()
        );
    }

    // TreePrintable....................................................................................................

    @Override //
    final void printTreeBody(final IndentingPrinter printer) {
        if (this.isText()) {
            this.printTreeBodyText(printer);
        } else {
            this.printTreeBodyBinary(printer);
        }
    }

    private void printTreeBodyBinary(final IndentingPrinter printer) {
        printer.println("body");
        printer.indent();
        {
            final byte[] bodyBytes = this.body().value();
            final int length = bodyBytes.length;
            for (int i = 0; i < length; i = i + TREE_PRINT_HEX_DUMP_WIDTH) {

                // hex bytes
                for (int j = 0; j < TREE_PRINT_HEX_DUMP_WIDTH; j++) {
                    final int k = i + j;
                    if (k < length) {
                        printer.print(
                            hex(
                                bodyBytes[k]
                            )
                        );
                    } else {
                        printer.print("  ");
                    }
                    printer.print(" ");
                }

                printer.print(" ");

                // ascii chars
                for (int j = 0; j < TREE_PRINT_HEX_DUMP_WIDTH; j++) {
                    final int k = i + j;
                    if (k < length) {
                        final char c = (char) bodyBytes[k];
                        printer.print(
                            String.valueOf(
                                Ascii.isPrintable(c) ? c : UNPRINTABLE_CHAR
                            )
                        );
                    } else {
                        printer.print(" ");
                    }
                }

                printer.println();
            }
        }
        printer.outdent();
    }

    final void printTreeBodyText(final IndentingPrinter printer) {
        final String bodyText = this.bodyText();
        if (false == bodyText.isEmpty()) {
            printer.println("bodyText");
            printer.indent();
            {
                printer.println(bodyText);
            }
            printer.outdent();
        }
    }

    private final static int TREE_PRINT_HEX_DUMP_WIDTH = 20;

    final static char UNPRINTABLE_CHAR = '.';

    // printTree & toString.............................................................................................

    private static CharSequence hex(final byte value) {
        return CharSequences.padLeft(
            Integer.toHexString(0xff & value),
            2,
            '0'
        );
    }

    private boolean isText() {
        final MediaType contentType = HttpHeaderName.CONTENT_TYPE.header(this)
            .orElse(null);

        return null != contentType &&
            (MediaType.ANY_TEXT.test(contentType) ||
                MediaType.APPLICATION_JSON.test(contentType) ||
                contentType.suffix()
                    .equals(JSON_SUFFIX)
            );
    }

    private final static Optional<String> JSON_SUFFIX = Optional.of("json");
}
