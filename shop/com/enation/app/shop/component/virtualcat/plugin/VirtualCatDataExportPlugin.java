package com.enation.app.shop.component.virtualcat.plugin;

import org.springframework.stereotype.Component;

import com.enation.app.base.core.plugin.data.IDataExportEvent;
import com.enation.app.base.core.service.dbsolution.DBSolutionFactory;
import com.enation.framework.plugin.AutoRegisterPlugin;

/**
 * 虚拟（自定义）分类导出插件
 * @author lzf
 * 2012-11-12下午2:38:28
 * ver 1.0
 */
@Component
public class VirtualCatDataExportPlugin extends AutoRegisterPlugin implements
		IDataExportEvent {

	@Override
	public String onDataExport() {
		String[] tables={"virtual_cat"}; 
		
		String insertdata = DBSolutionFactory.dbExport(tables, true, "es_");
		StringBuffer data= new StringBuffer();
		data.append("\t<action>\n");
		data.append("\t\t<command>truncate</command>\n");
		data.append("\t\t<table>es_virtual_cat</table>\n");
		data.append("\t</action>\n");
		data.append(insertdata);
		return data.toString();
	}

}
