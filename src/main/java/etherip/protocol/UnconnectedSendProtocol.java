/*******************************************************************************
 * Copyright (c) 2012 UT-Battelle, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package etherip.protocol;

import static etherip.types.CNPath.ConnectionManager;
import static etherip.types.CNService.CM_Unconnected_Send;

import java.nio.ByteBuffer;

/**
 * Protocol PDU that uses CM_Unconnected_Send
 *
 * @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class UnconnectedSendProtocol extends ProtocolAdapter
{
    final private ProtocolEncoder encoder;
    final private int slot;
    final private Protocol body;

    /**
     * Initialize
     *
     * @param slot
     *            Slot (0, 1, ...) of controller module in crate
     * @param body
     *            Embedded protocol for read/write
     */
    public UnconnectedSendProtocol(final int slot, final Protocol body)
    {
        this.encoder = new MessageRouterProtocol(CM_Unconnected_Send,
                ConnectionManager(), new ProtocolAdapter());
        this.slot = slot;
        this.body = body;
    }

    /** {@inheritDoc} */
    @Override
    public int getRequestSize()
    {
        return this.encoder.getRequestSize() + 4 + this.body.getRequestSize()
                + (this.needPad() ? 1 : 0) + 4;
    }

    /** {@inheritDoc} */
    @Override
    public void encode(final ByteBuffer buf, final StringBuilder log)
            throws Exception
    {
        this.encoder.encode(buf, log);

        final byte tick_time = (byte) 10;
        final byte ticks = (byte) 240;
        buf.put(tick_time);
        buf.put(ticks);
        final short body_size = (short) this.body.getRequestSize();
        buf.putShort(body_size);

        final boolean pad = this.needPad();

        if (log != null)
        {
            log.append("CM_Unconnected_Send\n");
            log.append("USINT tick_time         : ").append(tick_time)
                    .append("\n");
            log.append("USINT ticks             : ").append(ticks).append("\n");
            log.append("UINT message size       : ")
                    .append(this.body.getRequestSize()).append("\n");
            log.append("  \\/\\/\\/ embedded message \\/\\/\\/ (")
                    .append(body_size).append(" bytes)\n");
        }

        this.body.encode(buf, log);
        if (pad)
        {
            buf.put((byte) 0);
        }

        buf.put((byte) 1); // Path size
        buf.put((byte) 0); // reserved
        buf.put((byte) 1); // Port 1 = backplane
        buf.put((byte) this.slot);
        if (log != null)
        {
            log.append("  /\\/\\/\\ embedded message /\\/\\/\\\n");
            if (pad)
            {
                log.append(
                        "USINT pad               : 0 (odd length message)\n");
            }
            log.append("USINT path size         : ").append(1)
                    .append(" words\n");
            log.append("USINT reserved          : 0\n");
            log.append("USINT port 1, slot ").append(this.slot).append("\n");
        }
    }

    /** {@inheritDoc} */
    @Override
    public void decode(final ByteBuffer buf, final int available,
            final StringBuilder log) throws Exception
    {
        // CM_Unconnected_Send wraps a request.
        // The response then arrives without CM_Unconnected_Send wrapper,
        // so pass decoding on to the body.
        this.body.decode(buf, available, log);
    }

    /** @return Is path of odd length, requiring a pad byte? */
    private boolean needPad()
    {
        // Findbugs: x%2==1 fails for negative numbers
        return (this.body.getRequestSize() % 2) != 0;
    }
}
