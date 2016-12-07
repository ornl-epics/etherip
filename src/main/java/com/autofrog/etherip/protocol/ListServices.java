/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package etherip.protocol;

import etherip.protocol.ListServicesProtocol.Service;

/** List services that the device supports
 *
 *  @author Kay Kasemir
 */
public class ListServices extends Encapsulation
{
    final private ListServicesProtocol list_services;

    public ListServices()
    {
        this(new ListServicesProtocol());
    }

    private ListServices(final ListServicesProtocol list_services)
    {
        super(Command.ListServices, 0, list_services);
        this.list_services = list_services;
    }

    /** @return {@link Service}s supported by device */
	final public Service[] getServices()
	{
		return list_services.getServices();
	}
}
