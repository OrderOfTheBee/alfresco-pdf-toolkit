/**
 * PDF Toolkit page selection component.
 *
 * @namespace PDFToolkit
 * @class PDFToolkit.SelectPage
 */
if(typeof PDFToolkit == "undefined" || !PDFToolkit)
{
	var PDFToolkit = {};
}

PDFToolkit.Util = {};

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
			 * Reference to the pdf document
			 *
			 * @property nodeRef
			 * @type string
			 */
			nodeRef: null,
			
			/**
			 * Do we show the page scheme options?
			 * 
			 * @property showPageSchemes
			 * @type boolean
			 */
			showPageScheme: false,
		},

		schemesModule: null,
		pagesModule: null,
		pageCount: -1,
		
		onReady:  function SelectPage_onReady()
		{
			this.getPageCount(this.options.nodeRef);
			this.pagesModule = new YAHOO.widget.Module(this.id + "-pageModule");
			
			if(this.options.showPageScheme === "true")
			{
				this.schemesModule = new YAHOO.widget.Module(this.id + "-schemeModule");
				this.getPageSchemes(this.options.nodeRef);
				// default state is page select hidden if page schemes enabled, 
				this.pagesModule.hide();
			}
			
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
							var schemes = response.json.schemes;
							for(index in schemes)
							{
								var opt = document.createElement("option");
								opt.text = schemes[index].name;
								opt.value = schemes[index].value;
								schemeSelect.add(opt, null);
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
									text: "Could not retreive page schemes"
								}
							);
						}
					}
				});
		},
		
		toggleSchemes: function SelectPage_toggleSchemes(event, that)
		{
			if(event.target.checked)
			{
				that.schemesModule.show();
				that.pagesModule.hide();
			}
			else
			{
				that.schemesModule.hide();
				that.pagesModule.show();
			}
		}
	});
})();

(function()
{
	/**
	 * YUI Library aliases
	 */
	var Dom = YAHOO.util.Dom,
	Event = YAHOO.util.Event,
	Selector = YAHOO.util.Selector;
	
	/**
	 * DependentSelect constructor.
	 *
	 * @param {String} htmlId The HTML id of the parent element
	 * @return {PDFToolkit.DependentSelect} The new component instance
	 * @constructor
	 */
	PDFToolkit.DependentSelect = function DependentSelect_constructor(htmlId)
	{
		PDFToolkit.DependentSelect.superclass.constructor.call(this, "PDFToolkit.DependentSelect", htmlId, []);
		return this;
	};

	YAHOO.extend(PDFToolkit.DependentSelect, Alfresco.component.Base,
	{
		options:
		{
			/**
			 * The show / hide configuration(s) for the form controls
			 *
			 * @property nodeRef
			 * @type string
			 */
			showSelectValues: []
		},
		
		onReady:  function DependentSelect_onReady()
		{	
			YAHOO.util.Event.addListener([this.id], "change", this.toggleDependentFields, this);
		},
		
		toggleDependentFields: function DependentSelect_toggleDependentFields(event, that)
		{
			var config = that.options.showSelectValues;
			// anything assigned to another show value will be hidden
			for(index in config)
			{
				
				var name = config[index].name;
				var fields = config[index].fields;
				
				// if the event source is the right option, show the fields
				// if it is not, hide these fields
				if(name === event.srcElement.value)
				{
					for(fieldIndex in fields)
					{
						var field = YAHOO.util.Dom.get(that.options.htmlId + "_" + fields[fieldIndex]);
						var container = field.parentElement;
						container.style.display = 'block';
					}
				}
				else
				{
					for(fieldIndex in fields)
					{
						var field = YAHOO.util.Dom.get(that.options.htmlId + "_" + fields[fieldIndex]);
						var container = field.parentElement;
						container.style.display = 'none';
					}
				}
			}
		}
	});
})();

(function()
{
	PDFToolkit.Util.HideDependentControls = function(element, htmlIdPrefix)
	{
		// get the field html id
		var fieldHtmlId = element.id;
		// set the value of the hidden field
		var value = YAHOO.util.Dom.get(fieldHtmlId).checked;
		YAHOO.util.Dom.get(fieldHtmlId + "-hidden").value = value;
		// find and hide the dependent controls
		var controls = YAHOO.util.Dom.get(fieldHtmlId + "-tohide").value.split(",");

		for(index in controls)
		{
			var control = new YAHOO.util.Dom.get((htmlIdPrefix + "_" + controls[index] + "-cntrl"));
			var container = control.parentElement;
			if(value)
			{
				container.style.display = 'none';
			}
			else
			{
				container.style.display = 'block';
			}
		}
	}
})();