#!/usr/bin/python

from mininet.topo import Topo
from mininet.net import Mininet
from mininet.util import *
from mininet.node import RemoteController
from mininet.log import setLogLevel
from mininet.cli import CLI
import cmd
import sys


macs = ['ba:d1:de:a0:00:00', '02:02:02:02:02:02', '03:03:03:03:03:03', 'fa:ce:fa:ce:b0:0c', '04:04:04:04:04:04', '05:05:05:05:05:05', '1a:51:ad:53:55:55', '06:06:06:06:06:06', '07:07:07:07:07:07', 'aa:bb:cc:cc:bb:aa', '08:08:08:08:08:08', '09:09:09:09:09:09']
ips = ['10.0.0.'+str(i+1)+'/24' for i in range(12)]


class spider(Topo):

    def build(self):
        switchs = []
        for i in range(4):
            switch = self.addSwitch('s'+str(i+1), protocols="OpenFlow13")
            switchs.append(switch)
            for j in range(3):
                host = self.addHost('h'+str(i*3+j+1), mac=macs[i*3+j], ip=ips[i*3+j])
                self.addLink(host, switch)

        switch = self.addSwitch('s5', protocols="OpenFlow13")
        switchs.append(switch)

        self.addLink(switchs[0], switchs[4])
        self.addLink(switchs[1], switchs[4])
        self.addLink(switchs[2], switchs[4])
        self.addLink(switchs[2], switchs[3])


def scenario(controller_ip):
    topo = spider()
    net = Mininet(topo, build=False)
    control = RemoteController('c', ip=controller_ip)
    net.addController(control)
    net.build()
    net.start()
    CLI(net)
    net.stop()

if __name__ == '__main__':
    setLogLevel('info')

    if len(sys.argv)>1:
        controller_ip = sys.argv[1]
    else:
        controller_ip = '192.168.58.2'

    scenario(controller_ip)
