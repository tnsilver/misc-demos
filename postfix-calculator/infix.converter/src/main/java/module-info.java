/*
 * File: module-info.java Creation Date: Jun 12, 2019
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
 * License for the specific language governing permissions and limitations under
 * the License.
 */
/**
 * The module infix.converter contains the main class
 * {@link org.silvermania.rpn.infix.converter.InfixConverter} which converts
 * infix arithmetic expressions to reversed polish notation (a.k.a postfix).
 * <p>
 * In essence, parsing an infix expression from left to right involves resolving
 * the tokens one by one, and instantiating corresponding operators, operands,
 * functions, function argument separators and opening or closing brackets and
 * parentheses.
 * <p>
 * A regular infix arithmetic expression such as <b>3 + 4</b> is represented in
 * postfix as <b>3 4 +</b>
 * <p>
 * A more elaborate infix expression such as <b>3 + 4 × 2 ÷ ( 1 − 5 ) ^ 2 ^
 * 3</b> becomes: <b>3 4 2 × 1 5 − 2 3 ^ ^ ÷ +</b> (where the operator ^ is
 * 'exponent' or 'in the power of')
 * <p>
 * This module contains the converter which accepts an input {@code infix}
 * expression and transforms it to a {@code postfix} expression
 *
 * @author T.N.Silverman
 */
module infix.converter {

    requires transitive rpn.support;

    exports org.silvermania.rpn.infix.converter;
}
