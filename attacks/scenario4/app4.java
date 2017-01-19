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
package org.app4;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.*;

/**
 * Application that tries to fork bomb
 */
@Component(immediate = true)
public class AppComponent {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Activate
    protected void activate() {
        log.info("Application 4 started : beginning fork bomb");

        for(int i=0; i<10000; i++)
        {
            MyThread runnable = new MyThread(log);
            Thread thread = new Thread(runnable);
            thread.start();
            log.info("Thread {} started", i);
        }
    }

    @Deactivate
    protected void deactivate() {
        log.info("Application 4 stopped");
    }

    public class MyThread implements Runnable
    {
        int counter = 0;
        Logger log;

        public MyThread(Logger l)
        {
            log = l;
        }

        public void run()
        {
            while(true)
            {
                counter++;
                log.info("Counter: " + counter);
                try
                {Thread.sleep(10);}
                catch(Exception e) {}
            }
        }
    }

}
