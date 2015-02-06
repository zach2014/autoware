package com.tch.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.openqa.selenium.WebDriver;

public class OpenWRTDUT {

	private Properties propties = null;
    private Properties conf=null;
	
	public OpenWRTDUT(String properties){
		propties=new Properties();
		InputStream propFile = getClass().getResourceAsStream(properties);
		try {
			propties.load(propFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
			
	public OpenWRTDUT(String properties, String configures){
		propties=new Properties();
		conf = new Properties();
		InputStream propFile = getClass().getResourceAsStream(properties);
		InputStream confFile = getClass().getResourceAsStream(configures);
		try {
			propties.load(propFile);
			conf.load(confFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public WebDriver web(){
		return null;
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
