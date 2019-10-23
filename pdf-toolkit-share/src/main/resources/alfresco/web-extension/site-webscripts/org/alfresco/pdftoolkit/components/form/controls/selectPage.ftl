<!-- 
This control allows for selecting a page number or a page pattern from a 
scheme provided by the server
 -->
<script type="text/javascript" src="${url.context}/res/pdftoolkit/components/selectpage/selectpage.js"></script>
<#include "/org/alfresco/components/form/controls/common/utils.inc.ftl" />
<#assign showPageScheme=field.control.params.showPageScheme>
<#assign allowMultiPageSelect=field.control.params.allowMultiPageSelect>
<div class="form-field">
		<label for="${fieldHtmlId}">${field.label?html}:<#if field.mandatory><span class="mandatory-indicator">${msg("form.required.fields.marker")}</span></#if></label>
		<#if showPageScheme == "true">
		${msg("pdftoolkit.form.pagescheme")}
		<input class="formsCheckBox" id="${fieldHtmlId}-useScheme" type="checkbox" tabindex="0" name="-" 
             value="true" checked="checked"/>
        <div id="${fieldHtmlId}-schemeModule">
			<select id="${fieldHtmlId}-schemes" name="${field.name}-schemes">
			</select>
		</div>
		</#if>
		<div id="${fieldHtmlId}-pageModule">
			<select id="${fieldHtmlId}-pages" name="${field.name}-pages" <#if allowMultiPageSelect == "true">multiple</#if>>
			</select>
		</div>
		
		<input type="hidden" name="${field.name}" id="${fieldHtmlId}"/>
</div>
<script type="text/javascript">//<![CDATA[
var SelectPage = new PDFToolkit.SelectPage("${fieldHtmlId}").setOptions(
	      {
	         nodeRef: "${form.destination?js_string}",
	         showPageScheme: "${showPageScheme}",
	         allowMultiPageSelect: "${allowMultiPageSelect}"
	      }).setMessages(
	         {}
	      );
//]]></script>