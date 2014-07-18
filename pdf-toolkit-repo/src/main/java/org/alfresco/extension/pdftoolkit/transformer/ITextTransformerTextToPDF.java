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

package org.alfresco.extension.pdftoolkit.transformer;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.repo.content.transform.AbstractContentTransformer2;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.TransformationOptions;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;


/**
 * This class is a transformer from Plain Text to PDF, powered by iText
 * 
 * @author Nick Burch
 */
public class ITextTransformerTextToPDF
    extends AbstractContentTransformer2
{
    private static final Log logger = LogFactory.getLog(ITextTransformerTextToPDF.class);


    /**
     * Only supports Text to PDF
     */
    public boolean isTransformable(String sourceMimetype, String targetMimetype, TransformationOptions options)
    {
        if ((!MimetypeMap.MIMETYPE_TEXT_PLAIN.equals(sourceMimetype) && !MimetypeMap.MIMETYPE_TEXT_CSV.equals(sourceMimetype) && !MimetypeMap.MIMETYPE_XML.equals(sourceMimetype))
            || !MimetypeMap.MIMETYPE_PDF.equals(targetMimetype))
        {
            // only support (text/plain OR text/csv OR text/xml) to
            // (application/pdf)
            return false;
        }
        else
        {
            return true;
        }
    }


    /**
     * Do the transformation
     */
    @Override
    protected void transformInternal(ContentReader contentReader, ContentWriter contentWriter, TransformationOptions options)
        throws Exception
    {
        Document document = null;
        BufferedReader reader = null;

        try
        {
            // iText Setup
            document = new Document();
            PdfWriter.getInstance(document, contentWriter.getContentOutputStream());
            document.open();

            // Reader setup
            reader = new BufferedReader(buildReader(contentReader));

            // Process
            String line;
            while ((line = reader.readLine()) != null)
            {
                if (line.length() == 0)
                {
                    // Blank line
                    document.add(new Paragraph(" "));
                }
                else
                {
                    // Paragraph text
                    document.add(new Paragraph(line));
                }
            }
        }
        finally
        {
            if (document != null)
            {
                try
                {
                    document.close();
                }
                catch (Throwable e)
                {
                    e.printStackTrace();
                }
            }
            if (reader != null)
            {
                try
                {
                    reader.close();
                }
                catch (Throwable e)
                {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * Builds an InputStreamReader for the given Content Reader
     */
    protected InputStreamReader buildReader(ContentReader reader)
    {
        // Grab the underlying stream
        InputStream inp = reader.getContentInputStream();
        String node = reader.getContentUrl();

        // If the file has an encoding, try to use it
        String encoding = reader.getEncoding();
        if (encoding != null)
        {
            Charset charset = null;
            try
            {
                charset = Charset.forName(encoding);
            }
            catch (Exception e)
            {
                logger.warn("JVM doesn't understand encoding '" + encoding + "' when transforming " + node);
            }
            if (charset != null)
            {
                logger.debug("Processing plain text in encoding " + charset.displayName());
                return new InputStreamReader(inp, charset);
            }
        }

        // Fall back on the system default
        logger.debug("Processing plain text using system default encoding");
        return new InputStreamReader(inp);
    }
}
