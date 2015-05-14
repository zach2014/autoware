package com.tch.common;


public interface SSH {

	String remoteExec(String command) throws Exception;
	void closeSSH();
}
