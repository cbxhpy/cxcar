package com.enation.app.base.component.widget;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.eop.sdk.widget.AbstractWidget;
import com.enation.framework.database.IDBRouter;

/**
 * sql挂件
 * @author kingapex
 *2012-3-29上午10:09:42
 */
@Component("sql_list")
@Scope("prototype")
public class SqlListWidget extends AbstractWidget {
	private IDBRouter baseDBRouter;
	@Override
	protected void config(Map<String, String> params) {
		
	}

	@Override
	protected void display(Map<String, String> params) {
		String sql  =params.get("sql");
		sql = filterSql(sql);
		List list = this.daoSupport.queryForList(sql);
		this.putData("dataList", list);
	}

	private String filterSql(String sql ){
		 
		String pattern = "#(.*?)#";
 
		Pattern p = Pattern.compile(pattern, 2 | Pattern.DOTALL);
		Matcher m = p.matcher(sql);
		while(m.find()) {
				String tb  = m.group(0);
				 
				String newTb  = tb.replaceAll("#", "");
				newTb = this.baseDBRouter.getTableName(newTb);
				sql = sql.replaceAll(tb, newTb);
		}		
		return sql;
	}
	
	public static void main(String[] args){
		String sql  ="select * from  #goods# g ,#goods_cat# where cat_id=1";
		String pattern = "#(.*?)#";
 
		Pattern p = Pattern.compile(pattern, 2 | Pattern.DOTALL);
		Matcher m = p.matcher(sql);
		while(m.find()) {
				String tb  = m.group(0);
				String newTb  = tb.replaceAll("#", "");
				sql = sql.replaceAll(tb, newTb);
		}		
	}

	@Override
	public IDBRouter getBaseDBRouter() {
		return baseDBRouter;
	}

	@Override
	public void setBaseDBRouter(IDBRouter baseDBRouter) {
		this.baseDBRouter = baseDBRouter;
	}
	
}
