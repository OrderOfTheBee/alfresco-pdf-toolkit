package org.alfresco.extension.pdftoolkit.repo.action.executer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jooreports.converter.DocumentFamily;
import net.sf.jooreports.converter.DocumentFormat;

import org.alfresco.extension.pdftoolkit.constraints.MapConstraint;
import org.alfresco.repo.action.ParameterDefinitionImpl;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.cmr.repository.NodeRef;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
		
		// get a connection to OpenOffice via the usual Alfresco means
		
		// create the right configuration for OpenOffice
		
		// transform to PDF/A

	}

	/**
	* Returns DocumentFormat of PDF/A
	*/
	/*private DocumentFormat toDocumentFormatPDFA(int level) {

	    DocumentFormat customPdfFormat = new DocumentFormat(PORTABEL_FORMAT, PDF_APP, "pdf");
	    customPdfFormat.setExportFilter(DocumentFamily.TEXT, "writer_pdf_Export");
	    final Map<String, Integer> pdfOptions = new HashMap<String, Integer>();
	    pdfOptions.put("SelectPdfVersion", level);
	    customPdfFormat.setExportOption(DocumentFamily.TEXT, "FilterData", pdfOptions);
	    return customPdfFormat;
	}*/
	
    /**
     * Setter for constraint bean
     * 
     * @param encryptionLevelConstraint
     */
    public void setArchiveLevelConstraint(MapConstraint mc)
    {
        archiveLevelConstraint.putAll(mc.getAllowableValues());
    }
}
