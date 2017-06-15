/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package etherip.types;

import etherip.protocol.Protocol;

/**
 * Control Net Path
 * <p>
 * Example (with suitable static import):
 * <p>
 * <code>CNPath path = Identity().instance(1).attr(7)</code>
 *
 * @author Kay Kasemir, László Pataki
 */
@SuppressWarnings("nls")
abstract public class CNPath implements Protocol
{
    // Objects, see Spec 10 p. 1, 13, 25
    /**
     * Create path to Identity object
     *
     * @return {@link CNClassPath}
     */
    public static CNClassPath Identity()
    {
        return new CNClassPath(0x01, "Identity");
    }

    /**
     * Create path to MessageRouter object
     *
     * @return {@link CNClassPath}
     */
    public static CNClassPath MessageRouter()
    {
        return new CNClassPath(0x02, "MessageRouter");
    }

    public static CNClassPath ConnectionObject()
    {
        return new CNClassPath(0x05, "ConnectionObject");
    }

    /**
     * Create path to ConnectionManager object
     *
     * @return {@link CNPath}
     */
    public static CNPath ConnectionManager()
    {
        return new CNClassPath(0x06, "ConnectionManager");
    }

    public static CNClassPath Port()
    {
        return new CNClassPath(0xf4, "Port");
    }

    public static CNClassPath TcpIpInterface()
    {
        return new CNClassPath(0xf5, "TCP/IP Interface");
    }

    public static CNClassPath EthernetLink()
    {
        return new CNClassPath(0xf6, "Ethernet Link");
    }

    /**
     * Create symbol path
     *
     * @param name
     *            Name of the tag to put into symbol path
     * @return {@link CNPath}
     */
    public static CNPath Symbol(final String name)
    {
        return new CNSymbolPath(name);
    }
}
