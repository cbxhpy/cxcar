package com.enation.eop.resource.impl;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.enation.app.base.core.service.auth.IAdminUserManager;
import com.enation.app.base.core.service.dbsolution.DBSolutionFactory;
import com.enation.app.base.core.service.solution.ISetupLoader;
import com.enation.eop.resource.IAppManager;
import com.enation.eop.resource.IDomainManager;
import com.enation.eop.resource.ISiteManager;
import com.enation.eop.resource.IThemeManager;
import com.enation.eop.resource.IUserManager;
import com.enation.eop.resource.model.Dns;
import com.enation.eop.resource.model.EopApp;
import com.enation.eop.resource.model.EopSite;
import com.enation.eop.resource.model.EopSiteDomain;
import com.enation.eop.resource.model.EopUser;
import com.enation.eop.resource.model.Theme;
import com.enation.eop.sdk.context.EopContext;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.context.webcontext.WebSessionContext;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.database.ISqlFileExecutor;
import com.enation.framework.database.Page;
import com.enation.framework.database.StringMapper;
import com.enation.framework.resource.ResourceStateManager;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.FileUtil;
import com.enation.framework.util.StringUtil;

/**
 * 站点管理
 * 
 * @author kingapex 2010-5-9下午07:56:03
 */
public class SiteManagerImpl implements ISiteManager {

	private IDaoSupport<EopSite> daoSupport;
	private IDomainManager domainManager;
	private IThemeManager themeManager;
	private ISqlFileExecutor sqlFileExecutor;
	private IAdminUserManager adminUserManager;
	private IAppManager appManager;
	private ISetupLoader setupLoader;

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public int addDomain(EopSiteDomain eopSiteDomain) {
		// UserUtil.validUser(eopSiteDomain.getUserid());
		if (checkInDomain(eopSiteDomain.getDomain())) {
			throw new IllegalArgumentException("域名[" + eopSiteDomain.getDomain() + "]已存在！");
		}
		daoSupport.insert("eop_sitedomain", eopSiteDomain);
		return daoSupport.getLastId("eop_sitedomain");
	}

	@Override
	public Boolean checkInDomain(String domain) {
		String sql = "select count(0) from eop_sitedomain where domain = ?";
		int count = this.daoSupport.queryForInt(sql, domain);
		return count == 0 ? false : true;
	}

