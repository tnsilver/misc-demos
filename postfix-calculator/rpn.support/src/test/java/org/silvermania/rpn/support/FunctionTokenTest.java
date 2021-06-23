/*
 * File: FunctionTokenTest.java
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

import static org.junit.jupiter.api.Assertions.*;
import static org.silvermania.rpn.support.Multiplicity.BINARY;
import static org.silvermania.rpn.support.TokenUtil.*;

import java.math.BigDecimal;

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
 * The Class FunctionTokenTest is a unit test case to assert the functionality
 * of the {@link FunctionToken} class
 *
 * @author T.N.Silverman
 */
class FunctionTokenTest {

    private static final Logger logger = LoggerFactory.getLogger(FunctionTokenTest.class);
    private static CalculationContext context = CalculationContext.newInstance();

    @BeforeAll
    public static void registerFunction() throws Exception {
        context.registerFunction("power", BINARY, (arr) -> arr[0].pow(arr[1].intValue()));
    }

    @BeforeEach
    public void beforeEach(TestInfo info) throws Exception {
        logger.debug("entering {} {}", info.getTestMethod().orElseThrow().getName(), info.getDisplayName());
    }

    @Test
    @DisplayName("test register function twice and throw")
    public void testRegisteredFunctionTwiceAndThrow() {
        assertThrows(IllegalArgumentException.class,
                () -> context.registerFunction("power", BINARY, (arr) -> arr[0].pow(arr[1].intValue())));
    }

    @ParameterizedTest
    @DisplayName("test default functions recognition")
    @CsvSource({"sin,true",
                "cos,true",
                "min,true",
                "max,true",
                "avg,true",
                "mod,false",
                "log,true",
                "%,false",
                "*,false",
                "3.14,false"})
    public void testDefaultFunctionsRecognition(String token, String expected) {
        assertEquals(Boolean.valueOf(expected), isFunction(token, context));
    }

    @ParameterizedTest
    @DisplayName("test default unary functions")
    @CsvSource({"sin,90,1.0000000", "cos,90,0.0000000", "tan,45,1.0000000", "log,1000,3.0000000","log,10,1.0000000","log,1,0.0000000","log,0.1,-1.0000000"})
    public void testDefaultUnaryFunctions(String token, BigDecimal arg, BigDecimal expected) {
        ArithmeticToken function = getArithmeticToken(token, context);
        assertNotNull(function);
        BigDecimal actual = function.getOperation().apply(new BigDecimal[]{arg});
        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @DisplayName("test default binary functions")
    @CsvSource({"min,1,2,1.0000000", "max,1,2,2.0000000", "avg,1,2,1.5000000","pct,2,4,50.0000000","sum,1,2,3.0000000"})
    public void testDefaultBinaryFunctions(String token, BigDecimal arg1, BigDecimal arg2, BigDecimal expected) {
        ArithmeticToken function = getArithmeticToken(token, context);
        assertNotNull(function);
        BigDecimal actual = function.getOperation().apply(token.equals("pct") ? new BigDecimal[]{arg2, arg1} : new BigDecimal[]{arg1, arg2});
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("test hashcode")
    public void testHashCode() throws Exception {
        FunctionToken actual = FunctionToken.create("#", null);
        FunctionToken expected = FunctionToken.create("#", null);
        assertEquals(Integer.valueOf(expected.hashCode()), Integer.valueOf(actual.hashCode()));
    }

    @Test
    @DisplayName("test equals")
    public void testEquals() throws Exception {
        FunctionToken actual = FunctionToken.create("#", null);
        FunctionToken expected = FunctionToken.create("#", null);
        assertTrue(expected.equals(actual));
    }
}
