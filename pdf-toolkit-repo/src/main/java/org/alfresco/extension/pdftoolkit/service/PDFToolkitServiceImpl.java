package org.alfresco.extension.pdftoolkit.service;

import java.io.File;
import java.io.Serializable;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.model.FileNotFoundException;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.TempFileProvider;

public class PDFToolkitServiceImpl implements PDFToolkitService {

	
    public static final String 				FILE_EXTENSION 		= ".pdf";
    public static final String 				FILE_MIMETYPE  		= "application/pdf";
    public static final String				PDF 				= "pdf";
    
	private ServiceRegistry serviceRegistry;
    private NodeService ns;
    private ContentService cs;
    private FileFolderService ffs;
    private DictionaryService ds;
    
	@Override
	public void encryptPDF() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void signPDF() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void watermarkPDF() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void splitPDF() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void splitPDFAtPage() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void insertPDF() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deletePagesFromPDF() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void rotatePDF() {
		// TODO Auto-generated method stub
		
	}

	public ContentReader getReader(NodeRef nodeRef)
    {
        // First check that the node is a sub-type of content
        QName typeQName = ns.getType(nodeRef);
        if (ds.isSubClass(typeQName, ContentModel.TYPE_CONTENT) == false)
        {
            // it is not content, so can't transform
            return null;
        }

        // Get the content reader
        ContentReader contentReader = cs.getReader(nodeRef, ContentModel.PROP_CONTENT);

        return contentReader;
    }

    /**
     * @param ruleAction
     * @param filename
     * @return
     */
    public NodeRef createDestinationNode(String filename, NodeRef destinationParent, NodeRef target, boolean inplace, boolean createNew)
    {

    	NodeRef destinationNode;
    	
    	// if inplace mode is turned on, the destination for the modified content
    	// is the original node
    	if(inplace)
    	{
    		return target;
    	}
    	
    	if(createNew)
    	{
	    	//create a file in the right location
	        FileInfo fileInfo = ffs.create(destinationParent, filename, ContentModel.TYPE_CONTENT);
	        destinationNode = fileInfo.getNodeRef();
    	}
    	else
    	{
    		try 
    		{
	    		FileInfo fileInfo = ffs.copy(target, destinationParent, filename);
	    		destinationNode = fileInfo.getNodeRef();
    		}
    		catch(FileNotFoundException fnf)
    		{
    			throw new AlfrescoRuntimeException(fnf.getMessage(), fnf);
    		}
    	}

        return destinationNode;
    }
    
    public int getInteger(Serializable val)
    {
    	if(val == null)
    	{ 
    		return 0;
    	}
    	try
    	{
    		return Integer.parseInt(val.toString());
    	}
    	catch(NumberFormatException nfe)
    	{
    		return 0;
    	}
    }
    
    public File getTempFile(NodeRef nodeRef)
    {
    	File alfTempDir = TempFileProvider.getTempDir();
        File toolkitTempDir = new File(alfTempDir.getPath() + File.separatorChar + nodeRef.getId());
        toolkitTempDir.mkdir();
        File file = new File(toolkitTempDir, ffs.getFileInfo(nodeRef).getName());
        
        return file;
    }
    
    public File nodeRefToTempFile(NodeRef nodeRef)
    {
        File tempFromFile = TempFileProvider.createTempFile("PDFAConverter-", nodeRef.getId()
                + FILE_EXTENSION);
        ContentReader reader = cs.getReader(nodeRef, ContentModel.PROP_CONTENT);
        reader.getContent(tempFromFile);
        return tempFromFile;
    }
    
    /**
     * @param serviceRegistry
     */
    public void setServiceRegistry(ServiceRegistry serviceRegistry)
    {
        this.serviceRegistry = serviceRegistry;
        ns = serviceRegistry.getNodeService();
        cs = serviceRegistry.getContentService();
        ffs = serviceRegistry.getFileFolderService();
        ds = serviceRegistry.getDictionaryService();
    }
}
