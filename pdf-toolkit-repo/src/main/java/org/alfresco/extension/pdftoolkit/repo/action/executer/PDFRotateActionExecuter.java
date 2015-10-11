package org.alfresco.extension.pdftoolkit.repo.action.executer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.List;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.action.ParameterDefinitionImpl;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.util.TempFileProvider;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfNumber;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

public class PDFRotateActionExecuter extends BasePDFActionExecuter {

	/**
     * The logger
     */
    private static Log         logger                   = LogFactory.getLog(PDFRotateActionExecuter.class);

    /**
     * Action constants
     */
    public static final String NAME                     = "pdf-rotate";
    public static final String PARAM_DESTINATION_FOLDER = "destination-folder";
    public static final String PARAM_DEGREES     		= "degrees";
    public static final String PARAM_DESTINATION_NAME   = "destination-name";
    
	@Override
	protected void executeImpl(Action action, NodeRef actionedUponNodeRef) {

        if (serviceRegistry.getNodeService().exists(actionedUponNodeRef) == false)
        {
            // node doesn't exist - can't do anything
            return;
        }

        ContentReader contentReader = getReader(actionedUponNodeRef);

        if (contentReader != null)
        {
            // Rotate the pages of this document
            doRotate(action, actionedUponNodeRef, contentReader);

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
     * Add parameter definitions
     */
    @Override
    protected void addParameterDefinitions(List<ParameterDefinition> paramList)
    {
        paramList.add(new ParameterDefinitionImpl(PARAM_DESTINATION_FOLDER, DataTypeDefinition.NODE_REF, false, getParamDisplayLabel(PARAM_DESTINATION_FOLDER)));
        paramList.add(new ParameterDefinitionImpl(PARAM_DEGREES, DataTypeDefinition.TEXT, true, getParamDisplayLabel(PARAM_DEGREES)));
        paramList.add(new ParameterDefinitionImpl(PARAM_DESTINATION_NAME, DataTypeDefinition.TEXT, false, getParamDisplayLabel(PARAM_DESTINATION_NAME)));
        
        super.addParameterDefinitions(paramList);
    }
	/**
	 * Delete the requested pages from the PDF doc and save it to the destination location.
	 * 
	 * @param action
	 * @param actionedUponNodeRef
	 * @param reader
	 */
	private void doRotate(Action action, NodeRef actionedUponNodeRef, ContentReader reader)
	{
		InputStream is = null;
        File tempDir = null;
        ContentWriter writer = null;
        PdfReader pdfReader = null;
        NodeService ns = serviceRegistry.getNodeService();
        
        try
        {
            is = reader.getContentInputStream();

            File alfTempDir = TempFileProvider.getTempDir();
            tempDir = new File(alfTempDir.getPath() + File.separatorChar + actionedUponNodeRef.getId());
            tempDir.mkdir();
            
            Serializable providedName = action.getParameterValue(PARAM_DESTINATION_NAME);
            Boolean inplace = Boolean.valueOf(String.valueOf(action.getParameterValue(PARAM_INPLACE)));
            Integer degrees = Integer.valueOf(String.valueOf(action.getParameterValue(PARAM_DEGREES)));
            
            if(degrees % 90 != 0)
            {
            	throw new AlfrescoRuntimeException("Rotation degres must be a multiple of 90 (90, 180, 270, etc)");
            }
            
            String fileName = null;
            if(providedName != null)
            {
            	fileName = String.valueOf(providedName);
            }
            else
            {
            	fileName = String.valueOf(ns.getProperty(actionedUponNodeRef, ContentModel.PROP_NAME)) + "-pagesDeleted";
            }
            
            File file = new File(tempDir, serviceRegistry.getFileFolderService().getFileInfo(actionedUponNodeRef).getName());

            
            pdfReader = new PdfReader(is);
            PdfStamper stamp = new PdfStamper(pdfReader, new FileOutputStream(file));
            
            int rotation = 0;
            PdfDictionary pageDictionary;
            for (int pageNum = 1; pageNum <= pdfReader.getNumberOfPages(); pageNum++) 
            {
            	rotation = pdfReader.getPageRotation(pageNum);
            	pageDictionary = pdfReader.getPageN(pageNum);
                pageDictionary.put(PdfName.ROTATE, new PdfNumber(rotation + degrees));
            }
            stamp.close();
            pdfReader.close();

            NodeRef destinationNode = createDestinationNode(fileName, 
            		(NodeRef)action.getParameterValue(PARAM_DESTINATION_FOLDER), actionedUponNodeRef, inplace);
            writer = serviceRegistry.getContentService().getWriter(destinationNode, ContentModel.PROP_CONTENT, true);

            writer.setEncoding(reader.getEncoding());
            writer.setMimetype(FILE_MIMETYPE);

            // Put it in the repository
            writer.putContent(file);

            // Clean up
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
        catch (Exception e)
        {
        	throw new AlfrescoRuntimeException(e.getMessage(), e);
        }
        finally
        {
            if (pdfReader != null)
            {
            	pdfReader.close();
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
