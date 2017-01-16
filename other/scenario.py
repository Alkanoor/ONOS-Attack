import time
from scapy.all import *


class Scenario:

    def __init__(self, sniff_thread, tcp_handshake):
        self.sniff_thread = sniff_thread
        self.tcp_handshake = tcp_handshake

    def read_unblocking(self):
        tmp = self.sniff_thread.get_packets()
        if len(tmp)>0:
            self.last = tmp[-1]
        return tmp

    def read_blocking(self):
        tmp = self.sniff_thread.get_packets()
        while len(tmp) == 0:
            time.sleep(0.1)
            tmp = self.sniff_thread.get_packets()
            if self.tcp_handshake.communication_ended():
                print("Communication Ended !")
                exit()
        self.last = tmp[-1]
        return tmp

    def react_to(self, pkt):
        return []

    def analyse_and_send(self, p):
        return None

    def launch(self):
        pass

    def end(self):
        self.tcp_handshake.send_fin()
        while not self.tcp_handshake.communication_ended():
            tmp = self.read_blocking()
            for i in tmp:
                self.tcp_handshake.analyse_state_and_answer(i)
