/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
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
        return tags.add(tag_name);
    }
    
    @Override
    public void run()
    {
        logger.log(Level.FINE, "Scan list {0} sec", period);
        try
        {
            tags.process(connection);
        }
        catch (Exception ex)
        {
            if (aborted)
                return;
            logger.log(Level.WARNING, "Scan list " + period + " sec process failed", ex);
        }
    }
    
    @Override
    public boolean cancel()
    {
        aborted = true;
        return super.cancel();
    }
}