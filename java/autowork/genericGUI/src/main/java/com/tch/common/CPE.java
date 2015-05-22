package com.tch.common;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

	public static final String EMPTY_STR = "";
	public static final String DEF_HOST_IP = "192.168.1.1";
															
	public static final String ADMIN_ROLE_NM = "admin";
	public static final String GUEST_ROLE_NM = "guest";
	public static final String NG_SW = "openwrt";
	public static final CPE instance = new CPE();

	static final Logger loger = LogManager.getLogger(CPE.class.getName());

	private static final String S_H_CHECKING = "StrictHostKeyChecking";
	private static final long WAIT_4_CH_CLS = 1000;
	private static final String CPE_DEF_PROP_FILE = "cpe.properties";

	private String variant;
	private String host;
	private String page_hm_title;
	private String page_login_title;
	private Integer page_hm_guest_crds;
	private Integer page_hm_admin_crds;
	private String page_login_url;
	private Session ssh_Conn = null;
	private WebDriver web_Conn = null;
	private Properties prop = new Properties();

	
	public static void build() throws IOException{
		InputStream propInput = CPE.class.getClassLoader().getResourceAsStream(CPE_DEF_PROP_FILE);
		instance.prop.load(propInput);
		instance.setup();
	}
	
	public static void build(String propFilePath) throws IOException{
			FileInputStream propInput = new FileInputStream(propFilePath);
			instance.prop.load(propInput);
			instance.setup();
	}
	
	public static void reset(){
		instance.ssh_Conn = null;
		instance.web_Conn = null;
		instance.prop = new Properties();
	}
	
	private CPE() {}
	
	public String getHPageTitle() {
		return page_hm_title;
	}

	public String getLPageTitle() {
		return page_login_title;
	}

	public String getGivenLoginUrl() {
		if (page_login_url.isEmpty())
			return EMPTY_STR;
		else
			return "http://" + this.getHost() + "/" + page_login_url;
	}

	public Integer getHpageCards(String role) {
		if (role.equals(CPE.ADMIN_ROLE_NM))
			return page_hm_admin_crds;
		else
			return page_hm_guest_crds;
	}

	public String getVariant() {
		return variant;
	}

	public String getHost() {
		return host;
	}

	public String readProp(String key) {
		return prop.getProperty(key);
	}

	public String toString() {
		return "[CPE-" + this.getVariant() + "]@" + this.getHost();
	}

	public boolean openWEB() {
		String browser = prop.getProperty("GUI.browser", "firefox");
		long pageLoadTimer = Long.parseLong(prop.getProperty(
				"GUI.timer.pageload", "5"));
		long implWaitTime = Long.parseLong(prop.getProperty(
				"GUI.timer.implicitlyWait", "3"));
		if ("chrome".equalsIgnoreCase(browser))
			web_Conn = new ChromeDriver();
		else if ("ie".equalsIgnoreCase(browser))
			web_Conn = new InternetExplorerDriver();
		else
			web_Conn = new FirefoxDriver();

		web_Conn.manage().timeouts()
				.pageLoadTimeout(pageLoadTimer, TimeUnit.SECONDS);
		web_Conn.manage().timeouts()
				.implicitlyWait(implWaitTime, TimeUnit.SECONDS);
		return (null != web_Conn);
	}

	public WebDriver getWebPage() {
		getWebPage("http://" + getHost());
		return web_Conn;
	}

	public WebDriver getWebPage(String url) {
		if (web_Conn == null) {
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
			while (line != null) {
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
		if (ssh_Conn != null && ssh_Conn.isConnected())
			ssh_Conn.disconnect();
	}

	public void closeWEB() {
		if (web_Conn != null)
			web_Conn.quit();
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
	
	private void setup() {
		if(! prop.isEmpty()) {
			loger.info("Being setup CPE's properties accordingly.");
			variant = prop.getProperty("CPE.platform", EMPTY_STR);
			host = prop.getProperty("CPE.hostip", DEF_HOST_IP);
			page_hm_title = prop.getProperty("GUI.home.title", EMPTY_STR);
			page_login_title = prop.getProperty("GUI.login.title", EMPTY_STR);
			page_hm_guest_crds = Integer.parseInt(prop.getProperty(
					"GUI.home.guest.cards", "0"));
			page_hm_admin_crds = Integer.parseInt(prop.getProperty(
					"GUI.home.admin.cards", "0"));
			page_login_url = prop.getProperty("GUI.login.url", EMPTY_STR);
			loger.info("CPE setup completed.");
		}
		loger.info(instance.toString());
		loger.trace("GUI.home.title="+page_hm_title);
		loger.trace("GUI.login.title="+page_login_title);
		loger.trace("GUI.login.url="+page_login_url);
	}


}
