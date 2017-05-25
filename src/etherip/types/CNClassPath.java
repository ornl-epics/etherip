/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package etherip.types;

import java.nio.ByteBuffer;

/** Control Net Path for class, instance, attribute
 *
 *  <p>Example (with suitable static import):
 *  <p><code>CNPath path = Identity.instance(1).attr(7)</code>
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class CNClassPath extends CNPath
{
	final private int class_code;
	final private String class_name;
	private int instance = 1, attr = 0;

	protected CNClassPath(final int class_code, final String class_name)
	{
		this.class_code = class_code;
		this.class_name = class_name;
	}

	public CNClassPath instance(final int instance)
	{
		this.instance = instance;
		return this;
	}

	public CNPath attr(final int attr)
	{
		this.attr = attr;
		return this;
	}

	/** @return Path length in words */
    public byte getPathLength()
	{
    	return attr == 0 ? (byte)2 : (byte)3;
	}

	@Override
    public int getRequestSize()
    {   // Convert words into bytes
	    return getPathLength() * 2;
    }

    /** {@inheritDoc} */
	@Override
    public void encode(final ByteBuffer buf, final StringBuilder log)
	{
		buf.put(getPathLength());
		buf.put((byte) 0x20);
		buf.put((byte) class_code);
		buf.put((byte) 0x24);
		buf.put((byte) instance);
		if (attr > 0)
		{
			buf.put((byte) 0x30);
			buf.put((byte) attr);
		}
	}

	@Override
    public String toString()
	{
		if (attr > 0)
			return String.format("Path (3 el) Class(0x20) 0x%X (%s), instance(0x24) %d, attrib.(0x30) 0x%X",
	                   class_code, class_name, instance, attr);
		return String.format("Path (2 el) Class(0x20) 0x%X (%s), instance(0x24) %d",
				class_code, class_name, instance);
	}
}
