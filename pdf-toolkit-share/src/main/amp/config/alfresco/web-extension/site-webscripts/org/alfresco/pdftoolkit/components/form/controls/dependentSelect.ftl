<#include "/org/alfresco/components/form/controls/common/utils.inc.ftl" />

<#if field.control.params.showSelectValues??>
	<#assign selectValues = field.control.params.showSelectValues?split(";")>
</#if>

<#if field.control.params.optionSeparator??>
   <#assign optionSeparator=field.control.params.optionSeparator>
<#else>
   <#assign optionSeparator=",">
</#if>
<#if field.control.params.labelSeparator??>
   <#assign labelSeparator=field.control.params.labelSeparator>
<#else>
   <#assign labelSeparator="|">
</#if>

<#assign fieldValue=field.value>

<#if fieldValue?string == "" && field.control.params.defaultValueContextProperty??>
   <#if context.properties[field.control.params.defaultValueContextProperty]??>
      <#assign fieldValue = context.properties[field.control.params.defaultValueContextProperty]>
   <#elseif args[field.control.params.defaultValueContextProperty]??>
      <#assign fieldValue = args[field.control.params.defaultValueContextProperty]>
   </#if>
</#if>

<div class="form-field">
   <#if form.mode == "view">
      <#-- This is strictly for actions, there should never be a view -->
   <#else>
      <label for="${fieldHtmlId}">${field.label?html}:<#if field.mandatory><span class="mandatory-indicator">${msg("form.required.fields.marker")}</span></#if></label>
      <#if field.control.params.options?? && field.control.params.options != "">
         <select id="${fieldHtmlId}" name="${field.name}" tabindex="0"
               <#if field.description??>title="${field.description}"</#if>
               <#if field.control.params.size??>size="${field.control.params.size}"</#if> 
               <#if field.control.params.styleClass??>class="${field.control.params.styleClass}"</#if>
               <#if field.control.params.style??>style="${field.control.params.style}"</#if>
               <#if field.disabled  && !(field.control.params.forceEditable?? && field.control.params.forceEditable == "true")>disabled="true"</#if>>
               <#list field.control.params.options?split(optionSeparator) as nameValue>
                  <#if nameValue?index_of(labelSeparator) == -1>
                     <option value="${nameValue?html}"<#if nameValue == fieldValue?string || (fieldValue?is_number && fieldValue?c == nameValue)> selected="selected"</#if>>${nameValue?html}</option>
                  <#else>
                     <#assign choice=nameValue?split(labelSeparator)>
                     <option value="${choice[0]?html}"<#if choice[0] == fieldValue?string || (fieldValue?is_number && fieldValue?c == choice[0])> selected="selected"</#if>>${msgValue(choice[1])?html}</option>
                  </#if>
               </#list>
         </select>
         <@formLib.renderFieldHelp field=field />
      <#else>
         <div id="${fieldHtmlId}" class="missing-options">${msg("form.control.selectone.missing-options")}</div>
      </#if>
   </#if>
</div>
<script type="text/javascript">//<![CDATA[

// first, set up the list of select values that trigger a change

var showSelectValues = [
	<#list selectValues as value>
	
	<#assign showWhenSelectedName = value?split(":")[0]>
	<#assign showWhenSelectedValues = value?split(":")[1]?split(",")>
	{'name':'${showWhenSelectedName}',
	'fields':[
	<#list showWhenSelectedValues as show>
		'${show}'<#if show_has_next>,</#if>
	</#list>
	          ]}<#if value_has_next>,</#if>
	</#list>	
]
// next, set up the object that contains the fields to show / hide based on 
// the value of the selects
var DependentSelect = new PDFToolkit.DependentSelect("${fieldHtmlId}").setOptions(
	      {
	         showSelectValues: showSelectValues,
	         htmlId: "${args.htmlid}"
	      }).setMessages(
	         {}
	      );
//]]></script>