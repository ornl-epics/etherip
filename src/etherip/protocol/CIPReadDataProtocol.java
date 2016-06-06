/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package etherip.protocol;

import java.nio.ByteBuffer;

import etherip.types.CIPData;
import etherip.types.CNService;

/** Protocol body for {@link CNService#CIP_ReadData}
 *
 *  @author Kay Kasemir
 */
public class CIPReadDataProtocol extends ProtocolAdapter
{
    private CIPData data;

    @Override
    public int getRequestSize()
    {
        return 2;
    }

    @Override
    public void encode(final ByteBuffer buf, final StringBuilder log)
    {
        buf.putShort((short) 1); // elements
        if (log != null)
            log.append("USINT elements          : 1\n");
    }

    @Override
    public void decode(final ByteBuffer buf, final int available, final StringBuilder log) throws Exception
    {
        if (available <= 0)
        {
            data = null;
            if (log != null)
                log.append("USINT type, data        : - nothing-\n");
            return;
        }
        final CIPData.Type type = CIPData.Type.forCode(buf.getShort());
        final byte[] raw = new byte[available - 2];
        buf.get(raw);
        data = new CIPData(type, raw);
        if (log != null)
            log.append("USINT type, data        : ").append(data).append("\n");
    }

    final public CIPData getData()
    {
        return data;
    }
}
