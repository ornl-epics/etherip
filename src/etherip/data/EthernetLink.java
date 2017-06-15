/*******************************************************************************
 * Copyright (c) 2017 NETvisor Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package etherip.data;

/**
 * Ethernet Link Object
 *
 * @see CIP_Vol1_3.3: 5-4 Ethernet Link Object
 * @author László Pataki
 */
public class EthernetLink
{
    Integer interfaceSpeed;

    InterfaceFlags interfaceFlags;

    String physicalAddress, interfaceCounters, mediaCounters, interfaceControl;

    public EthernetLink()
    {
        this.interfaceSpeed = null;
        this.interfaceFlags = null;
        this.physicalAddress = null;
        this.interfaceCounters = null;
        this.mediaCounters = null;
        this.interfaceControl = null;
    }

    public Integer getInterfaceSpeed()
    {
        return this.interfaceSpeed;
    }

    public void setInterfaceSpeed(final Integer interfaceSpeed)
    {
        this.interfaceSpeed = interfaceSpeed;
    }

    public InterfaceFlags getInterfaceFlags()
    {
        return this.interfaceFlags;
    }

    public void setInterfaceFlags(final InterfaceFlags interfaceFlags)
    {
        this.interfaceFlags = interfaceFlags;
    }

    public String getPhysicalAddress()
    {
        return this.physicalAddress;
    }

    public void setPhysicalAddress(final String physicalAddress)
    {
        this.physicalAddress = physicalAddress;
    }

    public String getInterfaceCountersRaw()
    {
        return this.interfaceCounters;
    }

    public void setInterfaceCounters(final String interfaceCountersRaw)
    {
        this.interfaceCounters = interfaceCountersRaw;
    }

    public String getMediaCountersRaw()
    {
        return this.mediaCounters;
    }

    public void setMediaCountersRaw(final String mediaCounters)
    {
        this.mediaCounters = mediaCounters;
    }

    public String getInterfaceControlRaw()
    {
        return this.interfaceControl;
    }

    public void setInterfaceControl(final String interfaceControlRaw)
    {
        this.interfaceControl = interfaceControlRaw;
    }

    @Override
    public String toString()
    {
        return "EthernetLink [interfaceSpeed=" + this.interfaceSpeed
                + ", interfaceFlags=" + this.interfaceFlags
                + ", physicalAddress=" + this.physicalAddress
                + ", interfaceCounters=" + this.interfaceCounters
                + ", mediaCounters=" + this.mediaCounters
                + ", interfaceControl=" + this.interfaceControl + "]";
    }

}
