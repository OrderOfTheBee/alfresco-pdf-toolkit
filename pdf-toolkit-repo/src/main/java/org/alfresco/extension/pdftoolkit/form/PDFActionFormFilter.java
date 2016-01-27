package org.alfresco.extension.pdftoolkit.form;

import java.util.List;
import java.util.Map;

import org.alfresco.extension.pdftoolkit.constants.PDFToolkitConstants;
import org.alfresco.extension.pdftoolkit.repo.action.executer.PDFWatermarkActionExecuter;
import org.alfresco.repo.action.ActionDefinitionImpl;
import org.alfresco.repo.forms.Form;
import org.alfresco.repo.forms.FormData;
import org.alfresco.repo.forms.FormData.FieldData;
import org.alfresco.repo.forms.processor.AbstractFilter;
import org.alfresco.repo.forms.processor.action.ActionFormResult;
import org.alfresco.repo.model.Repository;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class PDFActionFormFilter extends AbstractFilter<Object, ActionFormResult> {
	
	private static Log 		logger 						= LogFactory.getLog(PDFActionFormFilter.class);
	private String 			WATERMARK_IMAGE_FIELD 		= "assoc_watermark-image_added";
	private String			DESTINATION_FOLDER_FIELD 	= "assoc_destination-folder_added";
	private String			INPLACE_PARAM				= "prop_" + PDFToolkitConstants.PARAM_INPLACE;
	
	private ServiceRegistry serviceRegistry;		
	private Repository repositoryHelper;
	
	@Override
	public void afterGenerate(Object obj, List<String> fields,
			List<String> forcedFields, Form form, Map<String, Object> context) {
		logger.debug("afterGenerate");
		//NTM - nothing to do here at the moment
	}

	@Override
	public void afterPersist(Object obj, FormData formData, ActionFormResult result) {
		logger.debug("afterPersist");
		//NTM - nothing to do here at the moment.
	}
	
	@Override
	public void beforeGenerate(Object obj, List<String> fields,
			List<String> forcedFields, Form form, Map<String, Object> context) {
		logger.debug("beforeGenerate");
		//NTM - nothing to do here at the moment
	}

	@Override
	public void beforePersist(Object obj, FormData formData) {
		logger.debug("beforePersist");
		
		NodeService ns = serviceRegistry.getNodeService();
		
		//check the action, is it one we need to handle?
		if(obj != null)
		{
			/*
			 * For all pdf-toolkit actions, check for the "in place" parameter.  If it is
			 * set to true, rewrite the destination folder field value so Alfresco will
			 * let the form action pass.  
			 */
			FieldData inplace = formData.getFieldData(INPLACE_PARAM);
			if(inplace != null && Boolean.valueOf(String.valueOf(inplace.getValue())))
			{
				formData.addFieldData(DESTINATION_FOLDER_FIELD, repositoryHelper.getCompanyHome(), true);
			}
			
			ActionDefinitionImpl act = (ActionDefinitionImpl)obj;
			if(act.getName().equals(PDFWatermarkActionExecuter.NAME))
			{
				/*
				 * fix form data to prevent formProcessor complaining about 
				 * invalid nodeRef (watermark action).  Even if the watermark-image
				 * action param is optional, Alfresco barks if the field value
				 * is not a syntactically valid noderef.  
				 */
				FieldData data = formData.getFieldData(WATERMARK_IMAGE_FIELD);
				if(data.getValue() == null || data.getValue().toString().equals(""))
				{
					/*
					 * set the field value = to the destination folder.  This value isn't
					 * actually used, it is just required to get the form validated.  Is this an
					 * Alfresco bug?  Should non-mandatory fields require a valid NodeRef?  
					 */
					FieldData dest = formData.getFieldData(DESTINATION_FOLDER_FIELD);
					formData.addFieldData(WATERMARK_IMAGE_FIELD, dest.getValue(), true);
				}
			}
		}
		logger.debug("exit beforePersist");
	}

	public void setServiceRegistry(ServiceRegistry serviceRegistry)
	{
		this.serviceRegistry = serviceRegistry;
	}
	
	public void setRepositoryHelper(Repository repositoryHelper)
	{
		this.repositoryHelper = repositoryHelper;
	}
}
