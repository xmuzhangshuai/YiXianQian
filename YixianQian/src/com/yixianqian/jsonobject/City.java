package com.yixianqian.jsonobject;

/**
 * 
* @Description: TODO(City.java类) 
* @author lks   
* @date 2014年7月9日 上午11:22:12 
* @version V1.0
 */
public class City {
	
	private int c_id;
	private int c_cityid;
	private int c_name;
	private int p_id;
	public int getC_id() {
		return c_id;
	}
	public void setC_id(int c_id) {
		this.c_id = c_id;
	}
	public int getC_cityid() {
		return c_cityid;
	}
	public void setC_cityid(int c_cityid) {
		this.c_cityid = c_cityid;
	}
	public int getC_name() {
		return c_name;
	}
	public void setC_name(int c_name) {
		this.c_name = c_name;
	}
	public int getP_id() {
		return p_id;
	}
	public void setP_id(int p_id) {
		this.p_id = p_id;
	}
	public City() {
		super();
	}
	public City(int c_cityid, int c_name, int p_id) {
		super();
		this.c_cityid = c_cityid;
		this.c_name = c_name;
		this.p_id = p_id;
	}
	
}
