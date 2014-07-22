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
import java.io.StringWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.imageio.ImageIO;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.extension.pdftoolkit.constraints.MapConstraint;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.action.ParameterDefinitionImpl;
import org.alfresco.repo.template.FreeMarkerProcessor;
import org.alfresco.repo.template.TemplateNode;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.util.TempFileProvider;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;


public class PDFWatermarkActionExecuter
    extends BasePDFStampActionExecuter

{
    /**
     * The logger
     */
    private static Log                    logger                   = LogFactory.getLog(PDFWatermarkActionExecuter.class);

    public static HashMap<String, String> fontConstraint           = new HashMap<String, String>();
    public static HashMap<String, String> depthConstraint          = new HashMap<String, String>();
    public static HashMap<String, String> typeConstraint           = new HashMap<String, String>();
    public static HashMap<String, String> fontSizeConstraint       = new HashMap<String, String>();

    /**
     * Action constants
     */
    public static final String            NAME                     = "pdf-watermark";
    public static final String            PARAM_WATERMARK_IMAGE    = "watermark-image";
    public static final String            PARAM_DESTINATION_FOLDER = "destination-folder";
    public static final String            PARAM_WATERMARK_DEPTH    = "watermark-depth";
    public static final String            PARAM_WATERMARK_TYPE     = "watermark-type";
    public static final String            PARAM_WATERMARK_TEXT     = "watermark-text";
    public static final String            PARAM_WATERMARK_FONT     = "watermark-font";
    public static final String            PARAM_WATERMARK_SIZE     = "watermark-size";

    private static final String           FILE_MIMETYPE            = "application/pdf";

    private FreeMarkerProcessor           freemarkerProcessor;

    /**
     * Position and page constants
     */
    public static final String            DEPTH_UNDER              = "under";
    public static final String            DEPTH_OVER               = "over";

    public static final String            TYPE_IMAGE               = "image";
    public static final String            TYPE_TEXT                = "text";

    public static final String            FONT_OPTION_HELVETICA    = BaseFont.HELVETICA;
    public static final String            FONT_OPTION_COURIER      = BaseFont.COURIER;
    public static final String            FONT_OPTION_TIMES_ROMAN  = BaseFont.TIMES_ROMAN;

    private static final float            PAD                      = 15;


    public PDFWatermarkActionExecuter()
    {

        freemarkerProcessor = new FreeMarkerProcessor();
    }


    /**
     * setters for constraint beans
     * 
     * @param fontConstraint
     */
    public void setFontConstraint(MapConstraint mc)
    {
        fontConstraint.putAll(mc.getAllowableValues());
    }


    public void setDepthConstraint(MapConstraint mc)
    {
        depthConstraint.putAll(mc.getAllowableValues());
    }


    public void setTypeConstraint(MapConstraint mc)
    {
        typeConstraint.putAll(mc.getAllowableValues());
    }


    public void setFontSizeConstraint(MapConstraint mc)
    {
        fontSizeConstraint.putAll(mc.getAllowableValues());
    }


    /**
     * Add parameter definitions
     */
    @Override
    protected void addParameterDefinitions(List<ParameterDefinition> paramList)
    {

        paramList.add(new ParameterDefinitionImpl(PARAM_DESTINATION_FOLDER, DataTypeDefinition.NODE_REF, false, getParamDisplayLabel(PARAM_DESTINATION_FOLDER)));
        paramList.add(new ParameterDefinitionImpl(PARAM_WATERMARK_IMAGE, DataTypeDefinition.NODE_REF, false, getParamDisplayLabel(PARAM_WATERMARK_IMAGE)));
        paramList.add(new ParameterDefinitionImpl(PARAM_WATERMARK_DEPTH, DataTypeDefinition.TEXT, true, getParamDisplayLabel(PARAM_WATERMARK_DEPTH), false, "pdfc-depth"));
        paramList.add(new ParameterDefinitionImpl(PARAM_WATERMARK_TEXT, DataTypeDefinition.TEXT, false, getParamDisplayLabel(PARAM_WATERMARK_TEXT)));
        paramList.add(new ParameterDefinitionImpl(PARAM_WATERMARK_FONT, DataTypeDefinition.TEXT, false, getParamDisplayLabel(PARAM_WATERMARK_FONT), false, "pdfc-font"));
        paramList.add(new ParameterDefinitionImpl(PARAM_WATERMARK_SIZE, DataTypeDefinition.TEXT, false, getParamDisplayLabel(PARAM_WATERMARK_SIZE), false, "pdfc-fontsize"));
        paramList.add(new ParameterDefinitionImpl(PARAM_WATERMARK_TYPE, DataTypeDefinition.TEXT, true, getParamDisplayLabel(PARAM_WATERMARK_TYPE), false, "pdfc-watermarktype"));

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
            // Add the watermark to the image
            doWatermark(ruleAction, actionedUponNodeRef, actionedUponContentReader);

        }
        else
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Can't execute rule: \n" + "   node: " + actionedUponNodeRef + "\n" + "   reader: "
                             + actionedUponContentReader + "\n" + "   action: " + this);
            }
        }
    }


    /**
     * @see org.alfresco.repo.action.executer.TransformActionExecuter#doWatermark(org.alfresco.service.cmr.action.Action,
     * org.alfresco.service.cmr.repository.ContentReader,
     * org.alfresco.service.cmr.repository.ContentWriter)
     */
    protected void doWatermark(Action ruleAction, NodeRef actionedUponNodeRef, ContentReader actionedUponContentReader)
    {

        Map<String, Object> options = new HashMap<String, Object>(INITIAL_OPTIONS);

        options.put(PARAM_DESTINATION_FOLDER, ruleAction.getParameterValue(PARAM_DESTINATION_FOLDER));
        options.put(PARAM_PAGE, ruleAction.getParameterValue(PARAM_PAGE));
        options.put(PARAM_POSITION, ruleAction.getParameterValue(PARAM_POSITION));
        options.put(PARAM_WATERMARK_DEPTH, ruleAction.getParameterValue(PARAM_WATERMARK_DEPTH));

        try
        {
            if (ruleAction.getParameterValue(PARAM_WATERMARK_TYPE) != null
                && ruleAction.getParameterValue(PARAM_WATERMARK_TYPE).equals(TYPE_IMAGE))
            {

                NodeRef watermarkNodeRef = (NodeRef)ruleAction.getParameterValue(PARAM_WATERMARK_IMAGE);
                ContentReader watermarkContentReader = getReader(watermarkNodeRef);

                // add additional options only used by this specific watermark
                // type
                options.put(PARAM_WATERMARK_IMAGE, ruleAction.getParameterValue(PARAM_WATERMARK_IMAGE));

                this.imageAction(ruleAction, actionedUponNodeRef, watermarkNodeRef, actionedUponContentReader, watermarkContentReader, options);

            }
            else if (ruleAction.getParameterValue(PARAM_WATERMARK_TYPE) != null
                     && ruleAction.getParameterValue(PARAM_WATERMARK_TYPE).equals(TYPE_TEXT))
            {

                // add additional options only used by text types
                options.put(PARAM_WATERMARK_TEXT, ruleAction.getParameterValue(PARAM_WATERMARK_TEXT));

                options.put(PARAM_WATERMARK_FONT, ruleAction.getParameterValue(PARAM_WATERMARK_FONT));

                options.put(PARAM_WATERMARK_SIZE, ruleAction.getParameterValue(PARAM_WATERMARK_SIZE));

                this.textAction(ruleAction, actionedUponNodeRef, actionedUponContentReader, options);
            }
        }
        catch (AlfrescoRuntimeException e)
        {
            throw new AlfrescoRuntimeException(e.getMessage(), e);
        }
    }


    /**
     * Applies an image watermark
     * 
     * @param reader
     * @param writer
     * @param options
     * @throws Exception
     */
    private void imageAction(Action ruleAction, NodeRef actionedUponNodeRef, NodeRef watermarkNodeRef,
            ContentReader actionedUponContentReader, ContentReader watermarkContentReader, Map<String, Object> options)
    {

        PdfStamper stamp = null;
        File tempDir = null;
        ContentWriter writer = null;

        try
        {
            // get a temp file to stash the watermarked PDF in before moving to
            // repo
            File alfTempDir = TempFileProvider.getTempDir();
            tempDir = new File(alfTempDir.getPath() + File.separatorChar + actionedUponNodeRef.getId());
            tempDir.mkdir();
            File file = new File(tempDir, serviceRegistry.getFileFolderService().getFileInfo(actionedUponNodeRef).getName());

            // get the PDF input stream and create a reader for iText
            PdfReader reader = new PdfReader(actionedUponContentReader.getContentInputStream());
            stamp = new PdfStamper(reader, new FileOutputStream(file));
            PdfContentByte pcb;

            // get a com.itextpdf.text.Image object via java.imageio.ImageIO
            Image img = Image.getInstance(ImageIO.read(watermarkContentReader.getContentInputStream()), null);

            // get the PDF pages and position
            String pages = (String)options.get(PARAM_PAGE);
            String position = (String)options.get(PARAM_POSITION);
            String depth = (String)options.get(PARAM_WATERMARK_DEPTH);

            // get the manual positioning options (if provided)
            int locationX = getInteger(ruleAction.getParameterValue(PARAM_LOCATION_X));
            int locationY = getInteger(ruleAction.getParameterValue(PARAM_LOCATION_Y));
            
            // image requires absolute positioning or an exception will be
            // thrown
            // set image position according to parameter. Use
            // PdfReader.getPageSizeWithRotation
            // to get the canvas size for alignment.
            img.setAbsolutePosition(100f, 100f);

            // stamp each page
            int numpages = reader.getNumberOfPages();
            for (int i = 1; i <= numpages; i++)
            {
                Rectangle r = reader.getPageSizeWithRotation(i);
                // set stamp position
                if (position.equals(POSITION_BOTTOMLEFT))
                {
                    img.setAbsolutePosition(0, 0);
                }
                else if (position.equals(POSITION_BOTTOMRIGHT))
                {
                    img.setAbsolutePosition(r.getWidth() - img.getWidth(), 0);
                }
                else if (position.equals(POSITION_TOPLEFT))
                {
                    img.setAbsolutePosition(0, r.getHeight() - img.getHeight());
                }
                else if (position.equals(POSITION_TOPRIGHT))
                {
                    img.setAbsolutePosition(r.getWidth() - img.getWidth(), r.getHeight() - img.getHeight());
                }
                else if (position.equals(POSITION_CENTER))
                {
                    img.setAbsolutePosition(getCenterX(r, img), getCenterY(r, img));
                }
                else if (position.equals(POSITION_MANUAL))
                {
                	img.setAbsolutePosition(locationX, locationY);
                }

                // if this is an under-text stamp, use getUnderContent.
                // if this is an over-text stamp, usse getOverContent.
                if (depth.equals(DEPTH_OVER))
                {
                    pcb = stamp.getOverContent(i);
                }
                else
                {
                    pcb = stamp.getUnderContent(i);
                }

                // only apply stamp to requested pages
                if (checkPage(pages, i, numpages))
                {
                    pcb.addImage(img);
                }
            }

            stamp.close();
            
            // Get a writer and prep it for putting it back into the repo
            //can't use BasePDFActionExecuter.getWriter here need the nodeRef of the destination
            NodeRef destinationNode = createDestinationNode(file.getName(), 
            		(NodeRef)ruleAction.getParameterValue(PARAM_DESTINATION_FOLDER), actionedUponNodeRef);
            writer = serviceRegistry.getContentService().getWriter(destinationNode, ContentModel.PROP_CONTENT, true);
            
            writer.setEncoding(actionedUponContentReader.getEncoding());
            writer.setMimetype(FILE_MIMETYPE);

            // Put it in the repo
            writer.putContent(file);

            // delete the temp file
            file.delete();
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
     * Applies a text watermark (current date, user name, etc, depending on
     * options)
     * 
     * @param reader
     * @param writer
     * @param options
     */
    private void textAction(Action ruleAction, NodeRef actionedUponNodeRef, ContentReader actionedUponContentReader,
            Map<String, Object> options)
    {

        PdfStamper stamp = null;
        File tempDir = null;
        ContentWriter writer = null;
        String watermarkText;
        StringTokenizer st;
        Vector<String> tokens = new Vector<String>();

        try
        {
            // get a temp file to stash the watermarked PDF in before moving to
            // repo
            File alfTempDir = TempFileProvider.getTempDir();
            tempDir = new File(alfTempDir.getPath() + File.separatorChar + actionedUponNodeRef.getId());
            tempDir.mkdir();
            File file = new File(tempDir, serviceRegistry.getFileFolderService().getFileInfo(actionedUponNodeRef).getName());

            // get the PDF input stream and create a reader for iText
            PdfReader reader = new PdfReader(actionedUponContentReader.getContentInputStream());
            stamp = new PdfStamper(reader, new FileOutputStream(file));
            PdfContentByte pcb;

            // get the PDF pages and position
            String pages = (String)options.get(PARAM_PAGE);
            String position = (String)options.get(PARAM_POSITION);
            String depth = (String)options.get(PARAM_WATERMARK_DEPTH);
            int locationX = getInteger(ruleAction.getParameterValue(PARAM_LOCATION_X));
            int locationY = getInteger(ruleAction.getParameterValue(PARAM_LOCATION_Y));
            
            // create the base font for the text stamp
            BaseFont bf = BaseFont.createFont((String)options.get(PARAM_WATERMARK_FONT), BaseFont.CP1250, BaseFont.EMBEDDED);


            // get watermark text and process template with model
            String templateText = (String)options.get(PARAM_WATERMARK_TEXT);
            Map<String, Object> model = buildWatermarkTemplateModel(actionedUponNodeRef);
            StringWriter watermarkWriter = new StringWriter();
            freemarkerProcessor.processString(templateText, model, watermarkWriter);
            watermarkText = watermarkWriter.getBuffer().toString();

            // tokenize watermark text to support multiple lines and copy tokens
            // to vector for re-use
            st = new StringTokenizer(watermarkText, "\r\n", false);
            while (st.hasMoreTokens())
            {
                tokens.add(st.nextToken());
            }

            // stamp each page
            int numpages = reader.getNumberOfPages();
            for (int i = 1; i <= numpages; i++)
            {
                Rectangle r = reader.getPageSizeWithRotation(i);

                // if this is an under-text stamp, use getUnderContent.
                // if this is an over-text stamp, use getOverContent.
                if (depth.equals(DEPTH_OVER))
                {
                    pcb = stamp.getOverContent(i);
                }
                else
                {
                    pcb = stamp.getUnderContent(i);
                }

                // set the font and size
                float size = Float.parseFloat((String)options.get(PARAM_WATERMARK_SIZE));
                pcb.setFontAndSize(bf, size);

                // only apply stamp to requested pages
                if (checkPage(pages, i, numpages))
                {
                    writeAlignedText(pcb, r, tokens, size, position, locationX, locationY);
                }
            }

            stamp.close();

            // Get a writer and prep it for putting it back into the repo
            //can't use BasePDFActionExecuter.getWriter here need the nodeRef of the destination
            NodeRef destinationNode = createDestinationNode(file.getName(), 
            		(NodeRef)ruleAction.getParameterValue(PARAM_DESTINATION_FOLDER), actionedUponNodeRef);
            writer = serviceRegistry.getContentService().getWriter(destinationNode, ContentModel.PROP_CONTENT, true);
            writer.setEncoding(actionedUponContentReader.getEncoding());
            writer.setMimetype(FILE_MIMETYPE);

            // Put it in the repo
            writer.putContent(file);

            // delete the temp file
            file.delete();
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
     * Writes text watermark to one of the 5 preconfigured locations
     * 
     * @param pcb
     * @param r
     * @param tokens
     * @param size
     * @param position
     */
    private void writeAlignedText(PdfContentByte pcb, Rectangle r, Vector<String> tokens, float size, 
    		String position, int locationX, int locationY)
    {
        // get the dimensions of our 'rectangle' for text
        float height = size * tokens.size();
        float width = 0;
        float centerX = 0, startY = 0;
        for (int i = 0; i < tokens.size(); i++)
        {
            if (pcb.getEffectiveStringWidth(tokens.get(i), false) > width)
            {
                width = pcb.getEffectiveStringWidth(tokens.get(i), false);
            }
        }

        // now that we have the width and height, we can calculate the center
        // position for
        // the rectangle that will contain our text.
        if (position.equals(POSITION_BOTTOMLEFT))
        {
            centerX = width / 2 + PAD;
            startY = 0 + PAD + height;
        }
        else if (position.equals(POSITION_BOTTOMRIGHT))
        {
            centerX = r.getWidth() - (width / 2) - PAD;
            startY = 0 + PAD + height;
        }
        else if (position.equals(POSITION_TOPLEFT))
        {
            centerX = width / 2 + PAD;
            startY = r.getHeight() - (PAD * 2);
        }
        else if (position.equals(POSITION_TOPRIGHT))
        {
            centerX = r.getWidth() - (width / 2) - PAD;
            startY = r.getHeight() - (PAD * 2);
        }
        else if (position.equals(POSITION_CENTER))
        {
            centerX = r.getWidth() / 2;
            startY = (r.getHeight() / 2) + (height / 2);
        }
        else if (position.equals(POSITION_MANUAL))
        {
        	centerX = r.getWidth() / 2 - locationX;
        	startY = locationY;
        }

        // apply text to PDF
        pcb.beginText();

        for (int t = 0; t < tokens.size(); t++)
        {
            pcb.showTextAligned(PdfContentByte.ALIGN_CENTER, tokens.get(t), centerX, startY - (size * t), 0);
        }

        pcb.endText();

    }


    /**
     * Builds a freemarker model which supports a subset of the default model.
     * 
     * @param ref
     * @return
     */
    private Map<String, Object> buildWatermarkTemplateModel(NodeRef ref)
    {
        Map<String, Object> model = new HashMap<String, Object>();

        NodeRef person = serviceRegistry.getPersonService().getPerson(serviceRegistry.getAuthenticationService().getCurrentUserName());
        model.put("person", new TemplateNode(person, serviceRegistry, null));
        NodeRef homespace = (NodeRef)serviceRegistry.getNodeService().getProperty(person, ContentModel.PROP_HOMEFOLDER);
        model.put("userhome", new TemplateNode(homespace, serviceRegistry, null));
        model.put("document", new TemplateNode(ref, serviceRegistry, null));
        NodeRef parent = serviceRegistry.getNodeService().getPrimaryParent(ref).getParentRef();
        model.put("space", new TemplateNode(parent, serviceRegistry, null));
        model.put("date", new Date());

        //also add all of the node properties to the model
        model.put("properties", serviceRegistry.getNodeService().getProperties(ref));
        
        return model;
    }
}
