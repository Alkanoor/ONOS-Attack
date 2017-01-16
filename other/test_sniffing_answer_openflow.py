import sys
from sniff_thread import *
from tcp_handshake import *
from openflow_session_scenario import *


if len(sys.argv)<2:
    print("Usage [interface]")
    exit()

t = TcpHandshake(("192.168.56.102",6633),sys.argv[1])
t.start()

sniffing_thread = Sniff_thread(sys.argv[1],"192.168.56.102",6633,t.sport,1000)
sniffing_thread.threaded_sniff()

analyse_scenario = Openflow_session_scenario(sniffing_thread,t)
analyse_scenario.launch()
