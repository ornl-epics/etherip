/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package etherip;

/** Device info
 * 
 *  <p>Read when connecting to the device.
 *  @author Kay Kasemir
 */
public class DeviceInfo
{
	final private short vendor;
	final private short device_type;
	final private short revision;
	final private short serial;
	final private String name;

	public DeviceInfo(final short vendor, final short device_type, final short revision,
			final short serial, final String name)
    {
	    this.vendor = vendor;
	    this.device_type = device_type;
	    this.revision = revision;
	    this.serial = serial;
	    this.name = name;
    }

	@Override
    public String toString()
    {
	    return String
	            .format("Device vendor=0x%X, device_type=0x%X, revision=0x%X, serial=0x%X, name='%s'",
	                    vendor, device_type, revision, serial, name);
    }
}
