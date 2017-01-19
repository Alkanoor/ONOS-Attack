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

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;

/**
 * Application that tries to disable a chosen app
 */
@Component(immediate = true)
public class AppComponent {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final String target = "qspidojsqojid";

    @Activate
    protected void activate() {
        log.info("Application 5 started : beginning application suppression");

        BundleContext bundleContext = contextbk.getBundleContext();
        Bundle[] bundleList = bc.getBundles();

        for(int i=0; i<bundleList.length; i++)
        {
            Bundle cur_bundle = bundleList[i];
            cur_bundle.getRegisteredServices();
            log.info("Bundle {} name : {}", i, cur_bundle.getSymbolicName());
            if(cur_bundle.getSymbolicName().contains(target))
            {
                log.info("Our app has been found, trying to uninstall");

                try
                {
                    cur_bundle.uninstall();
                }
                catch(BundleException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    @Deactivate
    protected void deactivate() {
        log.info("Application 5 stopped");
    }
}
