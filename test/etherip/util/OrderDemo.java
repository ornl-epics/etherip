/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package etherip.util;

import static org.junit.Assert.assertTrue;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.junit.Test;

/** JUnit demo of ByteBuffer orderring
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class OrderDemo
{
    @Test
    public void testOrder() throws Exception
    {
        final ByteBuffer buffer = ByteBuffer.allocate(20);
        buffer.order(ByteOrder.BIG_ENDIAN);

        // Fill
        buffer.putInt(0x1234);

        // Done filling, reset position & limit for writing
        buffer.flip();
        String text = Hexdump.toHexdump(buffer);
        System.out.println(text);

        assertTrue(text.startsWith("0000 - 00 00 12 34 "));

        // Change byte order
        buffer.clear();
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        // Fill
        buffer.putInt(0x1234);

        // Done filling, reset position & limit for writing
        buffer.flip();
        text = Hexdump.toHexdump(buffer);
        System.out.println(text);

        assertTrue(text.startsWith("0000 - 34 12 00 00 "));
    }
}
