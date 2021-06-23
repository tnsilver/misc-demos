/*
 * File: CalculationContextTest.java
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
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.silvermania.rpn.support.TokenUtil.getConstant;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class CalculationContextTest is a unit test case to assert the
 * functionality of the {@link CalculationContext} class
 *
 * @author T.N.Silverman
 */
class CalculationContextTest {

    private static final Logger logger = LoggerFactory.getLogger(CalculationContextTest.class);
    private CalculationContext context;

    @BeforeEach
    public void beforeEach(TestInfo info) throws Exception {
        logger.debug("entering {} {}", info.getTestMethod().orElseThrow().getName(), info.getDisplayName());
        context = CalculationContext.newInstance();
        context.registerConstant("λ", BigDecimal.ONE);
    }

    @ParameterizedTest
    @DisplayName("test registered constant found")
    @CsvSource({"λ"})
    public void testRegisteredConstantFound(String token) {
        assertNotNull(getConstant(token, context));
    }

    @ParameterizedTest
    @DisplayName("test add variable")
    @CsvSource({"r,12.0"})
    public void testAddVariable(String token, Double value) {
        context.addVariable(token, value);
        OperandToken actual = getConstant(token, context);
        assertNotNull(actual);
        assertEquals(actual.getValue(), BigDecimal.valueOf(value));
    }

    @Test
    @DisplayName("test add variable")
    public void testOverrideVariable() {
        context.addVariable("r", 10D);
        context.addVariable("r", 12D);
        OperandToken actual = getConstant("r", context);
        assertNotNull(actual);
        assertEquals(actual.getValue(), BigDecimal.valueOf(12D));
    }


    @Test
    @DisplayName("test add blank variable")
    public void testAddBlankVariable() {
        assertThrows(IllegalArgumentException.class, () -> context.addVariable("", 12.0));
    }

    @Test
    @DisplayName("test add colliding constant variable")
    public void testAddCollidingConstantVariable() {
        assertThrows(IllegalArgumentException.class, () -> context.addVariable("e", 12.0));
    }

    @Test
    @DisplayName("test add colliding function variable")
    public void testAddCollidingFunctionVariable() {
        assertThrows(IllegalArgumentException.class, () -> context.addVariable("min", 12.0));
    }

    @Test
    @DisplayName("test add null value variable")
    public void testAddNullValueVariable() {
        Double value = null;
        assertThrows(IllegalArgumentException.class, () -> context.addVariable("r", value));
    }

    @Test
    @DisplayName("test add null big decimal value variable")
    public void testAddNullBigDecimalValueVariable() {
        BigDecimal value = null;
        assertThrows(IllegalArgumentException.class, () -> context.addVariable("r", value));
    }


    @Test
    @DisplayName("test register constant twice and throw")
    public void testRegisterConstantTwiceAndThrow() {
        assertThrows(IllegalArgumentException.class, () -> context.registerConstant("λ", BigDecimal.ONE));
    }

    @Test
    @DisplayName("test register blank constant and throw")
    public void testRegisterBlankConstantAndThrow() {
        assertThrows(IllegalArgumentException.class, () -> context.registerConstant(" ", BigDecimal.ZERO));
    }

    @Test
    @DisplayName("test set negative decimal palces and throw")
    public void testSetNegativeDecimalPlacesAndThrow() throws Exception {
        assertThrows(IllegalArgumentException.class, () -> context.setPrecision(-1));
    }

    @Test
    @DisplayName("test round null double and throw")
    public void testRoundNullDoubleAndThrow() throws Exception {
        Double nill = null;
        assertThrows(IllegalArgumentException.class, () -> context.round(nill));
    }

    @Test
    @DisplayName("test round null bigDecimal and throw")
    public void testRoundNullBigDecimalAndThrow() throws Exception {
        BigDecimal nill = null;
        assertThrows(IllegalArgumentException.class, () -> context.round(nill));
    }

    @Test
    @DisplayName("test round NaN bigDecimal and throw")
    public void testRoundNaNBigDecimalAndThrow() throws Exception {
        assertThrows(IllegalArgumentException.class, () -> context.round(Double.NaN));
    }

    @Test
    @DisplayName("test get rounding mode")
    public void testGetRoundingMode() throws Exception {
        assertNotNull(context.getRoundingMode());
    }

    @Test
    @DisplayName("test get num of decimal places")
    public void testGetNumOfDecimalPlaces() throws Exception {
        assertNotNull(context.getPrecision());
    }

