/*
 * File: InfixConverter.java
 * Creation Date: Jun 18, 2019
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
package org.silvermania.rpn.infix.converter;

import static org.silvermania.rpn.support.TokenUtil.isOpener;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.Stack;
import java.util.stream.Collectors;

import org.silvermania.rpn.infix.support.InfixNormalizer;
import org.silvermania.rpn.infix.support.InfixTokenHandler;
import org.silvermania.rpn.support.CalculationContext;
import org.silvermania.rpn.support.Converter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class InfixConverter is the front end for converting {@code infix}
 * expressions to {@code postfix} (reversed polish notation) expressions. <br>
 * The algorithm for the conversion is explained at <a href=
 * "https://www.chris-j.co.uk/parsing.php">https://www.chris-j.co.uk/parsing.php</a>
 * and is as follows:
 *
 * For each token in turn in the input infix expression:
 * <ul>
 * <li>If the token is an operand, append it to the postfix output.</li>
 * <li>If the token is an operator A then:
 * <ul>
 * <li>While there is an operator B of higher or equal precidence than A at the
 * top of the stack, pop B off the stack and append it to the output.</li>
 * <li>Push A onto the stack.</li>
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
 *
 * @author T.N.Silverman
 */
public final class InfixConverter implements Converter<CharSequence, String> {

    private static final Logger logger =
        LoggerFactory.getLogger(InfixConverter.class);

    private CalculationContext context;

    /**
     * Instantiates a new infix to postfix converter.
     *
     * @param context the calculation context on which to locate the meaning of
     *        functions, operators, constants and to obtain information about
     *        rounding modes and rounding decimal places
     */
    private InfixConverter(final CalculationContext context) {
        super();
        this.context = context;
    }

    /**
     * a factory method for obtaining a new instance of this
     * {@code InfixConverter}
     *
     * @param context the calculation context on which to locate the meaning of
     *        functions, operators, constants and to obtain information about
     *        rounding modes and rounding decimal places
     *
     * @return new instance of {@code InfixConverter}
     */
    public static InfixConverter newInstance(CalculationContext context) {
        return new InfixConverter(context);
    }

    /**
     * Converts an {@code infix} expression (such as <b>1 + 2</b>) to a
     * {@code postfix} expression (such as <b>1 2 +</b>).
     *
     * @implNote to convert an infix expressions to a postfix expression the
     *           infix expression must have each operand, operator, function,
     *           function argument separator and bracket separated by a space.
     *           The {@link InfixNormalizer} is responsible for making sure this
     *           is the case with any input, space separated or not.
     *
     * @param expression the input {@code infix} expression
     * @return a space separated {@code postfix} expression
     * @throws IllegalArgumentException if the infix expression is malformed,
     *         for example, contains unmatched brackets or parentheses or if the
     *         infix expression contains unknown (unregistered) operators,
     *         functions or constants.
     */
    @Override
    public String convert(final CharSequence expression) {
        Queue<CharSequence> queue = new LinkedList<>();
        Stack<CharSequence> stack = new Stack<>();
        CharSequence infix =
            InfixNormalizer.newInstance(context).normalize(expression);
        logger.trace("converting normalized infix: {}", infix);
        InfixTokenHandler handler = InfixTokenHandler.newInstance(context);
        try (Scanner scanner = new Scanner(infix.toString())) {
            scanner.useDelimiter(" ");
            while (scanner.hasNext()) {
                handler.handle(scanner.next(), stack, queue);
            }
            // if there's opening ( on top of the stack the expression is malformed
            if (!stack.isEmpty()) {
                if (isOpener(stack.peek())) {
                    throw new IllegalArgumentException(
                            "Malformed expression! Unmatched opening bracket ( | [ | {");
                }
                // pop the rest of the operators on the stack to the output queue.
                while (!stack.isEmpty()) {
                    queue.offer(stack.pop());
                }
            }
        }
        String result = queue.stream().collect(Collectors.joining(" "));
        logger.debug("converted infix '{}' to postfix '{}'", infix, result);
        return result;
    }
}
