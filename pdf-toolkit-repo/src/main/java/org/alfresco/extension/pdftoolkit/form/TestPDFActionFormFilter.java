package org.alfresco.extension.pdftoolkit.form;

import org.alfresco.repo.action.ActionDefinitionImpl;
import org.alfresco.repo.forms.FormData;
import org.alfresco.service.ServiceRegistry;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.tradeshift.test.remote.Remote;
import com.tradeshift.test.remote.RemoteTestRunner;

@RunWith(RemoteTestRunner.class)
@Remote(runnerClass=SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:alfresco/application-context.xml")
public class TestPDFActionFormFilter {
	// parameter constants from PDFActionFormFilter
	private String WATERMARK_IMAGE_FIELD 		= "assoc_watermark-image_added";

	@Autowired @Qualifier("ServiceRegistry")
	private ServiceRegistry serviceRegistry;
	ActionDefinitionImpl act = new ActionDefinitionImpl("test");

	@Test
	public void testNullParamsInBeforePersist() {
		PDFActionFormFilter filter = new PDFActionFormFilter();
		filter.setServiceRegistry(serviceRegistry);
		FormData formDataNullInplace = new FormData();
		formDataNullInplace.addFieldData(WATERMARK_IMAGE_FIELD, "");

		filter.beforePersist((Object) act, formDataNullInplace);
	}
}
