/*
 * Copyright 2008-2012 Alfresco Software Limited.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * This file is part of an unsupported extension to Alfresco.
 */

package org.alfresco.extension.pdftoolkit.repo.action.executer;


import java.util.HashMap;
import java.util.List;

import org.alfresco.extension.pdftoolkit.constants.PDFToolkitConstants;
import org.alfresco.extension.pdftoolkit.constraints.MapConstraint;
import org.alfresco.repo.action.ParameterDefinitionImpl;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.cmr.repository.NodeRef;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class PDFEncryptionActionExecuter extends BasePDFActionExecuter
{

    /**
     * The logger
     */
    private static Log                    logger                              = LogFactory.getLog(PDFEncryptionActionExecuter.class);
    
    /**
     * Constraints
     */
    public static HashMap<String, String> encryptionLevelConstraint           = new HashMap<String, String>();

    /**
     * Action constants
     */
    public static final String            NAME                                = "pdf-encryption";

    /**
     * Setter for constraint bean
     * 
     * @param encryptionLevelConstraint
     */
    public void setEncryptionLevelConstraint(MapConstraint mc)
    {
        encryptionLevelConstraint.putAll(mc.getAllowableValues());
    }

    /**
     * Add parameter definitions
     */
    @Override
    protected void addParameterDefinitions(List<ParameterDefinition> paramList)
    {

        paramList.add(new ParameterDefinitionImpl(PDFToolkitConstants.PARAM_DESTINATION_FOLDER, DataTypeDefinition.NODE_REF, false, getParamDisplayLabel(PDFToolkitConstants.PARAM_DESTINATION_FOLDER)));
        paramList.add(new ParameterDefinitionImpl(PDFToolkitConstants.PARAM_USER_PASSWORD, DataTypeDefinition.TEXT, true, getParamDisplayLabel(PDFToolkitConstants.PARAM_USER_PASSWORD)));
        paramList.add(new ParameterDefinitionImpl(PDFToolkitConstants.PARAM_OWNER_PASSWORD, DataTypeDefinition.TEXT, true, getParamDisplayLabel(PDFToolkitConstants.PARAM_OWNER_PASSWORD)));
        paramList.add(new ParameterDefinitionImpl(PDFToolkitConstants.PARAM_ALLOW_PRINT, DataTypeDefinition.BOOLEAN, true, getParamDisplayLabel(PDFToolkitConstants.PARAM_ALLOW_PRINT)));
        paramList.add(new ParameterDefinitionImpl(PDFToolkitConstants.PARAM_ALLOW_COPY, DataTypeDefinition.BOOLEAN, true, getParamDisplayLabel(PDFToolkitConstants.PARAM_ALLOW_COPY)));
        paramList.add(new ParameterDefinitionImpl(PDFToolkitConstants.PARAM_ALLOW_CONTENT_MODIFICATION, DataTypeDefinition.BOOLEAN, true, getParamDisplayLabel(PDFToolkitConstants.PARAM_ALLOW_CONTENT_MODIFICATION)));
        paramList.add(new ParameterDefinitionImpl(PDFToolkitConstants.PARAM_ALLOW_ANNOTATION_MODIFICATION, DataTypeDefinition.BOOLEAN, true, getParamDisplayLabel(PDFToolkitConstants.PARAM_ALLOW_ANNOTATION_MODIFICATION)));
        paramList.add(new ParameterDefinitionImpl(PDFToolkitConstants.PARAM_ALLOW_FORM_FILL, DataTypeDefinition.BOOLEAN, true, getParamDisplayLabel(PDFToolkitConstants.PARAM_ALLOW_FORM_FILL)));
        paramList.add(new ParameterDefinitionImpl(PDFToolkitConstants.PARAM_ALLOW_SCREEN_READER, DataTypeDefinition.BOOLEAN, true, getParamDisplayLabel(PDFToolkitConstants.PARAM_ALLOW_SCREEN_READER)));
        paramList.add(new ParameterDefinitionImpl(PDFToolkitConstants.PARAM_ALLOW_DEGRADED_PRINT, DataTypeDefinition.BOOLEAN, true, getParamDisplayLabel(PDFToolkitConstants.PARAM_ALLOW_DEGRADED_PRINT)));
        paramList.add(new ParameterDefinitionImpl(PDFToolkitConstants.PARAM_ALLOW_ASSEMBLY, DataTypeDefinition.BOOLEAN, true, getParamDisplayLabel(PDFToolkitConstants.PARAM_ALLOW_ASSEMBLY)));
        paramList.add(new ParameterDefinitionImpl(PDFToolkitConstants.PARAM_ENCRYPTION_LEVEL, DataTypeDefinition.TEXT, true, getParamDisplayLabel(PDFToolkitConstants.PARAM_ENCRYPTION_LEVEL), false, "pdfc-encryptionlevel"));
        paramList.add(new ParameterDefinitionImpl(PDFToolkitConstants.PARAM_EXCLUDE_METADATA, DataTypeDefinition.BOOLEAN, true, getParamDisplayLabel(PDFToolkitConstants.PARAM_EXCLUDE_METADATA)));
        paramList.add(new ParameterDefinitionImpl(PDFToolkitConstants.PARAM_DESTINATION_NAME, DataTypeDefinition.TEXT, false, getParamDisplayLabel(PDFToolkitConstants.PARAM_DESTINATION_NAME)));

        super.addParameterDefinitions(paramList);
    }

    /**
     * @see org.alfresco.repo.action.executer.ActionExecuterAbstractBase#executeImpl(org.alfresco.service.cmr.repository.NodeRef,
     * org.alfresco.service.cmr.repository.NodeRef)
     */
    @Override
    protected void executeImpl(Action action, NodeRef actionedUponNodeRef)
    {
    	NodeRef result = pdfToolkitService.encryptPDF(actionedUponNodeRef, action.getParameterValues());
    	action.setParameterValue(PARAM_RESULT, result);
    }
}
