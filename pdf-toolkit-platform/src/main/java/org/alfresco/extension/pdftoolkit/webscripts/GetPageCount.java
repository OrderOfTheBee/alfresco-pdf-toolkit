package org.alfresco.extension.pdftoolkit.webscripts;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.model.ContentModel;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.NodeRef;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;

import com.itextpdf.text.pdf.PdfReader;

public class GetPageCount extends DeclarativeWebScript 
{
	private ServiceRegistry 		serviceRegistry;
	private int 					count 				= -1;
	private static final Log 		logger 				= LogFactory.getLog(GetPageCount.class);

	
	public Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) 
	{
		String nodeRef = req.getParameter("nodeRef");
		Map<String, Object> model = new HashMap<String, Object>();
		
		try
		{
			ContentReader reader = serviceRegistry
					.getContentService().getReader(new NodeRef(nodeRef), ContentModel.PROP_CONTENT);
			PdfReader pdfReader = new PdfReader(reader.getContentInputStream());
			count = pdfReader.getNumberOfPages();
			pdfReader.close();			
		}
		catch(IOException ioex)
		{
			logger.error("Error fetching page count for document: " + ioex);
		}
		
		model.put("pageCount", count);
		return model;
	}
	
	public void setServiceRegistry(ServiceRegistry serviceRegistry)
	{
		this.serviceRegistry = serviceRegistry;
	}
}
