/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package etherip.protocol;

import static etherip.types.CNPath.Symbol;
import etherip.types.CIPData;
import etherip.types.CNService;

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
        this(tag, new CIPReadDataProtocol());
    }

    /** Initialize
     *  @param tag Name of tag to read
     *  @param body Protocol embedded in the message request/response
     */
    private MRChipReadProtocol(final String tag, final CIPReadDataProtocol reader)
    {
        super(CNService.CIP_ReadData, Symbol(tag), reader);
        this.reader = reader;
    }
    
    public CIPData getData()
    {
        return reader.getData();
    }
}
