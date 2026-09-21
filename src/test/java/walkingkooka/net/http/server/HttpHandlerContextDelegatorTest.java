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

import walkingkooka.logging.LoggerPath;
import walkingkooka.logging.LoggingLevel;
import walkingkooka.net.http.server.HttpHandlerContextDelegatorTest.TestHttpHandlerContextDelegator;

import java.util.Objects;

public final class HttpHandlerContextDelegatorTest implements HttpHandlerContextTesting2<TestHttpHandlerContextDelegator> {

    @Override
    public TestHttpHandlerContextDelegator createContext() {
        return new TestHttpHandlerContextDelegator();
    }

    @Override
    public Class<TestHttpHandlerContextDelegator> type() {
        return TestHttpHandlerContextDelegator.class;
    }

    @Override
    public void testTestNaming() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testTypeNaming() {
        throw new UnsupportedOperationException();
    }

    final static class TestHttpHandlerContextDelegator implements HttpHandlerContextDelegator {
        @Override
        public HttpHandlerContext httpHandlerContext() {
            return new FakeHttpHandlerContext() {
                @Override
                public void logEnter(final LoggerPath logger) {
                    Objects.requireNonNull(logger, "logger");
                }

                @Override
                public void logExit() {
                    // nop
                }

                @Override
                public void log(final LoggingLevel loggingLevel,
                                final String message,
                                final Throwable throwable) {
                    Objects.requireNonNull(loggingLevel, "logger");
                }

                @Override
                public boolean isLoggingEnabled(final LoggingLevel loggingLevel) {
                    Objects.requireNonNull(loggingLevel, "loggingLevel");
                    throw new UnsupportedOperationException();
                }
            };
        }

        @Override
        public String toString() {
            return this.getClass()
                .getSimpleName();
        }
    }
}
