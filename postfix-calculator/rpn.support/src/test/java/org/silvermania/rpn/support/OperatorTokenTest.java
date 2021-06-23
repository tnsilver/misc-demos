/*
 * File: OperatorTokenTest.java
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
package org.silvermania.rpn.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.silvermania.rpn.support.Associativity.LEFT;
import static org.silvermania.rpn.support.Associativity.RIGHT;
import static org.silvermania.rpn.support.Multiplicity.UNARY;
import static org.silvermania.rpn.support.Precedence.HIGHEST;
import static org.silvermania.rpn.support.Precedence.LOW;
import static org.silvermania.rpn.support.Precedence.LOWEST;
import static org.silvermania.rpn.support.TokenUtil.getOperator;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class OperatorTokenTest is a unit test case to assert the functionality
 * of the {@link OperatorToken} class
 *
 * @author T.N.Silverman
 */
class OperatorTokenTest {

    private static final Logger logger = LoggerFactory.getLogger(OperatorTokenTest.class);
    private static CalculationContext context = CalculationContext.newInstance();

    @BeforeAll
    public static void beforeAll() throws Exception {
        context.registerOperator("~", HIGHEST, LEFT, UNARY, (arr) -> context.round(Math.sqrt(arr[0].doubleValue())));
    }

    @BeforeEach
    public void beforeEach(TestInfo info) throws Exception {
        logger.debug("entering {} {}", info.getTestMethod().orElseThrow().getName(), info.getDisplayName());
    }

    @ParameterizedTest
    @DisplayName("test registered operator")
    @CsvSource({"~"})
    public void testRegisteredOperator(String token) {
        assertNotNull(getOperator(token, context));
    }

    @Test
    @DisplayName("test registered operator twice and throw")
    public void testRegisteredOperatorTwiceAndThrow() {
        assertThrows(IllegalArgumentException.class, () -> context.registerOperator("~", HIGHEST, LEFT, UNARY,
                (arr) -> context.round(Math.sqrt(arr[0].doubleValue()))));
    }

    @ParameterizedTest
    @DisplayName("test default operators")
    @CsvSource({"^", "!", "*", "×", "/", "÷", "%", "+", "−", "-"})
    public void testDefaultOperators(String token) {
        assertNotNull(getOperator(token, context));
    }

    @Test
    @DisplayName("test hashcode")
    public void testHashCode() throws Exception {
        OperatorToken actual = OperatorToken.create("&", LOWEST, null);
        OperatorToken expected = OperatorToken.create("&", LOWEST, null);
        assertEquals(Integer.valueOf(expected.hashCode()), Integer.valueOf(actual.hashCode()));
    }

    @Test
    @DisplayName("test hashcode")
    public void testAssociativityHashCode() throws Exception {
        OperatorToken actual = OperatorToken.create("&", null, null, null);
        OperatorToken expected = OperatorToken.create("&", null, null, null);
        assertEquals(Integer.valueOf(expected.hashCode()), Integer.valueOf(actual.hashCode()));
    }

    @Test
    @DisplayName("test equals")
    public void testEquals() throws Exception {
        OperatorToken actual = OperatorToken.create("&", LOWEST, null);
        OperatorToken expected = OperatorToken.create("&", LOWEST, null);
        assertTrue(expected.equals(actual));
    }

    @Test
    @DisplayName("test same equals")
    public void testSameEquals() throws Exception {
        OperatorToken actual = OperatorToken.create("&", LOWEST, null);
        assertTrue(actual.equals(actual));
    }

    @Test
    @DisplayName("test super not equals")
    public void testSuperNotEquals() throws Exception {
        OperatorToken actual = OperatorToken.create("&", LOWEST, null);
        Token expected = new Token("%", UNARY);
        assertFalse(actual.equals(expected));
    }

    @Test
    @DisplayName("test class not equals")
    public void testClassNotEquals() throws Exception {
        OperatorToken actual = OperatorToken.create("&", null, null, null);
        Token expected = new Token("&", null);
        assertFalse(actual.equals(expected));
    }

    @Test
    @DisplayName("test associativity not equals")
    public void testAssociativityNotEquals() throws Exception {
        OperatorToken actual = OperatorToken.create("&", LOWEST, LEFT, UNARY, null);
        OperatorToken expected = OperatorToken.create("&", LOWEST, RIGHT, UNARY, null);
        assertFalse(actual.equals(expected));
    }

    @Test
    @DisplayName("test precedence null not equals")
    public void testPrecedenceNullNotEquals() throws Exception {
        OperatorToken actual = OperatorToken.create("&", null, LEFT, UNARY, null);
        OperatorToken expected = OperatorToken.create("&", LOWEST, LEFT, UNARY, null);
        assertFalse(actual.equals(expected));
    }

    @Test
    @DisplayName("test precedence not equals")
    public void testPrecedenceNotEquals() throws Exception {
        OperatorToken actual = OperatorToken.create("&", LOWEST, LEFT, UNARY, null);
        OperatorToken expected = OperatorToken.create("&", LOW, LEFT, UNARY, null);
        assertFalse(actual.equals(expected));
    }
}
