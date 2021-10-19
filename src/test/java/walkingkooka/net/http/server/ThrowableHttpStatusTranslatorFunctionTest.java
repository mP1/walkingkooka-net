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

import org.junit.jupiter.api.Test;
import walkingkooka.net.http.HttpEntity;
import walkingkooka.net.http.HttpStatus;
import walkingkooka.net.http.HttpStatusCode;
import walkingkooka.util.FunctionTesting;

import java.util.Optional;

public final class ThrowableHttpStatusTranslatorFunctionTest implements FunctionTesting<ThrowableHttpStatusTranslatorFunction, Throwable, HttpStatus> {

    private final static String MESSAGE = "message123";
    private final static String MESSAGE_MULTI_LINE = MESSAGE + "\n2\r3";

    @Test
    public void testIllegalArgumentExceptionNull() {
        this.applyAndCheck(
                new IllegalArgumentException(),
                HttpStatusCode.BAD_REQUEST
                        .status()
        );
    }

    @Test
    public void testIllegalArgumentExceptionEmptyMessage() {
        this.applyAndCheck(
                new IllegalArgumentException(""),
                HttpStatusCode.BAD_REQUEST
                        .status()
        );
    }

    @Test
    public void testIllegalArgumentException() {
        this.applyAndCheck(
                new IllegalArgumentException(MESSAGE_MULTI_LINE),
                HttpStatusCode.BAD_REQUEST
                        .setMessage(MESSAGE)
        );
    }

    @Test
    public void testIllegalStateExceptionNull() {
        this.applyAndCheck(
                new IllegalStateException(),
                HttpStatusCode.NOT_FOUND.status()
        );
    }

    @Test
    public void testIllegalStateExceptionEmptyMessage() {
        this.applyAndCheck(
                new IllegalStateException(""),
                HttpStatusCode.NOT_FOUND.status()
        );
    }

    @Test
    public void testIllegalStateException() {
        this.applyAndCheck(
                new IllegalStateException(MESSAGE_MULTI_LINE),
                HttpStatusCode.NOT_FOUND
                        .setMessage(MESSAGE)
        );
    }

    private final static HttpStatus STATUS = HttpStatusCode.withCode(999)
            .setMessage("Hello!");

    @Test
    public void testHttpResponseHttpServerException() {
        this.applyAndCheck(
                new HttpResponseHttpServerException(
                        STATUS,
                        HttpResponseHttpServerException.NO_ENTITY
                ),
                STATUS
        );
    }

    @Test
    public void testHttpResponseHttpServerExceptionEntityIgnored() {
        this.applyAndCheck(
                new HttpResponseHttpServerException(
                        STATUS,
                        Optional.of(
                                HttpEntity.EMPTY.setBodyText("Ignored123")
                        )
                ),
                STATUS
        );
    }

    @Test
    public void testUnsupportedOperationExceptionNull() {
        this.applyAndCheck(
                new UnsupportedOperationException(),
                HttpStatusCode.NOT_IMPLEMENTED
                        .status()
        );
    }

    @Test
    public void testUnsupportedOperationExceptionEmptyMessage() {
        this.applyAndCheck(
                new UnsupportedOperationException(""),
                HttpStatusCode.NOT_IMPLEMENTED
                        .status()
        );
    }

    @Test
    public void testUnsupportedOperationException() {
        this.applyAndCheck(
                new UnsupportedOperationException(MESSAGE_MULTI_LINE),
                HttpStatusCode.NOT_IMPLEMENTED
                        .setMessage(MESSAGE)
        );
    }

    @Test
    public void testExceptionNull() {
        this.applyAndCheck(
                new Exception(),
                HttpStatusCode.INTERNAL_SERVER_ERROR
                        .status()
        );
    }

    @Test
    public void testExceptionEmptyMessage() {
        this.applyAndCheck(
                new Exception(""),
                HttpStatusCode.INTERNAL_SERVER_ERROR
                        .status()
        );
    }

    @Test
    public void testException() {
        this.applyAndCheck(
                new Exception(MESSAGE_MULTI_LINE),
                HttpStatusCode.INTERNAL_SERVER_ERROR
                        .setMessage(MESSAGE)
        );
    }

    @Override
    public ThrowableHttpStatusTranslatorFunction createFunction() {
        return ThrowableHttpStatusTranslatorFunction.INSTANCE;
    }

    @Override
    public Class<ThrowableHttpStatusTranslatorFunction> type() {
        return ThrowableHttpStatusTranslatorFunction.class;
    }
}
