/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.epics.etherip.types;

import org.epics.etherip.protocol.ProtocolEncoder;

/** Control Net Path
 * 
 *  <p>Example (with suitable static import):
 *  <p><code>CNPath path = Identity().instance(1).attr(7)</code>
 *  @author Kay Kasemir
 */
abstract public class CNPath implements ProtocolEncoder
{
	// Objects, see Spec 10 p. 1, 13, 25
    /** Create path to Identity object
     *  @return {@link CNClassPath}
     */
	public static CNClassPath Identity()
	{
	    return new CNClassPath(0x01, "Identity");
	}
	
    /** Create path to MessageRouter object
     *  @return {@link CNClassPath}
     */
	public static CNClassPath MessageRouter()
	{
	    return new CNClassPath(0x02, "MessageRouter");
	}
	
    /** Create path to ConnectionManager object
     *  @return {@link CNPath}
     */
	public static CNPath ConnectionManager()
	{
	    return new CNClassPath(0x06, "ConnectionManager");
	}
	
	/** Create symbol path
	 *  @param name Name of the tag to put into symbol path
	 *  @return {@link CNPath}
	 */
	public static CNPath Symbol(final String name)
	{
	    return new CNSymbolMultiPath(name);
	}
}