    @Test
    @DisplayName("test try mutate num of decimal places")
    public void testTryMutateNumOfDecimalPlaces() throws Exception {
        Integer unexpected = context.getPrecision();
        unexpected = unexpected + 2;
        assertNotEquals(unexpected, context.getPrecision());
    }

    @ParameterizedTest
    @DisplayName("test rounding")
    @CsvSource({"3.1426,1,3.1", "3.1426,2,3.14", "3.1426,3,3.143", "3.1426,4,3.1426", "3.1426,5,3.14260"})
    public void testRounding(Double value, int places, BigDecimal expected) throws Exception {
        context.setPrecision(places);
        assertEquals(expected, context.round(value));
    }

    @ParameterizedTest
    @DisplayName("test decimal 32 rounding")
    @CsvSource({"3.141592653589793115997963468544185161590576171875" + ",3.1415927"})
    public void testDecimal32ContextRounding(String raw, String rounded) throws Exception {
        context = CalculationContext.DECIMAL32_CONTEXT;
        BigDecimal expected = new BigDecimal(rounded);
        BigDecimal actual = context.round(new BigDecimal(raw));
        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @DisplayName("test decimal 64 rounding")
    @CsvSource({"3.141592653589793115997963468544185161590576171875" + ",3.1415926535897931"})
    public void testDecimal64ContextRounding(String raw, String rounded) throws Exception {
        context = CalculationContext.DECIMAL64_CONTEXT;
        BigDecimal expected = new BigDecimal(rounded);
        BigDecimal actual = context.round(new BigDecimal(raw));
        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @DisplayName("test decimal 128 rounding")
    @CsvSource({"3.141592653589793115997963468544185161590576171875" + ",3.1415926535897931159979634685441852"})
    public void testDecimal128ContextRounding(String raw, String rounded) throws Exception {
        context = CalculationContext.DECIMAL128_CONTEXT;
        BigDecimal expected = new BigDecimal(rounded);
        BigDecimal actual = context.round(new BigDecimal(raw));
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("test equals when equals")
    public void testEqualsWhenEquals() throws Exception {
        CalculationContext actual = CalculationContext.newInstance();
        CalculationContext expected = CalculationContext.newInstance();
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("test equals when same")
    public void testEqualsWhenSame() throws Exception {
        CalculationContext actual = CalculationContext.newInstance();
        CalculationContext expected = actual;
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("test not equals when other is null")
    public void testNotEqualsWhenOtherIsNull() throws Exception {
        CalculationContext context = CalculationContext.newInstance();
        CalculationContext other = null;
        assertFalse(context.equals(other));
    }

    @Test
    @DisplayName("test not equals when other is different")
    public void testNotEqualsWhenOtherIsDifferet() throws Exception {
        CalculationContext context = CalculationContext.DECIMAL64_CONTEXT;
        CalculationContext other = CalculationContext.DECIMAL32_CONTEXT;
        assertFalse(context.equals(other));
    }

    @Test
    @DisplayName("test not equals when other is different")
    public void testNotEqualsWhenBothAreDifferet() throws Exception {
        CalculationContext context = CalculationContext.DECIMAL128_CONTEXT;
        CalculationContext other = CalculationContext.DECIMAL64_CONTEXT;
        assertFalse(context.equals(other));
    }

    @Test
    @DisplayName("test not equals when other is different")
    public void testNotEqualsWhenOtherIsCustom() throws Exception {
        CalculationContext context = CalculationContext.newInstance();
        CalculationContext other = CalculationContext.newInstance();
        other.setRoundingMode(RoundingMode.CEILING);
        other.setPrecision(2);
        assertFalse(context.equals(other));
    }

    @Test
    @DisplayName("test not equals when other is null")
    public void testNotEqualsWhenDifferentClass() throws Exception {
        CalculationContext context = CalculationContext.newInstance();
        Printable other = () -> "";
        assertFalse(context.equals(other));
    }

    @Test
    @DisplayName("test hashcode")
    public void testHashCode() throws Exception {
        CalculationContext expected = CalculationContext.newInstance();
        CalculationContext actual = CalculationContext.newInstance();
        assertEquals(expected.hashCode(), actual.hashCode());
    }

    @Test
    @DisplayName("test to string")
    public void testToString() throws Exception {
        CalculationContext expected = CalculationContext.newInstance();
        CalculationContext actual = CalculationContext.newInstance();
        assertEquals(expected.toString(), actual.toString());
    }

}
