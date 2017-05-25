/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package etherip.protocol;

import java.nio.ByteBuffer;

/** Register a session
 *
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class RegisterSessionProtocol extends ProtocolAdapter
{
	@Override
	public int getRequestSize()
	{
		return 4;
	}

	@Override
	public void encode(final ByteBuffer buf, final StringBuilder log)
	{
		buf.putShort((short) 1); // protocol
		buf.putShort((short) 0); // options
		if (log != null)
		{
			log.append("USINT protocol  : 1\n");
			log.append("USINT options   : 0\n");
		}
	}
}
