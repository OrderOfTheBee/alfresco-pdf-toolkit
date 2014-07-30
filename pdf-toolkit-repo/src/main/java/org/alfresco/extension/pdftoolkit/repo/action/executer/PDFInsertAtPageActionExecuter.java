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
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.action.ParameterDefinitionImpl;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.cmr.model.FileExistsException;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.util.TempFileProvider;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFMergerUtility;
import org.apache.pdfbox.util.Splitter;


/**
 * Insert PDF action executer
 * 
 * @author Jared Ottley
 * 
 */

public class PDFInsertAtPageActionExecuter
    extends BasePDFActionExecuter

{

    /**
     * The logger
     */
    private static Log         logger                   = LogFactory.getLog(PDFInsertAtPageActionExecuter.class);

    /**
     * Action constants
     */
    public static final String NAME                     = "pdf-insert-at-page";
    public static final String PARAM_DESTINATION_FOLDER = "destination-folder";
    public static final String PARAM_INSERT_AT_PAGE     = "insert-at-page";
    public static final String PARAM_DESTINATION_NAME   = "destination-name";
    public static final String PARAM_INSERT_CONTENT     = "insert-content";


    /**
     * Add parameter definitions
     */
    @Override
    protected void addParameterDefinitions(List<ParameterDefinition> paramList)
    {
        paramList.add(new ParameterDefinitionImpl(PARAM_DESTINATION_FOLDER, DataTypeDefinition.NODE_REF, false, getParamDisplayLabel(PARAM_DESTINATION_FOLDER)));
        paramList.add(new ParameterDefinitionImpl(PARAM_INSERT_AT_PAGE, DataTypeDefinition.TEXT, false, getParamDisplayLabel(PARAM_INSERT_AT_PAGE)));
        paramList.add(new ParameterDefinitionImpl(PARAM_INSERT_CONTENT, DataTypeDefinition.NODE_REF, true, getParamDisplayLabel(PARAM_INSERT_CONTENT)));
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

        ContentReader contentReader = getReader(actionedUponNodeRef);

        ContentReader insertContentReader = getReader((NodeRef)ruleAction.getParameterValue(PARAM_INSERT_CONTENT));

        if (contentReader != null)
        {
            // Do the work....insert the PDF
            doInsert(ruleAction, actionedUponNodeRef, contentReader, insertContentReader);

            {
                if (logger.isDebugEnabled())
                {
                    logger.debug("Can't execute rule: \n" + "   node: " + actionedUponNodeRef + "\n" + "   reader: "
                                 + contentReader + "\n" + "   action: " + this);
                }
            }
        }
    }


    /**
     * 
     * Build out the insert call
     * 
     */
    protected void doInsert(Action ruleAction, NodeRef actionedUponNodeRef, ContentReader contentReader,
            ContentReader insertContentReader)
    {
        Map<String, Object> options = new HashMap<String, Object>(INITIAL_OPTIONS);
        options.put(PARAM_DESTINATION_NAME, ruleAction.getParameterValue(PARAM_DESTINATION_NAME));
        options.put(PARAM_DESTINATION_FOLDER, ruleAction.getParameterValue(PARAM_DESTINATION_FOLDER));
        options.put(PARAM_INSERT_AT_PAGE, ruleAction.getParameterValue(PARAM_INSERT_AT_PAGE));
        options.put(PARAM_DESTINATION_NAME, ruleAction.getParameterValue(PARAM_DESTINATION_NAME));
        options.put(PARAM_INPLACE, ruleAction.getParameterValue(PARAM_INPLACE));
        
        try
        {
            this.action(ruleAction, actionedUponNodeRef, contentReader, insertContentReader, options);
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
    protected final void action(Action ruleAction, NodeRef actionedUponNodeRef, ContentReader reader, ContentReader insertReader,
            Map<String, Object> options)
    {
        PDDocument pdf = null;
        PDDocument insertContentPDF = null;
        InputStream is = null;
        InputStream cis = null;
        File tempDir = null;
        ContentWriter writer = null;

        try
        {

            int insertAt = Integer.valueOf((String)options.get(PARAM_INSERT_AT_PAGE)).intValue();

            // Get contentReader inputStream
            is = reader.getContentInputStream();
            // Get insertContentReader inputStream
            cis = insertReader.getContentInputStream();
            // stream the target document in
            pdf = PDDocument.load(is);
            // stream the insert content document in
            insertContentPDF = PDDocument.load(cis);

            // split the PDF and put the pages in a list
            Splitter splitter = new Splitter();
            // Need to adjust the input value to get the split at the right page
            splitter.setSplitAtPage(insertAt - 1);

            // Split the pages
            List<PDDocument> pdfs = splitter.split(pdf);

            // Build the output PDF
            PDFMergerUtility merger = new PDFMergerUtility();
            merger.appendDocument((PDDocument)pdfs.get(0), insertContentPDF);
            merger.appendDocument((PDDocument)pdfs.get(0), (PDDocument)pdfs.get(1));
            merger.setDestinationFileName(options.get(PARAM_DESTINATION_NAME).toString());
            merger.mergeDocuments();

            // build a temp dir, name based on the ID of the noderef we are
            // importing
            File alfTempDir = TempFileProvider.getTempDir();
            tempDir = new File(alfTempDir.getPath() + File.separatorChar + actionedUponNodeRef.getId());
            tempDir.mkdir();

            String fileName = options.get(PARAM_DESTINATION_NAME).toString();
            Boolean inplace = Boolean.valueOf(String.valueOf(options.get(PARAM_INPLACE)));
            
            PDDocument completePDF = (PDDocument)pdfs.get(0);

            completePDF.save(tempDir + "" + File.separatorChar + fileName + FILE_EXTENSION);

            try
            {
                completePDF.close();
            }
            catch (IOException e)
            {
                throw new AlfrescoRuntimeException(e.getMessage(), e);
            }


            for (File file : tempDir.listFiles())
            {
                try
                {
                    if (file.isFile())
                    {

                        // Get a writer and prep it for putting it back into the
                        // repo
                        NodeRef destinationNode = createDestinationNode(file.getName(), 
                        		(NodeRef)ruleAction.getParameterValue(PARAM_DESTINATION_FOLDER), actionedUponNodeRef, inplace);
                        writer = serviceRegistry.getContentService().getWriter(destinationNode, ContentModel.PROP_CONTENT, true);
                        
                        writer.setEncoding(reader.getEncoding()); // original
                        // encoding
                        writer.setMimetype(FILE_MIMETYPE);

                        // Put it in the repo
                        writer.putContent(file);

                        // Clean up
                        file.delete();
                    }
                }
                catch (FileExistsException e)
                {
                    throw new AlfrescoRuntimeException("Failed to process file.", e);
                }
            }
        }
        // TODO add better handling
        catch (COSVisitorException e)
        {
            throw new AlfrescoRuntimeException(e.getMessage(), e);
        }
        catch (IOException e)
        {
            throw new AlfrescoRuntimeException(e.getMessage(), e);
        }

        finally
        {
            if (pdf != null)
            {
                try
                {
                    pdf.close();
                }
                catch (IOException e)
                {
                    throw new AlfrescoRuntimeException(e.getMessage(), e);
                }
            }
            if (is != null)
            {
                try
                {
                    is.close();
                }
                catch (IOException e)
                {
                    throw new AlfrescoRuntimeException(e.getMessage(), e);
                }
            }

            if (tempDir != null)
            {
                tempDir.delete();
            }
        }
    }
}
