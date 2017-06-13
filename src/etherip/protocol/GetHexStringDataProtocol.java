package etherip.protocol;

import java.nio.ByteBuffer;

public class GetHexStringDataProtocol extends ProtocolAdapter
{
	private String value;

	@Override
	public void decode(final ByteBuffer buf, final int available, final StringBuilder log) throws Exception
	{
		final int len = buf.remaining();
		final byte[] raw = new byte[len];
		buf.get(raw);
		this.value = "";

		for (final byte item : raw)
		{
			this.value += String.format("%02X ", item);
		}

		if (log != null)
		{
			log.append("Raw value      :\n").append(this.value).append("\n");
		}

		// Skip remaining bytes
		final int rest = available - len;
		if (rest > 0)
		{
			buf.position(buf.position() + rest);
		}
	}

	final public String getValue()
	{
		return this.value;
	}
}
