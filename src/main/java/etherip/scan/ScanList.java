/*******************************************************************************
 * Copyright (c) 2012 UT-Battelle, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package etherip.scan;

import static etherip.EtherNetIP.logger;

import java.util.TimerTask;
import java.util.logging.Level;

import etherip.Tag;
import etherip.TagList;
import etherip.protocol.Connection;

/**
 * List of tags that are processed (read or written)
 *
 * @author Kay Kasemir
 */
class ScanList extends TimerTask
{
    final private double period;
    final private Connection connection;

    final private TagList tags = new TagList();

    private volatile boolean aborted = false;

    public ScanList(final double period, final Connection connection)
    {
        this.period = period;
        this.connection = connection;
    }

    public Tag add(final String tag_name)
    {
        return this.tags.add(tag_name);
    }

    @Override
    public void run()
    {
        logger.log(Level.FINE, "Scan list {0} sec", this.period);
        try
        {
            this.tags.process(this.connection);
        }
        catch (final Exception ex)
        {
            if (this.aborted)
            {
                return;
            }
            logger.log(Level.WARNING,
                    "Scan list " + this.period + " sec process failed", ex);
        }
    }

    @Override
    public boolean cancel()
    {
        this.aborted = true;
        return super.cancel();
    }
}