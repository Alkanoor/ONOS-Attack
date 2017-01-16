#!/usr/bin/python

from mininet.topo import Topo
from mininet.net import Mininet
from mininet.util import *
from mininet.node import RemoteController
from mininet.log import setLogLevel
from mininet.cli import CLI
import cmd


macs = ['ba:d1:de:a0:00:00', 'aa:bb:cc:cc:bb:aa']
ips = ['192.168.56.58/24', '192.168.56.90/24']

class Basic(Topo):

    def build(self):
        switch = self.addSwitch('s1')
        host = self.addHost('h1', mac=macs[0], ip=ips[0])
        self.addLink(host, switch)
        switch = self.addSwitch('s2')
        host = self.addHost('h2', mac=macs[1], ip=ips[1])
        self.addLink(host, switch)


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
