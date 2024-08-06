/*******************************************************************************
 * Copyright (c) 2012-2024 UT-Battelle, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package etherip.protocol;

import static etherip.protocol.Encapsulation.Command.SendRRData;
import static etherip.types.CNPath.MessageRouter;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.ByteBuffer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import etherip.TestSettings;
import etherip.types.CNService;
import etherip.util.Hexdump;

/** @author Kay Kasemir */
@SuppressWarnings("nls")
public class MultiRequestTest
{
    private final ByteBuffer buf = TestSettings.getBuffer();

    @BeforeEach
    public void setup()
    {
        TestSettings.logAll();
        Transaction.reset();
    }

    @Test
    public void testReadData() throws Exception
    {
        final int session = 0x12345678;

        final MRChipReadProtocol cip_read1 = new MRChipReadProtocol("kay_ai");
        final MRChipReadProtocol cip_read2 = new MRChipReadProtocol("kay_ao");

        final Encapsulation encap = new Encapsulation(SendRRData, session,
                new SendRRDataProtocol(new UnconnectedSendProtocol(0,
                        new MessageRouterProtocol(CNService.CIP_MultiRequest,
                                MessageRouter(), new CIPMultiRequestProtocol(
                                        cip_read1, cip_read2)))));

        final StringBuilder log = new StringBuilder();
        encap.encode(this.buf, log);
        System.out.println(log.toString());
        this.buf.flip();

        final String dump = Hexdump.toHexdump(this.buf);
        System.out.println(dump);
        assertEquals("0000 - 6F 00 42 00 78 56 34 12 00 00 00 00 30 30 30 30 - o.B.xV4.....0000\n"
                   + "0010 - 30 30 30 31 00 00 00 00 00 00 00 00 00 00 02 00 - 0001............\n"
                   + "0020 - 00 00 00 00 B2 00 32 00 52 02 20 06 24 01 0A F0 - ......2.R. .$...\n"
                   + "0030 - 24 00 0A 02 20 02 24 01 02 00 06 00 12 00 4C 04 - $... .$.......L.\n"
                   + "0040 - 91 06 6B 61 79 5F 61 69 01 00 4C 04 91 06 6B 61 - ..kay_ai..L...ka\n"
                   + "0050 - 79 5F 61 6F 01 00 01 00 01 00                   - y_ao......      \n",
                   dump);
    }
}
