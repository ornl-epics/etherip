/*******************************************************************************
 * Copyright (c) 2012-2024 UT-Battelle, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package etherip.protocol;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import etherip.EtherNetIP;
import etherip.TestSettings;
import etherip.protocol.ListServicesProtocol.Service;

/**
 * JUnit demo of {@link ListServices}
 *
 * @author Kay Kasemir, László Pataki
 */
@SuppressWarnings("nls")
public class ListServicesDemo
{
    @Test
    public void testListServices() throws Exception
    {
        TestSettings.logAll();

        try (EtherNetIP etherNetIP = new EtherNetIP(TestSettings.get("plc"),
                TestSettings.getInt("slot"));)
        {
            etherNetIP.connectTcp();

            final Service[] services = etherNetIP.listServices();
            assertTrue(services != null);

            // In principle, multiple services could be supported.
            // So far have only seen this one
            assertEquals(1, services.length);
            assertEquals("Communications..", services[0].getName());
        }
    }
}
