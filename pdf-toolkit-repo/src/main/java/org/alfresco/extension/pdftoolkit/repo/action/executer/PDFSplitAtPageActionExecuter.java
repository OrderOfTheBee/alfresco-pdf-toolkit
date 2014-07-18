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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.action.ParameterDefinitionImpl;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.cmr.model.FileExistsException;
import org.alfresco.service.cmr.model.FileInfo;
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
 * Split PDF action executer
 * 
 * @author Jared Ottley
 * 
 */

public class PDFSplitAtPageActionExecuter
    extends BasePDFActionExecuter

{

    /**
     * The logger
     */
    private static Log         logger                   = LogFactory.getLog(PDFSplitAtPageActionExecuter.class);

    /**
     * Action constants
     */
    public static final String NAME                     = "pdf-split-at-page";
    public static final String PARAM_DESTINATION_FOLDER = "destination-folder";
    public static final String PARAM_SPLIT_AT_PAGE      = "split-at-page";


    /**
     * Add parameter definitions
     */
    @Override
    protected void addParameterDefinitions(List<ParameterDefinition> paramList)
    {
        paramList.add(new ParameterDefinitionImpl(PARAM_DESTINATION_FOLDER, DataTypeDefinition.NODE_REF, true, getParamDisplayLabel(PARAM_DESTINATION_FOLDER)));
        paramList.add(new ParameterDefinitionImpl(PARAM_SPLIT_AT_PAGE, DataTypeDefinition.TEXT, false, getParamDisplayLabel(PARAM_SPLIT_AT_PAGE)));
        
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

        if (contentReader != null)
        {
            // Do the work....split the PDF
            doSplit(ruleAction, actionedUponNodeRef, contentReader);

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
     * @see org.alfresco.repo.action.executer.TransformActionExecuter#doTransform(org.alfresco.service.cmr.action.Action,
     * org.alfresco.service.cmr.repository.ContentReader,
     * org.alfresco.service.cmr.repository.ContentWriter)
     */
    protected void doSplit(Action ruleAction, NodeRef actionedUponNodeRef, ContentReader contentReader)
    {
        Map<String, Object> options = new HashMap<String, Object>(INITIAL_OPTIONS);
        options.put(PARAM_DESTINATION_FOLDER, ruleAction.getParameterValue(PARAM_DESTINATION_FOLDER));
        options.put(PARAM_SPLIT_AT_PAGE, ruleAction.getParameterValue(PARAM_SPLIT_AT_PAGE));

        try
        {
            this.action(ruleAction, actionedUponNodeRef, contentReader, options);
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
    protected final void action(Action ruleAction, NodeRef actionedUponNodeRef, ContentReader reader, Map<String, Object> options)
    {
        PDDocument pdf = null;
        InputStream is = null;
        File tempDir = null;
        ContentWriter writer = null;

        try
        {
            // Get the split frequency
            int splitFrequency = 0;

            String splitFrequencyString = options.get(PARAM_SPLIT_AT_PAGE).toString();
            if (!splitFrequencyString.equals(""))
            {
                try
                {
                    splitFrequency = Integer.valueOf(splitFrequencyString);
                }
                catch (NumberFormatException e)
                {
                    throw new AlfrescoRuntimeException(e.getMessage(), e);
                }
            }

            // Get contentReader inputStream
            is = reader.getContentInputStream();
            // stream the document in
            pdf = PDDocument.load(is);
            // split the PDF and put the pages in a list
            Splitter splitter = new Splitter();
            // Need to adjust the input value to get the split at the right page
            splitter.setSplitAtPage(splitFrequency - 1);

            // Split the pages
            List<PDDocument> pdfs = splitter.split(pdf);

            // Start page split numbering at
            int page = 1;

            // build a temp dir, name based on the ID of the noderef we are
            // importing
            File alfTempDir = TempFileProvider.getTempDir();
            tempDir = new File(alfTempDir.getPath() + File.separatorChar + actionedUponNodeRef.getId());
            tempDir.mkdir();

            // FLAG: This is ugly.....get the first PDF.
            PDDocument firstPDF = (PDDocument)pdfs.remove(0);

            int pagesInFirstPDF = firstPDF.getNumberOfPages();

            String lastPage = "";
            String pg = "_pg";

            if (pagesInFirstPDF > 1)
            {
                pg = "_pgs";
                lastPage = "-" + pagesInFirstPDF;
            }

            String fileNameSansExt = getFilenameSansExt(actionedUponNodeRef, FILE_EXTENSION);
            firstPDF.save(tempDir + "" + File.separatorChar + fileNameSansExt + pg + page + lastPage + FILE_EXTENSION);

            try
            {
                firstPDF.close();
            }
            catch (IOException e)
            {
                throw new AlfrescoRuntimeException(e.getMessage(), e);
            }

            // FLAG: Like I said: "_UGLY_" ..... and it gets worse
            PDDocument secondPDF = null;

            Iterator<PDDocument> its = pdfs.iterator();

            int pagesInSecondPDF = 0;

            while (its.hasNext())
            {
                if (secondPDF != null)
                {
                    // Get the split document and save it into the temp dir with
                    // new name
                    PDDocument splitpdf = (PDDocument)its.next();

                    int pagesInThisPDF = splitpdf.getNumberOfPages();
                    pagesInSecondPDF = pagesInSecondPDF + pagesInThisPDF;

                    PDFMergerUtility merger = new PDFMergerUtility();
                    merger.appendDocument(secondPDF, splitpdf);
                    merger.mergeDocuments();


                    try
                    {
                        splitpdf.close();
                    }
                    catch (IOException e)
                    {
                        throw new AlfrescoRuntimeException(e.getMessage(), e);
                    }

                }
                else
                {
                    secondPDF = (PDDocument)its.next();

                    pagesInSecondPDF = secondPDF.getNumberOfPages();
                }
            }

            if (pagesInSecondPDF > 1)
            {

                pg = "_pgs";
                lastPage = "-" + (pagesInSecondPDF + pagesInFirstPDF);

            }
            else
            {
                pg = "_pg";
                lastPage = "";
            }

            // This is where we should save the appended PDF
            // put together the name and save the PDF
            secondPDF.save(tempDir + "" + File.separatorChar + fileNameSansExt + pg + splitFrequency + lastPage + FILE_EXTENSION);

            for (File file : tempDir.listFiles())
            {
                try
                {
                    if (file.isFile())
                    {
                        // Get a writer and prep it for putting it back into the
                        // repo
                        NodeRef destinationNode = createDestinationNode(file.getName(), 
                        		(NodeRef)ruleAction.getParameterValue(PARAM_DESTINATION_FOLDER), actionedUponNodeRef);
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


    /**
     * @param fileName
     * @param extension
     * @return
     */
    public String removeExtension(String fileName, String extension)
    {
        // Does the file have the extension?
        if (fileName != null && fileName.contains(extension))
        {
            // Where does the extension start?
            int extensionStartsAt = fileName.indexOf(extension);
            // Get the Filename sans the extension
            return fileName.substring(0, extensionStartsAt);
        }

        return fileName;
    }


    protected String getFilename(NodeRef actionedUponNodeRef)
    {
        FileInfo fileInfo = serviceRegistry.getFileFolderService().getFileInfo(actionedUponNodeRef);
        String filename = fileInfo.getName();

        return filename;
    }


    protected String getFilenameSansExt(NodeRef actionedUponNodeRef, String extension)
    {
        String filenameSansExt;
        filenameSansExt = removeExtension(getFilename(actionedUponNodeRef), extension);

        return filenameSansExt;
    }
}
