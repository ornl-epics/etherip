package etherip.data;

/**
 * @see CIP Vol2_1.4: 5-4.3.2.1
 * @author László Pataki 
 */
public class InterfaceFlags
{

	boolean isActiveLink, isFullDuplex, manualSettingRequiresReset, localHardwareFault;

	short negotiationStatus;

	public boolean isActiveLink()
	{
		return this.isActiveLink;
	}

	public void setActiveLink(final boolean isActiveLink)
	{
		this.isActiveLink = isActiveLink;
	}

	public boolean isFullDuplex()
	{
		return this.isFullDuplex;
	}

	public void setFullDuplex(final boolean isFullDuplex)
	{
		this.isFullDuplex = isFullDuplex;
	}

	public boolean isManualSettingRequiresReset()
	{
		return this.manualSettingRequiresReset;
	}

	public void setManualSettingRequiresReset(final boolean manualSettingRequiresReset)
	{
		this.manualSettingRequiresReset = manualSettingRequiresReset;
	}

	public boolean isLocalHardwareFault()
	{
		return this.localHardwareFault;
	}

	public void setLocalHardwareFault(final boolean localHardwareFault)
	{
		this.localHardwareFault = localHardwareFault;
	}

	public short getNegotiationStatusRaw()
	{
		return this.negotiationStatus;
	}

	public String getNegotiationStatus() throws Exception
	{
		switch (this.negotiationStatus)
		{
		case 0x0:
			return "Auto-negotiation in progress.";
		case 0x1:
			return "Auto-negotiation and speed detection failed. Using default values for speed and duplex. Default values are product-dependent; recommended defaults are 10Mbps and half duplex.";
		case 0x2:
			return "Auto negotiation failed but detected speed. Duplex was defaulted. Default value is product-dependent; recommended default is half duplex.";
		case 0x3:
			return "Successfully negotiated speed and duplex.";
		case 0x4:
			return "Auto-negotiation not attempted. Forced speed and duplex.";
		default:
			throw new Exception("Unknown Negotiation Status in InterfaceFlags class");
		}
	}

	public void setNegotiationStatus(final short negotiationStatus)
	{
		this.negotiationStatus = negotiationStatus;
	}

    @Override
    public String toString()
    {
        return "InterfaceFlags [isActiveLink=" + isActiveLink + ", isFullDuplex=" + isFullDuplex + ", manualSettingRequiresReset="
                + manualSettingRequiresReset + ", localHardwareFault=" + localHardwareFault + ", negotiationStatus=" + negotiationStatus + "]";
    }
}
