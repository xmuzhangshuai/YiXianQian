package com.yixianqian.jsonobject;

/**
 * 
* @Description: TODO(Province.java类) 
* @author lks   
* @date 2014年7月9日 上午11:20:48 
* @version V1.0
 */
public class Province {
	
	private int p_id;
	private int p_provinceid;
	private String p_name;
	public int getP_id() {
		return p_id;
	}
	public void setP_id(int p_id) {
		this.p_id = p_id;
	}
	public int getP_provinceid() {
		return p_provinceid;
	}
	public void setP_provinceid(int p_provinceid) {
		this.p_provinceid = p_provinceid;
	}
	public String getP_name() {
		return p_name;
	}
	public void setP_name(String p_name) {
		this.p_name = p_name;
	}
	public Province() {
		super();
	}
	public Province(int p_provinceid, String p_name) {
		super();
		this.p_provinceid = p_provinceid;
		this.p_name = p_name;
	}
	
	
}
