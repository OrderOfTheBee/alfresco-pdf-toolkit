package org.alfresco.extension.pdftoolkit.transformer;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.repo.content.transform.AbstractContentTransformer2;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.TransformationOptions;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.RandomAccessFileOrArray;
import com.itextpdf.text.pdf.codec.TiffImage;

public class ITextTransformerTIFFToPDF extends AbstractContentTransformer2 {

	public static final String MIMETYPE_IMAGE_TIFF = "image/tiff";
	private static final Log logger = LogFactory.getLog(ITextTransformerTIFFToPDF.class);
	
	@Override
	public boolean isTransformable(String sourceMimetype, String targetMimetype,
			TransformationOptions options) {
		
		//This transformer only supports image/tiff -> PDF transformations
        if (!MIMETYPE_IMAGE_TIFF.equals(sourceMimetype) || !MimetypeMap.MIMETYPE_PDF.equals(targetMimetype))
            {
                return false;
            }
            else
            {
                return true;
            }
	}

	@Override
	/**
	 * This is a VERY basic transformer. Still a lot to do, such as:
	 * 
	 * proper scaling
	 * landscape vs portrait
	 * multipage tiff
	 */
    protected void transformInternal(ContentReader contentReader, ContentWriter contentWriter, TransformationOptions options)
        throws Exception
    {
		
		//get an output stream for the PDF writer
		OutputStream out = contentWriter.getContentOutputStream();
		
		//create a byte array to hold the image data
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		//set up a pdf document and writer
		Document doc = new Document(PageSize.A4);
		PdfWriter pdfWriter = PdfWriter.getInstance(doc, out);
		pdfWriter.setStrictImageSequence(true);

		
		//iText Image needs the byte array for the tiff, could be big
		contentReader.getContent(baos);
		
		//open the doc and add the image
		doc.open();
		Image tiff = null;
		RandomAccessFileOrArray randomAccessFile = new RandomAccessFileOrArray(baos.toByteArray());
		int pages = TiffImage.getNumberOfPages(randomAccessFile);
		for (int i = 1; i <= pages; i++) {
			tiff = TiffImage.getTiffImage(randomAccessFile, i);
			tiff.scaleToFit(PageSize.A4.getWidth(), PageSize.A4.getHeight());
			tiff.setAbsolutePosition(0, 0);
            doc.add(tiff);
            doc.newPage();
        }
		doc.close();
		out.flush();
	}
}
