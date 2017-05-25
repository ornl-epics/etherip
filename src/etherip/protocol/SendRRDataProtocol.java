/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package etherip.protocol;

import java.nio.ByteBuffer;

/** SendRRData, the unconnected Request/Response protocol
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class SendRRDataProtocol extends ProtocolAdapter
{
	/** Byte size of encapsulation header */
	final public static int RR_DATA_HEADER_SIZE = 16;

	final private Protocol body;

	/** Initialize
	 *  @param body Protocol to place in body of RRData
	 */
    public SendRRDataProtocol(final Protocol body)
    {
    	this.body = body;
    }

	/** {@inheritDoc} */
    @Override
    public int getRequestSize()
    {
	    return RR_DATA_HEADER_SIZE + body.getRequestSize();
    }

	/** {@inheritDoc} */
    @Override
    public void encode(final ByteBuffer buf, final StringBuilder log) throws Exception
    {
    	final short addr_type = 0;
    	final short data_type = 0xB2;
    	buf.putInt(0);
    	buf.putShort((short)0);
    	buf.putShort((short)2);
    	buf.putShort(addr_type);
    	buf.putShort((short)0);
    	buf.putShort(data_type);
    	buf.putShort((short) body.getRequestSize());
    	if (log != null)
        {
    		log.append("Send RR Data\n");
    		log.append("UDINT interface handle  : 0\n");
    		log.append("UINT timeout            : 0\n");
    		log.append("UINT count (addr., data): 2\n");
    		log.append(String.format("UINT address type       : 0x%X (%s)\n", addr_type, decodeCPT(addr_type)));
    		log.append("UINT address length     : 0x00\n");
    		log.append(String.format("UINT data type          : 0x%X (%s)\n", data_type, decodeCPT(data_type)));
    		log.append("UINT data length        : ").append((short) body.getRequestSize()).append("\n");
        }

    	body.encode(buf, log);
    }

    /** {@inheritDoc} */
    @Override
    public void decode(final ByteBuffer buf, final int available, final StringBuilder log) throws Exception
    {
    	final int iface = buf.getInt();
    	if (iface != 0)
    		throw new Exception("Received interface " + iface + " instead of 0");
    	final short timeout = buf.getShort();
    	final short count = buf.getShort();
    	if (count != 2)
    		throw new Exception("Received count " + count + " instead of 2");
    	final short addr_type = buf.getShort();
    	final short addr_length = buf.getShort();
    	final short data_type = buf.getShort();
    	final short received_data_length = buf.getShort();

    	//  Followed by data...
    	if (log != null)
        {
        	log.append("Received RR Data\n");
        	log.append("UDINT interface handle  : ").append(iface).append("\n");
        	log.append("UINT timeout            : ").append(timeout).append("\n");
        	log.append("UINT count (addr., data): ").append(count).append("\n");
        	log.append(String.format("UINT address type       : 0x%X (%s)\n", addr_type, decodeCPT(addr_type)));
        	log.append("UINT address length     : ").append(addr_length).append("\n");
        	log.append(String.format("UINT data type          : 0x%X (%s)\n", data_type, decodeCPT(data_type)));
        	log.append("UINT data length        : ").append(received_data_length).append("\n");
        }

    	body.decode(buf, received_data_length, log);
    }

    /** Decode IDs for "Common Packet Type" (address and data IDs)
     *  <p>Spec, 8.9.1
     */
    private static String decodeCPT(final short id)
    {
        switch (id)
        {
            case (short)0x0000: return "UCMM";
            case (short)0x00A1: return "connection based";
            case (short)0x8000: return "sockaddr, orig->tgt.";
            case (short)0x8001: return "sockaddr, tgt.->orig";
            case (short)0x8002: return "sequenced address";
            case (short)0x00B1: return "Connected PDU";
            case (short)0x00B2: return "Unconnected Message";
        }
        return "<unknown>";
    }
}
