/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package etherip;

import static etherip.EtherNetIP.logger;
import static etherip.protocol.Encapsulation.Command.SendRRData;
import static etherip.types.CNPath.MessageRouter;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import etherip.Tag.State;
import etherip.protocol.CIPMultiRequestProtocol;
import etherip.protocol.Connection;
import etherip.protocol.Encapsulation;
import etherip.protocol.MRChipReadProtocol;
import etherip.protocol.MRChipWriteProtocol;
import etherip.protocol.MessageRouterProtocol;
import etherip.protocol.SendRRDataProtocol;
import etherip.protocol.UnconnectedSendProtocol;
import etherip.types.CNService;

/** List of {@link Tag}s on the PLC
 *  @author Kay Kasemir
 */
public class TagList
{
    /** {@link Tag}s handled by this {@link TagList}
     *  
     *  <p>SYNC on <code>this</code> for access
     */
    final private List<Tag> tags = new ArrayList<>();

    /** @param name Name of tag to add to list 
     *  @return {@link Tag}
     */
    public synchronized Tag add(final String name)
    {
        final Tag tag = new Tag(name);
        tags.add(tag);
        return tag;
    }
    
    /** Locate tag for name
     *  @param name Tag name
     *  @return {@link Tag}
     *  @throws IllegalArgumentException when tag name not known
     */
    public synchronized Tag get(final String name)
    {
        for (Tag tag : tags)
            if (tag.getName().equals(name))
                return tag;
        throw new IllegalArgumentException("Unknown tag '" + name + "'");
    }

    /** Process tags on list
     * 
     *  <p>Reads most tags and updates their value.
     *  Exception are tags marked for writing, which are written once,
     *  then reset to read-mode.
     * 
     *  @param connection {@link Connection} to use for the communication
     *  @throws Exception on error
     */
    public synchronized void process(final Connection connection) throws Exception
    {
        // Determine which tags are to read and which to write
        final MessageRouterProtocol[] readwrite = new MessageRouterProtocol[tags.size()];
        for (int i=0; i<tags.size(); ++i)
        {
            final Tag tag = tags.get(i);
            synchronized (tag)
            {
                switch (tag.getState())
                {
                case READING:
                    readwrite[i] = new MRChipReadProtocol(tag.getName());
                    break;
                default:
                    readwrite[i] = new MRChipWriteProtocol(tag.getName(), tag.getValue());
                    tag.setState(State.WRITING);
                }
            }
        }
        
        // Perform the protocol exchange
        final Encapsulation encap =
            new Encapsulation(SendRRData, connection.getSession(),
                new SendRRDataProtocol(
                    new UnconnectedSendProtocol(connection.getSlot(),
                        new MessageRouterProtocol(CNService.CIP_MultiRequest, MessageRouter(),
                            new CIPMultiRequestProtocol(readwrite)))));
        connection.execute(encap);
        
        // Handle responses: Fetch data, reset 'write' flags
        for (int i=0; i<tags.size(); ++i)
        {
            final Tag tag = tags.get(i);
            synchronized (tag)
            {
                switch (tag.getState())
                {
                case READING:
                    // Read, update the tag's value
                    final MRChipReadProtocol reader = (MRChipReadProtocol)readwrite[i];
                    tag.setValue(reader.getData());
                    logger.log(Level.FINE, "Read {0}", tag);
                    break;
                case WRITING:
                    // Finished writing, return to reading
                    tag.setState(State.READING);
                    logger.log(Level.FINE, "Wrote {0}", tag);
                    break;
                case TO_BE_WRITTEN:
                    // Received yet another value to be written while writing the
                    // previous one, so keep the TO_BE_WRITTEN state
                    logger.log(Level.FINE, "Wrote {0}, need to write updated value", tag);
                }
            }
        }
    }
    
    @Override
    public String toString()
    {
        return "Tags: " + tags.toString();
    }
}
