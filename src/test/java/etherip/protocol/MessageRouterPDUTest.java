/*******************************************************************************
 * Copyright (c) 2012-2024 UT-Battelle, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package etherip.protocol;

import static etherip.types.CNPath.Identity;
import static etherip.types.CNPath.Symbol;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.ByteBuffer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import etherip.TestSettings;
import etherip.types.CNService;
import etherip.util.Hexdump;

/** @author Kay Kasemir */
@SuppressWarnings("nls")
public class MessageRouterPDUTest
{
    private final ByteBuffer buf = TestSettings.getBuffer();

    @BeforeEach
    public void setup()
    {
        TestSettings.logAll();
    }

    @Test
    public void testGetAttrib() throws Exception
    {
        final MessageRouterProtocol pdu = new MessageRouterProtocol(
                CNService.Get_Attribute_Single,
                Identity().instance(0x24).attr(0x06), new ProtocolAdapter());
        assertEquals(8, pdu.getRequestSize());

        StringBuilder log = new StringBuilder();
        pdu.encode(this.buf, log);
        System.out.println(log.toString());
        this.buf.flip();

        assertEquals("0000 - 0E 03 20 01 24 24 30 06 - .. .$$0.",
                     Hexdump.toCompactHexdump(this.buf));

        // Fake response
        this.buf.clear();
        this.buf.put(
                new byte[] { (byte) 0x8E, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                             (byte) 0x49, (byte) 0x2C, (byte) 0x41, (byte) 0x00 });
        this.buf.flip();
        log = new StringBuilder();
        pdu.decode(this.buf, this.buf.remaining(), log);
        System.out.println(log.toString());

        System.out.print("USINT data           :");
        while (this.buf.remaining() > 0)
        {
            System.out.format(" %02X", this.buf.get());
        }
        System.out.println();
    }

    @Test
    public void testReadData() throws Exception
    {
        final MessageRouterProtocol pdu = new MessageRouterProtocol(
                CNService.CIP_ReadData, Symbol("kay_ai"), new ProtocolAdapter()
                {
                    @Override
                    public int getRequestSize()
                    {
                        return 2;
                    }

                    @Override
                    public void encode(final ByteBuffer buf,
                            final StringBuilder log)
                    { // Number of elements to read
                        buf.putShort((short) 1);
                        log.append("UINT elements : 1\n");
                    }
                });
        assertEquals(12, pdu.getRequestSize());

        StringBuilder log = new StringBuilder();
        pdu.encode(this.buf, log);
        System.out.println(log.toString());
        this.buf.flip();

        assertEquals("0000 - 4C 04 91 06 6B 61 79 5F 61 69 01 00 - L...kay_ai..",
                     Hexdump.toCompactHexdump(this.buf));

        // Fake response
        this.buf.clear();
        this.buf.put(new byte[] { (byte) 0xCC, (byte) 0x00, (byte) 0x00,
                                  (byte) 0x00, (byte) 0xCA, (byte) 0x00, (byte) 0xF9, (byte) 0x0F,
                                  (byte) 0x49, (byte) 0x40 });
        this.buf.flip();
        log = new StringBuilder();
        pdu.decode(this.buf, this.buf.remaining(), log);
        System.out.println(log.toString());

        System.out.print("USINT data           :");
        while (this.buf.remaining() > 0)
        {
            System.out.format(" %02X", this.buf.get());
        }
        System.out.println();
    }
}
