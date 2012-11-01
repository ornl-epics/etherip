/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package etherip.protocol;

import static etherip.types.CNPath.Identity;
import static etherip.types.CNPath.Symbol;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.nio.ByteBuffer;


import org.junit.Before;
import org.junit.Test;

import etherip.TestSettings;
import etherip.protocol.MessageRouterProtocol;
import etherip.protocol.ProtocolAdapter;
import etherip.types.CNService;
import etherip.util.Hexdump;

public class MessageRouterPDUTest
{
	private ByteBuffer buf = TestSettings.getBuffer();

	@Before
	public void setup()
	{
		TestSettings.logAll();
	}
	
	@Test
	public void testGetAttrib() throws Exception
	{
		final MessageRouterProtocol pdu = new MessageRouterProtocol(CNService.Get_Attribute_Single, Identity().instance(0x24).attr(0x06), new ProtocolAdapter());
		assertThat(pdu.getRequestSize(), equalTo(8));
		
		StringBuilder log = new StringBuilder();
		pdu.encode(buf, log);
		System.out.println(log.toString());
		buf.flip();
		
		assertThat(Hexdump.toCompactHexdump(buf), equalTo("0000 - 0E 03 20 01 24 24 30 06 - .. .$$0."));
		
		// Fake response
		buf.clear();
		buf.put(new byte[] { (byte)0x8E, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x49, (byte)0x2C, (byte)0x41, (byte)0x00 });
		buf.flip();
		log = new StringBuilder();
		pdu.decode(buf, buf.remaining(), log);
		System.out.println(log.toString());
		
		System.out.print("USINT data           :");
		while (buf.remaining() > 0)
			System.out.format(" %02X",  buf.get());
		System.out.println();
	}

	@Test
	public void testReadData() throws Exception
	{
		final MessageRouterProtocol pdu = new MessageRouterProtocol(CNService.CIP_ReadData, Symbol("kay_ai"),
				new ProtocolAdapter()
		{
			@Override
            public int getRequestSize()
            {
	            return 2;
            }

			@Override
            public void encode(final ByteBuffer buf, final StringBuilder log)
            {	// Number of elements to read
	            buf.putShort((short)1);
	            log.append("UINT elements : 1\n");
            }
		});
		assertThat(pdu.getRequestSize(), equalTo(12));

		StringBuilder log = new StringBuilder();
		pdu.encode(buf, log);
		System.out.println(log.toString());
		buf.flip();
		
		assertThat(Hexdump.toCompactHexdump(buf), equalTo("0000 - 4C 04 91 06 6B 61 79 5F 61 69 01 00 - L...kay_ai.."));

		// Fake response
		buf.clear();
		buf.put(new byte[] { (byte)0xCC, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0xCA, (byte)0x00, (byte)0xF9, (byte)0x0F, (byte)0x49, (byte)0x40 });
		buf.flip();
		log = new StringBuilder();
		pdu.decode(buf, buf.remaining(), log);
		System.out.println(log.toString());
		
		System.out.print("USINT data           :");
		while (buf.remaining() > 0)
			System.out.format(" %02X",  buf.get());
		System.out.println();
	}
}
