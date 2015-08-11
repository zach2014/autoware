package cn.zjp.mock.network;

import org.pcap4j.core.PacketListener;
import org.pcap4j.core.PcapHandle;

public abstract class PcapWorker implements Runnable {
	
	protected PcapHandle handle;
	protected PacketListener listener;
	
	PcapWorker(PcapHandle handle, PacketListener listener) {
		this.handle = handle;
		this.listener = listener;
	}
}
