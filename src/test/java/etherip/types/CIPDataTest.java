/*******************************************************************************
 * Copyright (c) 2012-2024 UT-Battelle, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package etherip.types;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.ByteBuffer;

import org.junit.jupiter.api.Test;

import etherip.TestSettings;
import etherip.types.CIPData.Type;
import etherip.util.Hexdump;

/**
 * JUnit test of {@link CIPData}
 *
 * @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class CIPDataTest
{
    @Test
    public void testFloat() throws Exception
    {
        // Decode
        final CIPData data = new CIPData(CIPData.Type.REAL,
        		new byte[] { (byte) 0xF9, (byte) 0x0F, (byte) 0x49, (byte) 0x40 });

        assertEquals(CIPData.Type.REAL, data.getType());
        assertEquals(1, data.getElementCount());
        assertEquals("CIP_REAL (0x00CA): [3.1416]", data.toString());

        assertTrue(data.isNumeric());
        assertEquals("3.1416", data.getNumber(0).toString());

        // Encode
        final ByteBuffer buf = TestSettings.getBuffer();
        data.encode(buf);
        buf.flip();
        assertEquals("0000 - CA 00 01 00 F9 0F 49 40", Hexdump.toHex(buf).trim());

        // Modify
        data.set(0, 42.0);
        assertEquals("CIP_REAL (0x00CA): [42.0]", data.toString());
    }

    @Test
    public void testString() throws Exception
    {
        final byte[] data = new byte[] { (byte) 0xCE, (byte) 0x0F, 5, 0, 0, 0,
                'H', 'e', 'l', 'l', 'o' };
        final CIPData value = new CIPData(CIPData.Type.STRUCT, data);

        assertEquals(CIPData.Type.STRUCT, value.getType());

        final String txt = value.getString();
        System.out.println(txt);
        assertEquals("Hello", txt);

        assertTrue(value.toString().contains("CIP_STRUCT"));
        assertTrue(value.toString().contains("STRING"));
    }

    @Test
    public void testCreateType() throws Exception
    {
        final CIPData value = new CIPData(Type.INT, 3);
        value.set(0, 1);
        value.set(1, 2);
        value.set(2, 3);
        assertEquals("CIP_INT (0x00C3): [1, 2, 3]", value.toString());
    }
}
