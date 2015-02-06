package com.tch.common;

import java.util.Collection;
import java.util.Set;

public class CPE {
	
	private String variant;
	private String hostAddr;
	private Set<String> services;
	
	public CPE(){
		this.variant=null;
		this.hostAddr=null;
		this.services=null;
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
	
	public void setVariant(String variant){
		this.variant=variant;
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
	
	public void setServices(Collection<String> services){
		this.services.addAll(services);
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
		return "["+variant+"]@"+hostAddr;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
