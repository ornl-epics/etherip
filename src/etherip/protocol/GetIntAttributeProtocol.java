/*******************************************************************************
 * Copyright (c) 2017 NETvisor Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package etherip.protocol;

import java.nio.ByteBuffer;

//TODO Implement CIP data types according to CPI_Vol_3.3: Appendix_C_Section_C-6.1.
/**
 * Decode 4 byte length attributes.<br>
 *
 * @see CPI_Vol_3.3: Appendix_C_Section_C-6.1.
 * @author László Pataki
 */
public class GetIntAttributeProtocol extends ProtocolAdapter
{
    private int value;

    /** {@inheritDoc} */
    @Override
    public void decode(final ByteBuffer buf, final int available,
            final StringBuilder log) throws Exception
    {
        this.value = buf.getInt();

        if (log != null)
        {
            log.append("ULONG value      : ").append(this.value).append("\n");
        }
    }

    /** @return Value read from response */
    final public int getValue()
    {
        return this.value;
    }
}
