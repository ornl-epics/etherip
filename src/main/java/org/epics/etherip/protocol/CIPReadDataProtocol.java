/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.epics.etherip.protocol;

import java.nio.ByteBuffer;

import org.epics.etherip.types.CIPData;
import org.epics.etherip.types.CNService;

/** Protocol body for {@link CNService#CIP_ReadData}
 *
 *  @author Kay Kasemir
 */
public class CIPReadDataProtocol extends ProtocolAdapter
{
    private final int count;
    private CIPData data;

    public CIPReadDataProtocol(int count){
        this.count = count;
    }


    @Override
    public int getRequestSize()
    {
        return 2;
    }

    @Override
    public void encode(final ByteBuffer buf, final StringBuilder log)
    {
        buf.putShort((short) count); // elements
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
