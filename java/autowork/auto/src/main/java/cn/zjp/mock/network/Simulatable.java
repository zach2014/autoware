package cn.zjp.mock.network;

import org.pcap4j.core.PacketListener;
import org.pcap4j.packet.Packet;

public interface Simulatable {
	
	public void start(); // start to capturing on the device 
	
	public void stop(); // stop to capturing on the device
	
	public void shutdown(); // shutdown the threads of capturing and release the handles on the device
	public void sendPacket(Packet packet); // send the packet out
	
	public void setListener(PacketListener listener); // add listener to receive packet and handle
	
	public boolean isCapturing(); // wheather pcap is capturing on the device 
	
}
