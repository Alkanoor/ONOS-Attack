import time
from openflow_scenario import Openflow_scenario


class Sniffing_scenario(Openflow_scenario):

    def __init__(self, sniff_thread, tcp_handshake):
        Openflow_scenario.__init__(self, sniff_thread, tcp_handshake)

    def launch(self):
        tmp = self.read_blocking()
        if len(tmp)>1:
            print("Warning : strange answering behavior (more than a SYN ACK received)")

        ended = False
        try:
            while True:
                for i in tmp:
                    self.tcp_handshake.analyse_state_and_answer(i)
                    if self.tcp_handshake.communication_ended():
                        ended = True
                        break
                tmp = self.read_blocking()
        except:
            if not ended:
                self.end()
            exit()

        if not Ended:
            self.end()
