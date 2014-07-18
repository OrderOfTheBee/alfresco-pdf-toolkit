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

package org.alfresco.extension.pdftoolkit.service;


import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.extension.pdftoolkit.repo.action.executer.PDFAppendActionExecuter;
import org.alfresco.extension.pdftoolkit.repo.action.executer.PDFEncryptionActionExecuter;
import org.alfresco.extension.pdftoolkit.repo.action.executer.PDFInsertAtPageActionExecuter;
import org.alfresco.extension.pdftoolkit.repo.action.executer.PDFSignatureActionExecuter;
import org.alfresco.extension.pdftoolkit.repo.action.executer.PDFSplitActionExecuter;
import org.alfresco.extension.pdftoolkit.repo.action.executer.PDFSplitAtPageActionExecuter;
import org.alfresco.extension.pdftoolkit.repo.action.executer.PDFWatermarkActionExecuter;
import org.alfresco.repo.processor.BaseProcessorExtension;
import org.alfresco.service.ServiceException;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ActionService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mozilla.javascript.NativeObject;


public class PDFToolkitService extends BaseProcessorExtension
{

	private ServiceRegistry serviceRegistry;
	private static final Log logger = LogFactory.getLog(PDFToolkitService.class);
	
	private String PARAM_TARGET = "target";
	
	public void setServiceRegistry(ServiceRegistry serviceRegistry)
	{
		this.serviceRegistry = serviceRegistry;
	}
	
    public PDFToolkitService()
    {
    }


    public void jsConstructor()
    {
    }


    public String getClassName()
    {
        return "PDFToolkitService";
    }


    /**
     * Wrapper for the encrypt PDF action. This calls the PDFEncryptionActionExecuter
     * 
     * When used in a JS context, this code expects a JSON object to with the following structure:
     * 
     * 	{
     * 		target : "workspace:SpacesStore://node-uuid",
     * 		destination-folder : "workspace:SpacesStore://node-uuid",
     * 		user-password : "password",
     *  	owner-password : "password",
     *  	allow-print : true,
     *  	allow-copy : true,
     *  	allow-content-modification : true,
     *  	allow-annotation-modification : true,
     *  	allow-form-fill : true,
     *  	allow-screen-reader : true,
     *  	allow-degraded-print : true,
     *  	allow-assembly : true,
     *  	encryption-level : "0",
     *  	exclude-metadata : true
     * 	}
     * 
     * For the available options for encryption-level, look at the constraint pdfc-encryptionlevel 
     * in module-context.xml
     */
    public void encryptPDF(NativeObject obj)
    {

    	Map<String, Serializable> params = buildParamMap(obj);
    	NodeRef toEncrypt = getActionTargetNode(params);
    	this.executePDFAction(PDFEncryptionActionExecuter.NAME, params, toEncrypt);
    }

    /**
     * Wrapper for the sign PDF action. This calls the PDFSignatureActionExecuter
     * 
     * When used in a JS context, this code expects a JSON object to with the following structure:
     * 
     * 	{
     * 		target : "workspace:SpacesStore://node-uuid",
     * 		destination-folder : "workspace:SpacesStore://node-uuid",
     * 		private-key : "workspace:SpacesStore://node-uuid",
     * 		location : "location",
     *  	reason : "reason",
     *  	key-password : "keypassword",
     *  	width : "200",
     *  	height : "50",
     *  	key-type : "default",
     *  	alias : "alias",
     *  	store-password : "storepassword",
     *  	visibility : "visible",
     *  	position : "center",
     *  	location-x : "50",
     *  	location-y : "50"
     * 	}
     * 
     * For the available options for visibility, look at the constraint pdfc-visibility
     * in module-context.xml
     * 
     * For the available options for key-type, look at the constraint pdfc-keytype
     * in module-context.xml
     * 
     * For the available options for position, look at the constraint pdfc-position
     * in module-context.xml
     */
    public void signPDF(NativeObject obj)
    {
    	Map<String, Serializable> params = buildParamMap(obj);
    	
    	//check and make sure we have a valid ref for the private key
    	NodeRef key = getDependentNode(params, PDFSignatureActionExecuter.PARAM_PRIVATE_KEY);
    	params.put(PDFSignatureActionExecuter.PARAM_PRIVATE_KEY, key);

    	NodeRef toSign = getActionTargetNode(params);
    	this.executePDFAction(PDFSignatureActionExecuter.NAME, params, toSign);
    }

