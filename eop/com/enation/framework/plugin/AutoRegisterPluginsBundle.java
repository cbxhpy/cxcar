package com.enation.framework.plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.enation.eop.resource.model.EopSite;
import com.enation.eop.sdk.context.EopContext;
import com.enation.eop.sdk.context.EopSetting;

/**
 * 自动注册插件桩
 * 
 * @author apexking
 * 
 */
public abstract class AutoRegisterPluginsBundle implements IPluginBundle {
	protected static final Log loger = LogFactory.getLog(AutoRegisterPluginsBundle.class);

	private List<IPlugin> plugins;

	private Map<String, List<IPlugin>> saasPlugins;

	@Override
	public synchronized void registerPlugin(IPlugin plugin) {
		if ("2".equals(EopSetting.RUNMODE)) {
			this.registerPlugin2(plugin);
		}

		if ("1".equals(EopSetting.RUNMODE)) {
			this.registerPlugin1(plugin);
		}
	}

	private void registerPlugin1(IPlugin plugin) {
		if (plugins == null) {
			plugins = new ArrayList<IPlugin>();
		}

		if (!plugins.contains(plugin)) {
			plugins.add(plugin);
		}

		if (loger.isDebugEnabled()) {
			loger.debug("为插件桩" + getName() + "注册插件：" + plugin.getClass());
		}
	}

	private void registerPlugin2(IPlugin plugin) {
		if (saasPlugins == null) {
			saasPlugins = new HashMap<String, List<IPlugin>>();
		}

		String key = this.getKey();

		List<IPlugin> pluginList = saasPlugins.get(key);

		if (pluginList == null) {
			pluginList = new ArrayList<IPlugin>();
			saasPlugins.put(key, pluginList);
		}
		if (!pluginList.contains(plugin)) {
			pluginList.add(plugin);
		}
	}

	@Override
	public synchronized void unRegisterPlugin(IPlugin _plugin) {
		if ("2".equals(EopSetting.RUNMODE)) {
			if (saasPlugins == null) {
				return;
			}

			String key = this.getKey();
			List<IPlugin> pluginList = saasPlugins.get(key);
			if (pluginList == null) {
				return;
			} else {
				pluginList.remove(_plugin);
			}
		} else {
			if (plugins != null) {
				plugins.remove(_plugin);
			}
		}
	}

	/**
	 * 获取此插件列表
	 * 
	 * @return
	 */
	public synchronized List<IPlugin> getPlugins() {
		if ("2".equals(EopSetting.RUNMODE)) {
			if (saasPlugins == null) {
				return new ArrayList<IPlugin>();
			}

			String key = this.getKey();
			List<IPlugin> pluginList = saasPlugins.get(key);
			if (pluginList == null) {
				return new ArrayList<IPlugin>();
			} else {
				return pluginList;
			}
		} else {
			return plugins;
		}
	}

	private String getKey() {
		EopSite site = EopContext.getContext().getCurrentSite();
		int userid = site.getUserid();
		int siteid = site.getId();
		String key = userid + "_" + siteid;

		return key;
	}

	abstract public String getName();

}
