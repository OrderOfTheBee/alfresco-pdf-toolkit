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

import org.alfresco.extension.pdftoolkit.repo.action.executer.PDFSplitAtPageActionExecuter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.web.app.Application;
import org.alfresco.web.bean.repository.Repository;
import org.alfresco.web.bean.wizard.IWizardBean;
import org.alfresco.web.bean.actions.handlers.BaseActionHandler;


/**
 * Action handler for the "pdf-split-at-page" action.
 * 
 * @author Jared Ottley
 */

public class PDFSplitAtPageActionHandler
    extends BaseActionHandler
{
    private static final long     serialVersionUID       = -8277551254101165061L;

    protected static final String PROP_PDF_SPLIT_AT_PAGE = "PDFSplitAtPage";


    public String getJSPPath()
    {
        return getJSPPath(PDFSplitAtPageActionExecuter.NAME);
    }


    public void prepareForSave(Map<String, Serializable> actionProps, Map<String, Serializable> repoProps)
    {
        // add the destination space id to the action properties
        NodeRef destNodeRef = (NodeRef)actionProps.get(PROP_DESTINATION);
        repoProps.put(PDFSplitAtPageActionExecuter.PARAM_DESTINATION_FOLDER, destNodeRef);

        // add frequency of split to the action properties
        String splitAtNodeName = actionProps.get(PROP_PDF_SPLIT_AT_PAGE).toString();
        repoProps.put(PDFSplitAtPageActionExecuter.PARAM_SPLIT_AT_PAGE, splitAtNodeName);
    }


    public void prepareForEdit(Map<String, Serializable> actionProps, Map<String, Serializable> repoProps)
    {
        NodeRef destNodeRef = (NodeRef)repoProps.get(PDFSplitAtPageActionExecuter.PARAM_DESTINATION_FOLDER);
        actionProps.put(PROP_DESTINATION, destNodeRef);

        String newNodeName = actionProps.get(PROP_PDF_SPLIT_AT_PAGE).toString();
        repoProps.put(PDFSplitAtPageActionExecuter.PARAM_SPLIT_AT_PAGE, newNodeName);
    }


    public String generateSummary(FacesContext context, IWizardBean wizard, Map<String, Serializable> actionProps)
    {
        NodeRef space = (NodeRef)actionProps.get(PROP_DESTINATION);
        String name = Repository.getNameForNode(Repository.getServiceRegistry(context).getNodeService(), space);

        String splitAtNodeName = actionProps.get(PROP_PDF_SPLIT_AT_PAGE).toString();

        return MessageFormat.format(Application.getMessage(context, "action_pdf_split_at_page"), new Object[] { splitAtNodeName,
                name });
    }
}
