/*******************************************************************************
 * Copyright (c) 2017 NETvisor Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package etherip.data;

/**
 * Interface Configuration parameter of Tcp/Ip Interface Object
 *
 * @see CIP_vol2_v1.4_5-3.2.2
 * @author László Pataki
 */
public class InterfaceConfiguration
{

    protected String ipAddress, subnetMask, gateway, nameServer, nameServer2,
            domainName;

    public InterfaceConfiguration()
    {
        this.ipAddress = null;
        this.subnetMask = null;
        this.gateway = null;
        this.nameServer = null;
        this.nameServer2 = null;
        this.domainName = null;
    }

    public String getIpAddress()
    {
        return this.ipAddress;
    }

    public void setIpAddress(final String ipAddress)
    {
        this.ipAddress = ipAddress;
    }

    public String getSubnetMask()
    {
        return this.subnetMask;
    }

    public void setSubnetMask(final String subnetMask)
    {
        this.subnetMask = subnetMask;
    }

    public String getGateway()
    {
        return this.gateway;
    }

    public void setGateway(final String gateway)
    {
        this.gateway = gateway;
    }

    public String getNameServer()
    {
        return this.nameServer;
    }

    public void setNameServer(final String nameServer)
    {
        this.nameServer = nameServer;
    }

    public String getNameServer2()
    {
        return this.nameServer2;
    }

    public void setNameServer2(final String nameServer2)
    {
        this.nameServer2 = nameServer2;
    }

    public String getDomainName()
    {
        return this.domainName;
    }

    public void setDomainName(final String domainName)
    {
        this.domainName = domainName;
    }

    @Override
    public String toString()
    {
        return "InterfaceConfiguration [ipAddress=" + this.ipAddress
                + ", subnetMask=" + this.subnetMask + ", gateway="
                + this.gateway + ", nameServer=" + this.nameServer
                + ", nameServer2=" + this.nameServer2 + ", domainName="
                + this.domainName + "]";
    }

}
