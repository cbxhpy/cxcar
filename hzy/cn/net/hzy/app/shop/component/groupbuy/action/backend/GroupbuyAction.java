package cn.net.hzy.app.shop.component.groupbuy.action.backend;

import java.io.File;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Autowired;

import cn.net.hzy.app.shop.component.groupbuy.model.GroupBuy;
import cn.net.hzy.app.shop.component.groupbuy.service.IGroupBuyManager;

import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.framework.action.WWAction;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.FileUtil;
import com.enation.framework.util.StringUtil;

/**
 * 团购Action
 */
@ParentPackage("eop_default")
@Namespace("/shop/admin")
@Results({
	 @Result(name="list",type="freemarker", location="/shop/admin/groupbuy/groupbuy/groupbuy_list.html"),
	 @Result(name="add",type="freemarker", location="/shop/admin/groupbuy/groupbuy/groupbuy_add.html"),
	 @Result(name="edit",type="freemarker", location="/shop/admin/groupbuy/groupbuy/groupbuy_edit.html")
})

@Action("groupBuy")
public class GroupbuyAction extends WWAction{
	
	 @Autowired
	 private IGroupBuyManager groupBuyManager;
	 private int gbid;	 
	 private Integer status;
	 private String start_time;
	 private String end_time;
	 private GroupBuy groupBuy;
	 private File image;
	 private String imageFileName;
	 private String imgPath;
	 protected Integer[] group_id;
	 
	/**
	 * 跳转至团购列表
	 * @return
	 */
	public String list(){
		return "list";
	}
	
	/** 
	 * 新增团购
	 * @return
	 */
	public String add(){
		
		return "add";
	}
	
	/** 
	 * 编辑团购
	 * @return
	 */
	public String edit(){
		groupBuy  = groupBuyManager.get(gbid); 
		start_time=groupBuy.getStart_time_str();
		end_time=groupBuy.getEnd_time_str();
		if(!StringUtil.isEmpty(groupBuy.getGroup_pic_url())){			
			imgPath = UploadUtil.replacePath(groupBuy.getGroup_pic_url());
			
		}
		return "edit";
	}
	
	public String saveEdit() {
		if (image != null) {
			if (FileUtil.isAllowUp(imageFileName)) {
				String path = UploadUtil.upload(this.image,this.imageFileName, "groupbuy");
				groupBuy.setGroup_pic_url(path);
			} else {
				this.showErrorJson("不允许上传的文件格式，请上传gif,jpg,bmp,swf格式文件。");
				return JSON_MESSAGE;
			}
		}
		 groupBuy.setStart_time(DateUtil.getDatelineLong(start_time));
		 groupBuy.setEnd_time(DateUtil.getDatelineLong(end_time));
		 groupBuyManager.update(groupBuy);
		this.showSuccessJson("修改团购成功");
		return JSON_MESSAGE;
	}
	
	 /**
	  * 按活动id显示团购json
	  * @return
	  */
	 public String listJson(){
		 try {
			 
			this.webpage = this.groupBuyManager.listGroupBuy(getPage(), getPageSize(),status);
			this.showGridJson(webpage);
			
		} catch (Exception e) {
			this.logger.error("查询出错",e);
			this.showErrorJson("查询出错");
			
		}
		 return WWAction.JSON_MESSAGE;
	 }
	 
	 /**
	 * 删除商品
	 * @author LiFenLong
	 * @param goods_id 商品Id数组,Integer[]
	 * @return json
	 * result 1.操作成功。0.操作失败
	 */
	public String delete() {
		
		try {
			if (group_id != null)
			this.groupBuyManager.delete(group_id);
			this.showSuccessJson("删除成功");
		} catch (RuntimeException e) {
			this.showErrorJson("删除失败");
			logger.error("团购删除失败", e);
		}
		return WWAction.JSON_MESSAGE;
	}

	 /**
	  * 保存添加
	  * @return
	  */
	 public String saveAdd(){
		 try {
			 if(image!=null){
				//判断文件类型
				String allowTYpe = "gif,jpg,bmp,png";
				if (!imageFileName.trim().equals("") && imageFileName.length() > 0) {
					String ex = imageFileName.substring(imageFileName.lastIndexOf(".") + 1, imageFileName.length());
					if(allowTYpe.toString().indexOf(ex.toLowerCase()) < 0){
						throw new RuntimeException("对不起,只能上传gif,jpg,bmp,png格式的图片！");
					}
				}
				
				//判断文件大小
				
				if(image.length() > 2000 * 1024){
					throw new RuntimeException("图片不能大于2MB！");
					 
				}
				
				String imgPath=	UploadUtil.upload(image, imageFileName, "groupbuy");
				groupBuy.setGroup_pic_url(imgPath);
			}
			 groupBuy.setStart_time(DateUtil.getDatelineLong(start_time));
			 groupBuy.setEnd_time(DateUtil.getDatelineLong(end_time));
			 groupBuy.setCreate_time(DateUtil.getDateline());
			 groupBuyManager.add(groupBuy);
			this.showSuccessJson("添加成功");
		} catch (Exception e) {
			this.showErrorJson("添加失败");
			this.logger.error("团购添加失败："+e);
		}
		 return WWAction.JSON_MESSAGE;
		 
	 }

	 
	 //get set
	public IGroupBuyManager getGroupBuyManager() {
		return groupBuyManager;
	}
	public void setGroupBuyManager(IGroupBuyManager groupBuyManager) {
		this.groupBuyManager = groupBuyManager;
	}
	public int getGbid() {
		return gbid;
	}
	public void setGbid(int gbid) {
		this.gbid = gbid;
	}
	
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	
	public GroupBuy getGroupBuy() {
		return groupBuy;
	}
	public void setGroupBuy(GroupBuy groupBuy) {
		this.groupBuy = groupBuy;
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
	public String getStart_time() {
		return start_time;
	}
	public void setStart_time(String start_time) {
		this.start_time = start_time;
	}
	public String getEnd_time() {
		return end_time;
	}
	public void setEnd_time(String end_time) {
		this.end_time = end_time;
	}
	public Integer[] getGroup_id() {
		return group_id;
	}
	public void setGroup_id(Integer[] group_id) {
		this.group_id = group_id;
	}

	public String getImgPath() {
		return imgPath;
	}

	public void setImgPath(String imgPath) {
		this.imgPath = imgPath;
	}
}
