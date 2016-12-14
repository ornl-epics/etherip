/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.epics.etherip.util;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Test;

/** JUnit demo of locking, mostly meant to view in JProfiler
 *  @author Kay Kasemir
 */
public class LockDemo
{
    private volatile boolean run = true;
    
    @Test
    public void testOrder() throws Exception
    {
        final Runnable task = new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    while (LockDemo.this.run)
                    {
                        // Block other thread
                        synchronized (LockDemo.this)
                        {
                            System.out.println("Thread " + Thread.currentThread().getName());
                            SECONDS.sleep(2);
                        }
                        // Without yield, this thread is quite likely to run again immediately
                        Thread.yield();
                        // Even with yield, this thread will often run again right away.
                        // Only sleep makes it very likely that both threads take turns
                        SECONDS.sleep(1);
                    }
                }
                catch (InterruptedException ex)
                {
                    ex.printStackTrace();
                }
            }
        };
        
        final ExecutorService pool = Executors.newFixedThreadPool(2);
        pool.submit(task);
        pool.submit(task);
        
        SECONDS.sleep(10);
        run = false;
        pool.awaitTermination(5, SECONDS);
    }
}
