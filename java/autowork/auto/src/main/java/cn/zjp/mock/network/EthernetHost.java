package cn.zjp.mock.network;


import java.util.concurrent.Executors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.pcap4j.core.BpfProgram.BpfCompileMode;
import org.pcap4j.core.NotOpenException;
import org.pcap4j.core.PacketListener;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.core.PcapNetworkInterface.PromiscuousMode;
import org.pcap4j.core.Pcaps;
import org.pcap4j.packet.EthernetPacket;
import org.pcap4j.packet.Packet;
import org.pcap4j.packet.namednumber.EtherType;
import org.pcap4j.util.MacAddress;

public class EthernetHost extends Node {
	
	private static final Logger loger = LogManager.getLogger(EthernetHost.class.getName());
	
	private final MacAddress hwAddr;

	EthernetHost(String dev, MacAddress pseudoMAC) {
		super(dev);
		if(null == pseudoMAC || MacAddress.ETHER_BROADCAST_ADDRESS == pseudoMAC){
			throw new IllegalStateException("hwAddr: " + pseudoMAC.toString());
		}
		this.hwAddr = pseudoMAC;
	}
	
	public MacAddress getMac() {
		return hwAddr;
	}
	
	@Override
	public void start() {
		synchronized (lock) { // synchronized for capturing packets
			if(capturing){
				loger.info("Aready capuring");
				return;
			}
			try {
				if(captureHandler == null || ! captureHandler.isOpen()){
					captureHandler = pcapNif.openLive(SNAPLEN_MAX, PromiscuousMode.PROMISCUOUS, CAP_TIME_OUT);
					// setup a generic pcap_filter for Ethernet 
					StringBuilder filter_exp_eth = new StringBuilder();
					filter_exp_eth
					// packets to itself
					.append("(")
					.append("ether dst ")
					.append(Pcaps.toBpfString(hwAddr))
					.append(")")
					.append(" or ")
					// packets for ARP request
					.append("(")
					.append("arp and ether broadcast ")
					.append(")")
					.append(" or ")
					// Ethernet multicast
					.append("(")
					.append("ether multicast")
					.append(")");
					if(loger.isDebugEnabled()) loger.debug("Filter expression for EthernetHost: " + filter_exp_eth.toString() );
					captureHandler.setFilter(filter_exp_eth.toString(), BpfCompileMode.OPTIMIZE);
				}
			} catch (PcapNativeException e) {
				throw new IllegalStateException(e);
			} catch (NotOpenException e) {
				throw new IllegalStateException("Never to here.");
			}
			if(null == capExecutor){
				this.capExecutor = Executors.newSingleThreadExecutor();
			}	
			capExecutor.execute(new CaptureLooper());
			capturing = true;
		}
		loger.info("Start to capture.");
	}
	
	public void sendEtherPacket(Packet.Builder payloadBuilder, MacAddress dstMac, EtherType type){
		EthernetPacket.Builder ethBuilder = new EthernetPacket.Builder();
		ethBuilder
		.dstAddr(dstMac)
		.srcAddr(hwAddr)
		.payloadBuilder(payloadBuilder)
		.type(type)
		.paddingAtBuild(true);
		sendPacket(ethBuilder.build());
		
	}
	
	
	private class CaptureLooper implements Runnable {

		@Override
		public void run() {
			if(captureHandler == null || !captureHandler.isOpen()){
				throw new IllegalStateException("capture handle: " + captureHandler + "cannot work.");
			}
			
			try {
				captureHandler.loop(-1, new EthernetPacketEntry());
			} catch (PcapNativeException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (NotOpenException e) {
				e.printStackTrace();
			}
			loger.info("Stoped capture.");
		}
		
	}
	
	private class EthernetPacketEntry implements PacketListener {

		@Override
		public void gotPacket(Packet packet) {
			if(loger.isDebugEnabled()){
				loger.debug("RECV: <--\n"  + packet);
			}
			// the allowed Ethernet packets come to here
			if (null != packetListener) packetListener.gotPacket(packet);
			return;
		}
	}

}
