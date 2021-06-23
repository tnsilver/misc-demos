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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.silvermania.rpn.postfix.api.CalculationConfigurer;
import org.silvermania.rpn.postfix.calculator.RPNCalculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class CalculationConfigurerTest is a unit test to assert the
 * functionality of the {@link CalculationConfigurer} implementation class.
 *
 * @author T.N.Silverman
 */
class CalculationConfigurerTest {

    private static final Logger logger =
        LoggerFactory.getLogger(CalculationConfigurerTest.class);

    @BeforeEach
    public void beforeEach(TestInfo info) throws Exception {
        logger.debug("entering {} {}",
                info.getTestMethod().orElseThrow().getName(),
                info.getDisplayName());
    }

    @Test
    //@Disabled
    @DisplayName("test convert")
    public void testConvert() throws Exception {
        CalculationConfigurer configurer =
            RPNCalculator.withDefaults().convert("1*2");
        assertNotNull(configurer);
    }

    @Test
    //@Disabled
    @DisplayName("test accept")
    public void testAccept() throws Exception {
        CalculationConfigurer configurer =
            RPNCalculator.withDefaults().accept("1 2 *");
        assertNotNull(configurer);
    }

    @Test
    //@Disabled
    @DisplayName("test bad infix convert and throw")
    public void testAcceptBadInfixxAndThrow() throws Exception {
        assertThrows(IllegalArgumentException.class,
                () -> RPNCalculator.withDefaults().convert("blah"));
    }

    @Test
    //@Disabled
    @DisplayName("test bad postfix accept and throw")
    public void testAcceptBadPostifxAndThrow() throws Exception {
        assertThrows(IllegalArgumentException.class, () -> RPNCalculator
                .withDefaults().accept("1*2").thenCalculate());
    }

    @Test
    //@Disabled
    @DisplayName("test do print")
    public void testDoPrint() throws Exception {
        CalculationConfigurer configurer =
            RPNCalculator.withDefaults().convert("1*2").doPrint();
        assertNotNull(configurer);
    }

}
