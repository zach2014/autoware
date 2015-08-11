/**
 * 
 */
package cn.zjp.mock.network;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.pcap4j.core.BpfProgram.BpfCompileMode;
import org.pcap4j.core.NotOpenException;
import org.pcap4j.core.PacketListener;
import org.pcap4j.core.PcapAddress;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.core.PcapNetworkInterface;
import org.pcap4j.core.PcapNetworkInterface.PromiscuousMode;
import org.pcap4j.core.Pcaps;
import org.pcap4j.packet.ArpPacket;
import org.pcap4j.packet.EthernetPacket;
import org.pcap4j.packet.Packet;
import org.pcap4j.packet.namednumber.ArpHardwareType;
import org.pcap4j.packet.namednumber.ArpOperation;
import org.pcap4j.packet.namednumber.EtherType;
import org.pcap4j.util.ByteArrays;
import org.pcap4j.util.Inet4NetworkAddress;
import org.pcap4j.util.MacAddress;

/**
 * {@link AbstractNode is a helper class to simulate a network Node}
 * 
 */
public abstract class AbstractNode {
	
	private static final Logger loger = LogManager.getLogger(AbstractNode.class.getName());
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
	
	public String hostName(){
		if(null == host){
			// TODO select the address with same subnet as router
			return attachNif.getAddresses().get(0).toString();
		}
		return host.toString();
	}
	
	public InetAddress localAddress(){
		if (null == host){
			return attachNif.getAddresses().get(0).getAddress();
		}
		return host;
	}
	
	public MacAddress hwAddress(){
		if (null == hwAddress){
			return MacAddress.getByAddress(attachNif.getLinkLayerAddresses().get(0).getAddress());
		}
		return hwAddress;
	}
	
	public String router(){
		if(null == router) return InetAddress.getLoopbackAddress().getHostAddress();
		return router.getHostAddress();
	}
	
	public void setNif(PcapNetworkInterface nif){
		if(null != nif){
			this.attachNif = nif;
		}else {
			throw new IllegalStateException("Null parameter"); 
		}
	}
	
	public void arpRequest(InetAddress ipAddr){
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
				throw new IllegalStateException("Not found interface named: " + ifName);
			} else {
				doDAD(localAddress(), 3, 500);
			}
		} catch (PcapNativeException e) {
			e.printStackTrace();
		}
	}
	
	public void resolveRouter(){
		routerMac = arpResolve(router);
	}
	
	public MacAddress arpResolve(InetAddress target) {
		try {
			PcapHandle rhandle = attachNif.openLive(86, PromiscuousMode.PROMISCUOUS, 10);
			PcapHandle shandle = attachNif.openLive(86, PromiscuousMode.PROMISCUOUS, 10);
			rhandle.setFilter("arp", BpfCompileMode.OPTIMIZE);
			
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
			capture.run();
			Packet arpReq = genARPRequest(router, host, hwAddress);
			for(int i = 0; i < 3; i++){
				shandle.sendPacket(arpReq);
				Thread.sleep(1000);
			}
			
		} catch (PcapNativeException e) {
			e.printStackTrace();
		} catch (NotOpenException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			
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
		return genARPRequest(target, localAddress(), hwAddress());
	}
	
	protected Packet genARPRequest(InetAddress target, InetAddress srcIp, MacAddress srcMac) {
		ArpPacket.Builder arpBuilder = new ArpPacket.Builder();
		
		arpBuilder.dstProtocolAddr(target)
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
