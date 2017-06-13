package etherip.data;

/**
 * Wrapper for Interface Configuration
 * @see CIP_vol2_v1.4_5-3.2.2
 * @author László Pataki
 *
 */
public class InterfaceConfiguration
{
	// Response Example:
	// Get Attribute Single (Response) (Interface Configuration)
	// IP Address: 192.168.166.1
	// Subnet Mask: 255.255.240.0
	// Gateway: 192.168.175.254
	// Name Server: 0.0.0.0
	// Name Server2: 0.0.0.0
	// Domain Name:

	protected String ipAddress, subnetMask, gateway, nameServer, nameServer2, domainName;

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
		return "InterfaceConfiguration [ipAddress=" + ipAddress + ", subnetMask=" + subnetMask + ", gateway=" + gateway + ", nameServer=" + nameServer
				+ ", nameServer2=" + nameServer2 + ", domainName=" + domainName + "]";
	}

}
