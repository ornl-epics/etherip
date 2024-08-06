/*******************************************************************************
 * Copyright (c) 2012-2024 UT-Battelle, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package etherip.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.ByteBuffer;

import org.junit.jupiter.api.Test;

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
        assertEquals("0000 - 48 65 6C 6C 6F 21 0A - Hello!.", string);
    }
}
