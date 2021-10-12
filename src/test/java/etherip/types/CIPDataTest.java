/*******************************************************************************
 * Copyright (c) 2012 UT-Battelle, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package etherip.types;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.nio.ByteBuffer;

import org.junit.Assert;
import org.junit.Test;

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
        final CIPData data = new CIPData(CIPData.Type.REAL, new byte[] {
                (byte) 0xF9, (byte) 0x0F, (byte) 0x49, (byte) 0x40 });

        assertThat(CIPData.Type.REAL, equalTo(data.getType()));
        assertThat(1, equalTo(data.getElementCount()));
        assertThat(data.toString(), equalTo("CIP_REAL (0x00CA): [3.1416]"));

        assertThat(true, equalTo(data.isNumeric()));
        assertThat("3.1416", equalTo(data.getNumber(0).toString()));

        // Encode
        final ByteBuffer buf = TestSettings.getBuffer();
        data.encode(buf);
        buf.flip();
        assertThat(Hexdump.toHex(buf).trim(),
                equalTo("0000 - CA 00 01 00 F9 0F 49 40"));

        // Modify
        data.set(0, 42.0);
        assertThat(data.toString(), equalTo("CIP_REAL (0x00CA): [42.0]"));
    }

    @Test
    public void testString() throws Exception
    {
        final byte[] data = new byte[] { (byte) 0xCE, (byte) 0x0F, 5, 0, 0, 0,
                'H', 'e', 'l', 'l', 'o' };
        final CIPData value = new CIPData(CIPData.Type.STRUCT, data);

        assertThat(CIPData.Type.STRUCT, equalTo(value.getType()));

        final String txt = value.getString();
        System.out.println(txt);
        assertThat(txt, equalTo("Hello"));

        Assert.assertTrue(value.toString().contains("CIP_STRUCT"));
        Assert.assertTrue(value.toString().contains("STRING"));
    }

    @Test
    public void testCreateType() throws Exception
    {
        final CIPData value = new CIPData(Type.INT, 3);
        value.set(0, 1);
        value.set(1, 2);
        value.set(2, 3);
        assertThat(value.toString(), equalTo("CIP_INT (0x00C3): [1, 2, 3]"));
    }
}
