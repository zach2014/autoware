package com.tch.common;

public class Account {
	public static final String STR_ROOT = "root";
	
	private String userName;
	private String password;
	
	public Account(){
		this.userName = STR_ROOT;
		this.password = STR_ROOT;
	}
	public Account(String userName, String passwd) {
		this.userName = userName;
		this.password =passwd;
	}
	public String getUserName() {
		return userName;
	}
	public void setUser(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String passwd) {
		this.password = passwd;
	}

}
