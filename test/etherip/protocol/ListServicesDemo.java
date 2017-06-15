/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package etherip.protocol;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

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
            assertThat(services, not(nullValue()));

            // In principle, multiple services could be supported.
            // So far have only seen this one
            assertThat(services.length, equalTo(1));
            assertThat(services[0].getName(), equalTo("Communications.."));
        }
    }
}
