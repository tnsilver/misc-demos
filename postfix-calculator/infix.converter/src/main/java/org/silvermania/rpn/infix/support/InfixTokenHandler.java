/*
 * File: InfixTokenHandler.java
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
package org.silvermania.rpn.infix.support;

import static org.silvermania.rpn.support.TokenUtil.getOperator;
import static org.silvermania.rpn.support.TokenUtil.isClosingBracket;
import static org.silvermania.rpn.support.TokenUtil.isClosingCurlyBracket;
import static org.silvermania.rpn.support.TokenUtil.isClosingParentheses;
import static org.silvermania.rpn.support.TokenUtil.isFunction;
import static org.silvermania.rpn.support.TokenUtil.isFunctionArgSeparator;
import static org.silvermania.rpn.support.TokenUtil.isOpener;
import static org.silvermania.rpn.support.TokenUtil.isOpeningBracket;
import static org.silvermania.rpn.support.TokenUtil.isOpeningCurlyBracket;
import static org.silvermania.rpn.support.TokenUtil.isOpeningParentheses;
import static org.silvermania.rpn.support.TokenUtil.isOperand;
import static org.silvermania.rpn.support.TokenUtil.isOperator;
import static org.silvermania.rpn.support.TokenUtil.isPostfixBinaryOperator;
import static org.silvermania.rpn.support.TokenUtil.isPostfixUnaryOperator;
import static org.silvermania.rpn.support.TokenUtil.isPrefixBinaryOperator;
import static org.silvermania.rpn.support.TokenUtil.isPrefixUnaryOperator;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Stack;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.silvermania.rpn.support.CalculationContext;
import org.silvermania.rpn.support.OperatorToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class InfixTokenHandler encapsulates the logic of converting an infix
 * arithmetic expression to a {@code RPN} (reversed polish notation - or
 * 'postfix') expression.
 *
 * The algorithm for the conversion is explained at <a href=
 * "https://www.chris-j.co.uk/parsing.phphttps://www.chris-j.co.uk/parsing.php">
 * https://www.chris-j.co.uk/parsing.php </a> and is as follows:
 *
 * For each token in turn in the input infix expression:
 * <ul>
 * <li>If the token is an operand, append it to the postfix output.</li>
 * <li>If the token is an operator <span class="maths">A</span> then:
 * <ul>
 * <li>While there is an operator <span class="maths">B</span> of higher or
 * equal precidence than <span class="maths">A</span> at the top of the stack,
 * pop <span class="maths">B</span> off the stack and append it to the
 * output.</li>
 * <li>Push <span class="maths">A</span> onto the stack.</li>
 * </ul>
 * </li>
 * <li>If the token is an opening bracket, then push it onto the stack.</li>
 * <li>If the token is a closing bracket:
 * <ul>
 * <li>Pop operators off the stack and append them to the output, until the
 * operator at the top of the stack is a opening bracket.</li>
 * <li>Pop the opening bracket off the stack.</li>
 * </ul>
 * </li>
 * </ul>
 * <br>
 * When all the tokens have been read:
 * <ul>
 * <li>While there are still operator tokens in the stack:
 * <ul>
 * <li>Pop the operator on the top of the stack, and append it to the
 * output.</li>
 * </ul>
 * </li>
 * </ul>
 * <br>
 * This class follows the {@code State} design pattern, where the state is
 * encapsulated in a lambda expression to handle a {@code token} read from the
 * input expression and manipulate an operator stack and an output queue, to
 * represent the converted infix expression at the end of the queue.
 * <p>
 * The handler knows if it can handle the {@code token} by calling the
 * {@link #accept(Predicate, CharSequence)} method, which returns true if the
 * token can be represented by one of the
 * {@link org.silvermania.rpn.support.Token} hierarchy classes and the handler
 * lambda is designed to handle the token type. The first argument to accept
 * method is the test predicate while the {@link CharSequence} argument is the
 * token.
 * <p>
 * A typical accept {@link Predicate} looks like this:
 *
 * <pre>
 * <b>Predicate&lt;CharSequence&gt; NAME_OF_PRED = token -&gt; isSomeTypeOfOperator(token);</b>
 * </pre>
 *
 * Handler lambda expressions are basically {@link BiConsumer} expressions, only
 * in order to pass the {@code token} to the handler, they are in the form of a
 * BiConsumer returning {@link Function} which takes a CharSequence token as a
 * parameter.
 * <p>
 * A typical handler lambda expression typically takes the form of:
 *
 * <pre>
 * <b>Function&lt;Optional&lt;CharSequence&gt;,
 *              BiConsumer&lt;Stack&lt;CharSequence&gt;, Queue&lt;CharSequence&gt;&gt;&gt; NAME_OF =
 *      value -&gt; (stack, queue) -&gt; { do stuff with stack and queue }</b>
 * </pre>
 *
 * The {@code value} argument to the function expression is this handler
 * {@link Optional} value, that is only none empty, if the
 * {@link InfixTokenHandler#accept(Predicate, CharSequence)} method returns
 * true. Trying to operate on the handler without acceptance leads to an
 * {@link UnsupportedOperationException}.
 * <p>
 * To manage the different states, this class contains a handler registry with
 * default {@link Predicate} to pass to the
 * {@link #accept(Predicate, CharSequence)} method and a handler lambda to
 * handle the {@code token}. This class simply calls the accept method for each
 * registered handler and deals with the first that answers true. In this
 * mechanism the {@code token} is actually the state changer.
 * <p>
 * Care must be taken to ensure there's only one single handler per token type,
 * otherwise a token may be handled twice or more and the operator stack and
 * output queue ruined.
 *
 * @author T.N.Silverman
 */
