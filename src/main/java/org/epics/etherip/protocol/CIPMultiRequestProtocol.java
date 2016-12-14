/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.epics.etherip.protocol;

import java.nio.ByteBuffer;

import org.epics.etherip.types.CNService;

/** Protocol for {@link CNService#CIP_MultiRequest}
 *  
 *  <p>Handles several embedded {@link MessageRouterProtocol}
 *  read or write requests.
 *
 *  @author Kay Kasemir
 */
public class CIPMultiRequestProtocol extends ProtocolAdapter
{
    final private MessageRouterProtocol[] services;
    
    public CIPMultiRequestProtocol(final MessageRouterProtocol... services)
    {
        this.services = services;
    }

    @Override
    public int getRequestSize()
    {
        // Size: 'count' + offset to each service
        int total = 2 + 2 * services.length;
        // ..plus bytes of each service itself
        for (ProtocolAdapter service : services)
            total += service.getRequestSize();
        return total;
    }

    @Override
    public void encode(final ByteBuffer buf, final StringBuilder log)
    {
        final int start = buf.position();
        
        final short count = (short) services.length;
        // Encode service count
        buf.putShort(count);
        if (log != null)
            log.append("UINT count              : ").append(count).append("\n");
        
        // Encode offsets to individual requests
        // Offset to 1st item:
        // 2 bytes for 'count', 2 bytes for each offset
        short offset = (short) (2 + 2 * count);
        for (int i=0; i<count; ++i)
        {
            buf.putShort(offset);
            if (log != null)
                log.append("UINT offset             : ").append(offset).append("\n");
            // Next offset: After bytes for this request
            offset += services[i].getRequestSize();
        }
        
        for (int i=0; i<count; ++i)
        {
            if (log != null)
                log.append("    \\/\\/ request ").append(i+1).append(" \\/\\/ (offset ").append(buf.position() - start).append(" bytes)\n");
            services[i].encode(buf, log);
            if (log != null)
                log.append("    /\\/\\ request ").append(i+1).append(" /\\/\\\n");
        }
    }

    @Override
    public int getResponseSize(final ByteBuffer buf) throws Exception
    {
        int total = 0;
        for (ProtocolAdapter service : services)
            total += service.getResponseSize(null);
        return total;
    }

    @Override
    public void decode(final ByteBuffer buf, final int available, final StringBuilder log)
            throws Exception
    {
        final int start = buf.position();

        // Count
        final short count = buf.getShort();
        if (log != null)
            log.append("UINT count              : ").append(count).append("\n");

        // Offset table
        final short[] offset = new short[count];
        for (int i=0; i<count; ++i)
        {
            offset[i] = buf.getShort();
            if (log != null)
                log.append("UINT offset             : ").append(offset[i]).append("\n");
        }
        
        // Individual replies
        for (int i=0; i<count; ++i)
        {   // Track buffer offset from start
            final int off = buf.position() - start;
            if (off != offset[i])
                throw new Exception("Expected response #" + (i+1) + " at offset " + offset[i] + ", not " + off);
            
            // Determine length of this section
            final int section_length;
            if (i < count-1)
                section_length = offset[i+1] - off;     // .. from offset table
            else
                section_length = available - offset[i]; // .. from distance to end for last section
        
            if (log != null)
                log.append("    \\/\\/ response ").append(i+1).append(" \\/\\/ (offset ").append(off).append(" bytes)\n");
            services[i].decode(buf, section_length, log);
            if (log != null)
                log.append("    /\\/\\ response ").append(i+1).append(" /\\/\\\n");
        }
    }
}
