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
package org.app3;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Random;

/**
 * Application that tries to stuck controller in while(1)
 */
@Component(immediate = true)
public class AppComponent {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Activate
    protected void activate() {
        log.info("Application 3 started : beginning memory exhausion");

        int i=0;
        Random random = new Random();
        ArrayList<long[]> t = new ArrayList<long[]>();

        while(true)
        {
            t.add(new long[10000000]);
            for(int j=0; j<1000; j++)
                t.get(i)[random.nextInt(10000000)] = 666;
            log.info("Continuing loop : {} with table of size {} and t[i] of size {} ", i, t.size(), len(t.get(i)));
        }
    }

    @Deactivate
    protected void deactivate() {
        log.info("Application 3 stopped");
    }

}