package com.enation.app.base.component.widget.counter;

import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.service.IAccessRecorder;
import com.enation.eop.sdk.widget.AbstractWidget;

@Component("access")
@Scope("prototype")
public class AccessWidget extends AbstractWidget{
	private IAccessRecorder accessRecorder;
	
	public IAccessRecorder getAccessRecorder() {
		return accessRecorder;
	}

	public void setAccessRecorder(IAccessRecorder accessRecorder) {
		this.accessRecorder = accessRecorder;
	}

	@Override
	protected void display(Map<String, String> params) {
		Map map = this.accessRecorder.census();

		this.putData("access",map);
	}

	@Override
	protected void config(Map<String, String> params) {
		// TODO Auto-generated method stub
		
	}

}
