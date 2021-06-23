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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class MultiArgOperatorTest is a unit test case to assert the
 * functionality of the {@link MultiArgOperator} class
 *
 * @author T.N.Silverman
 */
class MultiArgOperatorTest {

    private static final Logger logger =
        LoggerFactory.getLogger(MultiArgOperatorTest.class);

    @BeforeEach
    public void beforeEach(TestInfo info) throws Exception {
        logger.debug("entering {} {}",
                info.getTestMethod().orElseThrow().getName(),
                info.getDisplayName());
    }

    @Test
    @DisplayName("test summing multi arg operator")
    public void testSummingMultiArgOperator() throws Exception {
        MultiArgOperator<BigDecimal> sum = args -> Arrays.stream(args)
                .reduce(BigDecimal.ZERO, (a, b) -> a.add(b));
        BigDecimal expected = BigDecimal.valueOf(6);
        BigDecimal actual = sum.apply(new BigDecimal[]{BigDecimal.valueOf(1),
                                                       BigDecimal.valueOf(2),
                                                       BigDecimal.valueOf(3)});
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("test min multi arg operator")
    public void testMinMultiArgOperator() throws Exception {
        MultiArgOperator<BigDecimal> min = args -> Arrays.stream(args).reduce(
                BigDecimal.valueOf(Double.MAX_VALUE),
                (a, b) -> a.compareTo(b) <= 0 ? a : b);
        BigDecimal expected = BigDecimal.valueOf(0);
        BigDecimal actual = min.apply(new BigDecimal[]{BigDecimal.ONE,
                                                       BigDecimal.ZERO,
                                                       BigDecimal.TEN});
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("test max multi arg operator")
    public void testMaxMultiArgOperator() throws Exception {
        MultiArgOperator<BigDecimal> max = args -> Arrays.stream(args).reduce(
                BigDecimal.valueOf(Double.MIN_VALUE),
                (a, b) -> a.compareTo(b) >= 0 ? a : b);
        BigDecimal expected = BigDecimal.valueOf(10);
        BigDecimal actual = max.apply(new BigDecimal[]{BigDecimal.ONE,
                                                       BigDecimal.ZERO,
                                                       BigDecimal.TEN});
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("test min multi arg operator with single arg")
    public void testMinMultiArgOperatorWithSingleArg() throws Exception {
        MultiArgOperator<BigDecimal> min = args -> Arrays.stream(args).reduce(
                BigDecimal.valueOf(Double.MAX_VALUE),
                (a, b) -> a.compareTo(b) <= 0 ? a : b);
        BigDecimal expected = BigDecimal.ONE;
        BigDecimal actual = min.apply(new BigDecimal[]{BigDecimal.ONE});
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("test max multi arg operator with single arg")
    public void testMaxMultiArgOperatorWithSingleArg() throws Exception {
        MultiArgOperator<BigDecimal> max = args -> Arrays.stream(args).reduce(
                BigDecimal.valueOf(Double.MIN_VALUE),
                (a, b) -> a.compareTo(b) >= 0 ? a : b);
        BigDecimal expected = BigDecimal.ONE;
        BigDecimal actual = max.apply(new BigDecimal[]{BigDecimal.ONE});
        assertEquals(expected, actual);
    }

}
