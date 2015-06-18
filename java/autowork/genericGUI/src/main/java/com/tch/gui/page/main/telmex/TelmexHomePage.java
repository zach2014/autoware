/**
 * 
 */
package com.tch.gui.page.main.telmex;

import com.tch.common.CPE;
import com.tch.gui.page.main.HomePage;

/**
 * @author zengjp
 *
 */
public class TelmexHomePage extends HomePage {

	public TelmexHomePage(CPE gw) {
		super(gw);
	}
	
	public TelmexLoginPage goLogin(){
		logout(page);
		return new TelmexLoginPage(cpe);
	}

}
