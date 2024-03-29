/*******************************************************************************
 * Copyright (c) 2012 UT-Battelle, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package etherip.util;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.nio.ByteBuffer;

import org.junit.Test;

/** @author Kay Kasemir */
@SuppressWarnings("nls")
public class HexdumpTest
{
    @Test
    public void hexTest()
    {
        final String string = Hexdump
                .toCompactHexdump(ByteBuffer.wrap("Hello!\n".getBytes()));
        System.out.println(string);
        assertThat(string, equalTo("0000 - 48 65 6C 6C 6F 21 0A - Hello!."));
    }
}
