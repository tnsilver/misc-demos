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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.silvermania.rpn.postfix.api.CalculatorState;
import org.silvermania.rpn.postfix.calculator.RPNCalculator;
import org.silvermania.rpn.support.CalculationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class CalculatorStateTest is a unit test to assert the functionality of
 * the {@link CalculatorState} class.
 *
 * @author T.N.Silverman
 */
class CalculatorStateTest {

    private static final Logger logger =
        LoggerFactory.getLogger(CalculatorStateTest.class);
    private CalculationContext context;
    private CalculatorState state;

    @BeforeEach
    public void beforeEach(TestInfo info) throws Exception {
        logger.debug("entering {} {}",
                info.getTestMethod().orElseThrow().getName(),
                info.getDisplayName());
        context = CalculationContext.newInstance();
        state = new CalculatorState(context);
    }

    @Test
    //@Disabled
    @DisplayName("test set calculator")
    public void testSetCalculator() throws Exception {
        RPNCalculator expected = state.getCalculator();
        assertNotNull(expected);
        state.setCalculator(expected);
        RPNCalculator actual = state.getCalculator();
        assertEquals(expected, actual);
    }

    @Test
    //@Disabled
    @DisplayName("test set context")
    public void testSetContext() throws Exception {
        CalculationContext expected = CalculationContext.DECIMAL32_CONTEXT;
        state.setContext(expected);
        assertEquals(expected, state.getContext());
    }

    @Test
    //@Disabled
    @DisplayName("test print")
    public void testPrint() throws Exception {
        state.setInfix("1 * 2");
        state.setPostfix("1 2 *");
        state.setPrinting(true);
        BigDecimal actual = state.calculate();
        assertNotNull(actual);
        assertEquals(BigDecimal.valueOf(2).setScale(context.getPrecision()),
                actual);
    }
}
