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

package walkingkooka.net.expression.function;

import walkingkooka.net.HasHostAddress;
import walkingkooka.net.Url;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.tree.expression.ExpressionEvaluationContext;
import walkingkooka.tree.expression.ExpressionFunctionName;
import walkingkooka.tree.expression.ExpressionPurityContext;
import walkingkooka.tree.expression.function.ExpressionFunction;
import walkingkooka.tree.expression.function.ExpressionFunctionParameter;
import walkingkooka.tree.expression.function.ExpressionFunctionParameterKind;
import walkingkooka.tree.expression.function.ExpressionFunctionParameterName;

import java.util.Optional;

/**
 * A {@link ExpressionFunction} with an assumed {@link C}.
 */
abstract class NetExpressionFunction<T, C extends ExpressionEvaluationContext> implements ExpressionFunction<T, C> {

    final static ExpressionFunctionParameter<EmailAddress> EMAIL_ADDRESS = ExpressionFunctionParameterName.with("emailAddress")
        .required(EmailAddress.class)
        .setKinds(ExpressionFunctionParameterKind.CONVERT_EVALUATE);

    final static ExpressionFunctionParameter<HasHostAddress> HAS_HOST_ADDRESS = ExpressionFunctionParameterName.with("hasHostAddress")
        .required(HasHostAddress.class)
        .setKinds(ExpressionFunctionParameterKind.CONVERT_EVALUATE);

    final static ExpressionFunctionParameter<HasHostAddress> HAS_HOST_ADDRESS2 = ExpressionFunctionParameterName.with("hostAddress2")
        .required(HasHostAddress.class)
        .setKinds(ExpressionFunctionParameterKind.CONVERT_EVALUATE);

    final static ExpressionFunctionParameter<Url> URL = ExpressionFunctionParameterName.with("url")
        .required(Url.class)
        .setKinds(ExpressionFunctionParameterKind.CONVERT_EVALUATE);

    NetExpressionFunction(final String name) {
        super();
        this.name = Optional.of(
            ExpressionFunctionName.with(name)
        );
    }

    @Override
    public final Optional<ExpressionFunctionName> name() {
        return this.name;
    }

    private final Optional<ExpressionFunctionName> name;

    /**
     * All functions are pure
     */
    @Override
    public final boolean isPure(final ExpressionPurityContext context) {
        return true;
    }

    @Override
    public final String toString() {
        return this.name()
            .get()
            .toString();
    }
}
