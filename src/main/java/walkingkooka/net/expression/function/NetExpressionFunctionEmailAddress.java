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

import walkingkooka.Cast;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.tree.expression.ExpressionEvaluationContext;
import walkingkooka.tree.expression.function.ExpressionFunctionParameter;

import java.util.List;

/**
 * A function that may be used to get or including parsing text into a {@link EmailAddress}.
 */
final class NetExpressionFunctionEmailAddress<C extends ExpressionEvaluationContext> extends NetExpressionFunction<EmailAddress, C> {

    static <C extends ExpressionEvaluationContext> NetExpressionFunctionEmailAddress<C> instance() {
        return Cast.to(INSTANCE);
    }

    /**
     * Singleton
     */
    private final static NetExpressionFunctionEmailAddress<ExpressionEvaluationContext> INSTANCE = new NetExpressionFunctionEmailAddress<>();

    private NetExpressionFunctionEmailAddress() {
        super("emailAddress");
    }

    @Override
    public List<ExpressionFunctionParameter<?>> parameters(final int count) {
        return PARAMETERS;
    }

    private final static List<ExpressionFunctionParameter<?>> PARAMETERS = ExpressionFunctionParameter.list(
        EMAIL_ADDRESS
    );

    @Override
    public Class<EmailAddress> returnType() {
        return EmailAddress.class;
    }

    @Override
    public EmailAddress apply(final List<Object> parameters,
                              final C context) {
        return EMAIL_ADDRESS.getOrFail(parameters, 0, context);
    }
}
