/*******************************************************************************
 * Copyright (c) 2012-2024 UT-Battelle, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package etherip.types;

import static etherip.types.CNPath.Identity;
import static etherip.types.CNPath.Symbol;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.nio.ByteBuffer;

import org.junit.jupiter.api.Test;

import etherip.util.Hexdump;

/** @author Kay Kasemir */
@SuppressWarnings("nls")
public class CNPathTest
{
    @Test
    public void testClassPath() throws Exception
    {
        final CNPath path = Identity().instance(1).attr(0x7);
        System.out.println(path.toString());
        assertEquals("Path (3 el) Class(0x20 0x1) Identity, instance(0x24) 1, attribute(0x30) 7",
        		     path.toString());

        final ByteBuffer buf = ByteBuffer.allocate(20);
        path.encode(buf, null);
        buf.flip();
        assertEquals("0000 - 03 20 01 24 01 30 07 - . .$.0.",
          		     Hexdump.toCompactHexdump(buf));
    }

    @Test
    public void testClassPathWithLargeInstance() throws Exception
    {
        final CNPath path = Identity().instance(456).attr(0x7);
        System.out.println(path.toString());
        assertEquals("Path (3 el) Class(0x20 0x1) Identity, instance(0x25) 456, attribute(0x30) 7",
        		     path.toString());

        final ByteBuffer buf = ByteBuffer.allocate(20);
        path.encode(buf, null);
        buf.flip();
        assertEquals("0000 - 04 20 01 25 00 01 C8 30 07 - . .%...0.",
        		     Hexdump.toCompactHexdump(buf));
    }

    @Test
    public void testSymbolPath() throws Exception
    {
        CNPath path = Symbol("my_tag");
        System.out.println(path.toString());
        assertEquals("Path Symbol(0x91) 'my_tag'", path.toString());

        final ByteBuffer buf = ByteBuffer.allocate(20);
        path.encode(buf, null);
        buf.flip();
        assertEquals("0000 - 04 91 06 6D 79 5F 74 61 67 - ...my_tag",
        		     Hexdump.toCompactHexdump(buf));

        // When using other name, must get other path
        final CNPath other = Symbol("other_tag");
        System.out.println(other.toString());
        assertNotEquals(other.toString(), path.toString());

        // With 'pad'
        path = Symbol("my_tag2");
        System.out.println(path.toString());
        assertEquals("Path Symbol(0x91) 'my_tag2', 0x00", path.toString());

        buf.clear();
        path.encode(buf, null);
        buf.flip();
        assertEquals("0000 - 05 91 07 6D 79 5F 74 61 67 32 00 - ...my_tag2.",
        		     Hexdump.toCompactHexdump(buf));
    }
}