public final class InfixTokenHandler implements Serializable {

    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(InfixTokenHandler.class);

    /** The log trace level function. */
    private final Function<String, BiConsumer<Stack<CharSequence>, Queue<CharSequence>>> traceFunction =
        message -> (stack, queue) -> {
            String stackContent =
                new StringBuilder(stack.stream().collect(Collectors.joining(" "))).reverse().toString();
            String queueContent = queue.stream().collect(Collectors.joining(" "));
            logger.trace(String.format("%-25s %-2s %-20s %-2s %-40s", message, "->", queueContent, "|", stackContent));
        };

    /** The context. */
    private CalculationContext context;

    /**
     * hidden constructor.
     *
     * @param context the calculation context on which to locate the meaning of
     *        functions, operators, constants and to obtain information about
     *        rounding modes and rounding decimal places
     */
    private InfixTokenHandler(final CalculationContext context) {
        super();
        this.context = context;
        handlersRegistry.put(operandPredicate, operandHandler);
        handlersRegistry.put(postfixUniOperatorPredicate, postfixUniOperatorHandler);
        handlersRegistry.put(prefixUniOperatorPredicate, prefixUniOperatorHandler);
        handlersRegistry.put(functionPredicate, functionHandler);
        handlersRegistry.put(functionArgSeparatorPredicate, functionArgSeparatorHandler);
        handlersRegistry.put(prefixBiOperatorPredicate, prefixBiOperatorHandler);
        handlersRegistry.put(postfixBiOperatorPredicate, postfixBiOperatorHandler);
        // brackets
        handlersRegistry.put(openCurlyPredicate, openCurlyHandler);
        handlersRegistry.put(openBracketPredicate, openBracketHandler);
        handlersRegistry.put(openParenthesesPredicate, openParenthesesHandler);
        handlersRegistry.put(closeParenthesesPredicate, closeParenthesesHandler);
        handlersRegistry.put(closeBracketPredicate, closeBracketHandler);
        handlersRegistry.put(closeCurlyPredicate, closeCurlyHandler);
    }

    /**
     * a factory method for obtaining a new instance of this
     * {@code InfixTokenHandler}.
     *
     * @param context the calculation context on which to locate the meaning of
     *        functions, operators, constants and to obtain information about
     *        rounding modes and rounding decimal places
     * @return new instance of {@code InfixTokenHandler}
     */
    public static InfixTokenHandler newInstance(CalculationContext context) {
        return new InfixTokenHandler(context);
    }

    // private final Predicate<CharSequence> BI_OPER_PRED = token ->
    // isBinaryOperator(token, context);

    /** The postfix bi operator predicate. */
    private final Predicate<CharSequence> postfixBiOperatorPredicate = token -> isPostfixBinaryOperator(token, context);

    /** The prefix bi operator predicate. */
    private final Predicate<CharSequence> prefixBiOperatorPredicate = token -> isPrefixBinaryOperator(token, context);

