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

import org.alfresco.extension.pdftoolkit.service.PDFToolkitService;
import org.alfresco.repo.action.ParameterDefinitionImpl;
import org.alfresco.repo.action.executer.ActionExecuterAbstractBase;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;


public abstract class BasePDFActionExecuter
    extends ActionExecuterAbstractBase
{
	private boolean createNew = true;
    
    protected ServiceRegistry     				serviceRegistry;
    protected PDFToolkitService					pdfToolkitService;
    
    //Default number of map entries at creation 
    protected static final int 					INITIAL_OPTIONS 	= 5;
    public static final String                  PARAM_INPLACE    	= "inplace";

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
     * Sets whether a PDF action creates a new empty node or copies the source node, preserving
     * the content type, applied aspects and properties
     * 
     * @param createNew
     */
    public void setCreateNew(boolean createNew)
    {
    	this.createNew = createNew;
    }
    
    /**
     * Add parameter definitions
     */
    @Override
    protected void addParameterDefinitions(List<ParameterDefinition> paramList)
    {
        paramList.add(new ParameterDefinitionImpl(PARAM_INPLACE, DataTypeDefinition.BOOLEAN, false, getParamDisplayLabel(PARAM_INPLACE), false));
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
