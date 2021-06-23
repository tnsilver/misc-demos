/*
 * File: ArithmeticTokenTest.java
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

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.silvermania.rpn.support.Associativity.LEFT;
import static org.silvermania.rpn.support.Precedence.LOWEST;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class ArithmeticTokenTest is a unit test case to assert the functionality
 * of the {@link ArithmeticToken} class
 *
 * @author T.N.Silverman
 */
class ArithmeticTokenTest {

    private static final Logger logger = LoggerFactory.getLogger(ArithmeticTokenTest.class);

    @BeforeEach
    public void beforeEach(TestInfo info) throws Exception {
        logger.debug("entering {} {}", info.getTestMethod().orElseThrow().getName(), info.getDisplayName());
    }

    @Test
    @DisplayName("test create operator token")
    public void testCreateOperatorToken() throws Exception {
        ArithmeticToken actual = OperatorToken.create("$", LOWEST, LEFT, (arr) -> BigDecimal.ZERO);
        assertAll(() -> assertNotNull(actual), () -> assertEquals(Multiplicity.BINARY, actual.getMultiplicity()));
    }

    @Test
    @DisplayName("test crerate function token")
    public void testCreateFunctionToken() throws Exception {
        ArithmeticToken actual = FunctionToken.create("func", (arr) -> BigDecimal.ONE);
        assertAll(() -> assertNotNull(actual), () -> assertEquals(Multiplicity.BINARY, actual.getMultiplicity()));
    }

    @Test
    @DisplayName("test hashcode")
    public void testHashCode() throws Exception {
        ArithmeticToken actual = OperatorToken.create("$", LOWEST, null);
        ArithmeticToken expected = OperatorToken.create("$", LOWEST, null);
        assertEquals(Integer.valueOf(expected.hashCode()), Integer.valueOf(actual.hashCode()));
    }

    @Test
    @DisplayName("test equals")
    public void testEquals() throws Exception {
        ArithmeticToken actual = OperatorToken.create("$", LOWEST, null);
        ArithmeticToken expected = OperatorToken.create("$", LOWEST, null);
        assertTrue(expected.equals(actual));
    }

    @Test
    @DisplayName("test this equals")
    public void testThisEquals() throws Exception {
        ArithmeticToken actual = OperatorToken.create("$", LOWEST, null);
        assertTrue(actual.equals(actual));
    }

    @Test
    @DisplayName("test not equals")
    public void testNotEquals() throws Exception {
        ArithmeticToken actual = OperatorToken.create("$", LOWEST, null);
        ArithmeticToken expected = OperatorToken.create("#", LOWEST, null);
        assertFalse(expected.equals(actual));
    }

    @Test
    @DisplayName("test multiplicity not equals")
    public void testMultiplicityNotEquals() throws Exception {
        ArithmeticToken actual = OperatorToken.create("#", LOWEST, LEFT, Multiplicity.UNARY, null);
        ArithmeticToken expected = OperatorToken.create("#", LOWEST, LEFT, Multiplicity.BINARY, null);
        assertFalse(expected.equals(actual));
    }

    @Test
    @DisplayName("test this symbol not equals")
    public void testThisSymbolNullNotEquals() throws Exception {
        ArithmeticToken actual = OperatorToken.create(null, LOWEST, LEFT, Multiplicity.UNARY, null);
        ArithmeticToken expected = OperatorToken.create("#", LOWEST, LEFT, Multiplicity.UNARY, null);
        assertFalse(expected.equals(actual));
    }

    @Test
    @DisplayName("test other symbol not equals")
    public void testOtherSymbolNullNotEquals() throws Exception {
        ArithmeticToken actual = OperatorToken.create("#", LOWEST, LEFT, Multiplicity.UNARY, null);
        ArithmeticToken expected = OperatorToken.create(null, LOWEST, LEFT, Multiplicity.UNARY, null);
        assertFalse(expected.equals(actual));
    }

    @Test
    @DisplayName("test null not equals")
    public void testNullNotEquals() throws Exception {
        Token actual = null;
        ArithmeticToken expected = OperatorToken.create("#", LOWEST, LEFT, Multiplicity.UNARY, null);
        assertFalse(expected.equals(actual));
    }

    @Test
    @DisplayName("test null not equals")
    public void testClassNotEquals() throws Exception {
        FunctionToken actual = FunctionToken.create("$", null);
        ArithmeticToken expected = OperatorToken.create(null, LOWEST, LEFT, Multiplicity.UNARY, null);
        assertFalse(expected.equals(actual));
    }

}
