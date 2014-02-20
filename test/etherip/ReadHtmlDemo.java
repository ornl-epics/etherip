/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package etherip;

import static etherip.util.Hexdump.toHexdump;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;

import org.junit.Test;

/** @author Kay Kasemir */
public class ReadHtmlDemo
{
    public static final int TIMEOUT_MS = 2000;
    @Test
    public void readHtml() throws Exception
    {
        final ByteBuffer buffer = ByteBuffer.allocate(1024);

        // Fill
        buffer.put("GET / HTTP/1.1\n".getBytes());
        buffer.put("\n".getBytes());
        buffer.put("\n".getBytes());

        // Done filling, reset position & limit for writing
        buffer.flip();
        System.out.println(toHexdump(buffer));

        final AsynchronousSocketChannel channel = AsynchronousSocketChannel.open();
        channel.connect(new InetSocketAddress("www.google.com", 80)).get(TIMEOUT_MS, MILLISECONDS);

        channel.write(buffer).get(TIMEOUT_MS, MILLISECONDS);

        buffer.clear();
        int read = channel.read(buffer).get(TIMEOUT_MS, MILLISECONDS);
        while (read > 0)
        {
            System.out.println("READ " + read + " bytes");
            buffer.flip();
            System.out.println(toHexdump(buffer));
            buffer.clear();
            // Assume a buffer that wasn't full indicates the server has no more
            // This is not perfect for HTTP
            if (read < buffer.capacity())
                break;
            read = channel.read(buffer).get(TIMEOUT_MS, MILLISECONDS);
        }

        channel.close();
    }
}