    /** The postfix uni operator predicate. */
    private final Predicate<CharSequence> postfixUniOperatorPredicate = token -> isPostfixUnaryOperator(token, context);

    /** The prefix uni operator predicate. */
    private final Predicate<CharSequence> prefixUniOperatorPredicate = token -> isPrefixUnaryOperator(token, context);

    /** The function arg separator predicate. */
    private final Predicate<CharSequence> functionArgSeparatorPredicate = token -> isFunctionArgSeparator(token);

    /** The function predicate. */
    private final Predicate<CharSequence> functionPredicate = token -> isFunction(token, context);

    /** The open parentheses predicate. */
    private final Predicate<CharSequence> openParenthesesPredicate = token -> isOpeningParentheses(token);

    /** The close parentheses predicate. */
    private final Predicate<CharSequence> closeParenthesesPredicate = token -> isClosingParentheses(token);

    /** The open bracket predicate. */
    private final Predicate<CharSequence> openBracketPredicate = token -> isOpeningBracket(token);

    /** The close bracket predicate. */
    private final Predicate<CharSequence> closeBracketPredicate = token -> isClosingBracket(token);

    /** The open curly predicate. */
    private final Predicate<CharSequence> openCurlyPredicate = token -> isOpeningCurlyBracket(token);

    /** The close curly predicate. */
    private final Predicate<CharSequence> closeCurlyPredicate = token -> isClosingCurlyBracket(token);

    /** The operand predicate. */
    private final Predicate<CharSequence> operandPredicate = token -> isOperand(token, context);

    /** The handlers registry. */
    private final Map<Predicate<CharSequence>,
            Function<Optional<CharSequence>, BiConsumer<Stack<CharSequence>, Queue<CharSequence>>>> handlersRegistry =
                new LinkedHashMap<>();

    /** The prefix bi operator function. */
    /*
     * If the token is a binary operator A then: If A is prefix, while there is an
     * operator B of higher or equal precedence than A at the top of the stack, pop
     * B off the stack and append it to the output. If A is postfix, while there is
     * an operator B of higher precedence than A at the top of the stack, pop B off
     * the stack and append it to the output. Push A onto the stack.
     */
    private Function<Optional<CharSequence>,
            BiConsumer<Stack<CharSequence>, Queue<CharSequence>>> prefixBiOperatorHandler = value -> (stack, queue) -> {
                OperatorToken operatorToken = getOperator(value.get(), context);
                while (!stack.isEmpty() && isOperator(stack.peek(), context) && getOperator(stack.peek(), context)
                        .getPrecedence().compareTo(operatorToken.getPrecedence()) >= 0) {
                    traceFunction.apply("prefixBiOperatorHandler (pop " + stack.peek() + ")").accept(stack, queue);
                    queue.offer(stack.pop());
                }
                stack.push(value.get());
                traceFunction.apply("prefixBiOperatorHandler (pushed " + value.get() + ")").accept(stack, queue);
            };

    /** The postfix bi operator function. */
    private final Function<Optional<CharSequence>,
            BiConsumer<Stack<CharSequence>, Queue<CharSequence>>> postfixBiOperatorHandler =
                value -> (stack, queue) -> {
                    OperatorToken operatorToken = getOperator(value.get(), context);
                    while (!stack.isEmpty() && isOperator(stack.peek(), context) && getOperator(stack.peek(), context)
                            .getPrecedence().compareTo(operatorToken.getPrecedence()) > 0) {
                        traceFunction.apply("postfixBiOperatorHandler (pop " + stack.peek() + ")").accept(stack, queue);
                        queue.offer(stack.pop());
                    }
                    stack.push(value.get());
                    traceFunction.apply("postfixBiOperatorHandler (pushed " + value.get() + ")").accept(stack, queue);
                };

    /** The postfix uni operator function. */
    /*
     * If the token is a unary postfix operator, append it to the output.
     */
    private final Function<Optional<CharSequence>,
            BiConsumer<Stack<CharSequence>, Queue<CharSequence>>> postfixUniOperatorHandler =
                value -> (stack, queue) -> {
                    queue.offer(value.get());
                    traceFunction.apply("postfixUniOperatorHandler").accept(stack, queue);
                };

