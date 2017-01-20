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
package org.app6;

import org.apache.felix.scr.annotations.*;
import org.onosproject.net.*;
import org.onosproject.net.device.DeviceAdminService;
import org.onosproject.net.device.DeviceClockService;
import org.onosproject.net.device.DeviceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Application that tries to remove links
 */
@Component(immediate = true)
public class AppComponent {

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected DeviceService deviceService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected DeviceAdminService deviceadmin;


    private final Logger log = LoggerFactory.getLogger(getClass());

    @Activate
    protected void activate() {
        log.info("Application 6 started : beginning links suppression");

        Iterable<Device> devices = deviceService.getDevices();
        Iterator it = devices.iterator();

        String removed_devices = new String();

        while(it.hasNext())
        {
            Device device = (Device)it.next();
            deviceadmin.removeDevice(device.id());
            removed_devices += device.id()+" || ";
        }

        log.info("Devices has been removed : {}", removed_devices);
    }

    @Deactivate
    protected void deactivate() {
        log.info("Application 6 stopped");
    }
}
