<#--
This control will hide the dependent controls injected via the
control parameters in the form config by hiding the div {field.name}-control
-->
<#assign hideProperties=field.control.params.hideProperties>

<div class="form-field" id="${field.name}-control">
	<#if form.mode == "view">
		<!#-- this control should not be used in view mode -->
	<#else>
	  <input id="${fieldHtmlId}" type="hidden" name="${field.name}" value="false" />
      <input id="${fieldHtmlId}-tohide" type="hidden" name="-" value="${hideProperties}" />
      <input class="formsCheckBox" id="${fieldHtmlId}-entry" type="checkbox" tabindex="0" name="-" 
             value="true"
             onchange="PDFToolkit.Util.HideDependentControls('${fieldHtmlId}', '${args.htmlid}')" />
      <label for="${fieldHtmlId}-entry" class="checkbox">${field.label?html}</label>
	</#if>
</div>
