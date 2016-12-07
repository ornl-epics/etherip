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

/** Protocol body for {@link CNService#CIP_WriteData}
 *
 *  @author Kay Kasemir
 */
public class CIPWriteDataProtocol extends ProtocolAdapter
{
    final private CIPData data;
    
    public CIPWriteDataProtocol(final CIPData data)
    {
        this.data = data;
    }

    @Override
    public int getRequestSize()
    {
        return data.getEncodedSize();
    }

    @Override
    public void encode(final ByteBuffer buf, final StringBuilder log)
    {
        data.encode(buf);
        if (log != null)
            log.append("USINT type, data        : ").append(data).append("\n");
    }
}
