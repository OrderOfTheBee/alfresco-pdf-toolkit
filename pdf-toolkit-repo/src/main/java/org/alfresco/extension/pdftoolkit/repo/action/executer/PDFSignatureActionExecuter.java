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
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.List;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.extension.pdftoolkit.constraints.MapConstraint;
import org.alfresco.extension.pdftoolkit.model.PDFToolkitModel;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.action.ParameterDefinitionImpl;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.cmr.repository.ContentIOException;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.TempFileProvider;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfSignatureAppearance;
import com.itextpdf.text.pdf.PdfStamper;


public class PDFSignatureActionExecuter
    extends BasePDFStampActionExecuter

{

    /**
     * The logger
     */
    private static Log                    logger                   = LogFactory.getLog(PDFSignatureActionExecuter.class);

    /**
     * Flag for use of "pdft:signed" aspect, enable search by signature metadata
     */
    private boolean useAspect									   = true;
    
    /**
     * Constraints
     */
    public static HashMap<String, String> visibilityConstraint     = new HashMap<String, String>();
    public static HashMap<String, String> keyTypeConstraint        = new HashMap<String, String>();

    /**
     * Action constants
     */
    public static final String            NAME                     = "pdf-signature";
    public static final String            PARAM_PRIVATE_KEY        = "private-key";
    public static final String            PARAM_DESTINATION_FOLDER = "destination-folder";
    public static final String			  PARAM_DESTINATION_NAME   = "destination-name";
    public static final String            PARAM_VISIBILITY         = "visibility";
    /*
     * don't confuse the location field below with the position field inherited
     * from parent. "location, in the context of a PDF signature, means the
     * location where it was signed, not the location of the signature block,
     * which is handled by position
     */
    public static final String            PARAM_LOCATION           = "location";
    public static final String            PARAM_REASON             = "reason";
    public static final String            PARAM_KEY_PASSWORD       = "key-password";
    public static final String            PARAM_WIDTH              = "width";
    public static final String            PARAM_HEIGHT             = "height";
    public static final String            PARAM_KEY_TYPE           = "key-type";
    
    // Aditional parameters to accessing the keystore file
    public static final String            PARAM_ALIAS              = "alias";
    public static final String            PARAM_STORE_PASSWORD     = "store-password";

    public static final String            VISIBILITY_HIDDEN        = "hidden";
    public static final String            VISIBILITY_VISIBLE       = "visible";

    public static final String            KEY_TYPE_PKCS12          = "pkcs12";
    public static final String            KEY_TYPE_DEFAULT         = "default";


    /**
     * Constraint beans
     * 
     * @param mc
     */
    public void setKeyTypeConstraint(MapConstraint mc)
    {
        keyTypeConstraint.putAll(mc.getAllowableValues());
    }


    public void setVisibilityConstraint(MapConstraint mc)
    {
        visibilityConstraint.putAll(mc.getAllowableValues());
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
        paramList.add(new ParameterDefinitionImpl(PARAM_PRIVATE_KEY, DataTypeDefinition.NODE_REF, true, getParamDisplayLabel(PARAM_PRIVATE_KEY)));
        paramList.add(new ParameterDefinitionImpl(PARAM_VISIBILITY, DataTypeDefinition.TEXT, true, getParamDisplayLabel(PARAM_VISIBILITY), false, "pdfc-visibility"));
        paramList.add(new ParameterDefinitionImpl(PARAM_LOCATION, DataTypeDefinition.TEXT, false, getParamDisplayLabel(PARAM_LOCATION)));
        paramList.add(new ParameterDefinitionImpl(PARAM_REASON, DataTypeDefinition.TEXT, false, getParamDisplayLabel(PARAM_REASON)));
        paramList.add(new ParameterDefinitionImpl(PARAM_KEY_PASSWORD, DataTypeDefinition.TEXT, true, getParamDisplayLabel(PARAM_KEY_PASSWORD)));
        paramList.add(new ParameterDefinitionImpl(PARAM_WIDTH, DataTypeDefinition.INT, false, getParamDisplayLabel(PARAM_WIDTH)));
        paramList.add(new ParameterDefinitionImpl(PARAM_HEIGHT, DataTypeDefinition.INT, false, getParamDisplayLabel(PARAM_HEIGHT)));
        paramList.add(new ParameterDefinitionImpl(PARAM_KEY_TYPE, DataTypeDefinition.TEXT, true, getParamDisplayLabel(PARAM_KEY_TYPE), false, "pdfc-keytype"));
        paramList.add(new ParameterDefinitionImpl(PARAM_ALIAS, DataTypeDefinition.TEXT, true, getParamDisplayLabel(PARAM_ALIAS)));
        paramList.add(new ParameterDefinitionImpl(PARAM_STORE_PASSWORD, DataTypeDefinition.TEXT, true, getParamDisplayLabel(PARAM_STORE_PASSWORD)));
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
            // Add the signature to the PDF
            doSignature(ruleAction, actionedUponNodeRef, actionedUponContentReader);

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
     * 
     * @param ruleAction
     * @param actionedUponNodeRef
     * @param actionedUponContentReader
     */
    protected void doSignature(Action ruleAction, NodeRef actionedUponNodeRef, ContentReader actionedUponContentReader)
    {

        NodeRef privateKey = (NodeRef)ruleAction.getParameterValue(PARAM_PRIVATE_KEY);
        String location = (String)ruleAction.getParameterValue(PARAM_LOCATION);
        String position = (String)ruleAction.getParameterValue(PARAM_POSITION);
        String reason = (String)ruleAction.getParameterValue(PARAM_REASON);
        String visibility = (String)ruleAction.getParameterValue(PARAM_VISIBILITY);
        String keyPassword = (String)ruleAction.getParameterValue(PARAM_KEY_PASSWORD);
        String keyType = (String)ruleAction.getParameterValue(PARAM_KEY_TYPE);
        int height = getInteger(ruleAction.getParameterValue(PARAM_HEIGHT));
        int width = getInteger(ruleAction.getParameterValue(PARAM_WIDTH));
        int pageNumber = getInteger(ruleAction.getParameterValue(PARAM_PAGE));
        
        // New keystore parameters
        String alias = (String)ruleAction.getParameterValue(PARAM_ALIAS);
        String storePassword = (String)ruleAction.getParameterValue(PARAM_STORE_PASSWORD);
        
        int locationX = getInteger(ruleAction.getParameterValue(PARAM_LOCATION_X));
        int locationY = getInteger(ruleAction.getParameterValue(PARAM_LOCATION_Y));
        
        Boolean inplace = Boolean.valueOf(String.valueOf(ruleAction.getParameterValue(PARAM_INPLACE)));
        
        File tempDir = null;
        ContentWriter writer = null;
        KeyStore ks = null;

        NodeService ns = serviceRegistry.getNodeService();
        
        try
        {
            // get a keystore instance by
            if (keyType == null || keyType.equalsIgnoreCase(KEY_TYPE_DEFAULT))
            {
                ks = KeyStore.getInstance(KeyStore.getDefaultType());
            }
            else if (keyType.equalsIgnoreCase(KEY_TYPE_PKCS12))
            {
                ks = KeyStore.getInstance("pkcs12");
            }
            else
            {
                throw new AlfrescoRuntimeException("Unknown key type " + keyType + " specified");
            }

            // open the reader to the key and load it
            ContentReader keyReader = getReader(privateKey);
            ks.load(keyReader.getContentInputStream(), storePassword.toCharArray());

            // set alias
            // String alias = (String) ks.aliases().nextElement();

            PrivateKey key = (PrivateKey)ks.getKey(alias, keyPassword.toCharArray());
            Certificate[] chain = ks.getCertificateChain(alias);

            // open original pdf
            ContentReader pdfReader = getReader(actionedUponNodeRef);
            PdfReader reader = new PdfReader(pdfReader.getContentInputStream());

            // create temp dir to store file
            File alfTempDir = TempFileProvider.getTempDir();
            tempDir = new File(alfTempDir.getPath() + File.separatorChar + actionedUponNodeRef.getId());
            tempDir.mkdir();
            File file = new File(tempDir, serviceRegistry.getFileFolderService().getFileInfo(actionedUponNodeRef).getName());

            FileOutputStream fout = new FileOutputStream(file);
            PdfStamper stamp = PdfStamper.createSignature(reader, fout, '\0');
            PdfSignatureAppearance sap = stamp.getSignatureAppearance();
            sap.setCrypto(key, chain, null, PdfSignatureAppearance.WINCER_SIGNED);

            // set reason for signature and location of signer
            sap.setReason(reason);
            sap.setLocation(location);

            if (visibility.equalsIgnoreCase(PDFSignatureActionExecuter.VISIBILITY_VISIBLE))
            {
            	//create the signature rectangle using either the provided position or
            	//the exact coordinates, if provided
            	if(position != null && !position.trim().equalsIgnoreCase("") 
            			&& !position.trim().equalsIgnoreCase(BasePDFStampActionExecuter.POSITION_MANUAL))
            	{
            		Rectangle pageRect = reader.getPageSizeWithRotation(pageNumber);
            		sap.setVisibleSignature(positionSignature(position, pageRect, width, height), pageNumber, null);
            	}
            	else
            	{
            		sap.setVisibleSignature(new Rectangle(locationX, locationY, locationX + width, locationY - height), pageNumber, null);
            	}
            }

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
            
            //can't use BasePDFActionExecuter.getWriter here need the nodeRef of the destination
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
            	serviceRegistry.getNodeService().addAspect(destinationNode, PDFToolkitModel.ASPECT_SIGNED, new HashMap<QName, Serializable>());
            	serviceRegistry.getNodeService().setProperty(destinationNode, PDFToolkitModel.PROP_REASON, reason);
            	serviceRegistry.getNodeService().setProperty(destinationNode, PDFToolkitModel.PROP_LOCATION, location);
            	serviceRegistry.getNodeService().setProperty(destinationNode, PDFToolkitModel.PROP_SIGNATUREDATE, new java.util.Date());
            	serviceRegistry.getNodeService().setProperty(destinationNode, PDFToolkitModel.PROP_SIGNEDBY, AuthenticationUtil.getRunAsUser());
            }
            
        }
        catch (IOException e)
        {
            throw new AlfrescoRuntimeException(e.getMessage(), e);
        }
        catch (KeyStoreException e)
        {
            throw new AlfrescoRuntimeException(e.getMessage(), e);
        }
        catch (ContentIOException e)
        {
            throw new AlfrescoRuntimeException(e.getMessage(), e);
        }
        catch (NoSuchAlgorithmException e)
        {
            throw new AlfrescoRuntimeException(e.getMessage(), e);
        }
        catch (CertificateException e)
        {
            throw new AlfrescoRuntimeException(e.getMessage(), e);
        }
        catch (UnrecoverableKeyException e)
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
        }
    }

    /**
     * Create a rectangle for the visible signature using the selected position and signature size
     * 
     * @param position
     * @param width
     * @param height
     * @return
     */
    private Rectangle positionSignature(String position, Rectangle pageRect, int width, int height)
    {

    	float pageHeight = pageRect.getHeight();
    	float pageWidth = pageRect.getWidth();
    	
    	Rectangle r = null;
    	
    	if (position.equals(POSITION_BOTTOMLEFT))
    	{
    		r = new Rectangle(0, height, width, 0);
    	}
    	else if (position.equals(POSITION_BOTTOMRIGHT))
    	{
    		r = new Rectangle(pageWidth - width, pageHeight, pageWidth, pageHeight - height);
    	}
    	else if (position.equals(POSITION_TOPLEFT))
    	{
    		r = new Rectangle(0, pageHeight, width, pageHeight - height);
    	}
    	else if (position.equals(POSITION_TOPRIGHT))
    	{
    		r = new Rectangle(pageWidth - width, height, pageWidth, 0);
    	}
    	else if (position.equals(POSITION_CENTER))
    	{
    		r = new Rectangle((pageWidth / 2) - (width / 2), (pageHeight / 2) - (height / 2),
    				(pageWidth / 2) + (width / 2), (pageHeight / 2) + (height / 2));
    	}

    	return r;
    }
}
