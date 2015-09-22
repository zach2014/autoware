package cn.zjp.mock.network;

import java.net.Inet4Address;
import org.pcap4j.core.PacketListener;
import org.pcap4j.packet.Packet;

abstract class IpV4Host implements ListenOn {
	protected EthernetHost ethLayer;
	private Inet4Address ipAddr;

	public IpV4Host(EthernetHost ethLayer, Inet4Address ipAddr){
		this.ethLayer = ethLayer;
		this.setIpAddr(ipAddr);
		ethLayer.setListener(new IpPacketListener());
	}

	public Inet4Address getIpAddr() {
		return ipAddr;
	}

	public void setIpAddr(Inet4Address ipAddr) {
		this.ipAddr = ipAddr;
	}
	
	@Override
	public void start() {
		// TODO Auto-generated method stub

	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setListener(PacketListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isCapturing() {
		// TODO Auto-generated method stub
		return false;
	}
	
	
	private class IpPacketListener implements PacketListener {

		@Override
		public void gotPacket(Packet packet) {
			// TODO Auto-generated method stub
			
		}
		
	}

}
