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

import walkingkooka.NeverError;
import walkingkooka.visit.Visiting;
import walkingkooka.visit.Visitor;

import java.util.Objects;
import java.util.function.Function;

/**
 * A visitor which accepts text that may contain a mixture of text and embedded {@link Url}, reporting each in sequence
 * to this {@link TextUrlVisitor}.
 */
public abstract class TextUrlVisitor extends Visitor<String> {

    protected TextUrlVisitor() {
        super();
    }

    @Override
    public final void accept(final String text) {
        Objects.requireNonNull(text, "text");

        final int textLength = text.length();

        final int MODE_MAYBE_URL = 0;
        final int MODE_URL = 1;
        final int MODE_TEXT = 2;

        int mode = MODE_MAYBE_URL;

        final StringBuilder b = new StringBuilder();
        Function<String, Url> urlParser = null;

        for (int i = 0; i < textLength; i++) {
            final char c = text.charAt(i);

            switch (mode) {
                case MODE_MAYBE_URL:
                    // data://
                    // http://
                    // https://
                    // mailto://
                    // /
                    if (text.startsWith("http://", i) || text.startsWith("https://", i)) {
                        mode = MODE_URL;
                        urlParser = Url::parseAbsolute;
                    } else {
                        if (text.startsWith("data:", i)) {
                            mode = MODE_URL;
                            urlParser = Url::parseData;
                        } else {
                            if (text.startsWith("mailto:", i)) {
                                mode = MODE_URL;
                                urlParser = Url::parseMailTo;
                            } else {
                                if (text.startsWith("/", i)) {
                                    mode = MODE_URL;
                                    urlParser = Url::parseRelative;
                                } else {
                                    mode = MODE_TEXT;
                                }
                            }
                        }
                    }
                    b.append(c); // consume one more text character
                    break;
                case MODE_TEXT:
                    b.append(c);
                    if (Character.isWhitespace(c)) {
                        this.acceptText(b);

                        mode = MODE_MAYBE_URL;
                        b.setLength(0);
                    }
                    break;
                case MODE_URL:
                    if (Character.isWhitespace(c)) {
                        this.acceptUrl(
                            b,
                            urlParser
                        );

                        b.setLength(0);
                        b.append(c); // first character of text
                        mode = MODE_TEXT;
                    } else {
                        b.append(c); // continue consuming URL
                    }
                    break;
                default:
                    NeverError.unhandledCase(
                        mode,
                        MODE_MAYBE_URL,
                        MODE_TEXT,
                        MODE_URL
                    );
            }
        }

        // handle unterminated case
        switch (mode) {
            case MODE_MAYBE_URL:
                this.acceptUrl(
                    b,
                    urlParser
                );
                break;
            case MODE_TEXT:
                this.acceptText(b);
                break;
            case MODE_URL:
                this.acceptUrl(
                    b,
                    urlParser
                );
                break;
            default:
                NeverError.unhandledCase(
                    mode,
                    MODE_MAYBE_URL,
                    MODE_TEXT,
                    MODE_URL
                );
        }
    }

    private void acceptUrl(final StringBuilder b,
                           final Function<String, Url> urlParser) {
        if (b.length() > 0) {
            final String text = b.toString();
            try {
                final Url url = urlParser.apply(text);
                if (Visiting.CONTINUE == this.startVisit(url)) {
                    url.accept(this);
                }
                this.endVisit(url);

            } catch (final IllegalArgumentException cause) {
                this.visitInvalidUrlText(text);
            }
        }
    }

    protected Visiting startVisit(final Url url) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final Url Url) {
        // nop
    }

    protected void visit(final AbsoluteUrl url) {
        // nop
    }

    protected void visit(final DataUrl url) {
        // nop
    }

    protected void visit(final MailToUrl url) {
        // nop
    }

    protected void visit(final RelativeUrl url) {
        // nop
    }

    private void acceptText(final StringBuilder b) {
        if (b.length() > 0) {
            this.visitText(
                b.toString()
            );
        }
    }

    /**
     * Receives a segment of text that does not contain any of the supported {@link Url}.
     */
    protected void visitText(final String text) {
        // nop
    }

    /**
     * Receives a segment of text that appears to be a {@link Url}, but {@link Url#parse(String)} failed.
     */
    protected void visitInvalidUrlText(final String text) {
        // nop
    }
}
