/**
 * 
 */
package cn.zjp.mock.network;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.pcap4j.core.BpfProgram.BpfCompileMode;
import org.pcap4j.core.NotOpenException;
import org.pcap4j.core.PacketListener;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.core.PcapNetworkInterface;
import org.pcap4j.core.PcapNetworkInterface.PromiscuousMode;
import org.pcap4j.core.Pcaps;
import org.pcap4j.packet.ArpPacket;
import org.pcap4j.packet.EthernetPacket;
import org.pcap4j.packet.IcmpV4EchoPacket;
import org.pcap4j.packet.IcmpV4EchoReplyPacket;
import org.pcap4j.packet.IpV4Packet;
import org.pcap4j.packet.IpV4Packet.IpV4Tos;
import org.pcap4j.packet.Packet;
import org.pcap4j.packet.namednumber.ArpHardwareType;
import org.pcap4j.packet.namednumber.ArpOperation;
import org.pcap4j.packet.namednumber.EtherType;
import org.pcap4j.packet.namednumber.IpNumber;
import org.pcap4j.packet.namednumber.IpVersion;
import org.pcap4j.util.ByteArrays;
import org.pcap4j.util.MacAddress;

/**
 * {@link AbstractNode is a helper class to simulate a network Node}
 * 
 */
public abstract class AbstractNode {
	
	public static final int SNAPLEN_MIN = 86;
	public static final int SNAPLEN_MAX = 64 * 1024;
	public static final int RETRY = 3;
	public static final int INTRVL_MIN = 10; // 10 mill
	public static final int INTRVL_MAX = 1000; // 1000 mill 
	public static final int ZERO_INDEX = 0;
	
	private static final Logger loger = LogManager.getLogger(AbstractNode.class.getName());
	private static short ICMP_ECHO_ID = 0;
	private static short ICMP_ECHO_SEQ = 0;
	
	private ExecutorService pcapPool  = Executors.newSingleThreadExecutor();
	protected InetAddress host;
	protected MacAddress routerMac;
	protected InetAddress router;
	protected static MacAddress resolvedMac=MacAddress.getByName("00:00:00:00:00:00");
	protected volatile PcapNetworkInterface attachNif = null; // the attached physical port for pcap
	protected MacAddress hwAddress;
	
	
	AbstractNode(){
		//only allow extending from same package
	}
	
	AbstractNode(String defRouter){
		try {
			router = InetAddress.getByName(defRouter);
		} catch (UnknownHostException e1){
			e1.printStackTrace();
		}
	}
	
