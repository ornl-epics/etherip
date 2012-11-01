/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package etherip;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Before;
import org.junit.Test;

import etherip.protocol.Connection;
import etherip.protocol.RegisterSession;

public class TagListDemo
{
	@Before
	public void setup()
	{		
		TestSettings.logAll();
	}

	@Test
	public void testTagList() throws Exception
	{
	    Logger.getLogger("").setLevel(Level.FINE);
	    try
	    (
            final Connection connection = new Connection(TestSettings.get("plc"), TestSettings.getInt("slot"));
        )
		{
            final RegisterSession register = new RegisterSession();
            connection.execute(register);
            connection.setSession(register.getSession());
            
		    final TagList tags = new TagList();
		    // Initial read
		    tags.add(TestSettings.get("tag1"));
		    tags.add(TestSettings.get("tag2"));
		    tags.process(connection);
		    
		    // Write a tag
		    tags.get(TestSettings.get("tag2")).setWriteValue(0, 17);
            tags.process(connection);

            // Read
            tags.process(connection);

            // Write again
            tags.get(TestSettings.get("tag2")).setWriteValue(0, 42);
            tags.process(connection);

            // Read
            tags.process(connection);
		}
	}
}
