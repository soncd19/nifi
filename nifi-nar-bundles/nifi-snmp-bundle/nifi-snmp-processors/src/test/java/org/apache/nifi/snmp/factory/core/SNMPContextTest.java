/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.snmp.factory.core;

import org.apache.nifi.snmp.configuration.SNMPConfiguration;
import org.junit.Test;
import org.snmp4j.Snmp;
import org.snmp4j.Target;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SNMPContextTest {

    @Test
    public void testCreateSNMPContext() {
        final SNMPContext snmpContext = spy(SNMPContext.class);
        final Snmp mockSnmpManager = mock(Snmp.class);
        final Target mockTarget = mock(Target.class);
        final SNMPConfiguration snmpConfiguration = mock(SNMPConfiguration.class);

        when(snmpContext.createSnmpManagerInstance(snmpConfiguration)).thenReturn(mockSnmpManager);
        when(snmpContext.createTargetInstance(snmpConfiguration)).thenReturn(mockTarget);

        snmpContext.createSNMPResourceHandler(snmpConfiguration);

        verify(snmpContext).createSnmpManagerInstance(snmpConfiguration);
        verify(snmpContext).createTargetInstance(snmpConfiguration);
    }

}
