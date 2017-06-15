/*******************************************************************************
 * Copyright (c) 2017 NETvisor Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package etherip.data;

/**
 * Get_Connection_Data Service Response
 *
 * @see CIP_VOL2_3.3: Table 3-5.27 Get_Connection_Data Service Response
 * @author László Pataki
 */
public class ConnectionData
{
    private byte connectionGeneralStatus, connectionAdditionalStatus;

    int connectionNumber, originatorPort, targetPort, connectionSerialNumber;

    public byte getConnectionAdditionalStatus()
    {
        return this.connectionAdditionalStatus;
    }

    public void setConnectionAdditionalStatus(
            final byte connectionAdditionalState)
    {
        this.connectionAdditionalStatus = connectionAdditionalState;
    }

    public int getConnectionNumber()
    {
        return this.connectionNumber;
    }

    public void setConnectionNumber(final int connectionNumber)
    {
        this.connectionNumber = connectionNumber;
    }

    public byte getConnectionGeneralStatus()
    {
        return this.connectionGeneralStatus;
    }

    public void setConnectionGeneralStatus(final byte connectionState)
    {
        this.connectionGeneralStatus = connectionState;
    }

    public int getOriginatorPort()
    {
        return this.originatorPort;
    }

    public void setOriginatorPort(final int originatorPort)
    {
        this.originatorPort = originatorPort;
    }

    public int getTargetPort()
    {
        return this.targetPort;
    }

    public void setTargetPort(final int targetPort)
    {
        this.targetPort = targetPort;
    }

    public int getConnectionSerialNumber()
    {
        return this.connectionSerialNumber;
    }

    public void setConnectionSerialNumber(final int connectionSerialNumber)
    {
        this.connectionSerialNumber = connectionSerialNumber;
    }

    @Override
    public String toString()
    {
        return "ConnectionData [connectionGeneralStatus="
                + this.connectionGeneralStatus + ", connectionAdditionalStatus="
                + this.connectionAdditionalStatus + ", connectionNumber="
                + this.connectionNumber + ", originatorPort="
                + this.originatorPort + ", targetPort=" + this.targetPort
                + ", connectionSerialNumber=" + this.connectionSerialNumber
                + "]";
    }
}
