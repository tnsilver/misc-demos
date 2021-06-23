/*
 * File: PostfixTokenHandlerTest.java
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
package org.silvermania.rpn.postfix.support;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Stack;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.silvermania.rpn.postfix.support.PostfixTokenHandler;
import org.silvermania.rpn.support.CalculationContext;
import org.silvermania.rpn.support.OperatorToken;
import org.silvermania.rpn.support.TokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class PostfixTokenHandlerTest is a unit test to assert the functionality
 * of calculations handling in the {@link PostfixTokenHandler} class.
 *
 * @author T.N.Silverman
 */
class PostfixTokenHandlerTest {

    private static final Logger logger =
        LoggerFactory.getLogger(PostfixTokenHandlerTest.class);
    private CalculationContext context;
    private PostfixTokenHandler postfixTokenHandler;

    @BeforeEach
    public void beforeEach(TestInfo info) throws Exception {
        logger.debug("entering {} {}",
                info.getTestMethod().orElseThrow().getName(),
                info.getDisplayName());
        context = CalculationContext.newInstance();
        postfixTokenHandler = PostfixTokenHandler.newInstance(context);
    }

    @Test
    //@Disabled
    @DisplayName("test register handling")
    public void testRegisterHandling() throws Exception {
        Predicate<CharSequence> acceptor =
            token -> token.toString().equals("ZERO");
        Function<Optional<CharSequence>, Consumer<Stack<BigDecimal>>> handler =
            value -> (stack) -> {
                stack.push(BigDecimal.ZERO);
            };
        postfixTokenHandler.registerHandling(acceptor, handler);
        assertTrue(postfixTokenHandler.isRegistered(acceptor));
    }

    @Test
    //@Disabled
    @DisplayName("test handling unknown token and throw")
    public void testUnknownTokenHandling() throws Exception {
        assertThrows(IllegalArgumentException.class,
                () -> postfixTokenHandler.handle("λ", new Stack<BigDecimal>()));
    }

    @Test
    //@Disabled
    @DisplayName("test handling")
    public void testHandling() throws Exception {
        Stack<BigDecimal> stack = new Stack<>();
        CharSequence token = "+";
        stack.push(BigDecimal.ONE);
        stack.push(BigDecimal.ONE);
        postfixTokenHandler.handle(token, stack);
        OperatorToken operator = TokenUtil.getOperator(token, context);
        BigDecimal expected = operator.getOperation()
                .apply(new BigDecimal[]{BigDecimal.ONE, BigDecimal.ONE});
        assertAll(() -> assertNotNull(operator),
                () -> assertFalse(stack.isEmpty()),
                () -> assertEquals(expected, stack.pop()));
    }
}
