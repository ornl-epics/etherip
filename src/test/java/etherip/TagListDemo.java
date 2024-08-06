/*******************************************************************************
 * Copyright (c) 2012-2024 UT-Battelle, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package etherip;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import etherip.protocol.Connection;
import etherip.protocol.RegisterSession;
import etherip.protocol.TcpConnection;

/** @author Kay Kasemir */
@SuppressWarnings("nls")
public class TagListDemo
{
    @BeforeEach
    public void setup()
    {
        TestSettings.logAll();
    }

    @Test
    public void testTagList() throws Exception
    {
        Logger.getLogger("").setLevel(Level.FINE);
        try (final Connection connection = new TcpConnection(
                TestSettings.get("plc"), TestSettings.getInt("slot"));)
        {
            final RegisterSession register = new RegisterSession();
            connection.execute(register);
            connection.setSession(register.getSession());

            final TagList tags = new TagList();
            // Initial read
            tags.add(TestSettings.get("float_tag"));
            tags.add(TestSettings.get("string_tag"));
            tags.process(connection);

            // Write a tag
            tags.get(TestSettings.get("float_tag")).setWriteValue(0, 17);
            tags.process(connection);

            // Read
            tags.process(connection);

            // Write again
            tags.get(TestSettings.get("float_tag")).setWriteValue(0, 42);
            tags.process(connection);

            // Read
            tags.process(connection);
        }
    }
}
