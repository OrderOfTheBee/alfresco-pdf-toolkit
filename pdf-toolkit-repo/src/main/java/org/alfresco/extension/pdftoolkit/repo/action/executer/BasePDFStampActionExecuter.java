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


import java.util.HashMap;
import java.util.List;

import org.alfresco.extension.pdftoolkit.constraints.MapConstraint;
import org.alfresco.repo.action.ParameterDefinitionImpl;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;

import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;


public abstract class BasePDFStampActionExecuter
    extends BasePDFActionExecuter
{

    public static HashMap<String, String> pageConstraint       = new HashMap<String, String>();
    public static HashMap<String, String> positionConstraint   = new HashMap<String, String>();

    /*
     * Page and position constants
     */
    public static final String                  PAGE_ALL             = "all";
    public static final String                  PAGE_ODD             = "odd";
    public static final String                  PAGE_EVEN            = "even";
    public static final String                  PAGE_FIRST           = "first";
    public static final String                  PAGE_LAST            = "last";

    public static final String                  POSITION_CENTER      = "center";
    public static final String                  POSITION_TOPLEFT     = "topleft";
    public static final String                  POSITION_TOPRIGHT    = "topright";
    public static final String                  POSITION_BOTTOMLEFT  = "bottomleft";
    public static final String                  POSITION_BOTTOMRIGHT = "bottomright";

    public static final String                  PARAM_POSITION       = "position";
    public static final String                  PARAM_LOCATION_X     = "location-x";
    public static final String                  PARAM_LOCATION_Y     = "location-y";
    public static final String					PARAM_PAGE			 = "page";

    /**
     * Constraints
     */
    public void setPositionConstraint(MapConstraint mc)
    {
        positionConstraint.putAll(mc.getAllowableValues());
    }


    public void setPageConstraint(MapConstraint mc)
    {
        pageConstraint.putAll(mc.getAllowableValues());
    }


    /**
     * Add parameter definitions
     */
    @Override
    protected void addParameterDefinitions(List<ParameterDefinition> paramList)
    {
        paramList.add(new ParameterDefinitionImpl(PARAM_POSITION, DataTypeDefinition.TEXT, false, getParamDisplayLabel(PARAM_POSITION), false, "pdfc-position"));
        paramList.add(new ParameterDefinitionImpl(PARAM_LOCATION_X, DataTypeDefinition.TEXT, false, getParamDisplayLabel(PARAM_LOCATION_X)));
        paramList.add(new ParameterDefinitionImpl(PARAM_LOCATION_Y, DataTypeDefinition.TEXT, false, getParamDisplayLabel(PARAM_LOCATION_Y)));
        paramList.add(new ParameterDefinitionImpl(PARAM_PAGE, DataTypeDefinition.TEXT, false, getParamDisplayLabel(PARAM_PAGE), false));
        
        super.addParameterDefinitions(paramList);
    }


    /**
     * Determines whether or not a watermark should be applied to a given page
     * 
     * @param pages
     * @param current
     * @param numpages
     * @return
     */
    protected boolean checkPage(String pages, int current, int numpages)
    {

        boolean markPage = false;

        if (pages.equals(PAGE_EVEN) || pages.equals(PAGE_ODD))
        {
            if (current % 2 == 0)
            {
                markPage = true;
            }
        }
        else if (pages.equals(PAGE_ODD))
        {
            if (current % 2 != 0)
            {
                markPage = true;
            }
        }
        else if (pages.equals(PAGE_FIRST))
        {
            if (current == 1)
            {
                markPage = true;
            }
        }
        else if (pages.equals(PAGE_LAST))
        {
            if (current == numpages)
            {
                markPage = true;
            }
        }
        else
        {
            markPage = true;
        }

        return markPage;
    }


    /**
     * Gets the X value for centering the watermark image
     * 
     * @param r
     * @param img
     * @return
     */
    protected float getCenterX(Rectangle r, Image img)
    {
        float x = 0;
        float pdfwidth = r.getWidth();
        float imgwidth = img.getWidth();

        x = (pdfwidth - imgwidth) / 2;

        return x;
    }


    /**
     * Gets the Y value for centering the watermark image
     * 
     * @param r
     * @param img
     * @return
     */
    protected float getCenterY(Rectangle r, Image img)
    {
        float y = 0;
        float pdfheight = r.getHeight();
        float imgheight = img.getHeight();

        y = (pdfheight - imgheight) / 2;

        return y;
    }
}
