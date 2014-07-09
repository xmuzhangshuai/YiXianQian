package com.yixianqian.jsonobject;
/**
 * 
* @Description: TODO(School.java类) 
* @author lks   
* @date 2014年7月9日 上午11:24:21 
* @version V1.0
 */
public class School {
	
	private int s_id;
	private String s_name;
	private String s_info;
	private int s_cometentid;
	private int s_cityid;
	public int getS_id() {
		return s_id;
	}
	public void setS_id(int s_id) {
		this.s_id = s_id;
	}
	public String getS_name() {
		return s_name;
	}
	public void setS_name(String s_name) {
		this.s_name = s_name;
	}
	public String getS_info() {
		return s_info;
	}
	public void setS_info(String s_info) {
		this.s_info = s_info;
	}
	public int getS_cometentid() {
		return s_cometentid;
	}
	public void setS_cometentid(int s_cometentid) {
		this.s_cometentid = s_cometentid;
	}
	public int getS_cityid() {
		return s_cityid;
	}
	public void setS_cityid(int s_cityid) {
		this.s_cityid = s_cityid;
	}
	public School() {
		super();
	}
	public School(int s_id, String s_name, String s_info, int s_cometentid,
			int s_cityid) {
		super();
		this.s_id = s_id;
		this.s_name = s_name;
		this.s_info = s_info;
		this.s_cometentid = s_cometentid;
		this.s_cityid = s_cityid;
	}
	
}
