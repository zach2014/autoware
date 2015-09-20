package cn.zjp.mock.network;

import java.net.InetAddress;

interface IpAddress {
	public InetAddress getIpAddr();
	public int getPrefixLen();
}