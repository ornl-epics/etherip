/*******************************************************************************
 * Copyright (c) 2012 UT-Battelle, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package etherip.protocol;

import static etherip.types.CNPath.Symbol;

import etherip.types.CIPData;
import etherip.types.CNService;

/**
 * Message Router protocol for writing a tag
 *
 * @author Kay Kasemir
 */
public class MRChipWriteProtocol extends MessageRouterProtocol
{
    /**
     * Initialize
     *
     * @param tag
     *            Name of tag to write
     * @param value
     *            {@link CIPData} to write
     */
    public MRChipWriteProtocol(final String tag, final CIPData value)
    {
        super(CNService.CIP_WriteData, Symbol(tag),
                new CIPWriteDataProtocol(value));
    }
}
