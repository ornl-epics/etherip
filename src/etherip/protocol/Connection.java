/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package etherip.protocol;

import static etherip.EtherNetIP.logger;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.logging.Level;

/** Connection to EtherNet/IP device
 *
 *  <p>Network connection as well as buffer and session info
 *  that's used for the duration of a connection.
 *
 *  @author Kay Kasemir, László Pataki
 */
@SuppressWarnings("nls")
public abstract class Connection implements AutoCloseable
{
	/** EtherIP uses little endian */
	final public static ByteOrder BYTE_ORDER = ByteOrder.LITTLE_ENDIAN;

	final private static int BUFFER_SIZE = 600;

	protected final int slot;

	protected final ByteBuffer buffer;

	private int session = 0;

	protected long timeout_ms = 2000;
	protected int port = 0xAF12;

	/** Initialize
	 *  @param address IP address of device
	 *  @param slot Slot number 0, 1, .. of the controller within PLC crate
	 *  @throws Exception on error
	 */
	public Connection(final String address, final int slot) throws Exception
	{
        logger.log(Level.INFO, "Connecting to {0}:{1}", new Object[] { address, String.format("0x%04X", port) });
	    this.slot = slot;

		buffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
		buffer.order(BYTE_ORDER);
	}

	/** @return Slot number 0, 1, .. of the controller within PLC crate */
	public int getSlot()
    {
        return slot;
    }

    /** @param session Session ID to be identified with this connection */
	public void setSession(final int session)
    {
	    this.session = session;
    }

    /** @return Session ID of this connection */
	public int getSession()
	{
	    return session;
    }

	/** @return {@link ByteBuffer} */
    public ByteBuffer getBuffer()
	{
		return buffer;
	}

	public abstract boolean isOpen() throws Exception;
	
	/** Write protocol data
	 *  @param encoder {@link ProtocolEncoder} used to <code>encode</code> buffer
	 *  @throws Exception on error
	 */
    public abstract void write(final ProtocolEncoder encoder) throws Exception;

	/** Read protocol data
	 *  @param decoder {@link ProtocolDecoder} used to <code>decode</code> buffer
	 *  @throws Exception on error
	 */
    protected abstract void read(final ProtocolDecoder decoder) throws Exception;
    
	/** Write protocol request and handle response
	 *  @param protocol {@link Protocol}
	 *  @throws Exception on error
	 */
	public void execute(final Protocol protocol) throws Exception
    {
		write(protocol);
		read(protocol);
    }

}
