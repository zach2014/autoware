package cn.zjp.mock.network;

import org.pcap4j.core.PacketListener;

/**
 * @author zengjp
 * {@link ListenOn} is an interface for being capturable.
 */
public interface ListenOn {
	
	public void start(); // start to capturing on the device 
	
	public void stop(); // stop to capturing on the device
	
	public void setListener(PacketListener listener); // add listener to receive packet and handle
	
	public boolean isCapturing(); // wheather pcap is capturing on the device 
	
}
