/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package etherip.util;

import java.nio.ByteBuffer;

/** Create hex-dump of buffer
 *  
 *  <p>The <code>to...</code> routines return a String in Hex, Ascii or combined hexdump format.
 *  
 *  <p>The plain <code>hex</code>, <code>ascii</code>, <code>hexdump</code> routines
 *  append the hex, ascii or combined info to a buffer.
 *  
 *  <p>The {@link ByteBuffer} will be dumped from its current position to its limit.
 *  The position and limit will not be modified.
 *  
 *  @author Kay Kasemir
 */
public class Hexdump
{
    private static final int ELEMENTS_PER_LINE = 16;

    public static String toHex(final ByteBuffer buffer)
    {
    	final StringBuilder out = new StringBuilder();
    	hex(buffer, out);
    	return out.toString();
    }
    
    public static void hex(final ByteBuffer buffer, final StringBuilder out)
    {
    	final int start = buffer.position();
    	final int end = buffer.limit();
    	for (int pos = 0; pos < end; pos += ELEMENTS_PER_LINE)
    	{
    		out.append(String.format("%04X - ", pos));
    		for (int i=0; i<ELEMENTS_PER_LINE; ++i)
    		{
    			final int idx = start + pos + i;
    			if (idx >= end)
    				out.append("   ");
    			else
    			{
    				final byte b = buffer.get(idx);
    				out.append(String.format("%02X ", b));
    			}
    		}
    		out.append("\n");
    	}
    	buffer.rewind();
    }

    public static String toAscii(final byte... bytes)
    {
    	return toAscii(ByteBuffer.wrap(bytes)); // byte order does not matter, only byte-wise access
    }
    
    public static String toAscii(final ByteBuffer buffer)
    {
    	final StringBuilder out = new StringBuilder();
    	ascii(buffer, out);
    	return out.toString();
    }
    
    public static void ascii(final ByteBuffer buffer, final StringBuilder out)
    {
    	final int start = buffer.position();
    	final int end = buffer.limit();
    	for (int pos = 0; pos < end; pos += ELEMENTS_PER_LINE)
    	{
    		out.append(String.format("%04X - ", pos));
    		for (int i=0; i<ELEMENTS_PER_LINE; ++i)
    		{
    			final int idx = start + pos + i;
    			if (idx >= end)
    				out.append(" ");
    			else
    				out.append(escapeChars(buffer.get(idx)));
    		}
    		out.append("\n");
    	}
    	buffer.rewind();
    }

    public static String toHexdump(final byte... bytes)
    {
    	return toHexdump(ByteBuffer.wrap(bytes)); // byte order does not matter, only byte-wise access
    }
    
    public static String toHexdump(final ByteBuffer buffer)
    {
    	final StringBuilder out = new StringBuilder();
    	hexdump(buffer, out);
    	return out.toString();
    }
    
    public static void hexdump(final ByteBuffer buffer, final StringBuilder out)
    {
    	final int start = buffer.position();
    	final int end = buffer.limit();
    	for (int pos = 0; pos < end; pos += ELEMENTS_PER_LINE)
    	{
    		out.append(String.format("%04X - ", pos));
    		for (int i=0; i<ELEMENTS_PER_LINE; ++i)
    		{
    			final int idx = start + pos + i;
    			if (idx >= end)
    				out.append("   ");
    			else
    			{
    				final byte b = buffer.get(idx);
    				out.append(String.format("%02X ", b));
    			}
    		}
    		out.append("- ");
    		for (int i=0; i<ELEMENTS_PER_LINE; ++i)
    		{
    			final int idx = start + pos + i;
    			if (idx >= end)
    				out.append(" ");
    			else
    				out.append(escapeChars(buffer.get(idx)));
    		}
    		out.append("\n");
    	}
    	buffer.rewind();
    }

    public static String escapeChars(final byte... bytes)
    {
    	final StringBuilder buf = new StringBuilder();
    	for (byte b : bytes)
    		if (b >= 32  &&  b < 127)
    			buf.append(Character.toChars(b));
    		else
    			buf.append('.');
    	return buf.toString();
    }

    public static String toCompactHexdump(final ByteBuffer buffer)
    {
        String string = toHexdump(buffer);
        string = string.replaceAll(" +", " ");
        string = string.replaceAll("\r", "");
        string = string.trim();
        return string;
    }
}