    /** The prefix uni operator function. */
    /*
     * If the token is a unary prefix operator, push it on to the stack.
     */
    private final Function<Optional<CharSequence>,
            BiConsumer<Stack<CharSequence>, Queue<CharSequence>>> prefixUniOperatorHandler =
                value -> (stack, queue) -> {
                    stack.push(value.get());
                    traceFunction.apply("prefixUniOperatorHandler").accept(stack, queue);
                };

    /** The function arg separator function. */
    /*
     * If the token is a function argument separator, pop the top element off the
     * stack and append it to the output, until the top element of the stack is an
     * opening bracket
     */
    private final Function<Optional<CharSequence>,
            BiConsumer<Stack<CharSequence>, Queue<CharSequence>>> functionArgSeparatorHandler =
                value -> (stack, queue) -> {
                    while (!stack.isEmpty() && !isOpeningParentheses(stack.peek())) {
                        queue.offer(stack.pop());
                    }
                    traceFunction.apply("functionArgSeparatorHandler").accept(stack, queue);
                };

    /** The function handler function. */
    /*
     * If the token is a function token, push it on to the stack.
     */
    private final Function<Optional<CharSequence>,
            BiConsumer<Stack<CharSequence>, Queue<CharSequence>>> functionHandler = value -> (stack, queue) -> {
                stack.push(value.get());
                traceFunction.apply("functionHandler").accept(stack, queue);
            };

    /** The open parentheses function. */
    /*
     * If the token is an opening bracket, then push it onto the stack.
     */
    private final Function<Optional<CharSequence>,
            BiConsumer<Stack<CharSequence>, Queue<CharSequence>>> openParenthesesHandler = value -> (stack, queue) -> {
                stack.push(value.get());
                traceFunction.apply("openParenthesesHandler").accept(stack, queue);
            };

    /** The open bracket function. */
    private final Function<Optional<CharSequence>,
            BiConsumer<Stack<CharSequence>, Queue<CharSequence>>> openBracketHandler = value -> (stack, queue) -> {
                stack.push(value.get());
                traceFunction.apply("openBracketHandler").accept(stack, queue);
            };

    /** The open curly function. */
    private final Function<Optional<CharSequence>,
            BiConsumer<Stack<CharSequence>, Queue<CharSequence>>> openCurlyHandler = value -> (stack, queue) -> {
                stack.push(value.get());
                traceFunction.apply("openCurlyHandler").accept(stack, queue);
            };

    /** The close parentheses function. */
    /*
     * If the token is a closing bracket: Pop OPERATORS off the stack and append
     * them to the output, until the operator at the top of the stack is an opening
     * bracket. Pop the opening bracket off the stack. If the token at the top of
     * the stack is a function token, pop it and append it to the output.
     */
    private final Function<Optional<CharSequence>,
            BiConsumer<Stack<CharSequence>, Queue<CharSequence>>> closeParenthesesHandler = value -> (stack, queue) -> {
                while (!stack.isEmpty() && !isOpener(stack.peek())) {
                    if (isOperator(stack.peek(), context)) {
                        queue.offer(stack.pop());
                    }
                    traceFunction.apply("closeParenthesesHandler (pop ops)").accept(stack, queue);
                }
                if (stack.isEmpty() || !isOpeningParentheses(stack.peek())) {
                    traceFunction.apply("closeParenthesesHandler (malformed " + "expression)").accept(stack, queue);
                    throw new IllegalArgumentException("Malformed expression! Unmatched ')'");
                } else {
                    stack.pop();
                    traceFunction.apply("closeParenthesesHandler (pop last)").accept(stack, queue);
                }
                if (!stack.isEmpty() && isFunction(stack.peek(), context)) {
                    queue.offer(stack.pop());
                    traceFunction.apply("closeParenthesesHandler (pop " + "function)").accept(stack, queue);
                }
                traceFunction.apply("closeParenthesesHandler (done)").accept(stack, queue);
            };

