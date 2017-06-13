package etherip.protocol;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.logging.Logger;

import etherip.data.InterfaceConfiguration;

public class GetInterfaceConfigurationProtocol extends ProtocolAdapter
{
	private InterfaceConfiguration interfaceConfiguration;

	final public static Logger log = Logger.getLogger(GetInterfaceConfigurationProtocol.class.getName());

	private int responseSize;

	public GetInterfaceConfigurationProtocol()
	{
		this.responseSize = 0;
	}

	@Override
	public void decode(final ByteBuffer buf, final int available, final StringBuilder oldLog) throws Exception
	{
		responseSize = buf.position();
		this.interfaceConfiguration = new InterfaceConfiguration();

		byte[] raw = new byte[4];

		buf.get(raw);
		this.interfaceConfiguration.setIpAddress(new Integer(raw[3] & 0xFF) + "." + new Integer(raw[2] & 0xFF & 0xFF) + "."
				+ new Integer(raw[1] & 0xFF) + "." + new Integer(raw[0] & 0xFF));

		buf.get(raw);
		this.interfaceConfiguration.setSubnetMask(new Integer(raw[3] & 0xFF) + "." + new Integer(raw[2] & 0xFF) + "." + new Integer(raw[1] & 0xFF)
				+ "." + new Integer(raw[0] & 0xFF));

		buf.get(raw);
		this.interfaceConfiguration.setGateway(new Integer(raw[3] & 0xFF) + "." + new Integer(raw[2] & 0xFF) + "." + new Integer(raw[1] & 0xFF) + "."
				+ new Integer(raw[0] & 0xFF));

		buf.get(raw);
		this.interfaceConfiguration.setNameServer(new Integer(raw[3] & 0xFF) + "." + new Integer(raw[2] & 0xFF) + "." + new Integer(raw[1] & 0xFF)
				+ "." + new Integer(raw[0] & 0xFF));

		buf.get(raw);
		this.interfaceConfiguration.setNameServer2(new Integer(raw[3] & 0xFF) + "." + new Integer(raw[2] & 0xFF) + "." + new Integer(raw[1] & 0xFF)
				+ "." + new Integer(raw[0] & 0xFF));

		raw = new byte[2];
		short domainNameLength = 0;

		buf.get(raw);
		domainNameLength = ByteBuffer.wrap(raw).order(ByteOrder.LITTLE_ENDIAN).getShort();
		if (domainNameLength % 2 != 0)
		{
			domainNameLength++;
		}
		raw = new byte[domainNameLength];
		buf.get(raw);
		this.interfaceConfiguration.setDomainName(new String(raw));

		responseSize -= buf.position();
		
		log.finest("InterfaceConfiguration value      : " + this.interfaceConfiguration);
	}

	/** @return Value read from response */
	final public InterfaceConfiguration getValue()
	{
		return this.interfaceConfiguration;
	}

	@Override
	public int getResponseSize(final ByteBuffer buf) throws Exception
	{
		return responseSize;
	}
}
