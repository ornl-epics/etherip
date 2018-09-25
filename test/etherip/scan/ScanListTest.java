/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package etherip.scan;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import etherip.Tag;
import etherip.TagListener;
import etherip.TestSettings;
import etherip.protocol.RegisterSession;
import etherip.protocol.TcpConnection;

/**
 * JUnit demo of the {@link ScanList}
 *
 * @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ScanListTest implements TagListener
{
    final private CountDownLatch updates = new CountDownLatch(5);
    final private CountDownLatch errors = new CountDownLatch(5);
    private TcpConnection connection;

    @Before
    public void setup() throws Exception
    {
        TestSettings.logAll();

        this.connection = new TcpConnection(TestSettings.get("plc"),
                TestSettings.getInt("slot"));
        final RegisterSession register = new RegisterSession();
        this.connection.execute(register);
        this.connection.setSession(register.getSession());
    }

    @After
    public void shutdown() throws Exception
    {
        this.connection.close();
    }

    @Override
    public void tagUpdate(final Tag tag)
    {
        System.out.println("Update: " + tag);
        this.updates.countDown();
    }

    @Override
    public void tagError(final Tag tag)
    {
        System.out.println("Error: " + tag);
        this.errors.countDown();
    }

    @Test
    public void testScanListRead() throws Exception
    {
        Logger.getLogger("").setLevel(Level.CONFIG);

        final Scanner scanner = new Scanner(this.connection);
        final Tag tag1 = scanner.add(1.0, TestSettings.get("float_tag"));
        final Tag tag2 = scanner.add(2.0, TestSettings.get("bool_tag"));

        tag1.addListener(this);
        tag2.addListener(this);
        this.updates.await(10, SECONDS);

        assertThat(this.updates.getCount(), equalTo(0l));

        scanner.stop();

        tag2.removeListener(this);
        tag1.removeListener(this);
    }

    @Test(timeout = 20000)
    public void testScanListWrite() throws Exception
    {
        Logger.getLogger("").setLevel(Level.CONFIG);

        final Scanner scanner = new Scanner(this.connection);
        final Tag tag1 = scanner.add(1.0, TestSettings.get("write_tag"));
        final Tag tag2 = scanner.add(2.0, TestSettings.get("bool_tag"));

        tag1.addListener(this);
        tag2.addListener(this);
        this.updates.await(10, SECONDS);
        assertThat(this.updates.getCount(), equalTo(0l));

        // Increment value of tag
        final Number value = tag1.getData().getNumber(0);
        System.out.println(tag1.getName() + " is " + value);
        System.out.println(" .. incrementing .. ");
        tag1.setWriteValue(0, value.intValue() + 1);

        // Tag will now actually indicate the value that's to be written.
        // Should wait for it to really be written (could check tag1.getState()),
        // then wait for the next readback to assert that we read that new value.
        // In here, we simply wait. In the console, you should see that a readback
        // arrived with the new value.
        Thread.sleep(2000);
        while (tag1.getData().equals(value))
        {
            Thread.sleep(200);
        }

        System.out.println(tag1.getName() + " changed to " + tag1.getData());

        scanner.stop();

        tag2.removeListener(this);
        tag1.removeListener(this);
    }

    @Test
    public void testScanListError() throws Exception
    {
        final Scanner scanner = new Scanner(this.connection);
        final Tag tag = scanner.add(1.0, TestSettings.get("invalid_tag"));

        tag.addListener(this);
        this.errors.await(10, SECONDS);

        assertThat(this.errors.getCount(), equalTo(0l));

        scanner.stop();

        tag.removeListener(this);
    }
}
