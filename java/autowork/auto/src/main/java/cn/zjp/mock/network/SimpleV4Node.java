/**
 * 
 */
package cn.zjp.mock.network;

/**
 * @author zach15
 *
 */
public class SimpleV4Node extends AbstractNode {
	
	public SimpleV4Node(String defRouter){
		super(defRouter);
	}
	
	public SimpleV4Node(String defRouter, String pseudoAddr){
		super(defRouter, pseudoAddr);
	}
	
	public SimpleV4Node(String defRouter, String pseudoAddr, String pseudoMac){
		super(defRouter, pseudoAddr, pseudoMac);
	}
	
}
