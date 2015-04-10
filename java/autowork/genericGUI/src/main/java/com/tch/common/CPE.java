package com.tch.common;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.WebDriver;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

/**
 * @author zengjp
 * 
 */
public class CPE implements SSH, WEB {
	
	public static final String NG_DEF_SW_FG = "OpenWrt";
	public static final String DEF_SW_FG = "Legacy";
	public static final String DEF_USER = "root";
	public static final String DEF_PASSWD = "root";
	public static final String DEF_HOST_IP = "192.168.1.1";
	
	private List<Account> accounts= new ArrayList<Account>();
	private String host = DEF_HOST_IP; // ipv4 address in string
	private String SW = NG_DEF_SW_FG;
	
	/*read-only attributes*/
	private String variantID;    /*which identify the available modules/services set*/
	
	
	public CPE(String varID){
		this.accounts.add(new Account());
		this.host = DEF_HOST_IP;
		this.variantID = varID;
	}
	
	public CPE(String varID,String host){
		this.accounts.add(new Account());
		this.host = host;
		this.variantID = varID;
	}
	
	public CPE(String varID, String userName, String passwd){
		this.accounts.add(new Account(userName, passwd));
		this.variantID = varID;
	}
	
	public CPE(String varID, List<Account> accounts, String hostIP){
		this.accounts.addAll(accounts);
		this.host = hostIP;
		this.variantID = varID;
	}
	
	public String getVariantID(){
		return this.variantID;
	}
	
	public void setHost(String ipString){
		host = ipString;
	}
	public String getHost(){
		return this.host;
	}
	
	public String getSW() {
		return SW;
	}

	public void setSW(String sW) {
		SW = sW;
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
	
	public String toString(){        
		return "[CPE-"+this.variantID+"]@"+this.host;
	}

	public WebDriver openWEB() {
		// TODO Auto-generated method stub
		return null;
	}

	public Session openSSH(Account ssh_Account, String key_file) {
		Session ssh=null;
		JSch jsch = new JSch();
		try {
			ssh = jsch.getSession(ssh_Account.getUserName(),this.host);
		} catch (JSchException e) {
			e.printStackTrace();
		}
		return ssh;
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
	*/
}
