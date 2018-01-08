package com.enation.app.cms.core.taglib;

import java.util.List;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;

import com.enation.app.cms.core.model.DataField;
import com.enation.eop.sdk.webapp.taglib.BaseTaglibSupport;

public class ArticleFieldOutTaglib extends BaseTaglibSupport {
	
	
 
	@Override
	public int doStartTag() throws JspException {
		Map article =(Map) this.pageContext.getAttribute("article");
		if(article!=null){
			List<DataField> fieldList  =(List<DataField>)this.pageContext.getAttribute("fieldList");
			if(fieldList!=null){
			for(DataField field : fieldList){
				String name =  field.getEnglish_name();
				Object value = article.get(name);
				this.print("<td>" + value+"</td>");
			}
			}
			
		}
		
		return Tag.EVAL_BODY_INCLUDE;
	}

	@Override
	public int doAfterBody() {
	 

		return Tag.EVAL_PAGE;
	}
}
