package etherip.protocol;

import java.nio.ByteBuffer;

import etherip.data.EthernetLink;
import etherip.data.InterfaceFlags;

/**
 * 	
 * (non-Javadoc)
 * @see CIP VOL2_1.4: 5-4.3.2 
 * <p>Table 5-4.4 Interface Flags Reserved: Shall be set to zero
 * @author László Pataki
 *
 */
public class GetEthernetLinkProtocol extends ProtocolAdapter
{
	private EthernetLink ethernetLink;

	@Override
	public void decode(final ByteBuffer buf, final int available, final StringBuilder log) throws Exception
	{
		this.ethernetLink = new EthernetLink();

		byte[] raw = new byte[4];

		buf.get(raw);
		this.ethernetLink.setInterfaceSpeed(new Integer(raw[3] & 0xFF) + new Integer(raw[2] & 0xFF) + new Integer(raw[1] & 0xFF)
				+ new Integer(raw[0] & 0xFF));

		buf.get(raw);
		final InterfaceFlags interfaceFlags = new InterfaceFlags();
		interfaceFlags.setActiveLink(((raw[0] >> 0) & 0x1) == 1);
		interfaceFlags.setFullDuplex(((raw[0] >> 1) & 0x1) == 1);
		interfaceFlags.setNegotiationStatus((short) ((raw[0] >> 2) & 0x7));
		interfaceFlags.setManualSettingRequiresReset(((raw[0] >> 5) & 0x1) == 1);
		interfaceFlags.setLocalHardwareFault(((raw[0] >> 6) & 0x1) == 1);
		// Reserved: Shall be set to zero
		this.ethernetLink.setInterfaceFlags(interfaceFlags);

		raw = new byte[6];
		buf.get(raw);
		this.ethernetLink.setPhysicalAddress(String.format("%02X", raw[0]) + String.format(":%02X", raw[1]) + String.format(":%02X", raw[2])
				+ String.format(":%02X", raw[3]) + String.format(":%02X", raw[4]) + String.format(":%02X", raw[5]));

		if (log != null)
		{
			log.append("EthernetLink value      : " + this.ethernetLink);
		}

		// Skip remaining bytes
		final int rest = available - 4 - 4 - 6;
		if (rest > 0)
		{
			buf.position(buf.position() + rest);
		}
	}

	/** @return Value read from response */
	final public EthernetLink getValue()
	{
		return this.ethernetLink;
	}
}
