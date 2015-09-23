/**
 * 
 */
package cn.zjp.mock.network;

import java.net.InetAddress;
import java.util.concurrent.ExecutorService;
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
public abstract class Node implements ListenOn {
		
	private static final Logger loger = LogManager.getLogger(Node.class.getName());

	protected static final int SNAPLEN_MAX = 65535;
	protected static final int CAP_TIME_OUT = 10;

	private static final long NODE_SHUTDOWN_TIMEOUT = 1;
	
	// the 3 fields dev, pcapNif, hwAddr should not be change after constructed
	private final String dev;
	protected final PcapNetworkInterface pcapNif;
	protected PcapHandle captureHandler;
	protected PcapHandle sendHandler;
	protected ExecutorService capExecutor;
	protected PacketListener packetListener = null;
	protected final Object lock = new Object();
	
	protected boolean capturing = false;
	
	Node(String dev){
		if(dev == null) {
			StringBuilder msg = new StringBuilder();
			msg.append("dev: " + dev);
			throw new NullPointerException(msg.toString());
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
		loger.info("Node setup on interface: \'"  + dev + "\'");
	}
	
	public String getDev(){
		return dev;
	}
	
	public PcapNetworkInterface getPcapNif(){
		return pcapNif;
	}

	@Override
	public abstract void start();
	
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

	public void sendPacket(Packet packet) {
		try {
			if (sendHandler == null || !sendHandler.isOpen()) {
				sendHandler = pcapNif.openLive(SNAPLEN_MAX, PromiscuousMode.PROMISCUOUS, CAP_TIME_OUT);
			}
			sendHandler.sendPacket(packet);
		} catch (PcapNativeException e) {
			e.printStackTrace();
		} catch (NotOpenException e) {
			e.printStackTrace();
		}
		if(loger.isDebugEnabled()){
			loger.info("SEND: -->\n" + packet);
		}
	}

	@Override
	public void setListener(PacketListener listener) {
		this.packetListener = listener;	
	}
	
	@Override
	public boolean isCapturing() {
		return capturing;
	}
	
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
			if(null != captureHandler && captureHandler.isOpen()) captureHandler.close();
			if(null != sendHandler && sendHandler.isOpen()) sendHandler.close();
		}
		loger.info("Node has been shutdown.");
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
