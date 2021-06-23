/*
 * File: Associativity.java
 * Creation Date: Jul 3, 2019
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

/**
 * The Enum Associativity represents how the operator relates to the operands. A
 * {@code !} operator or {@code ^}, for example are left associativity operators
 * as they relate to the operator on their left. The operator {@code √} for
 * example, relates to the operand on it's right. so has a right associativity.
 *
 * @author T.N.Silverman
 */
public enum Associativity {
    /** Right associativity. */
    RIGHT,

    /** Left associativity. */
    LEFT;
}
