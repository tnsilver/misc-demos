/*
 * File: Main.java
 * Creation Date: Jul 6, 2019
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
package org.silvermania.calculator.example;

import java.math.BigDecimal;

import org.silvermania.rpn.postfix.calculator.RPNCalculator;
import org.silvermania.rpn.support.CalculationContext;

/**
 * The Class Main is a simple demonstration of the {@link RPNCalculator} usage
 *
 * @author T.N.Silveman
 */
public class Main {

    /**
     * The main method demonstrates basic usage of the RPN Calculator
     *
     * @param args the arguments (not used in this case)
     */
    public static void main(String[] args) {
        // BigDecimal area = RPNCalculator.accept("π 12 * 2 pow").thenCalculate(); // calculates 1421.2230305
        // BigDecimal area = RPNCalculator.convert("pow(π*12,2)").thenCalculate(); // converts to postfix "π 12 * 2 pow" and calculates 1421.2230305
        CalculationContext context = CalculationContext.newInstance();
        context.addVariable("r",12.0); // register a variable 'r' with Double value 12
        BigDecimal area = RPNCalculator.withContext(context).convert("pow(π*r,2)").doPrint().thenCalculate(); // converts to postfix "π r * 2 pow" and calculates 1421.2230305
        System.out.format("area: %s%n",area);
    }

}