	AbstractNode(String defRouter, String pseudoAddress){
		this(defRouter);
		try {
			this.host = InetAddress.getByName(pseudoAddress);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
	
	AbstractNode(String defRouter, String pseudoAddr, String pseudoMac){
		this(defRouter, pseudoAddr);
		this.hwAddress = MacAddress.getByName(pseudoMac);
	}
	
	public String hostName(){
		if(null == host){
			// TODO select the address with same subnet as router
			loger.warn("Not set \'host\', try to get ip addr from attached interface");
			return attachNif.getAddresses().get(ZERO_INDEX).toString();
		}
		return host.toString();
	}
	
	public InetAddress getLocalAddr(){
		if (null == host){
			loger.warn("Not set \'host\', try to get ip addr from attached interface");
			return attachNif.getAddresses().get(ZERO_INDEX).getAddress();
		}
		return host;
	}
	
	public MacAddress getLocalMac(){
		if (null == hwAddress){
			return MacAddress.getByAddress(attachNif.getLinkLayerAddresses().get(ZERO_INDEX).getAddress());
		}
		return hwAddress;
	}
	
	public String getNextHop(){
		if(null == router) return InetAddress.getLoopbackAddress().getHostAddress();
		return router.getHostAddress();
	}
	
	/**
	 * setup {@link PcapNetworkInterface} according to the {@code ifName}, 
	 * and send gratuitous ARP to announce itself
	 * and ARP resolve for {@code router}
	 */
	public void attach(String ifName){
		if(null == ifName){
			throw new IllegalStateException("Invalid parameter");
		}
		try {
			attachNif = Pcaps.getDevByName(ifName);
			if(null == attachNif ){
				throw new IllegalStateException("Not found interface: " + ifName);
			} else {
				doDAD(getLocalAddr(), RETRY, INTRVL_MAX);
			}
		} catch (PcapNativeException e) {
			e.printStackTrace();
		}
	}
	
	public void resolveNextHop(){
		routerMac = arpResolve(router);
		if(loger.isDebugEnabled()){
			loger.debug("RouterMac:" + routerMac.toString());
		}
	}
	
	public void echoNextHop() {
		echoRequest(this.router);
	}
	
	public boolean echoRequest(InetAddress target){
		try {
			PcapHandle rhandle = attachNif.openLive(86, PromiscuousMode.PROMISCUOUS, INTRVL_MIN);
			PcapHandle shandle = attachNif.openLive(86, PromiscuousMode.PROMISCUOUS, INTRVL_MIN);
			rhandle.setFilter("icmp and src host " + target.getHostAddress() + " and dst host " + this.getLocalAddr().getHostAddress(), BpfCompileMode.OPTIMIZE);
			
			PacketListener echoListener = new PacketListener(){
				@Override
				public void gotPacket(Packet packet) {
					if(packet.contains(IcmpV4EchoReplyPacket.class)){
						IcmpV4EchoReplyPacket echoReply = packet.get(IcmpV4EchoReplyPacket.class);
						if(null != echoReply){
							loger.info("Receive echoReply");
						}
					}
				}	
			};
			
			PcapDump capture = new PcapDump(rhandle, echoListener);
			pcapPool.execute(capture);
			ICMP_ECHO_ID++;
			ICMP_ECHO_SEQ++;
			Packet echoReq = genEchoReq(target, ICMP_ECHO_ID, ICMP_ECHO_SEQ);
			shandle.sendPacket(echoReq);
		} catch (PcapNativeException e) {
			e.printStackTrace();
		} catch (NotOpenException e) {
			e.printStackTrace();
		}
		
		
		return false;
		
	}
	
	private Packet genEchoReq(InetAddress target, short identifier, short seq) {
		IcmpV4EchoPacket.Builder icmpV4Builder = new IcmpV4EchoPacket.Builder();
		icmpV4Builder.identifier(identifier)
		.sequenceNumber(seq);
		
		IpV4Packet.Builder ipV4Builder = new IpV4Packet.Builder();
		IpV4Tos tos = new IpV4Tos() {
			private static final long serialVersionUID = 7709932961444865676L;

			@Override
			public byte value() {
				// TODO Auto-generated method stub
				return 0;
			}
		};
		ipV4Builder
		.version(IpVersion.IPV4)
		.tos(tos)
		.protocol(IpNumber.ICMPV4)
		.srcAddr((Inet4Address) getLocalAddr())
		.dstAddr((Inet4Address) target)
		.payloadBuilder(icmpV4Builder)
		.correctChecksumAtBuild(true)
		.correctLengthAtBuild(true);
		
		EthernetPacket.Builder etherBuilder = new EthernetPacket.Builder();
		etherBuilder.dstAddr(arpResolve(target))
		.srcAddr(getLocalMac())
		.type(EtherType.IPV4)
		.payloadBuilder(ipV4Builder)
		.paddingAtBuild(true);
		
		return etherBuilder.build();
	}

	public MacAddress arpResolve(InetAddress target) {
		try {
			PcapHandle rhandle = attachNif.openLive(86, PromiscuousMode.PROMISCUOUS, INTRVL_MIN);
			PcapHandle shandle = attachNif.openLive(86, PromiscuousMode.PROMISCUOUS, INTRVL_MIN);
			rhandle.setFilter("arp and src host " + target.getHostAddress() 
			+ " and dst host " + host.getHostAddress() 
			+ " and ether dst " + hwAddress.toString(), 
			BpfCompileMode.OPTIMIZE);
			
			PacketListener arpListener = new PacketListener(){

				@Override
				public void gotPacket(Packet packet) {
					if(packet.contains(ArpPacket.class)){
						ArpPacket arp = packet.get(ArpPacket.class);
						ArpPacket.ArpHeader header = arp.getHeader();
						if(header.getOperation().equals(ArpOperation.REPLY)){
							resolvedMac = arp.getHeader().getSrcHardwareAddr();
						}
					}
				}	
			};
			
			PcapDump capture = new PcapDump(rhandle, arpListener);
			pcapPool.execute(capture);
			Packet arpReq = genARPRequest(router, getLocalAddr(), getLocalMac());
			for(int i = 0; i < 3; i++){
				shandle.sendPacket(arpReq);
				Thread.sleep(1000);
			}
			shandle.close();
		} catch (PcapNativeException e) {
			e.printStackTrace();
		} catch (NotOpenException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return resolvedMac;
	}

	private void doDAD(InetAddress target, int retry, int interval){
		Packet packet = genGratuitousARP(target);
		if(null == packet) {
			throw new IllegalStateException("Null packet");
		}
		try {
			PcapHandle transmit = attachNif.openLive(0, PromiscuousMode.PROMISCUOUS, 10);
			for (int i = 0; i < retry; i++) {
				transmit.sendPacket(packet);
				Thread.sleep(interval);
			}
		} catch (PcapNativeException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (NotOpenException e) {
			e.printStackTrace();
		} 
	}
	
	protected Packet genGratuitousARP(InetAddress target){
		return genARPRequest(target, getLocalAddr(), getLocalMac());
	}
	
	protected Packet genARPRequest(InetAddress target, InetAddress srcIp, MacAddress srcMac) {
		ArpPacket.Builder arpBuilder = new ArpPacket.Builder();
		
		arpBuilder
		.dstProtocolAddr(target)
		.dstHardwareAddr(MacAddress.ETHER_BROADCAST_ADDRESS)
		.srcProtocolAddr(srcIp)
		.srcHardwareAddr(srcMac)
		.protocolType(EtherType.IPV4)
		.protocolAddrLength((byte) ByteArrays.INET4_ADDRESS_SIZE_IN_BYTES)
		.hardwareType(ArpHardwareType.ETHERNET)
		.hardwareAddrLength((byte) MacAddress.SIZE_IN_BYTES)
		.operation(ArpOperation.REQUEST);
		
		EthernetPacket.Builder ethBuilder = new EthernetPacket.Builder();
		
		ethBuilder.dstAddr(MacAddress.ETHER_BROADCAST_ADDRESS)
		.srcAddr(srcMac)
		.type(EtherType.ARP)
		.payloadBuilder(arpBuilder)
		.paddingAtBuild(true);
		
		return ethBuilder.build();	
	}
}
