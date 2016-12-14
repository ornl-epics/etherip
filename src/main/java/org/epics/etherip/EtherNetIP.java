/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.epics.etherip;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.epics.etherip.protocol.CIPMultiRequestProtocol;
import org.epics.etherip.protocol.Connection;
import org.epics.etherip.protocol.Encapsulation;
import org.epics.etherip.protocol.GetShortAttributeProtocol;
import org.epics.etherip.protocol.GetStringAttributeProtocol;
import org.epics.etherip.protocol.ListServices;
import org.epics.etherip.protocol.ListServicesProtocol.Service;
import org.epics.etherip.protocol.MRChipReadProtocol;
import org.epics.etherip.protocol.MRChipWriteProtocol;
import org.epics.etherip.protocol.MessageRouterProtocol;
import org.epics.etherip.protocol.ProtocolAdapter;
import org.epics.etherip.protocol.RegisterSession;
import org.epics.etherip.protocol.SendRRDataProtocol;
import org.epics.etherip.protocol.UnconnectedSendProtocol;
import org.epics.etherip.types.CIPData;
import org.epics.etherip.types.CNService;
import org.epics.etherip.types.CNPath;

/** API for communicating via EtherNet/IP
 *  @author Kay Kasemir
 */
public class EtherNetIP implements AutoCloseable
{
    final public static String version = "1.0";
    
	final public static Logger logger = Logger.getLogger(EtherNetIP.class.getName());

	final private String address;
	final private int slot;
	private Connection connection = null;

	private DeviceInfo device_info;

	/** Initialize
	 *  @param address IP address of device
	 */
	public EtherNetIP(final String address, final int slot)
	{
		this.address = address;
		this.slot = slot;
	}
	
	/** Connect to device, register session, obtain basic info
	 *  @throws Exception on error
	 */
	public void connect() throws Exception
	{
		connection = new Connection(address, slot);
		listServices();
		registerSession();
		getDeviceInfo();
	}
	
	/** List supported services
	 *  @throws Exception on error getting services, or when expected service not supported
	 */
	private void listServices() throws Exception
    {
        final ListServices list_services = new ListServices();
		connection.execute(list_services);
		
		final Service[] services = list_services.getServices();
		if (services == null  ||  services.length < 1)
			throw new Exception("Device does not support EtherIP services");
		logger.log(Level.FINE, "Service: {0}", services[0].getName());
		if (! services[0].getName().toLowerCase().startsWith("comm"))
				throw new Exception("Expected EtherIP communication service, got " + services[0].getName());
    }

	/** Register session
	 *  @throws Exception on error
	 */
	private void registerSession() throws Exception
	{
		final RegisterSession register = new RegisterSession();
		connection.execute(register);
		connection.setSession(register.getSession());
	}

	/** Obtain device info
	 *  @throws Exception on error
	 */
	private void getDeviceInfo() throws Exception
    {
		final short vendor = getShortAttribute(1);
		final short device_type = getShortAttribute(2);
		final short revision = getShortAttribute(4);
		final short serial = getShortAttribute(6);
		final String name = getStringAttribute(7);
		device_info = new DeviceInfo(vendor, device_type, revision, serial, name);
		logger.log(Level.INFO, "{0}", device_info);
    }

	/** Helper for reading a 'short' typed attribute
	 *  @param attr Attribute to read from {@link Identity}
	 *  @return value of attribute
	 *  @throws Exception on error
	 */
	private short getShortAttribute(final int attr) throws Exception
    {
		final GetShortAttributeProtocol attr_proto;
		final Encapsulation encap =
			new Encapsulation(Encapsulation.Command.SendRRData, connection.getSession(),
				new SendRRDataProtocol(
					new MessageRouterProtocol(CNService.Get_Attribute_Single, CNPath.Identity().attr(attr),
					    (attr_proto = new GetShortAttributeProtocol()))));
		connection.execute(encap);
	    return attr_proto.getValue();
    }

