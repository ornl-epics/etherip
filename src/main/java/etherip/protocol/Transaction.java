/*******************************************************************************
 * Copyright (c) 2018-2024 UT-Battelle, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package etherip.protocol;

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
    // SYNC on access
    private static long transaction = 0;

    /** Reset transactions for repeatable unit tests */
    static void reset()
    {
        synchronized(Transaction.class)
        {
            transaction = 0;
        }
    }
    
    /** @return Next transaction ("context") identifier */
    static long nextTransaction()
    {
        synchronized(Transaction.class)
        {
            ++transaction;
            if (transaction > 0xFFFFFFFFL)
                transaction = 1;
            return transaction;
        }
    }

    /** @return Transaction ID as text / ASCII bytes for "context" */
    static byte[] format(final long transaction)
    {
        return String.format("%08X", transaction).getBytes();
    }
}
