/**
 * 
 */
package cn.zjp.mock.network;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.pcap4j.core.BpfProgram.BpfCompileMode;
import org.pcap4j.core.NotOpenException;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.core.PcapNetworkInterface;
import org.pcap4j.core.PcapNetworkInterface.PromiscuousMode;
import org.pcap4j.core.Pcaps;
import org.pcap4j.packet.Packet;
import org.pcap4j.packet.TcpPacket;

/**
 * {@link AbstractNode is a helper class to simulate a network Node}
 * 
 */
public abstract class AbstractNode {
	
	private static final Logger loger = LogManager.getLogger(AbstractNode.class.getName());
	private volatile PcapNetworkInterface nif = null;
	private volatile boolean dualStack;
	private PcapHandle receiveHandle;
	private PcapHandle sendHandle;
	
	AbstractNode(){
		//only allow extending from same package
	}
	AbstractNode(String nifName, Boolean dual){
		this.dualStack = dual;
		try {
			this.nif = Pcaps.getDevByName(nifName);
			if(nif == null) {
				loger.error("No interface named: " + nifName); 
				throw new IllegalStateException("Invalid interface name: " + nifName);
			}
		} catch (PcapNativeException e) {
			e.printStackTrace();
		}
	}
	public void onLink(){
		/*
		 * setup network Node on the given {@code ifName} 
		 * and send gratuitous ARP to announce itself
		 * */
		if(null != nif && nif.isLocal()){
			try {
				receiveHandle = nif.openLive(Integer.getInteger("", 65536), PromiscuousMode.PROMISCUOUS, 10);
				receiveHandle.setFilter("tcp", BpfCompileMode.OPTIMIZE);
			} catch (PcapNativeException e) {
				e.printStackTrace();
			} catch (NotOpenException e) {
				e.printStackTrace();
			}
			while(true){
				Packet packet;
				try {
					packet = receiveHandle.getNextPacket();
					if(packet == null) {
						loger.info("Continue waiting...");
						continue;
					}
					if(packet.contains(TcpPacket.class)) {
						loger.info("receive tcp:\n" + packet.toString());
						break;
					}
				} catch (NotOpenException e) {
					e.printStackTrace();
				}
			}
		} else {
			return;
		}
	}
	

}
