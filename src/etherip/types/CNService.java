/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package etherip.types;

/** CN Services
 * 
 *  <p>Spec 4, p.36
 *  @author Kay Kasemir
 */
public enum CNService 
{
	Get_Attribute_All(0x01),
    Get_Attribute_Single(0x0E), // Spec 6 p. 43
    CIP_MultiRequest(0x0A),     // Logix5000 Data Access
    CIP_ReadData(0x4C),         // Logix5000 Data Access
    CIP_WriteData(0x4D),        // Logix5000 Data Access
    CM_Unconnected_Send(0x52),

	Get_Attribute_All_Reply(0x01 | 0x80),
    Get_Attribute_Single_Reply(0x0E | 0x80),
    CIP_MultiRequest_Reply(0x0A | 0x80),
    CIP_ReadData_Reply(0x4C | 0x80),
    CIP_WriteData_Reply(0x4D | 0x80);

	final private byte code;
	
	private CNService(final int code)
	{
		this.code = (byte) code;
	}

	/** @param code Service code
	 *  @return Matching {@link CNService} or <code>null</code>
	 */
	public static CNService forCode(final int code)
	{
		for (CNService service : values())
			if ((byte)code == service.code)
				return service;
		return null;
	}
	
	/** @return Code (ID) of service as used in protocol */
	public byte getCode()
	{
		return code;
	}

	/** @return <code>true</code> if this is a 'reply' to a request */
	public boolean isReply()
	{
		return (code & 0x80) == 0x80;
	}
	
	/** Obtain the 'reply' for a service
	 *  @return {@link CNService} for the reply,
	 *          or <code>null</code> if there is no known reply
	 */
	public CNService getReply()
	{
		if (this == CM_Unconnected_Send)
			return null;
		// Is this already a 'reply'?
		if ((code & 0x80) == 0x80)
			return null;
		return forCode(code | 0x80);
	}
	
	@Override
    public String toString()
	{
		return String.format("%s (0x%02X)", name(), code);
	}
}
