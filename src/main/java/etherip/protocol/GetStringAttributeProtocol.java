/*******************************************************************************
 * Copyright (c) 2012 UT-Battelle, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package etherip.protocol;

import java.nio.ByteBuffer;

/**
 * Decode a <code>String</code> attribute
 *
 * @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class GetStringAttributeProtocol extends ProtocolAdapter
{
    private String value;

    /** {@inheritDoc} */
    @Override
    public void decode(final ByteBuffer buf, final int available,
            final StringBuilder log) throws Exception
    {
        final int len = buf.get() & 0x7F;
        if (len > available - 1)
        {
            throw new Exception(
                    "String length of " + len + " exceeds remaining data ("
                            + (available - 1) + " bytes)");
        }
        final byte[] raw = new byte[len];
        buf.get(raw);
        this.value = new String(raw);
        if (log != null)
        {
            log.append("String value      : ").append(this.value).append("\n");
        }

        // Skip remaining bytes
        final int rest = available - 1 - len;
        if (rest > 0)
        {
            buf.position(buf.position() + rest);
        }
    }

    /** @return Value read from response */
    final public String getValue()
    {
        return this.value;
    }
}
