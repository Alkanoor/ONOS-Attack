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
package org.app5;

import org.apache.felix.scr.annotations.*;
import org.onosproject.app.ApplicationAdminService;
import org.onosproject.core.Application;
import org.onosproject.core.ApplicationId;
import org.onosproject.core.CoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;


/**
 * Application that tries to read details about other apps
 */
@Component(immediate = true)
public class AppComponent {

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected ApplicationAdminService appadmin;

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Activate
    protected void activate() {
        log.info("Application 10 started : beginning application suppression");

        Set<Application> appset = appadmin.getApplications();

        for(Application a : appset)
        {
            log.info("New application {} :", a.id());
            log.info("Title : {}", a.title());
            log.info("Description : {}", a.description());
            log.info("Version : {}", a.version());
            log.info("Category : {}", a.category());
            log.info("Url : {}", a.url());
            log.info("Readme : {}", a.readme());
            log.info("Origin : {}", a.origin());
        }
    }

    @Deactivate
    protected void deactivate() {
        log.info("Application 10 stopped");
    }
}
