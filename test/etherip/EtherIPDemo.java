/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package etherip;

import org.junit.Before;
import org.junit.Test;

import etherip.types.CIPData;

public class EtherIPDemo
{
	@Before
	public void setup()
	{		
		TestSettings.logAll();
	}

	@Test
	public void testEtherIP() throws Exception
	{
	    // Logger.getLogger("").setLevel(Level.INFO);
		try
		(
		    EtherNetIP plc = new EtherNetIP(TestSettings.get("plc"), TestSettings.getInt("slot"));
		)
		{
			plc.connect();
			
			System.out.println("\n*\n* Connected:\n*\n");
			System.out.println(plc);

			System.out.println("\n*\n* Individual read/write:\n*\n");
			CIPData value = plc.readTag(TestSettings.get("tag1"));
			System.out.println(value);
			value.set(0,  47);
			plc.writeTag(TestSettings.get("tag1"), value);

            value = plc.readTag(TestSettings.get("tag1"));
            System.out.println(value);
            
            value.set(0,  3.1416);
            plc.writeTag(TestSettings.get("tag1"), value);
			
            System.out.println("\n*\n* Multi read:\n*\n");
			plc.readTags(TestSettings.get("tag1"), TestSettings.get("tag2"));
		}
	}
}
