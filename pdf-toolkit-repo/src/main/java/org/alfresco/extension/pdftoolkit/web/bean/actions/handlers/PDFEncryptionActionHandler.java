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
import java.util.HashMap;
import java.util.Map;

import javax.faces.context.FacesContext;

import org.alfresco.extension.pdftoolkit.repo.action.executer.PDFEncryptionActionExecuter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.web.app.Application;
import org.alfresco.web.bean.actions.handlers.BaseActionHandler;
import org.alfresco.web.bean.repository.Repository;
import org.alfresco.web.bean.wizard.IWizardBean;


public class PDFEncryptionActionHandler
    extends BaseActionHandler

{

    private static final long                      serialVersionUID                   = -6322014561543170858L;

    protected static final HashMap<String, String> OPTIONS_LEVEL                      = new HashMap<String, String>();

    protected static final String                  PROP_USER_PASSWORD                 = "UserPassword";
    protected static final String                  PROP_OWNER_PASSWORD                = "OwnerPassword";
    protected static final String                  PROP_ALLOW_PRINT                   = "AllowPrint";
    protected static final String                  PROP_ALLOW_COPY                    = "AllowCopy";
    protected static final String                  PROP_ALLOW_CONTENT_MODIFICATION    = "AllowContentModification";
    protected static final String                  PROP_ALLOW_ANNOTATION_MODIFICATION = "AllowAnnotationModification";
    protected static final String                  PROP_ALLOW_FORM_FILL               = "AllowFormFill";
    protected static final String                  PROP_ALLOW_SCREEN_READER           = "AllowScreenReader";
    protected static final String                  PROP_ALLOW_DEGRADED_PRINT          = "AllowDegradedPrint";
    protected static final String                  PROP_ALLOW_ASSEMBLY                = "AllowAssembly";
    protected static final String                  PROP_ENCRYPTION_LEVEL              = "EncryptionLevel";
    protected static final String                  PROP_EXCLUDE_METADATA              = "ExcludeMetadata";
    protected static final String                  PROP_OPTIONS_LEVEL                 = "LevelOptions";


    public String getJSPPath()
    {
        return getJSPPath(PDFEncryptionActionExecuter.NAME);
    }


    public void prepareForSave(Map<String, Serializable> actionProps, Map<String, Serializable> repoProps)
    {
        // add the destination space id to the action properties
        NodeRef destNodeRef = (NodeRef)actionProps.get(PROP_DESTINATION);
        repoProps.put(PDFEncryptionActionExecuter.PARAM_DESTINATION_FOLDER, destNodeRef);

        // add repo properties
        repoProps.put(PDFEncryptionActionExecuter.PARAM_USER_PASSWORD, (String)actionProps.get(PROP_USER_PASSWORD));
        repoProps.put(PDFEncryptionActionExecuter.PARAM_OWNER_PASSWORD, (String)actionProps.get(PROP_OWNER_PASSWORD));
        repoProps.put(PDFEncryptionActionExecuter.PARAM_ALLOW_PRINT, (Boolean)actionProps.get(PROP_ALLOW_PRINT));
        repoProps.put(PDFEncryptionActionExecuter.PARAM_ALLOW_COPY, (Boolean)actionProps.get(PROP_ALLOW_COPY));
        repoProps.put(PDFEncryptionActionExecuter.PARAM_ALLOW_CONTENT_MODIFICATION, (Boolean)actionProps.get(PROP_ALLOW_CONTENT_MODIFICATION));
        repoProps.put(PDFEncryptionActionExecuter.PARAM_ALLOW_ANNOTATION_MODIFICATION, (Boolean)actionProps.get(PROP_ALLOW_ANNOTATION_MODIFICATION));
        repoProps.put(PDFEncryptionActionExecuter.PARAM_ALLOW_FORM_FILL, (Boolean)actionProps.get(PROP_ALLOW_FORM_FILL));
        repoProps.put(PDFEncryptionActionExecuter.PARAM_ALLOW_SCREEN_READER, (Boolean)actionProps.get(PROP_ALLOW_SCREEN_READER));
        repoProps.put(PDFEncryptionActionExecuter.PARAM_ALLOW_DEGRADED_PRINT, (Boolean)actionProps.get(PROP_ALLOW_DEGRADED_PRINT));
        repoProps.put(PDFEncryptionActionExecuter.PARAM_ALLOW_ASSEMBLY, (Boolean)actionProps.get(PROP_ALLOW_ASSEMBLY));
        repoProps.put(PDFEncryptionActionExecuter.PARAM_ENCRYPTION_LEVEL, (String)actionProps.get(PROP_ENCRYPTION_LEVEL));
        repoProps.put(PDFEncryptionActionExecuter.PARAM_EXCLUDE_METADATA, (Boolean)actionProps.get(PROP_EXCLUDE_METADATA));

        // repoProps.put(PDFEncryptionActionExecuter.PARAM_S, arg1)

    }


    public void prepareForEdit(Map<String, Serializable> actionProps, Map<String, Serializable> repoProps)
    {
        // call setupUIDefaults first to make sure maps are initialized and
        // populated
        setupUIDefaults(actionProps);
        actionProps.put(PROP_OPTIONS_LEVEL, OPTIONS_LEVEL);

        NodeRef destNodeRef = (NodeRef)repoProps.get(PDFEncryptionActionExecuter.PARAM_DESTINATION_FOLDER);
        actionProps.put(PROP_DESTINATION, destNodeRef);

        // add action properties, preparing for edit
        actionProps.put(PROP_USER_PASSWORD, (String)repoProps.get(PDFEncryptionActionExecuter.PARAM_USER_PASSWORD));
        actionProps.put(PROP_OWNER_PASSWORD, (String)repoProps.get(PDFEncryptionActionExecuter.PARAM_OWNER_PASSWORD));
        actionProps.put(PROP_ALLOW_PRINT, (Boolean)repoProps.get(PDFEncryptionActionExecuter.PARAM_ALLOW_PRINT));
        actionProps.put(PROP_ALLOW_COPY, (Boolean)repoProps.get(PDFEncryptionActionExecuter.PARAM_ALLOW_COPY));
        actionProps.put(PROP_ALLOW_CONTENT_MODIFICATION, (Boolean)repoProps.get(PDFEncryptionActionExecuter.PARAM_ALLOW_CONTENT_MODIFICATION));
        actionProps.put(PROP_ALLOW_ANNOTATION_MODIFICATION, (Boolean)repoProps.get(PDFEncryptionActionExecuter.PARAM_ALLOW_ANNOTATION_MODIFICATION));
        actionProps.put(PROP_ALLOW_FORM_FILL, (Boolean)repoProps.get(PDFEncryptionActionExecuter.PARAM_ALLOW_FORM_FILL));
        actionProps.put(PROP_ALLOW_SCREEN_READER, (Boolean)repoProps.get(PDFEncryptionActionExecuter.PARAM_ALLOW_SCREEN_READER));
        actionProps.put(PROP_ALLOW_DEGRADED_PRINT, (Boolean)repoProps.get(PDFEncryptionActionExecuter.PARAM_ALLOW_DEGRADED_PRINT));
        actionProps.put(PROP_ALLOW_ASSEMBLY, (Boolean)repoProps.get(PDFEncryptionActionExecuter.PARAM_ALLOW_ASSEMBLY));
        actionProps.put(PROP_ENCRYPTION_LEVEL, (String)repoProps.get(PDFEncryptionActionExecuter.PARAM_ENCRYPTION_LEVEL));
        actionProps.put(PROP_EXCLUDE_METADATA, (Boolean)repoProps.get(PDFEncryptionActionExecuter.PARAM_EXCLUDE_METADATA));
    }


    public String generateSummary(FacesContext context, IWizardBean wizard, Map<String, Serializable> actionProps)
    {
        NodeRef space = (NodeRef)actionProps.get(PROP_DESTINATION);
        String name = Repository.getNameForNode(Repository.getServiceRegistry(context).getNodeService(), space);

        return MessageFormat.format(Application.getMessage(context, "action_pdf_encryption"), new Object[] { name });
    }


    @Override
    public void setupUIDefaults(Map<String, Serializable> actionProps)
    {

        // add lists
        actionProps.put(PROP_OPTIONS_LEVEL, PDFEncryptionActionExecuter.encryptionLevelConstraint);

        super.setupUIDefaults(actionProps);
    }
}
