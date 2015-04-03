package com.tch.common;

import java.util.List;

import org.openqa.selenium.WebDriver;

public class OpenWRTCPE extends CPE implements SSHD, WEB {

	/*read-only attributes*/
	private String variantID;   /*which identify the available modules/services set*/
	
	public OpenWRTCPE(String varID) {
		super();
		this.variantID = varID;
	}
	
	public OpenWRTCPE(String varID, String userName, String passwd){
		super(userName, passwd);
		this.variantID = varID;
	}
	
	public OpenWRTCPE(String varID, String host){
		super(host);
		this.variantID = varID;
	}
	
	public OpenWRTCPE(String varID, List<Account> accounts, String host){
		super(accounts, host);
		this.variantID = varID;
	}

	public String getVariantID() {
		return variantID;
	}
	
	public WebDriver openWEB() {
		// TODO Auto-generated method stub
		return null;
	}

	public ShCMD openSSH() {
		// TODO Auto-generated method stub
		return null;
	}





}
