package org.alfresco.extension.pdftoolkit.repo.action.executer;

import java.util.List;

import org.alfresco.extension.pdftoolkit.constants.PDFToolkitConstants;
import org.alfresco.repo.action.ParameterDefinitionImpl;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.cmr.repository.NodeRef;

public class PDFDecryptionActionExecuter extends BasePDFActionExecuter 
{

    /**
     * Action constants
     */
    public static final String            NAME                                = "pdf-decryption";
    
	/**
     * Add parameter definitions
     */
    @Override
    protected void addParameterDefinitions(List<ParameterDefinition> paramList)
    {

        paramList.add(new ParameterDefinitionImpl(PDFToolkitConstants.PARAM_DESTINATION_FOLDER, DataTypeDefinition.NODE_REF, false, getParamDisplayLabel(PDFToolkitConstants.PARAM_DESTINATION_FOLDER)));
        paramList.add(new ParameterDefinitionImpl(PDFToolkitConstants.PARAM_OWNER_PASSWORD, DataTypeDefinition.TEXT, true, getParamDisplayLabel(PDFToolkitConstants.PARAM_OWNER_PASSWORD)));
        paramList.add(new ParameterDefinitionImpl(PDFToolkitConstants.PARAM_DESTINATION_NAME, DataTypeDefinition.TEXT, false, getParamDisplayLabel(PDFToolkitConstants.PARAM_DESTINATION_NAME)));

        super.addParameterDefinitions(paramList);
    }
    
	@Override
	protected void executeImpl(Action action, NodeRef actionedUponNodeRef) 
	{
		NodeRef result = pdfToolkitService.decryptPDF(actionedUponNodeRef, action.getParameterValues());
		action.setParameterValue(PARAM_RESULT, result);
	}
}
