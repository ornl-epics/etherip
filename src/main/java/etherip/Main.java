/*******************************************************************************
 * Copyright (c) 2022 UT-Battelle, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package etherip;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import etherip.types.CIPData;
import etherip.types.CIPData.Type;

/** Command-line 'main' for basic read/write tests
 *  @author Kay Kasemir
 */
public class Main
{
    private static String address = "127.0.0.1";
    private static int slot = 0;
    private static short array = 1;
    private static String tag = "";
    private static CIPData write = null;

    private static void usage()
    {
        System.out.println("USAGE: etherip [options] <tag>");
        System.out.println("");
        System.out.println("Options:");
        System.out.println("-h             help");
        System.out.println("-v             verbose");
        System.out.println("-i " + address + "   IP address or DNS name of PLC");
        System.out.println("-s " + slot +   "           Controller slot in ControlLogix crate");
        System.out.println("-a " + array +   "           Number of array elements to read, 1 for scalar");
        System.out.println("-w 3.14        CIPReal value to write");
    }

    public static void main(final String[] args) throws Exception
    {

        for (int i=0; i<args.length; ++i)
        {
            if ("-h".equals(args[i]))
            {
                usage();
                return;
            }
            else if ("-v".equals(args[i]))
            {
                final Logger root = Logger.getLogger("");
                root.setLevel(Level.ALL);
                for (Handler handler : root.getHandlers())
                    handler.setLevel(root.getLevel());
            }
            else if ("-i".equals(args[i]))
            {
                if (i+1 < args.length)
                    address = args[++i];
                else
                {
                    System.out.println("Missing address for -i");
                    usage();
                    return;
                }
            }
            else if ("-s".equals(args[i]))
            {
                if (i+1 < args.length)
                    slot = Integer.parseInt(args[++i]);
                else
                {
                    System.out.println("Missing slot for -s");
                    usage();
                    return;
                }
            }
            else if ("-a".equals(args[i]))
            {
                if (i+1 < args.length)
                    array = Short.parseShort(args[++i]);
                else
                {
                    System.out.println("Missing array count for -a");
                    usage();
                    return;
                }
            }
            else if ("-w".equals(args[i]))
            {
                if (i+1 < args.length)
                {
                    write = new CIPData(Type.REAL, 1);
                    write.set(0, Double.parseDouble(args[++i]));
                }
                else
                {
                    System.out.println("Missing number to write for -w");
                    usage();
                    return;
                }
            }
            else if (args[i].startsWith("-"))
            {
                System.out.println("Unknown option '" + args[i] + "'");
                usage();
                return;
            }
            else if (tag.isBlank())
                tag = args[i].trim();
            else
            {
                System.out.println("Can only handle one tag");
                usage();
                return;
            }
        }

        if (tag.isBlank())
        {
            System.out.println("Missing <tag>");
            usage();
            return;
        }

        try (final EtherNetIP plc = new EtherNetIP(address, slot))
        {
            plc.connectTcp();

            if (write != null)
            {
                plc.writeTag(tag, write);
            }
            else
            {
                final CIPData data = plc.readTag(tag, array);
                System.out.println(data);
            }
        }
    }
}
