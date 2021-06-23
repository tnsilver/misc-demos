/*
 * File: module-info.java
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
/**
 * The module postfix.calculator contains a reversed polish notation
 * ({@code RPN}) known as 'postfix' RPNCalculator.
 * <p>
 * The RPNCalculator {@link org.silvermania.rpn.postfix.calculator.RPNCalculator}
 * accepts a space separated postfix postfix and evaluates it left to right.
 * <p>
 * The basic algorithm is as follows:
 *
 * <pre>
 * 1) Create a stack to store operands (or values).
 * 2) Scan the given postfix and do following for every scanned element.
 *   .a) If the element is a number, push it into the stack
 *   .b) If the element is a operator, pop operands for the operator from stack. Evaluate the operator and push the result back to the stack
 * 3) When the postfix is ended, the number in the stack is the final answer
 * </pre>
 *
 * @author T.N.Silverman
 */
module postfix.calculator {

    requires transitive infix.converter;
    exports org.silvermania.rpn.postfix.api;
    exports org.silvermania.rpn.postfix.calculator;
}
