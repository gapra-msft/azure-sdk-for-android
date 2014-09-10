/**
 * 
 * Copyright (c) Microsoft and contributors.  All rights reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * 
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

// Warning: This code was generated by a tool.
// 
// Changes to this file may cause incorrect behavior and will be lost if the
// code is regenerated.

package com.microsoft.azure.management.sql.models;

import com.microsoft.azure.core.OperationResponse;

/**
* Contains the response to the Create Recover Database Operation request.
*/
public class RecoverDatabaseOperationCreateResponse extends OperationResponse {
    private RecoverDatabaseOperation operation;
    
    /**
    * Optional. Gets or sets the operation that has been returned from a Create
    * Recover Database Operation request.
    * @return The Operation value.
    */
    public RecoverDatabaseOperation getOperation() {
        return this.operation;
    }
    
    /**
    * Optional. Gets or sets the operation that has been returned from a Create
    * Recover Database Operation request.
    * @param operationValue The Operation value.
    */
    public void setOperation(final RecoverDatabaseOperation operationValue) {
        this.operation = operationValue;
    }
}