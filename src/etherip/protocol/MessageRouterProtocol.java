/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package etherip.protocol;

import java.nio.ByteBuffer;

import etherip.data.CipException;
import etherip.types.CNPath;
import etherip.types.CNService;

/**
 * Message Router PDU (Protocol Data Unit)
 *
 * @author Kay Kasemir, László Pataki
 */
public class MessageRouterProtocol extends ProtocolAdapter
{
    final private CNService service;

    final private CNPath path;

    final protected Protocol body;

    private int status = 0;

    private int[] ext_status = new int[0];

	private boolean partialTransfert = false;;

    /**
     * Initialize
     *
     * @param service
     *            Service for request
     * @param path
     *            Path for request
     * @param body
     *            Protocol embedded in the message request/response
     */
    public MessageRouterProtocol(final CNService service, final CNPath path,
            final Protocol body)
    {
        this.service = service;
        this.path = path;
        this.body = body;
    }

    /** {@inheritDoc} */
    @Override
    public int getRequestSize()
    {
        return 2 + this.path.getRequestSize() + this.body.getRequestSize();
    }

    /**
     * {@inheritDoc}
     *
     * @throws Exception
     */
    @Override
    public void encode(final ByteBuffer buf, final StringBuilder log)
            throws Exception
    {
        buf.put(this.service.getCode());
        this.path.encode(buf, log);
        if (log != null)
        {
            log.append("MR Request\n");
            log.append("USINT service           : ").append(this.service)
                    .append("\n");
            log.append("USINT path              : ").append(this.path)
                    .append("\n");
        }
        this.body.encode(buf, log);
    }

    /** {@inheritDoc} */
    @Override
    public int getResponseSize(final ByteBuffer buf) throws Exception
    {
        throw new IllegalStateException("Unknown response size");
    }

    /** {@inheritDoc} */
    @Override
    public void decode(final ByteBuffer buf, final int available,
            final StringBuilder log) throws Exception
    {
        final byte service_code = buf.get();
        final CNService reply = CNService.forCode(service_code);
        if (reply == null)
        {
            throw new Exception("Received reply with unknown service code 0x"
                    + Integer.toHexString(service_code));
        }
        if (!reply.isReply())
        {
            throw new Exception("Expected reply, got " + reply);
        }

        final int reserved = buf.get();
        this.status = buf.get();
        final int ext_status_size = buf.get();
        this.ext_status = new int[ext_status_size];
        for (int i = 0; i < ext_status_size; ++i)
        {
            this.ext_status[i] = buf.getShort();
        }

        // Followed by data...
        if (log != null)
        {
            log.append("MR Response\n");
            log.append("USINT service           : ").append(reply).append("\n");
            log.append("USINT reserved          : 0x")
                    .append(Integer.toHexString(reserved)).append("\n");
            log.append("USINT status            : 0x")
                    .append(Integer.toHexString(this.status)).append(" (")
                    .append(")\n");
            log.append("USINT ext. stat. size   : 0x")
                    .append(Integer.toHexString(ext_status_size)).append("\n");
            for (final int ext : this.ext_status)
            {
                log.append("USINT ext status        : 0x")
                        .append(Integer.toHexString(ext)).append(" (")
                        .append(")\n");
            }
        }
        final CNService expected_reply = this.service.getReply();
        if (this.status != 0)
        {
            if (this.status == 6) { // Not an error, we need to ask for remaining
            	this.partialTransfert = true;
            }
            else {
	            if (ext_status_size > 0)
	            {
	                throw new CipException(this.status, this.ext_status[0]);
	            }
	            else
	            {
	                throw new CipException(this.status, 0);
	            }
            }
        }

        if (expected_reply != null && expected_reply != reply)
        {
            throw new Exception(
                    "Expected " + expected_reply + ", got " + reply);
        }

        this.body.decode(buf, available - 4 - 2 * ext_status_size, log);
    }
    
    public boolean isPartialTransfert() {
    	return partialTransfert;
    }

    /** @return Status code of response */
    public int getStatus()
    {
        return this.status;
    }

    /** @return Extended status codes of response */
    public int[] getExtendedStatus()
    {
        return this.ext_status;
    }
}
