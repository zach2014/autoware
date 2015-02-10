package com.tch.common;

import java.util.HashSet;
import java.util.Set;

public class CPE {
	
	private String variant;   //readonly
	private String hostAddr;
	private Set<String> services; //supported services, like web, ssh, etc.
	
	public CPE(){
		this.variant=null;
		this.hostAddr=null;
		this.services=new HashSet<String>();
	}
	
	public CPE(String variant, String host_ip){
		this.variant=variant;
		this.hostAddr=host_ip;
		this.services=null;
	}
	
	public CPE(String variant, String host_ip, Set<String> services){
		this.variant=variant;
		this.hostAddr=host_ip;
		this.services=services;
	}
	
	public String getVariant(){
		return variant;
	}
	
	public void setHostAddr(String ip){
		this.hostAddr=ip;
	}
	
	public String getHostAddr(){
		return this.hostAddr;
	}
	
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
        String str_svrs=" ";
        for( String s : services) {
            str_svrs += s;
            str_svrs += "/";
        }
		return "["+variant+"]@"+hostAddr + " with services of" + str_svrs;
	}
	
}
