/**
 * PDF digital signature position component.
 *
 * @namespace PDFToolkit
 * @class PDFToolkit.SelectPage
 */
if(typeof PDFToolkit == "undefined" || !PDFToolkit)
{
	var PDFToolkit = {};
}

(function()
{
	/**
	 * YUI Library aliases
	 */
	var Dom = YAHOO.util.Dom,
	Event = YAHOO.util.Event,
	Selector = YAHOO.util.Selector;
	
	/**
	 * SelectPage constructor.
	 *
	 * @param {String} htmlId The HTML id of the parent element
	 * @return {PDFToolkit.SelectPage} The new component instance
	 * @constructor
	 */
	PDFToolkit.SelectPage = function SelectPage_constructor(htmlId)
	{
		PDFToolkit.SelectPage.superclass.constructor.call(this, "PDFToolkit.SelectPage", htmlId, []);
		return this;
	};

	YAHOO.extend(PDFToolkit.SelectPage, Alfresco.component.Base,
	{
		/**
		 * Object container for initialization options
		 *
		 * @property options
		 * @type {object} object literal
		 */
		options:
		{
			/**
			 * Reference to the signed document
			 *
			 * @property nodeRef
			 * @type string
			 */
			nodeRef: null
		},

		schemesModule: null,
		pagesModule: null,
		pageCount: -1,
		
		onReady:  function SelectPage_onReady()
		{
			this.getPageCount(this.options.nodeRef);
			this.getPageSchemes(this.options.nodeRef);
			
			this.schemesModule = new YAHOO.widget.Module(this.id + "-schemes");
			this.schemesModule = new YAHOO.widget.Module(this.id + "-pages");
			
			YAHOO.util.Event.addListener([this.id + "-useScheme"], "click", this.toggleSchemes, this);
		},
		
		getPageCount: function SelectPage_getPageCount(nodeRef)
		{
			Alfresco.util.Ajax.jsonGet(
				{
					url: (Alfresco.constants.PROXY_URI + "pdftoolkit/pagecount?nodeRef=" + nodeRef),
					successCallback:
					{
						fn: function(response)
						{
							var pageSelect = YAHOO.util.Dom.get(this.id + "-pages");
							var pages = parseInt(response.json.pageCount);
							if(pages > 0)
							{
								for(var i = 1;i < pages + 1; i++)
								{
									var opt = document.createElement("option");
									opt.text = i;
									opt.value = i;
									pageSelect.add(opt, null);
								}
							}
						},
						scope: this
					},
					failureCallback:
					{
						fn: function(response)
						{
							Alfresco.util.PopupManager.displayMessage(
								{
									text: "Could not retreive page count"
								}
							);
						}
					}
				});
		},

		getPageSchemes: function SelectPage_getPageSchemes(nodeRef)
		{
			Alfresco.util.Ajax.jsonGet(
				{
					url: (Alfresco.constants.PROXY_URI + "pdftoolkit/pageschemes?nodeRef=" + nodeRef),
					successCallback:
					{
						fn: function(response)
						{
							var schemeSelect = YAHOO.util.Dom.get(this.id + "-schemes");
						},
						scope: this
					},
					failureCallback:
					{
						fn: function(response)
						{
							Alfresco.util.PopupManager.displayMessage(
								{
									text: "Could not retreive page schemes"
								}
							);
						}
					}
				});
		},
		
		toggleSchemes: function SelectPage_toggleSchemes(event)
		{
			if(event.target.checked)
			{
				schemesModule.show();
				pagesModule.hide();
			}
			else
			{
				schemesModule.hide();
				pagesModule.show();
			}
		}
	});
})();