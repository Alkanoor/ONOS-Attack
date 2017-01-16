#!/usr/bin/python

from mininet.topo import Topo
from mininet.net import Mininet
from mininet.util import *
from mininet.node import RemoteController
from mininet.log import setLogLevel
from mininet.cli import CLI
import cmd


macs = ['ba:d1:de:a0:00:00', 'aa:bb:cc:cc:bb:aa', 'fa:ce:fa:ce:b0:0c', '1a:51:ad:53:55:55', '3c:3c:3c:3c:3c:3c']
ips = ['10.0.0.1/24', '10.0.0.2/24', '10.0.0.3/24', '10.0.0.4/24', '10.0.0.5/24']

class Basic(Topo):

    def build(self):
        switch1 = self.addSwitch('s1')
        host = self.addHost('h1', mac=macs[0], ip=ips[0])
        self.addLink(host, switch1)
        switch2 = self.addSwitch('s2')
        host = self.addHost('h2', mac=macs[1], ip=ips[1])
        self.addLink(host, switch2)
        switch3 = self.addSwitch('s3')
        host = self.addHost('h3', mac=macs[2], ip=ips[2])
        self.addLink(host, switch3)
        switch4 = self.addSwitch('s4')
        host = self.addHost('h4', mac=macs[3], ip=ips[3])
        self.addLink(host, switch4)
        switch5 = self.addSwitch('s5')
        host = self.addHost('h5', mac=macs[4], ip=ips[4])
        self.addLink(host, switch5)

        self.addLink(switch1, switch2)
        self.addLink(switch2, switch3)
        self.addLink(switch3, switch4)
        self.addLink(switch2, switch5)


def scenario():
    topo = Basic()
    net = Mininet(topo, build=False)
    control = RemoteController('c', ip='192.168.56.102')
    net.addController(control)
    net.build()
    net.start()
    CLI(net)
    net.stop()

if __name__ == '__main__':
    setLogLevel('info')
    scenario()
