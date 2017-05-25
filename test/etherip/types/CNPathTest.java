/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package etherip.types;

import static etherip.types.CNPath.Identity;
import static etherip.types.CNPath.Symbol;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import java.nio.ByteBuffer;

import org.junit.Test;

import etherip.util.Hexdump;

/** @author Kay Kasemir */
@SuppressWarnings("nls")
public class CNPathTest
{
	@Test
	public void testClassPath() throws Exception
	{
		CNPath path = Identity().instance(1).attr(0x7);
		System.out.println(path.toString());
		assertThat(path.toString(), equalTo("Path (3 el) Class(0x20) 0x1 (Identity), instance(0x24) 1, attrib.(0x30) 0x7"));

		final ByteBuffer buf = ByteBuffer.allocate(20);
		path.encode(buf, null);
		buf.flip();
		assertThat(Hexdump.toCompactHexdump(buf), equalTo("0000 - 03 20 01 24 01 30 07 - . .$.0."));
	}

	@Test
	public void testSymbolPath() throws Exception
	{
		CNPath path = Symbol("my_tag");
		System.out.println(path.toString());
		assertThat(path.toString(), equalTo("Path Symbol(0x91) 'my_tag'"));

		final ByteBuffer buf = ByteBuffer.allocate(20);
		path.encode(buf, null);
		buf.flip();
		assertThat(Hexdump.toCompactHexdump(buf), equalTo("0000 - 04 91 06 6D 79 5F 74 61 67 - ...my_tag"));

		// When using other name, must get other path
        CNPath other = Symbol("other_tag");
        System.out.println(other.toString());
        assertThat(other.toString(), not(equalTo(path.toString())));

		// With 'pad'
		path = Symbol("my_tag2");
		System.out.println(path.toString());
		assertThat(path.toString(), equalTo("Path Symbol(0x91) 'my_tag2', 0x00"));

		buf.clear();
		path.encode(buf, null);
		buf.flip();
		assertThat(Hexdump.toCompactHexdump(buf), equalTo("0000 - 05 91 07 6D 79 5F 74 61 67 32 00 - ...my_tag2."));
	}
}
