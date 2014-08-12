package org.alfresco.extension.pdftoolkit.repo.action.executer;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.enterprise.repo.content.JodConverter;
import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.extension.pdftoolkit.constraints.MapConstraint;
import org.alfresco.extension.pdftoolkit.model.PDFToolkitModel;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.action.ParameterDefinitionImpl;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.artofsolving.jodconverter.OfficeDocumentConverter;
import org.artofsolving.jodconverter.document.DefaultDocumentFormatRegistry;
import org.artofsolving.jodconverter.document.DocumentFamily;
import org.artofsolving.jodconverter.document.DocumentFormat;
import org.artofsolving.jodconverter.document.DocumentFormatRegistry;

public class PDFConvertToArchivableActionExecuter extends BasePDFActionExecuter 
{

	/**
     * The logger
     */
    private static Log         logger                   				  = LogFactory.getLog(PDFConvertToArchivableActionExecuter.class);

    /**
     * Action constants
     */
    public static final String NAME                     				  = "pdf-archive";
    public static final String PARAM_DESTINATION_FOLDER 				  = "destination-folder";
    public static final String PARAM_ARCHIVE_LEVEL						  = "archive-level";

    /**
     * Constraints
     */
    public static HashMap<String, String> archiveLevelConstraint          = new HashMap<String, String>();
    
    private final String PDFA											  = "PDF/A";
    
    private JodConverter jodConverter;
    
    /**
     * Add parameter definitions
     */
    @Override
    protected void addParameterDefinitions(List<ParameterDefinition> paramList)
    {
        paramList.add(new ParameterDefinitionImpl(PARAM_DESTINATION_FOLDER, DataTypeDefinition.NODE_REF, false, getParamDisplayLabel(PARAM_DESTINATION_FOLDER)));
        paramList.add(new ParameterDefinitionImpl(PARAM_ARCHIVE_LEVEL, DataTypeDefinition.INT, true, getParamDisplayLabel(PARAM_ARCHIVE_LEVEL), false, "pdfc-archivelevel"));

        super.addParameterDefinitions(paramList);
    }
    
	@Override
	protected void executeImpl(Action action, NodeRef actionedUponNodeRef) 
	{
		
		NodeService ns = serviceRegistry.getNodeService();
		ContentService cs = serviceRegistry.getContentService();
		
		if(!ns.exists(actionedUponNodeRef))
		{
			throw new AlfrescoRuntimeException("PDF/A convert action called on non-existent node: " + actionedUponNodeRef);
		}
		
        Boolean inplace = Boolean.valueOf(String.valueOf(action.getParameterValue(PARAM_INPLACE)));
        Integer archiveLevel = Integer.valueOf(String.valueOf(action.getParameterValue(PARAM_ARCHIVE_LEVEL)));
        
		// get an output file for the new PDF (temp file)
        File out = getTempFile(actionedUponNodeRef);
                   
        // copy the source node content to a temp file
        File in = nodeRefToTempFile(actionedUponNodeRef);
        
		// transform to PDF/A
        DocumentFormatRegistry formatRegistry = new DefaultDocumentFormatRegistry();
        formatRegistry.getFormatByExtension(PDF).setInputFamily(DocumentFamily.DRAWING);
        OfficeDocumentConverter converter = new OfficeDocumentConverter(jodConverter.getOfficeManager(), formatRegistry);
        
		converter.convert(in, out, getDocumentFormat(archiveLevel));
		
		NodeRef destinationNode = createDestinationNode(String.valueOf(ns.getProperty(actionedUponNodeRef, ContentModel.PROP_NAME)), 
        		(NodeRef)action.getParameterValue(PARAM_DESTINATION_FOLDER), actionedUponNodeRef, inplace);
        ContentWriter writer = serviceRegistry.getContentService().getWriter(destinationNode, ContentModel.PROP_CONTENT, true);
        writer.setEncoding(cs.getReader(actionedUponNodeRef, ContentModel.PROP_CONTENT).getEncoding());
        writer.setMimetype(FILE_MIMETYPE);
        writer.putContent(out);

        // apply the marker aspect
        ns.addAspect(destinationNode, PDFToolkitModel.ASPECT_ARCHIVAL, new HashMap<QName, Serializable>());
        // delete the temp files
        in.delete();
        out.delete();

	}

	/**
	* Returns a DocumentFormat that will output to PDF/A
	*/
	private DocumentFormat getDocumentFormat(int level) {

		DocumentFormat format = new DocumentFormat(PDFA, PDF, FILE_MIMETYPE);
	    Map<String, Object> properties = new HashMap<String, Object>();
	    properties.put("FilterName", "draw_pdf_Export");

	    Map<String, Object> filterData = new HashMap<String, Object>();
	    filterData.put("SelectPdfVersion", level);
	    properties.put("FilterData", filterData);

	    format.setStoreProperties(DocumentFamily.DRAWING, properties);

	    return format;
	}
	
    /**
     * Setter for constraint bean
     * 
     * @param encryptionLevelConstraint
     */
    public void setArchiveLevelConstraint(MapConstraint mc)
    {
        archiveLevelConstraint.putAll(mc.getAllowableValues());
    }

    public void setJodConverter(JodConverter jodConverter)
    {
    	this.jodConverter = jodConverter;
    }
}
