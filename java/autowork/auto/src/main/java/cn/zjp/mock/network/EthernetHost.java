package cn.zjp.mock.network;


import java.util.concurrent.Executors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.pcap4j.core.BpfProgram.BpfCompileMode;
import org.pcap4j.core.NotOpenException;
import org.pcap4j.core.PacketListener;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.core.PcapNetworkInterface.PromiscuousMode;
import org.pcap4j.packet.Packet;
import org.pcap4j.core.Pcaps;
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
					StringBuilder gfilter_exp = new StringBuilder();
					gfilter_exp
					.append("(")
					.append("ether dst ")
					.append(Pcaps.toBpfString(hwAddr))
					.append(")")
					.append(" or ")
					.append("(")
					.append("arp and ether dst ")
					.append(Pcaps.toBpfString(MacAddress.ETHER_BROADCAST_ADDRESS))
					.append(")");
					captureHandler.setFilter(gfilter_exp.toString(), BpfCompileMode.OPTIMIZE);
				}
			} catch (PcapNativeException e) {
				throw new IllegalStateException(e);
			} catch (NotOpenException e) {
				throw new IllegalStateException("Never to here.");
			}
			
			this.capExecutor = Executors.newSingleThreadExecutor();
			capExecutor.execute(new CaptureLooper());
			capturing = true;
		}
		loger.info("Start to capture.");
	}
	
	
	private class CaptureLooper implements Runnable {

		@Override
		public void run() {
			if(captureHandler == null || !captureHandler.isOpen()){
				throw new IllegalStateException("capture handle: " + captureHandler + "cannot work.");
			}
			
			try {
				captureHandler.loop(-1, new PacketEntry());
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
	
	private class PacketEntry implements PacketListener {

		@Override
		public void gotPacket(Packet packet) {
			if(loger.isDebugEnabled()){
				loger.debug("RECV: <--\n"  + packet);
			}
			// all Ethernet packets come to here
			if (null != packetListener) packetListener.gotPacket(packet);
			return;
		}
	}

}
