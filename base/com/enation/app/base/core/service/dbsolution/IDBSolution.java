package com.enation.app.base.core.service.dbsolution;

import java.sql.Connection;
/**
 * 
 * @author liuzy
 * 
 * 数据库导入导出解决方案接口
 * 
 */
public interface IDBSolution {
	
	public void setConnection(Connection conn);
	
	public boolean dbImport(String xml);
	public boolean dbExport(String[] tables,String xml);
	public String dbExport(String[] tables,boolean dataOnly);
	public String dbSaasExport(String[] tables, boolean dataOnly, int userid, int siteid);
	public int dropTable(String table);
	public boolean dbSaasImport(String xml, int userid, int siteid);
	public void setPrefix(String prefix);
	public String toLocalType(String type, String size) ;
}
