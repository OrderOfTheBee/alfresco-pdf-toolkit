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

package org.alfresco.extension.pdftoolkit.service;

import java.io.Serializable;
import java.util.Map;

import org.alfresco.service.cmr.repository.NodeRef;

public interface PDFToolkitService
{
	// the actual action code
	/**
	 * Appends one PDF document to another 
	 * @param targetNodeRef
	 * @param params
	 * @return a NodeRef containing the appended PDF
	 */
	public NodeRef appendPDF(NodeRef targetNodeRef, Map<String, Serializable> params);
	
	/**
	 * Encrypts a PDF document, returns 
	 * @param targetNodeRef
	 * @param params
	 * @return a NodeRef pointing to the encrypted PDF
	 */
    public NodeRef encryptPDF(NodeRef targetNodeRef, Map<String, Serializable> params);
    
    /**
     * Decrypts a PDF document, given the owner password
     * @param targetNodeRef
     * @param params
     * @return a NodeRef pointing to the decrypted PDF
     */
    public NodeRef decryptPDF(NodeRef targetNodeRef, Map<String, Serializable> params);
    
    /**
     * Applies a digital signature to a PDF document
     * @param targetNodeRef
     * @param params
     * @return a NodeRef pointing to the signed PDF
     */
    public NodeRef signPDF(NodeRef targetNodeRef, Map<String, Serializable> params);
    
    /**
     * Applies a text or image watermark to a PDF document
     * @param targetNodeRef
     * @param params
     * @return a NodeRef pointing to the watermarked PDF
     */
    public NodeRef watermarkPDF(NodeRef targetNodeRef, Map<String, Serializable> params);
    
    /**
     * Splits a PDF document into single pages
     * @param targetNodeRef
     * @param params
     * @return a NodeRef pointing to the folder containing the split PDF
     */
    public NodeRef splitPDF(NodeRef targetNodeRef, Map<String, Serializable> params);
    
    /**
     * Splits a PDF document at a specific page
     * @param targetNodeRef
     * @param params
     * @return a NodeRef pointing to the folder containing the split PDF
     */
    public NodeRef splitPDFAtPage(NodeRef targetNodeRef, Map<String, Serializable> params);
    
    /**
     * Inserts one PDF document into another at a specific location
     * @param targetNodeRef
     * @param params
     * @return a NodeRef pointing to the combined PDF
     */
    public NodeRef insertPDF(NodeRef targetNodeRef, Map<String, Serializable> params);
    
    /**
     * Deletes selected pages from a PDF document
     * @param targetNodeRef
     * @param params
     * @return a NodeRef pointing to the resulting PDF
     */
    public NodeRef deletePagesFromPDF(NodeRef targetNodeRef, Map<String, Serializable> params);
    
    /**
     * Extracts selected pages from a PDF document
     * @param targetNodeRef
     * @param params
     * @return a NodeRef pointing to the PDF containing the extracted pages
     */
    public NodeRef extractPagesFromPDF(NodeRef targetNodeRef, Map<String, Serializable> params);
    
    /**
     * Rotates a PDF document
     * @param targetNodeRef
     * @param params
     * @return a NodeRef pointing to the rotated PDF
     */
    public NodeRef rotatePDF(NodeRef targetNodeRef, Map<String, Serializable> params);
}
