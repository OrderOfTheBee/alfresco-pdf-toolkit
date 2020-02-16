
#alfresco-pdf-toolkit
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

LICENSING

The Alfresco PDF Toolkit is an unsupported, community developed open source project.  The project code itself is released under the 
AGPL License.

iText - Affero GPL 3 (http://itextpdf.com/terms-of-use/agpl.php)




# Alfresco AIO Project - SDK 4.0

This is an All-In-One (AIO) project for Alfresco SDK 4.0.

Run with `./run.sh build_start` or `./run.bat build_start` and verify that it

 * Runs Alfresco Content Service (ACS)
 * Runs Alfresco Share
 * Runs Alfresco Search Service (ASS)
 * Runs PostgreSQL database
 * Deploys the JAR assembled modules
 
All the services of the project are now run as docker containers. The run script offers the next tasks:

 * `build_start`. Build the whole project, recreate the ACS and Share docker images, start the dockerised environment composed by ACS, Share, ASS and 
 PostgreSQL and tail the logs of all the containers.
 * `build_start_it_supported`. Build the whole project including dependencies required for IT execution, recreate the ACS and Share docker images, start the 
 dockerised environment composed by ACS, Share, ASS and PostgreSQL and tail the logs of all the containers.
 * `start`. Start the dockerised environment without building the project and tail the logs of all the containers.
 * `stop`. Stop the dockerised environment.
 * `purge`. Stop the dockerised container and delete all the persistent data (docker volumes).
 * `tail`. Tail the logs of all the containers.
 * `reload_share`. Build the Share module, recreate the Share docker image and restart the Share container.
 * `reload_acs`. Build the ACS module, recreate the ACS docker image and restart the ACS container.
 * `build_test`. Build the whole project, recreate the ACS and Share docker images, start the dockerised environment, execute the integration tests from the
 `integration-tests` module and stop the environment.
 * `test`. Execute the integration tests (the environment must be already started).

# Few things to notice

 * No parent pom
 * No WAR projects, the jars are included in the custom docker images
 * No runner project - the Alfresco environment is now managed through [Docker](https://www.docker.com/)
 * Standard JAR packaging and layout
 * Works seamlessly with Eclipse and IntelliJ IDEA
 * JRebel for hot reloading, JRebel maven plugin for generating rebel.xml [JRebel integration documentation]
 * AMP as an assembly
 * Persistent test data through restart thanks to the use of Docker volumes for ACS, ASS and database data
 * Integration tests module to execute tests against the final environment (dockerised)
 * Resources loaded from META-INF
 * Web Fragment (this includes a sample servlet configured via web fragment)

# TODO

  * Abstract assembly into a dependency so we don't have to ship the assembly in the archetype
  * Functional/remote unit tests
