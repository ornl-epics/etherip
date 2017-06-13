package etherip.protocol;

import static etherip.types.CNPath.ConnectionManager;

import java.nio.ByteBuffer;

import etherip.types.CNService;

/**
 * @see CIP Vol1_3.3: 3-5.5.5: Get_Connection_Data Service
 * @author László Pataki
 */
public class ConnectionDataProtocol extends ProtocolAdapter
{
	final private ProtocolEncoder encoder;

	final private Protocol body;

	public ConnectionDataProtocol(final Protocol body)
	{
		this.encoder = new MessageRouterProtocol(CNService.Get_Connection_Data, ConnectionManager(), new ProtocolAdapter());
		this.body = body;
	}

	/** {@inheritDoc} */
	@Override
	public int getRequestSize()
	{
		return this.encoder.getRequestSize() + 2;
	}

	/** {@inheritDoc} 
	 * @throws Exception */
	@Override
	public void encode(final ByteBuffer buf, final StringBuilder log) throws Exception
	{
		this.encoder.encode(buf, log);

		final short connectionNumber = (short) 1;
		buf.putShort(connectionNumber);
	}

	/** {@inheritDoc} */
	@Override
	public void decode(final ByteBuffer buf, final int available, final StringBuilder log) throws Exception
	{
		this.body.decode(buf, available, log);
	}
}
