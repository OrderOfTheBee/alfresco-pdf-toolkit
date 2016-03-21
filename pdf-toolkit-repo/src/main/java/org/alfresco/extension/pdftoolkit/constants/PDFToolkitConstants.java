package org.alfresco.extension.pdftoolkit.constants;

import com.itextpdf.text.pdf.BaseFont;

public abstract class PDFToolkitConstants 
{
    public static final String PARAM_INPLACE    	 				= "inplace";
    public static final String PARAM_DESTINATION_FOLDER 			= "destination-folder";
    public static final String PARAM_DESTINATION_NAME  				= "destination-name";
    public static final String PARAM_CREATE_NEW						= "create-new";
    public static final String PARAM_POSITION       				= "position";
    public static final String PARAM_LOCATION_X     				= "location-x";
    public static final String PARAM_LOCATION_Y     				= "location-y";
    public static final String PARAM_PAGE			 				= "page";
    
    public static final String PARAM_TARGET_NODE    				= "target-node";
    
    public static final String PARAM_USER_PASSWORD                 	= "user-password";
    public static final String PARAM_OWNER_PASSWORD                	= "owner-password";
    public static final String PARAM_ALLOW_PRINT                   	= "allow-print";
    public static final String PARAM_ALLOW_COPY                    	= "allow-copy";
    public static final String PARAM_ALLOW_CONTENT_MODIFICATION    	= "allow-content-modification";
    public static final String PARAM_ALLOW_ANNOTATION_MODIFICATION 	= "allow-annotation-modification";
    public static final String PARAM_ALLOW_FORM_FILL               	= "allow-form-fill";
    public static final String PARAM_ALLOW_SCREEN_READER           	= "allow-screen-reader";
    public static final String PARAM_ALLOW_DEGRADED_PRINT          	= "allow-degraded-print";
    public static final String PARAM_ALLOW_ASSEMBLY                	= "allow-assembly";
    public static final String PARAM_ENCRYPTION_LEVEL              	= "encryption-level";
    public static final String PARAM_EXCLUDE_METADATA              	= "exclude-metadata";
    public static final String PARAM_OPTIONS_LEVEL                 	= "level-options";
    
    public static final String PARAM_INSERT_CONTENT     			= "insert-content";
    
    public static final String PARAM_DEGREES     					= "degrees";
    
    public static final String PARAM_PRIVATE_KEY        			= "private-key";
    public static final String PARAM_VISIBILITY         			= "visibility";
    public static final String PARAM_LOCATION           			= "location";
    public static final String PARAM_REASON             			= "reason";
    public static final String PARAM_KEY_PASSWORD       			= "key-password";
    public static final String PARAM_WIDTH              			= "width";
    public static final String PARAM_HEIGHT             			= "height";
    public static final String PARAM_KEY_TYPE          				= "key-type";
    public static final String PARAM_ALIAS              			= "alias";
    public static final String PARAM_STORE_PASSWORD     			= "store-password";
    public static final String PARAM_NEW_REVISION    				= "new-revision";

    public static final String PARAM_SPLIT_FREQUENCY    			= "split-frequency";
    
    public static final String PARAM_WATERMARK_IMAGE    			= "watermark-image";
    public static final String PARAM_WATERMARK_DEPTH    			= "watermark-depth";
    public static final String PARAM_WATERMARK_TYPE     			= "watermark-type";
    public static final String PARAM_WATERMARK_TEXT     			= "watermark-text";
    public static final String PARAM_WATERMARK_FONT     			= "watermark-font";
    public static final String PARAM_WATERMARK_SIZE     			= "watermark-size";

    public static final String VISIBILITY_HIDDEN        			= "hidden";
    public static final String VISIBILITY_VISIBLE       			= "visible";

    public static final String KEY_TYPE_PKCS12          			= "pkcs12";
    public static final String KEY_TYPE_DEFAULT         			= "default";
    
    public static final String DEPTH_UNDER              			= "under";
    public static final String DEPTH_OVER               			= "over";

    public static final String TYPE_IMAGE               			= "image";
    public static final String TYPE_TEXT                			= "text";

    public static final String FONT_OPTION_HELVETICA    			= BaseFont.HELVETICA;
    public static final String FONT_OPTION_COURIER      			= BaseFont.COURIER;
    public static final String FONT_OPTION_TIMES_ROMAN  			= BaseFont.TIMES_ROMAN;

    public static final float PAD                      				= 15;
    
    public static final String PAGE_ALL             				= "all";
    public static final String PAGE_ODD             				= "odd";
    public static final String PAGE_EVEN            				= "even";
    public static final String PAGE_FIRST           				= "first";
    public static final String PAGE_LAST            				= "last";

    public static final String POSITION_CENTER      				= "center";
    public static final String POSITION_TOPLEFT     				= "topleft";
    public static final String POSITION_TOPRIGHT    				= "topright";
    public static final String POSITION_BOTTOMLEFT  				= "bottomleft";
    public static final String POSITION_BOTTOMRIGHT 				= "bottomright";
    public static final String POSITION_MANUAL 	 					= "manual";
    
    public static final String FILE_EXTENSION 						= ".pdf";
    public static final String FILE_MIMETYPE  						= "application/pdf";
    public static final String PDF 									= "pdf";
}
