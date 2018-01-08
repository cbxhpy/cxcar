package com.enation.app.shop.core.action.backend;

import java.util.List;

import net.sf.json.JSONArray;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Regions;
import com.enation.app.base.core.service.IRegionsManager;
import com.enation.framework.action.WWAction;

/**
 * 地区管理
 * 
 * @author lzf<br/>
 *         2010-4-22下午12:52:49<br/>
 *         version 1.0
 */
@Component
@Scope("prototype")
@ParentPackage("shop_default")
@Namespace("/shop/admin")
@Action("region")
@Results({
	@Result(name="list", type="freemarker", location="/shop/admin/regions/regions.html"),
	@Result(name="listChildren", type="freemarker", location="/shop/admin/regions/region_Panel.html"),
	@Result(name="add", type="freemarker", location="/shop/admin/regions/region_add.html"),
	@Result(name="edit", type="freemarker", location="/shop/admin/regions/region_edit.html"),
	@Result(name="children", type="freemarker", location="/shop/admin/regions/region_children.html") 
})
public class RegionAction extends WWAction {
	private IRegionsManager regionsManager;
	private List listRegion;
	private Integer parentid;
	private Regions regions;
	private Integer region_id;
	private Integer regiongrade;
	private Integer regionid;
	/**
	 * 跳转至地区管理列表页
	 * @return 地区管理列表页
	 */
	public String list(){
		return "list";
	}
	/**
	 * 获取地区管理列表Json
	 * @param listRegion 地区列表,List
	 * @return 获取地区管理列表Json
	 */
	public String listChildren(){
		listRegion = regionsManager.listChildren(0);
		this.json = JSONArray.fromObject(listRegion).toString();
		return JSON_MESSAGE;
	}
	/**
	 * 跳转至添加地区页
	 * @return 添加地区页
	 */
	public String add(){
		return "add";
	}
	/**
	 * 添加地区
	 * @param regions 地区,Regions
	 * @return json
	 * result 1.操作成功.0.操作失败
	 */
	public String saveAdd(){
		try{
			regionsManager.add(regions);
			this.showSuccessJson("地区添加成功");
		}catch(Exception e){
			e.printStackTrace();
			this.showErrorJson("地区添加失败");
		}
		return JSON_MESSAGE;
	}
	/**
	 * 添加子地区
	 * @param regions 地区,Regions
	 * @return json
	 * result 1.操作成功.0.操作失败
	 */
	public String saveAddchildren(){
		try{
			regionsManager.add(regions);
			this.showSuccessJson("子地区添加成功");
		}catch(Exception e){
			e.printStackTrace();
			this.showErrorJson("子地区添加失败");
		}
		return JSON_MESSAGE;
	}
	/**
	 * 跳转至会员修改页
	 * @param region_id 地区Id
	 * @return 会员修改页
	 */
	public String edit(){	
		regions = regionsManager.get(region_id);
		return "edit";	
	}
	/**
	 * 子地区
	 * @param region_id 地区Id
	 * @return
	 */
	public String children(){
		regions = regionsManager.get(region_id);
		return "children";
	}
	/**
	 * 修改地区
	 * @param regions 地区,Regions
	 * @return json
	 * result 1.操作成功.0.操作失败
	 */
	public String saveEdit(){
		try{
			regionsManager.update(regions);
			this.showSuccessJson("修改成功");
			
		}catch(Exception e){
			e.printStackTrace();
			this.showErrorJson("修改失败");
		}
		return JSON_MESSAGE;
	}
	/**
	 * 删除地区
	 * @param region_id 地区Id,Integer
	 * @return json
	 * result 1.操作成功.0.操作失败
	 */
	public String delete(){
		try {
			this.regionsManager.delete(region_id);
			this.showSuccessJson("删除成功");
		} catch (RuntimeException e) {
			this.showErrorJson("删除失败");
			e.printStackTrace();
		}
		return JSON_MESSAGE;
	}
	/**
	 * 初始化地区
	 * @return json
	 * result 1.操作成功.0.操作失败
	 */
	public String reset(){
		try {
			this.regionsManager.reset();
			this.showSuccessJson("初始化地区成功");
		} catch (RuntimeException e) {
			this.showErrorJson("初始化地区失败");
			e.printStackTrace();
		}
		return JSON_MESSAGE;
	}
	/**
	 * 获取子地区
	 * @return 子地区Json
	 */
	@SuppressWarnings("rawtypes")
	public String getChildren(){
		List list = this.regionsManager.listChildrenByid(regionid);
		this.json=JSONArray.fromObject(list).toString();
		return JSON_MESSAGE;
	}

	public IRegionsManager getRegionsManager() {
		return regionsManager;
	}

	public void setRegionsManager(IRegionsManager regionsManager) {
		this.regionsManager = regionsManager;
	}

	public List getListRegion() {
		return listRegion;
	}

	public void setListRegion(List listRegion) {
		this.listRegion = listRegion;
	}

	public Integer getParentid() {
		return parentid;
	}

	public void setParentid(Integer parentid) {
		this.parentid = parentid;
	}

	public Regions getRegions() {
		return regions;
	}

	public void setRegions(Regions regions) {
		this.regions = regions;
	}

	public Integer getRegion_id() {
		return region_id;
	}

	public void setRegion_id(Integer regionId) {
		region_id = regionId;
	}

	public Integer getRegiongrade() {
		return regiongrade;
	}

	public void setRegiongrade(Integer regiongrade) {
		this.regiongrade = regiongrade;
	}

	public Integer getRegionid() {
		return regionid;
	}

	public void setRegionid(Integer regionid) {
		this.regionid = regionid;
	}
	
}