	/** Helper for reading a 'String' typed attribute
	 *  @param attr Attribute to read from {@link Identity}
	 *  @return value of attribute
	 *  @throws Exception on error
	 */
	private String getStringAttribute(final int attr) throws Exception
    {
		final GetStringAttributeProtocol attr_proto;
		final Encapsulation encap =
				new Encapsulation(Encapsulation.Command.SendRRData, connection.getSession(),
					new SendRRDataProtocol(
						new MessageRouterProtocol(CNService.Get_Attribute_Single, CNPath.Identity().attr(attr),
						    (attr_proto = new GetStringAttributeProtocol()))));
		connection.execute(encap);
	    return attr_proto.getValue();
    }
	
	public CIPData readTag(final String tag) throws Exception
	{
		final MRChipReadProtocol cip_read = new MRChipReadProtocol(tag);
		final Encapsulation encap =
			new Encapsulation(Encapsulation.Command.SendRRData, connection.getSession(),
				new SendRRDataProtocol(
					new UnconnectedSendProtocol(slot,
					    cip_read)));
		connection.execute(encap);
		
		return cip_read.getData();
	}

	public CIPData readTag(final String tag, int count) throws Exception
	{
		final MRChipReadProtocol cip_read = new MRChipReadProtocol(tag, count);
		final Encapsulation encap =
				new Encapsulation(Encapsulation.Command.SendRRData, connection.getSession(),
						new SendRRDataProtocol(
								new UnconnectedSendProtocol(slot,
										cip_read)));
		connection.execute(encap);

		return cip_read.getData();
	}

	
	public CIPData readTags(final String... tags) throws Exception
	{
	    final MRChipReadProtocol[] reads = new MRChipReadProtocol[tags.length];
	    for (int i=0; i<reads.length; ++i)
	        reads[i] = new MRChipReadProtocol(tags[i]);
	            
	    final Encapsulation encap =
            new Encapsulation(Encapsulation.Command.SendRRData, connection.getSession(),
                new SendRRDataProtocol(
                    new UnconnectedSendProtocol(slot,
                        new MessageRouterProtocol(CNService.CIP_MultiRequest, CNPath.MessageRouter(),
                            new CIPMultiRequestProtocol(reads)))));
	    connection.execute(encap);
	        
	    // TODO Nothing decodes the individual responses
	    return null;
    }

	
	public void writeTag(final String tag, final CIPData value) throws Exception
	{
	    final MRChipWriteProtocol cip_write = new MRChipWriteProtocol(tag, value);

	    final Encapsulation encap =
	            new Encapsulation(Encapsulation.Command.SendRRData, connection.getSession(),
                    new SendRRDataProtocol(
	                    new UnconnectedSendProtocol(slot,
	                            cip_write)));
        connection.execute(encap);
    }

	public void writeTags(final String[] tags, final CIPData[] values) throws Exception
    {
	    if (tags.length != values.length)
	        throw new IllegalArgumentException("Got " + tags.length + " tags but " + values.length + " values");
        final MRChipWriteProtocol[] writes = new MRChipWriteProtocol[tags.length];
        for (int i=0; i<tags.length; ++i)
            writes[i] = new MRChipWriteProtocol(tags[i], values[i]);

        final Encapsulation encap =
                new Encapsulation(Encapsulation.Command.SendRRData, connection.getSession(),
                    new SendRRDataProtocol(
                        new UnconnectedSendProtocol(slot,
                                new MessageRouterProtocol(CNService.CIP_MultiRequest, CNPath.MessageRouter(),
                                        new CIPMultiRequestProtocol(writes)))));
        connection.execute(encap);
    }

	/** Unregister session (device will close connection) */
	private void unregisterSession()
	{
		if (connection.getSession() == 0)
			return;
		try
		{
			connection.write(new Encapsulation(Encapsulation.Command.UnRegisterSession, connection.getSession(), new ProtocolAdapter()));
			// Cannot read after this point because PLC will close the connection
		}
		catch (Exception ex)
		{
			logger.log(Level.WARNING, "Error un-registering session", ex);
		}
	}
	
	/** Close connection to device */
	@Override
    public void close() throws Exception
	{
		if (connection != null)
		{
			unregisterSession();
			connection.close();
		}
	}
	
	/** @return String representation for debugging */
	@Override
    public String toString()
	{
		final StringBuilder buf = new StringBuilder();
		buf.append("EtherIP address '").append(address).append("', session 0x").append(Integer.toHexString(connection.getSession())).append("\n");
		buf.append(device_info);
		return buf.toString();
	}
}
