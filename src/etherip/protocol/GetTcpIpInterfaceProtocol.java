/*******************************************************************************
 * Copyright (c) 2017 NETvisor Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package etherip.protocol;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.logging.Logger;

import etherip.data.TcpIpInterface;
import etherip.types.CNClassPath;

/**
 * TCP/IP Interface Object
 *
 * @see CIP_vol2_v1.4 5-3.2.2
 * @author László Pataki
 */
public class GetTcpIpInterfaceProtocol extends ProtocolAdapter
{
    private TcpIpInterface tcpIpnterface;

    final public static Logger log = Logger
            .getLogger(GetTcpIpInterfaceProtocol.class.getName());

    @Override
    public void decode(final ByteBuffer buf, int available,
            final StringBuilder oldLog) throws Exception
    {
        this.tcpIpnterface = new TcpIpInterface();
        short hostNameLength;
        byte[] raw;

        raw = new byte[4];
        buf.get(raw);
        this.tcpIpnterface.setStatus(
                ByteBuffer.wrap(raw).order(ByteOrder.LITTLE_ENDIAN).getInt());
        available -= 4;

        buf.get(raw);
        this.tcpIpnterface.setConfigurationCapability(
                ByteBuffer.wrap(raw).order(ByteOrder.LITTLE_ENDIAN).getInt());
        available -= 4;

        buf.get(raw);
        this.tcpIpnterface.setConfigurationControl(
                ByteBuffer.wrap(raw).order(ByteOrder.LITTLE_ENDIAN).getInt());
        available -= 4;

        final CNClassPath physicalLinkObject = new CNClassPath();
        physicalLinkObject.decode(buf, available, oldLog);
        this.tcpIpnterface.setPhysicalLinkObject(physicalLinkObject);
        available -= this.tcpIpnterface.getPhysicalLinkObject()
                .getResponseSize(buf);

        final GetInterfaceConfigurationProtocol getInterfaceConfigurationProtocol = new GetInterfaceConfigurationProtocol();
        getInterfaceConfigurationProtocol.decode(buf, available, oldLog);
        this.tcpIpnterface.setInterfaceConfiguration(
                getInterfaceConfigurationProtocol.getValue());
        available -= getInterfaceConfigurationProtocol.getResponseSize(buf);

        raw = new byte[2];
        buf.get(raw);
        hostNameLength = ByteBuffer.wrap(raw).order(ByteOrder.LITTLE_ENDIAN)
                .getShort();
        raw = new byte[hostNameLength];
        buf.get(raw);
        this.tcpIpnterface.setHostName(new String(raw));

        log.finest("TcpIpInterface value      : " + this.tcpIpnterface);

        // Skip remaining bytes
        final int rest = available - 1 - 2 - hostNameLength;
        if (rest > 0)
        {
            buf.position(buf.position() + rest);
        }
    }

    /** @return Value read from response */
    final public TcpIpInterface getValue()
    {
        return this.tcpIpnterface;
    }
}
