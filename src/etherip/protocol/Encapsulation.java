/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package etherip.protocol;

import java.nio.ByteBuffer;
import java.util.Arrays;

import etherip.util.Hexdump;


/** Encapsulation commands
 *
 *  <p>Spec 4 p. 164
 *
 *  @author Kay Kasemir
 */
public class Encapsulation implements Protocol
{
	/** Byte size of encapsulation header */
	final public static int ENCAPSULATION_HEADER_SIZE = 24;

	/** Encapsulation commands
	 *
	 *  <p>Spec 4 p. 164
	 *
	 *  @author Kay Kasemir
	 */
	public enum Command
	{
	    Nop(0x0000),
	    ListInterfaces(0x0064),  /* "Encapsulation Overview" slides */
	    RegisterSession(0x0065),
	    UnRegisterSession(0x0066),
	    ListServices(0x0004),
	    SendRRData(0x006F),
	    SendUnitData(0x0070),
	    IndicateStatus(0x0072);   /* "Encapsulation Overview" slides */

	    public static Command forCode(short code)
	    {
	        for (Command command : values())
	            if (command.code() == code)
	                return command;
	        return null;
	    }

	    final private short code;

	    private Command(final int code)
	    {
	        this.code = (short)code;
	    }

	    public short code()
	    {
	        return code;
	    }

	    @Override
	    public String toString()
	    {
	        return name() + String.format(" (0x%04X)", code);
	    }
	};
	
	
    final private Command command;
    private int session;
    final private Protocol body;
    
    final private static byte[] context = new byte[] { 'F', 'u', 'n', 's', 't', 'u', 'f', 'f' };

    public Encapsulation(final Command command, final int session, final Protocol body)
    {
        this.command = command;
        this.session = session;
        this.body = body;
    }

	/** {@inheritDoc} */
    @Override
    public int getRequestSize()
    {
	    return ENCAPSULATION_HEADER_SIZE + body.getRequestSize();
    }

	/** {@inheritDoc} */
    @Override
    public void encode(final ByteBuffer buf, final StringBuilder log)
    {
    	final int status = 0;
        final int options = 0;

        buf.putShort(command.code());
        buf.putShort((short) body.getRequestSize());
        buf.putInt(session);
        buf.putInt(status);
        buf.put(context);
        buf.putInt(options);
        
        if (log != null)
        {
        	log.append("Encapsulation Header\n");
        	log.append("UINT  command           : ").append(command).append("\n");
        	log.append("UINT  length            : ").append(body.getRequestSize()).append("\n");
        	log.append(String.format("UDINT session           : 0x%08X\n", session));
        	log.append(String.format("UDINT status            : 0x%08X\n", status));
        	log.append("USINT context[8]        : '").append(Hexdump.escapeChars(context)).append("'\n");
        	log.append(String.format("UDINT options           : 0x%08X\n", options));
        }
        
        body.encode(buf, log);
    }
    
    /** {@inheritDoc} */
    @Override
    public int getResponseSize(final ByteBuffer buf) throws Exception
    {
    	// Need at least the encapsulation header
    	int needed = 24 - buf.position();
    	if (needed > 0)
    		return needed;

    	// Buffer contains header (and maybe more after that)
    	final short command_code = buf.getShort(0);
		final Command command = Command.forCode(command_code);
    	if (command == null)
    		throw new Exception("Received unknown command code " + command_code);
    	if (command != this.command)
    		throw new Exception("Received command " + command + " instead of " + this.command);
    	
    	final short body_size = buf.getShort(2);
    	
    	return 24 + body_size;
    }
    
    /** {@inheritDoc} */
    @Override
    public void decode(final ByteBuffer buf, final int available, final StringBuilder log) throws Exception
    {
    	// Start decoding
    	final short command_code = buf.getShort();
		final Command command = Command.forCode(command_code);
    	if (command == null)
    		throw new Exception("Received unknown command code " + command_code);
    	if (command != this.command)
    		throw new Exception("Received command " + command + " instead of " + this.command);
    	
    	final short body_size = buf.getShort();
    	
    	final int session = buf.getInt();
    	if (session != this.session)
    	{
    		// If we did not have a session, remember the newly obtained session
    		if (this.session == 0)
    			this.session = session;
    		else // Error: Session changed
    			throw new Exception("Received session " + session + " instead of " + this.session);
    	}

    	final int status = buf.getInt();
    	
    	final byte[] context = new byte[8];
    	buf.get(context);
    	if (!Arrays.equals(context, Encapsulation.context))
    		throw new Exception("Received context " + Hexdump.toAscii(context));
    	
    	final int options = buf.getInt();
    	
        if (log != null)
        {
        	log.append("Encapsulation Header\n");
        	log.append("UINT  command           : ").append(command).append("\n");
        	log.append("UINT  length            : ").append(body_size).append("\n");
        	log.append(String.format("UDINT session           : 0x%08X\n", session));
        	log.append(String.format("UDINT status            : 0x%08X (%s)\n", status, getStatusMessage(status)));
        	log.append("USINT context[8]        : '").append(Hexdump.escapeChars(context)).append("'\n");
        	log.append(String.format("UDINT options           : 0x%08X\n", options));
        }
        
        if (status != 0)
        	throw new Exception(String.format("Received status 0x%08X (%s)\n", status, getStatusMessage(status)));
        if (buf.remaining() < body_size)
        	throw new Exception("Need " + body_size + " more bytes, have " + buf.remaining());
        
        body.decode(buf, body_size, log);
    }
    
    private String getStatusMessage(final int status)
    {
    	switch (status)
    	{
    	case 0x00:  return "OK";
    	case 0x01:  return "invalid/unsupported command";
    	case 0x02:  return "no memory on target";
    	case 0x03:  return "malformed data in request";
    	case 0x64:  return "invalid session ID";
    	case 0x65:  return "invalid data length";
    	case 0x69:  return "unsupported protocol revision";
    	}
    	return "<unknown>";
    }

	/** @return Session ID */
    final public int getSession()
    {
    	return session;
    }
}
