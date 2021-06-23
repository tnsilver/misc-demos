/*
 * File: OperandToken.java
 * Creation Date: Jul 2, 2019
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
package org.silvermania.rpn.support;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class OperandToken represents a numeric token or a mathematical constant.
 *
 * @author T.N.Silverman
 * @apiNote Users who wish to register new constants can do so via the
 *          {@link org.silvermania.rpn.support.CalculationContext#registerConstant(CharSequence, BigDecimal)
 *          CalculationContext#registerConstant(CharSequence, BigDecimal)}
 *          method, but are required to perform the call prior to any
 *          calculation operation
 */
public final class OperandToken implements Serializable {

    /** The Constant logger. */
    private static final Logger logger =
        LoggerFactory.getLogger(OperandToken.class);

    /** The value. */
    private BigDecimal value;

    /**
     * hidden constructor.
     *
     * @param numstr the numeric string representing the value of this operand
     *        token
     */
    private OperandToken(final CharSequence numstr) {
        super();
        this.value = new BigDecimal(
                Objects.requireNonNull(numstr, "operand token cannot be null")
                        .toString());
    }

    /**
     * factory method to create an operand given a {@link CharSequence} token.
     * The token can be either a constant or a valid representation of
     * {@link Double}
     *
     * @param numstr the numeric string representing the value of this operand
     *        token
     * @param context the context
     * @return a new OperandToken
     * @throws IllegalArgumentException if the token is null, does not represent
     *         a constant in the registry or is not a valid {@link Double}
     *         representation
     * @throws NullPointerException is the token is null
     */
    public static OperandToken create(CharSequence numstr,
            CalculationContext context) throws IllegalArgumentException {
        if (context.getConstantsRegistry().containsKey(Objects
                .requireNonNull(numstr, "operand token cannot be null"))) {
            OperandToken token = new OperandToken(
                    context.getConstantsRegistry().get(numstr).toString());
            return token;
        } else {
            logger.trace("constant '{}' is not rgistered", numstr);
            try {
                return new OperandToken(
                        Double.valueOf(numstr.toString()).toString());
            } catch (NumberFormatException nfex) {
                throw new IllegalArgumentException("", nfex);
            }
        }
    }

    /**
     * Gets the value.
     *
     * @return the value
     */
    public BigDecimal getValue() {
        return value;
    }

    /**
     * To string.
     *
     * @return the string
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("OperandToken [value=");
        builder.append(value);
        builder.append("]");
        return builder.toString();
    }

}
