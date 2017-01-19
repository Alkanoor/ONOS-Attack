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
 * Application that tries to remove links
 */
@Component(immediate = true)
public class AppComponent {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Activate
    protected void activate() {
        log.info("Application 6 started : beginning links suppression");

        Iterable<Device> dv = deviceService.getDevices();
        Iterator it = dv.iterator();

        if (it.hasNext())
            removed = "success|";

        while (it.hasNext()) {
            Device piece = (Device) it.next();
            deviceadmin.removeDevice(piece.id());
            removed += piece.id() + "-" + piece.serialNumber() + "|";
        }

        System.out.println("[ATTACK] Device Remove: \n" + removed);
        Iterable<Link> links = linkService.getActiveLinks();
        it = dv.iterator();
        while (it.hasNext()) {
            Link link = (Link) it.next();
        }

        return removed;
    }

    @Deactivate
    protected void deactivate() {
        log.info("Application 6 stopped");
    }
}
