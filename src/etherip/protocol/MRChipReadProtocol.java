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

    /** Initialize.  Note that if trying to read an array this will only return the first item.
	 *  @param tag Name of tag to read
	 */
    public MRChipReadProtocol(final String tag)
    {
        this(tag, (short)1);
    }


    /** Initialize.  Use this constructor to retrieve an array.
     *  @param tag      Name of tag to read
     *  @param count    Number of elements to read (if it is an array)
     */
    public MRChipReadProtocol(final String tag, final short count)
    {
        this(tag, new CIPReadDataProtocol(count));
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
