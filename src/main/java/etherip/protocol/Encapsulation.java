/*******************************************************************************
 * Copyright (c) 2012-2024 UT-Battelle, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package etherip.protocol;

import java.nio.ByteBuffer;
import java.util.Arrays;

import etherip.util.Hexdump;

/**
 * Encapsulation commands
 * <p>
 * Spec 4 p. 164
 *
 * @author Kay Kasemir, László Pataki
 */
@SuppressWarnings("nls")
public class Encapsulation implements Protocol
{
    /** Byte size of encapsulation header */
    final public static int ENCAPSULATION_HEADER_SIZE = 24;

    /**
     * Encapsulation commands
     * <p>
     * Spec 4 p. 164
     */
    public enum Command
    {
        Nop(0x0000), /* "Encapsulation Overview" slides */
        ListServices(0x0004),
        ListIdentity(0x0063),
        ListInterfaces(0x0064),
        RegisterSession(0x0065),
        UnRegisterSession(0x0066),
        SendRRData(0x006F),
        SendUnitData(0x0070),
        IndicateStatus(0x0072),
        Cancel(0x0073); /* "Encapsulation Overview" slides */

        public static Command forCode(final short code)
        {
            for (final Command command : values())
            {
                if (command.code() == code)
                {
                    return command;
                }
            }
            return null;
        }

        final private short code;

        private Command(final int code)
        {
            this.code = (short) code;
        }

        public short code()
        {
            return this.code;
        }

        @Override
        public String toString()
        {
            return this.name() + String.format(" (0x%04X)", this.code);
        }
    };

    private final byte[] context = Transaction.format(Transaction.nextTransaction());
    final private Command command;
    private int session;
    final private Protocol body;

    public Encapsulation(final Command command, final int session,
            final Protocol body)
    {
        this.command = command;
        this.session = session;
        this.body = body;
    }

    /** {@inheritDoc} */
    @Override
    public int getRequestSize()
    {
        return ENCAPSULATION_HEADER_SIZE + this.body.getRequestSize();
    }

    /** {@inheritDoc} */
    @Override
    public void encode(final ByteBuffer buf, final StringBuilder log)
            throws Exception
    {
        final int status = 0;
        final int options = 0;

        buf.putShort(this.command.code());
        buf.putShort((short) this.body.getRequestSize());
        buf.putInt(this.session);
        buf.putInt(status);
        buf.put(context);
        buf.putInt(options);

        if (log != null)
        {
            log.append("Encapsulation Header\n");
            log.append("UINT  command           : ").append(this.command)
                    .append("\n");
            log.append("UINT  length            : ")
                    .append(this.body.getRequestSize()).append("\n");
            log.append(String.format("UDINT session           : 0x%08X\n",
                    this.session));
            log.append(String.format("UDINT status            : 0x%08X\n",
                    status));
            log.append("USINT context[8]        : '")
                    .append(Hexdump.escapeChars(context)).append("'\n");
            log.append(String.format("UDINT options           : 0x%08X\n",
                    options));
        }

        this.body.encode(buf, log);
    }

    /** {@inheritDoc} */
    @Override
    public int getResponseSize(final ByteBuffer buf) throws Exception
    {
        // Need at least the encapsulation header
        final int needed = 24 - buf.position();
        if (needed > 0)
        {
            return needed;
        }

        // Buffer contains header (and maybe more after that)
        final short command_code = buf.getShort(0);
        final Command command = Command.forCode(command_code);
        if (command == null)
        {
            throw new Exception(
                    "Received unknown command code " + command_code);
        }
        if (command != this.command)
        {
            throw new Exception("Received command " + command + " instead of "
                    + this.command);
        }

        final short body_size = buf.getShort(2);

        return 24 + body_size;
    }

    /** {@inheritDoc} */
    @Override
    public void decode(final ByteBuffer buf, final int available,
            final StringBuilder log) throws Exception
    {
        // Start decoding
        final short command_code = buf.getShort();
        final Command command = Command.forCode(command_code);
        if (command == null)
        {
            throw new Exception(
                    "Received unknown command code " + command_code);
        }
        if (command != this.command)
        {
            throw new Exception("Received command " + command + " instead of "
                    + this.command);
        }

        final short body_size = buf.getShort();

        final int session = buf.getInt();
        if (session != this.session)
        {
            // If we did not have a session, remember the newly obtained session
            if (this.session == 0)
            {
                this.session = session;
            }
            else
            {
                throw new Exception("Received session " + session
                        + " instead of " + this.session);
            }
        }

        final int status = buf.getInt();

        final byte[] recvd_context = new byte[8];
        buf.get(recvd_context);
        if (!Arrays.equals(recvd_context, context))
            throw new Exception("Received context " + Hexdump.toAscii(recvd_context) + ", expected " + Hexdump.toAscii(context));

        final int options = buf.getInt();

        if (log != null)
        {
            log.append("Encapsulation Header\n");
            log.append("UINT  command           : ").append(command)
                    .append("\n");
            log.append("UINT  length            : ").append(body_size)
                    .append("\n");
            log.append(String.format("UDINT session           : 0x%08X\n",
                    session));
            log.append(String.format("UDINT status            : 0x%08X (%s)\n",
                    status, this.getStatusMessage(status)));
            log.append("USINT context[8]        : '")
                    .append(Hexdump.escapeChars(context)).append("'\n");
            log.append(String.format("UDINT options           : 0x%08X\n",
                    options));
        }

        if (status != 0)
        {
            throw new Exception(String.format("Received status 0x%08X (%s)\n",
                    status, this.getStatusMessage(status)));
        }
        if (buf.remaining() < body_size)
        {
            throw new Exception("Need " + body_size + " more bytes, have "
                    + buf.remaining());
        }

        this.body.decode(buf, body_size, log);
    }

    private String getStatusMessage(final int status)
    {
        switch (status)
        {
        case 0x00:
            return "OK";
        case 0x01:
            return "invalid/unsupported command";
        case 0x02:
            return "no memory on target";
        case 0x03:
            return "malformed data in request";
        case 0x64:
            return "invalid session ID";
        case 0x65:
            return "invalid data length";
        case 0x69:
            return "unsupported protocol revision";
        }
        return "<unknown>";
    }

    /** @return Session ID */
    final public int getSession()
    {
        return this.session;
    }
}
