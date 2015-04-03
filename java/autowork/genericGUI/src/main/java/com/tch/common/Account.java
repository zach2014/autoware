package com.tch.common;

public class Account {
	private String userName;
	private String password;
	
	public Account(){
		this.userName = "root";
		this.password = "root";
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