    /**
     * Wrapper for the watermark PDF action. This calls the PDFWatermarkActionExecuter
     * 
     * When used in a JS context, this code expects a JSON object to with the following structure:
     * 
     * 	{
     * 		target : "workspace:SpacesStore://node-uuid",
     * 		destination-folder : "workspace:SpacesStore://node-uuid",
     * 		watermark-image : "workspace:SpacesStore://node-uuid",
     * 		position : "center",
     *  	location-x : "50",
     *  	location-y : "50",
     *  	watermark-type : "image",
     *  	watermark-pages : "all",
     *  	watermark-depth : "under",
     *  	watermark-text : "Text to use as watermark",
     *  	watermark-font : "Courier",
     *  	watermark-size : "18"
     * 	}
     * 
     * For the available options for position, look at the constraint pdfc-position
     * in module-context.xml
     * 
     * For the available options for watermark-type, look at the constraint pdfc-watermarktype
     * in module-context.xml
     * 
     * For the available options for watermark-pages, look at the constraint pdfc-page
     * in module-context.xml
     * 
     * For the available options for watermark-depth, look at the constraint pdfc-depth
     * in module-context.xml
     * 
     * For the available options for watermark-font, look at the constraint pdfc-font
     * in module-context.xml
     */
    public void watermarkPDF(NativeObject obj)
    {
    	Map<String, Serializable> params = buildParamMap(obj);
    	NodeRef toWatermark = getActionTargetNode(params);
    	
    	//if this is an image watermark, verify that the node exists and add it to the
    	//params as a noderef instead of a string
    	if(params.get(PDFWatermarkActionExecuter.PARAM_WATERMARK_TYPE)
    			.toString().equalsIgnoreCase(PDFWatermarkActionExecuter.TYPE_IMAGE))
    	{
    		NodeRef image = getDependentNode(params, PDFWatermarkActionExecuter.PARAM_WATERMARK_IMAGE);
    		params.put(PDFWatermarkActionExecuter.PARAM_WATERMARK_IMAGE, image);
    	}
    	
    	this.executePDFAction(PDFWatermarkActionExecuter.NAME, params, toWatermark);
    }

    /**
     * Wrapper for the split PDF action. This calls the PDFSplitActionExecuter
     * 
     * When used in a JS context, this code expects a JSON object to with the following structure:
     * 
     * 	{
     * 		target : "workspace:SpacesStore://node-uuid",
     * 		destination-folder : "workspace:SpacesStore://node-uuid",
     * 		split-frequency : "1"
     * 	}
     * 
     */
    public void splitPDF(NativeObject obj)
    {
    	Map<String, Serializable> params = buildParamMap(obj);
    	NodeRef toSplit = getActionTargetNode(params);
    	this.executePDFAction(PDFSplitActionExecuter.NAME, params, toSplit);
    }

    /**
     * Wrapper for the split at page PDF action. This calls the PDFSplitAtPageActionExecuter
     * 
     * When used in a JS context, this code expects a JSON object to with the following structure:
     * 
     * 	{
     * 		target : "workspace:SpacesStore://node-uuid",
     * 		destination-folder : "workspace:SpacesStore://node-uuid",
     * 		split-at-page : "1"
     * 	}
     * 
     */
    public void splitPDFAtPage(NativeObject obj)
    {
    	Map<String, Serializable> params = buildParamMap(obj);
    	NodeRef toSplit = getActionTargetNode(params);
    	this.executePDFAction(PDFSplitAtPageActionExecuter.NAME, params, toSplit);
    }
    
    /**
     * Wrapper for the append PDF action. This calls the PDFAppendActionExecuter
     * 
     * When used in a JS context, this code expects a JSON object to with the following structure:
     * 
     * 	{
     * 		target : "workspace:SpacesStore://node-uuid",
     * 		destination-folder : "workspace:SpacesStore://node-uuid",
     * 		append-content : "workspace:SpacesStore://node-uuid",
     * 		destination-name : "new_file_name.pdf"
     * 	}
     * 
     */
    public void appendPDF(NativeObject obj)
    {
    	Map<String, Serializable> params = buildParamMap(obj);
    	NodeRef appendTo = getActionTargetNode(params);
    	
    	//check and make sure we have a valid ref for the pdf to append
    	NodeRef toAppend = getDependentNode(params, "append-content");
    	params.put("append-content", toAppend);
    	
    	this.executePDFAction(PDFAppendActionExecuter.NAME, params, appendTo);
    }

