package org.alfresco.extension.pdftoolkit.model;

import org.alfresco.service.namespace.QName;

public interface PDFToolkitModel 
{
	//namespace
	static final String PDFTOOLKIT_MODEL_1_0_URI = "http://www.alfresco.com/model/pdftoolkit/1.0";
	
	//signed aspect and properties
	static final QName ASPECT_SIGNED = QName.createQName(PDFTOOLKIT_MODEL_1_0_URI, "signed");
	static final QName PROP_SIGNATUREDATE = QName.createQName(PDFTOOLKIT_MODEL_1_0_URI, "signaturedate");
	static final QName PROP_REASON = QName.createQName(PDFTOOLKIT_MODEL_1_0_URI, "reason");
	static final QName PROP_LOCATION = QName.createQName(PDFTOOLKIT_MODEL_1_0_URI, "location");
	static final QName PROP_SIGNEDBY = QName.createQName(PDFTOOLKIT_MODEL_1_0_URI, "signedby");
	
	//encrypted aspect and properties
	static final QName ASPECT_ENCRYPTED = QName.createQName(PDFTOOLKIT_MODEL_1_0_URI, "encrypted");
	static final QName PROP_ENCRYPTIONDATE = QName.createQName(PDFTOOLKIT_MODEL_1_0_URI, "encryptiondate");
	static final QName PROP_ENCRYPTEDBY = QName.createQName(PDFTOOLKIT_MODEL_1_0_URI, "encryptedby");

}
