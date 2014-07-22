package org.alfresco.extension.pdftoolkit.repo.action.executer;

import java.util.List;

import org.alfresco.repo.action.ParameterDefinitionImpl;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.cmr.repository.NodeRef;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class PDFConvertToArchivableActionExecuter extends BasePDFActionExecuter {

	/**
     * The logger
     */
    private static Log         logger                   = LogFactory.getLog(PDFConvertToArchivableActionExecuter.class);

    /**
     * Action constants
     */
    public static final String NAME                     = "pdf-archive";
    public static final String PARAM_DESTINATION_FOLDER = "destination-folder";
    public static final String PARAM_DESTINATION_NAME   = "destination-name";
    public static final String PARAM_ARCHIVE_LEVEL		= "archive-level";


    /**
     * Add parameter definitions
     */
    @Override
    protected void addParameterDefinitions(List<ParameterDefinition> paramList)
    {
        paramList.add(new ParameterDefinitionImpl(PARAM_DESTINATION_FOLDER, DataTypeDefinition.NODE_REF, false, getParamDisplayLabel(PARAM_DESTINATION_FOLDER)));
        paramList.add(new ParameterDefinitionImpl(PARAM_DESTINATION_NAME, DataTypeDefinition.TEXT, false, getParamDisplayLabel(PARAM_DESTINATION_NAME)));
        
        super.addParameterDefinitions(paramList);
    }
    
	@Override
	protected void executeImpl(Action action, NodeRef actionedUponNodeRef) {
		
		// get a connection to OpenOffice via the usual Alfresco means
		
		// create the right configuration for OpenOffice
		
		// transform to PDF/A

	}

}
