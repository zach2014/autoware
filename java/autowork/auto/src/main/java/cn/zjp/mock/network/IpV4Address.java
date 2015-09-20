package cn.zjp.mock.network;

import java.net.Inet4Address;
import java.net.UnknownHostException;

class IpV4Address implements IpAddress {
	private Inet4Address ipAddr;
	private int netMask;

	public IpV4Address(String ipAddr, Integer netMask){
		try {
			this.ipAddr = (Inet4Address) Inet4Address.getByName(ipAddr);
		} catch (UnknownHostException e) {
			throw new IllegalStateException("Invalid IPv4Address/HostName: " + ipAddr);
		}
		this.netMask = netMask;
	}
	
	@Override
	public Inet4Address getIpAddr() {
		return ipAddr;
	}
	
	@Override
	public int getPrefixLen() {
		return netMask;
	}

	@Override
	public String toString(){
		String ipAddrStr = ipAddr.toString();
		StringBuilder ipAddress = new StringBuilder();
		ipAddress
		.append(ipAddrStr.substring(ipAddress.lastIndexOf("/") + 1))
		.append("/")
		.append(Integer.toString(netMask));
		return ipAddress.toString();
	}
}
