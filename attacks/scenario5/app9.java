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
package org.app7;

import org.apache.felix.scr.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;

/**
 * Application that tries to read useful things in /proc/pid
 */
@Component(immediate = true)
public class AppComponent {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Activate
    protected void activate() {
        log.info("Application 9 started : beginning information reading");

        String pid = getPid();
        log.info("PID found : {}", pid);

        ArrayList files = getAllFiles(new File("/proc/"+pid+""));
        log.info("App has access to");
        for(String s : files)
            log.info(s);

        String path = "/proc/"+pid+"/cmdline";
        String read = readFile(path, 1024);
        log.info("App read the file {} : {}", path, read);
    }

    @Deactivate
    protected void deactivate() {
        log.info("Application 9 stopped");
    }

    static String getPid() throws IOException
    {
	    byte[] bo = new byte[256];
	    InputStream is = new FileInputStream("/proc/self/stat");
	    is.read(bo);
	    for(int i = 0; i < bo.length; i++)
	        if((bo[i] < '0') || (bo[i] > '9'))
	            return new String(bo, 0, i);
	    return "-1";
	}

    static ArrayList getAllFiles(File curDir)
    {
		ArrayList r = new ArrayList();
        File[] filesList = curDir.listFiles();
        for(File f : filesList)
            r.add(f.getName());
        return r;
    }

    static String readFile(String path, int len) throws IOException
    {
        String ret = new String();
		InputStream is = new FileInputStream(path);
        byte[] encoded = new byte[len];
        while(is.read(encoded)>0)
        {
            is.read(encoded);
            tmp = new String(encoded);
            ret += tmp;
        }
        return ret;
    }
}
