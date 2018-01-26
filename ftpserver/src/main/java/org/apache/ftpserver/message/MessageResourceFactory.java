/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.ftpserver.message;

import org.apache.ftpserver.message.impl.BaseMessageResource;

/**
 * Factory for creating message resource implementation
 *
 * @author <a href="http://mina.apache.org">Apache MINA Project</a>
 */
public class MessageResourceFactory {

    private MessageResource messageResource;

    /**
     * Get an {@link MessageResource} from this factory
     *
     * @return The {@link MessageResource} instance
     */
    public MessageResource getMessageResource() {
        if (messageResource == null) {
            messageResource = createMessageResource();
        }
        return messageResource;
    }

    /**
     * Set an custom {@link MessageResource}
     *
     * @param messageResource The {@link MessageResource} instance
     */
    public void setMessageResource(MessageResource messageResource) {
        this.messageResource = messageResource;
    }

    /**
     * Create an {@link MessageResource} based on the configuration on this factory
     *
     * @return The {@link MessageResource} instance
     */
    private MessageResource createMessageResource() {
        return new BaseMessageResource();
    }
}
