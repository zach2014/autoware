package cn.zjp.mock.network;

import java.net.Inet4Address;
import java.net.InetAddress;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.pcap4j.core.PacketListener;
import org.pcap4j.packet.ArpPacket;
import org.pcap4j.packet.ArpPacket.ArpHeader;
import org.pcap4j.packet.EthernetPacket;
import org.pcap4j.packet.IcmpV4CommonPacket;
import org.pcap4j.packet.IcmpV4EchoPacket;
import org.pcap4j.packet.IcmpV4EchoReplyPacket;
import org.pcap4j.packet.IpV4Packet;
import org.pcap4j.packet.IpV4Rfc791Tos;
import org.pcap4j.packet.Packet;
import org.pcap4j.packet.SimpleBuilder;
import org.pcap4j.packet.namednumber.ArpHardwareType;
import org.pcap4j.packet.namednumber.ArpOperation;
import org.pcap4j.packet.namednumber.EtherType;
import org.pcap4j.packet.namednumber.IcmpV4Type;
import org.pcap4j.packet.namednumber.IpNumber;
import org.pcap4j.packet.namednumber.IpVersion;
import org.pcap4j.util.ByteArrays;
import org.pcap4j.util.MacAddress;

class IpV4Host  implements ListenOn {
	
	private static final Logger loger = LogManager.getLogger(IpV4Host.class.getName());
	
	private EthernetHost underLayer;
	private Inet4Address ipAddr;

	public IpV4Host(EthernetHost ethLayer, Inet4Address ipAddr){
		this.underLayer = ethLayer;
		this.setIpAddr(ipAddr);
		underLayer.setListener(new IpV4PacketEntry());
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
	
	public void sendIpV4Packet(Packet payload, Inet4Address dstIp, MacAddress dstMac){
		IpNumber upperProto; 
		if(payload instanceof IcmpV4CommonPacket) {
			upperProto = IpNumber.ICMPV4;
		}
		else {
			throw new IllegalArgumentException("Invalid Payload:\n" + payload.toString());
		}
		IpV4Packet.Builder ipV4Builder = new IpV4Packet.Builder();
		ipV4Builder.version(IpVersion.IPV4)
				   .tos(IpV4Rfc791Tos.newInstance((byte) 0))
				   .ttl((byte) 64)
				   .identification((short) 0)
				   .protocol(upperProto)
				   .srcAddr(ipAddr)
				   .dstAddr(dstIp)
				   .payloadBuilder(new SimpleBuilder(payload))
				   .correctChecksumAtBuild(true)
				   .correctLengthAtBuild(true);
		underLayer.sendEtherPacket(ipV4Builder, dstMac, EtherType.IPV4);
	}
	
	private void accepted(Packet packet){
		if(packet.contains(ArpPacket.class)){
			ArpHeader arpHeader = packet.get(ArpPacket.class).getHeader();
			
			if(arpHeader.getDstProtocolAddr().equals(ipAddr) && arpHeader.getOperation().equals(ArpOperation.REQUEST)){
				loger.info("ARP: Receive a Request to myself.");
				doArpReply(packet);
				return;
			}
		}
		if(packet.contains(IpV4Packet.class)){
			Inet4Address targetIp = packet.get(IpV4Packet.class).getHeader().getDstAddr();
			if(targetIp.equals(ipAddr)){
				if(packet.contains(IcmpV4EchoPacket.class)){
					loger.info("IPv4: Receive icmpv4 echo packet to myself.");
					doEchoReply(packet);
				}
				return;
			}
		}
		
		if(loger.isDebugEnabled()) loger.debug("Do NOT care the packet:\n" + packet.toString());
	}
	
	private void doArpReply(Packet packet){
		MacAddress dstMac = packet.get(ArpPacket.class).getHeader().getSrcHardwareAddr();
		InetAddress dstIP = packet.get(ArpPacket.class).getHeader().getSrcProtocolAddr();
		ArpPacket.Builder arpBuilder = new ArpPacket.Builder();
		arpBuilder
		.hardwareType(ArpHardwareType.ETHERNET)
		.protocolType(EtherType.IPV4)
		.hardwareAddrLength((byte)MacAddress.SIZE_IN_BYTES)
		.protocolAddrLength((byte) ByteArrays.INET4_ADDRESS_SIZE_IN_BYTES)
		.dstHardwareAddr(dstMac)
		.srcHardwareAddr(underLayer.getMac())
		.dstProtocolAddr(dstIP)
		.srcProtocolAddr(ipAddr)
		.operation(ArpOperation.REPLY);
		underLayer.sendEtherPacket(arpBuilder, dstMac, EtherType.ARP);
	}
	
	private void doEchoReply(Packet packet){
		IpV4Packet ipV4Packet = packet.get(IpV4Packet.class);
		IcmpV4EchoPacket echoReq = packet.get(IcmpV4EchoPacket.class);
		Packet.Builder outOfIcmpBuilder = packet.getBuilder().getOuterOf(IcmpV4EchoPacket.Builder.class);
		if(ipV4Packet == null || echoReq == null || outOfIcmpBuilder == null 
			|| !(outOfIcmpBuilder instanceof IcmpV4CommonPacket.Builder)){
			throw new IllegalArgumentException("Invalid IcmpEchoPacket:\n" + packet.toString());
		}
		
		IcmpV4EchoReplyPacket.Builder echoReplayBuilder = new IcmpV4EchoReplyPacket.Builder();
		echoReplayBuilder
		.identifier(echoReq.getHeader().getIdentifier())
		.sequenceNumber(echoReq.getHeader().getSequenceNumber())
		.payloadBuilder(echoReq.getPayload().getBuilder());
		
		((IcmpV4CommonPacket.Builder)outOfIcmpBuilder).type(IcmpV4Type.ECHO_REPLY)
						.payloadBuilder(echoReplayBuilder)
						.correctChecksumAtBuild(true);
		
		sendIpV4Packet(outOfIcmpBuilder.build(), ipV4Packet.getHeader().getSrcAddr(), packet.get(EthernetPacket.class).getHeader().getSrcAddr());		
	}
	
	
	private class IpV4PacketEntry implements PacketListener {
		
		@Override
		public void gotPacket(Packet packet) {
			accepted(packet);
		}
		
	}

}
