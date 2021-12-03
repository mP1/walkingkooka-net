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

import org.junit.jupiter.api.Test;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.reflect.TypeNameTesting;

public final class HttpStatusCodeCategoryTest implements ClassTesting2<HttpStatusCodeCategory>,
        TypeNameTesting<HttpStatusCodeCategory> {

    @Test
    public void testContinue() {
        this.categoryAndCheck(100, HttpStatusCodeCategory.INFORMATION);
    }

    @Test
    public void test199() {
        this.categoryAndCheck(199, HttpStatusCodeCategory.INFORMATION);
    }

    @Test
    public void testOk() {
        this.categoryAndCheck(200, HttpStatusCodeCategory.SUCCESSFUL);
    }

    @Test
    public void test299() {
        this.categoryAndCheck(299, HttpStatusCodeCategory.SUCCESSFUL);
    }

    @Test
    public void test304() {
        this.categoryAndCheck(304, HttpStatusCodeCategory.REDIRECTION);
    }

    @Test
    public void test399() {
        this.categoryAndCheck(399, HttpStatusCodeCategory.REDIRECTION);
    }

    @Test
    public void test404() {
        this.categoryAndCheck(404, HttpStatusCodeCategory.CLIENT_ERROR);
    }

    @Test
    public void test499() {
        this.categoryAndCheck(499, HttpStatusCodeCategory.CLIENT_ERROR);
    }

    @Test
    public void test500() {
        this.categoryAndCheck(500, HttpStatusCodeCategory.SERVER_ERROR);
    }

    @Test
    public void test599() {
        this.categoryAndCheck(599, HttpStatusCodeCategory.SERVER_ERROR);
    }

    @Test
    public void testUnknown() {
        this.categoryAndCheck(600, HttpStatusCodeCategory.UNKNOWN);
    }

    private void categoryAndCheck(final int code,
                                  final HttpStatusCodeCategory expected) {
        this.checkEquals(expected,
                HttpStatusCodeCategory.category(code),
                () -> "code " + code);
    }

    @Override
    public Class<HttpStatusCodeCategory> type() {
        return HttpStatusCodeCategory.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }

    // TypeNameTesting .........................................................................................

    @Override
    public String typeNamePrefix() {
        return HttpStatus.class.getSimpleName();
    }

    @Override
    public String typeNameSuffix() {
        return "";
    }
}
