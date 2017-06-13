package etherip.protocol;

import java.nio.ByteBuffer;

import etherip.types.CNClassPath;

public class GetPhysicalLinkObjectProtocol extends CNClassPath
{
    @Override
	public void encode(final ByteBuffer buf, final StringBuilder log)
	{
		// NOP
	}

	@Override
	public int getRequestSize()
	{
		return 0;
	}
}
