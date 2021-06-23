/*
 * File: PostfixFunctionCalculationIntegrationTest.java
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
package org.silvermania.rpn.postfix.calculator;

import java.lang.reflect.Constructor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.silvermania.rpn.infix.converter.InfixConverter;
import org.silvermania.rpn.support.CalculationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class BaseCalculatorTestCase is a base unit test to initialize required
 * test classes for all test case
 *
 * @author T.N.Silverman
 */
abstract class BaseCalculatorTestCase {

    protected Logger logger;
    protected CalculationContext context;
    protected InfixConverter infixConverter;
    protected RPNCalculator RPNCalculator;

    protected RPNCalculator getCalulator(CalculationContext context) {
        try {
            Constructor<RPNCalculator> constructor =
                RPNCalculator.class.getDeclaredConstructor(
                        new Class<?>[]{CalculationContext.class});
            constructor.setAccessible(true);
            return constructor.newInstance(context);
        } catch (Exception error) {
            throw new RuntimeException(error);
        }
    }

    @BeforeEach
    public void beforeEach(TestInfo info) throws Exception {
        logger = LoggerFactory.getLogger(this.getClass());
        logger.debug("entering {} {}",
                info.getTestMethod().orElseThrow().getName(),
                info.getDisplayName());
        context = CalculationContext.newInstance();
        infixConverter = InfixConverter.newInstance(context);
        RPNCalculator = getCalulator(context);

    }

}
