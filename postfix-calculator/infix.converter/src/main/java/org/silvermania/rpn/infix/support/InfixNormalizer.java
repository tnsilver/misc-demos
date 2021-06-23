/*
 * File: InfixNormalizer.java
 * Creation Date: Jun 20, 2019
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

import static org.silvermania.rpn.support.TokenUtil.isArithmeticToken;
import static org.silvermania.rpn.support.TokenUtil.isOperand;
import static org.silvermania.rpn.support.TokenUtil.isToken;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.silvermania.rpn.support.CalculationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class InfixNormalizer normalizes a given {@code infix} expression by
 * separating the operands and operators into a space separated char sequence.
 *
 * @author T.N.Silverman
 */
public final class InfixNormalizer implements Serializable {

    private static final Logger logger =
        LoggerFactory.getLogger(InfixNormalizer.class);

    /**
     * the list of tokens in an infix expression
     */
    private final List<CharSequence> tokens = new LinkedList<CharSequence>();
    private CalculationContext context;

    /**
     * hidden constructor
     *
     * @param context the calculation context on which to locate the meaning of
     *        functions, operators, constants and to obtain information about
     *        rounding modes and rounding decimal places
     */
    private InfixNormalizer(final CalculationContext context) {
        super();
        this.context = context;
    }

    /**
     * a factory method for obtaining a new instance of this
     * {@code InfixNormalizer}
     *
     * @param context the calculation context on which to locate the meaning of
     *        functions, operators, constants and to obtain information about
     *        rounding modes and rounding decimal places
     *
     * @return new instance of {@code InfixNormalizer}
     */
    public static InfixNormalizer newInstance(CalculationContext context) {
        return new InfixNormalizer(context);
    }

    /**
     * Stores the first longest token in a given {@code infix} expression, and
     * return the suffix of the original infix expression less the token, for
     * recursive processing.
     *
     * @param infix the infix expression to extract the first longest token from
     * @return the suffix of the original {@code infix} expression less the
     *         token found
     * @throws IllegalArgumentException if a token is found that cannot be
     *         evaluated to an operand (numeric or constant) or an operator,
     *         function, function argument separator, or any opening or closing
     *         parentheses, square or curly brackets.
     */
    private CharSequence storeAndNormalize(CharSequence infix)
            throws IllegalArgumentException {
        logger.trace("infix: {}", infix);
        StringBuilder chars = new StringBuilder(infix);
        CharSequence current = null;
        CharSequence token = null;
        int endIdx = 1;
        while (endIdx <= chars.length()) {
            current = chars.substring(0, endIdx); // look at first char
            if (isToken(current, context)) { // if token, temporarily save it
                logger.info(context.print());
                token = current;
                logger.trace("is-token: {}", current);
                if (!tokens.isEmpty()
                        && isOperand(tokens.get(tokens.size() - 1), context)
                        && isArithmeticToken(token, context)) {
                    // it it's a minus sign, and the previous token was an operand, our current
                    // minus sign is not an operand but an operator, so we're done
                    break;
                }
            } else {
                logger.trace("no-token: {}", current);
                if (null != token && isToken(token, context)) {
                    break; // we already have the largest token, so done
                }
            }
            endIdx++; // keep looking for a larger token
        }
        if (token != null) {
            tokens.add(token);
            CharSequence result = chars.substring(token.length());
            return storeAndNormalize(result);
        } else {
            if (infix.length() > 0 && current != null) {
                throw new IllegalArgumentException(
                        "unrecognized token '" + infix.charAt(0)
                            + "' in infix expression");
            }
            return "";
        }
    }

    /**
     * Normalizes a given {@code infix} expression to a space separated
     * {@code CharSequence} expression for later conversion to {@code postfix}
     * expression.
     *
     * @param infix the input infix expression
     * @return a space separated char sequence representing the contents of the
     *         input {@code infix} expression
     * @throws IllegalArgumentException if the given {@code infix} expression is
     *         null or blank
     */
    public CharSequence normalize(CharSequence infix)
            throws IllegalArgumentException {
        CharSequence result = stream(infix).collect(Collectors.joining(" "));
        logger.debug("normalized '{}' to '{}'", infix, result);
        return result;
    }

    /**
     * Streams the tokens representing the contents of the input {@code infix}
     * expression
     *
     * @param infix the input infix expression
     * @return a stream of tokens representing contents of the input
     *         {@code infix} expression
     * @throws IllegalArgumentException if the given {@code infix} expression is
     *         null or blank
     */
    public Stream<CharSequence> stream(CharSequence infix)
            throws IllegalArgumentException {
        if (null == infix || infix.toString().isBlank()) {
            throw new IllegalArgumentException("offending infix expression."
                + " input expression cannot be null or blank");
        }
        infix = infix.toString().replaceAll(" ", "").replaceAll("\t", "");
        logger.trace("infix: {}", infix);
        storeAndNormalize(infix);
        return tokens.stream();
    }

}
