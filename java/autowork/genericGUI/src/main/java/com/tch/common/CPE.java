package com.tch.common;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public abstract class CPE {
	
	public static final String NG_DEF_SW_FG = "OpenWrt";
	public static final String DEF_SW_FG = "Legacy";
	public static final String DEF_USER = "root";
	public static final String DEF_PASSWD = "root";
	public static final String DEF_HOST_IP = "192.168.1.1";
	
	private List<Account> accounts= new ArrayList<Account>();
	private String host = DEF_HOST_IP; // ipv4 address in string
		
	public CPE(){
		this.accounts.add(new Account());
		this.host = DEF_HOST_IP;
	}
	
	public CPE(String host){
		this.accounts.add(new Account());
		this.host = host;
	}
	
	public CPE(String userName, String passwd){
		this.accounts.add(new Account(userName, passwd));
	}
	
	public CPE(List<Account> accounts, String hostIP){
		this.accounts.addAll(accounts);
		this.host = hostIP;
	}
	
	public void setHost(String ipString){
		host = ipString;
	}
	public String getHost(){
		return this.host;
	}
	
	public InetAddress getV4Address() throws UnknownHostException{
		return InetAddress.getByName(host);
	}
		
	public boolean addAccount(Account account){
		return this.accounts.add(account);
	}
	
	public boolean delAccount(String userName){
		for(Account a : this.accounts){
			if(a.getUserName().equals(userName)){
				return this.accounts.remove(a);
			}
		}
		return false;
		
	}
	
	/*
	public void setServices(Set<String> services){
		this.services = services;
	}
	
	public boolean isEnabled(String service){
		return this.services.contains(service);
	}
	
	public void enableService(String service){
		this.services.add(service);
	}
	
	public void disableService(String service){
		services.remove(service);
	}
	
	public String toString(){        
		return "["+this.variantID+"]@"+this.host;
	}
	*/
}
