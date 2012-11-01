/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package etherip.protocol;

import static etherip.EtherNetIP.logger;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.logging.Level;

import etherip.util.Hexdump;

/** Connection to EtherNet/IP device
 * 
 *  <p>Network connection as well as buffer and session info
 *  that's used for the duration of a connection.
 *  
 *  @author Kay Kasemir
 */
public class Connection implements AutoCloseable
{
	/** EtherIP uses little endian */
	final public static ByteOrder BYTE_ORDER = ByteOrder.LITTLE_ENDIAN;

	final private static int BUFFER_SIZE = 600;
	
	final private int slot;
	
	final private AsynchronousSocketChannel channel;
	final private ByteBuffer buffer;
	
	private int session = 0;

	private long timeout_ms = 2000;
	private int port = 0xAF12;
	
	/** Initialize
	 *  @param address IP address of device
	 *  @param slot Slot number 0, 1, .. of the controller within PLC crate
	 *  @throws Exception on error
	 */
	public Connection(final String address, final int slot) throws Exception
	{
	    logger.log(Level.INFO, "Connecting to {0}:{1}", new Object[] { address, String.format("0x%04X", port) });
	    this.slot = slot;
        channel = AsynchronousSocketChannel.open();
		channel.connect(new InetSocketAddress(address, port)).get(timeout_ms, MILLISECONDS);
		
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
	
	@Override
	public void close() throws Exception
	{
		channel.close();
	}

	/** Write protocol data
	 *  @param encoder {@link ProtocolEncoder} used to <code>encode</code> buffer
	 *  @throws Exception on error
	 */
	public void write(final ProtocolEncoder encoder) throws Exception
    {
		final StringBuilder log = logger.isLoggable(Level.FINER) ? new StringBuilder() : null;
		buffer.clear();
		encoder.encode(buffer, log);
		if (log != null)
			logger.finer("Protocol Encoding\n" + log.toString());
		
		buffer.flip();
		if (logger.isLoggable(Level.FINEST))
			logger.log(Level.FINEST, "Data sent ({0} bytes):\n{1}",
					new Object[] { buffer.remaining(), Hexdump.toHexdump(buffer) });
		
		int to_write = buffer.limit();
		while (to_write > 0)
		{
			final int written = channel.write(buffer).get(timeout_ms, MILLISECONDS);
			to_write -= written;
			if (to_write > 0)
				buffer.compact();
		}
    }

	/** Read protocol data
	 *  @param decoder {@link ProtocolDecoder} used to <code>decode</code> buffer
	 *  @throws Exception on error
	 */
	public void read(final ProtocolDecoder decoder) throws Exception
    {
		// Read until protocol has enough data to decode
		buffer.clear();
		do
		{
			channel.read(buffer).get(timeout_ms, MILLISECONDS);
		}
		while (buffer.position() < decoder.getResponseSize(buffer));
		
		// Prepare to decode
		buffer.flip();

		if (logger.isLoggable(Level.FINEST))
			logger.log(Level.FINEST, "Data read ({0} bytes):\n{1}",
					new Object[] { buffer.remaining(), Hexdump.toHexdump(buffer) });

		final StringBuilder log = logger.isLoggable(Level.FINER) ? new StringBuilder() : null;
		decoder.decode(buffer, buffer.remaining(), log);
		if (log != null)
			logger.finer("Protocol Decoding\n" + log.toString());
    }

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
