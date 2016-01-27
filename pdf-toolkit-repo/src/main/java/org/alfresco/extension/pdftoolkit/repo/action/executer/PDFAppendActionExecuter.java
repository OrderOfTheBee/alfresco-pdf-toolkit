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
import java.util.Map;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.extension.pdftoolkit.constants.PDFToolkitConstants;
import org.alfresco.repo.action.ParameterDefinitionImpl;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.NodeRef;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Append PDF action executer
 * 
 * @author Jared Ottley
 * 
 */

public class PDFAppendActionExecuter extends BasePDFActionExecuter
{

    /**
     * The logger
     */
    private static Log         logger                   = LogFactory.getLog(PDFAppendActionExecuter.class);

    /**
     * Action constants
     */
    public static final String NAME                     = "pdf-append";

    /**
     * Add parameter definitions
     */
    @Override
    protected void addParameterDefinitions(List<ParameterDefinition> paramList)
    {
        paramList.add(new ParameterDefinitionImpl(PDFToolkitConstants.PARAM_TARGET_NODE, DataTypeDefinition.NODE_REF, true, getParamDisplayLabel(PDFToolkitConstants.PARAM_TARGET_NODE)));
        paramList.add(new ParameterDefinitionImpl(PDFToolkitConstants.PARAM_DESTINATION_FOLDER, DataTypeDefinition.NODE_REF, false, getParamDisplayLabel(PDFToolkitConstants.PARAM_DESTINATION_FOLDER)));
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
        // Do the work....split the PDF
    	NodeRef result = pdfToolkitService.appendPDF(actionedUponNodeRef, action.getParameterValues());
    	action.setParameterValue(PARAM_RESULT, result);
    }
}
