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


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.extension.pdftoolkit.constraints.MapConstraint;
import org.alfresco.extension.pdftoolkit.model.PDFToolkitModel;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.action.ParameterDefinitionImpl;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.TempFileProvider;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;


public class PDFEncryptionActionExecuter
    extends BasePDFActionExecuter
{

    /**
     * The logger
     */
    private static Log                    logger                              = LogFactory.getLog(PDFEncryptionActionExecuter.class);

    /**
     * Flag for use of "pdft:encrypted" aspect, enable search by encryption metadata
     */
    private boolean useAspect									   			  = true;
    
    /**
     * Constraints
     */
    public static HashMap<String, String> encryptionLevelConstraint           = new HashMap<String, String>();

    /**
     * Action constants
     */
    public static final String            NAME                                = "pdf-encryption";
    public static final String            PARAM_DESTINATION_FOLDER            = "destination-folder";
    public static final String			  PARAM_DESTINATION_NAME			  = "destination-name";

    /**
     * Encryption constants
     */
    public static final String            PARAM_USER_PASSWORD                 = "user-password";
    public static final String            PARAM_OWNER_PASSWORD                = "owner-password";
    public static final String            PARAM_ALLOW_PRINT                   = "allow-print";
    public static final String            PARAM_ALLOW_COPY                    = "allow-copy";
    public static final String            PARAM_ALLOW_CONTENT_MODIFICATION    = "allow-content-modification";
    public static final String            PARAM_ALLOW_ANNOTATION_MODIFICATION = "allow-annotation-modification";
    public static final String            PARAM_ALLOW_FORM_FILL               = "allow-form-fill";
    public static final String            PARAM_ALLOW_SCREEN_READER           = "allow-screen-reader";
    public static final String            PARAM_ALLOW_DEGRADED_PRINT          = "allow-degraded-print";
    public static final String            PARAM_ALLOW_ASSEMBLY                = "allow-assembly";
    public static final String            PARAM_ENCRYPTION_LEVEL              = "encryption-level";
    public static final String            PARAM_EXCLUDE_METADATA              = "exclude-metadata";
    public static final String            PARAM_OPTIONS_LEVEL                 = "level-options";


    /**
     * Setter for constraint bean
     * 
     * @param encryptionLevelConstraint
     */
    public void setEncryptionLevelConstraint(MapConstraint mc)
    {
        encryptionLevelConstraint.putAll(mc.getAllowableValues());
    }

    public void setUseAspect(boolean useAspect)
    {
    	this.useAspect = useAspect;
    }

    /**
     * Add parameter definitions
     */
    @Override
    protected void addParameterDefinitions(List<ParameterDefinition> paramList)
    {

        paramList.add(new ParameterDefinitionImpl(PARAM_DESTINATION_FOLDER, DataTypeDefinition.NODE_REF, false, getParamDisplayLabel(PARAM_DESTINATION_FOLDER)));
        paramList.add(new ParameterDefinitionImpl(PARAM_USER_PASSWORD, DataTypeDefinition.TEXT, true, getParamDisplayLabel(PARAM_USER_PASSWORD)));
        paramList.add(new ParameterDefinitionImpl(PARAM_OWNER_PASSWORD, DataTypeDefinition.TEXT, true, getParamDisplayLabel(PARAM_OWNER_PASSWORD)));
        paramList.add(new ParameterDefinitionImpl(PARAM_ALLOW_PRINT, DataTypeDefinition.BOOLEAN, true, getParamDisplayLabel(PARAM_ALLOW_PRINT)));
        paramList.add(new ParameterDefinitionImpl(PARAM_ALLOW_COPY, DataTypeDefinition.BOOLEAN, true, getParamDisplayLabel(PARAM_ALLOW_COPY)));
        paramList.add(new ParameterDefinitionImpl(PARAM_ALLOW_CONTENT_MODIFICATION, DataTypeDefinition.BOOLEAN, true, getParamDisplayLabel(PARAM_ALLOW_CONTENT_MODIFICATION)));
        paramList.add(new ParameterDefinitionImpl(PARAM_ALLOW_ANNOTATION_MODIFICATION, DataTypeDefinition.BOOLEAN, true, getParamDisplayLabel(PARAM_ALLOW_ANNOTATION_MODIFICATION)));
        paramList.add(new ParameterDefinitionImpl(PARAM_ALLOW_FORM_FILL, DataTypeDefinition.BOOLEAN, true, getParamDisplayLabel(PARAM_ALLOW_FORM_FILL)));
        paramList.add(new ParameterDefinitionImpl(PARAM_ALLOW_SCREEN_READER, DataTypeDefinition.BOOLEAN, true, getParamDisplayLabel(PARAM_ALLOW_SCREEN_READER)));
        paramList.add(new ParameterDefinitionImpl(PARAM_ALLOW_DEGRADED_PRINT, DataTypeDefinition.BOOLEAN, true, getParamDisplayLabel(PARAM_ALLOW_DEGRADED_PRINT)));
        paramList.add(new ParameterDefinitionImpl(PARAM_ALLOW_ASSEMBLY, DataTypeDefinition.BOOLEAN, true, getParamDisplayLabel(PARAM_ALLOW_ASSEMBLY)));
        paramList.add(new ParameterDefinitionImpl(PARAM_ENCRYPTION_LEVEL, DataTypeDefinition.TEXT, true, getParamDisplayLabel(PARAM_ENCRYPTION_LEVEL), false, "pdfc-encryptionlevel"));
        paramList.add(new ParameterDefinitionImpl(PARAM_EXCLUDE_METADATA, DataTypeDefinition.BOOLEAN, true, getParamDisplayLabel(PARAM_EXCLUDE_METADATA)));
        paramList.add(new ParameterDefinitionImpl(PARAM_DESTINATION_NAME, DataTypeDefinition.TEXT, false, getParamDisplayLabel(PARAM_DESTINATION_NAME)));

        super.addParameterDefinitions(paramList);
    }


    /**
     * @see org.alfresco.repo.action.executer.ActionExecuterAbstractBase#executeImpl(org.alfresco.service.cmr.repository.NodeRef,
     * org.alfresco.service.cmr.repository.NodeRef)
     */
    @Override
    protected void executeImpl(Action ruleAction, NodeRef actionedUponNodeRef)
    {
        if (serviceRegistry.getNodeService().exists(actionedUponNodeRef) == false)
        {
            // node doesn't exist - can't do anything
            return;
        }

        ContentReader actionedUponContentReader = getReader(actionedUponNodeRef);

        if (actionedUponContentReader != null)
        {
            // Encrypt the document with the requested options
            doEncrypt(ruleAction, actionedUponNodeRef, actionedUponContentReader);

            {
                if (logger.isDebugEnabled())
                {
                    logger.debug("Can't execute rule: \n" + "   node: " + actionedUponNodeRef + "\n" + "   reader: "
                                 + actionedUponContentReader + "\n" + "   action: " + this);
                }
            }
        }
    }


    /**
     * @see org.alfresco.repo.action.executer.TransformActionExecuter#doTransform(org.alfresco.service.cmr.action.Action,
     * org.alfresco.service.cmr.repository.ContentReader,
     * org.alfresco.service.cmr.repository.ContentWriter)
     */
    protected void doEncrypt(Action ruleAction, NodeRef actionedUponNodeRef, ContentReader actionedUponContentReader)
    {

        Map<String, Object> options = new HashMap<String, Object>(INITIAL_OPTIONS);

        options.put(PARAM_DESTINATION_FOLDER, ruleAction.getParameterValue(PARAM_DESTINATION_FOLDER));
        options.put(PARAM_USER_PASSWORD, ruleAction.getParameterValue(PARAM_USER_PASSWORD));
        options.put(PARAM_OWNER_PASSWORD, ruleAction.getParameterValue(PARAM_OWNER_PASSWORD));
        options.put(PARAM_ALLOW_PRINT, ruleAction.getParameterValue(PARAM_ALLOW_PRINT));
        options.put(PARAM_ALLOW_COPY, ruleAction.getParameterValue(PARAM_ALLOW_COPY));
        options.put(PARAM_ALLOW_CONTENT_MODIFICATION, ruleAction.getParameterValue(PARAM_ALLOW_CONTENT_MODIFICATION));
        options.put(PARAM_ALLOW_ANNOTATION_MODIFICATION, ruleAction.getParameterValue(PARAM_ALLOW_ANNOTATION_MODIFICATION));
        options.put(PARAM_ALLOW_FORM_FILL, ruleAction.getParameterValue(PARAM_ALLOW_FORM_FILL));
        options.put(PARAM_ALLOW_SCREEN_READER, ruleAction.getParameterValue(PARAM_ALLOW_SCREEN_READER));
        options.put(PARAM_ALLOW_DEGRADED_PRINT, ruleAction.getParameterValue(PARAM_ALLOW_DEGRADED_PRINT));
        options.put(PARAM_ALLOW_ASSEMBLY, ruleAction.getParameterValue(PARAM_ALLOW_ASSEMBLY));
        options.put(PARAM_ENCRYPTION_LEVEL, ruleAction.getParameterValue(PARAM_ENCRYPTION_LEVEL));
        options.put(PARAM_EXCLUDE_METADATA, ruleAction.getParameterValue(PARAM_EXCLUDE_METADATA));
        options.put(PARAM_OPTIONS_LEVEL, ruleAction.getParameterValue(PARAM_OPTIONS_LEVEL));
        options.put(PARAM_INPLACE, ruleAction.getParameterValue(PARAM_INPLACE));
        options.put(PARAM_DESTINATION_FOLDER, ruleAction.getParameterValue(PARAM_DESTINATION_FOLDER));

        try
        {
            this.action(ruleAction, actionedUponNodeRef, actionedUponContentReader, options);
        }
        catch (AlfrescoRuntimeException e)
        {
            throw new AlfrescoRuntimeException(e.getMessage(), e);
        }
    }


    /**
     * @param reader
     * @param writer
     * @param options
     * @throws Exception
     */
    protected final void action(Action ruleAction, NodeRef actionedUponNodeRef, ContentReader actionedUponContentReader,
            Map<String, Object> options)
    {

        PdfStamper stamp = null;
        File tempDir = null;
        ContentWriter writer = null;
        NodeService ns = serviceRegistry.getNodeService();
        
        try
        {
            // get the parameters
            String userPassword = (String)options.get(PARAM_USER_PASSWORD);
            String ownerPassword = (String)options.get(PARAM_OWNER_PASSWORD);
            Boolean inplace = Boolean.valueOf(String.valueOf(options.get(PARAM_INPLACE)));
            int permissions = buildPermissionMask(options);
            int encryptionType = Integer.parseInt((String)options.get(PARAM_ENCRYPTION_LEVEL));

            // if metadata is excluded, alter encryption type
            if ((Boolean)options.get(PARAM_EXCLUDE_METADATA))
            {
                encryptionType = encryptionType | PdfWriter.DO_NOT_ENCRYPT_METADATA;
            }

            // get temp file
            File alfTempDir = TempFileProvider.getTempDir();
            tempDir = new File(alfTempDir.getPath() + File.separatorChar + actionedUponNodeRef.getId());
            tempDir.mkdir();
            File file = new File(tempDir, serviceRegistry.getFileFolderService().getFileInfo(actionedUponNodeRef).getName());

            // get the PDF input stream and create a reader for iText
            PdfReader reader = new PdfReader(actionedUponContentReader.getContentInputStream());
            stamp = new PdfStamper(reader, new FileOutputStream(file));

            // encrypt PDF
            stamp.setEncryption(userPassword.getBytes(Charset.forName("UTF-8")), ownerPassword.getBytes(Charset.forName("UTF-8")), permissions, encryptionType);
            stamp.close();

            Serializable providedName = ruleAction.getParameterValue(PARAM_DESTINATION_NAME);
            String fileName = null;
            if(providedName != null)
            {
            	fileName = String.valueOf(providedName) + FILE_EXTENSION;
            }
            else
            {
            	fileName = String.valueOf(ns.getProperty(actionedUponNodeRef, ContentModel.PROP_NAME));
            }
            
            // write out to destination
            NodeRef destinationNode = createDestinationNode(fileName, 
            		(NodeRef)ruleAction.getParameterValue(PARAM_DESTINATION_FOLDER), actionedUponNodeRef, inplace);
            writer = serviceRegistry.getContentService().getWriter(destinationNode, ContentModel.PROP_CONTENT, true);

            writer.setEncoding(actionedUponContentReader.getEncoding());
            writer.setMimetype(FILE_MIMETYPE);
            writer.putContent(file);
            file.delete();
            
            //if useAspect is true, store some additional info about the signature in the props
            if(useAspect)
            {
            	serviceRegistry.getNodeService().addAspect(destinationNode, PDFToolkitModel.ASPECT_ENCRYPTED, new HashMap<QName, Serializable>());
            	serviceRegistry.getNodeService().setProperty(destinationNode, PDFToolkitModel.PROP_ENCRYPTIONDATE, new java.util.Date());
            	serviceRegistry.getNodeService().setProperty(destinationNode, PDFToolkitModel.PROP_ENCRYPTEDBY, AuthenticationUtil.getRunAsUser());
            }
            
        }
        catch (IOException e)
        {
            throw new AlfrescoRuntimeException(e.getMessage(), e);
        }
        catch (DocumentException e)
        {
            throw new AlfrescoRuntimeException(e.getMessage(), e);
        }
        finally
        {
            if (tempDir != null)
            {
                try
                {
                    tempDir.delete();
                }
                catch (Exception ex)
                {
                    throw new AlfrescoRuntimeException(ex.getMessage(), ex);
                }
            }

            if (stamp != null)
            {
                try
                {
                    stamp.close();
                }
                catch (Exception ex)
                {
                    throw new AlfrescoRuntimeException(ex.getMessage(), ex);
                }
            }
        }
    }


    /**
     * Build the permissions mask for iText
     * 
     * @param options
     * @return
     */
    private int buildPermissionMask(Map<String, Object> options)
    {
        int permissions = 0;

        if ((Boolean)options.get(PARAM_ALLOW_PRINT))
        {
            permissions = permissions | PdfWriter.ALLOW_PRINTING;
        }
        if ((Boolean)options.get(PARAM_ALLOW_COPY))
        {
            permissions = permissions | PdfWriter.ALLOW_COPY;
        }
        if ((Boolean)options.get(PARAM_ALLOW_CONTENT_MODIFICATION))
        {
            permissions = permissions | PdfWriter.ALLOW_MODIFY_CONTENTS;
        }
        if ((Boolean)options.get(PARAM_ALLOW_ANNOTATION_MODIFICATION))
        {
            permissions = permissions | PdfWriter.ALLOW_MODIFY_ANNOTATIONS;
        }
        if ((Boolean)options.get(PARAM_ALLOW_SCREEN_READER))
        {
            permissions = permissions | PdfWriter.ALLOW_SCREENREADERS;
        }
        if ((Boolean)options.get(PARAM_ALLOW_DEGRADED_PRINT))
        {
            permissions = permissions | PdfWriter.ALLOW_DEGRADED_PRINTING;
        }
        if ((Boolean)options.get(PARAM_ALLOW_ASSEMBLY))
        {
            permissions = permissions | PdfWriter.ALLOW_ASSEMBLY;
        }
        if ((Boolean)options.get(PARAM_ALLOW_FORM_FILL))
        {
            permissions = permissions | PdfWriter.ALLOW_FILL_IN;
        }

        return permissions;
    }
}
