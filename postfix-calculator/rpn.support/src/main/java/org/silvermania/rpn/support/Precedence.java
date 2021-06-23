/*
 * File: Precedence.java
 * Creation Date: Jul 5, 2019
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
 * The Enum Precedence represents operators precedence
 *
 * @author T.N.Silverman
 */
public enum Precedence {

    /** lowest precedence for standard operators like '+' and '-' */
    LOWEST,

    /** higher than lowest but low precedence for operators like '*' and '/' */
    LOW,

    /** high precedence for operators like '√' and '!' */
    HIGH,

    /** highest precedence for operators like '^' exponential */
    HIGHEST;

}
