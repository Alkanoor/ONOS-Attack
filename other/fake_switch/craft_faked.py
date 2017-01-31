import time
from scapy.all import *


class Craft_faked:

    def __init__(self, tcp_connection, type, args):
        self.connection = tcp_connection
        self.type = type
        self.args = args
        self.craft()

    def craft(self):
        self.pkt = ""
        if self.type == "LLDP":
            self.target_addr = self.args[1]
            self.src_addr = self.args[0]

            version = "04"
            pkt_in = "0a"
            transaction_id = "00000000"
            buffer_id = "ffffffff"
            reason = "01"
            table_id = "00"
            cookie = "0000776655443322"
            ofpmt_oxm = "0001"
            length_oxm = "000c"
            ofpxmt_ofb_in_port = "8000"
            has_mask = "00"
            length_following = "04"
            value = "00000001"
            padding = "000000000000"
            self.eth = str(Ether(dst=self.target_addr, src=self.src_addr, type=0x88cc))
            chassis = "020704"+self.args[2].replace(':','')
            port_number = int(self.args[3])
            port = "040502"+binascii.hexlify(struct.pack('>I',port_number))
            ttl = "06020078"
            part1 = "fe12a42305014f4e4f5320446973636f76657279"
            part2 = "fe13a42305026f663a"+binascii.hexlify(self.args[2].replace(':',''))
            self.eth += binascii.unhexlify(chassis+port+ttl+part1+part2+"0000")

            payload_length_str = "0000"
            length_str = "0000"
            self.head = binascii.unhexlify(version+pkt_in+length_str+transaction_id+buffer_id+payload_length_str+reason+table_id+cookie+ofpmt_oxm+length_oxm+ofpxmt_ofb_in_port+has_mask+length_following+value+padding)

            print(binascii.hexlify(self.head))
            print(binascii.hexlify(self.eth))

            payload_length = len(self.eth)
            payload_length_str = binascii.hexlify(struct.pack('>H', payload_length))
            length = payload_length+len(self.head)
            length_str = binascii.hexlify(struct.pack('>H', length))
            self.head = binascii.unhexlify(version+pkt_in+length_str+transaction_id+buffer_id+payload_length_str+reason+table_id+cookie+ofpmt_oxm+length_oxm+ofpxmt_ofb_in_port+has_mask+length_following+value+padding)

            print(binascii.hexlify(self.head))
            self.pkt = self.head+self.eth
            print(binascii.hexlify(self.pkt))

        elif self.type == "PING":
            self.src_mac = self.args[1]
            self.dst_mac = self.args[0]
            self.src_ip = self.args[2]
            self.dst_ip = self.args[3]

            version = "04"
            pkt_in = "0a"
            transaction_id = "00000000"
            buffer_id = "ffffffff"
            reason = "01"
            table_id = "00"
            cookie = "0000776655443322"
            ofpmt_oxm = "0001"
            length_oxm = "000c"
            ofpxmt_ofb_in_port = "8000"
            has_mask = "00"
            length_following = "04"
            value = "00000001"
            padding = "000000000000"
            self.eth = self.src_mac.replace(':','')+self.dst_mac.replace(':','')+"0800450000547c6a40004001cc59"+self.src_ip+self.dst_ip+"0800c3a749450001a5bf1c58000000006327070000000000101112131415161718191a1b1c1d1e1f202122232425262728292a2b2c2d2e2f3031323334353637"
            self.eth = binascii.unhexlify(self.eth)

            payload_length_str = "0000"
            length_str = "0000"
            self.head = binascii.unhexlify(version+pkt_in+length_str+transaction_id+buffer_id+payload_length_str+reason+table_id+cookie+ofpmt_oxm+length_oxm+ofpxmt_ofb_in_port+has_mask+length_following+value+padding)

            print(binascii.hexlify(self.head))
            print(binascii.hexlify(self.eth))

            payload_length = len(self.eth)
            payload_length_str = binascii.hexlify(struct.pack('>H', payload_length))
            length = payload_length+len(self.head)
            length_str = binascii.hexlify(struct.pack('>H', length))
            self.head = binascii.unhexlify(version+pkt_in+length_str+transaction_id+buffer_id+payload_length_str+reason+table_id+cookie+ofpmt_oxm+length_oxm+ofpxmt_ofb_in_port+has_mask+length_following+value+padding)

            print(binascii.hexlify(self.head))
            self.pkt = self.head+self.eth

        elif self.type == "ARP":
            self.dst_mac = self.args[1]
            self.src_mac = self.args[0]
            self.src_ip = self.args[2]
            self.dst_ip = self.args[3]

            version = "04"
            pkt_in = "0a"
            transaction_id = "00000000"
            buffer_id = "ffffffff"
            reason = "01"
            table_id = "00"
            cookie = "0000776655443322"
            ofpmt_oxm = "0001"
            length_oxm = "000c"
            ofpxmt_ofb_in_port = "8000"
            has_mask = "00"
            length_following = "04"
            value = "00000001"
            padding = "000000000000"
            self.eth = self.dst_mac.replace(':','')+self.src_mac.replace(':','')+"08060001080006040001"+self.src_mac.replace(':','')+self.src_ip+"000000000000"+self.dst_ip
            self.eth = binascii.unhexlify(self.eth)

            payload_length_str = "0000"
            length_str = "0000"
            self.head = binascii.unhexlify(version+pkt_in+length_str+transaction_id+buffer_id+payload_length_str+reason+table_id+cookie+ofpmt_oxm+length_oxm+ofpxmt_ofb_in_port+has_mask+length_following+value+padding)

            print(binascii.hexlify(self.head))
            print(binascii.hexlify(self.eth))

            payload_length = len(self.eth)
            payload_length_str = binascii.hexlify(struct.pack('>H', payload_length))
            length = payload_length+len(self.head)
            length_str = binascii.hexlify(struct.pack('>H', length))
            self.head = binascii.unhexlify(version+pkt_in+length_str+transaction_id+buffer_id+payload_length_str+reason+table_id+cookie+ofpmt_oxm+length_oxm+ofpxmt_ofb_in_port+has_mask+length_following+value+padding)

            print(binascii.hexlify(self.head))
            self.pkt = self.head+self.eth

    def send(self):
        self.connection.send_data_no_previous(self.pkt, False)
