/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */
package org.apache.mina.transport.socket;

import org.apache.mina.core.session.IoSessionConfig;

import java.net.Socket;

/**
 * An {@link IoSessionConfig} for socket transport type.
 *
 * @author <a href="http://mina.apache.org">Apache MINA Project</a>
 */
public interface SocketSessionConfig extends IoSessionConfig {
    /**
     * @return <tt>true</tt> if SO_REUSEADDR is enabled.
     * @see Socket#getReuseAddress()
     */
    boolean isReuseAddress();

    /**
     * @param reuseAddress Tells if SO_REUSEADDR is enabled or disabled
     * @see Socket#setReuseAddress(boolean)
     */
    void setReuseAddress(boolean reuseAddress);

    /**
     * @return the size of the receive buffer
     * @see Socket#getReceiveBufferSize()
     */
    int getReceiveBufferSize();

    /**
     * @param receiveBufferSize The size of the receive buffer
     * @see Socket#setReceiveBufferSize(int)
     */
    void setReceiveBufferSize(int receiveBufferSize);

    /**
     * @return the size of the send buffer
     * @see Socket#getSendBufferSize()
     */
    int getSendBufferSize();

    /**
     * @param sendBufferSize The size of the send buffer
     * @see Socket#setSendBufferSize(int)
     */
    void setSendBufferSize(int sendBufferSize);

    /**
     * @return the traffic class
     * @see Socket#getTrafficClass()
     */
    int getTrafficClass();

    /**
     * @param trafficClass The traffic class to set, one of <tt>IPTOS_LOWCOST</tt> (0x02)
     *                     <tt>IPTOS_RELIABILITY</tt> (0x04), <tt>IPTOS_THROUGHPUT</tt> (0x08) or <tt>IPTOS_LOWDELAY</tt> (0x10)
     * @see Socket#setTrafficClass(int)
     */
    void setTrafficClass(int trafficClass);

    /**
     * @return <tt>true</tt> if <tt>SO_KEEPALIVE</tt> is enabled.
     * @see Socket#getKeepAlive()
     */
    boolean isKeepAlive();

    /**
     * @param keepAlive if <tt>SO_KEEPALIVE</tt> is to be enabled
     * @see Socket#setKeepAlive(boolean)
     */
    void setKeepAlive(boolean keepAlive);

    /**
     * @return <tt>true</tt> if <tt>SO_OOBINLINE</tt> is enabled.
     * @see Socket#getOOBInline()
     */
    boolean isOobInline();

    /**
     * @param oobInline if <tt>SO_OOBINLINE</tt> is to be enabled
     * @see Socket#setOOBInline(boolean)
     */
    void setOobInline(boolean oobInline);

    /**
     * Please note that enabling <tt>SO_LINGER</tt> in Java NIO can result
     * in platform-dependent behavior and unexpected blocking of I/O thread.
     *
     * @return The value for <tt>SO_LINGER</tt>
     * @see Socket#getSoLinger()
     * @see <a href="http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6179351">Sun Bug Database</a>
     */
    int getSoLinger();

    /**
     * Please note that enabling <tt>SO_LINGER</tt> in Java NIO can result
     * in platform-dependent behavior and unexpected blocking of I/O thread.
     *
     * @param soLinger Please specify a negative value to disable <tt>SO_LINGER</tt>.
     * @see Socket#setSoLinger(boolean, int)
     * @see <a href="http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6179351">Sun Bug Database</a>
     */
    void setSoLinger(int soLinger);

    /**
     * @return <tt>true</tt> if <tt>TCP_NODELAY</tt> is enabled.
     * @see Socket#getTcpNoDelay()
     */
    boolean isTcpNoDelay();

    /**
     * @param tcpNoDelay <tt>true</tt> if <tt>TCP_NODELAY</tt> is to be enabled
     * @see Socket#setTcpNoDelay(boolean)
     */
    void setTcpNoDelay(boolean tcpNoDelay);
}
