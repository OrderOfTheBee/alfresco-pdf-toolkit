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


import java.io.Serializable;
import java.util.List;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.action.ParameterDefinitionImpl;
import org.alfresco.repo.action.executer.ActionExecuterAbstractBase;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.model.FileNotFoundException;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;


public abstract class BasePDFActionExecuter
    extends ActionExecuterAbstractBase
{
	private boolean createNew = true;
	
    protected static final String 				FILE_EXTENSION 		= ".pdf";
    protected static final String 				FILE_MIMETYPE  		= "application/pdf";
    protected ServiceRegistry     				serviceRegistry;
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
    protected ContentReader getReader(NodeRef nodeRef)
    {
        // First check that the node is a sub-type of content
        QName typeQName = serviceRegistry.getNodeService().getType(nodeRef);
        if (serviceRegistry.getDictionaryService().isSubClass(typeQName, ContentModel.TYPE_CONTENT) == false)
        {
            // it is not content, so can't transform
            return null;
        }

        // Get the content reader
        ContentReader contentReader = serviceRegistry.getContentService().getReader(nodeRef, ContentModel.PROP_CONTENT);

        return contentReader;
    }

    /**
     * @param ruleAction
     * @param filename
     * @return
     */
    protected NodeRef createDestinationNode(String filename, NodeRef destinationParent, NodeRef target)
    {

    	NodeRef destinationNode;
    	
    	if(createNew)
    	{
	    	//create a file in the right location
	        FileInfo fileInfo = serviceRegistry.getFileFolderService().create(destinationParent, filename, ContentModel.TYPE_CONTENT);
	        destinationNode = fileInfo.getNodeRef();
    	}
    	else
    	{
    		try 
    		{
	    		FileInfo fileInfo = serviceRegistry.getFileFolderService().copy(target, destinationParent, filename);
	    		destinationNode = fileInfo.getNodeRef();
    		}
    		catch(FileNotFoundException fnf)
    		{
    			throw new AlfrescoRuntimeException(fnf.getMessage(), fnf);
    		}
    	}

        return destinationNode;
    }
    
    protected int getInteger(Serializable val)
    {
    	if(val == null)
    	{ 
    		return 0;
    	}
    	try
    	{
    		return Integer.parseInt(val.toString());
    	}
    	catch(NumberFormatException nfe)
    	{
    		return 0;
    	}
    }
}
