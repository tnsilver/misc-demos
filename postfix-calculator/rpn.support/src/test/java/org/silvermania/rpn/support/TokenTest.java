/*
 * File: TokenTest.java
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
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class TokenTest is a unit test case to assert the functionality of the
 * {@link Token} class
 *
 * @author T.N.Silverman
 */
class TokenTest {

    private static final Logger logger =
        LoggerFactory.getLogger(TokenTest.class);

    @BeforeEach
    public void beforeEach(TestInfo info) throws Exception {
        logger.debug("entering {} {}",
                info.getTestMethod().orElseThrow().getName(),
                info.getDisplayName());
    }

    @Test
    @DisplayName("test hashcode")
    public void testHashCode() throws Exception {
        Token actual = new Token("$");
        Token expected = new Token("$");
        assertEquals(Integer.valueOf(expected.hashCode()),
                Integer.valueOf(actual.hashCode()));
    }

    @Test
    @DisplayName("test equals")
    public void testEquals() throws Exception {
        Token actual = new Token("$");
        Token expected = new Token("$");
        assertTrue(expected.equals(actual));
    }

    @Test
    @DisplayName("test this equals")
    public void testThisEquals() throws Exception {
        Token actual = new Token("$");
        assertTrue(actual.equals(actual));
    }

    @Test
    @DisplayName("test not equals")
    public void testNotEquals() throws Exception {
        Token actual = new Token("$");
        Token expected = new Token("#");
        assertFalse(expected.equals(actual));
    }

    @Test
    @DisplayName("test type not equals")
    public void testTypeNotEquals() throws Exception {
        Token actual = new Token("#", Multiplicity.BINARY);
        Token expected = new Token("#", Multiplicity.UNARY);
        assertFalse(expected.equals(actual));
    }

    @Test
    @DisplayName("test this symbol not equals")
    public void testThisSymbolNullNotEquals() throws Exception {
        Token actual = new Token(null, Multiplicity.BINARY);
        Token expected = new Token("#", Multiplicity.BINARY);
        assertFalse(expected.equals(actual));
    }

    @Test
    @DisplayName("test other symbol not equals")
    public void testOtherSymbolNullNotEquals() throws Exception {
        Token actual = new Token("#", Multiplicity.BINARY);
        Token expected = new Token(null, Multiplicity.BINARY);
        assertFalse(expected.equals(actual));
    }

    @Test
    @DisplayName("test null not equals")
    public void testNullNotEquals() throws Exception {
        Token actual = null;
        Token expected = new Token("$");
        assertFalse(expected.equals(actual));
    }

    @Test
    @DisplayName("test null not equals")
    public void testClassNotEquals() throws Exception {
        FunctionToken actual = FunctionToken.create("$", null);
        Token expected = new Token("$");
        assertFalse(expected.equals(actual));
    }
}
