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
import java.util.Map;

import org.alfresco.extension.pdftoolkit.repo.action.executer.BasePDFStampActionExecuter;
import org.alfresco.extension.pdftoolkit.repo.action.executer.PDFSignatureActionExecuter;
import org.alfresco.extension.pdftoolkit.repo.action.executer.PDFWatermarkActionExecuter;
import org.alfresco.web.bean.actions.handlers.BaseActionHandler;


public abstract class BasePDFStampActionHandler
    extends BaseActionHandler
{

    /**
	 * 
	 */
    private static final long     serialVersionUID      = -4612600133004443816L;

    protected static final String PROP_LOCATION_X       = "LocationX";
    protected static final String PROP_LOCATION_Y       = "LocationY";
    protected static final String PROP_POSITION         = "StampPosition";

    protected static final String PROP_OPTIONS_PAGE     = "PageOptions";
    protected static final String PROP_OPTIONS_POSITION = "PositionOptions";


    public void prepareForSave(Map<String, Serializable> actionProps, Map<String, Serializable> repoProps)
    {
        String locationX = (String)actionProps.get(PROP_LOCATION_X);
        repoProps.put(PDFSignatureActionExecuter.PARAM_LOCATION_X, locationX);

        String locationY = (String)actionProps.get(PROP_LOCATION_Y);
        repoProps.put(PDFSignatureActionExecuter.PARAM_LOCATION_Y, locationY);

        String position = (String)actionProps.get(PROP_POSITION);
        repoProps.put(PDFWatermarkActionExecuter.PARAM_POSITION, position);
    }


    public void prepareForEdit(Map<String, Serializable> actionProps, Map<String, Serializable> repoProps)
    {
        String locationX = (String)repoProps.get(PDFSignatureActionExecuter.PARAM_LOCATION_X);
        actionProps.put(PROP_LOCATION_X, locationX);

        String locationY = (String)repoProps.get(PDFSignatureActionExecuter.PARAM_LOCATION_Y);
        actionProps.put(PROP_LOCATION_Y, locationY);

        String position = (String)repoProps.get(PDFWatermarkActionExecuter.PARAM_POSITION);
        actionProps.put(PROP_POSITION, position);
    }


    public void setupUIDefaults(Map<String, Serializable> actionProps)
    {
        actionProps.put(PROP_OPTIONS_PAGE, BasePDFStampActionExecuter.pageConstraint);
        actionProps.put(PROP_OPTIONS_POSITION, BasePDFStampActionExecuter.positionConstraint);
    }
}
