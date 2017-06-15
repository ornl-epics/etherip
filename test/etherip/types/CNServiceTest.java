/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package etherip.types;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

/**
 * JUnit test for {@link CNService}
 *
 * @author Kay Kasemir
 */
public class CNServiceTest
{
    @Test
    public void testCNService()
    {
        final CNService service = CNService
                .forCode(CNService.Get_Attribute_Single.getCode());
        System.out.println(service);
        assertThat(service, equalTo(CNService.Get_Attribute_Single));

        final CNService reply = service.getReply();
        System.out.println(reply);
        assertThat(reply, equalTo(CNService.Get_Attribute_Single_Reply));
    }
}
