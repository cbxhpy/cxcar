package com.enation.app.shop.component.virtualcat.action;

import java.io.File;
import java.util.List;

import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

import com.enation.app.shop.component.virtualcat.model.VirtualCat;
import com.enation.app.shop.component.virtualcat.service.IVirtualCatManager;
import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.framework.action.WWAction;
import com.enation.framework.util.FileUtil;

/**
 * 用户自定义分类管理Action
 * @author lzf
 * 2012-11-29上午10:10:29
 */
@ParentPackage("shop_default")
@Namespace("/shop/admin")
@Results({ 
    @Result(name="list", type="freemarker", location="/com/enation/app/shop/component/virtualcat/action/list.html") ,
	@Result(name="edit", type="freemarker", location="/com/enation/app/shop/component/virtualcat/action/edit.html") ,
	@Result(name="add", type="freemarker", location="/com/enation/app/shop/component/virtualcat/action/add.html")
})
public class VirtualCatAction extends WWAction {

	private IVirtualCatManager virtualCatManager;
	private VirtualCat myCat;
	private int vid;
	protected List catList;
	private File image;
	private String imageFileName;
	
	public String list(){
		catList = this.virtualCatManager.getTree();
		return "list";
	}
	
	public String add(){
		catList = this.virtualCatManager.getTree();
		return "add";
	}
	
	public String saveAdd(){
		if (image != null) {
			if (FileUtil.isAllowUp(imageFileName)) {
				myCat.setPic_url( UploadUtil.upload(image,imageFileName,"virtualcat") );
				
			} else {
				this.msgs.add("不允许上传的文件格式，请上传gif,jpg,bmp格式文件。");
				return WWAction.MESSAGE;
			}
		}
		this.virtualCatManager.add(myCat);
		this.msgs.add("添加成功");
		this.urls.put("分类列表", "virtual-cat!list.do");
		return WWAction.MESSAGE;
	}
	
	public String edit(){
		myCat = this.virtualCatManager.get(vid);
		catList = this.virtualCatManager.getTree();
		return "edit";
	}
	
	public String saveEdit(){
		if (image != null) {
			if (FileUtil.isAllowUp(imageFileName)) {
				myCat.setPic_url( UploadUtil.upload(image,imageFileName,"virtualcat") );
				
			} else {
				this.msgs.add("不允许上传的文件格式，请上传gif,jpg,bmp格式文件。");
				return WWAction.MESSAGE;
			}
		}
		this.virtualCatManager.edit(myCat);
		this.msgs.add("修改成功");
		this.urls.put("分类列表", "virtual-cat!list.do");
		return WWAction.MESSAGE;
	}
	
	public String delete(){
		this.virtualCatManager.delete(vid);
		this.json = "{\"result\":\"0\"}";
		return WWAction.JSON_MESSAGE;
	}

	public IVirtualCatManager getVirtualCatManager() {
		return virtualCatManager;
	}

	public void setVirtualCatManager(IVirtualCatManager virtualCatManager) {
		this.virtualCatManager = virtualCatManager;
	}

	public VirtualCat getMyCat() {
		return myCat;
	}

	public void setMyCat(VirtualCat myCat) {
		this.myCat = myCat;
	}

	public int getVid() {
		return vid;
	}

	public void setVid(int vid) {
		this.vid = vid;
	}

	public List getCatList() {
		return catList;
	}

	public void setCatList(List catList) {
		this.catList = catList;
	}

	public File getImage() {
		return image;
	}

	public void setImage(File image) {
		this.image = image;
	}

	public String getImageFileName() {
		return imageFileName;
	}

	public void setImageFileName(String imageFileName) {
		this.imageFileName = imageFileName;
	}
	
	

}
