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
import org.alfresco.extension.pdftoolkit.repo.action.executer.PDFSignatureActionExecuter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.web.app.Application;
import org.alfresco.web.bean.wizard.IWizardBean;


public class PDFSignatureActionHandler
    extends BasePDFStampActionHandler
{

    private static final long     serialVersionUID      = 4104223319038540508L;

    protected static final String PROP_PRIVATE_KEY      = "PrivateKey";
    protected static final String PROP_KEY_TYPE         = "KeyType";
    protected static final String PROP_VISIBILITY       = "SignatureVisibility";
    protected static final String PROP_LOCATION         = "Location";
    protected static final String PROP_REASON           = "Reason";
    protected static final String PROP_KEY_PASSWORD     = "KeyPassword";
    protected static final String PROP_WIDTH            = "Width";
    protected static final String PROP_HEIGHT           = "Height";
    protected static final String PROP_OPTIONS_VISIBLE  = "VisibilityOptions";
    protected static final String PROP_OPTIONS_KEY_TYPE = "KeyTypeOptions";

    protected static final String PROP_ALIAS            = "Alias";
    protected static final String PROP_STORE_PASSWORD   = "StorePassword";


    public String getJSPPath()
    {
        return getJSPPath(PDFSignatureActionExecuter.NAME);
    }


    public void prepareForSave(Map<String, Serializable> actionProps, Map<String, Serializable> repoProps)
    {
        super.prepareForSave(actionProps, repoProps);

        // add the destination space id to the action properties
        NodeRef destNodeRef = (NodeRef)actionProps.get(PROP_DESTINATION);
        repoProps.put(PDFSignatureActionExecuter.PARAM_DESTINATION_FOLDER, destNodeRef);

        // add private key
        NodeRef signatureFile = (NodeRef)actionProps.get(PROP_PRIVATE_KEY);
        repoProps.put(PDFSignatureActionExecuter.PARAM_PRIVATE_KEY, signatureFile);

        String keyType = (String)actionProps.get(PROP_KEY_TYPE);
        repoProps.put(PDFSignatureActionExecuter.PARAM_KEY_TYPE, keyType);

        // add visibility, location, reason, password, location_x, location_y,
        // height and width
        String visibility = (String)actionProps.get(PROP_VISIBILITY);
        repoProps.put(PDFSignatureActionExecuter.PARAM_VISIBILITY, visibility);

        String location = (String)actionProps.get(PROP_LOCATION);
        repoProps.put(PDFSignatureActionExecuter.PARAM_LOCATION, location);

        String reason = (String)actionProps.get(PROP_REASON);
        repoProps.put(PDFSignatureActionExecuter.PARAM_REASON, reason);

        String keyPassword = (String)actionProps.get(PROP_KEY_PASSWORD);
        repoProps.put(PDFSignatureActionExecuter.PARAM_KEY_PASSWORD, keyPassword);

        String height = (String)actionProps.get(PROP_HEIGHT);
        repoProps.put(PDFSignatureActionExecuter.PARAM_HEIGHT, height);

        String width = (String)actionProps.get(PROP_WIDTH);
        repoProps.put(PDFSignatureActionExecuter.PARAM_WIDTH, width);

        // Alias and storePassword added
        String storePassword = (String)actionProps.get(PROP_STORE_PASSWORD);
        repoProps.put(PDFSignatureActionExecuter.PARAM_STORE_PASSWORD, storePassword);

        String alias = (String)actionProps.get(PROP_ALIAS);
        repoProps.put(PDFSignatureActionExecuter.PARAM_ALIAS, alias);
    }


    public void prepareForEdit(Map<String, Serializable> actionProps, Map<String, Serializable> repoProps)
    {

        super.prepareForEdit(actionProps, repoProps);

        // add lists
        actionProps.put(PROP_OPTIONS_VISIBLE, PDFSignatureActionExecuter.visibilityConstraint);
        actionProps.put(PROP_OPTIONS_KEY_TYPE, PDFSignatureActionExecuter.keyTypeConstraint);

        NodeRef destNodeRef = (NodeRef)repoProps.get(PDFSignatureActionExecuter.PARAM_DESTINATION_FOLDER);
        actionProps.put(PROP_DESTINATION, destNodeRef);

        NodeRef certificateFile = (NodeRef)repoProps.get(PDFSignatureActionExecuter.PARAM_PRIVATE_KEY);
        actionProps.put(PROP_PRIVATE_KEY, certificateFile);

        String keyType = (String)repoProps.get(PDFSignatureActionExecuter.PARAM_KEY_TYPE);
        actionProps.put(PROP_KEY_TYPE, keyType);

        // add visibility, location, reason, password, location_x, location_y,
        // height and width
        String visibility = (String)repoProps.get(PDFSignatureActionExecuter.PARAM_VISIBILITY);
        actionProps.put(PROP_VISIBILITY, visibility);

        String location = (String)repoProps.get(PDFSignatureActionExecuter.PARAM_LOCATION);
        actionProps.put(PROP_LOCATION, location);

        String reason = (String)repoProps.get(PDFSignatureActionExecuter.PARAM_REASON);
        actionProps.put(PROP_REASON, reason);

        String keyPassword = (String)repoProps.get(PDFSignatureActionExecuter.PARAM_KEY_PASSWORD);
        actionProps.put(PROP_KEY_PASSWORD, keyPassword);

        String height = (String)repoProps.get(PDFSignatureActionExecuter.PARAM_HEIGHT);
        actionProps.put(PROP_HEIGHT, height);

        String width = (String)repoProps.get(PDFSignatureActionExecuter.PARAM_WIDTH);
        actionProps.put(PROP_WIDTH, width);

        // alias and storePassword
        String alias = (String)repoProps.get(PDFSignatureActionExecuter.PARAM_ALIAS);
        actionProps.put(PROP_ALIAS, alias);

        String storePassword = (String)repoProps.get(PDFSignatureActionExecuter.PARAM_STORE_PASSWORD);
        actionProps.put(PROP_STORE_PASSWORD, storePassword);
    }


    public String generateSummary(FacesContext context, IWizardBean wizard, Map<String, Serializable> actionProps)
    {

        return MessageFormat.format(Application.getMessage(context, "action_pdf_signature"), new Object[] {});
    }


    @Override
    public void setupUIDefaults(Map<String, Serializable> actionProps)
    {

        // add lists
        actionProps.put(PROP_OPTIONS_VISIBLE, PDFSignatureActionExecuter.visibilityConstraint);
        actionProps.put(PROP_OPTIONS_KEY_TYPE, PDFSignatureActionExecuter.keyTypeConstraint);

        // set defaults
        actionProps.put(PROP_VISIBILITY, PDFSignatureActionExecuter.VISIBILITY_HIDDEN);
        actionProps.put(PROP_KEY_TYPE, PDFSignatureActionExecuter.KEY_TYPE_DEFAULT);

        super.setupUIDefaults(actionProps);
    }
}
