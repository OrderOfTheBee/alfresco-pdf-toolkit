alfresco-pdf-toolkit
====================

The new home of the Alfresco PDF Toolkit (replacing the deprecated Google Code project)

Alfresco PDF Toolkit adds additional functionality to Alfresco that allows you to work with PDF files.

The toolkit compatibility matrix and some additional information is available on addons.alfresco.com. If you find this stuff useful, please pop over to the addons site and "like" the project!

Current Functionality:

* Append - Append PDF to another PDF, generating a new PDF
* Split - Splits a PDF at a specified interval, (ex. 2 pages, 3 pages, etc.), generating new PDFs for each section.
* Split At - Split PDF into two PDFs at a specific page, generating two new PDFs.
* Insert PDF - Insert PDF at a specific Page
* Delete page(s) from a PDF document
* Extract page(s) from a PDF document
* PDF Watermarking
* PDF Encryption / decryption
* Digital Signatures
* Rotate PDF Documents
* TIFF to PDF transformation (currently rather rudimentary, but functional)
* Extended metadata to capture information about encryption or signature events
* Search for encrypted or signed documents by encryption or signature metadata


All of the actions are available as actions that can be used in content rules, and as document library actions in the document browse and detail views.

Note that as of Version 1.2, the legacy Explorer UI components will no longer be maintained and will be fully removed in version 1.3.

NOTE:  The latest 1.3 betas remove the PDF/A conversion action, since that action has dependencies on Alfresco Enterprise.  I'm committed to keeping this project 100% compatible with Alfresco community.  The PDF/A action will be released as a separate module for Alfresco Enterprise.

A teaser screencast is available here:

http://www.youtube.com/watch?v=cJYpFgx037E

Functionality in development:

* Minimally requires Alfresco 3.4 for the repository tier actions, 4.1 for the Share UI components.
