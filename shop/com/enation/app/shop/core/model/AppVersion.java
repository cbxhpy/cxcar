package com.enation.app.shop.core.model;

/**
 * 应用版本
 * @author yexf
 * 2016-11-5
 */
public class AppVersion implements java.io.Serializable {

    private Integer app_version_id; //主键
    private String name; //版本名称
    private Integer platform; //系统 1：android 2：ios
    private Integer update_type; //升级类型 0：不升级 1：建议升级 2：强制升级
    private String updata_url; //下载地址
    private String version_num; //版本号
    private Integer num; //版本序号
    private String content; //更新信息
    private long create_time; //创建时间
    
    private String file_size;//应用大小
    
	public Integer getApp_version_id() {
		return app_version_id;
	}
	public void setApp_version_id(Integer app_version_id) {
		this.app_version_id = app_version_id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getPlatform() {
		return platform;
	}
	public void setPlatform(Integer platform) {
		this.platform = platform;
	}
	public Integer getUpdate_type() {
		return update_type;
	}
	public void setUpdate_type(Integer update_type) {
		this.update_type = update_type;
	}
	public String getUpdata_url() {
		return updata_url;
	}
	public void setUpdata_url(String updata_url) {
		this.updata_url = updata_url;
	}
	public String getVersion_num() {
		return version_num;
	}
	public void setVersion_num(String version_num) {
		this.version_num = version_num;
	}
	public Integer getNum() {
		return num;
	}
	public void setNum(Integer num) {
		this.num = num;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public long getCreate_time() {
		return create_time;
	}
	public void setCreate_time(long create_time) {
		this.create_time = create_time;
	}
	public String getFile_size() {
		return file_size;
	}
	public void setFile_size(String file_size) {
		this.file_size = file_size;
	}
    
    
}