/*
 * File: PostfixTokenHandler.java
 * Creation Date: Jun 22, 2019
 *
 * Copyright (c) 2019 T.N.Silverman - all rights reserved
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions
 * and limitations under the License.
 */
package org.silvermania.rpn.postfix.support;

import static org.silvermania.rpn.support.Associativity.LEFT;
import static org.silvermania.rpn.support.TokenUtil.getArithmeticToken;
import static org.silvermania.rpn.support.TokenUtil.isArithmeticToken;
import static org.silvermania.rpn.support.TokenUtil.isBinaryArithmeticToken;
import static org.silvermania.rpn.support.TokenUtil.isOperand;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.EmptyStackException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Stack;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.silvermania.rpn.support.ArithmeticToken;
import org.silvermania.rpn.support.Associativity;
import org.silvermania.rpn.support.CalculationContext;
import org.silvermania.rpn.support.OperandToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class PostfixTokenHandler is responsible for the logic of evaluating
 * {@code postfix} expressions.
 *
 * @author T.N.Silverman
 */
public final class PostfixTokenHandler implements Serializable {

    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(PostfixTokenHandler.class);

    /** The context. */
    private CalculationContext context;

    /** The handlers registry. */
    private final Map<Predicate<CharSequence>,
            Function<Optional<CharSequence>, Consumer<Stack<BigDecimal>>>> handlersRegistry = new LinkedHashMap<>();

    /** The log trace level function. */
    private final Function<String, Consumer<Stack<BigDecimal>>> traceFunction = message -> (stack) -> {
        String content = stack.stream().map(d -> d.toString()).collect(Collectors.joining(" "));
        logger.trace(String.format("%-50s %-2s %-20s", message, "->", content));
    };

    /** The exception creator */
    private final BiFunction<CharSequence, Throwable,
            Supplier<IllegalArgumentException>> errorCreator = (token,
                    cause) -> () -> new IllegalArgumentException(String.format("Cannot handle token '%1$s'.%n"
                        + "Is '%1$s' part of (or is a) space separated postfix postfix?%n"
                        + "If the input postfix is a normalized space separated postfix, this is typically caused "
                        + "by an unknown opernad, operator, function, unmatched parentheses or brackets, or a missing "
                        + "space character in the postfix.%n Otherwise, please register a token acceptor and "
                        + "handler or normalize the input postfix postfix.", token), cause);

    /**
     * Instantiates a new PostfixTokenHandler.
     *
     * @param context the calculation context on which to locate the meaning of
     *        functions, operators, constants and to obtain information about
     *        rounding modes and rounding decimal places
     */
    private PostfixTokenHandler(final CalculationContext context) {
        super();
        this.context = context;
        handlersRegistry.put(operandPredicate, operandHandler);
        handlersRegistry.put(operatorPredicate, operatorHandler);
    }

    /**
     * a factory method for obtaining a new instance of this
     * {@code PostfixTokenHandler}.
     *
     * @param context the calculation context on which to locate the meaning of
     *        functions, operators, constants and to obtain information about
     *        rounding modes and rounding decimal places
     * @return new instance of {@code InfixTokenHandler}
     */
    public static PostfixTokenHandler newInstance(CalculationContext context) {
        return new PostfixTokenHandler(context);
    }

    /** The operand predicate. */
    /*
     * predicate for operands
     */
    private final Predicate<CharSequence> operandPredicate = token -> isOperand(token, context);

    /** The operator or function predicate. */
    /*
     * predicate for operators and functions
     */
    private final Predicate<CharSequence> operatorPredicate = token -> isArithmeticToken(token, context);

    /** The operand handler function. */
    /*
     * handles operand tokens
     */
    private final Function<Optional<CharSequence>, Consumer<Stack<BigDecimal>>> operandHandler = value -> (stack) -> {
        // push the value of the operand to the stack
        OperandToken operand = OperandToken.create(value.get(), context);
        logger.trace("pushing value: {}", operand.getValue());
        stack.push(operand.getValue());
        traceFunction.apply("operandHandler (push " + value.get() + ")").accept(stack);
    };

