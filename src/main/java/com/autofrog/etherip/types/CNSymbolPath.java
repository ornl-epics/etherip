/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package etherip.types;

import java.nio.ByteBuffer;

/** Control Net Path for element path
 * 
 *  <p>Example (with suitable static import):
 *  <p><code>CNPath path = Symbol.name("my_tag")</code>
 *  @author Kay Kasemir
 */
public class CNSymbolPath extends CNPath
{
	private String symbol;

	/** Initialize
	 *  @param symbol Name of symbol
	 */
	protected CNSymbolPath(final String symbol)
	{
		this.symbol = symbol;
	}
	
    /** {@inheritDoc} */
	@Override
    public int getRequestSize()
    {   // End of string is padded if length is odd
	    return 2 + symbol.length() + (needPad() ? 1 : 0);
    }
    
    /** {@inheritDoc} */
	@Override
    public void encode(final ByteBuffer buf, final StringBuilder log)
	{
		// spec 4 p.21: "ANSI extended symbol segment"
		buf.put((byte) (getRequestSize() / 2));
		buf.put((byte) 0x91);
		buf.put((byte) symbol.length());
		buf.put(symbol.getBytes());
		if (needPad())
			buf.put((byte) 0);
	}

	/** @return Is path of odd length, requiring a pad byte? */
	private boolean needPad()
    {
	    // Findbugs: x%2==1 fails for negative numbers
	    return (symbol.length() % 2) != 0;
    }
    
	@Override
    public String toString()
	{
		final StringBuilder buf = new StringBuilder();
		buf.append("Path Symbol(0x91) '").append(symbol).append("'");
		if (needPad())
			buf.append(", 0x00");
		return buf.toString();
	}
}