    /**
     * Wrapper for the insert PDF action. This calls the PDFInsertAtPageActionExecuter
     * 
     * When used in a JS context, this code expects a JSON object to with the following structure:
     * 
     * 	{
     * 		target : "workspace:SpacesStore://node-uuid",
     * 		destination-folder : "workspace:SpacesStore://node-uuid",
     * 		insert-content : "workspace:SpacesStore://node-uuid",
     * 		destination-name : "new_file_name.pdf",
     * 		insert-at-page : "1"
     * 	}
     * 
     */
    public void insertPDF(NativeObject obj)
    {
    	Map<String, Serializable> params = buildParamMap(obj);
    	NodeRef insertInto = getActionTargetNode(params);
    	
    	//check and make sure we have a valid ref for the pdf to insert
    	NodeRef toInsert= getDependentNode(params, PDFInsertAtPageActionExecuter.PARAM_INSERT_CONTENT);
    	params.put(PDFInsertAtPageActionExecuter.PARAM_INSERT_CONTENT, toInsert);
    	
    	this.executePDFAction(PDFInsertAtPageActionExecuter.NAME, params, insertInto);
    }
    
    /**
     * Executes a specific PDF action called by the service
     * 
     * @param name
     * @param params
     * @param actioned
     */
    private void executePDFAction(String name, Map<String, Serializable> params, NodeRef actioned)
    {
    	ActionService actionService = serviceRegistry.getActionService();
    	Action toExecute = actionService.createAction(name, params);
    	actionService.executeAction(toExecute, actioned);
    }
    
    /**
     * Finds a named String parameter and converts it to a NodeRef
     * 
     * @param params
     * @param name
     * @return
     */
    private NodeRef getDependentNode(Map<String, Serializable> params, String name)
    {
    	NodeService nodeService = serviceRegistry.getNodeService();
    	
    	//grab the target node and make sure it exists
    	if(params.get(name) == null)
    	{
    		throw new ServiceException("Object property " + name + " must be provided");
    	}
    	String nodeString = params.get(name).toString();

    	
    	NodeRef dep = new NodeRef(nodeString);
    	if(!nodeService.exists(dep))
    	{
    		throw new ServiceException("Object property " + name + " must be a valid node reference");
    	}
    	
    	return dep;
    }
    
    /**
     * Get a NodeRef to the target node, defined by the "node" property of the Javascript object
     * passed to the service
     * 
     * @param params
     * @return
     */
    private NodeRef getActionTargetNode(Map<String, Serializable> params)
    {
    	
    	return getDependentNode(params, PARAM_TARGET);
    }
    
    /**
     * Build a proper parameters map suitable for passing to the ActionService
     * 
     * @param obj
     * @return
     */
    private Map<String, Serializable> buildParamMap(NativeObject obj)
    {
    	Map<String, Serializable> params = nativeObjectToMap(obj);
    	
    	NodeRef destination = getDependentNode(params, PDFEncryptionActionExecuter.PARAM_DESTINATION_FOLDER);
    	
    	//add the noderef back to the param map
    	params.put(PDFEncryptionActionExecuter.PARAM_DESTINATION_FOLDER, destination);
    	
    	return params;
    }
    
    /**
     * Can't cast to Map, as Alfresco's Rhino version is WAY out of date and 
     * NativeObject doesn't implement Map.  So, we do this instead.
     * 
     * @param obj
     */
    private Map<String, Serializable> nativeObjectToMap(NativeObject obj)
    {
    	Map<String, Serializable> map = new HashMap<String, Serializable>();
    	Object[] keys = obj.getAllIds();
    	for(Object key : keys)
    	{
    		Object value = NativeObject.getProperty(obj, key.toString());
    		map.put(key.toString(), (Serializable)value);
    	}
    	return map;
    }
}
