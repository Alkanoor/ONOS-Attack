#!/usr/bin/python

from mininet.topo import Topo
from mininet.net import Mininet
from mininet.util import *
from mininet.node import RemoteController
from mininet.log import setLogLevel
from mininet.cli import CLI
import cmd


macs = ['ba:d1:de:a0:00:00', 'fa:ce:be:ef:fa:ce', 'f0:01:1d:ea:f0:01', 'de:f1:c1:3c:e6:66']
ips = ['192.168.56.58/24', '192.168.56.68/24', '192.168.56.78/24', '192.168.56.66/24']

class TreeWithMalicious(Topo):

    def build(self):
        switch1 = self.addSwitch('s1')
        switch2 = self.addSwitch('s2')
        switch3 = self.addSwitch('s3')
        eve = self.addSwitch('s4')
        self.addLink(switch1,switch2)
        self.addLink(switch2,switch3)

        host = self.addHost('h1', mac=macs[0], ip=ips[0])
        self.addLink(host, switch1)
        host = self.addHost('h2', mac=macs[1], ip=ips[1])
        self.addLink(host, switch2)
        host = self.addHost('h3', mac=macs[2], ip=ips[2])
        self.addLink(host, switch3)
        host = self.addHost('evil', mac=macs[3], ip=ips[3])
        self.addLink(host, eve)


def scenario():
    topo = TreeWithMalicious()
    net = Mininet(topo, build=False)
    control = RemoteController('c', ip='192.168.56.102')
    net.addController(control)
    net.build()
    net.start()
    # print "Dumping host connections"
    # dumpNodeConnections(net.hosts)
    # print "Dumping switches"
    # dumpPorts(net.switches)
    # print "Testing network connectivity"
    # net.pingAll()
    CLI(net)
    net.stop()

if __name__ == '__main__':
    setLogLevel('info')
    scenario()
