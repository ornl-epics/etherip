package etherip.data;

import java.util.Arrays;

//CIP_VOL1-3.3: 5-2 Identity Object
//Status example:
//	Status: 0x0060
//    .... .... .... ...0 = Owned: 0
//    .... .... .... .0.. = Configured: 0
//    .... .... 0110 .... = Extended Device Status: 0x6
//    .... ...0 .... .... = Minor Recoverable Fault: 0
//    .... ..0. .... .... = Minor Unrecoverable Fault: 0
//    .... .0.. .... .... = Major Recoverable Fault: 0
//    .... 0... .... .... = Major Unrecoverable Fault: 0
//    0000 .... .... .... = Extended Device Status 2: 0x0

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
		this.revision = new Integer[]
			{null, null};
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
        return "Identity [vendorId=" + vendorId + ", deviceType=" + deviceType + ", productCode=" + productCode + ", revision="
                + Arrays.toString(revision) + ", productName=" + productName + ", serialNumber=" + serialNumber + ", status=" + status + "]";
    }
	
}
