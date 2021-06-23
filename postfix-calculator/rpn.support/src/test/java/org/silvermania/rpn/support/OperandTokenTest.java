/*
 * File: OperandTokenTest.java
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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class OperandTokenTest is a unit test case to assert the functionality of
 * the {@link OperandToken} class
 *
 * @author T.N.Silverman
 */
class OperandTokenTest {

    private static final Logger logger =
        LoggerFactory.getLogger(OperandTokenTest.class);
    private static CalculationContext context =
        CalculationContext.newInstance();

    @BeforeEach
    public void beforeEach(TestInfo info) throws Exception {
        logger.debug("entering {} {}",
                info.getTestMethod().orElseThrow().getName(),
                info.getDisplayName());
    }

    @Test
    @DisplayName("test create new operand token")
    public void testCreateNewOperandToken() {
        OperandToken actual =
            OperandToken.create(Double.valueOf(Math.PI).toString(), context);
        assertNotNull(actual);
    }

    @Test
    @DisplayName("test create new operand token")
    public void testCreateCachedOperandToken() {
        OperandToken actual = OperandToken.create("π", context);
        assertNotNull(actual);
    }

    @Test
    @DisplayName("test create operand token with context")
    public void testCreateNewOperandTokenWithContext() {
        OperandToken actual =
            OperandToken.create(Double.valueOf(Math.PI).toString(), context);
        assertNotNull(actual);
    }

    @Test
    @DisplayName("test create cached operand token with context")
    public void testCreateCachedOperandTokenWithContext() {
        OperandToken actual = OperandToken.create("π", context);
        assertNotNull(actual);
    }

    @Test
    @DisplayName("test create bad operand token with context and throw")
    public void testCreateNoneNumericOperandTokenWithContextAndThrow() {
        assertThrows(IllegalArgumentException.class,
                () -> OperandToken.create("%", context));
    }

    @Test
    @DisplayName("test create bad operand token and throw")
    public void testCreateNoneNumericOperandTokenAndThrow() {
        assertThrows(IllegalArgumentException.class,
                () -> OperandToken.create("%", context));
    }

    @Test
    @DisplayName("test to string")
    public void testToString() {
        OperandToken actual =
            OperandToken.create(Double.valueOf(Math.PI).toString(), context);
        assertNotNull(actual.toString());
    }

    @Test
    @DisplayName("test get value")
    public void testGetValue() {
        OperandToken actual =
            OperandToken.create(Double.valueOf(Math.PI).toString(), context);
        assertNotNull(actual.getValue());
        assertEquals(Double.toString(Math.PI), actual.getValue().toString());
    }

}
