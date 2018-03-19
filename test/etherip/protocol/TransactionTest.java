/*******************************************************************************
 * Copyright (c) 2018 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package etherip.protocol;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

/** @author Kay Kasemir */
public class TransactionTest
{
    @Test
    public void testTransaction() throws Exception
    {
        for (int i=0; i<10; ++i)
            System.err.println(Transaction.nextTransaction());

        long t = Transaction.nextTransaction();
        final byte[] bytes = Transaction.format(t);
        assertThat(bytes.length, equalTo(8));
        final String text = new String(bytes);
        System.out.println(text);
    }
}
