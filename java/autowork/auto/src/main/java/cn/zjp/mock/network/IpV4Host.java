package cn.zjp.mock.network;

import java.net.Inet4Address;
import java.net.InetAddress;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.pcap4j.core.PacketListener;
import org.pcap4j.packet.ArpPacket;
import org.pcap4j.packet.ArpPacket.ArpHeader;
import org.pcap4j.packet.EthernetPacket;
import org.pcap4j.packet.IcmpV4EchoPacket;
import org.pcap4j.packet.IpV4Packet;
import org.pcap4j.packet.Packet;
import org.pcap4j.packet.namednumber.ArpHardwareType;
import org.pcap4j.packet.namednumber.ArpOperation;
import org.pcap4j.packet.namednumber.EtherType;
import org.pcap4j.util.ByteArrays;
import org.pcap4j.util.MacAddress;

class IpV4Host  implements ListenOn {
	
	private static final Logger loger = LogManager.getLogger(IpV4Host.class.getName());
	
	private EthernetHost underLayer;
	private Inet4Address ipAddr;

	public IpV4Host(EthernetHost ethLayer, Inet4Address ipAddr){
		this.underLayer = ethLayer;
		this.setIpAddr(ipAddr);
		underLayer.setListener(new IpV4PacketEntry(underLayer, ipAddr));
	}

	public Inet4Address getIpAddr() {
		return ipAddr;
	}

	public void setIpAddr(Inet4Address ipAddr) {
		this.ipAddr = ipAddr;
	}
	
	@Override
	public void start() {
		underLayer.start();
	}

	@Override
	public void stop() {
		underLayer.stop();
	}

	@Override
	public void setListener(PacketListener listener) {
		underLayer.setListener(listener);
	}

	@Override
	public boolean isCapturing() {
		return underLayer.isCapturing();
	}
	
	
	private class IpV4PacketEntry implements PacketListener {
		private EthernetHost underLayer;
		private InetAddress ipAddr;
		
		public IpV4PacketEntry(EthernetHost underLayer, InetAddress ipAddr){
			this.underLayer = underLayer;
			this.ipAddr = ipAddr;
		}
		
		@Override
		public void gotPacket(Packet packet) {
			this.handleArpReq(packet);
			//this.handleIpV4(packet);
		}

		private void handleIpV4(Packet packet) {
			IpV4Packet ipPacket = packet.get(IpV4Packet.class);
			handleEchoReq(ipPacket);
		}

		private void handleArpReq(Packet packet) {
			if(packet.contains(ArpPacket.class)){
				ArpHeader header = packet.get(ArpPacket.class).getHeader();
				if(header.getOperation().equals(ArpOperation.REQUEST) 
						&& header.getDstProtocolAddr().equals(this.ipAddr)){
					loger.info("Receive ARP Request for myself.");
					
					ArpPacket.Builder arpB = new ArpPacket.Builder();
					MacAddress dstMac = header.getSrcHardwareAddr();
					InetAddress dstIP = header.getSrcProtocolAddr();
					MacAddress myMac = underLayer.getMac();
					
					arpB
					.hardwareType(ArpHardwareType.ETHERNET)
					.protocolType(EtherType.IPV4)
					.hardwareAddrLength((byte)MacAddress.SIZE_IN_BYTES)
					.protocolAddrLength((byte) ByteArrays.INET4_ADDRESS_SIZE_IN_BYTES)
					.dstHardwareAddr(dstMac)
					.srcHardwareAddr(myMac)
					.dstProtocolAddr(dstIP)
					.srcProtocolAddr(this.ipAddr)
					.operation(ArpOperation.REPLY);
					EthernetPacket.Builder eb = new EthernetPacket.Builder();
					eb
					.dstAddr(dstMac)
					.srcAddr(myMac)
					.payloadBuilder(arpB)
					.type(EtherType.ARP)
					.paddingAtBuild(true);
					underLayer.sendPacket(eb.build());
				}
			}
		}
		
		private void handleEchoReq(IpV4Packet packet){
			if(packet.contains(IcmpV4EchoPacket.class)){
				loger.info("Receive IPv4 echo pcaket to myself.");
				Inet4Address dstIp = packet.get(IpV4Packet.class).getHeader().getDstAddr();
				
			}
		}
		
	}

}
