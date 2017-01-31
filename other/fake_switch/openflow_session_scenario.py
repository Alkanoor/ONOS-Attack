import time
from scapy.all import *
from threading import Thread
from openflow_scenario import Openflow_scenario


class Openflow_session_scenario(Openflow_scenario):

    def __init__(self, sniff_thread, tcp_handshake, dpid):
        Openflow_scenario.__init__(self, sniff_thread, tcp_handshake, dpid)
        self.block = False

    def treat_and_send(self, to_send):
        self.tcp_handshake.send_data_no_previous(to_send)
        self.block = False

    def launch(self):
        tmp = self.read_blocking()
        if len(tmp)>1:
            print("Warning : strange answering behavior (more than a SYN ACK received)")

        try:
            self.tcp_handshake.analyse_state_and_answer(tmp[0])
            self.tcp_handshake.send_data(OFPTHello(),tmp[0])

            self.counter = 0
            to_send = []
            while self.counter < 100000:
                to_send.extend(self.react_to(self.read_blocking()))
                if len(to_send)>0 and not self.block:
                    self.block = True
                    treat_msg = Thread(target = self.treat_and_send, args = (to_send[0],))
                    treat_msg.daemon = True
                    treat_msg.start()
                    del to_send[0]
                time.sleep(0.05)
        except Exception,e:
            print("=================================================")
            print("=================================================")
            print("=================================================")
            print("Exception raised with "+str(e))
            print("=================================================")
            print("=================================================")
            print("=================================================")
            self.end()
            exit()
        except Exception:
            print("=================================================")
            print("=================================================")
            print("=================================================")
            print("Exception caught, ending")
            print("=================================================")
            print("=================================================")
            print("=================================================")
            self.end()
            exit()

        self.end()

    def launch_threaded(self):
        t = Thread(target = self.launch)
        t.daemon = True
        t.start()
