from threading import Thread
from multiprocessing import Process, Queue
from scapy.all import *


class Sniff_thread:

    def __init__(self, iface, dst, sport, dport, count = 100000):
        self.iface = iface
        self.dst = dst
        self.sport = sport
        self.dport = dport
        self.count = count
        self.q = Queue.Queue()

    def threaded_sniff_target(self, q):
        sniff(iface = self.iface, count = self.count, filter = "tcp src port %s and tcp dst port %s"%(self.sport,self.dport), prn = lambda x : q.put(x))

    def threaded_sniff(self):
        sniffer = Thread(target = self.threaded_sniff_target, args = (self.q,))
        sniffer.daemon = True
        sniffer.start()

    def get_packets(self):
        r = []
        while not self.q.empty():
            r.append(self.q.get())
        return r
