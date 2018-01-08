package cn.net.hzy.app.shop.component.fenxiao.plugin.member;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.enation.app.base.core.model.Member;
import com.enation.eop.processor.core.freemarker.FreeMarkerPaser;
import com.enation.framework.plugin.AutoRegisterPluginsBundle;
import com.enation.framework.plugin.IPlugin;

public class FenXiaoMemberPluginBundle extends AutoRegisterPluginsBundle {

	@Override
	public String getName() {
		
		return "分销插件桩";
	}
	
	/**
	 * 获取会员分销详细页的选项卡
	 * @return
	 */
	public Map<Integer,String> getTabList(Member member){
		List<IPlugin> plugins = this.getPlugins();
		
		Map<Integer,String> tabMap = new TreeMap<Integer, String>();
		if (plugins != null) {
			
		 
			for (IPlugin plugin : plugins) {
				if(plugin instanceof IFenXiaoMemberTabShowEvent ){
					
					
					IFenXiaoMemberTabShowEvent event  = (IFenXiaoMemberTabShowEvent)plugin;
					
					/**
					 * 如果插件返回不执行，则跳过此插件
					 */
					if(!event.canBeExecute(member)){
						continue;
					}
					
					
					String name = event.getTabName(member);
					tabMap.put( event.getOrder(), name);
					 
					 
				}
			}
			
			 
		}
		return tabMap;
	}
	
	

	/**
	 * 获取各个插件的html
	 * 
	 */
	public Map<Integer,String>   getDetailHtml(Member member) {
		 Map<Integer,String> htmlMap = new TreeMap<Integer,String>();
		FreeMarkerPaser freeMarkerPaser =FreeMarkerPaser.getInstance();
		freeMarkerPaser.putData("member",member);
		List<IPlugin> plugins = this.getPlugins();
		
		if (plugins != null) {
			for (IPlugin plugin : plugins) {
			
				
				if (plugin instanceof IFenXiaoMemberTabShowEvent) {
					IFenXiaoMemberTabShowEvent event = (IFenXiaoMemberTabShowEvent) plugin;
					freeMarkerPaser.setClz(event.getClass());
						
						/**
						 * 如果插件返回不执行，则跳过此插件
						 */
						if(!event.canBeExecute(member)){
							continue;
						}
						String html =event.onShowMemberDetailHtml(member);
						htmlMap.put(event.getOrder(), html);
					 
				}
			}
		}
		
		
		return htmlMap;
	}

}
