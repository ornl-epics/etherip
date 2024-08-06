/*******************************************************************************
 * Copyright (c) 2012-2024 UT-Battelle, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package etherip.protocol;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

import etherip.TestSettings;

/**
 * JUnitDemo of {@link RegisterSession}
 *
 * @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class RegisterSessionDemo
{
    @Test
    public void testRegisterSession() throws Exception
    {
        TestSettings.logAll();

        try (TcpConnection tcpConnection = new TcpConnection(
                TestSettings.get("plc"), TestSettings.getInt("slot"));)
        {
            final RegisterSession register = new RegisterSession();
            tcpConnection.write(register);

            assertEquals(0, register.getSession());

            tcpConnection.read(register);
            System.out.println("Received session 0x"
                    + Integer.toHexString(register.getSession()));
            assertNotEquals(0, register.getSession());
        }
    }
}
