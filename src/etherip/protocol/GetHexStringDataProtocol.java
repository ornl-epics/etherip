/*******************************************************************************
 * Copyright (c) 2017 NETvisor Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package etherip.protocol;

import java.nio.ByteBuffer;

/**
 * Decode the cip response to a string in HEX code<br>
 * Use this for example to check CIP response on byte level without debugging.
 *
 * @author László Pataki
 */
public class GetHexStringDataProtocol extends ProtocolAdapter
{
    private String value;

    @Override
    public void decode(final ByteBuffer buf, final int available,
            final StringBuilder log) throws Exception
    {
        final int len = buf.remaining();
        final byte[] raw = new byte[len];
        buf.get(raw);
        this.value = "";

        for (final byte item : raw)
        {
            this.value += String.format("%02X ", item);
        }

        if (log != null)
        {
            log.append("Raw value      :\n").append(this.value).append("\n");
        }

        // Skip remaining bytes
        final int rest = available - len;
        if (rest > 0)
        {
            buf.position(buf.position() + rest);
        }
    }

    final public String getValue()
    {
        return this.value;
    }
}
