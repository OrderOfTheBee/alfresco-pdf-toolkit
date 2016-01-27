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


public class PDFWatermarkActionExecuter extends BasePDFStampActionExecuter
{
    /**
     * The logger
     */
    private static Log                    logger                   = LogFactory.getLog(PDFWatermarkActionExecuter.class);

    public static HashMap<String, String> fontConstraint           = new HashMap<String, String>();
    public static HashMap<String, String> depthConstraint          = new HashMap<String, String>();
    public static HashMap<String, String> typeConstraint           = new HashMap<String, String>();
    public static HashMap<String, String> fontSizeConstraint       = new HashMap<String, String>();

    /**
     * Action constants
     */
    public static final String            NAME                     = "pdf-watermark";

    /**
     * setters for constraint beans
     * 
     * @param fontConstraint
     */
    public void setFontConstraint(MapConstraint mc)
    {
        fontConstraint.putAll(mc.getAllowableValues());
    }

    public void setDepthConstraint(MapConstraint mc)
    {
        depthConstraint.putAll(mc.getAllowableValues());
    }

    public void setTypeConstraint(MapConstraint mc)
    {
        typeConstraint.putAll(mc.getAllowableValues());
    }

    public void setFontSizeConstraint(MapConstraint mc)
    {
        fontSizeConstraint.putAll(mc.getAllowableValues());
    }

    /**
     * Add parameter definitions
     */
    @Override
    protected void addParameterDefinitions(List<ParameterDefinition> paramList)
    {
        paramList.add(new ParameterDefinitionImpl(PDFToolkitConstants.PARAM_DESTINATION_FOLDER, DataTypeDefinition.NODE_REF, false, getParamDisplayLabel(PDFToolkitConstants.PARAM_DESTINATION_FOLDER)));
        paramList.add(new ParameterDefinitionImpl(PDFToolkitConstants.PARAM_WATERMARK_IMAGE, DataTypeDefinition.NODE_REF, false, getParamDisplayLabel(PDFToolkitConstants.PARAM_WATERMARK_IMAGE)));
        paramList.add(new ParameterDefinitionImpl(PDFToolkitConstants.PARAM_WATERMARK_DEPTH, DataTypeDefinition.TEXT, true, getParamDisplayLabel(PDFToolkitConstants.PARAM_WATERMARK_DEPTH), false, "pdfc-depth"));
        paramList.add(new ParameterDefinitionImpl(PDFToolkitConstants.PARAM_WATERMARK_TEXT, DataTypeDefinition.TEXT, false, getParamDisplayLabel(PDFToolkitConstants.PARAM_WATERMARK_TEXT)));
        paramList.add(new ParameterDefinitionImpl(PDFToolkitConstants.PARAM_WATERMARK_FONT, DataTypeDefinition.TEXT, false, getParamDisplayLabel(PDFToolkitConstants.PARAM_WATERMARK_FONT), false, "pdfc-font"));
        paramList.add(new ParameterDefinitionImpl(PDFToolkitConstants.PARAM_WATERMARK_SIZE, DataTypeDefinition.TEXT, false, getParamDisplayLabel(PDFToolkitConstants.PARAM_WATERMARK_SIZE), false, "pdfc-fontsize"));
        paramList.add(new ParameterDefinitionImpl(PDFToolkitConstants.PARAM_WATERMARK_TYPE, DataTypeDefinition.TEXT, true, getParamDisplayLabel(PDFToolkitConstants.PARAM_WATERMARK_TYPE), false, "pdfc-watermarktype"));
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
    	NodeRef result = pdfToolkitService.watermarkPDF(actionedUponNodeRef, action.getParameterValues());
    	action.setParameterValue(PARAM_RESULT, result);
    }
}