    /** handles operators and function tokens. */
    private final Function<Optional<CharSequence>, Consumer<Stack<BigDecimal>>> operatorHandler = value -> (stack) -> {
        ArithmeticToken operator = getArithmeticToken(value.get(), context);
        CharSequence symbol = operator.getSymbol();
        traceFunction.apply("OPERATOR_FUNCTION_FUNC (found " + symbol + ")").accept(stack);
        BigDecimal arg1 = BigDecimal.ZERO;
        BigDecimal arg2 = BigDecimal.ZERO;
        try {
            if (isBinaryArithmeticToken(symbol, context)) {
                /*
                 * Here is a special case for the exponential operator. Behaves like RIGHT
                 * association in infix to RPN conversion, but LEFT association in RPN
                 * evaluation. Here, we swap the associativity in this single case.
                 *
                 * TODO: improve the design to avoid this by adding RIGHT_LEFT association???
                 */
                Associativity associativity = symbol.equals("^") ? LEFT : operator.getAssociativity();
                switch (associativity) {
                    case RIGHT:
                        arg1 = stack.pop();
                        arg2 = stack.pop();
                        break;
                    case LEFT:
                        arg2 = stack.pop();
                        arg1 = stack.pop();
                        break;
                    default:
                        break;
                }
            } else {
                arg1 = stack.pop();
            }
        } catch (EmptyStackException ex) {
            throw errorCreator.apply(value.orElse("N/A"), ex).get();
        }
        BigDecimal result = operator.getOperation().apply(new BigDecimal[]{arg1, arg2});
        stack.push(result);
        traceFunction.apply("OPERATOR_FUNCTION_FUNC (pushed " + result + ")").accept(stack);
    };

    /**
     * handles the calculation operation by applying the given {@code handler} on
     * the current {@code value} and further process the given {@code stack} with
     * the result. The {@code acceptor} predicate decides if any handling is
     * required, based on a given {@code token}
     *
     * @param acceptor the acceptor predicate
     * @param handler the handler function
     */
    public void registerHandling(Predicate<CharSequence> acceptor,
            Function<Optional<CharSequence>, Consumer<Stack<BigDecimal>>> handler) {
        handlersRegistry.put(acceptor, handler);
    }

    /**
     * checks if an acceptor predicate is already registered.
     *
     * @param acceptor the predicate to decide if this handler can handle the
     *        {@code token}
     * @return true if the given {@code acceptor} is already registered, otherwise
     *         false
     */
    public boolean isRegistered(Predicate<CharSequence> acceptor) {
        return handlersRegistry.containsKey(acceptor);
    }

    /** The value. */
    // handling
    private Optional<CharSequence> value = Optional.empty();

    /**
     * Accept.
     *
     * @param predicate the predicate
     * @param token the token
     * @return true, if successful
     */
    private boolean accept(Predicate<CharSequence> predicate, CharSequence token) {
        if (predicate.test(token)) {
            value = Optional.of(token);
            return true;
        }
        return false;
    }

    /**
     * handles the calculation operation by applying the given {@code function} on
     * the current {@code value} and further process the given {@code stack} with
     * the result.
     *
     * @param function the operation to apply
     * @param stack the operands stack
     */
    private void handle(Function<Optional<CharSequence>, Consumer<Stack<BigDecimal>>> function,
            Stack<BigDecimal> stack) {
        function.apply(value).accept(stack);
        value = Optional.empty(); // reset the value
    };

    /**
     * handle the token evaluation by operating on the stack.
     *
     * @param token the postfix postfix token
     * @param stack the operator / operand stack
     */
    public void handle(CharSequence token, Stack<BigDecimal> stack) {
        handlersRegistry.entrySet().stream().filter(e -> accept(e.getKey(), token)).map(e -> {
            handle(e.getValue(), stack);
            return true;
        }).findAny().orElseThrow(errorCreator.apply(token,
                new IllegalArgumentException(String.format("offending postfix postfix '%s'", token))));
    }
}
