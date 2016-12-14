/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.epics.etherip.protocol;

import org.epics.etherip.types.CIPData;
import org.epics.etherip.types.CNService;
import org.epics.etherip.types.CNPath;

/** Message Router protocol for reading a tag
 *  @author Kay Kasemir
 */
public class MRChipReadProtocol extends MessageRouterProtocol
{
    final private CIPReadDataProtocol reader;

    /** Initialize
	 *  @param tag Name of tag to read
	 */
    public MRChipReadProtocol(final String tag)
    {
        this(tag, new CIPReadDataProtocol(1));
    }


    public MRChipReadProtocol(final String tag, final int count)
    {
        this(tag, new CIPReadDataProtocol(count));
    }


    /**
     * Initialize
     * @param tag       tag to read
     * @param reader    the reader to read it on
     */
    private MRChipReadProtocol(final String tag, final CIPReadDataProtocol reader)
    {
        super(CNService.CIP_ReadData, CNPath.Symbol(tag), reader);
        this.reader = reader;
    }
    
    public CIPData getData()
    {
        return reader.getData();
    }
}
