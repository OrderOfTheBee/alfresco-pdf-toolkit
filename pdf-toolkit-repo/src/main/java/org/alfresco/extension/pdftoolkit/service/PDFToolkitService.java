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
	public void appendPDF(NodeRef targetNodeRef, Map<String, Serializable> params);
    public void encryptPDF(NodeRef targetNodeRef, Map<String, Serializable> params);
    public void decryptPDF(NodeRef targetNodeRef, Map<String, Serializable> params);
    public void signPDF(NodeRef targetNodeRef, Map<String, Serializable> params);
    public void watermarkPDF(NodeRef targetNodeRef, Map<String, Serializable> params);
    public void splitPDF(NodeRef targetNodeRef, Map<String, Serializable> params);
    public void splitPDFAtPage(NodeRef targetNodeRef, Map<String, Serializable> params);
    public void insertPDF(NodeRef targetNodeRef, Map<String, Serializable> params);
    public void deletePagesFromPDF(NodeRef targetNodeRef, Map<String, Serializable> params);
    public void rotatePDF(NodeRef targetNodeRef, Map<String, Serializable> params);
}
