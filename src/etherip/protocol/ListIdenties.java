/*******************************************************************************
 * Copyright (c) 2017 NETvisor Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package etherip.protocol;

import etherip.data.Identity;

/**
 * List identities of the device
 *
 * @author László Pataki
 */
public class ListIdenties extends Encapsulation
{
    final private ListIdentiesProtocol list_identities;

    public ListIdenties()
    {
        this(new ListIdentiesProtocol());
    }

    private ListIdenties(final ListIdentiesProtocol list_identities)
    {
        super(Command.ListIdentity, 0, list_identities);
        this.list_identities = list_identities;
    }

    final public Identity[] getIdentities()
    {
        return this.list_identities.getIdentities();
    }
}
