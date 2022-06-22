/*******************************************************************************
 * Copyright (c) 2017 NETvisor Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package etherip.protocol;

import static etherip.EtherNetIP.logger;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;

import etherip.util.Hexdump;

/**
 * Connection to EtherNet/IP device via TCP
 * <p>
 * Network connection as well as buffer and session info that's used for the duration of a connection.
 *
 * @author Kay Kasemir, László Pataki
 */
public class TcpConnection extends Connection
{
    private final AsynchronousSocketChannel channel;

    /**
     * Initialize
     *
     * @param address
     *            IP address of device
     * @param slot
     *            Slot number 0, 1, .. of the controller within PLC crate
     * @throws Exception
     */
    public TcpConnection(final String address, final int slot) throws Exception
    {
        super(address, slot);

        this.channel = AsynchronousSocketChannel.open();
        this.channel.connect(new InetSocketAddress(address, this.port))
                .get(this.timeout_ms, MILLISECONDS);
    }

    /**
     * Write protocol data
     *
     * @param encoder
     *            {@link ProtocolEncoder} used to <code>encode</code> buffer
     * @throws Exception
     *             on error
     */
    @Override
    public void write(final ProtocolEncoder encoder) throws Exception
    {
        final StringBuilder log = logger.isLoggable(Level.FINER)
                ? new StringBuilder() : null;
        this.buffer.clear();
        encoder.encode(this.buffer, log);
        if (log != null)
        {
            logger.finer("Protocol Encoding\n" + log.toString());
        }

        this.buffer.flip();
        if (logger.isLoggable(Level.FINEST))
        {
            logger.log(Level.FINEST, "Data sent ({0} bytes):\n{1}",
                    new Object[] { this.buffer.remaining(),
                            Hexdump.toHexdump(this.buffer) });
        }

        int to_write = this.buffer.limit();
        while (to_write > 0)
        {
            final int written = this.channel.write(this.buffer)
                    .get(this.timeout_ms, MILLISECONDS);
            to_write -= written;
            if (to_write > 0)
            {
                this.buffer.compact();
            }
        }
    }

    /**
     * Read protocol data
     *
     * @param decoder
     *            {@link ProtocolDecoder} used to <code>decode</code> buffer
     * @throws Exception
     *             on error
     */
    @Override
    public void read(final ProtocolDecoder decoder) throws Exception
    {
        // Read until protocol has enough data to decode
        this.buffer.clear();
        long count = 0;
        do
        {
            if (++count > max_retry_count)
                throw new TimeoutException("Message response time out");
            this.channel.read(this.buffer).get(this.timeout_ms, MILLISECONDS);
        }
        while (this.buffer.position() < decoder.getResponseSize(this.buffer));

        // Prepare to decode
        this.buffer.flip();

        if (logger.isLoggable(Level.FINEST))
        {
            logger.log(Level.FINEST, "Data read ({0} bytes):\n{1}",
                    new Object[] { this.buffer.remaining(),
                            Hexdump.toHexdump(this.buffer) });
        }

        final StringBuilder log = logger.isLoggable(Level.FINER)
                ? new StringBuilder() : null;
        try
        {
            decoder.decode(this.buffer, this.buffer.remaining(), log);
        }
        finally
        {   // Show log even on error
            if (log != null)
            {
                logger.finer("Protocol Decoding\n" + log.toString());
            }
        }
    }

    @Override
    public void close() throws Exception
    {
        this.channel.close();
    }

    @Override
    public boolean isOpen() throws Exception
    {
        return this.channel.isOpen();
    }

}