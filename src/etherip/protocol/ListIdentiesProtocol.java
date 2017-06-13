package etherip.protocol;

import java.nio.ByteBuffer;

import etherip.data.Identity;

public class ListIdentiesProtocol extends ProtocolAdapter
{
	private Identity[] identities;

	@Override
	public void decode(final ByteBuffer buf, final int available, final StringBuilder log) throws Exception
	{
		// Decode reply to ListIdentity
		final int count = buf.getShort();
		if (log != null)
		{
			log.append("ListIdentities Reply\n");
			log.append("UINT count      : ").append(count).append("\n");
		}

		this.identities = new Identity[count];
		for (int i = 0; i < count; ++i)
		{
			final byte[] raw = new byte[22];
			buf.get(raw);

			final GetIdentityProtocol getIdentityProtocol = new GetIdentityProtocol();
			getIdentityProtocol.decode(buf, buf.remaining(), log);

			final Identity identity = getIdentityProtocol.getValue();
			if (log != null)
			{
				log.append("Identity ").append(i).append(":\n").append(identity.toString()).append("\n");
			}
			this.identities[i] = identity;
		}
	}

	/** @return {@link Service}s supported by device */
	final public Identity[] getIdentities()
	{
		return this.identities;
	}
}
