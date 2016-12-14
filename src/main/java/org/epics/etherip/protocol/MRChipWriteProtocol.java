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

/** Message Router protocol for writing a tag
 *  @author Kay Kasemir
 */
public class MRChipWriteProtocol extends MessageRouterProtocol
{
    /** Initialize
     *  @param tag Name of tag to write
	 *  @param value {@link CIPData} to write
	 */
    public MRChipWriteProtocol(final String tag, final CIPData value)
    {
        super(CNService.CIP_WriteData, CNPath.Symbol(tag), new CIPWriteDataProtocol(value));
    }
}
