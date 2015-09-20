/**
 * 
 */
package cn.zjp.mock.network;

import java.net.InetAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.pcap4j.core.NotOpenException;
import org.pcap4j.core.PacketListener;
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
import org.pcap4j.util.MacAddress;

/**
 * {@link Node} capturing packets on the network interface of {@code dev}
 * 
 */
public class Node implements Simulatable {
		
	private static final Logger loger = LogManager.getLogger(Node.class.getName());

	private static final int CAP_TIME_OUT = 10;

	private static final long NODE_SHUTDOWN_TIMEOUT = 1;
	
	private final String dev;
	private final PcapNetworkInterface pcapNif;
	private PcapHandle captureHandler;
	private PcapHandle sendHandler;
	private ExecutorService capExecutor;
	private PacketListener packetListener = new AllPacketListener();
	private final Object lock = new Object();
	
	private boolean capturing;
	
	@SuppressWarnings("null")
	Node(String dev){
		if(dev == null) {
			throw new NullPointerException("dev: " + dev.toString());
		}
		this.dev = dev;
		
		PcapNetworkInterface  pnif = null;
		try {
			pnif = Pcaps.getDevByName(dev);
		} catch (PcapNativeException e) {
			throw new IllegalStateException(e);
		}
		if(pnif == null){
			throw new IllegalStateException("NO NIF was found, may no dev named \'" 
					+ dev 
					+ "\' or not be granted capabilities to java by superuser");
		}
		this.pcapNif = pnif;
		try {
			captureHandler = pcapNif.openLive(0, PromiscuousMode.PROMISCUOUS, CAP_TIME_OUT);
			sendHandler = pcapNif.openLive(0, PromiscuousMode.PROMISCUOUS, CAP_TIME_OUT);
		} catch (PcapNativeException e) {
			throw new IllegalStateException(e);
		}
		this.capExecutor = Executors.newSingleThreadExecutor();
		
		loger.info("Node setup on interface: \'"  + dev + "\'");
	}
	
	public String getDev(){
		return dev;
	}
	
	public PcapNetworkInterface getPcapNif(){
		return pcapNif;
	}

	@Override
	public void start() {
		synchronized (lock) { // synchronized for capturing packets
			if(capturing){
				loger.info("Aready capuring");
				return;
			}
			capExecutor.execute(new CaptureDump());
			capturing = true;
		}
		loger.info("Start to capture.");
	}

	@Override
	public void stop() {
		synchronized (lock) { // synchronized to break the loop of capturing down
			if (!capturing) {
				loger.info("Aready stoped, not capturing.");
				return;
			}
			try {
				captureHandler.breakLoop();
			} catch (NotOpenException e) {
				e.printStackTrace();
			}
			capturing = false;
		}
		loger.info("Stop to capture.");
	}

	@Override
	public void sendPacket(Packet packet) {
		if(sendHandler == null  || ! sendHandler.isOpen()){
			throw new IllegalStateException("send handle: " + sendHandler + "cannot work.");
		}
		try {
			sendHandler.sendPacket(packet);
		} catch (PcapNativeException e) {
			e.printStackTrace();
		} catch (NotOpenException e) {
			e.printStackTrace();
		}
		loger.info("send: -->\n" + packet);
	}

	@Override
	public void AddListener(PacketListener listener) {
		this.packetListener = listener;	
	}
	
	@Override
	public boolean isCapturing() {
		return capturing;
	}
	
	@Override
	public void shutdown(){
		synchronized (lock) {
			if (capturing) {
				stop();
			}
			capExecutor.shutdown();
			try {
				if (!capExecutor.awaitTermination(NODE_SHUTDOWN_TIMEOUT, TimeUnit.SECONDS)) {
					loger.warn("Shutdown timeout.");
				}
			} catch (InterruptedException e) {
				loger.error(e);
			}
			captureHandler.close();
			sendHandler.close();
		}
		loger.info("Node has been shutdown.");
	}
	
	private class CaptureDump implements Runnable {

		@Override
		public void run() {
			if(captureHandler == null || !captureHandler.isOpen()){
				throw new IllegalStateException("capture handle: " + captureHandler + "cannot work.");
			}
			
			try {
				captureHandler.loop(-1, packetListener);
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
	
	private class AllPacketListener implements PacketListener {

		@Override
		public void gotPacket(Packet packet) {
			if(loger.isDebugEnabled()){
				loger.debug("recv: <--\n"  + packet);
			}
			return;
		}
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