	@Override
	public void deleteDomain(String domain) {
		String sql = "delete from eop_sitedomain where domain=?";
		this.daoSupport.execute(sql, domain);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public Integer add(EopUser user, EopSite site, String domain) {
		int userid = user.getId();
		site.setUserid(userid);

		if (site.getIcofile() == null)
			site.setIcofile(EopSetting.IMG_SERVER_DOMAIN + "/images/favicon.ico");
		if (site.getLogofile() == null)
			site.setLogofile(EopSetting.IMG_SERVER_DOMAIN + "/images/logo.gif");

		site.setPoint(1000);

		// 2010-09-12新增站点创建时间字段、最后登录时间、最后领取积分字段
		site.setCreatetime(Long.valueOf(DateUtil.getDateline()));
		site.setLastlogin(0L); // 未登录过
		site.setLastgetpoint(DateUtil.getDateline()); // 默认创建即获取了积分

		// 添加站点并得到id
		this.daoSupport.insert("eop_site", site);
		Integer siteid = this.daoSupport.getLastId("eop_site");

		EopSiteDomain eopSiteDomain = new EopSiteDomain();
		eopSiteDomain.setDomain(domain);
		eopSiteDomain.setSiteid(siteid);
		eopSiteDomain.setUserid(userid);

		// 为站点添加域名
		this.addDomain(eopSiteDomain);

		return siteid;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public Integer add(EopSite site, String domain) {

		WebSessionContext<EopUser> sessonContext = ThreadContextHolder.getSessionContext();
		EopUser user = sessonContext.getAttribute(IUserManager.USER_SESSION_KEY);
		return this.add(user, site, domain);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void deleteDomain(Integer domainid) {
		EopSiteDomain domain = domainManager.get(domainid);
		UserUtil.validUser(domain.getUserid());
		int domainCount = this.domainManager.getDomainCount(domain.getSiteid());
		if (domainCount <= 1) {
			throw new RuntimeException("此站点只有一个域名不可删除，删除后将不可访问");
		}
		String sql = "delete from eop_sitedomain where id=?";
		this.daoSupport.execute(sql, domainid);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(Integer id) {
		EopSite site = this.get(id);
		// UserUtil.validUser(site.getUserid());
		Integer userid = site.getUserid();
		Integer siteid = site.getId();

		// 删除表
		String sql = "show tables like '%_" + userid + "_" + siteid + "'";
		List tableList = this.daoSupport.queryForList(sql, new StringMapper());
		for (String tbName : (List<String>) tableList) {
			sql = "drop table if exists " + tbName;
			this.daoSupport.execute(sql);
		}

		// 删除数据信息
		sql = "delete from eop_sitedomain where siteid = ?";
		this.daoSupport.execute(sql, id);
		sql = "delete from  eop_site  where id = ?";
		this.daoSupport.execute(sql, id);

		// 删除文件
		String userTplFile = EopSetting.EOP_PATH + "/user/" + userid + "/" + siteid;
		String userStyleFile = EopSetting.IMG_SERVER_PATH + "/user/" + userid + "/" + siteid;
		FileUtil.delete(userTplFile);
		FileUtil.delete(userStyleFile);
	}

	/**
	 * 获取DNS列表 即domain -- site的对照关系列表
	 */
	@Override
	public List getDnsList() {
		String sql = "select a.domain as domain,a.siteid as siteid, b.* from eop_sitedomain a left join eop_site b on b.id = a.siteid";
		return this.daoSupport.queryForList(sql, new DnsMapper());
	}

	/**
	 * Liuzy add(2012-10-29) 解决重复代码和SaaS下出错问题
	 * 
	 * @return
	 */
	private static class ValueConvert {
		public static String replacePrefix(String in, EopSite site) {
			String replaceString = null;
			if ("2".equals(EopSetting.RUNMODE)) {
				replaceString = EopSetting.IMG_SERVER_DOMAIN + "/user/"	+ site.getUserid() + "/" + site.getId();
			} else {
				replaceString = EopSetting.IMG_SERVER_DOMAIN;
			}

			if (in != null)
				return in.replaceAll(EopSetting.FILE_STORE_PREFIX, replaceString);
			return in;
		}
	}

	private static class DnsMapper implements ParameterizedRowMapper<Dns> {
		@Override
		public Dns mapRow(ResultSet rs, int rowNum) throws SQLException {
			Dns dns = new Dns();

			dns.setDomain(rs.getString("domain"));
			EopSite site = new EopSite();
			site.setId(rs.getInt("siteid"));
			site.setAdminthemeid(rs.getInt("adminthemeid"));
			site.setThemeid(rs.getInt("themeid"));
			site.setSitename(rs.getString("sitename"));
			site.setUserid(rs.getInt("userid"));
			site.setKeywords(rs.getString("keywords"));
			site.setDescript(rs.getString("descript"));
			site.setThemepath(rs.getString("themepath"));
			site.setPoint(rs.getInt("point"));
			// 20100912新增下面字段 --kingapex
			site.setCreatetime(rs.getLong("createtime"));
			site.setLastgetpoint(rs.getLong("lastgetpoint"));
			site.setLastlogin(rs.getLong("lastlogin"));
			site.setLogincount(rs.getInt("logincount"));
			// lzf add 20101026
			site.setCopyright(rs.getString("copyright"));
			site.setTitle(rs.getString("title"));
			site.setUsername(rs.getString("username"));
			site.setUsertel(rs.getString("usertel"));
			site.setUsermobile(rs.getString("usermobile"));
			site.setUsertel1(rs.getString("usertel1"));
			site.setUseremail(rs.getString("useremail"));
			site.setIcp(rs.getString("icp"));
			site.setAddress(rs.getString("address"));
			site.setZipcode(rs.getString("zipcode"));
			site.setSiteon(rs.getInt("siteon"));
			site.setState(rs.getInt("state"));
			site.setQq(rs.getInt("qq"));
			site.setMsn(rs.getInt("msn"));
			site.setWw(rs.getInt("ww"));
			site.setTel(rs.getInt("tel"));
			site.setWt(rs.getInt("wt"));
			site.setQqlist(rs.getString("qqlist"));
			site.setMsnlist(rs.getString("msnlist"));
			site.setWwlist(rs.getString("wwlist"));
			site.setTellist(rs.getString("tellist"));
			site.setWorktime(rs.getString("worktime"));
			site.setLinkman(rs.getString("linkman"));
			site.setLinktel(rs.getString("linktel"));
			site.setEmail(rs.getString("email"));
			site.setClosereson(rs.getString("closereson"));
			// end of lzf add 20101026

			site.setRelid(rs.getString("relid"));
			site.setImptype(rs.getInt("imptype"));
			site.setSitestate(rs.getInt("sitestate"));
			site.setIsimported(rs.getInt("isimported"));

			site.setIcofile(ValueConvert.replacePrefix(rs.getString("icofile"),	site));
			site.setLogofile(ValueConvert.replacePrefix(rs.getString("logofile"), site));
			site.setBklogofile(ValueConvert.replacePrefix(rs.getString("bklogofile"), site));
			site.setBkloginpicfile(ValueConvert.replacePrefix(rs.getString("bkloginpicfile"), site));

			site.setMulti_site(rs.getInt("multi_site"));
			site.setProductid(rs.getString("productid"));
			site.setMobilesite(rs.getInt("mobilesite"));

			dns.setSite(site);

			return dns;
		}
	}

	@Override
	public EopSite get(Integer id) {
		String sql = "select * from eop_site where  id = ?";
		EopSite site = this.daoSupport.queryForObject(sql, EopSite.class, id);
		String icofile = site.getIcofile();
		if (icofile != null)
			icofile = UploadUtil.replacePath(icofile);
		site.setIcofile(icofile);

		site.setLogofile(ValueConvert.replacePrefix(site.getLogofile(), site));
		site.setBklogofile(ValueConvert.replacePrefix(site.getBklogofile(),	site));
		site.setBkloginpicfile(ValueConvert.replacePrefix(site.getBkloginpicfile(), site));

		return site;
	}

	@Override
	public EopSite get(String domain) {
		String sql = "select a.* from eop_site a join eop_sitedomain b on b.siteid = a.id and b.domain= ?";
		List<EopSite> sitelist = this.daoSupport.queryForList(sql, EopSite.class, domain);
		if (sitelist == null || sitelist.isEmpty())
			return null;
		EopSite site = sitelist.get(0);

		site.setIcofile(ValueConvert.replacePrefix(site.getIcofile(), site));
		site.setLogofile(ValueConvert.replacePrefix(site.getLogofile(), site));
		site.setBklogofile(ValueConvert.replacePrefix(site.getBklogofile(),	site));
		site.setBkloginpicfile(ValueConvert.replacePrefix(site.getBkloginpicfile(), site));

		return site;
	}

	/**
	 * 分页读取所有站点<br>
	 * 可以按关键字搜索
	 * 
	 * @param pageNo
	 *            页码
	 * @param pageSize
	 *            页大小
	 * @param keyword
	 *            关键字:模糊匹配域名站点名，用户名
	 * @return
	 */
	@Override
	public Page list(String keyword, int sitestate, int pageNo, int pageSize) {
		String sql = "select s.id,s.sitename,s.point,s.createtime,s.sitestate,s.logincount,"
				+ "s.lastlogin,u.username from eop_site s,"
				+ "eop_user u where s.userid=u.id";

		String sitesql = sitestate > 0 ? " and s.sitestate=" + (sitestate - 1) : "";

		if (!StringUtil.isEmpty(keyword)) {
			sql += " and (s.sitename like '%" + keyword + "%' ";
			sql += " or u.username like '%" + keyword + "%'";
			sql += " or s.id in(select siteid from eop_sitedomain where domain like '%"	+ keyword + "%'))";
		}
		sql += sitesql + " order by s.createtime desc";

		return this.daoSupport.queryForPage(sql, pageNo, pageSize);
	}

	@Override
	public Page list(int pageNo, int pageSize, String order, String search) {
		Integer userid = EopContext.getContext().getCurrentSite().getUserid();
		List<EopSiteDomain> listdomain = this.domainManager.listUserDomain();
		if (search == null)
			search = "";
		else
			search = " and sitename like '%" + search + "%'";
		if (order == null)
			order = "";
		else
			order = " order by " + order.replace(":", " ");

		Page page = daoSupport.queryForPage("select * from eop_site where deleteflag = 0 and userid = "	+ userid + search + order, pageNo, pageSize);

		List<Map> listsite = (List<Map>) (page.getResult());

		for (Map site : listsite) {
			List<EopSiteDomain> domainList = new ArrayList<EopSiteDomain>();
			String logofile = site.get("logofile").toString();
			if (logofile != null)
				logofile = logofile.replaceAll(EopSetting.FILE_STORE_PREFIX, EopSetting.IMG_SERVER_DOMAIN + "/user/" + site.get("userid").toString() + "/" + site.get("id").toString());
			site.put("logofile", logofile);
			for (EopSiteDomain siteDomain : listdomain) {
				if (site.get("id").toString().equals(siteDomain.getSiteid().toString())) {
					domainList.add(siteDomain);
				}
			}
			site.put("eopSiteDomainList", domainList);
		}

		return page;
	}

	@Override
	public void edit(EopSite eopSite) {
		eopSite.setPoint(this.get(eopSite.getId()).getPoint());
		this.daoSupport.update("eop_site", eopSite, "id = " + eopSite.getId());
	}

	@Override
	public void setSiteProduct(Integer userid, Integer siteid, String productid) {
		String sql = "update eop_site set productid=? where userid=? and id=?";
		this.daoSupport.execute(sql, productid, userid, siteid);
	}

	@Override
	public void changeAdminTheme(Integer themeid) {
		EopSite site = EopContext.getContext().getCurrentSite();
		String sql = "update eop_site set adminthemeid=? where userid=? and id=?";

		this.daoSupport.execute(sql, themeid, site.getUserid(), site.getId());
		EopContext.getContext().getCurrentSite().setAdminthemeid(themeid);
	}

	@Override
	public void changeTheme(Integer themeid) {
		EopSite site = EopContext.getContext().getCurrentSite();
		Theme theme = themeManager.getTheme(themeid);
		String sql = "update eop_site set themeid=?,themepath=? where userid=? and id=?";
		this.daoSupport.execute(sql, themeid, theme.getPath(), site.getUserid(), site.getId());
		site.setThemeid(themeid);
		site.setThemepath(theme.getPath());
		ResourceStateManager.setDisplayState(1);
	}

	@Override
	public List listDomain(Integer userid, Integer siteid) {
		String sql = "select * from eop_sitedomain where userid=? and siteid=?";
		return this.daoSupport.queryForList(sql, EopSiteDomain.class, userid, siteid);
	}

	@Override
	public void updatePoint(Integer id, int point, int sitestate) {
		this.daoSupport.execute("update eop_site set point=?,sitestate=? where id=?", point, sitestate, id);
	}

	/**
	 * 领取积分
	 */
	@Override
	public int getPoint(Integer id, int point) {
		EopSite site = EopContext.getContext().getCurrentSite();
		long lastgetpoint = site.getLastgetpoint();// 上次领取积分的时间
		long now = (int) (System.currentTimeMillis() / 1000); // 当前时间
		int onemonth = 60 * 60 * 24 * 30;
		if (now - lastgetpoint > onemonth) {// 已经有一个月未领取积分
			this.daoSupport.execute("update eop_site set point=point+? where id=?", point, id); // 更新站点积分
			site.setPoint(site.getPoint() + point);
			site.setLastgetpoint(DateUtil.getDateline()); // 更新上下文中最后领取积分时间
			this.daoSupport.execute("update eop_site set lastgetpoint=? where id=?", DateUtil.getDateline(), id); // 更新数据库中最后领取积分时间

			return 1;
		} else {
			return 0;
		}
	}

	/**
	 * 初始化当前站点数据为初厂设置<br>
	 * 方式为执行当前站点的init.sql
	 */
	@Override
	public void initData() {
		EopSite site = EopContext.getContext().getCurrentSite();
		String productId = site.getProductid();
		String dataPath = EopSetting.PRODUCTS_STORAGE_PATH + "/" + productId + "/init.xml";
		if (!DBSolutionFactory.dbImport(dataPath, ""))
			throw new RuntimeException("初始化数据出错...");
	}

	public ISqlFileExecutor getSqlFileExecutor() {
		return sqlFileExecutor;
	}

	public void setSqlFileExecutor(ISqlFileExecutor sqlFileExecutor) {
		this.sqlFileExecutor = sqlFileExecutor;
	}

	/**
	 * 读取所有站点列表
	 */
	@Override
	public List<Map> list() {
		String sql = "select * from eop_site ";
		return this.daoSupport.queryForList(sql);
	}

	/**
	 * 读取某用户的所有站点列表
	 */
	@Override
	public List<Map> list(Integer userid) {
		String sql = "select * from eop_site where userid=?";
		List<Map> list = this.daoSupport.queryForList(sql, userid);
		for (Map site : list) {
			List<EopSiteDomain> domainList = this.domainManager.listSiteDomain(Integer.parseInt(site.get("id").toString()));
			site.put("eopSiteDomainList", domainList);
		}
		return list;
	}

	/**
	 * 得到本站点的应用列表
	 */
	@Override
	public List<EopApp> getSiteApps() {
		if (EopSetting.RUNMODE.equals("2")) {
			List<EopApp> appList = this.listSaasApp();
			return appList;
		} else {
			List<EopApp> appList = appManager.list();
			return appList;
		}
	}

	private List<EopApp> listSaasApp() {
		List<EopApp> appList = new ArrayList<EopApp>();
		// 尝试加载proflie.xml，如果不存在则返回所有app
		String xmlFile = EopSetting.EOP_PATH + EopContext.getContext().getContextPath() + "/profile.xml";
		if (new File(xmlFile).exists()) {
			try {
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				DocumentBuilder builder = factory.newDocumentBuilder();
				Document document = builder.parse(xmlFile);
				NodeList nodeList = document.getElementsByTagName("apps");
				if (nodeList != null) {
					// 通过xml的apps节点生成appList
					Node appsNode = nodeList.item(0);
					NodeList appNodeList = appsNode.getChildNodes();

					// xml节点和EopApp的转换
					for (int i = 0, len = appNodeList.getLength(); i < len; i++) {
						Node node = appNodeList.item(i);
						if (node.getNodeType() == Node.ELEMENT_NODE) {
							Element appNode = (Element) node; // app的xml节点
							EopApp eopApp = new EopApp();
							eopApp.setAppid(appNode.getAttribute("id")); // app的id属性
							eopApp.setVersion(appNode.getAttribute("version")); // app的版本属性
							appList.add(eopApp);
						}
					}
				}
				return appList;
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException("加载本站点根目录的profile.xml失败，不能导出。");
			}
		} else {
			throw new RuntimeException("本站点根目录下未含有profile.xml，不能导出。");
		}
	}

	public IAdminUserManager getAdminUserManager() {
		return adminUserManager;
	}

	public void setAdminUserManager(IAdminUserManager adminUserManager) {
		this.adminUserManager = adminUserManager;
	}

	@Override
	public void editDomain(String domainOld, String domainNew) {
		this.daoSupport.execute("update eop_sitedomain set domain = ? where domain = ?", domainNew, domainOld);
	}

	public IDaoSupport<EopSite> getDaoSupport() {
		return daoSupport;
	}

	public void setDaoSupport(IDaoSupport<EopSite> daoSupport) {
		this.daoSupport = daoSupport;
	}

	public IDomainManager getDomainManager() {
		return domainManager;
	}

	public void setDomainManager(IDomainManager domainManager) {
		this.domainManager = domainManager;
	}

	public IThemeManager getThemeManager() {
		return themeManager;
	}

	public void setThemeManager(IThemeManager themeManager) {
		this.themeManager = themeManager;
	}

	public IAppManager getAppManager() {
		return appManager;
	}

	public void setAppManager(IAppManager appManager) {
		this.appManager = appManager;
	}

	public ISetupLoader getSetupLoader() {
		return setupLoader;
	}

	public void setSetupLoader(ISetupLoader setupLoader) {
		this.setupLoader = setupLoader;
	}

}
