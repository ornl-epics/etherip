package etherip.data;

import etherip.types.CNClassPath;

/**
 * Wrapper class for TCP/IP Object
 * @see CIP_vol2_v1.4 5-3.2.2
 * @author László Pataki
 *
 */
public class TcpIpInterface
{
	private int status, configurationCapability, configurationControl;

	private String hostName;

	private InterfaceConfiguration interfaceConfiguration;

	private CNClassPath physicalLinkObject;

	public TcpIpInterface()
	{
		this.interfaceConfiguration = new InterfaceConfiguration();
	}

	public void setStatus(final int status)
	{
		this.status = status;
	}

	public void setConfigurationCapability(final int configurationCapability)
	{
		this.configurationCapability = configurationCapability;
	}

	public void setConfigurationControl(final int configurationControl)
	{
		this.configurationControl = configurationControl;
	}

	public void setHostName(final String hostName)
	{
		this.hostName = hostName;
	}

	public InterfaceConfiguration getInterfaceConfiguration()
	{
		return this.interfaceConfiguration;
	}

	public void setInterfaceConfiguration(final InterfaceConfiguration interfaceConfiguration)
	{
		this.interfaceConfiguration = interfaceConfiguration;
	}

	public CNClassPath getPhysicalLinkObject()
	{
		return this.physicalLinkObject;
	}

	public void setPhysicalLinkObject(final CNClassPath cnClassPath)
	{
		this.physicalLinkObject = cnClassPath;
	}

    @Override
    public String toString()
    {
        return "TcpIpInterface [status=" + status + ", configurationCapability=" + configurationCapability + ", configurationControl="
                + configurationControl + ", hostName=" + hostName + ", interfaceConfiguration=" + interfaceConfiguration + ", physicalLinkObject="
                + physicalLinkObject + "]";
    }

}