    /** The close bracket function. */
    private final Function<Optional<CharSequence>,
            BiConsumer<Stack<CharSequence>, Queue<CharSequence>>> closeBracketHandler = value -> (stack, queue) -> {
                while (!stack.isEmpty() && !isOpener(stack.peek())) {
                    if (isOperator(stack.peek(), context)) {
                        queue.offer(stack.pop());
                    }
                    traceFunction.apply("closeBracketHandler (popping " + "operators)").accept(stack, queue);
                }
                if (stack.isEmpty() || !isOpeningBracket(stack.peek())) {
                    traceFunction.apply("closeBracketHandler (malformed " + "expression)").accept(stack, queue);
                    throw new IllegalArgumentException("Malformed expression! Unmatched ']'");
                } else {
                    stack.pop();
                    traceFunction.apply("closeBracketHandler (pop last)").accept(stack, queue);
                }
                if (!stack.isEmpty() && isFunction(stack.peek(), context)) {
                    queue.offer(stack.pop());
                    traceFunction.apply("closeBracketHandler (pop function)").accept(stack, queue);
                }
                traceFunction.apply("closeBracketHandler (done)").accept(stack, queue);
            };

    /** The close curly function. */
    private final Function<Optional<CharSequence>,
            BiConsumer<Stack<CharSequence>, Queue<CharSequence>>> closeCurlyHandler = value -> (stack, queue) -> {
                while (!stack.isEmpty() && !isOpener(stack.peek())) {
                    if (isOperator(stack.peek(), context)) {
                        traceFunction.apply("closeCurlyHandler (popping " + "operators)").accept(stack, queue);
                        queue.offer(stack.pop());
                    }
                }
                if (stack.isEmpty() || !isOpeningCurlyBracket(stack.peek())) {
                    traceFunction.apply("closeCurlyHandler (malformed " + "expression)").accept(stack, queue);
                    throw new IllegalArgumentException("Malformed expression! Unmatched '}'");
                } else {
                    stack.pop();
                    traceFunction.apply("closeCurlyHandler (pop last)").accept(stack, queue);
                }
                if (!stack.isEmpty() && isFunction(stack.peek(), context)) {
                    queue.offer(stack.pop());
                    traceFunction.apply("closeCurlyHandler (pop function)").accept(stack, queue);
                }
                traceFunction.apply("closeCurlyHandler (done)").accept(stack, queue);
            };

    /** The operand function. */
    // If the token is an operand, append it to the postfix output.
    private Function<Optional<CharSequence>, BiConsumer<Stack<CharSequence>, Queue<CharSequence>>> operandHandler =
        value -> (stack, queue) -> {
            queue.offer(value.get());
            traceFunction.apply("operandHandler (" + value.get() + ")").accept(stack, queue);
        };

    /**
     * Augment the handlers registry with a new way to handle a {@code token} found
     * in an {@code infix} expression. The method accepts a {@code acceptor}
     * {@link Predicate} to decide if the token is to be handled, and a
     * {@code handler function} to handle the {@code token}.
     *
     * @apiNote The {@code handler} function handles the {@code token} by either
     *          pusing or poping operands and operators from it's {@code stack} and
     *          placing them on it's output {@code queue}
     *
     * @param acceptor the acceptor predicate
     * @param handler the handler function
     */
    public void registerHandling(Predicate<CharSequence> acceptor,
            Function<Optional<CharSequence>, BiConsumer<Stack<CharSequence>, Queue<CharSequence>>> handler) {
        handlersRegistry.put(acceptor, handler);
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
     * Handle.
     *
     * @param function the function
     * @param stack the stack
     * @param queue the queue
     */
    private void handle(Function<Optional<CharSequence>, BiConsumer<Stack<CharSequence>, Queue<CharSequence>>> function,
            Stack<CharSequence> stack, Queue<CharSequence> queue) {
        if (value.isEmpty()) {
            throw new UnsupportedOperationException("Cannot handle empty token! Did 'accept()' return true?");
        }
        function.apply(value).accept(stack, queue);
        value = Optional.empty(); // reset the value
    };

    /**
     * Handle.
     *
     * @param token the token
     * @param stack the stack
     * @param queue the queue
     */
    public void handle(CharSequence token, Stack<CharSequence> stack, Queue<CharSequence> queue) {
        handlersRegistry.entrySet().stream().filter(e -> accept(e.getKey(), token)).map(e -> {
            handle(e.getValue(), stack, queue);
            return true;
        }).findAny()
                .orElseThrow(() -> new IllegalArgumentException(String
                        .format("No matching token handler registered for accepting and " + "handling value '%s'%n"
                            + "The error cause is an unknown opernad, operator, "
                            + "function or missing space.%n"
                            + "Please register an approproate type and handler or "
                            + "fix the input infix expression.", token)));
    }
}
