if (typeof PDFToolKIT == "undefined" || !PDFToolKIT)
{
   var PDFToolKIT = {};
}

PDFToolKIT.RuleConfigActionCustom = function(htmlId)
{
   PDFToolKIT.RuleConfigActionCustom.superclass.constructor.call(this, htmlId);

   // Re-register with our own name
   this.name = "PDFToolKIT.RuleConfigActionCustom";
   Alfresco.util.ComponentManager.reregister(this);

   // Instance variables
   this.customisations = YAHOO.lang.merge(this.customisations, PDFToolKIT.RuleConfigActionCustom.superclass.customisations);
   this.renderers = YAHOO.lang.merge(this.renderers, PDFToolKIT.RuleConfigActionCustom.superclass.renderers);

   return this;
};

hideParameterCustom = function (parameterDefinitions)
      {
      
               for (var i = 0, il = parameterDefinitions.length; i < il; i++)
               {
            	     if(parameterDefinitions[i].name=="destination-folder")
            	     {
              		  parameterDefinitions[i]._type = "hidden";
                     }
               }
      };

YAHOO.extend(PDFToolKIT.RuleConfigActionCustom, Alfresco.RuleConfigAction,
{

   /**
    * CUSTOMISATIONS
    */

   customisations:
   {         
      PDFToolKITDestination:
      {
       text: function(configDef, ruleConfig, configEl)
         {
              // Display as path
              this._getParamDef(configDef, "destination-folder")._type = "path";
              return configDef;
         },
         edit: function(configDef, ruleConfig, configEl)
         {
             // Hide all parameters since we are using a custom ui but set default values
            // this._hideParameters(configDef.parameterDefinitions);
             
             
             if (configDef.parameterDefinitions)
         	{             	
             	hideParameterCustom(configDef.parameterDefinitions);
        	 }

             // Make parameter renderer create a "Destination" button that displays an destination folder browser
             configDef.parameterDefinitions.splice(0,0,{
                type: "arca:destination-dialog-button",
                displayLabel: this.msg("label.destination.folder"),
                _buttonLabel: this.msg("button.select-folder"),
                _destinationParam: "destination-folder"
             });
             
             return configDef;
         }
      
      },
   },

});