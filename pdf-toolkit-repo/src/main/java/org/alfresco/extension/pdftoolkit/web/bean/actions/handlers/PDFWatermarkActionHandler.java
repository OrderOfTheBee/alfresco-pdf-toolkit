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

import org.alfresco.extension.pdftoolkit.repo.action.executer.BasePDFStampActionExecuter;
import org.alfresco.extension.pdftoolkit.repo.action.executer.PDFWatermarkActionExecuter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.web.app.Application;
import org.alfresco.web.bean.repository.Repository;
import org.alfresco.web.bean.wizard.IWizardBean;


public class PDFWatermarkActionHandler
    extends BasePDFStampActionHandler

{

    private static final long     serialVersionUID     = 8536617341410665343L;

    protected static final String PROP_WATERMARK_IMAGE = "WatermarkImage";
    protected static final String PROP_WATERMARK_PAGES = "WatermarkPages";

    protected static final String PROP_WATERMARK_DEPTH = "WatermarkDepth";
    protected static final String PROP_WATERMARK_TYPE  = "WatermarkType";
    protected static final String PROP_WATERMARK_FONT  = "WatermarkFont";
    protected static final String PROP_WATERMARK_TEXT  = "WatermarkText";
    protected static final String PROP_WATERMARK_SIZE  = "WatermarkSize";

    protected static final String PROP_OPTIONS_DEPTH   = "DepthOptions";
    protected static final String PROP_OPTIONS_TYPE    = "TypeOptions";
    protected static final String PROP_OPTIONS_FONT    = "FontOptions";
    protected static final String PROP_OPTIONS_SIZE    = "SizeOptions";


    public String getJSPPath()
    {
        return getJSPPath(PDFWatermarkActionExecuter.NAME);
    }


    public void prepareForSave(Map<String, Serializable> actionProps, Map<String, Serializable> repoProps)
    {
        super.prepareForSave(actionProps, repoProps);

        // add the destination space id to the action properties
        NodeRef destNodeRef = (NodeRef)actionProps.get(PROP_DESTINATION);
        repoProps.put(PDFWatermarkActionExecuter.PARAM_DESTINATION_FOLDER, destNodeRef);

        // add watermark image
        NodeRef watermarkImage = (NodeRef)actionProps.get(PROP_WATERMARK_IMAGE);
        repoProps.put(PDFWatermarkActionExecuter.PARAM_WATERMARK_IMAGE, watermarkImage);

        // add the watermark position, pages, depth to the action properties
        String pages = (String)actionProps.get(PROP_WATERMARK_PAGES);
        repoProps.put(PDFWatermarkActionExecuter.PARAM_PAGE, pages);

        String depth = (String)actionProps.get(PROP_WATERMARK_DEPTH);
        repoProps.put(PDFWatermarkActionExecuter.PARAM_WATERMARK_DEPTH, depth);

        String type = (String)actionProps.get(PROP_WATERMARK_TYPE);
        repoProps.put(PDFWatermarkActionExecuter.PARAM_WATERMARK_TYPE, type);

        String text = (String)actionProps.get(PROP_WATERMARK_TEXT);
        repoProps.put(PDFWatermarkActionExecuter.PARAM_WATERMARK_TEXT, text);

        String font = (String)actionProps.get(PROP_WATERMARK_FONT);
        repoProps.put(PDFWatermarkActionExecuter.PARAM_WATERMARK_FONT, font);

        String size = (String)actionProps.get(PROP_WATERMARK_SIZE);
        repoProps.put(PDFWatermarkActionExecuter.PARAM_WATERMARK_SIZE, size);
    }


    public void prepareForEdit(Map<String, Serializable> actionProps, Map<String, Serializable> repoProps)
    {

        super.prepareForEdit(actionProps, repoProps);

        actionProps.put(PROP_OPTIONS_PAGE, BasePDFStampActionExecuter.pageConstraint);
        actionProps.put(PROP_OPTIONS_POSITION, BasePDFStampActionExecuter.positionConstraint);
        actionProps.put(PROP_OPTIONS_DEPTH, PDFWatermarkActionExecuter.depthConstraint);
        actionProps.put(PROP_OPTIONS_TYPE, PDFWatermarkActionExecuter.typeConstraint);
        actionProps.put(PROP_OPTIONS_FONT, PDFWatermarkActionExecuter.fontConstraint);
        actionProps.put(PROP_OPTIONS_SIZE, PDFWatermarkActionExecuter.fontSizeConstraint);

        NodeRef destNodeRef = (NodeRef)repoProps.get(PDFWatermarkActionExecuter.PARAM_DESTINATION_FOLDER);
        actionProps.put(PROP_DESTINATION, destNodeRef);

        NodeRef watermarkImage = (NodeRef)repoProps.get(PDFWatermarkActionExecuter.PARAM_WATERMARK_IMAGE);
        actionProps.put(PROP_WATERMARK_IMAGE, watermarkImage);

        String pages = (String)repoProps.get(PDFWatermarkActionExecuter.PARAM_PAGE);
        actionProps.put(PROP_WATERMARK_PAGES, pages);

        String depth = (String)repoProps.get(PDFWatermarkActionExecuter.PARAM_WATERMARK_DEPTH);
        actionProps.put(PROP_WATERMARK_DEPTH, depth);

        String type = (String)repoProps.get(PDFWatermarkActionExecuter.PARAM_WATERMARK_TYPE);
        actionProps.put(PROP_WATERMARK_TYPE, type);

        String text = (String)repoProps.get(PDFWatermarkActionExecuter.PARAM_WATERMARK_TEXT);
        actionProps.put(PROP_WATERMARK_TEXT, text);

        String font = (String)repoProps.get(PDFWatermarkActionExecuter.PARAM_WATERMARK_FONT);
        actionProps.put(PROP_WATERMARK_FONT, font);

        String size = (String)repoProps.get(PDFWatermarkActionExecuter.PARAM_WATERMARK_SIZE);
        actionProps.put(PROP_WATERMARK_SIZE, size);
    }


    public String generateSummary(FacesContext context, IWizardBean wizard, Map<String, Serializable> actionProps)
    {
        NodeRef space = (NodeRef)actionProps.get(PROP_DESTINATION);
        String name = Repository.getNameForNode(Repository.getServiceRegistry(context).getNodeService(), space);

        String watermarkPages = actionProps.get(PROP_WATERMARK_PAGES).toString();
        String watermarkDepth = actionProps.get(PROP_WATERMARK_DEPTH).toString();

        return MessageFormat.format(Application.getMessage(context, "action_pdf_watermark"), new Object[] { name, watermarkPages,
                watermarkDepth });
    }


    @Override
    public void setupUIDefaults(Map<String, Serializable> actionProps)
    {

        // add lists
        actionProps.put(PROP_OPTIONS_DEPTH, PDFWatermarkActionExecuter.depthConstraint);
        actionProps.put(PROP_OPTIONS_TYPE, PDFWatermarkActionExecuter.typeConstraint);
        actionProps.put(PROP_OPTIONS_FONT, PDFWatermarkActionExecuter.fontConstraint);
        actionProps.put(PROP_OPTIONS_SIZE, PDFWatermarkActionExecuter.fontSizeConstraint);

        // set defaults
        actionProps.put(PROP_WATERMARK_TYPE, PDFWatermarkActionExecuter.TYPE_IMAGE);
        actionProps.put(PROP_WATERMARK_DEPTH, PDFWatermarkActionExecuter.DEPTH_OVER);
        actionProps.put(PROP_POSITION, BasePDFStampActionExecuter.POSITION_CENTER);
        actionProps.put(PROP_WATERMARK_PAGES, PDFWatermarkActionExecuter.PAGE_ALL);
        actionProps.put(PROP_WATERMARK_SIZE, "34");
        actionProps.put(PROP_WATERMARK_FONT, PDFWatermarkActionExecuter.FONT_OPTION_COURIER);

        super.setupUIDefaults(actionProps);
    }
}
