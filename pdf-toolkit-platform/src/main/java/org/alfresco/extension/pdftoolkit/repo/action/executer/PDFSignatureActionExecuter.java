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


public class PDFSignatureActionExecuter extends BasePDFStampActionExecuter
{

    /**
     * The logger
     */
    private static Log                    logger                   = LogFactory.getLog(PDFSignatureActionExecuter.class);
    
    /**
     * Constraints
     */
    public static HashMap<String, String> visibilityConstraint     = new HashMap<String, String>();
    public static HashMap<String, String> keyTypeConstraint        = new HashMap<String, String>();

    /**
     * Action constants
     */
    public static final String            NAME                     = "pdf-signature";

    /**
     * Constraint beans
     * 
     * @param mc
     */
    public void setKeyTypeConstraint(MapConstraint mc)
    {
        keyTypeConstraint.putAll(mc.getAllowableValues());
    }

    public void setVisibilityConstraint(MapConstraint mc)
    {
        visibilityConstraint.putAll(mc.getAllowableValues());
    }
    
    /**
     * Add parameter definitions
     */
    @Override
    protected void addParameterDefinitions(List<ParameterDefinition> paramList)
    {

        paramList.add(new ParameterDefinitionImpl(PDFToolkitConstants.PARAM_DESTINATION_FOLDER, DataTypeDefinition.NODE_REF, false, getParamDisplayLabel(PDFToolkitConstants.PARAM_DESTINATION_FOLDER)));
        paramList.add(new ParameterDefinitionImpl(PDFToolkitConstants.PARAM_PRIVATE_KEY, DataTypeDefinition.NODE_REF, true, getParamDisplayLabel(PDFToolkitConstants.PARAM_PRIVATE_KEY)));
        paramList.add(new ParameterDefinitionImpl(PDFToolkitConstants.PARAM_VISIBILITY, DataTypeDefinition.TEXT, true, getParamDisplayLabel(PDFToolkitConstants.PARAM_VISIBILITY), false, "pdfc-visibility"));
        paramList.add(new ParameterDefinitionImpl(PDFToolkitConstants.PARAM_LOCATION, DataTypeDefinition.TEXT, false, getParamDisplayLabel(PDFToolkitConstants.PARAM_LOCATION)));
        paramList.add(new ParameterDefinitionImpl(PDFToolkitConstants.PARAM_REASON, DataTypeDefinition.TEXT, false, getParamDisplayLabel(PDFToolkitConstants.PARAM_REASON)));
        paramList.add(new ParameterDefinitionImpl(PDFToolkitConstants.PARAM_KEY_PASSWORD, DataTypeDefinition.TEXT, true, getParamDisplayLabel(PDFToolkitConstants.PARAM_KEY_PASSWORD)));
        paramList.add(new ParameterDefinitionImpl(PDFToolkitConstants.PARAM_WIDTH, DataTypeDefinition.INT, false, getParamDisplayLabel(PDFToolkitConstants.PARAM_WIDTH)));
        paramList.add(new ParameterDefinitionImpl(PDFToolkitConstants.PARAM_HEIGHT, DataTypeDefinition.INT, false, getParamDisplayLabel(PDFToolkitConstants.PARAM_HEIGHT)));
        paramList.add(new ParameterDefinitionImpl(PDFToolkitConstants.PARAM_KEY_TYPE, DataTypeDefinition.TEXT, true, getParamDisplayLabel(PDFToolkitConstants.PARAM_KEY_TYPE), false, "pdfc-keytype"));
        paramList.add(new ParameterDefinitionImpl(PDFToolkitConstants.PARAM_ALIAS, DataTypeDefinition.TEXT, true, getParamDisplayLabel(PDFToolkitConstants.PARAM_ALIAS)));
        paramList.add(new ParameterDefinitionImpl(PDFToolkitConstants.PARAM_STORE_PASSWORD, DataTypeDefinition.TEXT, true, getParamDisplayLabel(PDFToolkitConstants.PARAM_STORE_PASSWORD)));
        paramList.add(new ParameterDefinitionImpl(PDFToolkitConstants.PARAM_DESTINATION_NAME, DataTypeDefinition.TEXT, false, getParamDisplayLabel(PDFToolkitConstants.PARAM_DESTINATION_NAME)));
        paramList.add(new ParameterDefinitionImpl(PDFToolkitConstants.PARAM_NEW_REVISION, DataTypeDefinition.BOOLEAN, false, getParamDisplayLabel(PDFToolkitConstants.PARAM_NEW_REVISION), false));

        super.addParameterDefinitions(paramList);

    }


    /**
     * @see org.alfresco.repo.action.executer.ActionExecuterAbstractBase#executeImpl(org.alfresco.service.cmr.repository.NodeRef,
     * org.alfresco.service.cmr.repository.NodeRef)
     */
    @Override
    protected void executeImpl(Action action, NodeRef actionedUponNodeRef)
    {
    	NodeRef result = pdfToolkitService.signPDF(actionedUponNodeRef, action.getParameterValues());
    	action.setParameterValue(PARAM_RESULT, result);
    }
}
