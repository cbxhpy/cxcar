package com.enation.test.eop.component;

import org.junit.Test;

import com.enation.framework.component.IComponent;
import com.enation.framework.test.SpringTestSupport;

public class ComponentTest extends SpringTestSupport {
	private IComponent component;
	
	public void exportDB(){
		String[] tables = {"receipt"}; 
		//System.out.println(DBSolutionFactory.dbExport(tables, false, "es_"));
	}

	public void componentInstall() {
		component = SpringTestSupport.getBean("orderReturnsComponent");
		component.install();
	}
	
	@Test
	public void doTest(){
		//componentInstall();
		exportDB();
	}
}
