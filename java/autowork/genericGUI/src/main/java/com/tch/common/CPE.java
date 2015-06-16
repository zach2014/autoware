package com.tch.common;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

/**
 * @author zengjp
 * 
 */
/**
 * @author zach15
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
	// private static final String CPE_DEF_PROP_FILE = "cpe.properties";
	private static final String DEF_CLI_PASSWD = "cat /proc/rip/0124 | head -c 8";

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

	/**
	 * To load required properties from default resource file
	 * 
	 * @throws IOException
	 */
	/*
	 * public static void build() throws IOException { InputStream propInput =
	 * CPE.class.getClassLoader().getResourceAsStream( CPE_DEF_PROP_FILE); try {
	 * instance.prop.load(propInput); instance.setup(); } catch (IOException io)
	 * { loger.error("Faile to load CPE Properties from " + CPE_DEF_PROP_FILE);
	 * throw io; } }
	 */

	/**
	 * To load required properties from given property file
	 * 
	 * @param propFilePath
	 * @throws IOException
	 */
	public static void build(String propFilePath) throws IOException {
		try {
			FileInputStream propInput = new FileInputStream(propFilePath);
			instance.prop.clear();
			instance.prop.load(propInput);
			instance.setup();
		} catch (FileNotFoundException io) {
			loger.error("Faile to load CPE Properties from " + propFilePath);
		}
	}

	/**
	 * To close all opened remote connections and clean associated properties,
	 */
	public static void reset() {
		loger.debug("Destroy the instance of CPE");
		if (instance.ssh_Conn != null && instance.ssh_Conn.isConnected()) {
			instance.closeSSH();
			instance.ssh_Conn = null;
		}
		instance.closeWEB();
		instance.web_Conn = null;
		instance.prop.clear();
	}

	private CPE() {
	}

	public String getHPageTitle() {
		return page_hm_title;
	}

	public String getLPageTitle() {
		return page_login_title;
	}

	/**
	 * To compose URL of login
	 * 
	 * @return loginURL
	 */
	public String getGivenLoginUrl() {
		if (page_login_url.isEmpty())
			return EMPTY_STR;
		else
			return "http://" + this.getHost() + "/" + page_login_url;
	}

	/**
	 * To get amount of cards in home page according the current user role
	 * 
	 * @param role
	 * @return modalNum
	 */
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

	/**
	 * To open web driver to access CPE, firefox is used by default, if no
	 * specify in properties. Chrome and IE are supported as well
	 */
	public boolean openWEB() {
		String browser = prop.getProperty("GUI.browser", "firefox");
		long pageLoadTimer = Long.parseLong(prop.getProperty(
				"GUI.timer.pageload", "5"));
		long implWaitTime = Long.parseLong(prop.getProperty(
				"GUI.timer.implicitlyWait", "3"));
		if ("chrome".equalsIgnoreCase(browser)) {
			System.setProperty("webdriver.chrome.driver",
					this.readProp("webdriver.chrome.driver.path"));
			web_Conn = new ChromeDriver();
		} else if ("ie".equalsIgnoreCase(browser))
			web_Conn = new InternetExplorerDriver();
		else {
			FirefoxProfile profile = new FirefoxProfile();
			// set proxy preference, 3=no_proxy, 4=auto-detect
			profile.setPreference("network.proxy.type", 3);
			web_Conn = new FirefoxDriver(profile);
		}

		if (null != web_Conn) {
			web_Conn.manage().timeouts()
					.pageLoadTimeout(pageLoadTimer, TimeUnit.SECONDS);
			web_Conn.manage().timeouts()
					.implicitlyWait(implWaitTime, TimeUnit.SECONDS);

			web_Conn.manage().window().maximize();
			loger.debug("Web browser is " + browser + ", detailed info: \n"
					+ web_Conn.toString());
			return true;
		} else {
			loger.fatal("Fail to launch web browser: " + browser);
			return false;
		}
	}

	/**
	 * To get the web driver for accessing web of CPE
	 * 
	 * @return webdriver
	 */
	public WebDriver getWebPage() {
		getWebPage(getHPageURL());
		return web_Conn;
	}

	/**
	 * To get the web driver with the given url
	 * 
	 * @param url
	 * @return webdriver
	 */
	public WebDriver getWebPage(String url) {
		if (web_Conn == null) {
			openWEB();
		}
		web_Conn.get(url);
		return web_Conn;
	}

	/**
	 * To execute given cli command string via ssh connection and return the
	 * response in stdout
	 * 
	 * @param cli_command
	 * @return stdout
	 */
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
				loger.error("Interrupt the wait for openning SSH channel");
				loger.debug(e.getMessage());
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
			System.out.println("\'" + command_str + "\' exit:" + exit_S);
		}
		ssh_Ch.disconnect();
		return re.toString();
	}

	/**
	 * To close the remote SSH session to CPE
	 */
	public void closeSSH() {
		if (ssh_Conn != null && ssh_Conn.isConnected())
			ssh_Conn.disconnect();
		loger.debug("SSH session to CPE is disconnected.");
	}

	/**
	 * To close the web connection of CPE, quit the web driver
	 */
	public void closeWEB() {
		if (web_Conn != null) {
			web_Conn.close();
			// web_Conn.quit();
			loger.debug("WEB seesion to CPE is quit");
		}
	}

	public String getWebUser() {
		String givenUser = readProp("web.username");
		if (null == givenUser)
			return "admin";
		return givenUser;
	}

	public String getWebPasswd() throws IOException, JSchException {
		String givenPass = readProp("web.password");
		if (null == givenPass) {
			String pass_cli = readProp("CPE.cli.defpasswd");
			if (null == pass_cli)
				pass_cli = DEF_CLI_PASSWD;
			givenPass = remoteExec(pass_cli);
		}
		return givenPass;
	}

	/**
	 * To compose the url of home page
	 * 
	 * @return url
	 */
	public String getHPageURL() {
		String givenUrl = this.readProp("GUI.home.url");
		if (null == givenUrl) {
			givenUrl = "http://" + getHost();
		}
		return givenUrl;
	}

	protected boolean openSSH() throws JSchException {
		if (this.ssh_Conn == null) {
			JSch.setConfig(S_H_CHECKING, "no");
			JSch jsch = new JSch();
			String ssh_userName = readProp("ssh.username");
			String ssh_passwd = readProp("ssh.password");
			String ssh_key_file = readProp("CPE.ssh.id");
			ssh_Conn = jsch.getSession(ssh_userName, this.getHost());
			ssh_Conn.setPassword(ssh_passwd);
			if (null == ssh_key_file) {
				loger.debug("To open SSH session with username/password: "
						+ ssh_userName + "/" + ssh_passwd);
				ssh_Conn.connect();
			} else {
				jsch.addIdentity(ssh_key_file);
				loger.debug("To open SSH with identity key: " + ssh_key_file);
				ssh_Conn.connect();
			}
		}
		if (ssh_Conn.isConnected())
			return true;
		return false;
	}

	private void setup() {
		if (!prop.isEmpty()) {
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
		loger.debug("GUI.home.title=" + page_hm_title);
		loger.debug("GUI.login.title=" + page_login_title);
		loger.debug("GUI.login.url=" + page_login_url);
	}
}
