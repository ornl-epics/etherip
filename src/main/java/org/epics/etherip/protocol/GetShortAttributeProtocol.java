/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.epics.etherip.protocol;

import java.nio.ByteBuffer;

/** Decode a <code>short</code> attribute
 *  @author Kay Kasemir
 */
public class GetShortAttributeProtocol extends ProtocolAdapter
{
	private short value;

	/** {@inheritDoc} */
	@Override
	public void decode(final ByteBuffer buf, final int available, final StringBuilder log) throws Exception
	{
		value = buf.getShort();
		if (log != null)
			log.append("UINT value      : ").append(value).append("\n");
	}
	
	/** @return Value read from response */
	final public short getValue()
	{
		return value;
	}
}
