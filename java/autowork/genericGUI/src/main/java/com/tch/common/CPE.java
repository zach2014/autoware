package com.tch.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

/**
 * @author zengjp
 * 
 */
public class CPE implements SSH, WEB {

	/*
	 * public static final String NG_DEF_SW_FG = "OpenWrt"; public static final
	 * String DEF_SW_FG = "Legacy"; public static final String DEF_USER =
	 * "root"; public static final String DEF_PASSWD = "root"; public static
	 * final String DEF_HOST_IP = "192.168.1.1";
	 */

	static final String S_H_CHECKING = "StrictHostKeyChecking";
	static final String UNKNOWN = "unknown";
	static final String CPE_DEF_HOST_IP = "192.168.1.1"; // ipv4 address in
															// string private
	static final String NG_SW = "openwrt";
	private static final long WAIT_4_CH_CLS = 1000;

	private Properties prop = new Properties();
	private Session ssh_Conn = null;
	private WebDriver web_Conn = null;

	public CPE() throws IOException {
		prop.load(this.getClass().getClassLoader()
				.getResourceAsStream("cpe.properties"));
	}

	public String getVariantID() {
		return prop.getProperty("CPE.platform", UNKNOWN);
	}

	public String getHost() {
		return prop.getProperty("CPE.hostip", CPE_DEF_HOST_IP);
	}

	public String getSW() {
		return prop.getProperty("CPE.CPE.sw", NG_SW) + "-"
				+ prop.getProperty("CPE.CPE.sw.ver");
	}

	public String showVersion(String ch) {
		// use shell|web to show software version
		return null;
	}

	public String toString() {
		return "[CPE-" + this.getVariantID() + "]@" + this.getHost();
	}

	protected boolean openSSH() throws JSchException {
		if (this.ssh_Conn == null) {
			JSch.setConfig(S_H_CHECKING, "no");
			JSch jsch = new JSch();
			ssh_Conn = jsch.getSession(prop.getProperty("ssh.username"),
					this.getHost());
			// ssh_Conn.setPassword(prop.getProperty("ssh.password"));
			if (prop.getProperty("CPE.ssh.id").isEmpty()) {
				ssh_Conn.connect();
			} else {
				jsch.addIdentity(prop.getProperty("CPE.ssh.id"));
				ssh_Conn.connect();
			}
		}
		if (ssh_Conn.isConnected())
			return true;
		return false;
	}

	public boolean openWEB() {
		String browser= prop.getProperty("GUI.browser", "firefox");
		long pageLoadTimer = Long.parseLong(prop.getProperty("GUI.timer.pageload", "5"));
		long implWaitTime = Long.parseLong(prop.getProperty("GUI.timer.implicitlyWait", "3"));
		if("chrome".equalsIgnoreCase(browser)) web_Conn = new ChromeDriver();
		else if("ie".equalsIgnoreCase(browser)) web_Conn = new InternetExplorerDriver();
		else web_Conn = new FirefoxDriver();

		web_Conn.manage().timeouts().pageLoadTimeout(pageLoadTimer, TimeUnit.SECONDS);
		web_Conn.manage().timeouts().implicitlyWait(implWaitTime, TimeUnit.SECONDS);
		return (null != web_Conn);
	}
	
	public WebDriver getWebPage(){
		getGWHomePage("http://" + getHost());
		return web_Conn;
	}
	
	public WebDriver getGWHomePage(String url){
		if(web_Conn == null){
			openWEB();
		}
		web_Conn.get(url);
		return web_Conn;
	}
	
	public String remoteExec(String command_str) throws IOException,
			JSchException {
		StringBuffer re = new StringBuffer();
		// make sure ssh connection is connected firstly
		if (ssh_Conn == null)
			openSSH();
		if (!ssh_Conn.isConnected())
			ssh_Conn.connect();
		
		ChannelExec ssh_Ch = (ChannelExec) ssh_Conn.openChannel("exec");
		ssh_Ch.setCommand(command_str);
		ssh_Ch.setInputStream(null);
		ssh_Ch.setErrStream(System.err);
		InputStream is = ssh_Ch.getInputStream();
		Reader reader = new InputStreamReader(is);
		BufferedReader buffered = new BufferedReader(reader);
		ssh_Ch.connect();
		
		// wait for channel closed
		while (!ssh_Ch.isClosed())
			try {
				Thread.sleep(WAIT_4_CH_CLS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		Integer exit_S = ssh_Ch.getExitStatus();
		if (0 == exit_S) { // retrieve output of cmd
			String line = buffered.readLine();
			while (line != null){
				re.append(line);
				line = buffered.readLine();
			}
			is.close();
		} else {
			System.out.println("Command exit:" + exit_S);
		}
		ssh_Ch.disconnect();
		return re.toString();
	}

	public void closeSSH() {
		if(ssh_Conn != null && ssh_Conn.isConnected()) ssh_Conn.disconnect();
	}
	
	
	public void closeWEB(){
		if(web_Conn != null) web_Conn.quit();
	}
}
