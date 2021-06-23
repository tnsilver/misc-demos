/*
 * File: AbstractConfigurerImpl.java
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
package org.silvermania.rpn.postfix.support;

import org.silvermania.rpn.postfix.api.CalculatorState;
import org.silvermania.rpn.postfix.api.Configurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class AbstractConfigurerImpl is a base class for all
 * {@link org.silvermania.rpn.postfix.api.Configurer} implementing sub-classes.
 * It contains a {@link CalculatorState} object with a reference to the various
 * objects that are required to perform an assignment of a {@code infix} or
 * {@code postix} expressions, evaluate them using a default or a supplied
 * {@link org.silvermania.rpn.support.CalculationContext} and calculate the
 * result.
 * <p>
 * The {@code calculatorState} object is transferred via {@code configurer}
 * sub-classes, each augmenting it with user provided parameters.
 *
 * @author T.N.Silverman
 */
public abstract class AbstractConfigurerImpl implements Configurer {

    /** The Constant logger. */
    protected static final Logger logger = LoggerFactory.getLogger(AbstractConfigurerImpl.class);

    /** The calculatorState. */
    private CalculatorState calculatorState;

    /**
     * Instantiates a new AbstractConfigurerImpl.
     *
     * @param calculatorState the internal calculatorState of this configurer
     */
    protected AbstractConfigurerImpl(final CalculatorState calculatorState) {
        super();
        setState(calculatorState);
    }

    /**
     * gets the calculator state.
     *
     * @return the calculator state
     */
    public CalculatorState getState() {
        return calculatorState;
    }

    /**
     * sets the calculator state of this configurer.
     *
     * @param calculatorState the new calculator state of the configurer
     */
    public void setState(CalculatorState calculatorState) {
        this.calculatorState = calculatorState;
    }

}
