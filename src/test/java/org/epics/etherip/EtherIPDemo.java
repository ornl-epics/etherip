/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.epics.etherip;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Before;
import org.junit.Test;

import org.epics.etherip.types.CIPData;
import org.epics.etherip.types.CIPData.Type;

/** @author Kay Kasemir */
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
			assertThat(value, not(nullValue()));
			value.set(0,  47);
			plc.writeTag(TestSettings.get("tag1"), value);

            value = plc.readTag(TestSettings.get("tag1"));
            assertThat(value, not(nullValue()));
            System.out.println(value);

            value.set(0,  3.1416);
            plc.writeTag(TestSettings.get("tag1"), value);

            System.out.println("\n*\n* Multi read:\n*\n");
			plc.readTags(TestSettings.get("tag1"), TestSettings.get("tag2"));
		}
	}

    @Test
    public void testBool() throws Exception
    {
        Logger.getLogger("").setLevel(Level.INFO);
        try
        (
            EtherNetIP plc = new EtherNetIP(TestSettings.get("plc"), TestSettings.getInt("slot"));
        )
        {
            plc.connect();

            final String tag = TestSettings.get("bool_tag");
            CIPData value = plc.readTag(tag);
            System.out.println("Original Value: " + value);

            value = new CIPData(Type.BOOL, 1);
            value.set(0, 255);
            plc.writeTags(new String[] { tag }, new CIPData[] { value });
            value = plc.readTag(tag);
            System.out.println("Wrote 255: " + value);
            assertThat(value, not(nullValue()));
            assertThat(value.getNumber(0).intValue(), not(equalTo(0)));

            value.set(0, 1);
            plc.writeTag(tag, value);
            value = plc.readTag(tag);
            System.out.println("Wrote 1: " + value);
            assertThat(value, not(nullValue()));
            assertThat(value.getNumber(0).intValue(), not(equalTo(0)));

            value.set(0, 0);
            plc.writeTag(tag, value);
            value = plc.readTag(tag);
            System.out.println("Wrote 0: " + value);
            assertThat(value, not(nullValue()));
            assertThat(value.getNumber(0).intValue(), equalTo(0));
        }
    }
}
