/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package etherip.protocol;

/** Perform the 'RegisterSession' command
 *  @author Kay Kasemir
 */
public class RegisterSession extends Encapsulation
{
    public RegisterSession()
    {
        super(Encapsulation.Command.RegisterSession, 0, new RegisterSessionProtocol());
    }
}
