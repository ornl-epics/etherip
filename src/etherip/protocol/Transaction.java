/*******************************************************************************
 * Copyright (c) 2018 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package etherip.protocol;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Almost unique transaction ID
 *
 * <p>Rolls around from 1 to 0xFFFFFFFF
 *
 * <p>Used in the encapsulation header's `context`
 * thanks to John Priller who found that it can
 * detect a mismatched request/response
 * which he occasionally observed on a heavily loaded network.
 *
 * @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class Transaction
{
    private static final AtomicLong transaction = new AtomicLong(0);

    static long nextTransaction()
    {
        return transaction.accumulateAndGet(1, (value, plus) ->
        {
            long result = value + plus;
            if (result > 0xffffffffL)
                return 1;
            return result;
        });
    }

    static byte[] format(final long transaction)
    {
        return String.format("%08X", transaction).getBytes();
    }

    static long parse(final byte[] bytes)
    {
        return -1;
    }
}
