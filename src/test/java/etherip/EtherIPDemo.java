/*******************************************************************************
 * Copyright (c) 2012-2024 UT-Battelle, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package etherip;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import etherip.data.CipException;
import etherip.data.Identity;
import etherip.types.CIPData;
import etherip.types.CIPData.Type;

/** @author Kay Kasemir, László Pataki */
@SuppressWarnings("nls")
public class EtherIPDemo
{
    @BeforeEach
    public void setup()
    {
        TestSettings.logAll();
        Logger.getLogger("").setLevel(Level.ALL);
    }

    @Test
    public void testConnectTcp() throws Exception
    {
        try
        (
            EtherNetIP plc = new EtherNetIP(TestSettings.get("plc"),
                                            TestSettings.getInt("slot"));
        )
        {
            plc.connectTcp();

            System.out.println("\n*\n* Connected:\n*\n");
            System.out.println(plc.getIdentity());
        }
        catch (final CipException e)
        {
            // It is possible to ask some individual field of the ExceptionCip, but these are included in the getMessage().
            // System.err.println(e.getStatusCode());
            // System.err.println(e.getStatusName());
            // System.err.println(e.getStatusDescription());
            System.err.println(e.getMessage());
            System.err.println("Failed with CipException");
            fail("Failed with CipException");
        }
    }

    @Test
    @Disabled
    public void testConnectUdp() throws Exception
    {
        Logger.getLogger("").setLevel(Level.INFO);

        TestSettings.logAll();
        try
        (
            EtherNetIP plc = new EtherNetIP(TestSettings.get("plc"),
                                            TestSettings.getInt("slot"));
        )
        {
            plc.connectUdp();

            System.out.println("\n*\n* UDP Socket established:\n*\n");
            System.out.println(plc);

            final Identity[] listIdentity = plc.listIdentity();

            for (final Identity identity : listIdentity)
            {
                System.out.println(identity);
            }
            // plc.listServices();
        }
        catch (final CipException e)
        {
            // It is possible to ask some individual field of the ExceptionCip, but these are included in the getMessage().
            // System.err.println(e.getStatusCode());
            // System.err.println(e.getStatusName());
            // System.err.println(e.getStatusDescription());
            System.err.println(e.getMessage());
            System.err.println("Failed with CipException");
            fail("Failed with CipException");
        }
    }

    @Test
    public void testFloat() throws Exception
    {
        try
        (
            EtherNetIP plc = new EtherNetIP(TestSettings.get("plc"),
                                            TestSettings.getInt("slot"));
        )
        {
            plc.connectTcp();

            final String tag = TestSettings.get("float_tag");

            System.out.println("\n*\n* float '" + tag + "':\n*\n");
            CIPData value = plc.readTag(tag);
            System.out.println(value);
            assertNotNull(value);
            value.set(0, 47.11);
            plc.writeTag(tag, value);

            value = plc.readTag(tag);
            assertNotNull(value);
            System.out.println("Changed to " + value);
            assertEquals(47.11, value.getNumber(0).doubleValue(), 0.01);
            
            value.set(0, 48.12);
            plc.writeTag(tag, value);

            value = plc.readTag(tag);
            assertNotNull(value);
            System.out.println("Changed to " + value);
            assertEquals(48.12, value.getNumber(0).doubleValue(), 0.01);
        }
    }

    @Test
    public void testInt() throws Exception
    {
        try
        (
            EtherNetIP plc = new EtherNetIP(TestSettings.get("plc"),
                                            TestSettings.getInt("slot"));
        )
        {
            plc.connectTcp();

            final String tag = TestSettings.get("int_tag");

            System.out.println("\n*\n* int '" + tag + "':\n*\n");
            CIPData value = plc.readTag(tag);
            System.out.println(value);
            assertNotNull(value);
            value.set(0, 42);
            plc.writeTag(tag, value);

            value = plc.readTag(tag);
            assertNotNull(value);
            System.out.println("Changed to " + value);
            assertEquals(42, value.getNumber(0).intValue());
            
            value.set(0, 47);
            plc.writeTag(tag, value);

            value = plc.readTag(tag);
            assertNotNull(value);
            System.out.println("Changed to " + value);
            assertEquals(47, value.getNumber(0).intValue());
        }
    }
    
    @Test
    public void testBool() throws Exception
    {
        try
        (
            EtherNetIP plc = new EtherNetIP(TestSettings.get("plc"),
                                            TestSettings.getInt("slot"));
        )
        {
            plc.connectTcp();

            final String tag = TestSettings.get("bool_tag");
            System.out.println("\n*\n* bool '" + tag + "':\n*\n");

            CIPData value = plc.readTag(tag);
            System.out.println("Original Value: " + value);
            System.out.flush();
            System.err.flush();
            
            value = new CIPData(Type.BOOL, 1);

            value.set(0, 1);
            plc.writeTag(tag, value);
            value = plc.readTag(tag);
            System.out.println("Wrote 1: " + value);
            assertNotNull(value);
            assertNotEquals(0, value.getNumber(0).intValue());

            value.set(0, 0);
            plc.writeTag(tag, value);
            value = plc.readTag(tag);
            System.out.println("Wrote 0: " + value);
            assertNotNull(value);
            assertEquals(0, value.getNumber(0).intValue());

            value.set(0, 255);
            plc.writeTags(new String[] { tag }, new CIPData[] { value });
            value = plc.readTag(tag);
            System.out.println("Wrote 255: " + value);
            assertNotNull(value);
            assertNotEquals(0, value.getNumber(0).intValue());
        }
    }

    @Test
    public void testString() throws Exception
    {
        try
        (
            EtherNetIP plc = new EtherNetIP(TestSettings.get("plc"),
                                            TestSettings.getInt("slot"));
        )
        {
            plc.connectTcp();

            final String tag = TestSettings.get("string_tag");

            System.out.println("\n*\n* string '" + tag + "':\n*\n");
            CIPData value = plc.readTag(tag);
            System.out.println(value);
            assertNotNull(value);
            assertFalse(value.isNumeric());
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
            assertEquals(new_value, value.getString());
        }
    }

    @Test
    public void testMultiRead() throws Exception
    {
        try
        (
            EtherNetIP plc = new EtherNetIP(TestSettings.get("plc"),
                                            TestSettings.getInt("slot"));
        )
        {
            plc.connectTcp();

            System.out.println("\n*\n* Multi read:\n*\n");
            final String[] tags = new String[] { TestSettings.get("float_tag"),
                    TestSettings.get("bool_tag"),
                    TestSettings.get("int_tag"),
                    TestSettings.get("string_tag") };
            final CIPData[] results = plc.readTags(tags);
            assertNotNull(results);
            assertEquals(tags.length, results.length);

            for (int i = 0; i < results.length; ++i)
            {
                System.out.println(tags[i] + " = " + results[i]);
            }
        }
    }
}
