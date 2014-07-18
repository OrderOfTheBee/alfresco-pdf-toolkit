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

package org.alfresco.extension.pdftoolkit.web.bean.actions.handlers;


import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Map;

import javax.faces.context.FacesContext;

import org.alfresco.extension.pdftoolkit.repo.action.executer.PDFAppendActionExecuter;
import org.alfresco.extension.pdftoolkit.repo.action.executer.PDFSplitActionExecuter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.web.app.Application;
import org.alfresco.web.bean.repository.Repository;
import org.alfresco.web.bean.wizard.IWizardBean;
import org.alfresco.web.bean.actions.handlers.BaseActionHandler;


/**
 * Action handler for the "pdf-split" action.
 * 
 * @author Jared Ottley
 */

public class PDFAppendActionHandler
    extends BaseActionHandler
{
    private static final long     serialVersionUID       = 8277555214101165061L;

    protected static final String PROP_PDF_APPEND_TARGET = "PDFAppendTarget";
    protected static final String PROP_PDF_APPEND_NAME   = "PDFAppendName";


    public String getJSPPath()
    {
        return getJSPPath(PDFAppendActionExecuter.NAME);
    }


    public void prepareForSave(Map<String, Serializable> actionProps, Map<String, Serializable> repoProps)
    {
        // add new nodes name to the action properties
        String newNodeName = actionProps.get(PROP_PDF_APPEND_NAME).toString();
        repoProps.put(PDFAppendActionExecuter.PARAM_DESTINATION_NAME, newNodeName);

        // add the target Node id to the action properties
        String[] targetNodes = (String[])actionProps.get(PROP_PDF_APPEND_TARGET);
        NodeRef targetNodeRef = new NodeRef(targetNodes[0]);
        repoProps.put(PDFAppendActionExecuter.PARAM_TARGET_NODE, targetNodeRef);

        // add the destination space id to the action properties
        NodeRef destNodeRef = (NodeRef)actionProps.get(PROP_DESTINATION);
        repoProps.put(PDFAppendActionExecuter.PARAM_DESTINATION_FOLDER, destNodeRef);
    }


    public void prepareForEdit(Map<String, Serializable> actionProps, Map<String, Serializable> repoProps)
    {
        repoProps.put(PDFAppendActionExecuter.PARAM_DESTINATION_NAME, PROP_PDF_APPEND_NAME);

        NodeRef targetNodeRef = (NodeRef)actionProps.get(PROP_PDF_APPEND_TARGET);
        repoProps.put(PDFAppendActionExecuter.PARAM_TARGET_NODE, targetNodeRef);

        NodeRef destNodeRef = (NodeRef)repoProps.get(PDFSplitActionExecuter.PARAM_DESTINATION_FOLDER);
        actionProps.put(PROP_DESTINATION, destNodeRef);
    }


    public String generateSummary(FacesContext context, IWizardBean wizard, Map<String, Serializable> actionProps)
    {
        String[] targetNodes = (String[])actionProps.get(PROP_PDF_APPEND_TARGET);
        NodeRef targetNode = new NodeRef(targetNodes[0]);
        String targetNodeName = Repository.getNameForNode(Repository.getServiceRegistry(context).getNodeService(), targetNode);

        String newNodeName = actionProps.get(PROP_PDF_APPEND_NAME).toString();

        NodeRef space = (NodeRef)actionProps.get(PROP_DESTINATION);
        String spaceName = Repository.getNameForNode(Repository.getServiceRegistry(context).getNodeService(), space);

        return MessageFormat.format(Application.getMessage(context, "action_pdf_append"), new Object[] { targetNodeName,
                newNodeName, spaceName });
    }
}
