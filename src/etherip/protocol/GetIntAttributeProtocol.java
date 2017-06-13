package etherip.protocol;

import java.nio.ByteBuffer;

public class GetIntAttributeProtocol extends ProtocolAdapter
{
	private int value;

	/** {@inheritDoc} */
	@Override
	public void decode(final ByteBuffer buf, final int available, final StringBuilder log) throws Exception
	{
		this.value = buf.getInt();

		if (log != null)
		{
			log.append("ULONG value      : ").append(this.value).append("\n");
		}
	}

	/** @return Value read from response */
	final public int getValue()
	{
		return this.value;
	}
}
