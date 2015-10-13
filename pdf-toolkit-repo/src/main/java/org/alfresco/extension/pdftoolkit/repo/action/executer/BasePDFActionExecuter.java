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


import java.util.List;

import org.alfresco.extension.pdftoolkit.constants.PDFToolkitConstants;
import org.alfresco.extension.pdftoolkit.service.PDFToolkitService;
import org.alfresco.repo.action.ParameterDefinitionImpl;
import org.alfresco.repo.action.executer.ActionExecuterAbstractBase;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;


public abstract class BasePDFActionExecuter
    extends ActionExecuterAbstractBase
{
    
    protected ServiceRegistry     				serviceRegistry;
    protected PDFToolkitService					pdfToolkitService;
    
    //Default number of map entries at creation 
    protected static final int 					INITIAL_OPTIONS 	= 5;
    

    /**
     * Set a service registry to use, this will do away with all of the
     * individual service registrations
     * 
     * @param serviceRegistry
     */
    public void setServiceRegistry(ServiceRegistry serviceRegistry)
    {
        this.serviceRegistry = serviceRegistry;
    }
    
    /**
     * Add parameter definitions
     */
    @Override
    protected void addParameterDefinitions(List<ParameterDefinition> paramList)
    {
        paramList.add(new ParameterDefinitionImpl(PDFToolkitConstants.PARAM_INPLACE, DataTypeDefinition.BOOLEAN, false, getParamDisplayLabel(PDFToolkitConstants.PARAM_INPLACE), false));
        paramList.add(new ParameterDefinitionImpl(PDFToolkitConstants.PARAM_CREATE_NEW, DataTypeDefinition.BOOLEAN, false, getParamDisplayLabel(PDFToolkitConstants.PARAM_CREATE_NEW), false));
    }
    /**
     * @param actionedUponNodeRef
     * @return
     */
    
    public void setPDFToolkitService(PDFToolkitService pdfToolkitService)
    {
    	this.pdfToolkitService = pdfToolkitService;
    }
}
