/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package etherip.protocol;

import java.nio.ByteBuffer;

import etherip.types.CNPath;
import etherip.types.CNService;

/** Message Router PDU (Protocol Data Unit)
 *  @author Kay Kasemir
 */
public class MessageRouterProtocol extends ProtocolAdapter
{
	final private CNService service;
	final private CNPath path;
	final protected Protocol body;

	private int status = 0;

	private int[] ext_status = new int[0];

	/** Initialize
	 *  @param service Service for request
	 *  @param path Path for request
	 *  @param body Protocol embedded in the message request/response
	 */
    public MessageRouterProtocol(final CNService service, final CNPath path, final Protocol body)
    {
    	this.service = service;
    	this.path = path;
    	this.body = body;
    }

	/** {@inheritDoc} */
    @Override
    public int getRequestSize()
	{
	    return 2 + path.getRequestSize() + body.getRequestSize();
	}

	/** {@inheritDoc} */
    @Override
    public void encode(final ByteBuffer buf, final StringBuilder log)
    {
        buf.put((byte)service.getCode());
        path.encode(buf, log);
        if (log != null)
        {
        	log.append("MR Request\n");
        	log.append("USINT service           : ").append(service).append("\n");
        	log.append("USINT path              : ").append(path).append("\n");
        }
        
        body.encode(buf, log);
    }
    
    /** {@inheritDoc} */
    @Override
    public int getResponseSize(final ByteBuffer buf) throws Exception
    {
    	throw new IllegalStateException("Unknown response size");
    }
    
    /** {@inheritDoc} */
    @Override
    public void decode(final ByteBuffer buf, final int available, final StringBuilder log) throws Exception
    {
    	final byte service_code = buf.get();
    	final CNService reply = CNService.forCode(service_code);
    	if (reply == null)
    		throw new Exception("Received reply with unknown service code 0x" + Integer.toHexString(service_code));
    	if (! reply.isReply())
    		throw new Exception("Expected reply, got " + reply);
    	final CNService expected_reply = service.getReply();
		if (expected_reply != null  &&  expected_reply!= reply)
    		throw new Exception("Expected " + expected_reply + ", got " + reply);
    	
    	final int reserved = buf.get();
    	status = buf.get();
    	final int ext_status_size = buf.get();
		ext_status = new int[ext_status_size];
		for (int i=0; i<ext_status_size; ++i)
			ext_status[i] = buf.getShort();
    	
    	//  Followed by data...
    	if (log != null)
        {
        	log.append("MR Response\n");
        	log.append("USINT service           : ").append(reply).append("\n");
        	log.append("USINT reserved          : 0x").append(Integer.toHexString(reserved)).append("\n");
        	log.append("USINT status            : 0x").append(Integer.toHexString(status)).append(" (").append(decodeStatus()).append(")\n");
        	log.append("USINT ext. stat. size   : 0x").append(Integer.toHexString(ext_status_size)).append("\n");
        	for (int ext : ext_status)
            	log.append("USINT ext status        : 0x").append(Integer.toHexString(ext)).append(" (").append(decodeExtendedStatus(ext)).append(")\n");
        }
    	
    	body.decode(buf, available - 4 - 2*ext_status_size, log);
    }
    
    /** @return Status code of response */
    public int getStatus()
    {
    	return status;
    }
    
    /** @return Status of response */
    public String decodeStatus()
    {
    	// Spec 4, p.46 and 1756-RM005A-EN-E
    	switch (status)
    	{
    	case 0x00:  return "Ok";
    	case 0x01:  return "Extended error";
    	case 0x04:  return "Unknown tag or Path error";
    	case 0x05:  return "Instance not found";
    	case 0x06:  return "Buffer too small, partial data only";
    	case 0x08:  return "Service not supported";
    	case 0x09:  return "Invalid Attribute";
    	case 0x13:  return "Not enough data";
    	case 0x14:  return "Attribute not supported, ext. shows attribute";
    	case 0x15:  return "Too much data";
    	case 0x1E:  return "One of the MultiRequests failed";
    	}
    	return "<unknown>";
    }
    
    /** @return Extended status codes of response */
    public int[] getExtendedStatus()
    {
    	return ext_status;
    }
    
    /** @param ext_status Extended status codes of response to decode
     *  @return Status description
     */
    public String decodeExtendedStatus(final int ext_status)
    {
    	switch (ext_status)
    	{
    	case 0x0107: return "Connection not found";
		case 0x0204: return "Unconnected send timed out, no module in slot?";
		case 0x0312: return "link not found, no module in slot?";
		case 0x0318: return "link to self invalid";
		case 0x2105: return "Access beyond end of object, wrong array index";
		case 0x2107: return "CIP type does not match object type";
		case 0x2104: return "Beginning offset beyond end of template";
    	}
    	return "<unknown>";
    }
}
