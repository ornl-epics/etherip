/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.epics.etherip.protocol;

import static org.epics.etherip.types.CNClassPath.Identity;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.nio.ByteBuffer;


import org.epics.etherip.types.CNService;
import org.junit.Before;
import org.junit.Test;

import org.epics.etherip.TestSettings;
import org.epics.etherip.util.Hexdump;

/** @author Kay Kasemir */
public class SendRRDataTest
{
	final private ByteBuffer buf = TestSettings.getBuffer();

	@Before
	public void setup()
	{
		TestSettings.logAll();
	}
	
	@Test
	public void testSendRRData()
	{
		final MessageRouterProtocol pdu = new MessageRouterProtocol(CNService.Get_Attribute_Single, Identity().attr(7), new ProtocolAdapter());
		final SendRRDataProtocol rr_data = new SendRRDataProtocol(pdu);
		final Encapsulation encap = new Encapsulation(Encapsulation.Command.SendRRData, 0x12027100, rr_data);
		final StringBuilder log = new StringBuilder();
		encap.encode(buf, log);
		System.out.println(log.toString());
		
		buf.flip();
		String hex = Hexdump.toHexdump(buf);
		System.out.println(hex);
		assertThat(hex, equalTo("0000 - 6F 00 18 00 00 71 02 12 00 00 00 00 46 75 6E 73 - o....q......Funs\n0010 - 74 75 66 66 00 00 00 00 00 00 00 00 00 00 02 00 - tuff............\n0020 - 00 00 00 00 B2 00 08 00 0E 03 20 01 24 01 30 07 - .......... .$.0.\n"));
	}
}
