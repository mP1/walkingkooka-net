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

import org.junit.jupiter.api.Test;
import walkingkooka.collect.list.Lists;
import walkingkooka.reflect.JavaVisibility;

import java.util.List;

public final class TextUrlVisitorTest implements TextUrlVisitorTesting<TextUrlVisitor> {

    @Test
    public void testAcceptInvalidUrl() {
        final String url = "https://";

        new TestTestUrlVisitor()
            .acceptAndCheck(
                url,
                INVALID_URL_TEXT + url
            );
    }

    @Test
    public void testAcceptText() {
        final String text = "123";

        new TestTestUrlVisitor()
            .acceptAndCheck(
                text,
                text
            );
    }

    @Test
    public void testAcceptTextAbsoluteUrl() {
        final String text = "123 ";
        final String url = "https://example.com";

        new TestTestUrlVisitor()
            .acceptAndCheck(
                text + url,
                text,
                Url.parseAbsolute(url)
            );
    }

    @Test
    public void testAcceptAbsoluteUrl() {
        final String url = "https://example.com";

        new TestTestUrlVisitor()
            .acceptAndCheck(
                url,
                Url.parseAbsolute(url)
            );
    }

    @Test
    public void testAcceptAbsoluteUrlText() {
        final String url = "https://example.com";
        final String text = " 456";

        new TestTestUrlVisitor()
            .acceptAndCheck(
                url + text,
                Url.parseAbsolute(url),
                text
            );
    }

    @Test
    public void testAcceptTextDataUrl() {
        final String text = "123 ";
        final String url = "data:text/plain;base64,YWJjMTIz";

        new TestTestUrlVisitor()
            .acceptAndCheck(
                text + url,
                text,
                Url.parseData(url)
            );
    }

    @Test
    public void testAcceptDataUrl() {
        final String url = "data:text/plain;base64,YWJjMTIz";

        new TestTestUrlVisitor()
            .acceptAndCheck(
                url,
                Url.parseData(url)
            );
    }

    @Test
    public void testAcceptDataUrlText() {
        final String url = "data:text/plain;base64,YWJjMTIz";
        final String text = " 456";

        new TestTestUrlVisitor()
            .acceptAndCheck(
                url + text,
                Url.parseData(url),
                text
            );
    }

    @Test
    public void testAcceptMailtoUrl() {
        final String url = "mailto:user@example.com";

        new TestTestUrlVisitor()
            .acceptAndCheck(
                url,
                Url.parseMailTo(url)
            );
    }

    @Test
    public void testAcceptTextMailtoUrl() {
        final String text = "123 ";
        final String url = "mailto:user@example.com";

        new TestTestUrlVisitor()
            .acceptAndCheck(
                text + url,
                text,
                Url.parseMailTo(url)
            );
    }

    @Test
    public void testAcceptMailtoUrlText() {
        final String url = "mailto:user@example.com";
        final String text = " 456";

        new TestTestUrlVisitor()
            .acceptAndCheck(
                url + text,
                Url.parseMailTo(url),
                text
            );
    }

    @Test
    public void testAcceptRelativeUrl() {
        final String url = "/path123";

        new TestTestUrlVisitor()
            .acceptAndCheck(
                url,
                Url.parseRelative(url)
            );
    }

    @Test
    public void testAcceptTextRelativeUrl() {
        final String text = "123 ";
        final String url = "/path123";

        new TestTestUrlVisitor()
            .acceptAndCheck(
                text + url,
                text,
                Url.parseRelative(url)
            );
    }

    @Test
    public void testAcceptRelativeUrlText() {
        final String url = "/path123";
        final String text = " 456";

        new TestTestUrlVisitor()
            .acceptAndCheck(
                url + text,
                Url.parseRelative(url),
                text
            );
    }

    @Test
    public void testAcceptAbsoluteTextDataTextMailToTextRelativeUrl() {
        final String absoluteUrl = "https://example.com";
        final String text = " 111 ";
        final String data = "data:text/plain;base64,YWJjMTIz";
        final String text2 = " 222 ";
        final String mailTo = "mailto:user@example.com";
        final String text3 = " 333 ";
        final String relative = "/path123";

        new TestTestUrlVisitor()
            .acceptAndCheck(
                absoluteUrl + text + data + text2 + mailTo + text3 + relative,
                Url.parseAbsolute(absoluteUrl),
                text,
                Url.parseData(data),
                text2,
                Url.parseMailTo(mailTo),
                text3,
                Url.parseRelative(relative)
            );
    }

    @Test
    public void testAcceptAbsoluteTextDataTextMailToTextRelativeUrlText() {
        final String absoluteUrl = "https://example.com";
        final String text = " 111 ";
        final String data = "data:text/plain;base64,YWJjMTIz";
        final String text2 = " 222 ";
        final String mailTo = "mailto:user@example.com";
        final String text3 = " 333 ";
        final String relative = "/path123";
        final String text4 = " 444";

        new TestTestUrlVisitor()
            .acceptAndCheck(
                absoluteUrl + text + data + text2 + mailTo + text3 + relative + text4,
                Url.parseAbsolute(absoluteUrl),
                text,
                Url.parseData(data),
                text2,
                Url.parseMailTo(mailTo),
                text3,
                Url.parseRelative(relative),
                text4
            );
    }

    @Test
    public void testAcceptTextInvalidUrlTextAbsolute() {
        final String text = "123 ";
        final String invalid = "https://";
        final String text2 = " 456 ";
        final String absoluteUrl = "https://example.com";

        new TestTestUrlVisitor()
            .acceptAndCheck(
                text + invalid + text2 + absoluteUrl,
                text,
                INVALID_URL_TEXT + invalid,
                text2,
                Url.parseAbsolute(absoluteUrl)
            );
    }

    final static String INVALID_URL_TEXT = "INVALID URL TEXT ";

    final class TestTestUrlVisitor extends TextUrlVisitor {

        private TestTestUrlVisitor() {
            super();
        }

        void acceptAndCheck(final String text,
                            final Object... expected) {
            this.accept(text);

            final List<Object> expectedList = Lists.array();
            for (final Object token : expected) {
                if (false == token.toString().contains(INVALID_URL_TEXT)) {
                    expectedList.add(token.getClass().getSimpleName());
                }
                expectedList.add(token);
            }

            checkEquals(
                expectedList,
                this.tokens
            );
        }

        @Override
        protected void visit(final AbsoluteUrl url) {
            this.add(url);
        }

        @Override
        protected void visit(final DataUrl url) {
            this.add(url);
        }

        @Override
        protected void visit(final MailToUrl url) {
            this.add(url);
        }

        @Override
        protected void visit(final RelativeUrl url) {
            this.add(url);
        }

        @Override
        protected void visitText(final String text) {
            this.add(text);
        }

        @Override
        protected void visitInvalidUrlText(final String text) {
            this.tokens.add(INVALID_URL_TEXT + text);
        }

        private void add(final Object token) {
            this.tokens.add(token.getClass().getSimpleName());
            this.tokens.add(token);
        }

        private final List<Object> tokens = Lists.array();
    }

    // TextUrlVisitor...................................................................................................

    @Override
    public TextUrlVisitor createVisitor() {
        return new TextUrlVisitor() {

            @Override
            public String toString() {
                return "TextUrlVisitor";
            }
        };
    }

    @Override
    public void testCheckToStringOverridden() {
        throw new UnsupportedOperationException();
    }

    // class............................................................................................................

    @Override
    public Class<TextUrlVisitor> type() {
        return TextUrlVisitor.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
