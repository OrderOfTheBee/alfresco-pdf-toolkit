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
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;

public abstract class BasePDFStampActionExecuter extends BasePDFActionExecuter
{

    public static HashMap<String, String> pageConstraint       = new HashMap<String, String>();
    public static HashMap<String, String> positionConstraint   = new HashMap<String, String>();

    /**
     * Constraints
     */
    public void setPositionConstraint(MapConstraint mc)
    {
        positionConstraint.putAll(mc.getAllowableValues());
    }

    public void setPageConstraint(MapConstraint mc)
    {
        pageConstraint.putAll(mc.getAllowableValues());
    }

    /**
     * Add parameter definitions
     */
    @Override
    protected void addParameterDefinitions(List<ParameterDefinition> paramList)
    {
        paramList.add(new ParameterDefinitionImpl(PDFToolkitConstants.PARAM_POSITION, DataTypeDefinition.TEXT, false, getParamDisplayLabel(PDFToolkitConstants.PARAM_POSITION), false, "pdfc-position"));
        paramList.add(new ParameterDefinitionImpl(PDFToolkitConstants.PARAM_LOCATION_X, DataTypeDefinition.TEXT, false, getParamDisplayLabel(PDFToolkitConstants.PARAM_LOCATION_X)));
        paramList.add(new ParameterDefinitionImpl(PDFToolkitConstants.PARAM_LOCATION_Y, DataTypeDefinition.TEXT, false, getParamDisplayLabel(PDFToolkitConstants.PARAM_LOCATION_Y)));
        paramList.add(new ParameterDefinitionImpl(PDFToolkitConstants.PARAM_PAGE, DataTypeDefinition.TEXT, false, getParamDisplayLabel(PDFToolkitConstants.PARAM_PAGE), false));
        
        super.addParameterDefinitions(paramList);
    }
}
