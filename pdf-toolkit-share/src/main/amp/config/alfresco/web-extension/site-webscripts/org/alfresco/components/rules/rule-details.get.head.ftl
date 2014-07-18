<#include "../component.head.inc">
<!-- Rule Configs -->
<!-- Note! Needs to be imported here since they are brought in via XMLHttpRequest after page has rendered -->
<@script type="text/javascript" src="${page.url.context}/res/components/rules/config/rule-config-util.js"></@script>
<@link rel="stylesheet" type="text/css" href="${page.url.context}/res/components/rules/config/rule-config.css" />
<@script type="text/javascript" src="${page.url.context}/res/components/rules/config/rule-config.js"></@script>
<@link rel="stylesheet" type="text/css" href="${page.url.context}/res/components/rules/config/rule-config-type.css" />
<@script type="text/javascript" src="${page.url.context}/res/components/rules/config/rule-config-type.js"></@script>
<@link rel="stylesheet" type="text/css" href="${page.url.context}/res/components/rules/config/rule-config-condition.css" />
<@script type="text/javascript" src="${page.url.context}/res/components/rules/config/rule-config-condition.js"></@script>
<@script type="text/javascript" src="${page.url.context}/res/components/rules/config/rule-config-action.js"></@script>
<!-- Rule Details -->
<@link rel="stylesheet" type="text/css" href="${page.url.context}/res/components/rules/rule-details.css" />
<@script type="text/javascript" src="${page.url.context}/res/components/rules/rule-details.js"></@script>

<!-- Alfresco PDF Toolkit additions, requires document picker -->
<@script type="text/javascript" src="${page.url.context}/res/modules/document-picker/document-picker.js"></@script>
<@script type="text/javascript" src="${page.url.context}/res/components/object-finder/object-finder.js"></@script>
<@link rel="stylesheet" type="text/css" href="${page.url.context}/res/modules/document-picker/document-picker.css" />
<@link rel="stylesheet" type="text/css" href="${page.url.context}/res/components/object-finder/object-finder.css" />
<@script type="text/javascript" src="${page.url.context}/res/pdftoolkit/components/rules/config/pdftoolkit-rule-config-actions.js"></@script>

