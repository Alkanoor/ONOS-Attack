#!/usr/bin/python

from mininet.topo import Topo
from mininet.net import Mininet
from mininet.util import dumpNodeConnections
from mininet.node import RemoteController
from mininet.log import setLogLevel
from mininet.cli import CLI


macs = ['de:ad:be:ef:de:ad', 'fa:ce:be:ef:fa:ce', 'f0:01:1d:ea:f0:01']
ips = ['10.0.0.2', '10.0.0.3', '35.35.35.35']

class SingleSwitchTopo(Topo):
    "Single switch connected to n hosts."
    def build(self, n=2):
        self.hosts_list = []
        switch = self.addSwitch('s1')
        # Python's range(N) generates 0..N-1
        for h in range(n):
            host = self.addHost('h%s' % (h + 1), mac=macs[h], ip=ips[h])
            self.addLink(host, switch)
            self.hosts_list.append(host)

def simpleTest():
    "Create and test a simple network"
    topo = SingleSwitchTopo(n=2)
    net = Mininet(topo, build=False)
    control = RemoteController('c', ip='192.168.56.102')
    net.addController(control)
    net.build()
    net.start()
    print "Dumping host connections"
    dumpNodeConnections(net.hosts)
    print "Testing network connectivity"
    net.pingAll()
    net.iperf(net.get('h1','h2'))
    CLI(net)
    net.stop()

if __name__ == '__main__':
    # Tell mininet to print useful information
    setLogLevel('info')
    simpleTest()
