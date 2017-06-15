/*******************************************************************************
 * Copyright (c) 2017 NETvisor Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package etherip.data;

import java.util.Arrays;

/**
 * Identity Object.
 * <p>
 * Status parameter is not decoded.<br>
 * Example for decoding:<br>
 * Status: 0x0060<br>
 * .... .... .... ...0 = Owned: 0<br>
 * .... .... .... .0.. = Configured: 0<br>
 * .... .... 0110 .... = Extended Device Status: 0x6<br>
 * .... ...0 .... .... = Minor Recoverable Fault: 0<br>
 * .... ..0. .... .... = Minor Unrecoverable Fault: 0<br>
 * .... .0.. .... .... = Major Recoverable Fault: 0<br>
 * .... 0... .... .... = Major Unrecoverable Fault: 0<br>
 * 0000 .... .... .... = Extended Device Status 2: 0x0<br>
 *
 * @see CIP_VOL1-3.3: 5-2 Identity Object
 * @author László Pataki
 */
public class Identity
{
    private Integer vendorId, deviceType, productCode;

    private Integer[] revision;

    private String productName, serialNumber, status;

    public Identity()
    {
        this.vendorId = null;
        this.deviceType = null;
        this.productCode = null;
        this.revision = new Integer[] { null, null };
        this.status = null;
        this.serialNumber = null;
        this.productName = null;
    }

    public int getVendorId()
    {
        return this.vendorId;
    }

    public void setVendorId(final int vendorId)
    {
        this.vendorId = vendorId;
    }

    public int getDeviceTypeRaw()
    {
        return this.deviceType;
    }

    public void setDeviceType(final int deviceType)
    {
        this.deviceType = deviceType;
    }

    public int getProductCode()
    {
        return this.productCode;
    }

    public void setProductCode(final int productCode)
    {
        this.productCode = productCode;
    }

    public Integer[] getRevision()
    {
        return this.revision;
    }

    public void setRevision(final Integer[] revision)
    {
        this.revision = revision;
    }

    public int getMajorRevision()
    {
        return this.revision[0];
    }

    public int getMinorRevision()
    {
        return this.revision[1];
    }

    public String getStatusRaw()
    {
        return this.status;
    }

    public void setStatus(final String status)
    {
        this.status = status;
    }

    public String getSerialNumberRaw()
    {
        return this.serialNumber;
    }

    public void setSerialNumber(final String serialNumber)
    {
        this.serialNumber = serialNumber;
    }

    public String getProductName()
    {
        return this.productName;
    }

    public void setProductName(final String productName)
    {
        this.productName = productName;
    }

    @Override
    public String toString()
    {
        return "Identity [vendorId=" + this.vendorId + ", deviceType="
                + this.deviceType + ", productCode=" + this.productCode
                + ", revision=" + Arrays.toString(this.revision)
                + ", productName=" + this.productName + ", serialNumber="
                + this.serialNumber + ", status=" + this.status + "]";
    }

}
