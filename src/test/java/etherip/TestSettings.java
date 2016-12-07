/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package etherip;

import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.util.Properties;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import etherip.protocol.Connection;

/** @author Kay Kasemir */
public class TestSettings
{
    final private static Properties settings = new Properties();
    
    static
    {
        try
        {
            settings.load(ClassLoader.getSystemResourceAsStream("test.properties"));
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
    
    public static String get(final String name)
    {
        return settings.getProperty(name, "");
    }

    public static int getInt(final String name)
    {
        return Integer.parseInt(settings.getProperty(name, "0"));
    }
    
    /** Configure logging */
	public static void logAll()
    {
		Logger logger = Logger.getLogger("");
		logger.setLevel(Level.FINEST);
		for (Handler handler : logger.getHandlers())
			handler.setLevel(Level.FINEST);
    }
	
	/** @return {@link ByteBuffer} suitable for tests */
	public static ByteBuffer getBuffer()
	{
		final ByteBuffer buf = ByteBuffer.allocate(100);
		buf.order(Connection.BYTE_ORDER);
		return buf;
	}
}
