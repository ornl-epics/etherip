/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package etherip;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Before;
import org.junit.Test;

import etherip.types.CIPData;
import etherip.types.CIPData.Type;

/** @author Kay Kasemir */
@SuppressWarnings("nls")
public class EtherIPDemo
{
	@Before
	public void setup()
	{
		TestSettings.logAll();
		Logger.getLogger("").setLevel(Level.ALL);
        //Logger.getLogger("").setLevel(Level.WARNING);
	}

	@Test
	public void testConnectIP() throws Exception
	{
		try
		(
		    EtherNetIP plc = new EtherNetIP(TestSettings.get("plc"), TestSettings.getInt("slot"));
		)
		{
			plc.connect();

			System.out.println("\n*\n* Connected:\n*\n");
			System.out.println(plc);
		}
	}

    @Test
    public void testFloat() throws Exception
    {
        try
        (
            EtherNetIP plc = new EtherNetIP(TestSettings.get("plc"), TestSettings.getInt("slot"));
        )
        {
            plc.connect();

            final String tag = TestSettings.get("float_tag");

            System.out.println("\n*\n* float '" + tag + "':\n*\n");
            CIPData value = plc.readTag(tag);
            System.out.println(value);
            assertThat(value, not(nullValue()));
            value.set(0,  47.11);
            plc.writeTag(tag, value);

            value = plc.readTag(tag);
            assertThat(value, not(nullValue()));
            System.out.println(value);
        }
    }

    @Test
    public void testBool() throws Exception
    {
        try
        (
            EtherNetIP plc = new EtherNetIP(TestSettings.get("plc"), TestSettings.getInt("slot"));
        )
        {
            plc.connect();

            final String tag = TestSettings.get("bool_tag");
            System.out.println("\n*\n* bool '" + tag + "':\n*\n");

            CIPData value = plc.readTag(tag);
            System.out.println("Original Value: " + value);

            value = new CIPData(Type.BOOL, 1);

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

            value.set(0, 255);
            plc.writeTags(new String[] { tag }, new CIPData[] { value });
            value = plc.readTag(tag);
            System.out.println("Wrote 255: " + value);
            assertThat(value, not(nullValue()));
            assertThat(value.getNumber(0).intValue(), not(equalTo(0)));
        }
    }

    @Test
    public void testString() throws Exception
    {
        try
        (
            EtherNetIP plc = new EtherNetIP(TestSettings.get("plc"), TestSettings.getInt("slot"));
        )
        {
            plc.connect();

            final String tag = TestSettings.get("string_tag");

            System.out.println("\n*\n* string '" + tag + "':\n*\n");
            CIPData value = plc.readTag(tag);
            System.out.println(value);
            assertThat(value, not(nullValue()));
            assertThat(value.isNumeric(), equalTo(false));
            String new_value = value.getString();
            if (new_value.equals("test"))
                new_value = "Testing!";
            else
                new_value = "test";
            System.out.println("Writing '" + new_value + "'");
            value.setString(new_value);
            plc.writeTag(tag, value);

            value = plc.readTag(tag);
            System.out.println(value);
            assertThat(value.getString(), equalTo(new_value));
        }
    }

    @Test
    public void testMultiRead() throws Exception
    {
        try
        (
            EtherNetIP plc = new EtherNetIP(TestSettings.get("plc"), TestSettings.getInt("slot"));
        )
        {
            plc.connect();

            System.out.println("\n*\n* Multi read:\n*\n");
            final String[] tags = new String[]
            {
                    TestSettings.get("float_tag"),
                    TestSettings.get("bool_tag"),
                    TestSettings.get("string_tag")
            };
            final CIPData[] results = plc.readTags(tags);
            assertThat(results, not(nullValue()));
            assertThat(results.length, equalTo(tags.length));

            for (int i=0; i<results.length; ++i)
                System.out.println(tags[i] + " = " + results[i]);
        }
    }
}
