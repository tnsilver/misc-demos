/*
 * File: InfixTokenHandlerTest.java
 * Creation Date: Jun 18, 2019
 *
 * Copyright (c) 2019 T.N.Silverman - all rights reserved
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license
 * agreements. See the NOTICE file distributed with this work for additional
 * information regarding
 * copyright ownership. The ASF licenses this file to you under the Apache
 * License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express
 * or implied. See the License for the specific language governing permissions
 * and limitations under
 * the License.
 */
package org.silvermania.rpn.infix.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.silvermania.rpn.support.TokenUtil.isOpener;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.Stack;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.silvermania.rpn.infix.support.InfixTokenHandler;
import org.silvermania.rpn.support.CalculationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class InfixTokenHandlerTest is a unit test to assert the functionality of
 * the {@link InfixTokenHandler} class
 *
 * @author T.N.Silverman
 */
class InfixTokenHandlerTest {

    private static final Logger logger =
        LoggerFactory.getLogger(InfixTokenHandlerTest.class);

    private Queue<CharSequence> queue = new LinkedList<>();

    private Stack<CharSequence> stack = new Stack<>();

    private InfixTokenHandler handler =
        InfixTokenHandler.newInstance(CalculationContext.newInstance());

    @BeforeEach
    public void beforeEach(TestInfo info) throws Exception {
        logger.debug("entering {} {}",
                info.getTestMethod().orElseThrow().getName(),
                info.getDisplayName());
    }

    @ParameterizedTest
    @DisplayName("test handler conversion")
    @CsvSource(delimiter = '@',
               value = {"2 ^ 4 * [ ( 8 ! + 3 ) / ( 8 % 3 ) ] + 1@2 4 ^ 8 ! 3 + 8 3 % / * 1 +",
                        "2 ^ 4 * [ ( 8 + 3 ) ]@2 4 ^ 8 3 + *",
                        "-2 ^ 4 * 8@-2 4 ^ 8 *",
                        "2 ^ 4 * 8@2 4 ^ 8 *",
                        "-2 ^ 4 * 8@-2 4 ^ 8 *",
                        "3 + 4 * 2@3 4 2 * +",
                        "3 + 4 * 2 / ( 1 - 5 ) ^ 2 ^ 3@3 4 2 * 1 5 - 2 3 ^ ^ / +",
                        "3 + 4 × 2 ÷ ( 1 − 5 ) ^ 2 ^ 3@3 4 2 × 1 5 − 2 3 ^ ^ ÷ +",
                        "sin ( max ( 2 , 3 ) ÷ 3 × π )@2 3 max 3 ÷ π × sin"})
    public void testHandlerConversion(String expression, String expected) {
        try (Scanner scanner = new Scanner(expression)) {
            scanner.useDelimiter(" ");
            while (scanner.hasNext()) {
                handler.handle(scanner.next(), stack, queue);
            }
            // if there's opening parentheses on top of the stack, the expression is malformed
            if (!stack.isEmpty()) {
                if (isOpener(stack.peek())) {
                    throw new IllegalArgumentException(
                            "Malformed expression! Unmatched opening bracket '(','[' or '{'");
                }
                // pop the rest of the operators on the stack to the output queue.
                while (!stack.isEmpty() /* && isOperator(stack.peek()) */) {
                    queue.offer(stack.pop());
                }
            }
        }
        String actual = queue.stream().collect(Collectors.joining(" "));
        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @DisplayName("test unmatched infix expression conversion")
    @CsvSource(delimiter = '@',
               value = {"{ { [ ( 1 + 1 ) + ( 1 + 1 ) ] }",
                        "{ [ ( 1 + 1 ) + ( 1 + 1 ) ] } }",
                        "{ [ [ ( 1 + 1 ) + ( 1 + 1 ) ] }",
                        "{ [ ( 1 + 1 ) + ( 1 + 1 ) ] ] }",
                        "{ [ ( ( 1 + 1 ) + ( 1 + 1 ) ] }",
                        "{ [ ( 1 + 1 ) + ( 1 + 1 ) ) ] }",
                        "1 + { 2 + [ ( 1 + 1 ) + ( 1 + 1 ) ] } + 2 }"})
    void testUnmatchedBracketsInfixExpression(String expression) {
        assertThrows(IllegalArgumentException.class, () -> {
            try (Scanner scanner = new Scanner(expression)) {
                scanner.useDelimiter(" ");
                while (scanner.hasNext()) {
                    handler.handle(scanner.next(), stack, queue);
                }
                // if there's opening parentheses on top of the stack, the expression is malformed
                if (!stack.isEmpty()) {
                    if (isOpener(stack.peek())) {
                        throw new IllegalArgumentException(
                                "Malformed expression! Unmatched opening bracket '(','[' or '{'");
                    }
                    // pop the rest of the operators on the stack to the output queue.
                    while (!stack.isEmpty() /* && isOperator(stack.peek()) */) {
                        queue.offer(stack.pop());
                    }
                }
            }
        });
    }

}
