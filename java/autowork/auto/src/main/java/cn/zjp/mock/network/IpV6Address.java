package cn.zjp.mock.network;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class IpV6Address implements IpAddress {
	private Inet6Address ip6Addr;
	private int prefixLen;
	
	public IpV6Address(String ip6Addr, int prefixLen) {
		try {
			this.ip6Addr = (Inet6Address) Inet6Address.getByName(ip6Addr);
		} catch (UnknownHostException e) {
			throw new IllegalStateException("Invalid IPv6Address/HostName: " + ip6Addr);
		}
		this.prefixLen = prefixLen;
	}

	@Override
	public InetAddress getIpAddr() {
		return ip6Addr;
	}

	@Override
	public int getPrefixLen() {
		return prefixLen;
	}
	
	@Override
	public String toString(){
		String ip6AddrStr = ip6Addr.toString();
		StringBuilder ip6Address = new StringBuilder();
		ip6Address
		.append(ip6AddrStr.substring(ip6Address.lastIndexOf("/") + 1 ))
		.append("/")
		.append(Integer.toString(prefixLen));
		return ip6Address.toString();
	}

}
