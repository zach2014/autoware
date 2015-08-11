package cn.zjp.mock.network;

import org.pcap4j.core.NotOpenException;
import org.pcap4j.core.PacketListener;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapNativeException;

class PcapDump extends PcapWorker {
	
	PcapDump(PcapHandle handle, PacketListener listener) {
		super(handle, listener);
	}
	
	@Override
	public void run() {
		try {
			handle.loop(10, listener);
		} catch (PcapNativeException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (NotOpenException e) {
			e.printStackTrace();
		} finally {
		}
		
	}
	
	

}
